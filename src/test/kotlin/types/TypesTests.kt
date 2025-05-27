package types

import java.io.File
import kotlin.test.Test

class TypesTests {

    @Test
    fun doTests() {
        TypesRuntime.interpret()
            // Load test library
            .load(File("test/assert.pl"))
            // Load and perform tests
            .load(File("types/tests/subst-tests.pl"))
            .load(File("types/tests/beta-tests.pl"))
            .load(File("types/tests/type_tests.pl"))
    }

}