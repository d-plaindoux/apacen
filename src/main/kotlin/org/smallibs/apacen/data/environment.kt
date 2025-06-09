package org.smallibs.apacen.data

import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.parser.SolverParser.term
import org.smallibs.apacen.parser.SolverParser.variable
import org.smallibs.core.IList
import java.util.*
import java.util.Map.entry

data class Substitutions(val values: TreeMap<Variable, Term>) {
    operator fun get(variable: Variable): Term? = values[variable]

    fun add(variable: Variable, term: Term): Substitutions {
        values.put(variable, term)
        return this
    }
}

data class Environment(val substitutions: TreeMap<Variable, Term>, val system: List<Relation> = emptyList()) {
    constructor(vararg term: Pair<Variable, Term>) : this(TreeMap(mapOf(*term)))

    fun addSubstitution(variable: Variable, term: Term): Environment {
        if (term != variable)
            this.substitutions.put(variable, term)

        return this
    }

    fun addEquation(entry: Relation): Environment =
        Environment(this.substitutions, system + entry)

    fun copy(): Environment = Environment(TreeMap(substitutions), system)
}