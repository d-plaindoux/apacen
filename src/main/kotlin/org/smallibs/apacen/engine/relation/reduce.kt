package org.smallibs.apacen.engine.relation

import org.smallibs.apacen.data.CompoundTerm.Comparator.EQ
import org.smallibs.apacen.data.CompoundTerm.Comparator.GT
import org.smallibs.apacen.data.CompoundTerm.Comparator.GTE
import org.smallibs.apacen.data.CompoundTerm.Comparator.LT
import org.smallibs.apacen.data.CompoundTerm.Comparator.LTE
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.relation.Checker.check
import org.smallibs.apacen.engine.relation.Optimizer.optimize
import org.smallibs.apacen.engine.relation.Transformer.transform
import org.smallibs.apacen.engine.term.Unification.unify
import org.smallibs.apacen.engine.term.normalize

object Reducer {

    tailrec fun reduce(environment: Environment): Environment? {
        val equations = environment.system.normalize(environment)
        val newRuntime = equations.reduce(environment)?.let {
            Environment(it.substitutions, it.system.optimize())
        }

        if (newRuntime == null || newRuntime.system.check().not()) {
            return null
        }

        if (newRuntime == environment) {
            return environment
        }
        return reduce(newRuntime)
    }

    private fun List<Relation>.reduce(environment: Environment): Environment? {
        var currentRuntime = environment
        var currentRelations = emptyList<Relation>()

        for (equation in this) {
            val reduction = equation.reduce(environment)
            if (reduction == null) {
                return null
            }
            val equation = reduction.first
            if (equation != null) {
                currentRelations = currentRelations + equation
            }
            currentRuntime = reduction.second
        }

        return Environment(currentRuntime.substitutions, currentRelations)
    }

    private fun Relation.reduce(environment: Environment): Pair<Relation?, Environment>? =
        if (lhd is Term.NumberLiteral && rhd is Term.NumberLiteral) {
            if (when (this.comparator) {
                    EQ -> lhd.value == rhd.value
                    LT -> lhd.value < rhd.value
                    LTE -> lhd.value <= rhd.value
                    GT -> lhd.value > rhd.value
                    GTE -> lhd.value >= rhd.value
                }
            ) {
                null to environment
            } else {
                null
            }

        } else if (this.comparator == EQ && (lhd is Variable || rhd is Variable)) {
            unify(environment, lhd, rhd)?.let { null to it }
        } else {
            this to environment
        }

    private fun List<Relation>.normalize(environment: Environment): List<Relation> {
        val result = emptyList<Relation>().toMutableList()

        for (equation in this) {
            result += equation.normalize(environment)
        }

        return result.toList()
    }

    private fun Relation.normalize(environment: Environment): Relation =
        if (lhd is Variable) {
            Relation(comparator, lhd.normalize(environment), rhd.normalize(environment))
        } else if (rhd is Variable) {
            Relation(comparator, lhd.normalize(environment), rhd.normalize(environment))
        } else {
            transform()
        }
}