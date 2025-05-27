package org.smallibs.apacen.engine.logic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.smallibs.apacen.data.CompoundTerm.Comparator.EQ
import org.smallibs.apacen.data.CompoundTerm.Comparator.GT
import org.smallibs.apacen.data.CompoundTerm.Comparator.LT
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Logic.And
import org.smallibs.apacen.data.Logic.Or
import org.smallibs.apacen.data.Logic.Single
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.engine.logic.Normalize.normalize

class NormalizeTest {

    @Test
    fun shouldNormalizeSingle() {
        // Given
        val relation = Relation(EQ, NumberLiteral(1.0), NumberLiteral(2.0))
        val logic = Single(relation)

        // When
        val normalized = logic.normalize()

        // Then
        Assertions.assertEquals(listOf(listOf(relation)), normalized)
    }

    @Test
    fun shouldNormalizeOr() {
        // Given
        val relation1 = Relation(EQ, NumberLiteral(1.0), NumberLiteral(2.0))
        val relation2 = Relation(GT, NumberLiteral(3.0), NumberLiteral(4.0))
        val logic = Or(Single(relation1), Single(relation2))

        // When
        val normalized = logic.normalize()

        // Then
        Assertions.assertEquals(listOf(listOf(relation1), listOf(relation2)), normalized)
    }

    @Test
    fun shouldNormalizeAnd() {
        // Given
        val relation1 = Relation(EQ, NumberLiteral(1.0), NumberLiteral(2.0))
        val relation2 = Relation(GT, NumberLiteral(3.0), NumberLiteral(4.0))
        val logic = And(Single(relation1), Single(relation2))

        // When
        val normalized = logic.normalize()

        // Then
        Assertions.assertEquals(listOf(listOf(relation1, relation2)), normalized)
    }

    @Test
    fun shouldNormalizeAndOr() {
        // Given
        val relation1 = Relation(EQ, NumberLiteral(1.0), NumberLiteral(2.0))
        val relation2 = Relation(GT, NumberLiteral(3.0), NumberLiteral(4.0))
        val relation3 = Relation(LT, NumberLiteral(5.0), NumberLiteral(6.0))
        val logic = And(
            Single(relation1), Or(Single(relation2), Single(relation3))
        )

        // When
        val normalized = logic.normalize()

        // Then
        Assertions.assertEquals(listOf(listOf(relation1, relation2), listOf(relation1, relation3)), normalized)
    }

    @Test
    fun shouldNormalizeOrAnd() {
        // Given
        val relation1 = Relation(EQ, NumberLiteral(1.0), NumberLiteral(2.0))
        val relation2 = Relation(GT, NumberLiteral(3.0), NumberLiteral(4.0))
        val relation3 = Relation(LT, NumberLiteral(5.0), NumberLiteral(6.0))
        val logic = Or(
            Single(relation1), And(Single(relation2), Single(relation3))
        )

        // When
        val normalized = logic.normalize()

        // Then
        Assertions.assertEquals(listOf(listOf(relation1), listOf(relation2, relation3)), normalized)
    }
}