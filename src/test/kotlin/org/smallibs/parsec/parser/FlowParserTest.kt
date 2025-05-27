package org.smallibs.parsec.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.smallibs.parsec.io.Reader

class FlowParserTest : StringSpec({

    "shouldSequenceParserReturnsAccept" {
        val parser = returns<Char, Char>('a') then returns(1)

        val result = parser(givenAReader()).fold({ it.value == 'a' to 1 }, { false })

        result shouldBe true
    }

    "shouldSequenceParserReturnsReject" {
        val parser = any<Char>() then fails<Char, Unit>()

        val result = parser(givenAReader()).fold({ false }, { true })

        result shouldBe true
    }

    "shouldSequenceParserReturnsAcceptWithLeftValue" {
        val parser = returns<Char, Char>('a') thenLeft returns(1)

        val result = parser(givenAReader()).get()

        result shouldBe  'a'
    }

    "shouldSequenceParserReturnsAcceptWithRightValue" {
        val parser = returns<Char, Char>('a') thenRight returns(1)

        val result = parser(givenAReader()).get()

        result shouldBe 1
    }

    "shouldChoiceParserReturnsAccept" {
        val parser = returns<Char, Char>('a') or returns('b')

        val result = parser(givenAReader()).fold({ it.value == 'a' }, { false })

        result shouldBe true
    }

    "shouldChoiceWithFailsParserReturnsAccept" {
        val parser = fails<Char, Char>() or returns('b')

        val result = parser(givenAReader()).fold({ it.value == 'b' }, { false })

        result shouldBe true
    }

}) {

    companion object {
        private fun <A> Response<*, A>.get(): A? = this.fold({ it.value }, { null })

        private fun givenAReader() = Reader.string("an example")
    }
}