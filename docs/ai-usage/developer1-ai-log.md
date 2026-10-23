# AI Usage Log — Developer 1

## Part A — Workshop 1 (Products Module)

### Object-Oriented Programming concepts

**2026-09-05**
**Query:** Difference between association, composition and inheritance; when a class should be abstract.
**Use type:** Conceptual (OOP)
**How I used it:** Confirmed why `Product` must be abstract before writing the class, and why `reduceStock()` should control state changes instead of using a plain setter (real encapsulation, not just private attributes with getters/setters).

**2026-09-05**
**Query:** How to declare an abstract method and why it forces subclasses to implement it (polymorphism).
**Use type:** Conceptual (OOP)
**How I used it:** Understood why `describe()` is declared with no body in `Product`, and why the compiler wouldn't let me create `VideoGame` until I added its own implementation with `@Override`.

**2026-09-06**
**Query:** What the `super()` keyword does in a subclass constructor.
**Use type:** Conceptual (OOP)
**How I used it:** Used `super(id, title, price, stock)` in the `VideoGame` and `Console` constructors to initialize inherited attributes without duplicating `Product`'s logic.

### English identifier naming suggestions

**2026-09-05**
**Query:** English translation of the class/attribute/method names that appeared in Spanish in the team's diagrams (Producto → Product, Videojuego → VideoGame, etc.).
**Use type:** English identifier naming suggestions
**How I used it:** Applied these translated names directly to my classes (`Product`, `VideoGame`, `Console`) to stay consistent with the team's translated diagrams.

### Tooling and environment (IntelliJ / NetBeans)

**2026-09-05**
**Query:** How to open an existing Maven project in IntelliJ/NetBeans and correctly create packages and classes nested under `com.gamezone`.
**Use type:** Tooling setup
**How I used it:** Fixed a mistake where I had accidentally created the `model` package outside of `com.gamezone`, and later a `Service` package with an uppercase letter instead of `service`.

**2026-09-06**
**Query:** How to auto-generate a constructor with `super(...)` and getters using Alt+Insert in NetBeans.
**Use type:** Java-specific tooling
**How I used it:** Generated the constructor and getters for `VideoGame` and `Console` this way, after understanding why it wasn't working until I declared the abstract `describe()` method.

**2026-09-06**
**Query:** Meaning of the compiler error "VideoGame is not abstract and does not override abstract method describe()".
**Use type:** Compiler error explanation
**How I used it:** Understood that any concrete subclass of an abstract class must implement all its abstract methods, and added `describe()` to `VideoGame` to resolve it.

### Git and GitHub

**2026-09-06**
**Query:** Basic Git commands (`git checkout -b`, `git branch -m`, `git push --set-upstream`) and why Git doesn't track empty folders.
**Use type:** Git command explanation
**How I used it:** Created and renamed my `feature/product-module` branch correctly, and understood why my teammates' empty folders weren't showing up until they had files inside.

**2026-09-07**
**Query:** How to handle a direct instruction from the professor (do not delete feature branches) that contradicts what the workshop document says.
**Use type:** Conceptual / prioritization criteria
**How I used it:** Confirmed that the professor's instruction takes precedence over the written document, and stopped deleting my branches after merging.

### Review of my own code

**2026-09-06**
**Query:** Review of my `Product`, `VideoGame`, and `Console` classes (compilation errors, typos, logic in `reduceStock()`).
**Use type:** Review of my own code
**How I used it:** Fixed a typo (`tittle` → `title`), an assignment-vs-comparison bug (`=` instead of `<`), removed unnecessary setters not justified by a real business need, and corrected direct access to a private inherited attribute (`title` → `getTitle()`).

**2026-09-08**
**Query:** Review of `ProductRepository` (wrong package location, missing imports, case-sensitivity error on `VideoGame`, a variable not initialized on every possible path, misplaced braces).
**Use type:** Review of my own code
**How I used it:** Fixed the package to `com.gamezone.persistence`, added the necessary imports, corrected `videoGame` to `VideoGame`, added a final `else` to guarantee initialization, and reordered the closing braces correctly.

**2026-09-08**
**Query:** Review of `ProductService` (incorrect uppercase package name, invalid syntax when calling a constructor with `new`, parameter order).
**Use type:** Review of my own code
**How I used it:** Fixed the package to lowercase `service`, removed the types (`String`, `double`, `int`) I had mistakenly left inside the `new VideoGame(...)` call, and reordered the parameters to match the constructor's real order.

