// port-lint: source lib.rs
package io.github.kotlinmania.strummacros

import io.github.kotlinmania.procmacro2.TokenStream
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
import io.github.kotlinmania.strummacros.macros.strings.GenerateTraitVariant
import io.github.kotlinmania.strummacros.macros.strings.asRefStrInner
import io.github.kotlinmania.strummacros.macros.strings.asStaticStrInner
import io.github.kotlinmania.strummacros.macros.strings.displayInner
import io.github.kotlinmania.strummacros.macros.strings.fromStringInner
import io.github.kotlinmania.strummacros.macros.strings.toStringInner
import io.github.kotlinmania.syn.DeriveInputParse
import io.github.kotlinmania.syn.parse2

public fun fromString(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return fromStringInner(ast).getOrElse { it.toCompileError() }
}

public fun asRefStr(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return asRefStrInner(ast).getOrElse { it.toCompileError() }
}

public fun variantNames(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumVariantNamesInner(ast).getOrElse { it.toCompileError() }
}

public fun variantNamesDeprecated(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumVariantNamesInner(ast).getOrElse { it.toCompileError() }
}

public fun staticVariantsArray(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return staticVariantsArrayInner(ast).getOrElse { it.toCompileError() }
}

public fun asStaticStr(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return asStaticStrInner(ast, GenerateTraitVariant.AsStaticStr).getOrElse { it.toCompileError() }
}

public fun intoStaticStr(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return asStaticStrInner(ast, GenerateTraitVariant.From).getOrElse { it.toCompileError() }
}

public fun toString(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return toStringInner(ast).getOrElse { it.toCompileError() }
}

public fun display(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return displayInner(ast).getOrElse { it.toCompileError() }
}

public fun enumIter(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumIterInner(ast).getOrElse { it.toCompileError() }
}

public fun enumIs(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumIsInner(ast).getOrElse { it.toCompileError() }
}

public fun enumTryAs(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumTryAsInner(ast).getOrElse { it.toCompileError() }
}

public fun enumTable(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumTableInner(ast).getOrElse { it.toCompileError() }
}

public fun fromRepr(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return fromReprInner(ast).getOrElse { it.toCompileError() }
}

public fun enumMessages(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumMessageInner(ast).getOrElse { it.toCompileError() }
}

public fun enumProperties(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumPropertiesInner(ast).getOrElse { it.toCompileError() }
}

public fun enumDiscriminants(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumDiscriminantsInner(ast).getOrElse { it.toCompileError() }
}

public fun enumCount(input: TokenStream): TokenStream {
    val ast = parse2(DeriveInputParse::parse, input).getOrElse { return it.toCompileError() }
    return enumCountInner(ast).getOrElse { it.toCompileError() }
}

/** Top-level strum-macros namespace module. */
public object StrumMacros {
    public const val VERSION: String = "0.26.4"
}
