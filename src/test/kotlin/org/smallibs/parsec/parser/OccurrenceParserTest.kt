package org.smallibs.parsec.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.smallibs.parsec.io.Reader

class OccurrenceParserTest : StringSpec({

    "shouldOptionalParserWithEmptyStringReturnsAccept" {
        val parser = any<Char>().opt then eos()

        val result = parser.invoke(Reader.string("")).fold({ it.value.first == null }, { false })

        result shouldBe true
    }

    "shouldOptionalParserWithNonEmptyStringReturnsAccept" {
        val parser = any<Char>().opt then eos()

        val result = parser.invoke(Reader.string("a")).fold({ it.value.first == 'a' }, { false })

        result shouldBe true
    }

    "shouldOptionalRepeatableParserWithEmptyStringReturnsAccept" {
        val parser = any<Char>().optrep then eos()

        val result = parser.invoke(Reader.string("")).fold({ true }, { false })

        result shouldBe true
    }

    "shouldOptionalRepeatableParserWithNonEmptyStringReturnsAccept" {
        val parser = any<Char>().optrep then eos()

        val result = parser.invoke(Reader.string("ab")).fold({ it.value.first == listOf('a', 'b') }, { false })

        result shouldBe true
    }

    "shouldRepeatableParserWithEmptyStringReturnsReject" {
        val parser = any<Char>().rep then eos()

        val result = parser.invoke(Reader.string("")).fold({ false }, { true })

        result shouldBe true
    }

    "shouldRepeatableParserWithNonEmptyStringReturnsAccept" {
        val parser = any<Char>().rep then eos()

        val result = parser.invoke(Reader.string("ab")).fold({ it.value.first == listOf('a', 'b') }, { false })

        result shouldBe true
    }

    "shouldRepeatableNotParserWithNonEmptyStringReturnsAccept" {
        val parser = not(char('a')).rep then eos()

        val result = parser.invoke(Reader.string("bc")).fold({ it.value.first == listOf('b', 'c') }, { false })

        result shouldBe true
    }

    "shouldRepeatableNotThenCharParserWithNonEmptyStringReturnsAccept" {
        val parser = not(char('a')).optrep

        val result = parser.invoke(Reader.string("bca")).fold({ it.value == listOf('b', 'c') }, { false })

        result shouldBe true
    }

    "shouldBeAbleToParseLargeInput" {
        val parser = any<Char>().optrep thenLeft eos()

        val size = 16 * 1024
        val content = "a".repeat(size)

        val result = parser.invoke(Reader.string(content)).fold({ it.value.size }, { 0 })

        result shouldBe size
    }
})