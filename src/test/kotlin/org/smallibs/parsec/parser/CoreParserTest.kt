package org.smallibs.parsec.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.smallibs.parsec.io.Reader

class CoreParserTest : StringSpec({

    "shouldReturnsParserReturnsAccept" {
        val parser: Parser<Char, Boolean> = returns(true)

        val result = parser.invoke(givenAReader()).fold({ true }, { false })

        result shouldBe true
    }

    "shouldFailsParserReturnsError" {
        val parser = fails<Char, Char>()

        val result = parser.invoke(givenAReader()).fold({ true }, { false })

        result shouldBe false
    }

    "shouldAnyReturnsSuccess" {
        val parser = any<Char>()

        val result = parser.invoke(givenAReader("session-16-16h00-avril")).fold({ true }, { false })

        result shouldBe true
    }

    "shouldAnyReturnsReject" {
        val parser = any<Char>()

        val result = parser.invoke(givenAReader()).fold({ false }, { true })

        result shouldBe true
    }

    "shouldNotReturnsReject" {
        val parser = not(any<Char>())

        val result = parser.invoke(givenAReader("session-16-16h00-avril")).fold({ false }, { true })

        result shouldBe true
    }
}) {
    companion object {
        private fun givenAReader(s: String = "") = Reader.string(s)
    }
}
