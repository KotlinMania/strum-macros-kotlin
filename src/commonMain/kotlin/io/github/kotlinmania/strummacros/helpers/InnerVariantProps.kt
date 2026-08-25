// port-lint: source helpers/inner_variant_props.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.syn.Field
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.SynResult

internal interface HasInnerVariantProperties {
    fun getVariantInnerProperties(): SynResult<StrumInnerVariantProperties>
}

internal data class StrumInnerVariantProperties(
    var defaultWith: LitStr? = null,
)

internal fun Field.getVariantInnerProperties(): SynResult<StrumInnerVariantProperties> {
    val output = StrumInnerVariantProperties()
    var defaultWithKw: Ident? = null

    val namedMeta = when (val res = this.getNamedMetadata()) {
        is SynResult.Success -> res.value
        is SynResult.Failure -> return SynResult.failure(res.error)
    }

    for (meta in namedMeta) {
        when (meta) {
            is InnerVariantMeta.DefaultWith -> {
                val fst = defaultWithKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "default_with"))
                }
                defaultWithKw = meta.kw
                output.defaultWith = meta.value
            }
        }
    }

    return SynResult.success(output)
}
