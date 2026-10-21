package consumer

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.strummacros.enumCount
import io.github.kotlinmania.strummacros.fromString
import io.github.kotlinmania.strummacros.variantNames

fun countTokens(): TokenStream = enumCount(TokenStream.fromString("enum Color { Red, Green, Blue }").getOrThrow())
fun nameTokens(): TokenStream = variantNames(TokenStream.fromString("enum Color { Red, Green, Blue }").getOrThrow())
fun parserTokens(): TokenStream = fromString(TokenStream.fromString("enum Color { Red, Green, Blue }").getOrThrow())
