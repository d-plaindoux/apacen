package org.smallibs.apacen.data

import org.smallibs.parsec.utils.Location

data class Located<A>(val region: Pair<Location, Location>, val data: A)