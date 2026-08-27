// port-lint: source strum_macros/src/macros/strings/to_string.rs
package io.github.kotlinmania.strummacros.macros.strings

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.SynError
import io.github.kotlinmania.syn.SynResult

internal fun toStringInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val variants = when (val data = ast.data) {
        is Data.Enum -> data.variants.toList()
        else -> return SynResult.failure(nonEnumError())
    }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val arms = mutableListOf<TokenStream>()

    for (variant in variants) {
        val ident = variant.ident
        val variantProperties = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }

        if (variantProperties.disabled != null) {
            continue
        }

        if (variantProperties.toStringValue == null && variantProperties.default != null) {
            when (val fields = variant.fields) {
                is Fields.Unnamed -> {
                    if (fields.fields.unnamed.len() != 1) {
                        return SynResult.failure(
                            SynError.new(
                                variant.ident.span(),
                                "Default only works on newtype structs with a single String field",
                            ),
                        )
                    }
                    arms.add(quote("#name::#ident(ref s) => ::std::string::String::from(s)", "name" to name, "ident" to ident))
                    continue
                }
                else -> {
                    return SynResult.failure(
                        SynError.new(
                            variant.ident.span(),
                            "Default only works on newtype structs with a single String field",
                        ),
                    )
                }
            }
        }

        val output = variantProperties.getPreferredName(
            typeProperties.caseStyle,
            typeProperties.prefix,
            typeProperties.suffix,
        )

        val params = when (variant.fields) {
            is Fields.Unit, Fields.Unit -> quote("")
            is Fields.Unnamed -> quote("(..)")
            is Fields.Named -> quote("{..}")
        }

        arms.add(quote("#name::#ident #params => ::std::string::String::from(#output)", "name" to name, "ident" to ident, "params" to params, "output" to output))
    }

    if (arms.size < variants.size) {
        arms.add(quote("_ => panic!(\"to_string() called on disabled variant.\")"))
    }

    val output = quote(
        """
        #[allow(clippy::use_self)]
        #[automatically_derived]
        impl #impl_generics ::std::string::ToString for #name #ty_generics #where_clause {
            fn to_string(&self) -> ::std::string::String {
                match *self {
                    #(#arms),*
                }
            }
        }
        """.trimIndent(),
        "impl_generics" to implGenerics,
        "name" to name,
        "ty_generics" to tyGenerics,
        "where_clause" to whereClause,
        "arms" to arms,
    )
    return SynResult.success(output)
}
