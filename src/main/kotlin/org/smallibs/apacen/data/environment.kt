package org.smallibs.apacen.data

import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.core.IList

data class Substitutions(val values: IList<Pair<Variable, Term>> = IList.Nil) {
    operator fun get(variable: Variable): Term? {
        tailrec fun find(variable: Variable, values: IList<Pair<Variable, Term>>): Term? =
            when (values) {
                is IList.Cons<Pair<Variable, Term>> ->
                    if (values.head.first == variable) {
                        values.head.second
                    } else {
                        find(variable, values.tail)
                    }

                IList.Nil -> null
            }

        return find(variable, values)
    }

    operator fun plus(entry: Pair<Variable, Term>): Substitutions =
        Substitutions(IList.Cons(entry, values))

}

data class Environment(val substitutions: Map<Variable, Term>, val system: List<Relation> = emptyList()) {
    constructor(vararg term: Pair<Variable, Term>) : this(mapOf(*term))

    fun addSubstitution(variable: Variable, term: Term): Environment =
        if (term == variable) this else Environment(this.substitutions + (variable to term), system)

    fun addEquation(entry: Relation): Environment =
        Environment(this.substitutions, system + entry)
}