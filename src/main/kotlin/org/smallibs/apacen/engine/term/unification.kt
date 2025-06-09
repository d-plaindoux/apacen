package org.smallibs.apacen.engine.term

import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.term.Free.free
import org.smallibs.core.IList
import org.smallibs.core.IList.Cons
import org.smallibs.core.IList.Nil

object Unification {
    fun unify(environment: Environment, lhd: IList<Term>, rhd: IList<Term>): Environment? =
        when (lhd) {
            is Cons<Term> -> when (rhd) {
                is Cons<Term> -> unify(environment, lhd.head, rhd.head)?.let {
                    unify(it, lhd.tail, rhd.tail)
                }

                is Nil -> null
            }

            is Nil -> when (rhd) {
                is Cons<Term> -> null
                is Nil -> environment
            }
        }

    fun unify(environment: Environment, lhd: Term, rhd: Term): Environment? {
        val lhd = lhd.normalize(environment)
        val rhd = rhd.normalize(environment)

        return when (lhd) {
            is Variable ->
                if (lhd == rhd) {
                    environment
                } else if (rhd.free().contains(lhd)) {
                    // Reject cyclic unification [?]
                    null
                } else {
                    environment.addSubstitution(lhd, rhd)
                }

            is Constructor ->
                when (rhd) {
                    is Variable ->
                        if (lhd.free().contains(rhd)) {
                            // Reject cyclic unification [?]
                            null
                        } else {
                            environment.substitutions[rhd]?.let { unify(environment, lhd, it) }
                                ?: (environment.addSubstitution(rhd, lhd))
                        }

                    is Constructor ->
                        if (lhd.name == rhd.name && lhd.parameters.size == rhd.parameters.size) {
                            unify(environment, lhd.parameters, rhd.parameters)
                        } else {
                            null
                        }

                    else -> null
                }

            is BinOp ->
                when (rhd) {
                    is Variable ->
                        if (lhd.free().contains(rhd)) {
                            // Reject cyclic unification [?]
                            null
                        } else {
                            environment.addSubstitution(rhd, lhd)
                        }

                    is BinOp ->
                        if (lhd.kind == rhd.kind) {
                            unify(environment, lhd.lhd, rhd.lhd)?.let {
                                unify(it, lhd.rhd, rhd.rhd)
                            }
                        } else {
                            null
                        }

                    else -> null
                }

            else ->
                when (rhd) {
                    is Variable ->
                        if (lhd.free().contains(rhd)) {
                            // Reject cyclic unification [?]
                            null
                        } else {
                            environment.addSubstitution(rhd, lhd)
                        }

                    else -> if (lhd == rhd) environment else null
                }
        }
    }
}