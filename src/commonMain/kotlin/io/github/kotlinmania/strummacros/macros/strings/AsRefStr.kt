// port-lint: source strum_macros/src/macros/strings/as_ref_str.rs
package io.github.kotlinmania.strummacros.macros.strings

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.nonSingleFieldVariantError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.SynResult

private fun getArms(ast: DeriveInput, transparentFn: (TokenStream) -> TokenStream): SynResult<List<TokenStream>> {
    val name = ast.ident
    val arms = mutableListOf<TokenStream>()
    val variants = when (val data = ast.data) {
        is Data.Enum -> data.variants.toList()
        else -> return SynResult.failure(nonEnumError())
    }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }

    for (variant in variants) {
        val ident = variant.ident
        val variantProperties = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }

        if (variantProperties.disabled != null) {
            continue
        }

        if (variantProperties.transparent != null) {
            val armResult = extractSingleFieldVariantAndThen(name, variant) { tok ->
                transparentFn(tok)
            }
            if (armResult.isFailure) {
                return SynResult.failure(nonSingleFieldVariantError("transparent"))
            }
            arms.add(armResult.getOrThrow())
            continue
        }

        val output = variantProperties.getPreferredName(
            typeProperties.caseStyle,
            typeProperties.prefix,
            typeProperties.suffix,
        )
        val params = when (variant.fields) {
            is Fields.Unit -> quote("")
            is Fields.Unnamed -> quote("(..)")
            is Fields.Named -> quote("{..}")
        }

        arms.add(quote("#name::#ident #params => #output", "name" to name, "ident" to ident, "params" to params, "output" to output))
    }

    if (arms.size < variants.size) {
        arms.add(
            quote(
                """
                _ => panic!(
                    "AsRef::<str>::as_ref() or AsStaticRef::<str>::as_static() \
                     called on disabled variant.",
                )
                """.trimIndent(),
            ),
        )
    }

    return SynResult.success(arms)
}

internal fun asRefStrInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val arms = getArms(ast) { tok ->
        quote("::core::convert::AsRef::<str>::as_ref(#tok)", "tok" to tok)
    }.getOrElse { return SynResult.failure(it) }

    val output = quote(
        """
        #[automatically_derived]
        impl #impl_generics ::core::convert::AsRef<str> for #name #ty_generics #where_clause {
            #[inline]
            fn as_ref(&self) -> &str {
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

public enum class GenerateTraitVariant {
    AsStaticStr,
    From,
}

public fun asStaticStrInner(
    ast: DeriveInput,
    traitVariant: GenerateTraitVariant,
): SynResult<TokenStream> {
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val arms = getArms(ast) { tok ->
        quote("::core::convert::From::from(#tok)", "tok" to tok)
    }.getOrElse { return SynResult.failure(it) }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()

    val generics = ast.generics.copy()
    generics.params.pushValue(GenericParam.LifetimeParam.new(Lifetime.new("'_derivative_strum", io.github.kotlinmania.procmacro2.Span.callSite())))
    val (implGenerics2, _, _) = generics.splitForImpl()

    val output = when (traitVariant) {
        GenerateTraitVariant.AsStaticStr -> quote(
            """
            #[automatically_derived]
            impl #impl_generics #strum_module_path::AsStaticRef<str> for #name #ty_generics #where_clause {
                #[inline]
                fn as_static(&self) -> &'static str {
                    match *self {
                        #(#arms),*
                    }
                }
            }
            """.trimIndent(),
            "impl_generics" to implGenerics,
            "strum_module_path" to strumModulePath,
            "name" to name,
            "ty_generics" to tyGenerics,
            "where_clause" to whereClause,
            "arms" to arms,
        )
        GenerateTraitVariant.From -> if (!typeProperties.constIntoStr) {
            quote(
                """
                #[automatically_derived]
                impl #impl_generics ::core::convert::From<#name #ty_generics> for &'static str #where_clause {
                    #[inline]
                    fn from(x: #name #ty_generics) -> &'static str {
                        match x {
                            #(#arms),*
                        }
                    }
                }
                #[automatically_derived]
                impl #impl_generics2 ::core::convert::From<&'_derivative_strum #name #ty_generics> for &'static str #where_clause {
                    #[inline]
                    fn from(x: &'_derivative_strum #name #ty_generics) -> &'static str {
                        match *x {
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
                "impl_generics2" to implGenerics2,
            )
        } else {
            quote(
                """
                #[automatically_derived]
                impl #impl_generics #name #ty_generics #where_clause {
                    pub const fn into_str(&self) -> &'static str {
                        match self {
                            #(#arms),*
                        }
                    }
                }
                #[automatically_derived]
                impl #impl_generics ::core::convert::From<#name #ty_generics> for &'static str #where_clause {
                    fn from(x: #name #ty_generics) -> &'static str {
                        match x {
                            #(#arms),*
                        }
                    }
                }
                #[automatically_derived]
                impl #impl_generics2 ::core::convert::From<&'_derivative_strum #name #ty_generics> for &'static str #where_clause {
                    fn from(x: &'_derivative_strum #name #ty_generics) -> &'static str {
                        x.into_str()
                    }
                }
                """.trimIndent(),
                "impl_generics" to implGenerics,
                "name" to name,
                "ty_generics" to tyGenerics,
                "where_clause" to whereClause,
                "arms" to arms,
                "impl_generics2" to implGenerics2,
            )
        }
    }

    return SynResult.success(output)
}
