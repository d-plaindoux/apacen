package org.smallibs.apacen.runtime

import org.smallibs.apacen.data.Declaration
import org.smallibs.apacen.data.Declaration.Fact
import org.smallibs.apacen.data.Declaration.Rule
import org.smallibs.apacen.parser.SolverParser.program
import org.smallibs.core.IList
import org.smallibs.core.ILists.filter
import org.smallibs.core.ILists.map
import org.smallibs.parsec.io.Reader.Companion.string
import org.smallibs.parsec.parser.Response
import java.io.File

object Loader {
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun load(file: File): Pair<IList<Rule>, IList<Fact>>? =
        load(file.toString(), this::class.java.classLoader.getResource(file.toString()).readText(Charsets.UTF_8))

    fun load(path: String, source: String): Pair<IList<Rule>, IList<Fact>>? {
        val lines = source.lines()
        val program = program()(string(source))

        return when (program) {
            is Response.Accept<Char, IList<Declaration>> -> {
                val rules = program.value.filter { it is Rule }.map { it as Rule }
                val facts = program.value.filter { it is Fact }.map { it as Fact }

                rules to facts
            }

            is Response.Reject<Char> -> {
                reportError(path.toString(), lines, program)

                null
            }
        }
    }

    private fun reportError(path: String, lines: List<String>, program: Response.Reject<Char>) {
        System.err.println("| ${lines.get(program.location.line - 1)}")
        System.err.println("| ${"-".repeat(program.location.column - 1)}^")

        if (program.location.stack.isNotEmpty()) {
            System.err.println("| Syntax error in $path at ${program.location.line}:${program.location.column}: waiting for a ${program.location.stack.last()}")
        } else {
            System.err.println("| Syntax error in $path at ${program.location.line}:${program.location.column}")
        }
    }

}