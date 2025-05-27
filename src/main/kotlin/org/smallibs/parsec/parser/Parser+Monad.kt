package org.smallibs.parsec.parser

import org.smallibs.parsec.parser.Response.Accept
import org.smallibs.parsec.parser.Response.Reject

//
// Parser providing pseudo-Monadic ADT
//

infix fun <I, A, B> Parser<I, A>.map(f: (A) -> B): Parser<I, B> = {
    this(it).fold({
        Accept(f(it.value), it.input, it.consumed)
    }, {
        Reject(it.location, it.consumed)
    })
}

fun <I, A> join(p: Parser<I, Parser<I, A>>): Parser<I, A> = {
    val a = p(it)
    when (a) {
        is Accept -> {
            when (val b = a.value.invoke(a.input)) {
                is Reject -> Reject(b.location, b.consumed || a.consumed)
                is Accept -> Accept(b.value, b.input, b.consumed || a.consumed)
            }
        }

        is Reject -> Reject(a.location, a.consumed)
    }
}

infix fun <I, A, B> Parser<I, A>.flatMap(f: (A) -> Parser<I, B>): Parser<I, B> =
    join(this map f)

//
// Filtering
//

infix fun <I, A> Parser<I, A>.satisfy(p: (A) -> Boolean): Parser<I, A> = { s ->
    when (val r = this(s)) {
        is Accept<I, A> ->
            if (p(r.value)) {
                r
            } else {
                Reject(r.input.location(), false, null)
            }

        is Reject<I> -> r
    }
}
