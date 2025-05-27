package org.smallibs.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.smallibs.core.ILists.append
import org.smallibs.core.ILists.reverse

class IListsTest {

    @Test
    fun shouldReverse() {
        // Given
        val l = IList.of(1, 2, 3, 4, 5)

        // When
        val r = l.reverse()

        // Then
        assertEquals(IList.of(5, 4, 3, 2, 1), r)
    }

    @Test
    fun shouldReverseBigList() {
        // Given
        val l = IList.from(List(10_000) { e -> e })

        // When
        val r = l.reverse()

        // Then
        assertEquals(IList.from(List(10_000) { e -> e }.reversed()), r)
    }

    @Test
    fun shouldAppendLists() {
        val l1 = IList.of(1, 2, 3, 4, 5)
        val l2 = IList.of(6, 7, 8, 9, 10)

        // When
        val r = l1.append(l2)

        // Then
        assertEquals(IList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), r)
    }

    @Test
    fun shouldAppendBigLists() {
        val l1 = IList.from(List(10_000) { e -> e })
        val l2 = IList.from(List(10_000) { e -> e + 10_000 })

        // When
        val r = l1.append(l2)

        // Then
        assertEquals(IList.from(List(20_000) { e -> e }), r)
    }
}