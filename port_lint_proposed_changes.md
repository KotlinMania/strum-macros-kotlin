# port-lint Proposed Changes

**Generated:** 2026-08-24
**Source:** tmp/strum_macros
**Target:** src

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `commonMain/kotlin/io/github/kotlinmania/strummacros/helpers/CaseStyle.kt` | `// port-lint: source helpers/case_style.rs` | `// port-lint: source helpers/case_style.rs` | `helpers/case_style.rs` | `port-lint provenance header matched only after fallback normalization: 'helpers/case_style.rs' vs expected 'helpers/case_style.rs'` |
| `commonTest/kotlin/io/github/kotlinmania/strummacros/helpers/CaseStyleTest.kt` | `// port-lint: tests helpers/case_style.rs` | `// port-lint: tests helpers/case_style.rs` | `helpers/case_style.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:helpers/case_style.rs' vs expected 'helpers/case_style.rs'` |
