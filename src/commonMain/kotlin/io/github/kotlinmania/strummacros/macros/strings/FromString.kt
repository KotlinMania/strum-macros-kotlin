// port-lint: source macros/strings/from_string.rs
package io.github.kotlinmania.strummacros.macros.strings

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantInnerProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.missingParseErrAttrError
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.occurrenceError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.SynError
import io.github.kotlinmania.syn.SynResult

internal fun fromStringInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val variants =
        when (val data = ast.data) {
            is Data.Enum -> data.variants.toList()
            else -> return SynResult.failure(nonEnumError())
        }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()

    if (typeProperties.parseErrFn != null && typeProperties.parseErrTy == null) {
        return SynResult.failure(missingParseErrAttrError())
    }

    var defaultKw: Ident? = null
    var defaultMatchArm: TokenStream? = null

    val phfExactMatchArms = mutableListOf<TokenStream>()
    val standardMatchArms = mutableListOf<TokenStream>()

    for (variant in variants) {
        val ident = variant.ident
        val variantProperties = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }

        if (variantProperties.disabled != null) {
            continue
        }

        if (variantProperties.default != null) {
            val kw = variantProperties.default!!
            val prevDefaultKw = defaultKw
            if (prevDefaultKw != null) {
                return SynResult.failure(occurrenceError(prevDefaultKw, kw, "default"))
            }
            defaultKw = kw

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
                    defaultMatchArm = quote("#name::#ident(s.into())", "name" to name, "ident" to ident)
                }
                is Fields.Named -> {
                    if (fields.fields.named.len() != 1) {
                        return SynResult.failure(
                            SynError.new(
                                variant.ident.span(),
                                "Default only works on newtype structs with a single String field",
                            ),
                        )
                    }
                    val fieldName =
                        fields.fields.named
                            .last()!!
                            .ident!!
                    defaultMatchArm = quote("#name::#ident { #fieldName : s.into() }", "name" to name, "ident" to ident, "fieldName" to fieldName)
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
            continue
        }

        val params =
            when (val fields = variant.fields) {
                is Fields.Unit -> quote("")
                is Fields.Unnamed -> {
                    if (variantProperties.defaultWith != null) {
                        val value = variantProperties.defaultWith!!
                        val func = Ident.new(value.value(), value.span())
                        val defaults = listOf(quote("#func()", "func" to func))
                        quote("(#(#defaults),*)", "defaults" to defaults)
                    } else {
                        val defaults = List(fields.fields.unnamed.len()) { quote("Default::default()") }
                        quote("(#(#defaults),*)", "defaults" to defaults)
                    }
                }
                is Fields.Named -> {
                    val defaults = mutableListOf<TokenStream>()
                    for (field in fields.fields.named.toList()) {
                        val meta = field.getVariantInnerProperties().getOrElse { return SynResult.failure(it) }
                        val fieldIdent = field.ident!!
                        if (meta.defaultWith != null) {
                            val defaultWith = meta.defaultWith!!
                            val func = Ident.new(defaultWith.value(), defaultWith.span())
                            defaults.add(quote("#fieldIdent: #func()", "fieldIdent" to fieldIdent, "func" to func))
                        } else {
                            defaults.add(quote("#fieldIdent: Default::default()", "fieldIdent" to fieldIdent))
                        }
                    }
                    quote("{#(#defaults),*}", "defaults" to defaults)
                }
            }

        val isAsciiCaseInsensitive = variantProperties.asciiCaseInsensitive ?: typeProperties.asciiCaseInsensitive

        for (serialization in variantProperties.getSerializations(typeProperties.caseStyle)) {
            if (typeProperties.usePhf) {
                phfExactMatchArms.add(quote("#serialization => #name::#ident #params,", "serialization" to serialization, "name" to name, "ident" to ident, "params" to params))
                if (isAsciiCaseInsensitive) {
                    val serString = serialization.value()
                    val lower = LitStr.new(serString.lowercase(), serialization.span())
                    val upper = LitStr.new(serString.uppercase(), serialization.span())
                    phfExactMatchArms.add(quote("#lower => #name::#ident #params,", "lower" to lower, "name" to name, "ident" to ident, "params" to params))
                    phfExactMatchArms.add(quote("#upper => #name::#ident #params,", "upper" to upper, "name" to name, "ident" to ident, "params" to params))
                    standardMatchArms.add(quote("s if s.eq_ignore_ascii_case(#serialization) => #name::#ident #params,", "serialization" to serialization, "name" to name, "ident" to ident, "params" to params))
                }
            } else if (!isAsciiCaseInsensitive) {
                standardMatchArms.add(quote("#serialization => #name::#ident #params,", "serialization" to serialization, "name" to name, "ident" to ident, "params" to params))
            } else {
                standardMatchArms.add(quote("s if s.eq_ignore_ascii_case(#serialization) => #name::#ident #params,", "serialization" to serialization, "name" to name, "ident" to ident, "params" to params))
            }
        }
    }

    val isInfallible = defaultMatchArm != null
    val hasCustomErrTy = typeProperties.parseErrTy != null
    val errTy =
        if (typeProperties.parseErrTy != null) {
            val ty = typeProperties.parseErrTy!!
            quote("#ty", "ty" to ty)
        } else if (isInfallible) {
            quote("::core::convert::Infallible")
        } else {
            quote("#strum_module_path::ParseError", "strum_module_path" to strumModulePath)
        }

    val defaultArmTokens =
        if (defaultMatchArm != null) {
            defaultMatchArm
        } else if (typeProperties.parseErrFn != null) {
            val f = typeProperties.parseErrFn!!
            quote("return ::core::result::Result::Err(#f(s))", "f" to f)
        } else if (hasCustomErrTy) {
            return SynResult.failure(missingParseErrAttrError())
        } else {
            quote("return ::core::result::Result::Err(#strum_module_path::ParseError::VariantNotFound)", "strum_module_path" to strumModulePath)
        }

    var matchExpression =
        if (standardMatchArms.isEmpty()) {
            defaultArmTokens
        } else {
            quote(
                """
                match s {
                    #(#standard_match_arms)*
                    _ => #default_arm,
                }
                """.trimIndent(),
                "standard_match_arms" to standardMatchArms,
                "default_arm" to defaultArmTokens,
            )
        }

    if (phfExactMatchArms.isNotEmpty()) {
        matchExpression =
            quote(
                """
                use #strum_module_path::_private_phf_reexport_for_macro_if_phf_feature as phf;
                static PHF: phf::Map<&'static str, #name> = phf::phf_map! {
                    #(#phf_exact_match_arms)*
                };

                if let Some(value) = PHF.get(s).cloned() {
                    value
                } else {
                    #match_expression
                }
                """.trimIndent(),
                "strum_module_path" to strumModulePath,
                "name" to name,
                "phf_exact_match_arms" to phfExactMatchArms,
                "match_expression" to matchExpression,
            )
    }

    val fromImpl =
        if (isInfallible && !hasCustomErrTy) {
            quote(
                """
                #[allow(clippy::use_self)]
                #[automatically_derived]
                impl #impl_generics ::core::convert::From<&str> for #name #ty_generics #where_clause {
                    #[inline]
                    fn from(s: &str) -> #name #ty_generics {
                        #match_expression
                    }
                }
                """.trimIndent(),
                "impl_generics" to implGenerics,
                "name" to name,
                "ty_generics" to tyGenerics,
                "where_clause" to whereClause,
                "match_expression" to matchExpression,
            )
        } else {
            quote(
                """
                #[allow(clippy::use_self)]
                #[automatically_derived]
                impl #impl_generics ::core::convert::TryFrom<&str> for #name #ty_generics #where_clause {
                    type Error = #err_ty;

                    #[inline]
                    fn try_from(s: &str) -> ::core::result::Result< #name #ty_generics , <Self as ::core::convert::TryFrom<&str>>::Error> {
                        Ok({
                            #match_expression
                        })
                    }
                }
                """.trimIndent(),
                "impl_generics" to implGenerics,
                "name" to name,
                "ty_generics" to tyGenerics,
                "where_clause" to whereClause,
                "err_ty" to errTy,
                "match_expression" to matchExpression,
            )
        }

    val fromStr =
        quote(
            """
            #[allow(clippy::use_self)]
            #[automatically_derived]
            impl #impl_generics ::core::str::FromStr for #name #ty_generics #where_clause {
                type Err = #err_ty;

                #[inline]
                fn from_str(s: &str) -> ::core::result::Result< #name #ty_generics , <Self as ::core::str::FromStr>::Err> {
                    <Self as ::core::convert::TryFrom<&str>>::try_from(s)
                }
            }
            """.trimIndent(),
            "impl_generics" to implGenerics,
            "name" to name,
            "ty_generics" to tyGenerics,
            "where_clause" to whereClause,
            "err_ty" to errTy,
        )

    val output =
        quote(
            """
            #from_str
            #from_impl
            """.trimIndent(),
            "from_str" to fromStr,
            "from_impl" to fromImpl,
        )
    return SynResult.success(output)
}
