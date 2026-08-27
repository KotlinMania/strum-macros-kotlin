// port-lint: source strum_macros/src/macros/enum_messages.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.SynResult

internal fun enumMessageInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val variants = when (val data = ast.data) {
        is Data.Enum -> data.variants.toList()
        else -> return SynResult.failure(nonEnumError())
    }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()

    val arms = mutableListOf<TokenStream>()
    val detailedArms = mutableListOf<TokenStream>()
    val documentationArms = mutableListOf<TokenStream>()
    val serializations = mutableListOf<TokenStream>()

    for (variant in variants) {
        val variantProperties = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }
        val messages = variantProperties.message
        val detailedMessages = variantProperties.detailedMessage
        val documentation = variantProperties.documentation
        val ident = variant.ident

        val params = when (variant.fields) {
            is Fields.Unit -> quote("")
            is Fields.Unnamed -> quote("(..)")
            is Fields.Named -> quote("{..}")
        }

        run {
            val serializationVariants = variantProperties.getSerializations(typeProperties.caseStyle)
            val count = serializationVariants.size
            serializations.add(
                quote(
                    """
                    &#name::#ident #params => {
                        static ARR: [&'static str; #count] = [#(#serialization_variants),*];
                        &ARR
                    }
                    """.trimIndent(),
                    "name" to name,
                    "ident" to ident,
                    "params" to params,
                    "count" to count,
                    "serialization_variants" to serializationVariants,
                ),
            )
        }

        if (variantProperties.disabled != null) {
            continue
        }

        if (messages != null) {
            val tokens = quote(
                "&#name::#ident #params => ::core::option::Option::Some(#messages)",
                "name" to name,
                "ident" to ident,
                "params" to params,
                "messages" to messages,
            )
            arms.add(tokens)
            if (detailedMessages == null) {
                detailedArms.add(tokens)
            }
        }

        if (detailedMessages != null) {
            detailedArms.add(
                quote(
                    "&#name::#ident #params => ::core::option::Option::Some(#detailed_messages)",
                    "name" to name,
                    "ident" to ident,
                    "params" to params,
                    "detailed_messages" to detailedMessages,
                ),
            )
        }

        if (documentation.isNotEmpty()) {
            val docCleaned = documentation.map { litStr ->
                val line = litStr.value()
                if (line.startsWith(" ")) {
                    LitStr.new(line.substring(1), litStr.span())
                } else {
                    litStr
                }
            }
            if (docCleaned.size == 1) {
                val text = docCleaned[0]
                documentationArms.add(
                    quote(
                        "&#name::#ident #params => ::core::option::Option::Some(#text)",
                        "name" to name,
                        "ident" to ident,
                        "params" to params,
                        "text" to text,
                    ),
                )
            } else {
                documentationArms.add(
                    quote(
                        "&#name::#ident #params => ::core::option::Option::Some(concat!(#(concat!(#documentation, \"\\n\")),*))",
                        "name" to name,
                        "ident" to ident,
                        "params" to params,
                        "documentation" to docCleaned,
                    ),
                )
            }
        }
    }

    if (arms.size < variants.size) {
        arms.add(quote("_ => ::core::option::Option::None"))
    }
    if (detailedArms.size < variants.size) {
        detailedArms.add(quote("_ => ::core::option::Option::None"))
    }
    if (documentationArms.size < variants.size) {
        documentationArms.add(quote("_ => ::core::option::Option::None"))
    }

    val output = quote(
        """
        #[automatically_derived]
        impl #impl_generics #strum_module_path::EnumMessage for #name #ty_generics #where_clause {
            #[inline]
            fn get_message(&self) -> ::core::option::Option<&'static str> {
                match self {
                    #(#arms),*
                }
            }

            #[inline]
            fn get_detailed_message(&self) -> ::core::option::Option<&'static str> {
                match self {
                    #(#detailed_arms),*
                }
            }

            #[inline]
            fn get_documentation(&self) -> ::core::option::Option<&'static str> {
                match self {
                    #(#documentation_arms),*
                }
            }

            #[inline]
            fn get_serializations(&self) -> &'static [&'static str] {
                match self {
                    #(#serializations),*
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
        "detailed_arms" to detailedArms,
        "documentation_arms" to documentationArms,
        "serializations" to serializations,
    )
    return SynResult.success(output)
}
