package org.smallibs.apacen.engine.relation

import org.smallibs.apacen.data.CompoundTerm.Comparator
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.BinOpKind.ADD
import org.smallibs.apacen.data.Term.BinOpKind.DIV
import org.smallibs.apacen.data.Term.BinOpKind.GEN
import org.smallibs.apacen.data.Term.BinOpKind.MAX
import org.smallibs.apacen.data.Term.BinOpKind.MIN
import org.smallibs.apacen.data.Term.BinOpKind.MUL
import org.smallibs.apacen.data.Term.BinOpKind.SUB
import org.smallibs.apacen.engine.term.Variables.Direction
import org.smallibs.apacen.engine.term.Variables.Direction.LEFT
import org.smallibs.apacen.engine.term.Variables.Direction.RIGHT
import org.smallibs.apacen.engine.term.Variables.variables
import org.smallibs.core.IList
import org.smallibs.core.IList.Cons
import org.smallibs.core.IList.Nil

object Transformer {

    fun Relation.transform(): Relation {
        val leftVariables = lhd.variables()
        val rightVariables = rhd.variables()

        return if (leftVariables.isNotEmpty()) {
            val variable = leftVariables.toList()[0]
            Relation(comparator.reverse(), rhd, lhd).transform(variable.second)
        } else if (rightVariables.isNotEmpty()) {
            val variable = rightVariables.toList()[0]
            transform(variable.second)
        } else {
            this
        }
    }

    tailrec fun Relation.transform(path: IList<Direction>): Relation =
        when (path) {
            Nil -> Relation(comparator, lhd, rhd)
            is Cons<Direction> ->
                when (rhd) {
                    is BinOp -> {
                        val inversion = inverse(comparator, path.head, lhd, rhd)
                        if (inversion == null) {
                            Relation(comparator, lhd, rhd)
                        } else {
                            inversion.transform(path.tail)
                        }
                    }

                    else -> Relation(comparator, lhd, rhd)
                }
        }

    private fun inverse(comparator: Comparator, direction: Direction, lhd: Term, rhd: BinOp): Relation? =
        when (direction) {
            LEFT -> {
                when (rhd.kind) {
                    ADD -> Relation(comparator, BinOp(SUB, lhd, rhd.rhd), rhd.lhd)
                    SUB -> Relation(comparator, BinOp(ADD, lhd, rhd.rhd), rhd.lhd)
                    MUL -> rhd.rhd.negative()?.let { negative ->
                        val comparator = if (negative) comparator.reverse() else comparator
                        Relation(comparator, BinOp(DIV, lhd, rhd.rhd), rhd.lhd)
                    }

                    DIV -> rhd.rhd.negative()?.let { negative ->
                        val comparator = if (negative) comparator.reverse() else comparator
                        Relation(comparator, BinOp(MUL, lhd, rhd.rhd), rhd.lhd)
                    }

                    MIN, MAX, is GEN -> throw IllegalArgumentException()
                }
            }

            RIGHT ->
                when (rhd.kind) {
                    ADD -> Relation(comparator, BinOp(SUB, lhd, rhd.lhd), rhd.rhd)
                    SUB -> Relation(comparator.reverse(), BinOp(SUB, rhd.lhd, lhd), rhd.rhd)
                    MUL -> rhd.lhd.negative()?.let { negative ->
                        val comparator = if (negative) comparator.reverse() else comparator
                        Relation(comparator, BinOp(DIV, lhd, rhd.lhd), rhd.rhd)
                    }

                    DIV -> rhd.lhd.negative()?.let { negative ->
                        val comparator = if (negative) comparator.reverse() else comparator
                        Relation(comparator.reverse(), BinOp(DIV, rhd.lhd, lhd), rhd.rhd)
                    }

                    MIN, MAX, is GEN -> throw IllegalArgumentException()
                }
        }

    private fun Term.negative(): Boolean? =
        when (this) {
            is BinOp -> null
            is Term.Constructor -> null
            is Term.NumberLiteral -> value < 0
            is Term.StringLiteral -> null
            is Term.Variable -> null
        }
}