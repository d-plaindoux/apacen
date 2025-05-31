package org.smallibs.apacen.engine.delta

import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.StringLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.Solver.Runtime
import org.smallibs.apacen.engine.term.normalize
import org.smallibs.core.IList
import org.smallibs.core.IList.Cons
import org.smallibs.core.IList.Nil
import org.smallibs.core.ILists.map

object Delta {

    var members: Map<String, Pair<Int?, (IList<Term>, Runtime) -> Pair<Runtime, IList<CompoundTerm>>?>> =
        mapOf(
            "abort" to (0 to { l, r ->
                System.exit(1)
                val r = Runtime(r.goals, r.postponed, r.rules, r.environment, r.deferred, true)
                r to Nil
            }),
            "trace_on" to (0 to { l, r ->
                val r = Runtime(r.goals, r.postponed, r.rules, r.environment, r.deferred, true)
                r to Nil
            }),
            "trace_off" to (0 to { l, r ->
                val r = Runtime(r.goals, r.postponed, r.rules, r.environment, r.deferred, false)
                r to Nil
            }),
            "const0" to (1 to { l, r ->
                val t = (l as Cons<Term>).head.normalize(r.environment)
                if (t is Constructor && t.parameters.isEmpty()) r to Nil else null
            }),
            "call" to (1 to { l, r ->
                val t = (l as Cons<Term>).head.normalize(r.environment)
                if (t is Constructor) r to IList.of(Functor(t.name, t.parameters)) else null
            }),
            "true" to (0 to { l, r ->
                r to Nil
            }),
            "fail" to (0 to { l, r ->
                null
            }),
            "number" to (1 to { l, r ->
                if ((l as Cons<Term>).head.normalize(r.environment) is NumberLiteral) r to Nil else null
            }),
            "string" to (1 to { l, r ->
                if ((l as Cons<Term>).head.normalize(r.environment) is StringLiteral) r to Nil else null
            }),
            "bound" to (1 to { l, r ->
                if ((l as Cons<Term>).head.normalize(r.environment) !is Variable) r to Nil else null
            }),
            "unbound" to (1 to { l, r ->
                if ((l as Cons<Term>).head.normalize(r.environment) is Variable) r to Nil else null
            }),
            "println" to (null to { l, r ->
                println(l.map { it.normalize(r.environment) }.asString(" ", Term::asString, false))
                r to Nil
            })
        )

    fun Functor.deltaReduce(runtime: Runtime): Pair<Runtime, IList<CompoundTerm>>? =
        members[this.name]?.let {
            if (it.first == null || it.first == this.parameters.size) {
                it.second(this.parameters, runtime)
            } else {
                null
            }
        }
}