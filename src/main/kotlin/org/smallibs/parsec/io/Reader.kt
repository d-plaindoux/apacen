package org.smallibs.parsec.io

import org.smallibs.parsec.utils.Location
import java.net.URL

interface Reader<out A> {
    fun pushTrace(s: String): Reader<A>
    fun popTrace(): Reader<A>
    fun location(): Location
    fun read(): Pair<A, Reader<A>>?

    fun read(length: Int): Pair<List<A>, Reader<A>>? =
        if (length <= 0) {
            emptyList<A>() to this
        } else {
            read()?.let { p1 -> p1.second.read(length - 1)?.let { p2 -> listOf(p1.first) + p2.first to p2.second } }
        }

    private class FromList<A>(
        private val source: List<A>,
        private val location: Location,
        private val locate: (A, Location) -> Location,
    ) : Reader<A> {
        override fun pushTrace(trace: String): Reader<A> = FromList(source, location.push(trace), locate)
        override fun popTrace(): Reader<A> = FromList(source, location.pop(), locate)
        override fun location() = location
        override fun read() =
            source.getOrNull(location.position)?.let {
                it to FromList(source, locate(it, location.nextPosition()), locate)
            }
    }

    // Companion object
    companion object {
        private fun locate(c: Char, location: Location): Location =
            if (c == '\n') {
                Location(location.position, location.line + 1, 1, location.stack)
            } else {
                Location(location.position, location.line, location.column + 1, location.stack)
            }

        fun string(s: String): Reader<Char> = FromList(s.toList(), Location.create()) { c, l ->
            locate(c, l)
        }

        fun url(s: URL): Reader<Char> = string(s.readText())
    }

}
