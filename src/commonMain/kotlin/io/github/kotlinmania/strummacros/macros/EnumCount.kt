// port-lint: source strum_macros/src/macros/enum_count.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.SynResult

internal fun enumCountInner(ast: DeriveInput): SynResult<TokenStream> {
    val variants = when (val data = ast.data) {
        is Data.Enum -> data.variants.toList()
        else -> return SynResult.failure(nonEnumError())
    }

    var n = 0
    for (v in variants) {
        val props = v.getVariantProperties().getOrElse { return SynResult.failure(it) }
        if (props.disabled == null) {
            n += 1
        }
    }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()

    val stream = quote(
        """
        #[automatically_derived]
        impl #impl_generics #strum_module_path::EnumCount for #name #ty_generics #where_clause {
            const COUNT: usize = #n;
        }
        """.trimIndent(),
        "impl_generics" to implGenerics,
        "strum_module_path" to strumModulePath,
        "name" to name,
        "ty_generics" to tyGenerics,
        "where_clause" to whereClause,
        "n" to n,
    )
    return SynResult.success(stream)
}