### Persistence layer design

**2026-09-08**
**Query:** Comparison of the persistence formats available in Java (plain text, CSV, Java serialization, JSON), with their advantages and disadvantages.
**Use type:** How a specific Java functionality is implemented
**How I used it:** Decided to use CSV for `ProductRepository`, prioritizing a human-readable file and understanding the read/write process over the simplicity of automatic serialization.

**2026-09-08**
**Query:** What to do if saving or loading a file fails, since the workshop doesn't specify anything about it; difference between checked and unchecked exceptions in Java.
**Use type:** Conceptual (Java error handling) + my own design decision
**How I used it:** Decided to catch `IOException` (checked) and rethrow it as a `RuntimeException` (unchecked) to avoid propagating unnecessary `throws` declarations across layers, leaving the responsibility of informing the user to an upper layer.

### JavaDoc

**2026-09-09**
**Query:** Format and purpose of JavaDoc comments (`/** ... */`, `@param`, `@return`, `@throws`).
**Use type:** How a standard Java convention is implemented
**How I used it:** Applied JavaDoc to all 5 classes in my module (`Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductService`) before the final submission.

---

## Part B — Requirement 7 (GRASP audit and correction)

### Phase 1 — Audit

#### Requirement analysis and GRASP concepts

**2026-10-21**
**Query:** I shared the Requirement 7 document and asked what Developer 1 has to audit in Phase 1, what the individual report must contain and which Git commands I needed.
**Use type:** Conceptual (requirement analysis)
**How I used it:** Built my checklist from the assignment table: the products module, the accessories, promotions and warranties hierarchies, `Return` and `Sale.canBeReturned`, and the `exceptions` and `validation` packages, plus the structure of the report (scope, one section per GRASP pattern with a verdict, and the fields of each violation). I later narrowed the scope using `TEAM.md` and the commit history (see the entries below).

**2026-10-21**
**Query:** How to decide between *violation*, *correct* and *not applicable* for a pattern, and how to record a fragment that breaks several patterns without duplicating it.
**Use type:** Conceptual (GRASP patterns)
**How I used it:** Applied the criterion "evidence in the code plus a concrete consequence for a foreseeable change". Fragments that touch several patterns are recorded once, under the pattern that guides the correction (V-01, V-02, V-06, V-09 and V-10), and some points were left as reviewed and not counted (the 30-day return window, the 10% warranty rate, `listVideoGames` and `listConsoles`) instead of forcing violations.

#### Git and PowerShell

**2026-10-21**
**Query:** How to check whether the Technical Leader had already created `feature/grasp-audit`, how to move from my old local branch to the right one, and why `git status` said my branch was behind by 12 commits.
**Use type:** Git command explanation
**How I used it:** Understood that I must join the leader's branch instead of creating another one, and that "behind by 12 commits" only meant my local copy of an old branch (`feature/exception-handling`) was outdated. Used `git fetch origin --prune`, `git branch -r` and `git checkout` to move to the correct branch.

**2026-10-21**
**Query:** Whether the whole report can be uploaded at once, and what the requirement says about commits.
**Use type:** Conceptual / prioritization criteria
**How I used it:** Checked the Git rules of the requirement (atomic commits, push after every commit, at least six commits per member and phase, no direct commits to `develop` or `main`) and kept my report outside the repository until the leader's branch existed. The Technical Leader later set the team's plan to upload the reports in a single commit.

**2026-10-22**
**Query:** Whether I can run `git grep origin/develop` while being on `feature/grasp-audit`, and why several commands failed (`it log` instead of `git log`, `-- sr` instead of `-- src`, a folder that did not exist in my old branch).
**Use type:** Git command explanation
**How I used it:** Used `git grep -n ... origin/develop`, `git show origin/develop:<path>`, `git log` and `git blame -L` to read `develop` without changing branch, to find who calls each method and to check who wrote each line. I fixed my typos and paths and saved the output to a file to give exact line numbers.

