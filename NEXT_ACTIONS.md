# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 24/24 (100.0%)
- **Function parity:** 62/63 matched (target 82) — 98.4%
- **Class/type parity:** 19/20 matched (target 47) — 95.0%
- **Combined symbol parity:** 81/83 matched (target 129) — 97.6%
- **Average inline-code cosine:** 0.62 (function body across 20 matched files)
- **Average documentation cosine:** 0.01 (doc text across 20 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 8 files with <0.60 function similarity

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
- **Priority Score:** 2010903.5
- **Functions:** 6/6 matched (target 18)
- **Missing functions:** _none_
- **Types:** 2/3 matched
- **Missing types:** `Err`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/helpers/case_style.rs` vs expected `helpers/case_style.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:strum_macros/src/helpers/case_style.rs` vs expected `helpers/case_style.rs`
- **Proposed provenance header:** `// port-lint: source helpers/case_style.rs` (current: `// port-lint: source strum_macros/src/helpers/case_style.rs`)
- **Proposed provenance header:** `// port-lint: tests helpers/case_style.rs` (current: `// port-lint: tests strum_macros/src/helpers/case_style.rs`)
- **Lint issues:** 2

### 2. strings.as_ref_str

- **Target:** `strings.AsRefStr [PROVENANCE-FALLBACK]`
- **Similarity:** 0.68
- **Dependents:** 1
- **Priority Score:** 1000403.2
- **Functions:** 3/3 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/strings/as_ref_str.rs` vs expected `macros/strings/as_ref_str.rs`
- **Proposed provenance header:** `// port-lint: source macros/strings/as_ref_str.rs` (current: `// port-lint: source strum_macros/src/macros/strings/as_ref_str.rs`)
- **Lint issues:** 1

### 3. strings.display

- **Target:** `strings.Display [PROVENANCE-FALLBACK]`
- **Similarity:** 0.65
- **Dependents:** 1
- **Priority Score:** 1000303.5
- **Functions:** 3/3 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/strings/display.rs` vs expected `macros/strings/display.rs`
- **Proposed provenance header:** `// port-lint: source macros/strings/display.rs` (current: `// port-lint: source strum_macros/src/macros/strings/display.rs`)
- **Lint issues:** 1

### 4. strings.from_string

- **Target:** `strings.FromString [PROVENANCE-FALLBACK]`
- **Similarity:** 0.63
- **Dependents:** 1
- **Priority Score:** 1000103.7
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/strings/from_string.rs` vs expected `macros/strings/from_string.rs`
- **Proposed provenance header:** `// port-lint: source macros/strings/from_string.rs` (current: `// port-lint: source strum_macros/src/macros/strings/from_string.rs`)
- **Lint issues:** 1

### 5. strings.to_string

- **Target:** `strings.ToString [PROVENANCE-FALLBACK]`
- **Similarity:** 0.80
- **Dependents:** 1
- **Priority Score:** 1000102.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/strings/to_string.rs` vs expected `macros/strings/to_string.rs`
- **Proposed provenance header:** `// port-lint: source macros/strings/to_string.rs` (current: `// port-lint: source strum_macros/src/macros/strings/to_string.rs`)
- **Lint issues:** 1

### 6. lib

- **Target:** `strummacros.Lib [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 11910.0
- **Functions:** 18/19 matched
- **Missing functions:** `debug_print_generated`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:strum_macros/src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source strum_macros/src/lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests strum_macros/src/lib.rs`)
- **Lint issues:** 2

### 7. helpers.metadata

- **Target:** `helpers.Metadata [PROVENANCE-FALLBACK]`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 1304.0
- **Functions:** 5/5 matched (target 10)
- **Missing functions:** _none_
- **Types:** 8/8 matched (target 32)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/helpers/metadata.rs` vs expected `helpers/metadata.rs`
- **Proposed provenance header:** `// port-lint: source helpers/metadata.rs` (current: `// port-lint: source strum_macros/src/helpers/metadata.rs`)
- **Lint issues:** 1

### 8. helpers.mod

