// port-lint: source strum_macros/src/macros/from_repr.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.formatIdent
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.SynError
import io.github.kotlinmania.syn.SynResult
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.parse2
import io.github.kotlinmania.syn.parseStr

internal fun fromReprInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val gen = ast.generics
    val (implGenerics, tyGenerics, whereClause) = gen.splitForImpl()
    val vis = ast.vis

    var discriminantType: SynType = parseStr(SynType.Companion::withoutPlus, "usize").getOrElse { return SynResult.failure(it) }
    val typeProps = ast.getTypeProperties().getOrNull()
    if (typeProps?.enumRepr != null) {
        val parsedType = parse2(SynType.Companion::withoutPlus, typeProps.enumRepr!!).getOrNull()
        if (parsedType is SynType.Path) {
            val lastSeg = parsedType.path.segments.last()
            if (lastSeg != null) {
                val segIdent = lastSeg.ident.toString()
                if (segIdent in listOf("u8", "u16", "u32", "u64", "usize", "i8", "i16", "i32", "i64", "isize")) {
                    discriminantType = parsedType
                }
            }
        }
    }

    if (gen.lifetimes().count() > 0) {
        return SynResult.failure(
            SynError.new(
                Span.callSite(),
                "This macro doesn't support enums with lifetimes. The resulting enums would be unbounded.",
            ),
        )
    }

    val variants =
        when (val data = ast.data) {
            is Data.Enum -> data.variants.toList()
            else -> return SynResult.failure(nonEnumError())
        }

    val arms = mutableListOf<TokenStream>()
    val constantDefs = mutableListOf<TokenStream>()
    var hasAdditionalData = false
    var prevConstVarIdent: Ident? = null

    for (variant in variants) {
        val props = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }
        if (props.disabled != null) {
            continue
        }

        val ident = variant.ident
        val params =
            when (val fields = variant.fields) {
                is Fields.Unit -> quote("")
                is Fields.Unnamed -> {
                    hasAdditionalData = true
                    val defaults = List(fields.fields.unnamed.len()) { quote("::core::default::Default::default()") }
                    quote("(#(#defaults),*)", "defaults" to defaults)
                }
                is Fields.Named -> {
                    hasAdditionalData = true
                    val fieldIdents =
                        fields.fields.named
                            .toList()
                            .mapNotNull { it.ident }
                    val fieldAssignments = fieldIdents.map { f -> quote("#f: ::core::default::Default::default()", "f" to f) }
                    quote("{#(#field_assignments),*}", "field_assignments" to fieldAssignments)
                }
            }

        val constVarStr = "${variant.ident}_DISCRIMINANT"
        val constVarIdent = formatIdent("{}", constVarStr)

        val constValExpr =
            if (variant.discriminant != null) {
                val expr = variant.discriminant!!.expr
                quote("#expr", "expr" to expr)
            } else if (prevConstVarIdent != null) {
                val prev = prevConstVarIdent
                quote("#prev + 1", "prev" to prev)
            } else {
                quote("0")
            }

        constantDefs.add(
            quote(
                """
                #[allow(non_upper_case_globals)]
                const #const_var_ident: #discriminant_type = #const_val_expr;
                """.trimIndent(),
                "const_var_ident" to constVarIdent,
                "discriminant_type" to discriminantType,
                "const_val_expr" to constValExpr,
            ),
        )
        arms.add(
            quote(
                "v if v == #const_var_ident => ::core::option::Option::Some(#name::#ident #params)",
                "const_var_ident" to constVarIdent,
                "name" to name,
                "ident" to ident,
                "params" to params,
            ),
        )

        prevConstVarIdent = constVarIdent
    }

    arms.add(quote("_ => ::core::option::Option::None"))

    val constIfPossible =
        if (hasAdditionalData) {
            quote("")
        } else {
            quote("const")
        }

    val output =
        quote(
            """
            #[allow(clippy::use_self)]
            #[automatically_derived]
            impl #impl_generics #name #ty_generics #where_clause {
                #[doc = "Try to create [Self] from the raw representation"]
                #[inline]
                #vis #const_if_possible fn from_repr(discriminant: #discriminant_type) -> Option<#name #ty_generics> {
                    #(#constant_defs)*
                    match discriminant {
                        #(#arms),*
                    }
                }
            }
            """.trimIndent(),
            "impl_generics" to implGenerics,
            "name" to name,
            "ty_generics" to tyGenerics,
            "where_clause" to whereClause,
            "vis" to vis,
            "const_if_possible" to constIfPossible,
            "discriminant_type" to discriminantType,
            "constant_defs" to constantDefs,
            "arms" to arms,
        )

    return SynResult.success(output)
}
