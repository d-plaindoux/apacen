package org.smallibs.apacen.data

import org.smallibs.core.IList

sealed interface CompoundTerm : Pretty {

    enum class Comparator(val value: String) {
        EQ("="), LT("<"), LTE("<="), GT(">"), GTE(">=");

        val isEquation: Boolean get() = this == EQ

        fun reverse(): Comparator =
            when (this) {
                EQ -> EQ
                LT -> GT
                LTE -> GTE
                GT -> LT
                GTE -> LTE
            }
    }

    data class Cut(val generation: Int = 0) : CompoundTerm {
        override fun asString(): String = "!"
    }

    data class Functor(val name: String, val parameters: IList<Term>) : CompoundTerm {
        override fun asString(): String =
            if (parameters.isEmpty()) {
                name
            } else {
                "$name(${parameters.asString(",", Term::asString, false)})"
            }
    }

    data class Relation(val comparator: Comparator, val lhd: Term, val rhd: Term) : CompoundTerm {
        override fun asString(): String =
            if (lhd is Term.Variable) {
                "${lhd.asString()} ${comparator.value} ${rhd.asString()}"
            } else {
                "${rhd.asString()} ${comparator.reverse().value} ${lhd.asString()}"
            }
    }
}
