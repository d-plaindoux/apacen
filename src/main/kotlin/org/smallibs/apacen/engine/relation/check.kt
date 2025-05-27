package org.smallibs.apacen.engine.relation

import org.smallibs.apacen.data.CompoundTerm.Comparator
import org.smallibs.apacen.data.CompoundTerm.Comparator.EQ
import org.smallibs.apacen.data.CompoundTerm.Comparator.GT
import org.smallibs.apacen.data.CompoundTerm.Comparator.GTE
import org.smallibs.apacen.data.CompoundTerm.Comparator.LT
import org.smallibs.apacen.data.CompoundTerm.Comparator.LTE
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable

object Checker {
    fun List<Relation>.check(): Boolean {
        var current = emptyList<Relation>()

        for (equation in this) {
            if (equation.check(current).not()) {
                return false
            }
            current = current + equation
        }

        return true
    }

    private fun Relation.check(current: List<Relation>): Boolean {
        for (relation in current) {
            if (this.check(relation).not()) {
                return false
            }
        }

        return true
    }

    private fun Relation.check(current: Relation): Boolean =
        if (lhd is Variable && current.lhd is Variable && lhd == current.lhd) {
            compare(comparator, rhd, current.comparator, current.rhd)
        } else if (rhd is Variable && current.rhd is Variable && rhd == current.rhd && comparator == current.comparator) {
            compare(comparator.reverse(), lhd, current.comparator.reverse(), current.lhd)
        } else if (lhd is Variable && current.rhd is Variable && lhd == current.rhd && comparator == current.comparator.reverse()) {
            compare(comparator, rhd, current.comparator.reverse(), current.lhd)
        } else if (rhd is Variable && current.lhd is Variable && rhd == current.lhd && comparator.reverse() == current.comparator) {
            compare(comparator.reverse(), lhd, current.comparator, current.rhd)
        } else {
            true
        }

    private fun compare(lcomp: Comparator, lhd: Term, rcomp: Comparator, rhd: Term): Boolean =
        if (lhd is NumberLiteral && rhd is NumberLiteral) {
            when (lcomp) {
                EQ -> true
                LT ->
                    when (rcomp) {
                        GT, GTE -> lhd.value > rhd.value
                        EQ, LT, LTE -> true
                    }

                LTE ->
                    when (rcomp) {
                        GT -> lhd.value > rhd.value
                        GTE -> lhd.value >= rhd.value
                        EQ, LT, LTE -> true
                    }

                GT ->
                    when (rcomp) {
                        LT, LTE -> lhd.value < rhd.value
                        EQ, GT, GTE -> true
                    }

                GTE ->
                    when (rcomp) {
                        LT -> lhd.value < rhd.value
                        LTE -> lhd.value <= rhd.value
                        EQ, GT, GTE -> true
                    }
            }
        } else {
            true
        }
}