package types

import org.smallibs.apacen.runtime.Interpreter
import java.io.File

object TypesRuntime {
    fun interpret(): Interpreter =
        Interpreter()
            // Core code
            .load(File("kernel/core.pl"))
            .load(File("kernel/logic.pl"))
            .load(File("kernel/number.pl"))
            .load(File("kernel/list.pl"))
            // Specific code
            .load(File("types/norm.pl"))
            .load(File("types/subst.pl"))
            .load(File("types/beta.pl"))
            .load(File("types/system.pl"))
            .load(File("types/proof.pl"))
            .load(File("types/program.pl"))
}