package org.smallibs.apacen.engine.declaration

import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.engine.term.normalize
import org.smallibs.core.ILists.map

fun Functor.normalize(environment: Environment): Functor =
    Functor(this.name, parameters.map { it.normalize(environment) })