package org.smallibs.apacen.engine.term

import org.smallibs.core.IList
import org.smallibs.core.IList.Nil
import org.smallibs.core.ILists.map
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.BinOpKind
import org.smallibs.apacen.data.Term.BinOpKind.MIN
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.StringLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.term.Variables.Direction.LEFT
import org.smallibs.apacen.engine.term.Variables.Direction.RIGHT

object Variables {
    enum class Direction {
        LEFT, RIGHT
    }

    fun Term.variables(): Map<Variable, IList<Direction>> =
        this.transformations().map { it.key to it.value.map { it.second } }.toMap()

    fun Term.transformations(): Map<Variable, IList<Pair<BinOpKind, Direction>>> =
        when (this) {
            is BinOp ->
                if (kind == MIN || kind == BinOpKind.MAX) {
                    emptyMap()
                } else {
                    lhd.transformations().map { entry ->
                        entry.key to IList.Cons(kind to LEFT, entry.value)
                    }.toMap() +
                            rhd.transformations().map { entry ->
                                entry.key to IList.Cons(kind to RIGHT, entry.value)
                            }.toMap()
                }

            is Constructor -> emptyMap()
            is NumberLiteral -> emptyMap()
            is StringLiteral -> emptyMap()
            is Variable ->
                if (isAnonymous) {
                    emptyMap()
                } else {
                    mapOf(this to Nil)
                }
        }
}