package org.smallibs.apacen.engine.declaration

import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.CompoundTerm.Cut
import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Declaration.Rule
import org.smallibs.apacen.engine.term.Fresh.fresh
import org.smallibs.core.IList
import org.smallibs.core.IList.Cons
import org.smallibs.core.IList.Empty

object Fresh {
    fun Rule.fresh(generation: Int): Rule = Rule(head.fresh(generation), tail.fresh(generation))

    fun Functor.fresh(generation: Int): Functor = Functor(name, parameters.fresh(generation))

    fun CompoundTerm.fresh(generation: Int): CompoundTerm =
        when (this) {
            is Functor -> this.fresh(generation)
            is Relation -> Relation(this.comparator, lhd.fresh(generation), rhd.fresh(generation))
            is Cut -> Cut(generation)
        }

    fun IList<CompoundTerm>.fresh(generation: Int): IList<CompoundTerm> =
        when (this) {
            is Cons<CompoundTerm> -> Cons(head.fresh(generation), tail.fresh(generation))
            Empty -> Empty
        }
}