- **Target:** `helpers.Helpers [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 610.0
- **Functions:** 6/6 matched (target 8)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/helpers/mod.rs` vs expected `helpers/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:strum_macros/src/helpers/mod.rs` vs expected `helpers/mod.rs`
- **Proposed provenance header:** `// port-lint: source helpers/mod.rs` (current: `// port-lint: source strum_macros/src/helpers/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests helpers/mod.rs` (current: `// port-lint: tests strum_macros/src/helpers/mod.rs`)
- **Lint issues:** 2

### 9. helpers.variant_props

- **Target:** `helpers.VariantProps [PROVENANCE-FALLBACK]`
- **Similarity:** 0.74
- **Dependents:** 0
- **Priority Score:** 602.6
- **Functions:** 4/4 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/helpers/variant_props.rs` vs expected `helpers/variant_props.rs`
- **Proposed provenance header:** `// port-lint: source helpers/variant_props.rs` (current: `// port-lint: source strum_macros/src/helpers/variant_props.rs`)
- **Lint issues:** 1

### 10. helpers.type_props

- **Target:** `helpers.TypeProps [PROVENANCE-FALLBACK]`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 406.1
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/helpers/type_props.rs` vs expected `helpers/type_props.rs`
- **Proposed provenance header:** `// port-lint: source helpers/type_props.rs` (current: `// port-lint: source strum_macros/src/helpers/type_props.rs`)
- **Lint issues:** 1

### 11. helpers.inner_variant_props

- **Target:** `helpers.InnerVariantProps [PROVENANCE-FALLBACK]`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 303.4
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/helpers/inner_variant_props.rs` vs expected `helpers/inner_variant_props.rs`
- **Proposed provenance header:** `// port-lint: source helpers/inner_variant_props.rs` (current: `// port-lint: source strum_macros/src/helpers/inner_variant_props.rs`)
- **Lint issues:** 1

### 12. strings.mod

- **Target:** `strings.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 210.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/strings/mod.rs` vs expected `macros/strings/mod.rs`
- **Proposed provenance header:** `// port-lint: source macros/strings/mod.rs` (current: `// port-lint: source strum_macros/src/macros/strings/mod.rs`)
- **Lint issues:** 1

### 13. macros.enum_properties

- **Target:** `macros.EnumProperties [PROVENANCE-FALLBACK]`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_properties.rs` vs expected `macros/enum_properties.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_properties.rs` (current: `// port-lint: source strum_macros/src/macros/enum_properties.rs`)
- **Lint issues:** 1

### 14. macros.enum_discriminants

- **Target:** `macros.EnumDiscriminants [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_discriminants.rs` vs expected `macros/enum_discriminants.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_discriminants.rs` (current: `// port-lint: source strum_macros/src/macros/enum_discriminants.rs`)
- **Lint issues:** 1

### 15. macros.enum_table

- **Target:** `macros.EnumTable [PROVENANCE-FALLBACK]`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 104.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_table.rs` vs expected `macros/enum_table.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_table.rs` (current: `// port-lint: source strum_macros/src/macros/enum_table.rs`)
- **Lint issues:** 1

### 16. macros.enum_iter

- **Target:** `macros.EnumIter [PROVENANCE-FALLBACK]`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 104.8
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_iter.rs` vs expected `macros/enum_iter.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_iter.rs` (current: `// port-lint: source strum_macros/src/macros/enum_iter.rs`)
- **Lint issues:** 1

### 17. macros.enum_messages

- **Target:** `macros.EnumMessages [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 103.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_messages.rs` vs expected `macros/enum_messages.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_messages.rs` (current: `// port-lint: source strum_macros/src/macros/enum_messages.rs`)
- **Lint issues:** 1

### 18. macros.enum_try_as

- **Target:** `macros.EnumTryAs [PROVENANCE-FALLBACK]`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 103.5
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_try_as.rs` vs expected `macros/enum_try_as.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_try_as.rs` (current: `// port-lint: source strum_macros/src/macros/enum_try_as.rs`)
- **Lint issues:** 1

### 19. macros.enum_count

- **Target:** `macros.EnumCount [PROVENANCE-FALLBACK]`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 103.4
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_count.rs` vs expected `macros/enum_count.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_count.rs` (current: `// port-lint: source strum_macros/src/macros/enum_count.rs`)
- **Lint issues:** 1

### 20. macros.enum_is

- **Target:** `macros.EnumIs [PROVENANCE-FALLBACK]`
- **Similarity:** 0.70
- **Dependents:** 0
- **Priority Score:** 103.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_is.rs` vs expected `macros/enum_is.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_is.rs` (current: `// port-lint: source strum_macros/src/macros/enum_is.rs`)
- **Lint issues:** 1

### 21. macros.from_repr

- **Target:** `macros.FromRepr [PROVENANCE-FALLBACK]`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 102.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/from_repr.rs` vs expected `macros/from_repr.rs`
- **Proposed provenance header:** `// port-lint: source macros/from_repr.rs` (current: `// port-lint: source strum_macros/src/macros/from_repr.rs`)
- **Lint issues:** 1

### 22. macros.enum_variant_array

- **Target:** `macros.EnumVariantArray [PROVENANCE-FALLBACK]`
- **Similarity:** 0.76
- **Dependents:** 0
- **Priority Score:** 102.4
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_variant_array.rs` vs expected `macros/enum_variant_array.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_variant_array.rs` (current: `// port-lint: source strum_macros/src/macros/enum_variant_array.rs`)
- **Lint issues:** 1

### 23. macros.enum_variant_names

- **Target:** `macros.EnumVariantNames [PROVENANCE-FALLBACK]`
- **Similarity:** 0.77
- **Dependents:** 0
- **Priority Score:** 102.3
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `strum_macros/src/macros/enum_variant_names.rs` vs expected `macros/enum_variant_names.rs`
- **Proposed provenance header:** `// port-lint: source macros/enum_variant_names.rs` (current: `// port-lint: source strum_macros/src/macros/enum_variant_names.rs`)
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

### Matched

| Source | Target | Path |
|--------|--------|------|
| `macros.mod` | `macros.Mod` | `macros/mod` |

