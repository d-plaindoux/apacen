package org.smallibs.apacen.engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.smallibs.core.IList
import org.smallibs.apacen.data.Generator
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.BinOpKind.MUL
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.term.Fresh.fresh

class FreshTest {

    @Nested
    inner class Rules {

        @Test
        fun `should return term with number`() {
            // Given
            val a = Constructor("t", IList.of(NumberLiteral(1.0)))

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(Constructor("t", IList.of(NumberLiteral(1.0))), r)
        }

        @Test
        fun `should return term with fresh variable`() {
            // Given
            val a = Constructor("t", IList.of(Variable("X")))

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(
                Constructor("t", IList.of(Variable("X", 1))), r
            )
        }

        @Test
        fun `should return constructors with fresh variables`() {
            // Given
            val a = Constructor(
                "t",
                IList.of(Constructor("equals", IList.of(Variable("X"), Variable("X"))))
            )

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(
                Constructor(
                    "t",
                    IList.of(Constructor("equals", IList.of(Variable("X", 1), Variable("X", 1))))
                ), r
            )
        }

        @Test
        fun `should return binop with fresh variables`() {
            // Given
            val a = Constructor("t", IList.of(BinOp(MUL, Variable("X"), Variable("X"))))

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(
                Constructor("t", IList.of(BinOp(MUL, Variable("X", 1), Variable("X", 1)))), r
            )
        }
    }

    @Nested
    inner class Arguments {

        @Test
        fun `should return number`() {
            // Given
            val a = NumberLiteral(1.0)

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(NumberLiteral(1.0), r)
        }

        @Test
        fun `should return fresh variable`() {
            // Given
            val a = Variable("X")

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(Variable("X", 1), r)
        }

        @Test
        fun `should return constructors with fresh variables`() {
            // Given
            val a = Constructor("equals", IList.of(Variable("X"), Variable("X")))

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(
                Constructor("equals", IList.of(Variable("X", 1), Variable("X", 1))),
                r
            )
        }

        @Test
        fun `should return binop with fresh variables`() {
            // Given
            val a = BinOp(MUL, Variable("X"), Variable("X"))

            // When
            val r = a.fresh(Generator().nextGeneration())

            // Then
            assertEquals(
                BinOp(MUL, Variable(name = "X", generation = 1), Variable(name = "X", generation = 1)),
                r
            )
        }
    }
}