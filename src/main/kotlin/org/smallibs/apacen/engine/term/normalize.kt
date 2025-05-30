package org.smallibs.apacen.engine.term

import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.BinOpKind.ADD
import org.smallibs.apacen.data.Term.BinOpKind.DIV
import org.smallibs.apacen.data.Term.BinOpKind.GEN
import org.smallibs.apacen.data.Term.BinOpKind.MAX
import org.smallibs.apacen.data.Term.BinOpKind.MIN
import org.smallibs.apacen.data.Term.BinOpKind.MUL
import org.smallibs.apacen.data.Term.BinOpKind.SUB
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.StringLiteral
import org.smallibs.core.ILists.map
import kotlin.Double.Companion.NaN

fun Term.normalize(environment: Environment): Term =
    when (this) {
        is BinOp -> {
            val l = lhd.normalize(environment)
            val r = rhd.normalize(environment)

            if (l is NumberLiteral && r is NumberLiteral) {
                when (kind) {
                    ADD -> NumberLiteral(l.value + r.value)
                    SUB -> NumberLiteral(l.value - r.value)
                    MUL -> NumberLiteral(l.value * r.value)
                    DIV -> NumberLiteral(l.value / r.value)
                    MIN -> NumberLiteral(l.value.coerceAtMost(r.value))
                    MAX -> NumberLiteral(l.value.coerceAtLeast(r.value))
                    is GEN -> BinOp(kind, l, r)
                }
            } else if (l is NumberLiteral && l.value == 0.0) { // right neutral for + and absorbent for * and /
                when (kind) {
                    ADD -> r
                    SUB -> BinOp(MUL, NumberLiteral(-1.0), r) // TODO ?
                    MUL, DIV -> NumberLiteral(0.0)
                    MIN, MAX, is GEN -> BinOp(kind, l, r)
                }
            } else if (r is NumberLiteral && r.value == 0.0) { // right neutral for + and - and absorbent for *
                when (kind) {
                    ADD, SUB -> l
                    MUL -> NumberLiteral(0.0)
                    DIV -> NumberLiteral(NaN)
                    MIN, MAX, is GEN -> BinOp(kind, l, r)
                }
            } else if (l is NumberLiteral && l.value == 1.0) { // left Neutral for *
                when (kind) {
                    ADD, SUB, DIV -> BinOp(kind, l, r)
                    MUL -> r
                    MIN, MAX, is GEN -> BinOp(kind, l, r)
                }
            } else if (r is NumberLiteral && r.value == 1.0) { // right Neutral for * and /
                when (kind) {
                    ADD, SUB -> BinOp(kind, l, r)
                    MUL, DIV -> l
                    MIN, MAX, is GEN -> BinOp(kind, l, r)
                }
            } else {
                BinOp(kind, l, r)
            }
        }

        is Constructor -> Constructor(this.name, parameters.map { it.normalize(environment) })
        is Term.Variable -> if (this.isAnonymous) this else environment.substitutions[this]?.normalize(environment)
            ?: this

        is NumberLiteral -> this
        is StringLiteral -> this
    }