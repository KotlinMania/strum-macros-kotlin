// port-lint: source macros/strings/mod.rs
package io.github.kotlinmania.strummacros.macros.strings

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.Variant

public class NonSingleFieldEnum : Exception()

public fun extractSingleFieldVariantAndThen(
    name: Ident,
    variant: Variant,
    returnValFn: (TokenStream) -> TokenStream,
): Result<TokenStream> {
    val variantIdent = variant.ident

    val patternAndReturn = when (val f = variant.fields) {
        is Fields.Unnamed -> {
            if (f.fields.unnamed.len() != 1) return Result.failure(NonSingleFieldEnum())
            val ident = quote("field0")
            val refKw = when (f.fields.unnamed.last()!!.ty) {
                is SynType.Reference -> quote("")
                else -> quote("ref")
            }
            val retVal = returnValFn(ident)
            quote("(#ref_kw #ident) => #ret_val", "ref_kw" to refKw, "ident" to ident, "ret_val" to retVal)
        }
        is Fields.Named -> {
            if (f.fields.named.len() != 1) return Result.failure(NonSingleFieldEnum())
            val field = f.fields.named.last()!!
            val refKw = when (field.ty) {
                is SynType.Reference -> quote("")
                else -> quote("ref")
            }
            val ident = quote("#ident", "ident" to field.ident!!)
            val retVal = returnValFn(ident)
            quote("{ #ref_kw #ident} => #ret_val", "ref_kw" to refKw, "ident" to ident, "ret_val" to retVal)
        }
        else -> return Result.failure(NonSingleFieldEnum())
    }

    return Result.success(quote("#name::#variant_ident #pattern_and_return", "name" to name, "variant_ident" to variantIdent, "pattern_and_return" to patternAndReturn))
}

