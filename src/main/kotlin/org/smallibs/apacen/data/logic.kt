package org.smallibs.apacen.data

import org.smallibs.apacen.data.CompoundTerm.Relation

sealed class Logic {
    data class Single(val relation: Relation) : Logic()
    data class And(val lhd: Logic, val rhd: Logic) : Logic()
    data class Or(val lhd: Logic, val rhd: Logic) : Logic()
}