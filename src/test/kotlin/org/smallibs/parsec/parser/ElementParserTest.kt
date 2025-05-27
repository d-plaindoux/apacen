package org.smallibs.parsec.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.smallibs.parsec.io.Reader

class ElementParserTest : StringSpec({

    "shouldAnyParserReturnsAccept" {
        val parser = any<Char>()

        val result = parser.invoke(Reader.string("a")).fold({ it.value == 'a' && it.consumed }, { false })

        result shouldBe true
    }

    "shouldAnyParserReturnsReject" {
        val parser = any<Char>()

        val result = parser.invoke(Reader.string("")).fold({ false }, { true })

        result shouldBe true
    }

    "shouldEOSParserReturnsAccept" {
        val parser = eos<Char>()

        val result = parser.invoke(Reader.string("")).fold({ true }, { false })

        result shouldBe true
    }

    "shouldEOSParserReturnsReject" {
        val parser = eos<Char>()

        val result = parser.invoke(Reader.string("session-16-16h00-avril")).fold({ false }, { true })

        result shouldBe true
    }

    "shouldChoiceParserReturnsReject" {
        val parser = ((any<Char>() thenLeft any()) or any()) then eos()

        val result = parser.invoke(Reader.string("a")).fold({ false }, { true })

        result shouldBe true
    }

    "shouldChoiceWithBacktrackParserReturnsAccept" {
        val parser = (`try`((any<Char>() then any()).map { it.first }) or any()) then eos()

        val result = parser.invoke(Reader.string("a")).fold({ true }, { false })

        result shouldBe true
    }

    "shouldSatisfyParserReturnsAccept" {
        val parser = any<Char>()

        val result = parser.invoke(Reader.string("a")).fold({ it.value == 'a' && it.consumed }, { false })

        result shouldBe true
    }

    "shouldNotSatisfyOrAnyParserReturnsAccept" {
        val parser = `try`(any<Char>().satisfy { it == 'a' }) or any()

        val result = parser.invoke(Reader.string("b")).fold({ it.value == 'b' && it.consumed }, { false })

        result shouldBe true
    }

    "shouldNotCharParserReturnsAccept" {
        val parser = not(char('a'))

        val result = parser.invoke(Reader.string("b")).fold({ it.value == 'b' && it.consumed }, { false })

        result shouldBe true
    }

    "shouldNotCharParserReturnsReject" {
        val parser = not(char('a'))

        val result = parser.invoke(Reader.string("a")).fold({ false }, { true })

        result shouldBe true
    }
})