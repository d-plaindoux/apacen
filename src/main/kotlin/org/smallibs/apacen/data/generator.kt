package org.smallibs.apacen.data

data class Generator(private var counter: Int = 0) {
    fun nextGeneration(): Int {
        counter += 1
        return counter
    }

    fun currentGeneration(): Int = counter
}