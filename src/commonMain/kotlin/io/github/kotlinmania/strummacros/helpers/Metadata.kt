// port-lint: source helpers/metadata.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.CustomKeywordParse
import io.github.kotlinmania.syn.CustomKeywordPeek
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Expr
import io.github.kotlinmania.syn.Field
import io.github.kotlinmania.syn.IdentParse
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.LitBoolParse
import io.github.kotlinmania.syn.LitParse
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.LitStrParse
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.MetaParse
import io.github.kotlinmania.syn.ParseStream
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathParse
import io.github.kotlinmania.syn.SynResult
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.VisibilityParse
import io.github.kotlinmania.syn.identParseAny
import io.github.kotlinmania.syn.parenthesized
import io.github.kotlinmania.syn.parseStr
import io.github.kotlinmania.syn.CommaParse
import io.github.kotlinmania.syn.CommaPeek
import io.github.kotlinmania.syn.CrateParse
import io.github.kotlinmania.syn.CratePeek
import io.github.kotlinmania.syn.EqParse
import io.github.kotlinmania.syn.EqPeek

internal object Kw {
    val serializeAllPeek = CustomKeywordPeek("serialize_all")
    val serializeAllParse = CustomKeywordParse("serialize_all")
    val constIntoStrPeek = CustomKeywordPeek("const_into_str")
    val constIntoStrParse = CustomKeywordParse("const_into_str")
    val usePhfPeek = CustomKeywordPeek("use_phf")
    val usePhfParse = CustomKeywordParse("use_phf")
    val prefixPeek = CustomKeywordPeek("prefix")
    val prefixParse = CustomKeywordParse("prefix")
    val suffixPeek = CustomKeywordPeek("suffix")
    val suffixParse = CustomKeywordParse("suffix")
    val parseErrTyPeek = CustomKeywordPeek("parse_err_ty")
    val parseErrTyParse = CustomKeywordParse("parse_err_ty")
    val parseErrFnPeek = CustomKeywordPeek("parse_err_fn")
    val parseErrFnParse = CustomKeywordParse("parse_err_fn")

    val derivePeek = CustomKeywordPeek("derive")
    val deriveParse = CustomKeywordParse("derive")
    val namePeek = CustomKeywordPeek("name")
    val nameParse = CustomKeywordParse("name")
    val visPeek = CustomKeywordPeek("vis")
    val visParse = CustomKeywordParse("vis")
    val docPeek = CustomKeywordPeek("doc")
    val docParse = CustomKeywordParse("doc")

    val messagePeek = CustomKeywordPeek("message")
    val messageParse = CustomKeywordParse("message")
    val detailedMessagePeek = CustomKeywordPeek("detailed_message")
    val detailedMessageParse = CustomKeywordParse("detailed_message")
    val serializePeek = CustomKeywordPeek("serialize")
    val serializeParse = CustomKeywordParse("serialize")
    val toStringPeek = CustomKeywordPeek("to_string")
    val toStringParse = CustomKeywordParse("to_string")
    val transparentPeek = CustomKeywordPeek("transparent")
    val transparentParse = CustomKeywordParse("transparent")
    val disabledPeek = CustomKeywordPeek("disabled")
    val disabledParse = CustomKeywordParse("disabled")
    val defaultPeek = CustomKeywordPeek("default")
    val defaultParse = CustomKeywordParse("default")
    val defaultWithPeek = CustomKeywordPeek("default_with")
    val defaultWithParse = CustomKeywordParse("default_with")
    val propsPeek = CustomKeywordPeek("props")
    val propsParse = CustomKeywordParse("props")
    val asciiCaseInsensitivePeek = CustomKeywordPeek("ascii_case_insensitive")
    val asciiCaseInsensitiveParse = CustomKeywordParse("ascii_case_insensitive")
}

internal sealed class EnumMeta {
    data class SerializeAll(val kw: Ident, val caseStyle: CaseStyle) : EnumMeta()
    data class AsciiCaseInsensitive(val kw: Ident) : EnumMeta()
    data class Crate(val kw: Ident, val crateModulePath: Path) : EnumMeta()
    data class UsePhf(val kw: Ident) : EnumMeta()
    data class Prefix(val kw: Ident, val prefix: LitStr) : EnumMeta()
    data class Suffix(val kw: Ident, val suffix: LitStr) : EnumMeta()
    data class ParseErrTy(val kw: Ident, val path: Path) : EnumMeta()
    data class ParseErrFn(val kw: Ident, val path: Path) : EnumMeta()
    data class ConstIntoStr(val kw: Ident) : EnumMeta()

