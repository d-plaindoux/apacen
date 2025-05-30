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
            .load(File("tests/types/subst-tests.pl"))
            .load(File("tests/types/beta-tests.pl"))
            .load(File("tests/types/type_tests.pl"))
    }

}