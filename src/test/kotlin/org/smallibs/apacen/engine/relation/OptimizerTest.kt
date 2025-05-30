package org.smallibs.apacen.engine.relation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.smallibs.apacen.data.CompoundTerm.Comparator.GTE
import org.smallibs.apacen.data.CompoundTerm.Comparator.LTE
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.BinOpKind.MIN
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.relation.Optimizer.optimize

class OptimizerTest {

    @Test
    fun shouldOptimizeTwoInequationsLTETwice() {
        // Given
        val system = listOf(
            Relation(LTE, Variable("X"), NumberLiteral(5.0)),
            Relation(LTE, Variable("X"), NumberLiteral(10.0))
        )

        // When
        val newSystem = system.optimize()

        // Then
        Assertions.assertEquals(
            listOf(
                Relation(
                    LTE, Variable("X"), BinOp(MIN, NumberLiteral(10.0), NumberLiteral(5.0))
                )
            ),
            newSystem
        )
    }

    @Test
    fun shouldOptimizeTwoInequationsLTEGTE() {
        // Given
        val system = listOf(
            Relation(LTE, Variable("X"), NumberLiteral(5.0)),
            Relation(GTE, NumberLiteral(10.0), Variable("X"))
        )

        // When
        val newSystem = system.optimize()

        // Then
        Assertions.assertEquals(
            listOf(
                Relation(
                    LTE, Variable("X"), BinOp(MIN, NumberLiteral(10.0), NumberLiteral(5.0))
                )
            ),
            newSystem
        )
    }

    @Test
    fun shouldOptimizeTwoInequationsGTELTE() {
        // Given
        val system = listOf(
            Relation(GTE, NumberLiteral(10.0), Variable("X")),
            Relation(LTE, Variable("X"), NumberLiteral(5.0))
        )

        // When
        val newSystem = system.optimize()

        // Then
        Assertions.assertEquals(
            listOf(
                Relation(
                    LTE, Variable("X"), BinOp(MIN, NumberLiteral(5.0), NumberLiteral(10.0))
                )
            ),
            newSystem
        )
    }

    @Test
    fun shouldOptimizeTwoInequationsGTEGTE() {
        // Given
        val system = listOf(
            Relation(GTE, NumberLiteral(10.0), Variable("X")),
            Relation(GTE, NumberLiteral(5.0), Variable("X"))
        )

        // When
        val newSystem = system.optimize()

        // Then
        Assertions.assertEquals(
            listOf(
                Relation(
                    LTE, Variable("X"), BinOp(MIN, NumberLiteral(5.0), NumberLiteral(10.0))
                )
            ),
            newSystem
        )
    }
}