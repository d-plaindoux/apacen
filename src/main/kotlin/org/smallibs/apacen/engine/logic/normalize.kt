package org.smallibs.apacen.engine.logic

import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Logic

object Normalize {
    fun Logic.normalize(): List<List<Relation>> =
        when (this) {
            is Logic.And -> {
                val lhd = lhd.normalize()
                val rhd = rhd.normalize()
                lhd.flatMap { l -> rhd.map { r -> l + r } }
            }

            is Logic.Or -> lhd.normalize() + rhd.normalize()
            is Logic.Single -> listOf(listOf(relation))
        }
}