package org.smallibs.apacen.engine.term

import org.smallibs.core.IList
import org.smallibs.core.IList.Cons
import org.smallibs.core.IList.Empty
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.Variable

object Fresh {
    fun Term.fresh(generation: Int): Term =
        when (this) {
            is BinOp -> BinOp(kind, lhd.fresh(generation), rhd.fresh(generation))
            is Variable -> if (isAnonymous) this else this.withGeneration(generation)
            is Constructor -> Constructor(name, parameters.fresh(generation))
            else -> this
        }

    fun IList<Term>.fresh(generation: Int): IList<Term> =
        when (this) {
            is Cons<Term> -> Cons(head.fresh(generation), tail.fresh(generation))
            is Empty -> Empty
        }
}
