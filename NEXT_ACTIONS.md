# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 4/24 (16.7%)
- **Function parity:** 12/67 matched (target 26) — 17.9%
- **Class/type parity:** 1/19 matched (target 6) — 5.3%
- **Combined symbol parity:** 13/86 matched (target 32) — 15.1%
- **Average inline-code cosine:** 0.65 (function body across 1 matched files)
- **Average documentation cosine:** 0.17 (doc text across 1 matched files)
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

- **Target:** `helpers.CaseStyle [PROVENANCE-FALLBACK]`
- **Similarity:** 0.65
- **Dependents:** 2
- **Priority Score:** 2020903.5
- **Functions:** 6/6 matched (target 18)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Err`, `CaseStyleHelpers`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/helpers/case_style.rs` vs expected `helpers/case_style.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/helpers/case_style.rs` vs expected `helpers/case_style.rs`
- **Proposed provenance header:** `// port-lint: source helpers/case_style.rs` (current: `// port-lint: source src/helpers/case_style.rs`)
- **Proposed provenance header:** `// port-lint: tests helpers/case_style.rs` (current: `// port-lint: tests src/helpers/case_style.rs`)
- **Lint issues:** 2

### 2. lib

- **Target:** `strummacros.Lib [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 191910.0
- **Functions:** 0/19 matched (target 1)
- **Missing functions:** `debug_print_generated`, `from_string`, `as_ref_str`, `variant_names`, `variant_names_deprecated`, `static_variants_array`, `as_static_str`, `into_static_str`, `to_string`, `display`, `enum_iter`, `enum_is`, `enum_try_as`, `enum_table`, `from_repr`, `enum_messages`, `enum_properties`, `enum_discriminants`, `enum_count`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source src/lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests src/lib.rs`)
- **Lint issues:** 2

### 3. helpers.mod

- **Target:** `helpers.Helpers [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 610.0
- **Functions:** 6/6 matched (target 7)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/helpers/mod.rs` vs expected `helpers/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/helpers/mod.rs` vs expected `helpers/mod.rs`
- **Proposed provenance header:** `// port-lint: source helpers/mod.rs` (current: `// port-lint: source src/helpers/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests helpers/mod.rs` (current: `// port-lint: tests src/helpers/mod.rs`)
- **Lint issues:** 2

### 4. macros.mod

- **Target:** `macros.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/macros/mod.rs` vs expected `macros/mod.rs`
- **Proposed provenance header:** `// port-lint: source macros/mod.rs` (current: `// port-lint: source src/macros/mod.rs`)
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
| `strings.mod` | `macros.strings.Mod` | 0 | `macros/strings/mod.rs` | `macros/strings/Mod.kt` |

