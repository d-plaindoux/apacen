package types

import org.smallibs.apacen.runtime.Repl
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    try {
        val interpreter = TypesRuntime.interpret()            // Tests code

        val repl = Repl(interpreter)
        val reader = BufferedReader(InputStreamReader(System.`in`))
        System.err.println("Welcome to Nethra type checker engine v0.1")
        var line: String? = reader.readLine()
        while (line != null) {
            val t0 = System.currentTimeMillis()
            if (repl.perform(line)) {
                println("[Done in " + (System.currentTimeMillis() - t0) + " ms]")
            }
            line = reader.readLine()
        }
    } catch (e: Throwable) {
        System.err.println("ABORT: ${e::class.simpleName} ${e.message}")
        throw e
    }
}
