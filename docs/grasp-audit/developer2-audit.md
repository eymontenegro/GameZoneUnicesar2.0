# GRASP Audit - Developer 2

- **Auditor:** Andrea Laurens (alaurens), Developer 2
- **Repository state audited:** `develop` at commit `22e6df8` (merge of pull request #37, exception handling)
- **Language note:** the requirement document calls the customer class `Customer`; in this repository it is `Client`.

## 1. Scope

| Module (requirement) | Classes audited |
|---|---|
| People (Workshop 1) | `Person`, `Client`, `Seller`, `PersonRepository`, `PersonService` |
| Accessories (Requirement 1) | `AccessoryRepository`, `AccessoryService` |
| Promotions (Requirement 2) | `PromotionRepository`, `PromotionService` |
| Returns (Requirement 3) | `ReturnRepository`, `ReturnService` (including `generateMonthlyBalance`) |
| Warranties (Requirement 4) | `WarrantyRepository`, `WarrantyService` |
| Exceptions and validators (Requirement 6) | Use of exceptions and validators in the services and repositories |

### Notes on the scope

- `AccessoryValidator`, `PromotionValidator`, `WarrantyValidator` and `SaleValidator` were written by Developer 2 in `feature/exception-handling`, so their use is audited here. The `exceptions` package and `ProductValidator` and `PersonValidator` belong to Developer 1.
- The low-level file handling of the repositories (`CsvSupport` and the wrapping of `IOException` in `PersistenceException`) was refactored by Developer 3 in Requirement 6, according to `TEAM.md`. Findings that depend on that refactor are reported to the Technical Leader as boundary findings.
- Methods that other developers added to classes outside this scope (for example `Sale.canBeReturned` or `ProductService.restoreStock`) are not audited here.

## 2. Verdict criteria

- **Violation:** the code breaks the pattern at one or more points, and a concrete class, method and line range can be cited.
- **Correct:** the pattern is applied, and the class and method that apply it can be cited.
- **Not applicable:** there is no point of application in the audited code, and the reason is explained.