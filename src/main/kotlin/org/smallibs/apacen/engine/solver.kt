package org.smallibs.apacen.engine

import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.CompoundTerm.Cut
import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Declaration.Rule
import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.data.Generator
import org.smallibs.apacen.data.Pretty
import org.smallibs.apacen.engine.declaration.Fresh.fresh
import org.smallibs.apacen.engine.declaration.Unification.unify
import org.smallibs.apacen.engine.declaration.normalize
import org.smallibs.apacen.engine.delta.Delta
import org.smallibs.apacen.engine.delta.Delta.deltaReduce
import org.smallibs.apacen.engine.relation.Reducer.reduce
import org.smallibs.core.IList
import org.smallibs.core.IList.Cons
import org.smallibs.core.IList.Empty
import org.smallibs.core.ILists.filter

data class Proof(val environment: Environment, val deferred: IList<Deferred>)
data class Deferred(
    val generation: Int,
    val trace: Boolean,
    val goals: IList<CompoundTerm>,
    val postponed: IList<IList<CompoundTerm>>,
    val rules: IList<Rule>,
    val environment: Environment
)

data class Solver(val system: IList<Rule>, val generator: Generator = Generator()) {

    private data class ProofStep(
        val goals: IList<CompoundTerm>,
        val postponed: IList<IList<CompoundTerm>>,
        val environment: Environment,
        val deferred: Deferred
    )

    data class Runtime(
        val goals: IList<CompoundTerm>,
        val postponed: IList<IList<CompoundTerm>>,
        val rules: IList<Rule>,
        val environment: Environment,
        val deferred: IList<Deferred>,
        val trace: Boolean
    ) : Pretty {
        override fun asString(): String = goals.asString(",", CompoundTerm::asString, false)
    }

    fun solve(goals: IList<CompoundTerm>): Proof? =
        solve(Runtime(goals, Empty, goals.getRules(), Environment(), Empty, false))

    fun backtrack(proof: Proof): Proof? = proof.deferred.backtrack()?.let { (current, deferred) ->
        solve(Runtime(current.goals, current.postponed, current.rules, current.environment, deferred, current.trace))
    }

    //
    // Private behaviors
    //

    private tailrec fun solve(runtime: Runtime): Proof? {
        return when (val goal = runtime.goals) {
            is Cons<CompoundTerm> -> {
                val nextRuntime = when (goal.head) {
                    is Cut -> {
                        Runtime(
                            goal.tail,
                            runtime.postponed,
                            goal.tail.getRules(),
                            runtime.environment,
                            cut(goal.head.generation, runtime.deferred),
                            runtime.trace
                        )
                    }

                    is Relation ->
                        reduce(runtime.environment.addEquation(goal.head))?.let { environment ->
                            Runtime(
                                goal.tail,
                                runtime.postponed,
                                goal.tail.getRules(),
                                environment,
                                runtime.deferred,
                                runtime.trace
                            )
                        }

                    is Functor -> {
                        val reduction = goal.head.deltaReduce(runtime)
                        if (reduction != null) {

                            if (runtime.trace) println(
                                "${" ".repeat(runtime.postponed.size)}<${runtime.postponed.size}/${generator.currentGeneration()}> delta reduction for ${
                                    goal.head.normalize(
                                        runtime.environment
                                    ).asString()
                                }"
                            )

                            val runtime = reduction.first
                            val goals = reduction.second
                            Runtime(
                                goals,
                                Cons(goal.tail, runtime.postponed),
                                goals.getRules(),
                                runtime.environment,
                                runtime.deferred,
                                runtime.trace
                            )
                        } else {
                            goal.head.solveHead(
                                runtime.trace,
                                goal,
                                runtime.postponed,
                                runtime.rules,
                                runtime.environment
                            )
                                ?.let { proofStep ->
                                    val goals = proofStep.goals
                                    val deferred = Cons(proofStep.deferred, runtime.deferred)

                                    Runtime(
                                        goals,
                                        Cons(goal.tail, proofStep.postponed),
                                        goals.getRules(),
                                        proofStep.environment,
                                        deferred,
                                        runtime.trace
                                    )
                                }
                        }
                    }
                }

                if (nextRuntime == null) {
                    val next = runtime.deferred.backtrack()

                    // Don't use elvis operator otherwise tailrec is not verified and applied during the compilation
                    if (next == null) {
                        null
                    } else {
                        val (current, deferred) = next

                        if (runtime.trace) println("${" ".repeat(runtime.postponed.size)}<${runtime.postponed.size}/${generator.currentGeneration()}> Backtracking to ${current.generation}")

                        solve(
                            Runtime(
                                current.goals,
                                current.postponed,
                                current.rules,
                                current.environment,
                                deferred,
                                current.trace
                            )
                        )
                    }
                } else {
                    solve(nextRuntime)
                }
            }

            Empty ->
                when (runtime.postponed) {
                    is Cons<IList<CompoundTerm>> ->
                        solve(
                            Runtime(
                                runtime.postponed.head,
                                runtime.postponed.tail,
                                runtime.postponed.head.getRules(),
                                runtime.environment,
                                runtime.deferred,
                                runtime.trace
                            )
                        )

                    Empty -> Proof(runtime.environment, runtime.deferred)
                }
        }
    }

    private fun IList<Deferred>.backtrack(): Pair<Deferred, IList<Deferred>>? =
        when (this) {
            is Cons<Deferred> -> head to tail
            Empty -> null
        }

