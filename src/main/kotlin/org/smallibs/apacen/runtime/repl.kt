package org.smallibs.apacen.runtime

class Repl(var interpreter: Interpreter) {
    private var bufferLines: List<String> = emptyList()

    fun perform(line: String): Boolean {
        this.bufferLines = this.bufferLines + line

        if (!line.trim().endsWith(".")) {
            return false
        }

        Loader.load("stdin", bufferLines.joinToString("\n"))?.let {
            interpreter.process(it)
        }?.let { interpreter ->
            this.interpreter = interpreter
        }

        this.bufferLines = emptyList()

        return true
    }
}
