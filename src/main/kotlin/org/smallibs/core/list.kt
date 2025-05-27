package org.smallibs.core

import org.smallibs.core.IList.Cons
import org.smallibs.core.IList.Empty
import org.smallibs.core.ILists.foldLeft
import org.smallibs.core.ILists.foldRight
import org.smallibs.core.ILists.reverse

sealed interface IList<out E> {

    fun asString(sep: String = "::", asString: E.() -> String, printEmpty: Boolean = true): String

    data object Empty : IList<Nothing> {
        override fun toString(): String = "nil"

        override fun asString(sep: String, asString: Nothing.() -> String, printEmpty: Boolean): String =
            if (printEmpty) {
                "nil"
            } else {
                ""
            }
    }

    data class Cons<E>(val head: E, val tail: IList<E>) : IList<E> {

        override fun toString(): String =
            this.reverse().foldRight({ e, l -> "$e::$l" }, "nil")

        override fun asString(sep: String, asString: E.() -> String, printEmpty: Boolean): String =
            if (!printEmpty && tail == Empty) {
                head.asString()
            } else {
                "${head.asString()}$sep${tail.asString(sep, asString, printEmpty)}"
            }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cons<*>

            var left: IList<E> = this
            var right: IList<*> = other

            while (true) {
                if (left is Cons<E> && right is Cons<*> && left.head == right.head) {
                    left = left.tail
                    right = right.tail
                } else {
                    return left == Empty && right == Empty;
                }
            }
        }

        override fun hashCode(): Int {
            var result = head?.hashCode() ?: 0
            var current = this
            while (current.isNotEmpty()) {
                when (current) {

                }
            }
            result = 31 * result + tail.hashCode()
            return result
        }
    }

    fun isEmpty(): Boolean = this == Empty

    fun isNotEmpty(): Boolean = !isEmpty()

    val size: Int
        get() = this.foldLeft({ s, _ -> s + 1 }, 0)

    companion object {
        fun <A> of(vararg elements: A): IList<A> =
            if (elements.isEmpty()) {
                Empty
            } else {
                Cons(elements[0], of(*elements.copyOfRange(1, elements.size)))
            }

        fun <A> from(l: Collection<A>): IList<A> {
            var r: IList<A> = Empty
            for (a in l.reversed()) {
                r = Cons(a, r)
            }
            return r
        }
    }
}

object ILists {
    fun <A> IList<A>.append(l2: IList<A>): IList<A> =
        l2.foldLeft<A, IList<A>>({ l, e -> Cons(e, l) }, this.reverse()).reverse()

    fun <A> IList<A>.reverse(): IList<A> =
        this.foldLeft<A, IList<A>>({ l, e -> Cons(e, l) }, Empty)

    tailrec fun <E, A> IList<E>.foldLeft(function: (A, E) -> A, init: A): A =
        when (this) {
            Empty -> init
            is Cons<E> -> this.tail.foldLeft(function, function(init, this.head))
        }

    fun <E, A> IList<E>.foldRight(function: (E, A) -> A, init: A): A =
        this.reverse().foldLeft({ e, s -> function(s, e) }, init)

    fun <E, A> IList<E>.map(function: (E) -> A): IList<A> =
        this.foldLeft<E, IList<A>>({ l, e -> Cons(function(e), l) }, Empty).reverse()

    fun <E, A> IList<E>.flatMap(function: (E) -> IList<A>): IList<A> =
        this.foldLeft<E, IList<A>>({ l, e -> function(e).append(l) }, Empty)

    infix fun <E> IList<E>.filter(function: (E) -> Boolean): IList<E> {
        tailrec fun <E> filterTailRec(l: IList<E>, function: (E) -> Boolean, acc: IList<E>): IList<E> =
            when (l) {
                is Cons<E> ->
                    if (function(l.head))
                        filterTailRec(l.tail, function, Cons(l.head, acc))
                    else
                        filterTailRec(l.tail, function, acc)

                is Empty -> acc
            }

        return filterTailRec(this, function, Empty)
    }
}