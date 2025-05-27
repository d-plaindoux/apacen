package org.smallibs.apacen.engine.declaration

import org.smallibs.core.IList
import org.smallibs.core.ILists.foldLeft
import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.CompoundTerm.Cut
import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.term.Free.free


object Free {
    fun IList<CompoundTerm>.free(): Set<Variable> =
        this.foldLeft({ s, e -> s + e.free() }, emptySet())

    fun CompoundTerm.free(): Set<Variable> =
        when (this) {
            is Relation -> IList.of(this.lhd, this.rhd).free()
            is Functor -> parameters.free()
            is Cut -> emptySet()
        }
}