package org.smallibs.parsec.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.smallibs.parsec.io.Reader

class MonadParserTest : StringSpec({

    "shouldMappedReturnsParserReturnsAccept" {
        val parser = returns<Char, Char>('a').map { it -> it == 'a' }

        val result = parser.invoke(givenAReader()).get()

        result shouldBe true
    }

    "shouldJoinReturnedReturns" {
        val parser = join(returns(any<Char>()))

        val result = parser.invoke(givenAReader()).get()

        result shouldBe 'a'
    }

    "shouldJoinReturnedReturnsWithConsumed" {
        val parser = join(returns(any<Char>()))

        val result = parser.invoke(givenAReader())

        result.consumed shouldBe true
    }

    "shouldJoinReturnedReturnsWithoutConsumed" {
        val parser = join(returns(returns<Char, Char>('a')))

        val result = parser.invoke(givenAReader())

        result.consumed shouldBe false
    }

    "shouldFlapMappedReturnsParserReturnsAccept" {
        val parser = any<Char>().flatMap { returns<Char, String>(it + "b") }.map { it == "ab" }

        val result = parser.invoke(givenAReader()).get()

        result shouldBe true
    }

    "shouldFlapMappedReturnsParserReturnsConsumed" {
        val parser = any<Char>().flatMap { returns<Char, String>(it + "b") }.map { it == "ab" }

        val result = parser(givenAReader())

        result.consumed shouldBe true
    }

    "shouldFlapMappedReturnsParserReturnsError" {
        val parser = returns<Char, Char>('a').flatMap { fails<Char, Char>() }

        val result = parser(givenAReader()).fold({ true }, { false })

        result shouldBe false
    }
}) {
    companion object {
        private fun <A> Response<*, A>.get(): A? = this.fold({ it.value }, { null })

        private fun givenAReader() = Reader.string("an example")
    }
}
