package org.smallibs.parsec.parser

import org.smallibs.parsec.parser.Response.Accept
import org.smallibs.parsec.parser.Response.Reject

//
// Specific Char parsers
//

val any: Parser<Char, Char> get() = any()

fun charIn(vararg s: Char): Parser<Char, Char> =
    (any satisfy { it in s }).or(fails("a in char in {${s.joinToString(",")}}"))

fun charIn(s: CharRange): Parser<Char, Char> =
    (any satisfy { it in s }).or(fails("a char in [${s.first}-${s.last}]"))

fun char(c: Char): Parser<Char, Char> =
    charIn(c).or(fails("the char <$c>"))

fun string(string: String): Parser<Char, String> = { s ->
    when (val read = s.read(string.length)) {
        null -> Reject(s.location(), false, "the string <$string>")
        else -> if (read.first == string.toList()) {
            Accept(string, read.second, !string.isEmpty())
        } else {
            Reject(s.location(), false, "the string <$string>")
        }
    }
}

fun lowerLetter(): Parser<Char, Char> =
    charIn('a'..'z').or(fails("a lower case letter"))

fun upperLetter(): Parser<Char, Char> =
    charIn('A'..'Z').or(fails("an upper case letter"))

fun alpha(): Parser<Char, Char> =
    lowerLetter().or(upperLetter()).or(fails("a letter"))

fun digit(): Parser<Char, Char> =
    charIn('0'..'9').or(fails("a digit"))

fun alphaNum(): Parser<Char, Char> =
    alpha().or(digit()).or(fails("a letter or a digit"))

