package org.smallibs.apacen.runtime

import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.Declaration.Fact
import org.smallibs.apacen.data.Declaration.Rule
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.Proof
import org.smallibs.apacen.engine.Solver
import org.smallibs.apacen.engine.declaration.Free.free
import org.smallibs.apacen.engine.term.normalize
import org.smallibs.core.IList
import org.smallibs.core.IList.Empty
import org.smallibs.core.ILists.append
import org.smallibs.core.ILists.map
import org.smallibs.core.ILists.reverse
import java.io.File

data class Interpreter(private val rules: IList<Rule>) {

    constructor() : this(Empty)

    fun load(file: File): Interpreter =
        println("import file <$file>").let {
            val t0 = System.currentTimeMillis()
            val loaded = Loader.load(file)?.let {
                process(it)
            } ?: this
            return loaded
        }

    fun load(rules: IList<Rule>): Interpreter =
        process(rules to Empty)

    fun process(program: Pair<IList<Rule>, IList<Fact>>): Interpreter {
        val rules = this.rules.append(program.first)

        with(Solver(this.rules)) {
            program.second.reverse().map { it.value }.map { goals ->
                val variables = goals.free()
                next(goals, variables, solve(goals).display(goals, 0, variables))
            }
        }

        return Interpreter(rules)
    }

    private fun Solver.next(goals: IList<CompoundTerm>, variables: Set<Variable>, proof: Proof?) {
        var proofNumber = 0
        var currentProof = proof

        while (currentProof != null) {
            proofNumber += 1
            currentProof = currentProof.let {
                backtrack(it).display(goals, proofNumber, variables)
            }
        }
    }

    private fun Proof?.display(goals: IList<CompoundTerm>, proofNumber: Int, variables: Set<Variable>): Proof? {
        this?.let { proof ->
            if (proofNumber > 0) {
                println()
            }

            val variables: List<String> = variables.mapNotNull { variable ->
                variable.normalize(proof.environment).let {
                    if (it != variable) {
                        "${variable.asString()}=${it.asString()}"
                    } else {
                        null
                    }
                }
            }

            val relations = proof.environment.system.map {
                it.asString()
            }

            if (variables.isNotEmpty() || relations.isNotEmpty()) {
                println("Solution <${proofNumber + 1}>:")
                variables.forEach { println("| $it") }
                relations.forEach { println("| $it") }
            }
        }

        if (this == null && proofNumber == 0) {
            System.err.println("| No solution found for")
            System.err.println("?- ${goals.asString(",", CompoundTerm::asString, false)}.")
        }

        return this
    }
}
