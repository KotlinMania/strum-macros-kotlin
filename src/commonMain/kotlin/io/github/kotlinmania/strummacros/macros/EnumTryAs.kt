// port-lint: source strum_macros/src/macros/enum_try_as.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.formatIdent
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.snakify
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.SynResult

internal fun enumTryAsInner(ast: DeriveInput): SynResult<TokenStream> {
    val variants = when (val data = ast.data) {
        is Data.Enum -> data.variants.toList()
        else -> return SynResult.failure(nonEnumError())
    }

    val enumName = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()

    val variantTokens = mutableListOf<TokenStream>()
    for (variant in variants) {
        val props = variant.getVariantProperties().getOrNull()
        if (props == null || props.disabled != null) {
            continue
        }

        when (val fields = variant.fields) {
            is Fields.Unnamed -> {
                val variantName = variant.ident
                val types = fields.fields.unnamed.toList().map { it.ty.toTokenStream() }
                val fieldNames = (0 until fields.fields.unnamed.len()).map { i ->
                    val name = "x".repeat(i + 1)
                    val ident = formatIdent("{}", name)
                    quote("#ident", "ident" to ident)
                }

                val moveFnName = formatIdent("try_as_{}", snakify(variantName.toString()))
                val refFnName = formatIdent("try_as_{}_ref", snakify(variantName.toString()))
                val mutFnName = formatIdent("try_as_{}_mut", snakify(variantName.toString()))

                val item = quote(
                    """
                    #[automatically_derived]
                    #[must_use]
                    #[inline]
                    pub fn #move_fn_name(self) -> ::core::option::Option<(#(#types),*)> {
                        match self {
                            #enum_name::#variant_name (#(#field_names),*) => Some((#(#field_names),*)),
                            _ => None
                        }
                    }

                    #[automatically_derived]
                    #[must_use]
                    #[inline]
                    pub const fn #ref_fn_name(&self) -> ::core::option::Option<(#(&#types),*)> {
                        match self {
                            #enum_name::#variant_name (#(#field_names),*) => Some((#(#field_names),*)),
                            _ => None
                        }
                    }

                    #[automatically_derived]
                    #[must_use]
                    #[inline]
                    pub fn #mut_fn_name(&mut self) -> ::core::option::Option<(#(&mut #types),*)> {
                        match self {
                            #enum_name::#variant_name (#(#field_names),*) => Some((#(#field_names),*)),
                            _ => None
                        }
                    }
                    """.trimIndent(),
                    "move_fn_name" to moveFnName,
                    "types" to types,
                    "enum_name" to enumName,
                    "variant_name" to variantName,
                    "field_names" to fieldNames,
                    "ref_fn_name" to refFnName,
                    "mut_fn_name" to mutFnName,
                )
                variantTokens.add(item)
            }
            else -> {}
        }
    }

    val output = quote(
        """
        #[automatically_derived]
        impl #impl_generics #enum_name #ty_generics #where_clause {
            #(#variant_tokens)*
        }
        """.trimIndent(),
        "impl_generics" to implGenerics,
        "enum_name" to enumName,
        "ty_generics" to tyGenerics,
        "where_clause" to whereClause,
        "variant_tokens" to variantTokens,
    )
    return SynResult.success(output)
}
