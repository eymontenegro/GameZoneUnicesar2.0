| **Explanation** | `CsvSupport` was created as a fabrication to avoid repetition, but the extraction was not completed. |
| **Consequence** | A change in the file format or in the error reporting must be repeated in each copy, and the copies can drift apart (functional defect FD-1 is an example). |
| **Proposed solution** | Move the shared read/write loop, the parsing and the folder creation into `CsvSupport`, and make every repository use them. The folder creation must keep today's behavior in `ProductRepository` and `PersonRepository` (see FD-1). Pattern applied: Pure Fabrication. |
| **Code of another member** | Yes. Developer 2 reports the same duplication as V-10 of her audit, and the original design of `ProductRepository` and `SaleRepository` belongs to other authors. Boundary finding: one violation in the consolidation, with `CsvSupport` extended by Developer 3. |

### V-04 — `WarrantyRepository` coupled to five repositories

| Field | Content |
|---|---|
| **ID** | V-04 |
| **Pattern** | Low Coupling (also affects High Cohesion; recorded once here) |
| **Module** | Persistence (Warranties) |
| **Evidence** | `WarrantyRepository:45–49` keeps 5 repositories; `229–235` reloads all of them on every `loadAll`. It imports 9 model classes. `Main:52` acknowledges it in a comment about cross-dependencies, and `Main:60–67` builds it with the five repositories. |
| **Explanation** | The repository is the point of highest coupling in the layer. Besides reading warranties, it orchestrates the loading of five other aggregates. |
| **Consequence** | A change in any of the five repositories can break warranty loading, and every read reloads everything from disk. |
| **Proposed solution** | Make the warranty loader depend only on what it needs (for example, receive the already loaded data) instead of holding and reloading five repositories. Pattern applied: Low Coupling. |
| **Code of another member** | Yes. The original design of `WarrantyRepository` belongs to Developer 2 (V-03 of her audit), and the `Main` wiring to the Technical Leader (boundary observation 3 of her audit). **Developer 3's part:** none of the coupling design; only to adapt `WarrantyRepositoryTest` and the exception wrapping in the reload if the constructor or `loadAll` changes. Boundary finding: one violation in the consolidation. |

### V-05 — Unused parameter in `SaleRepository.loadAll`

| Field | Content |
|---|---|
| **ID** | V-05 |
| **Pattern** | Low Coupling |
| **Module** | Persistence (Sales) |
| **Evidence** | `SaleRepository:169` declares `promotions` (documented at line 160) and never uses it. It appears only in lines 160 and 169. |
| **Explanation** | The method receives, and its callers must load, a collection of `Promotion` that it does not need to rebuild a sale, so the repository and its callers depend on `Promotion` without using it. |
| **Consequence** | Callers must provide data just to call the method, and the signature misleads the reader about what `loadAll` depends on. |
| **Proposed solution** | Remove the parameter, migrating the callers first so the build keeps compiling. Pattern applied: Low Coupling. |
| **Code of another member** | Yes. The Technical Leader reports the same parameter as V-07 of her audit, and its callers belong to other members (`SaleService`, `WarrantyRepository`). **Developer 3's part:** migrate the calls in `SaleRepositoryTest` (lines 66 and 305, per the Technical Leader's V-07) once the version without the parameter exists. Boundary finding: one violation in the consolidation. |

---

## 5. Boundary observations reported for the consolidation

These involve code authored by other members. They are **not** qualified as Developer 3's. They are reported because they break the stability guarantee that the deliverable requires, and they must be returned to their owners so they include them in their own reports. They are functional defects, so they are not corrected in Requirement 7 and belong to the out-of-scope section of the consolidated audit.

| ID | Severity | Owner | Finding | Evidence |
|---|---|---|---|---|
| B-1 | High | Technical Leader | `Main:41–99` has no `try/catch`, and the services load the CSV files when they are built (`ProductService:43`, `SaleService:53`). | **Reproduced:** with a corrupt `products.csv`, the repository throws the error with line and context, but `Main` lets it out and the JVM ends with `Exception in thread "main"`. |
| B-2 | Medium | Technical Leader | `SaleService:134–146` adds the sale to memory before saving and does not remove it when saving fails. | **Reproduced** with the save forced to fail: stock is restored (10), but 1 phantom sale stays in memory and purchase `[V1]` stays on the customer. |

---

## 6. Out-of-scope findings (functional defects, not corrected in Requirement 7)

| # | Defect | Evidence |
|---|---|---|
| FD-1 | The target folder is no longer created in `ProductRepository` and `PersonRepository`. The refactoring dropped a behavior that the original had (`ProductRepository`, commit `da4d791`, lines 63–64; `PersonRepository`, lines 58–59 and 84–85), so a first run on a clean machine, or a deleted `data/` folder, makes saving fail with `PersistenceException`. `AccessoryRepository` and `SaleRepository` do create it. Restoring it would change what the user observes today, so it is not corrected here. | `ProductRepository:117`, `PersonRepository:152`. **Reproduced:** with a non-existent folder both fail with `PersistenceException`. |
| FD-2 | `PersistenceException extends RuntimeException` (`PersistenceException.java:17`), but `TEAM.md` defines it as a subclass of `GameZoneException`. Developer 1 created the class extending `GameZoneException` (commit `2662475`, 2026-10-16), and the superclass was changed to `RuntimeException` in commit `ca6cb5d` (`xacosta`, 2026-10-18, "Implement exception handling in persistence layer"), so the line is Developer 3's. `SaleService:143–146` (code of other members) rethrows only `GameZoneException`, so it wraps this one in a generic `RuntimeException`, and that wrapper bypasses the `catch (PersistenceException)` that `Menu.displayMenu` already has. Severity high. Correcting it changes which exception the menu catches, so it is not corrected here. | `PersistenceException.java:17`, confirmed with `git log -S` and `git blame`. **Reproduced:** `RuntimeException \| cause: PersistenceException`. |

---

## 7. Test audit

**What is correct**
- All 60 tests pass (seven classes: 7+5+5+5+8+22+8). They were run on the repository code with a JUnit substitute, not with Maven.
- Every subtype has a round-trip test.
- `@TempDir` isolates the tests from the real disk.
- `refundIsNotDoubledAfterReload` protects the `ReturnRepository` problem (V-01).

**What fails**

| ID | Finding |
|---|---|
| T1 | No test covers a non-existent folder in `Product` or `Person`. Only `SaleRepositoryTest:131` does, which is why FD-1 went unnoticed. |
| T2 | There are 0 references to `GameZoneException` in the tests. A single assertion would have detected FD-2. |
| T3 | No test goes through persistence, service and menu. The requirement "respond without crashing" is shown only at repository level, and B-1 shows that at system level it is not met. |
| T4 | Only 20 message assertions against about 54 corrupt-file cases. Most only check that the exception is thrown, not its content. |
| T5 | The helpers `loadFails` and `writeFile` are duplicated in 6 of the 7 classes. It is test code only, so it is not registered as a violation. |

---

## 8. Evidence validation status

Every finding has file and line. The ones **reproduced by running the code** are V-01, FD-1, FD-2, B-1 and B-2. V-02 to V-05 rest on direct reading of the code. B-1 and B-2 must be returned to their owners.
