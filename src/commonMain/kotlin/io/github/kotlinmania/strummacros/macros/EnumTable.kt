// port-lint: source macros/enum_table.rs
package io.github.kotlinmania.strummacros.macros

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.formatIdent
import io.github.kotlinmania.quote.quote
import io.github.kotlinmania.strummacros.helpers.getVariantProperties
import io.github.kotlinmania.strummacros.helpers.nonEnumError
import io.github.kotlinmania.strummacros.helpers.snakify
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.SynError
import io.github.kotlinmania.syn.SynResult

internal fun enumTableInner(ast: DeriveInput): SynResult<TokenStream> {
    val name = ast.ident
    val gen = ast.generics
    val vis = ast.vis
    var docComment = "A map over the variants of `$name`"

    if (gen.lifetimes().count() > 0) {
        return SynResult.failure(
            SynError.new(
                Span.callSite(),
                "`EnumTable` doesn't support enums with lifetimes.",
            ),
        )
    }

    val variants =
        when (val data = ast.data) {
            is Data.Enum -> data.variants.toList()
            else -> return SynResult.failure(nonEnumError())
        }

    val tableName = formatIdent("{}Table", name)

    val pascalIdents = mutableListOf<Ident>()
    val snakeIdents = mutableListOf<Ident>()
    val getMatches = mutableListOf<TokenStream>()
    val getMatchesMut = mutableListOf<TokenStream>()
    val setMatches = mutableListOf<TokenStream>()
    val closureFields = mutableListOf<TokenStream>()
    val transformFields = mutableListOf<TokenStream>()

    val disabledVariants = mutableListOf<Ident>()
    val disabledMatches = mutableListOf<TokenStream>()

    for (variant in variants) {
        val props = variant.getVariantProperties().getOrElse { return SynResult.failure(it) }
        if (props.disabled != null) {
            val disabledIdent = variant.ident
            val panicMessage = "Can't use `$disabledIdent` with `$tableName` - variant is disabled for Strum features"
            disabledVariants.add(disabledIdent)
            disabledMatches.add(quote("#name::#disabled_ident => panic!(#panic_message),", "name" to name, "disabled_ident" to disabledIdent, "panic_message" to panicMessage))
            continue
        }

        if (variant.fields !is Fields.Unit) {
            return SynResult.failure(
                SynError.new(
                    variant.ident.span(),
                    "`EnumTable` doesn't support enums with non-unit variants",
                ),
            )
        }

        val pascalCase = variant.ident
        val snakeCase = formatIdent("_{}", snakify(pascalCase.toString()))

        getMatches.add(quote("#name::#pascal_case => &self.#snake_case,", "name" to name, "pascal_case" to pascalCase, "snake_case" to snakeCase))
        getMatchesMut.add(quote("#name::#pascal_case => &mut self.#snake_case,", "name" to name, "pascal_case" to pascalCase, "snake_case" to snakeCase))
        setMatches.add(quote("#name::#pascal_case => self.#snake_case = new_value,", "name" to name, "pascal_case" to pascalCase, "snake_case" to snakeCase))
        closureFields.add(quote("#snake_case: func(#name::#pascal_case),", "snake_case" to snakeCase, "name" to name, "pascal_case" to pascalCase))
        transformFields.add(quote("#snake_case: func(#name::#pascal_case, &self.#snake_case),", "snake_case" to snakeCase, "name" to name, "pascal_case" to pascalCase))
        pascalIdents.add(pascalCase)
        snakeIdents.add(snakeCase)
    }

    if (pascalIdents.isEmpty()) {
        return SynResult.failure(
            SynError.new(
                ast.ident.span(),
                "`EnumTable` requires at least one non-disabled variant",
            ),
        )
    }

    if (disabledVariants.isNotEmpty()) {
        docComment += "\n# Panics\nIndexing `$tableName` with any of the following variants will cause a panic:"
        for (variant in disabledVariants) {
            docComment += "\n\n- `$name::$variant`"
        }
    }

    val docNew = "Create a new $tableName with a value for each variant of $name"
    val docClosure = "Create a new $tableName by running a function on each variant of `$name`"
    val docTransform = "Create a new `$tableName` by running a function on each variant of `$name` and the corresponding value in the current `$tableName`"
    val docFilled = "Create a new `$tableName` with the same value in each field."
    val docOptionAll = "Converts `$tableName<Option<T>>` into `Option<$tableName<T>>`. Returns `Some` if all fields are `Some`, otherwise returns `None`."
    val docResultAllOk = "Converts `$tableName<Result<T, E>>` into `Result<$tableName<T>, E>`. Returns `Ok` if all fields are `Ok`, otherwise returns `Err`."

    val output =
        quote(
            """
            #[doc = #doc_comment]
            #[allow(
                missing_copy_implementations,
            )]
            #[derive(Debug, Clone, Default, PartialEq, Eq, Hash)]
            #vis struct #table_name<T> {
                #(#snake_idents: T,)*
            }

            #[automatically_derived]
            impl<T: Clone> #table_name<T> {
                #[doc = #doc_filled]
                #vis fn filled(value: T) -> #table_name<T> {
                    #table_name {
                        #(#snake_idents: value.clone(),)*
                    }
                }
            }

            #[automatically_derived]
            impl<T> #table_name<T> {
                #[doc = #doc_new]
                #[inline]
                #vis fn new(
                    #(#snake_idents: T,)*
                ) -> #table_name<T> {
                    #table_name {
                        #(#snake_idents,)*
                    }
                }

                #[doc = #doc_closure]
                #[inline]
                #vis fn from_closure<F: FnMut(#name)->T>(mut func: F) -> #table_name<T> {
                  #table_name {
                    #(#closure_fields)*
                  }
                }

                #[doc = #doc_transform]
                #[inline]
                #vis fn transform<U, F: FnMut(#name, &T)->U>(&self, mut func: F) -> #table_name<U> {
                  #table_name {
                    #(#transform_fields)*
                  }
                }

            }

            #[automatically_derived]
            impl<T> ::core::ops::Index<#name> for #table_name<T> {
                type Output = T;

                #[inline]
                fn index(&self, idx: #name) -> &T {
                    match idx {
                        #(#get_matches)*
                        #(#disabled_matches)*
                    }
                }
            }

            #[automatically_derived]
            impl<T> ::core::ops::IndexMut<#name> for #table_name<T> {
                #[inline]
                fn index_mut(&mut self, idx: #name) -> &mut T {
                    match idx {
                        #(#get_matches_mut)*
                        #(#disabled_matches)*
                    }
                }
            }

            #[automatically_derived]
            impl<T> #table_name<::core::option::Option<T>> {
                #[doc = #doc_option_all]
                #[inline]
                #vis fn all(self) -> ::core::option::Option<#table_name<T>> {
                    if let #table_name {
                        #(#snake_idents: ::core::option::Option::Some(#snake_idents),)*
                    } = self {
                        ::core::option::Option::Some(#table_name {
                            #(#snake_idents,)*
                        })
                    } else {
                        ::core::option::Option::None
                    }
                }
            }

            #[automatically_derived]
            impl<T, E> #table_name<::core::result::Result<T, E>> {
                #[doc = #doc_result_all_ok]
                #[inline]
                #vis fn all_ok(self) -> ::core::result::Result<#table_name<T>, E> {
                    ::core::result::Result::Ok(#table_name {
                        #(#snake_idents: self.#snake_idents?,)*
                    })
                }
            }
            """.trimIndent(),
            "doc_comment" to docComment,
            "vis" to vis,
            "table_name" to tableName,
            "snake_idents" to snakeIdents,
            "doc_filled" to docFilled,
            "doc_new" to docNew,
            "name" to name,
            "doc_closure" to docClosure,
            "closure_fields" to closureFields,
            "doc_transform" to docTransform,
            "transform_fields" to transformFields,
            "get_matches" to getMatches,
            "disabled_matches" to disabledMatches,
            "get_matches_mut" to getMatchesMut,
            "doc_option_all" to docOptionAll,
            "doc_result_all_ok" to docResultAllOk,
        )
    return SynResult.success(output)
}
