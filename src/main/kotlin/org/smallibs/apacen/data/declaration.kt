package org.smallibs.apacen.data

import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.core.IList

sealed interface Declaration : Pretty {
    data class Fact(val value: IList<CompoundTerm>) : Declaration {
        override fun asString(): String {
            return ":- ${value.asString(",", CompoundTerm::asString, printEmpty = false)}."
        }
    }

    data class Rule(val head: Functor, val tail: IList<CompoundTerm>) : Declaration {
        override fun asString(): String {
            return "${head.asString()} :- ${tail.asString(",", CompoundTerm::asString, printEmpty = false)}."
        }
    }
}
