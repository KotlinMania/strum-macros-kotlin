// port-lint: source macros/strings/display.rs
package io.github.kotlinmania.strummacros.macros.strings

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.formatIdent
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.nonSingleFieldVariantError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.IdentParse
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.SynError
import io.github.kotlinmania.syn.SynResult
import io.github.kotlinmania.syn.parseStr

internal fun displayInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val variants =
        when (val data = ast.data) {
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

        if (variantProperties.transparent != null) {
            val armResult =
                extractSingleFieldVariantAndThen(name, variant) { tok ->
                    quote("::core::fmt::Display::fmt(#tok, f)", "tok" to tok)
                }
            if (armResult.isFailure) {
                return SynResult.failure(nonSingleFieldVariantError("transparent"))
            }
            arms.add(armResult.getOrThrow())
            continue
        }

        val output =
            variantProperties.getPreferredName(
                typeProperties.caseStyle,
                typeProperties.prefix,
                typeProperties.suffix,
            )

        val params =
            when (val fields = variant.fields) {
                is Fields.Unit -> quote("")
                is Fields.Unnamed -> {
                    val names =
                        (0 until fields.fields.unnamed.len()).map { i ->
                            val fieldIdent = formatIdent("field{}", i)
                            quote("ref #fieldIdent", "fieldIdent" to fieldIdent)
                        }
                    quote("(#(#names),*)", "names" to names)
                }
                is Fields.Named -> {
                    val names =
                        fields.fields.named.toList().map { field ->
                            val fieldIdent = field.ident!!
                            quote("ref #fieldIdent", "fieldIdent" to fieldIdent)
                        }
                    quote("{#(#names),*}", "names" to names)
                }
            }

        if (variantProperties.toStringValue == null && variantProperties.default != null) {
            val armResult =
                extractSingleFieldVariantAndThen(name, variant) { tok ->
                    quote("::core::fmt::Display::fmt(#tok, f)", "tok" to tok)
                }
            if (armResult.isFailure) {
                return SynResult.failure(
                    SynError.new(
                        variant.ident.span(),
                        "Default only works on newtype structs with a single String field",
                    ),
                )
            }
            arms.add(armResult.getOrThrow())
            continue
        }

        val arm =
            when (val fields = variant.fields) {
                is Fields.Named -> {
                    val usedVars = captureFormatStringIdents(output).getOrElse { return SynResult.failure(it) }
                    if (usedVars.isEmpty()) {
                        quote("#name::#ident #params => ::core::fmt::Display::fmt(#output, f)", "name" to name, "ident" to ident, "params" to params, "output" to output)
                    } else {
                        val usedSet = usedVars.map { it.toString() }.toSet()
                        val args =
                            fields.fields.named.toList().mapNotNull { field ->
                                val fieldIdent = field.ident!!
                                if (fieldIdent.toString() !in usedSet) {
                                    null
                                } else {
                                    quote("#fieldIdent = #fieldIdent", "fieldIdent" to fieldIdent)
                                }
                            }
                        quote(
                            """
                            #[allow(unused_variables)]
                            #name::#ident #params => ::core::fmt::Display::fmt(&format_args!(#output, #(#args),*), f)
                            """.trimIndent(),
                            "name" to name,
                            "ident" to ident,
                            "params" to params,
                            "output" to output,
                            "args" to args,
                        )
                    }
                }
                is Fields.Unnamed -> {
                    val usedVars = captureFormatStrings(output).getOrElse { return SynResult.failure(it) }
                    if (usedVars.any { it.isEmpty() }) {
                        return SynResult.failure(
                            SynError.new(
                                output.span(),
                                "Empty {} is not allowed; Use manual numbering ({0})",
                            ),
                        )
                    }
                    if (usedVars.isEmpty()) {
                        quote("#name::#ident #params => ::core::fmt::Display::fmt(#output, f)", "name" to name, "ident" to ident, "params" to params, "output" to output)
                    } else {
                        val args =
                            (0 until fields.fields.unnamed.len()).map { i ->
                                formatIdent("field{}", i)
                            }
                        quote(
                            """
                            #[allow(unused_variables)]
                            #name::#ident #params => ::core::fmt::Display::fmt(&format!(#output, #(#args),*), f)
                            """.trimIndent(),
                            "name" to name,
                            "ident" to ident,
                            "params" to params,
                            "output" to output,
                            "args" to args,
                        )
                    }
                }
                is Fields.Unit, Fields.Unit -> {
                    val usedVars = captureFormatStrings(output).getOrElse { return SynResult.failure(it) }
                    if (usedVars.isNotEmpty()) {
                        return SynResult.failure(
                            SynError.new(
                                output.span(),
                                "Unit variants do not support interpolation",
                            ),
                        )
                    }
                    quote("#name::#ident #params => ::core::fmt::Display::fmt(#output, f)", "name" to name, "ident" to ident, "params" to params, "output" to output)
                }
            }

        arms.add(arm)
    }

    if (arms.size < variants.size) {
        arms.add(quote("_ => panic!(\"fmt() called on disabled variant.\")"))
    }

    val output =
        quote(
            """
            #[automatically_derived]
            impl #impl_generics ::core::fmt::Display for #name #ty_generics #where_clause {
                fn fmt(&self, f: &mut ::core::fmt::Formatter) -> ::core::result::Result<(), ::core::fmt::Error> {
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

private fun captureFormatStringIdents(stringLiteral: LitStr): SynResult<List<Ident>> {
    val rawList = captureFormatStrings(stringLiteral).getOrElse { return SynResult.failure(it) }
    val idents = mutableListOf<Ident>()
    for (identStr in rawList) {
        val parsed =
            parseStr(IdentParse::parse, identStr).getOrElse {
                return SynResult.failure(
                    SynError.new(
                        stringLiteral.span(),
                        "Invalid identifier inside format string bracket",
                    ),
                )
            }
        idents.add(parsed)
    }
    return SynResult.success(idents)
}

private fun captureFormatStrings(stringLiteral: LitStr): SynResult<List<String>> {
    val formatStr = stringLiteral.value().replace("{{", "").replace("}}", "")
    var newVarStartIndex: Int? = null
    val varUsed = mutableListOf<String>()

    for (i in formatStr.indices) {
        val chr = formatStr[i]
        if (chr == '{') {
            if (newVarStartIndex != null) {
                return SynResult.failure(
                    SynError.new(
                        stringLiteral.span(),
                        "Bracket opened without closing previous bracket",
                    ),
                )
            }
            newVarStartIndex = i
            continue
        }

        if (chr == '}') {
            val startIndex =
                newVarStartIndex ?: return SynResult.failure(
                    SynError.new(
                        stringLiteral.span(),
                        "Bracket closed without previous opened bracket",
                    ),
                )
            newVarStartIndex = null

            val insideBrackets = formatStr.substring(startIndex + 1, i)
            val identStr = insideBrackets.split(":")[0].trimEnd()
            varUsed.add(identStr)
        }
    }

    return SynResult.success(varUsed)
}
