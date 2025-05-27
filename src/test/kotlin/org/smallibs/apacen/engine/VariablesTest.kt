package org.smallibs.apacen.engine

import org.junit.jupiter.api.Test
import org.smallibs.core.IList
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Kind.DIV
import org.smallibs.apacen.data.Term.Kind.MUL
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.term.Variables.Direction.LEFT
import org.smallibs.apacen.engine.term.Variables.Direction.RIGHT
import org.smallibs.apacen.engine.term.Variables.variables
import kotlin.test.assertEquals

class VariablesTest {

    @Test
    fun shouldFindAVariable() {
        // Given
        val a = BinOp(DIV, BinOp(MUL, NumberLiteral(3.0), Variable("A")), NumberLiteral(75.0))

        // When
        val vars = a.variables()

        // Then
        assertEquals(mapOf(Variable("A") to IList.of(LEFT, RIGHT)), vars)
    }
}
