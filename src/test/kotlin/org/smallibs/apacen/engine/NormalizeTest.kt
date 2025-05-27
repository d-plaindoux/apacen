package org.smallibs.apacen.engine

import org.junit.jupiter.api.Test
import org.smallibs.apacen.data.Environment
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Kind.DIV
import org.smallibs.apacen.data.Term.Kind.MUL
import org.smallibs.apacen.data.Term.Kind.SUB
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.engine.term.normalize
import kotlin.test.assertEquals

class NormalizeTest {

    @Test
    fun shouldReduceAnAddition() {
        // Given
        val term = BinOp(MUL, NumberLiteral(2.0), NumberLiteral(3.0))

        // When
        val normalized = term.normalize(Environment())

        // Then
        assertEquals(NumberLiteral(6.0), normalized)
    }

    @Test
    fun shouldReduceASubtraction() {
        // Given
        val term = BinOp(SUB, NumberLiteral(2.0), NumberLiteral(3.0))

        // When
        val normalized = term.normalize(Environment())

        // Then
        assertEquals(NumberLiteral(-1.0), normalized)
    }

    @Test
    fun shouldReduceAMultiplication() {
        // Given
        val term = BinOp(MUL, NumberLiteral(2.0), NumberLiteral(3.0))

        // When
        val normalized = term.normalize(Environment())

        // Then
        assertEquals(NumberLiteral(6.0), normalized)
    }

    @Test
    fun shouldReduceADivide() {
        // Given
        val term = BinOp(DIV, NumberLiteral(4.0), NumberLiteral(2.0))

        // When
        val normalized = term.normalize(Environment())

        // Then
        assertEquals(NumberLiteral(2.0), normalized)
    }

}