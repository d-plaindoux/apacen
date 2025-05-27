package org.smallibs.parsec.io

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CharReaderTest : StringSpec({
    "should Return Something" {
        Reader.string("a").read()?.first shouldBe 'a'
    }

    "should Have Next Fails" {
        Reader.string("").read() shouldBe null
    }

    "should Have A Reader Returning Something" {
        Reader.string("ab").read()?.second?.read()?.first shouldBe 'b'
    }
})