package org.smallibs.apacen.engine.declaration

import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.engine.term.Unification.unify

object Unification {

    fun unify(environment: Environment, lhd: Functor, rhd: Functor): Environment? =
        if (lhd.name == rhd.name) {
            unify(environment, lhd.parameters, rhd.parameters)
        } else {
            null
        }

    fun unify(environment: Environment, lhd: Constructor, rhd: Constructor): Environment? =
        if (lhd.name == rhd.name && lhd.parameters.size == rhd.parameters.size) {
            unify(environment, lhd.parameters, rhd.parameters)
        } else {
            null
        }
}