**2026-10-22**
**Query:** How to add my audit report to `feature/grasp-audit` from PowerShell: the copy failed because the downloads folder is `Downloads` (not `Descargas`), the file was called `developer1-audit (9).md`, and a missing space joined the two paths of `Copy-Item`. I also asked about the "behind by 53 commits" message and the LF/CRLF warning.
**Use type:** Git command explanation
**How I used it:** Copied the report with the exact name the requirement asks for (`docs/grasp-audit/developer1-audit.md`), deleted a stray copy from the project root, ran `git pull` before committing so my branch was up to date, and understood that the LF/CRLF warning only concerns line endings on Windows. Then committed and pushed `docs: add Developer 1 GRASP audit report`.

#### Review of my own code (audit by module)

**2026-10-21**
**Query:** I pasted `ProductRepository`, `ProductService`, `Product`, `VideoGame` and `Console` and asked for an audit against the nine GRASP patterns.
**Use type:** Review of my own code
**How I used it:** Took three candidate violations: type conditionals in `ProductRepository` (V-01), `ProductService` bound to the concrete repository (V-02) and `listAll()` exposing the internal list (V-03). A fourth one (hardcoded file path) was dropped when I compared with `develop`: the copies I had pasted came from an old branch, and `develop` already receives the path through the constructor. I then re-checked V-01 to V-03 against the real `develop` code with `git grep`.

**2026-10-21**
**Query:** I pasted `Accessory`, `Controller`, `Cable`, `Memory` and `ConsoleCompatible` and asked for the audit of the accessories hierarchy.
**Use type:** Review of my own code
**How I used it:** Took the duplicated console compatibility code in `Controller` and `Memory` as V-04. After seeing that `ConsoleCompatible` already provides a default `isCompatibleWith`, I included the redundant overrides in that finding. I used `git log` and `git show` to see who created the interface and who changed it later.

