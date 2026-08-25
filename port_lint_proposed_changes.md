# port-lint Proposed Changes

**Generated:** 2026-08-25
**Source:** tmp/strum_macros/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/strummacros

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/strummacros/helpers/CaseStyle.kt` | `// port-lint: source src/helpers/case_style.rs` | `// port-lint: source helpers/case_style.rs` | `helpers/case_style.rs` | `port-lint provenance header matched only after fallback normalization: 'src/helpers/case_style.rs' vs expected 'helpers/case_style.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/strummacros/helpers/CaseStyleTest.kt` | `// port-lint: tests src/helpers/case_style.rs` | `// port-lint: tests helpers/case_style.rs` | `helpers/case_style.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:src/helpers/case_style.rs' vs expected 'helpers/case_style.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/strummacros/Lib.kt` | `// port-lint: source src/lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'src/lib.rs' vs expected 'lib.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/strummacros/LibTest.kt` | `// port-lint: tests src/lib.rs` | `// port-lint: tests lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:src/lib.rs' vs expected 'lib.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/strummacros/helpers/Helpers.kt` | `// port-lint: source src/helpers/mod.rs` | `// port-lint: source helpers/mod.rs` | `helpers/mod.rs` | `port-lint provenance header matched only after fallback normalization: 'src/helpers/mod.rs' vs expected 'helpers/mod.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/strummacros/HelpersTest.kt` | `// port-lint: tests src/helpers/mod.rs` | `// port-lint: tests helpers/mod.rs` | `helpers/mod.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:src/helpers/mod.rs' vs expected 'helpers/mod.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/strummacros/macros/Mod.kt` | `// port-lint: source src/macros/mod.rs` | `// port-lint: source macros/mod.rs` | `macros/mod.rs` | `port-lint provenance header matched only after fallback normalization: 'src/macros/mod.rs' vs expected 'macros/mod.rs'` |
