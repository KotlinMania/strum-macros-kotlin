// port-lint: source macros/enum_variant_names.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.SynResult

internal fun enumVariantNamesInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val gen = ast.generics
    val (implGenerics, tyGenerics, whereClause) = gen.splitForImpl()

    val variants =
        when (val data = ast.data) {
            is Data.Enum -> data.variants.toList()
            else -> return SynResult.failure(nonEnumError())
        }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()

    val names = mutableListOf<LitStr>()
    for (v in variants) {
        val props = v.getVariantProperties().getOrElse { return SynResult.failure(it) }
        val preferred =
            props.getPreferredName(
                typeProperties.caseStyle,
                typeProperties.prefix,
                typeProperties.suffix,
            )
        names.add(preferred)
    }

    val output =
        quote(
            """
            #[automatically_derived]
            impl #impl_generics #strum_module_path::VariantNames for #name #ty_generics #where_clause {
                const VARIANTS: &'static [&'static str] = &[ #(#names),* ];
            }
            """.trimIndent(),
            "impl_generics" to implGenerics,
            "strum_module_path" to strumModulePath,
            "name" to name,
            "ty_generics" to tyGenerics,
            "where_clause" to whereClause,
            "names" to names,
        )
    return SynResult.success(output)
}
