// port-lint: source src/helpers/case_style.rs
package io.github.kotlinmania.strummacros.helpers

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.LitStrParse
import io.github.kotlinmania.syn.ParseStream
import io.github.kotlinmania.syn.SynError
import io.github.kotlinmania.syn.SynResult

public enum class CaseStyle {
    CamelCase,
    KebabCase,
    MixedCase,
    ShoutySnakeCase,
    SnakeCase,
    TitleCase,
    UpperCase,
    LowerCase,
    ScreamingKebabCase,
    PascalCase,
    TrainCase;

    public companion object {
        public val VALID_CASE_STYLES: List<String> =
            listOf(
                "camelCase",
                "PascalCase",
                "kebab-case",
                "snake_case",
                "SCREAMING_SNAKE_CASE",
                "SCREAMING-KEBAB-CASE",
                "lowercase",
                "UPPERCASE",
                "title_case",
                "mixed_case",
                "Train-Case",
            )

        public fun fromStr(text: String): CaseStyle? =
            when (text) {
                "PascalCase", "camel_case" -> PascalCase
                "camelCase" -> CamelCase
                "snake_case", "snek_case" -> SnakeCase
                "kebab-case", "kebab_case" -> KebabCase
                "SCREAMING-KEBAB-CASE" -> ScreamingKebabCase
                "SCREAMING_SNAKE_CASE", "shouty_snake_case", "shouty_snek_case" -> ShoutySnakeCase
                "title_case" -> TitleCase
                "mixed_case" -> MixedCase
                "lowercase" -> LowerCase
                "UPPERCASE" -> UpperCase
                "Train-Case" -> TrainCase
                else -> null
            }

        public fun fromString(text: String): CaseStyle? = fromStr(text)

        public fun parse(input: ParseStream): SynResult<CaseStyle> {
            val text = when (val res = LitStrParse.parse(input)) {
                is SynResult.Success -> res.value
                is SynResult.Failure -> return SynResult.failure(res.error)
            }
            val value = text.value()
            val style = fromString(value)
            return if (style != null) {
                SynResult.success(style)
            } else {
                SynResult.failure(
                    SynError.newSpanned(
                        text,
                        "Unexpected case style for serialize_all: `$value`. Valid values are: $VALID_CASE_STYLES",
                    ),
                )
            }
        }
    }
}

private fun splitWords(s: String): List<String> {
    if (s.isEmpty()) return emptyList()
    val words = mutableListOf<String>()
    val current = StringBuilder()
    var i = 0
    while (i < s.length) {
        val c = s[i]
        if (c == '_' || c == '-' || c == ' ' || !c.isLetterOrDigit()) {
            if (current.isNotEmpty()) {
                words.add(current.toString())
                current.clear()
            }
            i++
            continue
        }
        if (c.isUpperCase() && current.isNotEmpty()) {
            val prev = current.last()
            val next = if (i + 1 < s.length) s[i + 1] else null
            if (!prev.isUpperCase() || (next != null && next.isLowerCase())) {
                words.add(current.toString())
                current.clear()
            }
        }
        current.append(c)
        i++
    }
    if (current.isNotEmpty()) {
        words.add(current.toString())
    }
    return words
}

private fun capitalize(w: String): String =
    if (w.isEmpty()) "" else w.take(1).uppercase() + w.drop(1).lowercase()

private fun toPascalCase(s: String): String =
    splitWords(s).joinToString("") { capitalize(it) }

private fun toCamelCase(s: String): String {
    val words = splitWords(s)
    if (words.isEmpty()) return ""
    return words.first().lowercase() + words.drop(1).joinToString("") { capitalize(it) }
}

private fun toKebabCase(s: String): String =
    splitWords(s).joinToString("-") { it.lowercase() }

private fun toSnakeCase(s: String): String =
    splitWords(s).joinToString("_") { it.lowercase() }

private fun toShoutySnakeCase(s: String): String =
    splitWords(s).joinToString("_") { it.uppercase() }

private fun toTitleCase(s: String): String =
    splitWords(s).joinToString(" ") { capitalize(it) }

private fun toTrainCase(s: String): String =
    splitWords(s).joinToString("-") { capitalize(it) }

public fun convertCase(
    identString: String,
    caseStyle: CaseStyle?,
): String =
    if (caseStyle == null) {
        identString
    } else {
        when (caseStyle) {
            CaseStyle.PascalCase -> toPascalCase(identString)
            CaseStyle.KebabCase -> toKebabCase(identString)
            CaseStyle.MixedCase -> toCamelCase(identString)
            CaseStyle.ShoutySnakeCase -> toShoutySnakeCase(identString)
            CaseStyle.SnakeCase -> toSnakeCase(identString)
            CaseStyle.TitleCase -> toTitleCase(identString)
            CaseStyle.UpperCase -> identString.uppercase()
            CaseStyle.LowerCase -> identString.lowercase()
            CaseStyle.ScreamingKebabCase -> toKebabCase(identString).uppercase()
            CaseStyle.TrainCase -> toTrainCase(identString)
            CaseStyle.CamelCase -> toCamelCase(identString)
        }
    }

public fun Ident.convertCase(caseStyle: CaseStyle?): String =
    convertCase(this.toString(), caseStyle)

/**
 * Converts alphanumeric words to snake case, treating numbers as distinct words.
 */
public fun snakify(s: String): String {
    val output = toSnakeCase(s).toMutableList()
    val numStarts = mutableListOf<Int>()
    for (pos in output.indices) {
        val c = output[pos]
        if (c.isDigit() && pos != 0 && !output[pos - 1].isDigit()) {
            numStarts.add(pos)
        }
    }
    for (i in numStarts.reversed()) {
        output.add(i, '_')
    }
    return output.joinToString("")
}
