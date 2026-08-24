# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/24 (12.5%)
- **Function parity:** 12/48 matched (target 25) — 25.0%
- **Class/type parity:** 1/19 matched (target 4) — 5.3%
- **Combined symbol parity:** 13/67 matched (target 29) — 19.4%
- **Average inline-code cosine:** 0.00 (function body across 1 matched files)
- **Average documentation cosine:** 1.00 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 3 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. helpers.case_style

- **Target:** `helpers.CaseStyle [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2020910.0
- **Functions:** 6/6 matched (target 18)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Err`, `CaseStyleHelpers`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `helpers/case_style.rs` vs expected `helpers/case_style.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:helpers/case_style.rs` vs expected `helpers/case_style.rs`
- **Proposed provenance header:** `// port-lint: source helpers/case_style.rs` (current: `// port-lint: source helpers/case_style.rs`)
- **Proposed provenance header:** `// port-lint: tests helpers/case_style.rs` (current: `// port-lint: tests helpers/case_style.rs`)
- **Lint issues:** 2

### 2. helpers.mod

- **Target:** `helpers.Helpers [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 610.0
- **Functions:** 6/6 matched (target 7)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `helpers/mod.rs` vs expected `helpers/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:helpers/mod.rs` vs expected `helpers/mod.rs`
- **Proposed provenance header:** `// port-lint: source helpers/mod.rs` (current: `// port-lint: source helpers/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests helpers/mod.rs` (current: `// port-lint: tests helpers/mod.rs`)
- **Lint issues:** 2

### 3. macros.mod

- **Target:** `macros.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `macros/mod.rs` vs expected `macros/mod.rs`
- **Proposed provenance header:** `// port-lint: source macros/mod.rs` (current: `// port-lint: source macros/mod.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `src/lib.rs` | `Lib.kt` |
| `strings.mod` | `macros.strings.Mod` | 0 | `src/macros/strings/mod.rs` | `macros/strings/Mod.kt` |

