// port-lint: source helpers/mod.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.syn.SynError

public fun missingParseErrAttrError(): SynError =
    SynError.new(
        Span.callSite(),
        "`parse_err_ty` and `parse_err_fn` attributes are both required.",
    )

public fun nonEnumError(): SynError =
    SynError.new(
        Span.callSite(),
        "This macro only supports enums.",
    )

public fun nonUnitVariantError(): SynError =
    SynError.new(
        Span.callSite(),
        "This macro only supports enums of strictly unit variants. Consider using it in conjunction with EnumDiscriminants",
    )

public fun nonSingleFieldVariantError(attr: String): SynError =
    SynError.new(
        Span.callSite(),
        "The [$attr] attribute only supports enum variants with a single field",
    )

public fun strumDiscriminantsPassthroughError(span: Span): SynError =
    SynError.new(
        span,
        "expected a pass-through attribute, e.g. #[strum_discriminants(serde(rename = \"var0\"))]",
    )

public fun occurrenceError(fst: ToTokens, snd: ToTokens, attr: String): SynError {
    val e = SynError.newSpanned(
        snd,
        "Found multiple occurrences of strum($attr)",
    )
    e.combine(SynError.newSpanned(fst, "first one here"))
    return e
}