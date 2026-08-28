# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 24/24 (100.0%)
- **Function parity:** 62/63 matched (target 82) — 98.4%
- **Class/type parity:** 19/20 matched (target 47) — 95.0%
- **Combined symbol parity:** 81/83 matched (target 129) — 97.6%
- **Average inline-code cosine:** 0.62 (function body across 21 matched files)
- **Average documentation cosine:** 0.02 (doc text across 21 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 7 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. helpers.case_style

- **Target:** `helpers.CaseStyle`
- **Similarity:** 0.65
- **Dependents:** 2
- **Priority Score:** 2010903.5
- **Functions:** 6/6 matched (target 18)
- **Missing functions:** _none_
- **Types:** 2/3 matched
- **Missing types:** `Err`
- **Tests:** 2/2 matched

### 2. strings.as_ref_str

- **Target:** `strings.AsRefStr`
- **Similarity:** 0.68
- **Dependents:** 1
- **Priority Score:** 1000403.2
- **Functions:** 3/3 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 3. strings.display

- **Target:** `strings.Display`
- **Similarity:** 0.65
- **Dependents:** 1
- **Priority Score:** 1000303.5
- **Functions:** 3/3 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 4. strings.from_string

- **Target:** `strings.FromString`
- **Similarity:** 0.63
- **Dependents:** 1
- **Priority Score:** 1000103.7
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 5. strings.to_string

- **Target:** `strings.ToString`
- **Similarity:** 0.80
- **Dependents:** 1
- **Priority Score:** 1000102.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 6. lib

- **Target:** `strummacros.Lib`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 11903.9
- **Functions:** 18/19 matched
- **Missing functions:** `debug_print_generated`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 7. helpers.metadata

- **Target:** `helpers.Metadata`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 1304.0
- **Functions:** 5/5 matched (target 10)
- **Missing functions:** _none_
- **Types:** 8/8 matched (target 32)
- **Missing types:** _none_

### 8. helpers.mod

- **Target:** `helpers.Helpers [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 610.0
- **Functions:** 6/6 matched (target 8)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 9. helpers.variant_props

- **Target:** `helpers.VariantProps`
- **Similarity:** 0.74
- **Dependents:** 0
- **Priority Score:** 602.6
- **Functions:** 4/4 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 10. helpers.type_props

- **Target:** `helpers.TypeProps`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 406.1
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 11. helpers.inner_variant_props

- **Target:** `helpers.InnerVariantProps`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 303.4
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 12. strings.mod

- **Target:** `strings.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 210.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 13. macros.enum_properties

- **Target:** `macros.EnumProperties`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 14. macros.enum_discriminants

- **Target:** `macros.EnumDiscriminants [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 15. macros.enum_table

- **Target:** `macros.EnumTable`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 104.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 16. macros.enum_iter

- **Target:** `macros.EnumIter`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 104.8
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 17. macros.enum_messages

- **Target:** `macros.EnumMessages`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 103.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 18. macros.enum_try_as

- **Target:** `macros.EnumTryAs`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 103.5
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 19. macros.enum_count

- **Target:** `macros.EnumCount`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 103.4
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 20. macros.enum_is

- **Target:** `macros.EnumIs`
- **Similarity:** 0.70
- **Dependents:** 0
- **Priority Score:** 103.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 21. macros.from_repr

- **Target:** `macros.FromRepr`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 102.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 22. macros.enum_variant_array

- **Target:** `macros.EnumVariantArray`
- **Similarity:** 0.76
- **Dependents:** 0
- **Priority Score:** 102.4
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 23. macros.enum_variant_names

- **Target:** `macros.EnumVariantNames`
- **Similarity:** 0.77
- **Dependents:** 0
- **Priority Score:** 102.3
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

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

