import Testing
import StrumMacros

// Smoke test for the Kotlin → Swift Export → SPM → swift test pipeline.
@Suite("StrumMacros Swift Export Smoke Tests")
struct StrumMacrosExportTests {
    @Test("StrumMacros swift module imported cleanly")
    func swiftModuleLoads() throws {
        #expect(Bool(true))
        let style = helpers.CaseStyle.PascalCase
        #expect(style == .PascalCase)
    }
}
