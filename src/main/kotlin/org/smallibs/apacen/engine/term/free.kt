package org.smallibs.apacen.engine.term

import org.smallibs.core.IList
import org.smallibs.core.ILists.foldLeft
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.StringLiteral
import org.smallibs.apacen.data.Term.Variable


object Free {
    fun IList<Term>.free(): Set<Variable> =
        this.foldLeft({ s, e -> s + e.free() }, emptySet())

    fun Term.free(): Set<Variable> =
        when (this) {
            is BinOp -> lhd.free() + rhd.free()
            is Constructor -> parameters.free()
            is Term.NumberLiteral, is StringLiteral -> emptySet()
            is Variable -> if (isAnonymous) emptySet() else setOf(this)
        }
}