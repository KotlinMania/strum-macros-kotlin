// port-lint: source strum_macros/src/helpers/type_props.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathParse
import io.github.kotlinmania.syn.SynResult
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.parseStr

internal interface HasTypeProperties {
    fun getTypeProperties(): SynResult<StrumTypeProperties>
}

internal data class StrumTypeProperties(
    var parseErrTy: Path? = null,
    var parseErrFn: Path? = null,
    var caseStyle: CaseStyle? = null,
    var asciiCaseInsensitive: Boolean = false,
    var cratePath: Path? = null,
    val discriminantDerives: MutableList<Path> = mutableListOf(),
    var discriminantName: Ident? = null,
    val discriminantOthers: MutableList<Meta> = mutableListOf(),
    var discriminantVis: Visibility? = null,
    var usePhf: Boolean = false,
    var prefix: LitStr? = null,
    var suffix: LitStr? = null,
    var enumRepr: TokenStream? = null,
    var constIntoStr: Boolean = false,
) {
    fun crateModulePath(): Path =
        cratePath ?: parseStr(PathParse::parse, "::strum").getOrThrow()
}

internal fun DeriveInput.getTypeProperties(): SynResult<StrumTypeProperties> {
    val output = StrumTypeProperties()

    val strumMeta = this.getMetadata().getOrElse { return SynResult.failure(it) }
    val discriminantsMeta = this.getDiscriminantsMetadata().getOrElse { return SynResult.failure(it) }

    var parseErrTyKw: Ident? = null
    var parseErrFnKw: Ident? = null
    var serializeAllKw: Ident? = null
    var asciiCaseInsensitiveKw: Ident? = null
    var usePhfKw: Ident? = null
    var crateModulePathKw: Ident? = null
    var prefixKw: Ident? = null
    var suffixKw: Ident? = null
    var constIntoStrKw: Ident? = null

    for (meta in strumMeta) {
        when (meta) {
            is EnumMeta.SerializeAll -> {
                val fst = serializeAllKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "serialize_all"))
                }
                serializeAllKw = meta.kw
                output.caseStyle = meta.caseStyle
            }
            is EnumMeta.AsciiCaseInsensitive -> {
                val fst = asciiCaseInsensitiveKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "ascii_case_insensitive"))
                }
                asciiCaseInsensitiveKw = meta.kw
                output.asciiCaseInsensitive = true
            }
            is EnumMeta.UsePhf -> {
                val fst = usePhfKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "use_phf"))
                }
                usePhfKw = meta.kw
                output.usePhf = true
            }
            is EnumMeta.Crate -> {
                val fst = crateModulePathKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "Crate"))
                }
                crateModulePathKw = meta.kw
                output.cratePath = meta.crateModulePath
            }
            is EnumMeta.Prefix -> {
                val fst = prefixKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "prefix"))
                }
                prefixKw = meta.kw
                output.prefix = meta.prefix
            }
            is EnumMeta.Suffix -> {
                val fst = suffixKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "suffix"))
                }
                suffixKw = meta.kw
                output.suffix = meta.suffix
            }
            is EnumMeta.ParseErrTy -> {
                val fst = parseErrTyKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "parse_err_ty"))
                }
                parseErrTyKw = meta.kw
                output.parseErrTy = meta.path
            }
            is EnumMeta.ParseErrFn -> {
                val fst = parseErrFnKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "parse_err_fn"))
                }
                parseErrFnKw = meta.kw
                output.parseErrFn = meta.path
            }
            is EnumMeta.ConstIntoStr -> {
                val fst = constIntoStrKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "const_into_str"))
                }
                constIntoStrKw = meta.kw
                output.constIntoStr = true
            }
        }
    }

    var nameKw: Ident? = null
    var visKw: Ident? = null
    for (meta in discriminantsMeta) {
        when (meta) {
            is EnumDiscriminantsMeta.Derive -> {
                output.discriminantDerives.addAll(meta.paths)
            }
            is EnumDiscriminantsMeta.Name -> {
                val fst = nameKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "name"))
                }
                nameKw = meta.kw
                output.discriminantName = meta.name
            }
            is EnumDiscriminantsMeta.Vis -> {
                val fst = visKw
                if (fst != null) {
                    return SynResult.failure(occurrenceError(fst, meta.kw, "vis"))
                }
                visKw = meta.kw
                output.discriminantVis = meta.vis
            }
            is EnumDiscriminantsMeta.Other -> {
                output.discriminantOthers.add(meta.passthroughMeta)
            }
        }
    }

    for (attr in this.attrs) {
        val list = attr.meta.requireList()
        if (list is SynResult.Success) {
            val ident = list.value.path.getIdent()
            if (ident?.toString() == "repr") {
                output.enumRepr = list.value.tokens
            }
        }
    }

    return SynResult.success(output)
}
