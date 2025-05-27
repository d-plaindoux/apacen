package org.smallibs.apacen.parser

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.smallibs.parsec.io.Reader
import org.smallibs.parsec.parser.Response.Reject
import org.smallibs.parsec.utils.Location
import org.smallibs.apacen.parser.SolverParser.constructor
import org.smallibs.apacen.parser.SolverParser.expr
import org.smallibs.apacen.parser.SolverParser.goal
import org.smallibs.apacen.parser.SolverParser.program
import org.smallibs.apacen.parser.SolverParser.rule
import org.smallibs.apacen.parser.SolverParser.term

class SolverParserErrorTest {

    @Nested
    inner class Arguments {
        @Test
        fun `should not parse an isolated number`() {
            // Given
            val parser = expr()

            // When
            val result = parser(Reader.string("(1.23")).fold({ null }, { it })

            // Then
            result shouldBe Reject(Location(5, 1, 6, listOf("expression", "group", ")")), true, null)
        }

        @Test
        fun `should not parse an addition`() {
            // Given
            val parser = expr()

            // When
            val result = parser(Reader.string("1 +")).fold({ null }, { it })

            // Then
            result shouldBe Reject(Location(3, 1, 4, listOf("expression", "expression", "group", "(")), true, null)
        }

        @Test
        fun `should not parse a constructor without parameter`() {
            // Given
            val parser = constructor()

            // When
            val result = parser(Reader.string("test(")).fold({ null }, { it })

            // Then
            result shouldBe Reject(Location(5, 1, 6, listOf("constructor", ")")), true, null)
        }

        @Test
        fun `should parse a constructor argument`() {
            // Given
            val parser = term()

            // When
            val result = parser(Reader.string("test\n(X + Y,)")).fold({ null }, { it })

            // Then
            result shouldBe Reject(Location(12, 2, 8, listOf("term", "expression", "constructor", "term", "constructor", "ident")), true, null)
        }
    }

    @Nested
    inner class Rules {
        @Test
        fun `should not parse a clause`() {
            // Given
            val parser = rule()

            // When
            val result = parser(Reader.string("test(X + Y, 12)")).fold({ null }, { it })

            // Then
            result shouldBe Reject(Location(15, 1, 16, listOf("rule", ".")), true, null)
        }

        @Test
        fun `should not parse a clause with a body`() {
            // Given
            val parser = rule()

            // When
            val result = parser(Reader.string("test(X + Y, 12) :- equals(,Y).")).fold({ null }, { it })

            // Then
            result shouldBe Reject(
                Location(26, 1, 27, listOf("rule", "compound term", "functor", ")")),
                true,
                null
            )
        }

        @Test
        fun `should not parse a simple fact`() {
            // Given
            val parser = goal()

            // When
            val result = parser(Reader.string("?- test(X + Y 12).")).fold({ null }, { it })

            // Then
            result shouldBe Reject(
                Location(14, 1, 15, listOf("fact", "compound term", "functor", ")")),
                true,
                null
            )
        }

        @Test
        fun `should not parse a complex fact`() {
            // Given
            val parser = goal()

            // When
            val result = parser(Reader.string("?- test(X + Y, 12), X = Y).")).fold({ null }, { it })

            // Then
            result shouldBe Reject(
                Location(25, 1, 26, listOf("fact", ".")),
                true,
                null
            )
        }

        @Test
        fun `should not parse a program`() {
            // Given
            val parser = program()

            // When
            val result =
                parser(
                    Reader.string(
                        """
                    equals(X,X). 
                    test(X + Y, 12) :- equals(X,Y)
                    """.trimIndent()
                    )
                ).fold({ null }, { it })

            // Then
            result shouldBe Reject(
                Location(44, 2, 31, listOf("program", "rule", ".")),
                true,
                null
            )
        }
    }
}