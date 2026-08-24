// port-lint: tests src/helpers/case_style.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import kotlin.test.Test
import kotlin.test.assertEquals

class CaseStyleTest {
    @Test
    fun testConvertCase() {
        val id = Ident.new("test_me", Span.callSite())
        assertEquals("testMe", id.convertCase(CaseStyle.CamelCase))
        assertEquals("TestMe", id.convertCase(CaseStyle.PascalCase))
        assertEquals("Test-Me", id.convertCase(CaseStyle.TrainCase))
    }

    @Test
    fun testImplFromStrForCaseStylePascalCase() {
        val f = { s: String -> CaseStyle.fromString(s) }

        assertEquals(CaseStyle.PascalCase, f("PascalCase"))
        assertEquals(CaseStyle.PascalCase, f("camel_case"))

        assertEquals(CaseStyle.CamelCase, f("camelCase"))

        assertEquals(CaseStyle.SnakeCase, f("snake_case"))
        assertEquals(CaseStyle.SnakeCase, f("snek_case"))

        assertEquals(CaseStyle.KebabCase, f("kebab-case"))
        assertEquals(CaseStyle.KebabCase, f("kebab_case"))

        assertEquals(CaseStyle.ScreamingKebabCase, f("SCREAMING-KEBAB-CASE"))

        assertEquals(CaseStyle.ShoutySnakeCase, f("SCREAMING_SNAKE_CASE"))
        assertEquals(CaseStyle.ShoutySnakeCase, f("shouty_snake_case"))
        assertEquals(CaseStyle.ShoutySnakeCase, f("shouty_snek_case"))

        assertEquals(CaseStyle.LowerCase, f("lowercase"))

        assertEquals(CaseStyle.UpperCase, f("UPPERCASE"))

        assertEquals(CaseStyle.TitleCase, f("title_case"))

        assertEquals(CaseStyle.MixedCase, f("mixed_case"))
    }

    @Test
    fun testSnakify() {
        assertEquals("hello_world", snakify("HelloWorld"))
        assertEquals("hello_2_world", snakify("Hello2World"))
        assertEquals("hello_2_world_3", snakify("Hello2World3"))
    }
}
