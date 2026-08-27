// port-lint: source strum_macros/src/macros/enum_discriminants.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.strumDiscriminantsPassthroughError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathParse
import io.github.kotlinmania.syn.SynResult
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.parseStr

private val ATTRIBUTES_TO_COPY: List<String> = listOf("doc", "cfg", "allow", "deny", "strum_discriminants")

internal fun enumDiscriminantsInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val vis = ast.vis

    val variants = when (val data = ast.data) {
        is Data.Enum -> data.variants.toList()
        else -> return SynResult.failure(nonEnumError())
    }

    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()

    val derives = typeProperties.discriminantDerives.toMutableList()
    val discriminants = mutableListOf<TokenStream>()

    var hasDefaultVariant = false
    for (variant in variants) {
        val ident = variant.ident
        var hasDefault = false

        val discriminant = variant.discriminant?.let { eqExpr ->
            val expr = eqExpr.expr
            quote("= #expr", "expr" to expr)
        }

        val attrs = mutableListOf<TokenStream>()
        for (attr in variant.attrs) {
            if (attr.path().isIdent("default")) {
                hasDefault = true
                hasDefaultVariant = true
            }

            if (ATTRIBUTES_TO_COPY.none { whitelisted -> attr.path().isIdent(whitelisted) }) {
                continue
            }

            if (attr.path().isIdent("strum_discriminants")) {
                val listMeta = attr.meta.requireList().getOrElse { return SynResult.failure(it) }
                val ts = listMeta.toTokenStream().toList()
                if (ts.size < 2) {
                    return SynResult.failure(strumDiscriminantsPassthroughError(attr.bracketToken.span.join()))
                }
                val passthroughGroup = ts[1]
                val passthroughAttribute = when (passthroughGroup) {
                    is TokenTree.Group -> passthroughGroup.value.stream()
                    else -> return SynResult.failure(strumDiscriminantsPassthroughError(passthroughGroup.span()))
                }
                if (passthroughAttribute.toList().isEmpty()) {
                    return SynResult.failure(strumDiscriminantsPassthroughError(passthroughGroup.span()))
                }
                attrs.add(quote("#[#passthroughAttribute]", "passthroughAttribute" to passthroughAttribute))
                continue
            }

            attrs.add(attr.toTokenStream())
        }

        val defaultAttr = if (hasDefault) {
            quote("#[default]")
        } else {
            TokenStream.new()
        }

        val discriminantTokens = TokenStream.new()
        defaultAttr.toTokens(discriminantTokens)
        for (a in attrs) {
            a.toTokens(discriminantTokens)
        }
        ident.toTokens(discriminantTokens)
        discriminant?.toTokens(discriminantTokens)
        discriminants.add(discriminantTokens)
    }

    if (hasDefaultVariant) {
        derives.add(parseStr(PathParse::parse, "::core::default::Default").getOrThrow())
    }

    val derivesTokens = quote(
        "#[derive(Clone, Copy, Debug, PartialEq, Eq, #(#derives),*)]",
        "derives" to derives,
    )

    val defaultName = Ident.new("${name}Discriminants", Span.callSite())
    val discriminantsName = typeProperties.discriminantName ?: defaultName
    val discriminantsVis = typeProperties.discriminantVis ?: vis

    val passThroughAttributes = typeProperties.discriminantOthers.toMutableList()
    val hasDoc = passThroughAttributes.any { it.path().isIdent("doc") }
    val passThroughTokens: MutableList<TokenStream> = passThroughAttributes.map { it.toTokenStream() }.toMutableList()
    if (!hasDoc) {
        passThroughTokens.add(quote("""doc = "Auto-generated discriminant enum variants""""))
    }

    val reprTokens = typeProperties.enumRepr?.let { repr ->
        quote("#[repr(#repr)]", "repr" to repr)
    }

    val arms = variants.map { variant ->
        val ident = variant.ident
        val params = when (variant.fields) {
            is Fields.Unit -> quote("")
            is Fields.Unnamed -> quote("(..)")
            is Fields.Named -> quote("{ .. }")
        }
        quote(
            "#name::#ident #params => #discriminantsName::#ident",
            "name" to name,
            "ident" to ident,
            "params" to params,
            "discriminantsName" to discriminantsName,
        )
    }

    val fromFnBody = if (variants.isEmpty()) {
        quote("unreachable!()")
    } else {
        quote(
            "match val { #(#arms),* }",
            "arms" to arms,
        )
    }

    val (implGenerics, tyGenerics, whereClause) = ast.generics.splitForImpl()
    val implFrom = quote(
        """
        #[automatically_derived]
        impl #implGenerics ::core::convert::From< #name #tyGenerics > for #discriminantsName #whereClause {
            #[inline]
            fn from(val: #name #tyGenerics) -> #discriminantsName {
                #fromFnBody
            }
        }
        """.trimIndent(),
        "implGenerics" to implGenerics,
        "name" to name,
        "tyGenerics" to tyGenerics,
        "discriminantsName" to discriminantsName,
        "whereClause" to whereClause,
        "fromFnBody" to fromFnBody,
    )

    val implFromRef = run {
        val generics = ast.generics.copy()
        val lifetime = GenericParam.LifetimeParam.new(Lifetime.new("'_enum", Span.callSite()))
        val enumLife = quote("& #lifetime", "lifetime" to lifetime)
        generics.params.pushValue(lifetime)
        val (refImplGenerics, _, _) = generics.splitForImpl()

        quote(
            """
            #[automatically_derived]
            impl #refImplGenerics ::core::convert::From< #enumLife #name #tyGenerics > for #discriminantsName #whereClause {
                #[inline]
                fn from(val: #enumLife #name #tyGenerics) -> #discriminantsName {
                    #fromFnBody
                }
            }
            """.trimIndent(),
            "refImplGenerics" to refImplGenerics,
            "enumLife" to enumLife,
            "name" to name,
            "tyGenerics" to tyGenerics,
            "discriminantsName" to discriminantsName,
            "whereClause" to whereClause,
            "fromFnBody" to fromFnBody,
        )
    }

    val implIntoDiscriminant = when (typeProperties.discriminantVis) {
        null, is Visibility.Public -> quote(
            """
            #[automatically_derived]
            impl #implGenerics #strumModulePath::IntoDiscriminant for #name #tyGenerics #whereClause {
                type Discriminant = #discriminantsName;
 
                #[inline]
                fn discriminant(&self) -> Self::Discriminant {
                    <Self::Discriminant as ::core::convert::From<&Self>>::from(self)
                }
            }
            """.trimIndent(),
            "implGenerics" to implGenerics,
            "strumModulePath" to strumModulePath,
            "name" to name,
            "tyGenerics" to tyGenerics,
            "whereClause" to whereClause,
            "discriminantsName" to discriminantsName,
        )
        else -> TokenStream.new()
    }

    val output = quote(
        """
        #derives
        #repr
        #(#[ #passThroughAttributes ])*
        #discriminantsVis enum #discriminantsName {
            #(#discriminants),*
        }

        #implIntoDiscriminant
        #implFrom
        #implFromRef
        """.trimIndent(),
        "derives" to derivesTokens,
        "repr" to reprTokens,
        "passThroughAttributes" to passThroughTokens,
        "discriminantsVis" to discriminantsVis,
        "discriminantsName" to discriminantsName,
        "discriminants" to discriminants,
        "implIntoDiscriminant" to implIntoDiscriminant,
        "implFrom" to implFrom,
        "implFromRef" to implFromRef,
    )

    return SynResult.success(output)
}
