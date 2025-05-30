package org.smallibs.apacen.engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.smallibs.core.IList
import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.BinOpKind.ADD
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.declaration.Unification
import org.smallibs.apacen.engine.term.Unification.unify
import kotlin.test.assertNull

class UnificationTest {

    @Nested
    inner class Numbers {
        @Test
        fun `should unify two similar numbers`() {
            // Given
            val a1 = NumberLiteral(1.0)
            val a2 = NumberLiteral(1.0)

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertNotNull(environment)
        }

        @Test
        fun `should unify two similar numbers after left reduction`() {
            // Given
            val a1 = BinOp(ADD, NumberLiteral(1.0), NumberLiteral(2.0))
            val a2 = NumberLiteral(3.0)

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertNotNull(environment)
        }

        @Test
        fun `should unify two similar numbers after right reduction`() {
            // Given
            val a1 = NumberLiteral(3.0)
            val a2 = BinOp(ADD, NumberLiteral(1.0), NumberLiteral(2.0))

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertNotNull(environment)
        }
    }

    @Nested
    inner class Variables {
        @Test
        fun `should unify left variable`() {
            // Given
            val a1 = Variable("X")
            val a2 = NumberLiteral(3.0)

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment(Variable("X") to a2))
        }

        @Test
        fun `should unify right variable`() {
            // Given
            val a1 = NumberLiteral(3.0)
            val a2 = Variable("X")

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment(Variable("X") to a1))
        }

        @Test
        fun `should not unify variable to itself`() {
            // Given
            val a1 = Variable("X")
            val a2 = Variable("X")

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment())
        }

        @Test
        fun `should not unify variable to itself after left reduction`() {
            // Given
            val a1 = Variable("X")
            val a2 = Variable("Y")

            // When
            val environment = unify(Environment(Variable("X") to Variable("Y")), a1, a2)

            // Then
            assertEquals(environment, Environment(Variable("X") to Variable("Y")))
        }

        @Test
        fun `should not unify variable to itself after right reduction`() {
            // Given
            val a1 = Variable("Y")
            val a2 = Variable("X")

            // When
            val environment = unify(Environment(Variable("X") to Variable("Y")), a1, a2)

            // Then
            assertEquals(environment, Environment(Variable("X") to Variable("Y")))
        }
    }

    @Nested
    inner class Constructors {
        @Test
        fun `should unify same constructors without arguments`() {
            // Given
            val a1 = Constructor("c", IList.of())
            val a2 = Constructor("c", IList.of())

            // When
            val environment = Unification.unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment())
        }

        @Test
        fun `should unify different constructors without arguments`() {
            // Given
            val a1 = Constructor("c1", IList.of())
            val a2 = Constructor("c2", IList.of())

            // When
            val environment = Unification.unify(Environment(), a1, a2)

            // Then
            assertNull(environment)
        }

        @Test
        fun `should unify same constructors with same number argument`() {
            // Given
            val a1 = Constructor("c", IList.of(NumberLiteral(1.0)))
            val a2 = Constructor("c", IList.of(NumberLiteral(1.0)))

            // When
            val environment = Unification.unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment())
        }

        @Test
        fun `should not unify same constructors with different number argument`() {
            // Given
            val a1 = Constructor("c", IList.of(NumberLiteral(1.0)))
            val a2 = Constructor("c", IList.of(NumberLiteral(2.0)))

            // When
            val environment = Unification.unify(Environment(), a1, a2)

            // Then
            assertNull(environment)
        }

        @Test
        fun `should unify same constructors with left variable and right number arguments`() {
            // Given
            val a1 = Constructor("c", IList.of(Variable("X")))
            val a2 = Constructor("c", IList.of(NumberLiteral(1.0)))

            // When
            val environment = Unification.unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment(Variable("X") to NumberLiteral(1.0)))
        }

        @Test
        fun `should unify same constructors with left number and right variable arguments`() {
            // Given
            val a1 = Constructor("c", IList.of(NumberLiteral(1.0)))
            val a2 = Constructor("c", IList.of(Variable("X")))

            // When
            val environment = Unification.unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment(Variable("X") to NumberLiteral(1.0)))
        }
    }

    @Nested
    inner class BinOps {
        @Test
        fun `should unify same binop`() {
            // Given
            val a1 = BinOp(ADD, NumberLiteral(1.0), NumberLiteral(2.0))
            val a2 = BinOp(ADD, NumberLiteral(1.0), NumberLiteral(2.0))

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment())
        }

        @Test
        fun `should unify binop with same variable in each argument`() {
            // Given
            val a1 = BinOp(ADD, Variable("X"), NumberLiteral(1.0))
            val a2 = BinOp(ADD, NumberLiteral(1.0), Variable("X"))

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertEquals(
                Environment().addSubstitution(Variable("X"), NumberLiteral(1.0)),
                environment
            )
        }

        @Test
        fun `should unify binop with same variable in same argument`() {
            // Given
            val a1 = BinOp(ADD, Variable("X"), NumberLiteral(1.0))
            val a2 = BinOp(ADD, Variable("X"), NumberLiteral(1.0))

            // When
            val environment = unify(Environment(), a1, a2)

            // Then
            assertEquals(environment, Environment())
        }
    }
}