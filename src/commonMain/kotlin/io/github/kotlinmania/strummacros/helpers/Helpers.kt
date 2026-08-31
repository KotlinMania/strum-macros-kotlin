// port-lint: source strum_macros/src/helpers/mod.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.syn.SynError

internal fun missingParseErrAttrError(): SynError =
    SynError.new(
        Span.callSite(),
        "`parse_err_ty` and `parse_err_fn` attributes are both required.",
    )

internal fun nonEnumError(): SynError =
    SynError.new(
        Span.callSite(),
        "This macro only supports enums.",
    )

internal fun nonUnitVariantError(): SynError =
    SynError.new(
        Span.callSite(),
        "This macro only supports enums of strictly unit variants. Consider using it in conjunction with EnumDiscriminants",
    )

internal fun nonSingleFieldVariantError(attr: String): SynError =
    SynError.new(
        Span.callSite(),
        "The [$attr] attribute only supports enum variants with a single field",
    )

internal fun strumDiscriminantsPassthroughError(span: Span): SynError =
    SynError.new(
        span,
        "expected a pass-through attribute, e.g. #[strum_discriminants(serde(rename = \"var0\"))]",
    )

internal fun occurrenceError(fst: Ident, snd: Ident, attr: String): SynError {
    val e = SynError.new(snd.span(), "Found multiple occurrences of strum($attr)")
    e.combine(SynError.new(fst.span(), "first one here"))
    return e
}

internal fun occurrenceError(fst: Span, snd: Span, attr: String): SynError {
    val e = SynError.new(snd, "Found multiple occurrences of strum($attr)")
    e.combine(SynError.new(fst, "first one here"))
    return e
}
