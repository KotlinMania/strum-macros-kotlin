// port-lint: source macros/enum_properties.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.SynResult

private enum class PropertyType {
    String,
    Integer,
    Bool,
}

private val PROPERTY_TYPES: List<PropertyType> = listOf(
    PropertyType.String,
    PropertyType.Integer,
    PropertyType.Bool,
)

internal fun enumPropertiesInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val variants = when (val data = ast.data) {
        is Data.Enum -> data.variants.toList()
        else -> return SynResult.failure(nonEnumError())
    }
    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()

    val builtArms: MutableMap<PropertyType, MutableList<TokenStream>> = PROPERTY_TYPES.associateWith { mutableListOf<TokenStream>() }.toMutableMap()

    for (variant in variants) {
        val ident = variant.ident
        val variantProperties = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }
        val arms: MutableMap<PropertyType, MutableList<TokenStream>> = PROPERTY_TYPES.associateWith { mutableListOf<TokenStream>() }.toMutableMap()

        if (variantProperties.disabled != null) {
            continue
        }

        val params = when (variant.fields) {
            is Fields.Unit -> quote("")
            is Fields.Unnamed -> quote("(..)")
            is Fields.Named -> quote("{..}")
        }

        for ((key, value) in variantProperties.props) {
            val propertyType = when (value) {
                is Lit.Str -> PropertyType.String
                is Lit.Bool -> PropertyType.Bool
                is Lit.Int -> PropertyType.Integer
                else -> error("unsupported property literal type")
            }

            arms[propertyType]!!.add(quote("#key => ::core::option::Option::Some( #value )", "key" to key, "value" to value))
        }

        for (property in PROPERTY_TYPES) {
            arms[property]!!.add(quote("_ => ::core::option::Option::None"))
            val armsAsString = arms[property]!!
            builtArms[property]!!.add(
                quote(
                    """
                    &#name::#ident #params => {
                        match prop {
                            #(#arms_as_string),*
                        }
                    }
                    """.trimIndent(),
                    "name" to name,
                    "ident" to ident,
                    "params" to params,
                    "arms_as_string" to armsAsString,
                ),
            )
        }
    }

    for ((_, armsList) in builtArms) {
        if (armsList.size < variants.size) {
            armsList.add(quote("_ => ::core::option::Option::None"))
        }
    }

    val builtStringArms = builtArms[PropertyType.String]!!
    val builtIntArms = builtArms[PropertyType.Integer]!!
    val builtBoolArms = builtArms[PropertyType.Bool]!!

    val output = quote(
        """
        #[automatically_derived]
        impl #impl_generics #strum_module_path::EnumProperty for #name #ty_generics #where_clause {
            #[inline]
            fn get_str(&self, prop: &str) -> ::core::option::Option<&'static str> {
                match self {
                    #(#built_string_arms),*
                }
            }

            #[inline]
            fn get_int(&self, prop: &str) -> ::core::option::Option<i64> {
                match self {
                    #(#built_int_arms),*
                }
            }

            #[inline]
            fn get_bool(&self, prop: &str) -> ::core::option::Option<bool> {
                match self {
                    #(#built_bool_arms),*
                }
            }

        }
        """.trimIndent(),
        "impl_generics" to implGenerics,
        "strum_module_path" to strumModulePath,
        "name" to name,
        "ty_generics" to tyGenerics,
        "where_clause" to whereClause,
        "built_string_arms" to builtStringArms,
        "built_int_arms" to builtIntArms,
        "built_bool_arms" to builtBoolArms,
    )

    return SynResult.success(output)
}
