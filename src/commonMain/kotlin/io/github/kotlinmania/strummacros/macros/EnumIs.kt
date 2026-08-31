// port-lint: source strum_macros/src/macros/enum_is.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.formatIdent
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.snakify
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.SynResult

internal fun enumIsInner(ast: DeriveInput): SynResult<TokenStream> {
    val variants =
        when (val data = ast.data) {
            is Data.Enum -> data.variants.toList()
            else -> return SynResult.failure(nonEnumError())
        }
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()

    val enumName = ast.ident
    val variantTokens = mutableListOf<TokenStream>()
    for (variant in variants) {
        val props = variant.getVariantProperties().getOrNull()
        if (props == null || props.disabled != null) {
            continue
        }

        val variantName = variant.ident
        val fnName = formatIdent("is_{}", snakify(variantName.toString()))
        val docComment = "Returns [true] if the enum is [$enumName::$variantName] otherwise [false]"
        val item =
            quote(
                """
                #[must_use]
                #[inline]
                #[doc = #doc_comment]
                pub const fn #fn_name(&self) -> bool {
                    match self {
                        &#enum_name::#variant_name { .. } => true,
                        _ => false
                    }
                }
                """.trimIndent(),
                "doc_comment" to docComment,
                "fn_name" to fnName,
                "enum_name" to enumName,
                "variant_name" to variantName,
            )
        variantTokens.add(item)
    }

    val output =
        quote(
            """
            #[automatically_derived]
            impl #impl_generics #enum_name #ty_generics #where_clause {
                #(#variants)*
            }
            """.trimIndent(),
            "impl_generics" to implGenerics,
            "enum_name" to enumName,
            "ty_generics" to tyGenerics,
            "where_clause" to whereClause,
            "variants" to variantTokens,
        )
    return SynResult.success(output)
}
