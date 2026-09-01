// port-lint: source macros/enum_variant_array.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.nonUnitVariantError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.SynResult

internal fun staticVariantsArrayInner(ast: DeriveInput): SynResult<TokenStream> {
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

    val idents = mutableListOf<Ident>()
    for (v in variants) {
        when (v.fields) {
            is Fields.Unit -> idents.add(v.ident)
            else -> return SynResult.failure(nonUnitVariantError())
        }
    }

    val output =
        quote(
            """
            #[automatically_derived]
            impl #impl_generics #strum_module_path::VariantArray for #name #ty_generics #where_clause {
                const VARIANTS: &'static [Self] = &[ #(#name::#idents),* ];
            }
            """.trimIndent(),
            "impl_generics" to implGenerics,
            "strum_module_path" to strumModulePath,
            "name" to name,
            "ty_generics" to tyGenerics,
            "where_clause" to whereClause,
            "idents" to idents,
        )
    return SynResult.success(output)
}
