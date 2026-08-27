// port-lint: source macros/enum_iter.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getTypeProperties
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.SynError
import io.github.kotlinmania.syn.SynResult
import io.github.kotlinmania.syn.parseStr

internal fun enumIterInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val gen = ast.generics
    val (implGenerics, tyGenerics, whereClause) = gen.splitForImpl()
    val vis = ast.vis
    val typeProperties = ast.getTypeProperties().getOrElse { return SynResult.failure(it) }
    val strumModulePath = typeProperties.crateModulePath()
    val docComment = "An iterator over the variants of [$name]"

    if (gen.lifetimes().count() > 0) {
        return SynResult.failure(
            SynError.new(
                Span.callSite(),
                "This macro doesn't support enums with lifetimes. The resulting enums would be unbounded.",
            ),
        )
    }

    val typeParamList = gen.typeParams().map { it.ident }.toList()
    val phantomData =
        if (typeParamList.isNotEmpty()) {
            quote("< fn() -> ( #(#g),* ) >", "g" to typeParamList)
        } else {
            quote("< fn() -> () >")
        }

    val variants =
        when (val data = ast.data) {
            is Data.Enum -> data.variants.toList()
            else -> return SynResult.failure(nonEnumError())
        }

    val arms = mutableListOf<TokenStream>()
    var idx = 0
    for (variant in variants) {
        val props = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }
        if (props.disabled != null) {
            continue
        }

        val ident = variant.ident
        val params =
            when (val fields = variant.fields) {
                is Fields.Unit -> quote("")
                is Fields.Unnamed -> {
                    val defaults = List(fields.fields.unnamed.len()) { quote("::core::default::Default::default()") }
                    quote("(#(#defaults),*)", "defaults" to defaults)
                }
                is Fields.Named -> {
                    val fieldIdents =
                        fields.fields.named
                            .toList()
                            .mapNotNull { it.ident }
                    val fieldAssignments = fieldIdents.map { f -> quote("#f: ::core::default::Default::default()", "f" to f) }
                    quote("{#(#field_assignments),*}", "field_assignments" to fieldAssignments)
                }
            }

        arms.add(quote("#idx => ::core::option::Option::Some(#name::#ident #params)", "idx" to idx, "name" to name, "ident" to ident, "params" to params))
        idx += 1
    }

    val variantCount = arms.size
    arms.add(quote("_ => ::core::option::Option::None"))
    val iterName = parseStr(io.github.kotlinmania.syn.IdentParse::parse, "${name}Iter").getOrElse { return SynResult.failure(it) }
    val iterNameDebugStruct = parseStr(io.github.kotlinmania.syn.LitStrParse::parse, "\"$iterName\"").getOrElse { return SynResult.failure(it) }

    val output =
        quote(
            """
            #[doc = #doc_comment]
            #[allow(
                missing_copy_implementations,
            )]
            #vis struct #iter_name #impl_generics {
                idx: usize,
                back_idx: usize,
                marker: ::core::marker::PhantomData #phantom_data,
            }

            #[automatically_derived]
            impl #impl_generics ::core::fmt::Debug for #iter_name #ty_generics #where_clause {
                fn fmt(&self, f: &mut ::core::fmt::Formatter<'_>) -> ::core::fmt::Result {
                    f.debug_struct(#iter_name_debug_struct)
                        .field("len", &self.len())
                        .finish()
                }
            }

            #[automatically_derived]
            impl #impl_generics #iter_name #ty_generics #where_clause {
                fn get(&self, idx: usize) -> ::core::option::Option<#name #ty_generics> {
                    match idx {
                        #(#arms),*
                    }
                }
            }

            #[automatically_derived]
            impl #impl_generics #strum_module_path::IntoEnumIterator for #name #ty_generics #where_clause {
                type Iterator = #iter_name #ty_generics;

                #[inline]
                fn iter() -> #iter_name #ty_generics {
                    #iter_name {
                        idx: 0,
                        back_idx: 0,
                        marker: ::core::marker::PhantomData,
                    }
                }
            }

            #[automatically_derived]
            impl #impl_generics ::core::iter::Iterator for #iter_name #ty_generics #where_clause {
                type Item = #name #ty_generics;

                #[inline]
                fn next(&mut self) -> ::core::option::Option<<Self as Iterator>::Item> {
                    self.nth(0)
                }

                #[inline]
                fn size_hint(&self) -> (usize, ::core::option::Option<usize>) {
                    let t = if self.idx + self.back_idx >= #variant_count { 0 } else { #variant_count - self.idx - self.back_idx };
                    (t, ::core::option::Option::Some(t))
                }

                #[inline]
                fn nth(&mut self, n: usize) -> ::core::option::Option<<Self as Iterator>::Item> {
                    let idx = self.idx + n + 1;
                    if idx + self.back_idx > #variant_count {
                        self.idx = #variant_count;
                        ::core::option::Option::None
                    } else {
                        self.idx = idx;
                        #iter_name::get(self, idx - 1)
                    }
                }
            }

            #[automatically_derived]
            impl #impl_generics ::core::iter::ExactSizeIterator for #iter_name #ty_generics #where_clause {
                #[inline]
                fn len(&self) -> usize {
                    self.size_hint().0
                }
            }

            #[automatically_derived]
            impl #impl_generics ::core::iter::DoubleEndedIterator for #iter_name #ty_generics #where_clause {
                #[inline]
                fn next_back(&mut self) -> ::core::option::Option<<Self as Iterator>::Item> {
                    let back_idx = self.back_idx + 1;

                    if self.idx + back_idx > #variant_count {
                        self.back_idx = #variant_count;
                        ::core::option::Option::None
                    } else {
                        self.back_idx = back_idx;
                        #iter_name::get(self, #variant_count - self.back_idx)
                    }
                }
            }

            #[automatically_derived]
            impl #impl_generics ::core::iter::FusedIterator for #iter_name #ty_generics #where_clause { }

            #[automatically_derived]
            impl #impl_generics ::core::clone::Clone for #iter_name #ty_generics #where_clause {
                #[inline]
                fn clone(&self) -> #iter_name #ty_generics {
                    #iter_name {
                        idx: self.idx,
                        back_idx: self.back_idx,
                        marker: self.marker.clone(),
                    }
                }
            }
            """.trimIndent(),
            "doc_comment" to docComment,
            "vis" to vis,
            "iter_name" to iterName,
            "impl_generics" to implGenerics,
            "phantom_data" to phantomData,
            "ty_generics" to tyGenerics,
            "where_clause" to whereClause,
            "iter_name_debug_struct" to iterNameDebugStruct,
            "name" to name,
            "arms" to arms,
            "strum_module_path" to strumModulePath,
            "variant_count" to variantCount,
        )

    return SynResult.success(output)
}
