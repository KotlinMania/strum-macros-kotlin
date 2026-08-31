// port-lint: tests strum_macros/src/helpers/mod.rs
package io.github.kotlinmania.strummacros

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.strummacros.helpers.missingParseErrAttrError
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.nonSingleFieldVariantError
import io.github.kotlinmania.strummacros.helpers.nonUnitVariantError
import io.github.kotlinmania.strummacros.helpers.strumDiscriminantsPassthroughError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HelpersTest {
    @Test
    fun testErrors() {
        val err1 = missingParseErrAttrError()
        assertEquals("`parse_err_ty` and `parse_err_fn` attributes are both required.", err1.message)

        val err2 = nonEnumError()
        assertEquals("This macro only supports enums.", err2.message)

        val err3 = nonUnitVariantError()
        assertEquals("This macro only supports enums of strictly unit variants. Consider using it in conjunction with EnumDiscriminants", err3.message)

        val err4 = nonSingleFieldVariantError("test_attr")
        assertEquals("The [test_attr] attribute only supports enum variants with a single field", err4.message)

        val err5 = strumDiscriminantsPassthroughError(Span.callSite())
        assertNotNull(err5.message)
    }
}
