package org.smallibs.parsec.parser

import org.smallibs.parsec.io.Reader
import org.smallibs.parsec.utils.Location

//
// Response data structure for Parser Combinator
//

sealed class Response<I, out A>(open val consumed: Boolean) {

    //
    // Possible Responses
    //

    data class Accept<I, out A>(
        val value: A,
        val input: Reader<I>,
        override val consumed: Boolean,
    ) : Response<I, A>(consumed)

    data class Reject<I>(
        val location: Location,
        override val consumed: Boolean,
        val reason: String? = null,
    ) : Response<I, Nothing>(consumed)

    //
    // Catamorphism
    //

    fun <B> fold(accept: (Accept<I, A>) -> B, reject: (Reject<I>) -> B): B =
        when (this) {
            is Accept -> accept(this)
            is Reject -> reject(this)
        }

}

