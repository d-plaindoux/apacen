package org.smallibs.apacen.parser

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.smallibs.core.IList
import org.smallibs.core.IList.Empty
import org.smallibs.parsec.io.Reader
import org.smallibs.apacen.data.CompoundTerm.Comparator.EQ
import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Declaration.Fact
import org.smallibs.apacen.data.Declaration.Rule
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.Kind.ADD
import org.smallibs.apacen.data.Term.Kind.SUB
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.parser.SolverParser.constructor
import org.smallibs.apacen.parser.SolverParser.expr
import org.smallibs.apacen.parser.SolverParser.goal
import org.smallibs.apacen.parser.SolverParser.number
import org.smallibs.apacen.parser.SolverParser.program
import org.smallibs.apacen.parser.SolverParser.rule
import org.smallibs.apacen.parser.SolverParser.term
import org.smallibs.apacen.parser.SolverParser.variable

class SolverParserNominalTest {

    @Nested
    inner class Arguments {
        @Test
        fun `should parse a variable`() {
            // Given
            val parser = variable()

            // When
            val result = parser(Reader.string("Test")).fold({ it.value }, { null })

            // Then
            result shouldBe Variable("Test")
        }

        @Test
        fun `should parse a number (integer)`() {
            // Given
            val parser = number()

            // When
            val result = parser(Reader.string("1")).fold({ it.value }, { null })

            // Then
            result shouldBe NumberLiteral(1.0)
        }

        @Test
        fun `should parse a number`() {
            // Given
            val parser = number()

            // When
            val result = parser(Reader.string("1.23")).fold({ it.value }, { null })

            // Then
            result shouldBe NumberLiteral(1.23)
        }

        @Test
        fun `should parse an explicit positive number`() {
            // Given
            val parser = number()

            // When
            val result = parser(Reader.string("+1.23")).fold({ it.value }, { null })

            // Then
            result shouldBe NumberLiteral(1.23)
        }

        @Test
        fun `should parse a negative number`() {
            // Given
            val parser = number()

            // When
            val result = parser(Reader.string("-1.23")).fold({ it.value }, { null })

            // Then
            result shouldBe NumberLiteral(-1.23)
        }

        @Test
        fun `should parse an isolated number`() {
            // Given
            val parser = expr()

            // When
            val result = parser(Reader.string("(1.23)")).fold({ it.value }, { null })

            // Then
            result shouldBe NumberLiteral(1.23)
        }

        @Test
        fun `should parse an addition`() {
            // Given
            val parser = expr()

            // When
            val result = parser(Reader.string("1 + 3.2")).fold({ it.value }, { null })

            // Then
            result shouldBe BinOp(ADD, NumberLiteral(1.0), NumberLiteral(3.2))
        }

        @Test
        fun `should parse a subtraction with variables`() {
            // Given
            val parser = expr()

            // When
            val result = parser(Reader.string("X - Y")).fold({ it.value }, { null })

            // Then
            result shouldBe BinOp(SUB, Variable("X"), Variable("Y"))
        }

        @Test
        fun `should parse a subtraction and an addition`() {
            // Given
            val parser = expr()

            // When
            val result = parser(Reader.string("X - (Y + Z)")).fold({ it.value }, { null })

            // Then
            result shouldBe BinOp(SUB, Variable("X"), BinOp(ADD, Variable("Y"), Variable("Z")))
        }

        @Test
        fun `should parse an addition and a subtraction`() {
            // Given
            val parser = expr()

            // When
            val result = parser(Reader.string("(X + Y) - Z)")).fold({ it.value }, { null })

            // Then
            result shouldBe BinOp(SUB, BinOp(ADD, Variable("X"), Variable("Y")), Variable("Z"))
        }

        @Test
        fun `should parse a constructor without parameter`() {
            // Given
            val parser = constructor()

            // When
            val result = parser(Reader.string("test()")).fold({ it.value }, { null })

            // Then
            result shouldBe Constructor("test", Empty)
        }

        @Test
        fun `should parse a constructor argument`() {
            // Given
            val parser = term()

            // When
            val result = parser(Reader.string("test(X + Y, 12)")).fold({ it.value }, { null })

            // Then
            result shouldBe Constructor("test", IList.of(BinOp(ADD, Variable("X"), Variable("Y")), NumberLiteral(12.0)))
        }
    }

    @Nested
    inner class Rules {
        @Test
        fun `should parse a clause`() {
            // Given
            val parser = rule()

            // When
            val result = parser(Reader.string("test(X + Y, 12).")).fold({ it.value }, { null })

            // Then
            result shouldBe Rule(
                Functor("test", IList.of(BinOp(ADD, Variable("X"), Variable("Y")), NumberLiteral(12.0))),
                Empty
            )
        }

        @Test
        fun `should parse a clause with a body`() {
            // Given
            val parser = rule()

            // When
            val result = parser(Reader.string("test(X + Y, 12) :- equals(X,Y).")).fold({ it.value }, { null })

            // Then
            result shouldBe Rule(
                Functor("test", IList.of(BinOp(ADD, Variable("X"), Variable("Y")), NumberLiteral(12.0))),
                IList.of(Functor("equals", IList.of(Variable("X"), Variable("Y"))))
            )
        }

        @Test
        fun `should parse a simple fact`() {
            // Given
            val parser = goal()

            // When
            val result = parser(Reader.string("?- test(X + Y, 12).")).fold({ it.value }, { null })

            // Then
            result shouldBe Fact(
                IList.of(Functor("test", IList.of(BinOp(ADD, Variable("X"), Variable("Y")), NumberLiteral(12.0))))
            )
        }

        @Test
        fun `should parse a complex fact`() {
            // Given
            val parser = goal()

            // When
            val result = parser(Reader.string("?- test(X + Y, 12), (X = Y).")).fold({ it.value }, { null })

            // Then
            result shouldBe Fact(
                IList.of(
                    Functor("test", IList.of(BinOp(ADD, Variable("X"), Variable("Y")), NumberLiteral(12.0))),
                    Relation(EQ, Variable("X"), Variable("Y")),
                )
            )
        }

        @Test
        fun `should parse a program`() {
            // Given
            val parser = program()

            // When
            val result =
                parser(
                    Reader.string(
                        """
                    equals(X,X). 
                    test(X + Y, 12) :- equals(X,Y).
                    """
                    )
                ).fold({ it.value }, { null })

            // Then
            result shouldBe IList.of(
                Rule(
                    Functor("equals", IList.of(Variable("X"), Variable("X"))),
                    IList.of()
                ),
                Rule(
                    Functor("test", IList.of(BinOp(ADD, Variable("X"), Variable("Y")), NumberLiteral(12.0))),
                    IList.of(Functor("equals", IList.of(Variable("X"), Variable("Y"))))
                )
            )
        }
    }
}