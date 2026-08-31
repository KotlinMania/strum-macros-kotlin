// port-lint: source strum_macros/src/helpers/variant_props.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.SynResult
import io.github.kotlinmania.syn.Variant

internal interface HasStrumVariantProperties {
    fun getVariantProperties(): SynResult<StrumVariantProperties>
}

internal data class StrumVariantProperties(
    var transparent: Ident? = null,
    var disabled: Ident? = null,
    var default: Ident? = null,
    var defaultWith: LitStr? = null,
    var asciiCaseInsensitive: Boolean? = null,
    var message: LitStr? = null,
    var detailedMessage: LitStr? = null,
    val documentation: MutableList<LitStr> = mutableListOf(),
    val props: MutableList<Pair<LitStr, Lit>> = mutableListOf(),
    val serialize: MutableList<LitStr> = mutableListOf(),
    var toStringValue: LitStr? = null,
    var ident: Ident? = null,
) {
    fun identAsStr(caseStyle: CaseStyle?): LitStr {
        val id = checkNotNull(ident) { "identifier" }
        return LitStr.new(id.convertCase(caseStyle), id.span())
    }

    fun getPreferredName(
        caseStyle: CaseStyle?,
        prefix: LitStr?,
        suffix: LitStr?,
    ): LitStr {
        var output = toStringValue ?: serialize.maxByOrNull { it.value().length } ?: identAsStr(caseStyle)

        if (prefix != null) {
            output = LitStr.new(prefix.value() + output.value(), output.span())
        }

        if (suffix != null) {
            output = LitStr.new(output.value() + suffix.value(), output.span())
        }

        return output
    }

    fun getSerializations(caseStyle: CaseStyle?): List<LitStr> {
        val attrs = serialize.toMutableList()
        val toStr = toStringValue
        if (toStr != null) {
            attrs.add(toStr)
        }
        if (attrs.isEmpty()) {
            attrs.add(identAsStr(caseStyle))
        }
        return attrs
    }
}

internal fun Variant.getVariantProperties(): SynResult<StrumVariantProperties> {
    val output = StrumVariantProperties(ident = this.ident)

    var messageKw: Ident? = null
    var detailedMessageKw: Ident? = null
    var transparentKw: Ident? = null
    var disabledKw: Ident? = null
    var defaultKw: Ident? = null
    var defaultWithKw: Ident? = null
    var toStringKw: Ident? = null
    var asciiCaseInsensitiveKw: Ident? = null

    val metaList = this.getMetadata().getOrElse { return SynResult.failure(it) }
    for (meta in metaList) {
        when (meta) {
            is VariantMeta.Message -> {
                val fst = messageKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "message"))
                }
                messageKw = meta.kw
                output.message = meta.value
            }
            is VariantMeta.DetailedMessage -> {
                val fst = detailedMessageKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "detailed_message"))
                }
                detailedMessageKw = meta.kw
                output.detailedMessage = meta.value
            }
            is VariantMeta.Documentation -> {
                output.documentation.add(meta.value)
            }
            is VariantMeta.Serialize -> {
                output.serialize.add(meta.value)
            }
            is VariantMeta.ToStringMeta -> {
                val fst = toStringKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "to_string"))
                }
                toStringKw = meta.kw
                output.toStringValue = meta.value
            }
            is VariantMeta.Transparent -> {
                val fst = transparentKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "transparent"))
                }
                transparentKw = meta.kw
                output.transparent = meta.kw
            }
            is VariantMeta.Disabled -> {
                val fst = disabledKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "disabled"))
                }
                disabledKw = meta.kw
                output.disabled = meta.kw
            }
            is VariantMeta.Default -> {
                val fst = defaultKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "default"))
                }
                defaultKw = meta.kw
                output.default = meta.kw
            }
            is VariantMeta.DefaultWith -> {
                val fst = defaultWithKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "default_with"))
                }
                defaultWithKw = meta.kw
                output.defaultWith = meta.value
            }
            is VariantMeta.AsciiCaseInsensitive -> {
                val fst = asciiCaseInsensitiveKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "ascii_case_insensitive"))
                }
                asciiCaseInsensitiveKw = meta.kw
                output.asciiCaseInsensitive = meta.value
            }
            is VariantMeta.Props -> {
                output.props.addAll(meta.props)
            }
        }
    }

    return SynResult.success(output)
}
