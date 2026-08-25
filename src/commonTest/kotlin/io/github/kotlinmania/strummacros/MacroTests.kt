package io.github.kotlinmania.strummacros

import io.github.kotlinmania.strummacros.macros.enumCountInner
import io.github.kotlinmania.strummacros.macros.enumDiscriminantsInner
import io.github.kotlinmania.strummacros.macros.enumIsInner
import io.github.kotlinmania.strummacros.macros.enumIterInner
import io.github.kotlinmania.strummacros.macros.enumMessageInner
import io.github.kotlinmania.strummacros.macros.enumPropertiesInner
import io.github.kotlinmania.strummacros.macros.enumTableInner
import io.github.kotlinmania.strummacros.macros.enumTryAsInner
import io.github.kotlinmania.strummacros.macros.enumVariantNamesInner
import io.github.kotlinmania.strummacros.macros.fromReprInner
import io.github.kotlinmania.strummacros.macros.staticVariantsArrayInner
import io.github.kotlinmania.strummacros.macros.strings.asRefStrInner
import io.github.kotlinmania.strummacros.macros.strings.displayInner
import io.github.kotlinmania.strummacros.macros.strings.fromStringInner
import io.github.kotlinmania.strummacros.macros.strings.toStringInner
import io.github.kotlinmania.syn.DeriveInputParse
import io.github.kotlinmania.syn.parseStr
import kotlin.test.Test
import kotlin.test.assertTrue

class MacroTests {
    @Test
    fun testEnumCount() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = enumCountInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("EnumCount"))
        assertTrue(tokens.contains("COUNT"))
        assertTrue(tokens.contains("3"))
    }

    @Test
    fun testEnumVariantNames() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = enumVariantNamesInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("VariantNames"))
        assertTrue(tokens.contains("\"Red\""))
        assertTrue(tokens.contains("\"Green\""))
        assertTrue(tokens.contains("\"Blue\""))
    }

    @Test
    fun testVariantArray() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = staticVariantsArrayInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("VariantArray"))
        assertTrue(tokens.contains("Red"))
    }

    @Test
    fun testEnumIter() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = enumIterInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("ColorIter"))
        assertTrue(tokens.contains("IntoEnumIterator"))
    }

    @Test
    fun testEnumMessage() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { #[strum(message = \"red color\")] Red, Green }").getOrThrow()
        val res = enumMessageInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("EnumMessage"))
        assertTrue(tokens.contains("red color"))
    }

    @Test
    fun testEnumProperty() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { #[strum(props(Key = \"Value\"))] Red, Green }").getOrThrow()
        val res = enumPropertiesInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("EnumProperty"))
        assertTrue(tokens.contains("get_str"))
    }

    @Test
    fun testEnumTable() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = enumTableInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("ColorTable"))
        assertTrue(tokens.contains("transform"))
    }

    @Test
    fun testEnumTryAs() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Value { Num(i32), Str(String) }").getOrThrow()
        val res = enumTryAsInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("try_as_num"))
        assertTrue(tokens.contains("try_as_str"))
    }

    @Test
    fun testFromRepr() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red = 1, Green = 2, Blue = 3 }").getOrThrow()
        val res = fromReprInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("from_repr"))
        assertTrue(tokens.contains("Option"))
    }

    @Test
    fun testAsRefStr() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = asRefStrInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("AsRef"))
        assertTrue(tokens.contains("\"Red\""))
    }

    @Test
    fun testDisplay() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = displayInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("Display"))
        assertTrue(tokens.contains("fmt"))
    }

    @Test
    fun testFromString() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = fromStringInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("FromStr"))
        assertTrue(tokens.contains("from_str"))
    }

    @Test
    fun testToString() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = toStringInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("ToString"))
        assertTrue(tokens.contains("to_string"))
    }

    @Test
    fun testEnumIs() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = enumIsInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("is_red"))
        assertTrue(tokens.contains("is_green"))
    }

    @Test
    fun testEnumDiscriminants() {
        val ast = parseStr({ DeriveInputParse.parse(it) }, "enum Color { Red, Green, Blue }").getOrThrow()
        val res = enumDiscriminantsInner(ast)
        assertTrue(res.isSuccess)
        val tokens = res.getOrThrow().toString()
        assertTrue(tokens.contains("ColorDiscriminants"))
    }
}
