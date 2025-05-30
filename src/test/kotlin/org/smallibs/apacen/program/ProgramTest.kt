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
import org.smallibs.apacen.data.Term.BinOpKind.ADD
import org.smallibs.apacen.data.Term.BinOpKind.DIV
import org.smallibs.apacen.data.Term.BinOpKind.MUL
import org.smallibs.apacen.data.Term.BinOpKind.SUB
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.apacen.engine.Solver
import org.smallibs.apacen.engine.term.normalize
import org.smallibs.apacen.parser.SolverParser.goal
import org.smallibs.apacen.parser.SolverParser.program
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProgramTest {

    private val program = """
    -- Ptz eligibility
    ptzEligibility(esthetical(E), price(P), renovation(R)) :-
        (E = ((25 * P) - (100 * R)) / 75).
    """

    private val fact1 = "?- ptzEligibility(esthetical(E), price(200000), renovation(20000))."
    private val fact2 = "?- ptzEligibility(esthetical(40000), price(P), renovation(20000))."
    private val fact3 = "?- ptzEligibility(esthetical(40000), price(200000), renovation(R))."
    private val fact4 = "?- ptzEligibility(esthetical(50000 + A), price(200000), renovation(20000))."
    private val fact5 = "?- ptzEligibility(esthetical(30000 + A), price(200000 + B), renovation(20000))."

    @Test
    fun `should compute the esthetical`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact1)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
        assertEquals(NumberLiteral(40000.0), Variable("E").normalize(result.environment))
    }

    @Test
    fun `should compute the price`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact2)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
        assertEquals(NumberLiteral(200000.0), Variable("P").normalize(result.environment))
    }

    @Test
    fun `should compute the renovation`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact3)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
        assertEquals(NumberLiteral(20000.0), Variable("R").normalize(result.environment))
    }

    @Test
    fun `should compute the renovation adaptation`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact4)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
        assertEquals(NumberLiteral(-10000.0), Variable("A").normalize(result.environment))
    }

    @Test
    fun `should compute the renovation adaptation with two adjustments`() {
        // Given
        val system =
            program()(string(program)).fold({ it.value }, { Nil }).filter { it is Rule }.map { it as Rule }
        val facts = goal()(string(fact5)).fold({ IList.of(it.value) }, { Nil })

        // When
        val result = Solver(system).solve(facts.flatMap { it.value })

        // Then
        assertNotNull(result)
        assertEquals(
            Variable("A").normalize(result.environment).asString(), BinOp(
                kind = SUB, lhd = BinOp(
                    kind = DIV, lhd = BinOp(
                        kind = SUB, lhd = BinOp(
                            kind = MUL, lhd = NumberLiteral(value = 25.0), rhd = BinOp(
                                kind = ADD,
                                lhd = NumberLiteral(value = 200000.0),
                                rhd = Variable(name = "B")
                            )
                        ), rhd = NumberLiteral(value = 2000000.0)
                    ), rhd = NumberLiteral(value = 75.0)
                ), rhd = NumberLiteral(value = 30000.0)
            ).asString()
        )
    }
}