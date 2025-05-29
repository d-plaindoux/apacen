package org.smallibs.apacen.program

import org.junit.jupiter.api.Test
import org.smallibs.core.IList
import org.smallibs.core.IList.Nil
import org.smallibs.core.ILists.filter
import org.smallibs.core.ILists.flatMap
import org.smallibs.core.ILists.map
import org.smallibs.parsec.io.Reader.Companion.string
import org.smallibs.apacen.data.Declaration.Rule
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Kind.SUB
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.Solver
import org.smallibs.apacen.engine.term.normalize
import org.smallibs.apacen.parser.SolverParser.goal
import org.smallibs.apacen.parser.SolverParser.program
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EquationSystemTest {

    private val program = """
    equals(X,Y) :- X = Y.   
    test0(X)    :- equals(10 - X, 6 + X).
    test1(X,Y)  :- equals(2, Y), equals(X + Y, 8).
    test2(X,Y)  :- equals(10, Y + X).
    """

    private val fact0 = "?- test0(X)."
    private val fact1 = "?- test1(X,Y)."
    private val fact2 = "?- test2(X,Y)."

    @Test
    fun `should compute the fact0`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact0)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
    }

    @Test
    fun `should compute the fact1`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact1)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
        assertEquals(NumberLiteral(6.0), Variable("X").normalize(result.environment))
        assertEquals(NumberLiteral(2.0), Variable("Y").normalize(result.environment))
    }

    @Test
    fun `should compute the fact2`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact2)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
        assertEquals(BinOp(SUB, NumberLiteral(10.0), Variable("X")), Variable("Y").normalize(result.environment))
    }
}