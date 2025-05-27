package org.smallibs.parsec.parser

import org.smallibs.parsec.parser.Response.Accept
import org.smallibs.parsec.parser.Response.Reject
import org.smallibs.parsec.utils.Location

//
// Basic parsers
//

fun <I, A> returns(v: A): Parser<I, A> = {
    Accept(v, it, false)
}

fun <I, A> fails(reason: String? = null): Parser<I, A> = {
    Reject(it.location(), false, reason)
}

//
// Element parser
//

fun <I> any(): Parser<I, I> = {
    it.read()?.let { Accept(it.first, it.second, true) } ?: Reject(it.location(), false)
}

//
// Negation
//

fun <I> not(p: Parser<I, *>): Parser<I, I> = { reader ->
    p(reader).fold({ Reject(reader.location(), false) }, { any<I>()(reader) })
}

//
// Backtracking
//

fun <I, A> `try`(p: Parser<I, A>): Parser<I, A> = { p(it).fold({ it }, { Reject(it.location, false) }) }

//
// Lazy
//

fun <I, A> lazy(p: () -> Parser<I, A>): Parser<I, A> = { p()(it) }

//
// Tracer
//

fun <A> trace(p: Parser<Char, A>, name: String = "", error: Exception = Exception()): Parser<Char, A> = { reader ->
    val s = reader.pushTrace(name)
    when (val r = p(s)) {
        is Accept<Char, A> -> Accept(r.value, r.input.popTrace(), r.consumed)
        is Reject<Char> -> Reject(r.location, r.consumed, r.reason)
    }
}

//
// Locator
//

fun <I, A> locate(p: Parser<I, A>): Parser<I, Pair<Location, A>> = { reader ->
    when (val r = p(reader)) {
        is Accept<I, A> -> Accept(r.input.location() to r.value, r.input.popTrace(), r.consumed)
        is Reject<I> -> Reject(r.location, r.consumed, r.reason)
    }
}