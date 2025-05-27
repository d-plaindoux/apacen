package org.smallibs.apacen.data

import org.smallibs.apacen.data.Term.Kind.MAX
import org.smallibs.apacen.data.Term.Kind.MIN
import org.smallibs.core.IList


sealed interface Term : Pretty {
    sealed class Kind(val value: String, val precedence: Int) {
        data object ADD : Kind("+", 1)
        data object SUB : Kind("-", 1)
        data object MUL : Kind("*", 2)
        data object DIV : Kind("/", 2)
        data object MIN : Kind("min", 3)
        data object MAX : Kind("max", 3)
        data class GEN(val name: String) : Kind(name, 4)
    }

    data class Variable(private val name: String?, private val generation: Int = 0) : Term {
        fun withGeneration(generation: Int): Variable = Variable(name, generation)
        val isAnonymous: Boolean get() = name == null
        override fun asString(): String = name?.let { name + if (generation > 0) "?$generation" else "" } ?: "_"
    }

    data class NumberLiteral(val value: Double) : Term {
        override fun asString(): String = value.toString()
    }

    data class StringLiteral(val value: String) : Term {
        override fun asString(): String = value
    }

    data class BinOp(val kind: Kind, val lhd: Term, val rhd: Term) : Term {
        override fun asString(): String =
            if (kind == MIN || kind == MAX) {
                "${kind.value}(${lhd.asString()}, ${rhd.asString()})"
            } else {
                "(${lhd.asString()} ${kind.value} ${rhd.asString()})"
            }
    }

    data class Constructor(val name: String, val parameters: IList<Term>) : Term {
        override fun asString(): String =
            if (parameters.isEmpty()) {
                name
            } else if (name == "[]" && parameters.size == 2) {
                "${parameters.asString("[", Term::asString, false)}]"
            } else {
                "$name(${parameters.asString(",", Term::asString, false)})"
            }
    }
}