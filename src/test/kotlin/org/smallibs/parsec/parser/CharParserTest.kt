package org.smallibs.parsec.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.smallibs.parsec.io.Reader
import org.smallibs.parsec.parser.Response.Reject
import org.smallibs.parsec.utils.Location


class CharParserTest : StringSpec({

    "shouldCharParserReturnsAccept" {
        val parser = char('a')

        val result = parser.invoke(Reader.string("a")).fold({ it.value == 'a' }, { false })

        result shouldBe true
    }

    "shouldCharParserReturnsFails" {
        val parser = char('a')

        val result = parser.invoke(Reader.string("b")).fold({ null }, { it })

        result shouldBe Reject(Location.create(), false, "the char <a>")
    }

    "shouldCharParserOrParserReturnsAccept" {
        val parser = char('a') or char('b')

        val result = parser.invoke(Reader.string("b")).fold({ it.value == 'b' }, { false })

        result shouldBe true
    }

    "shouldStringParserReturnsAccept" {
        val parser = string("ab")

        val result = parser.invoke(Reader.string("ab")).fold({ it.value == "ab" }, { false })

        result shouldBe true
    }

    "shouldStringParserOrReturnsAccept" {
        val parser = string("ab").or(string("ac"))

        val result = parser.invoke(Reader.string("ac")).fold({ it.value == "ac" }, { false })

        result shouldBe true
    }

    "shouldStringParserReturnsReject" {
        val parser = string("ab")

        val result = parser.invoke(Reader.string("ac")).fold({ null }, { it })

        result shouldBe Reject(Location.create(), false, "the string <ab>")
    }
})