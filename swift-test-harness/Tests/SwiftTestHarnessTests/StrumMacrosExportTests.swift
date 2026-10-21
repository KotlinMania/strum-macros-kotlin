import Testing
import StrumMacros

@Suite("StrumMacros Swift Export Behaviors")
struct StrumMacrosExportTests {
    private func tokens(_ source: String) throws -> io.github.kotlinmania.procmacro2.TokenStream {
        let result = io.github.kotlinmania.procmacro2.TokenStream.Companion.shared.fromString(src: source)
        #expect(result.error == nil)
        return try #require(result.value)
    }

    @Test("Enum count emits the number of declared variants")
    func enumCountTokensExport() throws {
        let input = try tokens("enum Color { Red, Green, Blue }")
        let output = enumCount(input: input).toString()
        #expect(output.contains("EnumCount"))
        #expect(output.contains("COUNT"))
        #expect(output.contains("3"))
    }

    @Test("Variant names retain every declared name")
    func variantNameTokensExport() throws {
        let input = try tokens("enum Color { Red, Green, Blue }")
        let output = variantNames(input: input).toString()
        #expect(output.contains("VariantNames"))
        #expect(output.contains("\"Red\""))
        #expect(output.contains("\"Green\""))
        #expect(output.contains("\"Blue\""))
    }

    @Test("Enum string parser emits parsing and error branches")
    func parserTokensExport() throws {
        let input = try tokens("enum Color { Red, Green, Blue }")
        let output = fromString(input: input).toString()
        #expect(output.contains("FromStr"))
        #expect(output.contains("from_str"))
        #expect(output.contains("VariantNotFound"))
    }

    @Test("An unsupported structure emits the upstream enum diagnostic")
    func nonEnumDiagnosticExport() throws {
        let input = try tokens("struct NotEnum;")
        let output = enumCount(input: input).toString()
        #expect(output.contains("compile_error"))
        #expect(output.contains("This macro only supports enums."))
    }
}
