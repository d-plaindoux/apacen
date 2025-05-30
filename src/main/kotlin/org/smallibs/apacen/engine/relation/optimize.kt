package org.smallibs.apacen.engine.relation

import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.CompoundTerm.Comparator.EQ
import org.smallibs.apacen.data.CompoundTerm.Comparator.GT
import org.smallibs.apacen.data.CompoundTerm.Comparator.GTE
import org.smallibs.apacen.data.CompoundTerm.Comparator.LT
import org.smallibs.apacen.data.CompoundTerm.Comparator.LTE
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.BinOpKind.MAX
import org.smallibs.apacen.data.Term.BinOpKind.MIN
import org.smallibs.apacen.data.Term.Variable

object Optimizer {
    fun List<Relation>.optimize(): List<Relation> {
        var current = emptyList<Relation>()

        for (equation in this) {
            current = equation.optimize(current)
        }

        return current
    }

    private fun Relation.optimize(current: List<Relation>): List<Relation> {
        var result = emptyList<Relation>()

        for (index in 0..current.size - 1) {
            val combined = this.optimize(current[index])

            if (combined == null) {
                result = result + current[index]
            } else {
                return result + combined + if (index + 1 == current.size) emptyList() else current.subList(
                    index + 1,
                    current.size
                )
            }
        }

        return result + this
    }

    private fun Relation.optimize(current: Relation): Relation? =
        if (lhd is Variable && current.lhd is Variable && lhd == current.lhd && comparator == current.comparator) {
            optimize(comparator, rhd, current.rhd)?.let { Relation(comparator, lhd, it) }
        } else if (rhd is Variable && current.rhd is Variable && rhd == current.rhd && comparator == current.comparator) {
            optimize(comparator.reverse(), lhd, current.lhd)?.let { Relation(comparator.reverse(), rhd, it) }
        } else if (lhd is Variable && current.rhd is Variable && lhd == current.rhd && comparator == current.comparator.reverse()) {
            optimize(comparator, rhd, current.lhd)?.let { Relation(comparator, lhd, it) }
        } else if (rhd is Variable && current.lhd is Variable && rhd == current.lhd && comparator.reverse() == current.comparator) {
            optimize(comparator.reverse(), lhd, current.rhd)?.let { Relation(comparator.reverse(), rhd, it) }
        } else {
            null
        }

    private fun optimize(comparator: CompoundTerm.Comparator, lhd: Term, rhd: Term): Term? =
        when (comparator) {
            EQ -> null
            LT -> BinOp(MIN, lhd, rhd)
            LTE -> BinOp(MIN, lhd, rhd)
            GT -> BinOp(MAX, lhd, rhd)
            GTE -> BinOp(MAX, lhd, rhd)
        }
}