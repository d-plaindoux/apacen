package org.smallibs.apacen.engine.relation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.smallibs.core.IList
import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.engine.term.Variables
import org.smallibs.apacen.engine.relation.Transformer.transform

class OrderRelationTest {

    @Test
    fun shouldTransformRightAddition() {
        // Given
        val path = IList.Companion.of(Variables.Direction.RIGHT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.ADD, Term.NumberLiteral(3.0), Term.Variable("A"))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE,
                Term.BinOp(Term.BinOpKind.SUB, Term.NumberLiteral(1.0), Term.NumberLiteral(3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformLeftAddition() {
        // Given
        val path = IList.Companion.of(Variables.Direction.LEFT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.ADD, Term.Variable("A"), Term.NumberLiteral(3.0))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE,
                Term.BinOp(Term.BinOpKind.SUB, Term.NumberLiteral(1.0), Term.NumberLiteral(3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformRightSubtraction() {
        // Given
        val path = IList.Companion.of(Variables.Direction.RIGHT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.SUB, Term.NumberLiteral(3.0), Term.Variable("A"))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE.reverse(),
                Term.BinOp(Term.BinOpKind.SUB, Term.NumberLiteral(3.0), Term.NumberLiteral(1.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformLeftSubtraction() {
        // Given
        val path = IList.Companion.of(Variables.Direction.LEFT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.SUB, Term.Variable("A"), Term.NumberLiteral(3.0))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE,
                Term.BinOp(Term.BinOpKind.ADD, Term.NumberLiteral(1.0), Term.NumberLiteral(3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformRightMultiply() {
        // Given
        val path = IList.Companion.of(Variables.Direction.RIGHT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.MUL, Term.NumberLiteral(3.0), Term.Variable("A"))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE,
                Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(1.0), Term.NumberLiteral(3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformRightMultiplyWithNegativeNumber() {
        // Given
        val path = IList.Companion.of(Variables.Direction.RIGHT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.MUL, Term.NumberLiteral(-3.0), Term.Variable("A"))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE.reverse(),
                Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(1.0), Term.NumberLiteral(-3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformLeftMultiply() {
        // Given
        val path = IList.Companion.of(Variables.Direction.LEFT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.MUL, Term.Variable("A"), Term.NumberLiteral(3.0))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE,
                Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(1.0), Term.NumberLiteral(3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformLeftMultiplyWithNegativeNumber() {
        // Given
        val path = IList.Companion.of(Variables.Direction.LEFT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.MUL, Term.Variable("A"), Term.NumberLiteral(-3.0))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE.reverse(),
                Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(1.0), Term.NumberLiteral(-3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformRightDivide() {
        // Given
        val path = IList.Companion.of(Variables.Direction.RIGHT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(3.0), Term.Variable("A"))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE,
                Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(3.0), Term.NumberLiteral(1.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformRightDivideWithNegativeNumber() {
        // Given
        val path = IList.Companion.of(Variables.Direction.RIGHT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(-3.0), Term.Variable("A"))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE.reverse(),
                Term.BinOp(Term.BinOpKind.DIV, Term.NumberLiteral(-3.0), Term.NumberLiteral(1.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformLeftDivide() {
        // Given
        val path = IList.Companion.of(Variables.Direction.LEFT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.DIV, Term.Variable("A"), Term.NumberLiteral(3.0))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.EQ, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.EQ,
                Term.BinOp(Term.BinOpKind.MUL, Term.NumberLiteral(1.0), Term.NumberLiteral(3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformLeftDivideWithNegativeNumber() {
        // Given
        val path = IList.Companion.of(Variables.Direction.LEFT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(Term.BinOpKind.DIV, Term.Variable("A"), Term.NumberLiteral(-3.0))

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.LTE, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.LTE.reverse(),
                Term.BinOp(Term.BinOpKind.MUL, Term.NumberLiteral(1.0), Term.NumberLiteral(-3.0)),
                Term.Variable("A")
            ),
            equation
        )
    }

    @Test
    fun shouldTransformEquation() {
        // Given
        val path = IList.Companion.of(Variables.Direction.LEFT, Variables.Direction.RIGHT)
        val lhd = Term.NumberLiteral(1.0)
        val rhd = Term.BinOp(
            Term.BinOpKind.DIV,
            Term.BinOp(Term.BinOpKind.MUL, Term.NumberLiteral(3.0), Term.Variable("A")),
            Term.NumberLiteral(75.0)
        )

        // When
        val equation = CompoundTerm.Relation(CompoundTerm.Comparator.EQ, lhd, rhd).transform(path)

        // Then
        Assertions.assertEquals(
            CompoundTerm.Relation(
                CompoundTerm.Comparator.EQ,
                Term.BinOp(
                    Term.BinOpKind.DIV,
                    Term.BinOp(Term.BinOpKind.MUL, Term.NumberLiteral(value = 1.0), Term.NumberLiteral(value = 75.0)),
                    Term.NumberLiteral(value = 3.0)
                ),
                Term.Variable("A")
            ),
            equation
        )
    }
}