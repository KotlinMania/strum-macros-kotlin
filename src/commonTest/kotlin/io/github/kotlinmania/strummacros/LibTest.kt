// port-lint: tests lib.rs
package io.github.kotlinmania.strummacros

import kotlin.test.Test
import kotlin.test.assertEquals

class LibTest {
    @Test
    fun testStrumMacrosVersion() {
        assertEquals("0.26.4", StrumMacros.VERSION)
    }
}