//
// Rules
//

    private var memoization: Map<Pair<String, Int>, IList<Rule>> = mapOf()

    private fun IList<CompoundTerm>.getRules(): IList<Rule> =
        when (this) {
            is Cons<CompoundTerm> ->
                when (head) {
                    is Functor -> getRules(head.name, head.parameters.size)
                    is Cut -> Empty
                    is Relation -> Empty
                }

            Empty -> Empty
        }

    private fun getRules(name: String, arity: Int): IList<Rule> {
        val rules = memoization[name to arity]

        if (rules == null) {
            val rules = system.filter { it -> it.head.name == name && it.head.parameters.size == arity }

            if (rules.isEmpty() && !Delta.members.contains(name)) {
                System.err.println("[Warning] No definition for $name/$arity")
            }

            memoization = memoization + ((name to arity) to rules)
            return rules
        }

        return rules
    }

    private tailrec fun Functor.solveHead(
        trace: Boolean,
        goals: IList<CompoundTerm>,
        postponed: IList<IList<CompoundTerm>>,
        rules: IList<Rule>,
        environment: Environment,
    ): ProofStep? =
        when (rules) {
            is Cons<Rule> -> {
                if (trace) print(
                    "${" ".repeat(postponed.size)}<${postponed.size}/${generator.currentGeneration()}> Trying selection ${
                        this.normalize(environment).asString()
                    } with ${rules.head.head.asString()} "
                )
                when (val select = this.select(environment, rules.head)) {
                    null -> {
                        if (trace) println("❌")
                        this.solveHead(trace, goals, postponed, rules.tail, environment)
                    }

                    else -> {
                        when (val newEnvironment = reduce(select.second)) {
                            null -> {
                                if (trace) println("❌")
                                this.solveHead(trace, goals, postponed, rules.tail, environment)
                            }

                            else -> {
                                if (trace) println("✅")
                                ProofStep(
                                    select.first,
                                    postponed,
                                    newEnvironment,
                                    Deferred(
                                        generator.currentGeneration(),
                                        trace,
                                        goals,
                                        postponed,
                                        rules.tail,
                                        environment
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Empty -> null
        }

    private fun Functor.select(environment: Environment, rule: Rule): Pair<IList<CompoundTerm>, Environment>? =
        rule.fresh(generator.nextGeneration()).let { rule ->
            unify(environment, rule.head, this)?.let { rule.tail to it }
        }

    private tailrec fun cut(generation: Int, deferred: IList<Deferred>): IList<Deferred> =
        when (deferred) {
            is Cons<Deferred> ->
                if (deferred.head.generation == generation) {
                    deferred.tail
                } else {
                    cut(generation, deferred.tail)
                }

            Empty ->
                Empty
        }
}
