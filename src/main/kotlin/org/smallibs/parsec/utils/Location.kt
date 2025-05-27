package org.smallibs.parsec.utils

data class Location(val position: Int, val line: Int, val column: Int, val stack: List<String>) {

    fun nextPosition() = Location(position + 1, line, column, stack)
    fun push(trace: String) = Location(position, line, column, stack + trace)
    fun pop() = Location(position, line, column, stack.dropLast(1))

    companion object {
        fun create(): Location = Location(0, 1, 1, emptyList())
    }

}