    companion object {
        fun parse(input: ParseStream): SynResult<EnumMeta> {
            return when {
                input.peek(Kw.serializeAllPeek) -> {
                    val kw = Kw.serializeAllParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val caseStyle = CaseStyle.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(SerializeAll(kw, caseStyle))
                }
                input.peek(CratePeek) -> {
                    val kw = CrateParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val pathStr = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val crateModulePath = parseStr(PathParse::parse, pathStr.value()).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Crate(Ident.new("crate", kw.span), crateModulePath))
                }
                input.peek(Kw.asciiCaseInsensitivePeek) -> {
                    val kw = Kw.asciiCaseInsensitiveParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(AsciiCaseInsensitive(kw))
                }
                input.peek(Kw.usePhfPeek) -> {
                    val kw = Kw.usePhfParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(UsePhf(kw))
                }
                input.peek(Kw.prefixPeek) -> {
                    val kw = Kw.prefixParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val prefix = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Prefix(kw, prefix))
                }
                input.peek(Kw.suffixPeek) -> {
                    val kw = Kw.suffixParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val suffix = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Suffix(kw, suffix))
                }
                input.peek(Kw.parseErrTyPeek) -> {
                    val kw = Kw.parseErrTyParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(ParseErrTy(kw, path))
                }
                input.peek(Kw.parseErrFnPeek) -> {
                    val kw = Kw.parseErrFnParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(ParseErrFn(kw, path))
                }
                input.peek(Kw.constIntoStrPeek) -> {
                    val kw = Kw.constIntoStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(ConstIntoStr(kw))
                }
                else -> SynResult.failure(input.error("expected enum metadata"))
            }
        }
    }
}

internal sealed class EnumDiscriminantsMeta {
    data class Derive(val kw: Ident, val paths: List<Path>) : EnumDiscriminantsMeta()
    data class Name(val kw: Ident, val name: Ident) : EnumDiscriminantsMeta()
    data class Vis(val kw: Ident, val vis: Visibility) : EnumDiscriminantsMeta()
    data class Other(val passthroughMeta: Meta) : EnumDiscriminantsMeta()

    companion object {
        fun parse(input: ParseStream): SynResult<EnumDiscriminantsMeta> {
            return when {
                input.peek(Kw.derivePeek) -> {
                    val kw = Kw.deriveParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val paren = parenthesized(input).getOrElse { return SynResult.failure(it) }
                    val paths = mutableListOf<Path>()
                    while (!paren.content.isEmpty()) {
                        val path = PathParse.parse(paren.content).getOrElse { return SynResult.failure(it) }
                        paths.add(path)
                        if (paren.content.peek(CommaPeek)) {
                            CommaParse.parse(paren.content).getOrElse { return SynResult.failure(it) }
                        } else {
                            break
                        }
                    }
                    paren.content.finishChildBuffer()
                    SynResult.success(Derive(kw, paths))
                }
                input.peek(Kw.namePeek) -> {
                    val kw = Kw.nameParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val paren = parenthesized(input).getOrElse { return SynResult.failure(it) }
                    val name = IdentParse.parse(paren.content).getOrElse { return SynResult.failure(it) }
                    paren.content.finishChildBuffer()
                    SynResult.success(Name(kw, name))
                }
                input.peek(Kw.visPeek) -> {
                    val kw = Kw.visParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val paren = parenthesized(input).getOrElse { return SynResult.failure(it) }
                    val vis = VisibilityParse.parse(paren.content).getOrElse { return SynResult.failure(it) }
                    paren.content.finishChildBuffer()
                    SynResult.success(Vis(kw, vis))
                }
                else -> {
                    val passthroughMeta = MetaParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Other(passthroughMeta))
                }
            }
        }
    }
}

internal interface DeriveInputExt {
    fun getMetadata(): SynResult<List<EnumMeta>>
    fun getDiscriminantsMetadata(): SynResult<List<EnumDiscriminantsMeta>>
}

internal fun DeriveInput.getMetadata(): SynResult<List<EnumMeta>> =
    getMetadataInner("strum", this.attrs, EnumMeta::parse)

internal fun DeriveInput.getDiscriminantsMetadata(): SynResult<List<EnumDiscriminantsMeta>> =
    getMetadataInner("strum_discriminants", this.attrs, EnumDiscriminantsMeta::parse)

internal sealed class VariantMeta {
    data class Message(val kw: Ident, val value: LitStr) : VariantMeta()
    data class DetailedMessage(val kw: Ident, val value: LitStr) : VariantMeta()
    data class Serialize(val kw: Ident, val value: LitStr) : VariantMeta()
    data class Documentation(val value: LitStr) : VariantMeta()
    data class ToStringMeta(val kw: Ident, val value: LitStr) : VariantMeta()
    data class Transparent(val kw: Ident) : VariantMeta()
    data class Disabled(val kw: Ident) : VariantMeta()
    data class Default(val kw: Ident) : VariantMeta()
    data class DefaultWith(val kw: Ident, val value: LitStr) : VariantMeta()
    data class AsciiCaseInsensitive(val kw: Ident, val value: Boolean) : VariantMeta()
    data class Props(val kw: Ident, val props: List<Pair<LitStr, Lit>>) : VariantMeta()