**2026-10-21**
**Query:** I pasted `Promotion`, `PercentageDiscount`, `CategoryDiscount` and `BulkPurchaseDiscount` and asked for the audit of the promotions hierarchy.
**Use type:** Review of my own code
**How I used it:** Took the percentage rule repeated in three classes (V-05), the category chosen with `instanceof` in `CategoryDiscount` (V-06) and the total of units rebuilt outside `Sale` (V-07, a boundary finding with the Technical Leader's code). I checked with `git grep` that `Sale` has no method for total units and does not depend on `Promotion`.

**2026-10-21**
**Query:** I pasted `Return` and `Sale` and asked for the audit of `Return` and `Sale.canBeReturned`.
**Use type:** Review of my own code
**How I used it:** Took the discount ratio and refund computed twice in `Return` (V-08, boundary with `Sale`) and the receipt formatting inside `Return` (V-09). `Sale.canBeReturned` was judged correct, and the fixed 30-day window was left as reviewed and not counted. With `git grep` I found that the receipt is only called from `SubMenu`, so the formatter belongs in the `ui` package.

**2026-10-21**
**Query:** I pasted `Warranty`, `BasicWarranty` and `ExtendedWarranty` and asked for the audit of the warranties hierarchy.
**Use type:** Review of my own code
**How I used it:** Took the certificate formatting inside `Warranty` (V-10). The hierarchy was recorded as a correct use of polymorphism (abstract duration, type and cost, no conditionals on the type). I also wrote down, for the consolidation, the `instanceof BasicWarranty` I found in `WarrantyRepository`, which is not my code.

**2026-10-22**
**Query:** I pasted the `exceptions` classes and the validators and asked for the audit of both packages.
**Use type:** Review of my own code
**How I used it:** No violation was found in `exceptions`. In `validation` I took the stock rule repeated between `ProductValidator` and `Product.reduceStock` (V-11). Rules duplicated between the model and the validators with different messages were not registered as violations, because unifying them would change what the user sees; they are listed as out-of-scope observations.

#### Checking the assistant's conclusions against the repository

**2026-10-22**
**Query:** Authorship of the code in my scope after the assistant assigned everything to me following the assignment table.
**Use type:** Review of my own code
**How I used it:** Compared the table with `TEAM.md`, `git log` and `git blame`. Four validators (`AccessoryValidator`, `PromotionValidator`, `WarrantyValidator`, `SaleValidator`) were written by Developer 2, and `CsvSupport`, the Requirement 6 refactoring of the repositories and the repository tests by Developer 3, so I removed them from my scope. `Sale.canBeReturned` was added by me in commit `bb8770e`, and `git blame` shows the Technical Leader's account only because of a later reformatting commit, so I kept it. V-01 became a boundary finding with Developer 3.

**2026-10-22**
**Query:** The assistant concluded that `PersistenceException` not extending `GameZoneException` was not a problem because the menu catches it explicitly.
**Use type:** Review of my own code
**How I used it:** I did not accept the conclusion as it was. Checking `SaleService` and Developer 3's reproduction showed that it rethrows only `GameZoneException`, so a `PersistenceException` reaches the menu wrapped in a generic `RuntimeException`. Correcting it changes which exception the menu catches, so I moved it to the out-of-scope observations instead of registering it as a GRASP violation.

**2026-10-22**
**Query:** Final review of my audit report before delivering it.
**Use type:** Review of my own code
**How I used it:** Replaced the draft placeholders with the real audited commit (`22e6df84`) and exact line numbers, added the ownership criteria and the authorship notes, checked that the argument received by `SaleRepository.loadAll` is only read (V-03), and added the boundary finding about the refund restored from the file that Developer 3 reported against `Return`.

#### AI usage log

**2026-10-22**
**Query:** Asked the assistant to update this log with what we did in Phase 1, using the format of my previous log.
**Use type:** Documentation (AI usage log)
**How I used it:** The assistant drafted the Phase 1 entries from our conversation, and I reviewed each one against what I actually did before committing the file.

### Phase 2 — Correction

#### Implementing the corrections

**2026-10-22**
**Query:** I shared my audit report and asked the assistant to correct the eleven violations (V-01 to V-11) following the proposed solution of each one, without changing what the user sees.
**Use type:** Code generation based on my own audit
**How I used it:** The assistant implemented the corrections as one or more atomic `refactor:` commits per violation on a local `feature/grasp-refactor` branch: `ProductStorage` (V-02), the read-only `listAll()` (V-03), `ProductCsvMapper` with `VideoGameCsvMapper` and `ConsoleCsvMapper` (V-01), `CompatibleAccessory` (V-04), `PercentageBasedPromotion` (V-05), `ProductCategory` and `Product.getCategory()` (V-06), `Product.hasStockFor` (V-11), `Sale.getTotalUnits` (V-07), `Sale.getDiscountRatio` (V-08), and `ReturnReceiptFormatter` and `WarrantyCertificateFormatter` in the `ui` package (V-09, V-10). Each design comes from the proposed solution I wrote in Phase 1. I reviewed every commit and its diff on a review page before pushing.

**2026-10-22**
**Query:** Whether the two arithmetic forms of the refund in `Return` (`price * (1 - ratio)` and `price - price * ratio`) could be merged into one method for V-08, as my audit proposed.
**Use type:** Design decision
**How I used it:** A numeric check over 5,000,000 random cases showed that merging them changes the printed cents in about 0.3 % of cases. Following the fallback I had written in V-08, I kept both forms and only shared the discount ratio through `Sale.getDiscountRatio()`. The trade-off is justified in `docs/grasp-refactor-log.md`.

#### Coordination with other members

**2026-10-22**
**Query:** Which corrections change code of other members and in which order the commits must go so the project compiles after each one.
**Use type:** Coordination / Git
**How I used it:** Two commits belong to the Technical Leader: the new methods in `Sale` (V-07, V-08) and the `SubMenu` calls to the formatters (V-09, V-10). For V-09 and V-10 the order is: add the formatter, change the calls in `SubMenu`, then remove the old method. In V-01, Developer 3's `CsvSupport` checks and error messages stay in `ProductRepository` unchanged, and he reviews that commit.

#### Non-regression verification

**2026-10-22**
**Query:** How to prove that the corrections do not change the external behavior of the system.
**Use type:** Testing strategy
**How I used it:** Two checks were run on `develop` and after every commit, besides `mvn clean test`: a harness that prints the output of every method touched by my corrections (212 lines, identical after all 15 code commits), and a full scripted run of the menu comparing the console output (791 lines) and the eight CSV files (identical; only the random UUIDs of returns and warranties differ). The run also showed two crashes that already happen on `develop` (updating stock above the available quantity, and a comma in a product title); I recorded them as out-of-scope defects instead of fixing them. Results are in `docs/grasp-refactor-log.md`.

#### Refactor log

**2026-10-22**
**Query:** Asked the assistant to write my rows of the refactor log.
**Use type:** Documentation (refactor log)
**How I used it:** The assistant drafted `docs/grasp-refactor-log.md` with one row per change (class, change, violation ID, pattern, commit, responsible), the design decisions and the non-regression evidence. I checked it against the commits.