    companion object {
        fun parse(input: ParseStream): SynResult<VariantMeta> {
            return when {
                input.peek(Kw.messagePeek) -> {
                    val kw = Kw.messageParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val value = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Message(kw, value))
                }
                input.peek(Kw.detailedMessagePeek) -> {
                    val kw = Kw.detailedMessageParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val value = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(DetailedMessage(kw, value))
                }
                input.peek(Kw.serializePeek) -> {
                    val kw = Kw.serializeParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val value = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Serialize(kw, value))
                }
                input.peek(Kw.toStringPeek) -> {
                    val kw = Kw.toStringParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val value = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(ToStringMeta(kw, value))
                }
                input.peek(Kw.transparentPeek) -> {
                    val kw = Kw.transparentParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Transparent(kw))
                }
                input.peek(Kw.disabledPeek) -> {
                    val kw = Kw.disabledParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Disabled(kw))
                }
                input.peek(Kw.defaultPeek) -> {
                    val kw = Kw.defaultParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Default(kw))
                }
                input.peek(Kw.defaultWithPeek) -> {
                    val kw = Kw.defaultWithParse.parse(input).getOrElse { return SynResult.failure(it) }
                    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val value = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(DefaultWith(kw, value))
                }
                input.peek(Kw.asciiCaseInsensitivePeek) -> {
                    val kw = Kw.asciiCaseInsensitiveParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val value = if (input.peek(EqPeek)) {
                        EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                        LitBoolParse.parse(input).getOrElse { return SynResult.failure(it) }.value()
                    } else {
                        true
                    }
                    SynResult.success(AsciiCaseInsensitive(kw, value))
                }
                input.peek(Kw.propsPeek) -> {
                    val kw = Kw.propsParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val paren = parenthesized(input).getOrElse { return SynResult.failure(it) }
                    val propList = mutableListOf<Prop>()
                    while (!paren.content.isEmpty()) {
                        val prop = parseProp(paren.content).getOrElse { return SynResult.failure(it) }
                        propList.add(prop)
                        if (paren.content.peek(CommaPeek)) {
                            CommaParse.parse(paren.content).getOrElse { return SynResult.failure(it) }
                        } else {
                            break
                        }
                    }
                    paren.content.finishChildBuffer()
                    val props = propList.map { prop -> LitStr.new(prop.ident.toString(), prop.ident.span()) to prop.lit }.toList()
                    SynResult.success(Props(kw, props))
                }
                else -> SynResult.failure(input.error("expected variant metadata"))
            }
        }
    }
}

internal data class Prop(val ident: Ident, val lit: Lit)

private fun parseProp(input: ParseStream): SynResult<Prop> {
    val k = identParseAny(input).getOrElse { return SynResult.failure(it) }
    EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val v = LitParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Prop(k, v))
}

internal interface VariantExt {
    fun getMetadata(): SynResult<List<VariantMeta>>
}

internal fun Variant.getMetadata(): SynResult<List<VariantMeta>> {
    val result = when (val res = getMetadataInner("strum", this.attrs, VariantMeta::parse)) {
        is SynResult.Success -> res.value.toMutableList()
        is SynResult.Failure -> return res
    }

    for (attr in this.attrs) {
        if (attr.meta.path().isIdent("doc")) {
            val meta = attr.meta
            if (meta is Meta.NameValue) {
                val expr = meta.value
                if (expr is Expr.Lit && expr.lit is Lit.Str) {
                    result.add(VariantMeta.Documentation((expr.lit as Lit.Str).value))
                }
            }
        }
    }
    return SynResult.success(result)
}

internal sealed class InnerVariantMeta {
    data class DefaultWith(val kw: Ident, val value: LitStr) : InnerVariantMeta()

    companion object {
        fun parse(input: ParseStream): SynResult<InnerVariantMeta> {
            return if (input.peek(Kw.defaultWithPeek)) {
                val kw = Kw.defaultWithParse.parse(input).getOrElse { return SynResult.failure(it) }
                EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                val value = LitStrParse.parse(input).getOrElse { return SynResult.failure(it) }
                SynResult.success(DefaultWith(kw, value))
            } else {
                SynResult.failure(input.error("expected inner variant metadata"))
            }
        }
    }
}

internal interface InnerVariantExt {
    fun getNamedMetadata(): SynResult<List<InnerVariantMeta>>
}

internal fun Field.getNamedMetadata(): SynResult<List<InnerVariantMeta>> {
    return getMetadataInner("strum", this.attrs, InnerVariantMeta::parse)
}

private fun <T> getMetadataInner(
    ident: String,
    attrs: Iterable<Attribute>,
    parser: (ParseStream) -> SynResult<T>,
): SynResult<List<T>> {
    val list = mutableListOf<T>()
    for (attr in attrs) {
        if (attr.path().isIdent(ident)) {
            val parsed = attr.parseArgsWith { input ->
                val items = mutableListOf<T>()
                while (!input.isEmpty()) {
                    val item = parser(input).getOrElse { return@parseArgsWith SynResult.failure(it) }
                    items.add(item)
                    if (input.peek(CommaPeek)) {
                        CommaParse.parse(input).getOrElse { return@parseArgsWith SynResult.failure(it) }
                    } else {
                        break
                    }
                }
                SynResult.success(items)
            }.getOrElse { return SynResult.failure(it) }
            list.addAll(parsed)
        }
    }
    return SynResult.success(list)
}
