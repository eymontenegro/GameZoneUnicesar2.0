# Consolidated GRASP Audit — GameZone Unicesar

- **Consolidated by:** Estefany Montenegro (`eymontenegro`), Technical Leader
- **Branch:** `feature/grasp-audit`
- **Audited commit of `develop`:** `22e6df84fc809dd25f7869e22ab2c58f40e15d78` (merge of pull request #37, exception handling)
- **Date:** 2026-10-22
- **Source reports:** `leader-audit.md`, `developer1-audit.md`, `developer2-audit.md`, `developer3-audit.md`
- **Ownership criteria:** `TEAM.md` and the commit history (`git blame`) prevail over the assignment table of the requirement. Where `git blame` shows another account because of shared accounts or "reorganize" commits, the author of the module decides.

**Naming used in this repository:** `Client` is the requirement's `Customer`; `Menu` and `SubMenu` are the requirement's `ConsoleMenu`.

**Team note:** the team has four members. Developer 3 (Xavi Acosta) joined at Requirement 6 and does not appear in the assignment table of the requirement. His report is consolidated like the others. Its location (`docs/grasp-audit/developer3-audit.md`) and acceptance are to be confirmed with the instructor.

**How to read the IDs:** the violations below carry the final IDs `V-01` to `V-30`, grouped by pattern. Each one lists its origin in the individual reports (for example `developer2-audit V-03`).

---

## 1. Coverage table

Every class of the system was audited by its owner, and no class was audited twice. Where the same file has lines from several authors, each member audited the lines he or she wrote (see the remarks).

| Package | Class / member | Audited by | Remarks |
|---|---|---|---|
| `model` | `Product`, `VideoGame`, `Console` | Developer 1 | |
| `model` | `Accessory`, `Controller`, `Cable`, `Memory`, `ConsoleCompatible` | Developer 1 | The default method of `ConsoleCompatible` appears under the Leader's account (reformat commit `cdfc589`); it is Developer 1's design |
| `model` | `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | Developer 1 | |
| `model` | `Return` | Developer 1 | |
| `model` | `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Developer 1 | |
| `model` | `Person`, `Client`, `Seller` | Developer 2 | |
| `model` | `Sale` (all except `canBeReturned`), `SaleDetail` | Technical Leader | Five lines around the Javadoc of `generateReceipt` appear under `yanelis1612`; the method logic is the Leader's |
| `model` | `Sale.canBeReturned()` and `canBeReturned(LocalDate)` | Developer 1 | Added in commit `bb8770e`; shown under the Leader's account only because of reformat commit `f61fefef`. Excluded from the Leader's report |
| `exceptions` | `GameZoneException`, `BusinessRuleException`, `InvalidDataException`, `ResourceNotFoundException` | Developer 1 | |
| `exceptions` | `PersistenceException` | Developer 1 (design) / Developer 3 (21 of 37 lines, among them `extends RuntimeException`) | See FD-02 |
| `validation` | `ProductValidator`, `PersonValidator` | Developer 1 | |
| `validation` | `AccessoryValidator`, `PromotionValidator`, `WarrantyValidator`, `SaleValidator` | Developer 2 | Written by Andrea Laurens (commits `8d6ca41`, `8ccbfe8`, `36a4da1`, `cc3903d`) although the requirement table assigns the package to Developer 1 |
| `persistence` | `CsvSupport` | Developer 3 | |
| `persistence` | `ProductRepository` | Developer 1 (original design) + Developer 3 (lines of the Requirement 6 refactor: 116 of 208) | |
| `persistence` | `PersonRepository` | Developer 2 + Developer 3 (129 of 247 lines) | |
| `persistence` | `SaleRepository` | Technical Leader (`saveAll`, `loadAll` signature, lookup maps) + Developer 3 (229 of 354 lines, among them loading and the two file loops) | The Leader's entry "line-by-line reading" refers to the lines she wrote; the file loops written by Developer 3 were audited by him (V-26) |
| `persistence` | `AccessoryRepository`, `PromotionRepository`, `ReturnRepository`, `WarrantyRepository` | Developer 2 + Developer 3 (148, 101, 127 and 113 lines) | |
| `service` | `ProductService` | Developer 1 (all, except `restoreStock`) | Validator and typed-exception lines audited by Developer 2 (see below) |
| `service` | `ProductService.restoreStock` | Technical Leader | Added in commit `6e590e8e`; lines 213–214 and 218 (typed exceptions) audited by Developer 2; lines shown under `yanelis1612` come from reformat commit `40350836` |
| `service` | `SaleService` | Technical Leader | Lines 3–5, 16–17, 65, 75, 81–82, 84–85, 89, 143–145, 169, 172, 174, 177, 179, 180 (validators and typed exceptions) audited by Developer 2 |
| `service` | `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService` (with `generateMonthlyBalance`), `WarrantyService` | Developer 2 | |
| `ui` | `Menu`, `SubMenu` | Technical Leader | |
| (root) | `Main` | Technical Leader | |
| `src/test` | Seven `*RepositoryTest` classes | Developer 3 | |

**Coverage result:** complete. No class was left unaudited and none was audited twice.

**Points checked and accepted**
- The audits of `restoreStock`, of the four validators and of the Requirement 6 lines in `SaleService` and `ProductService` are split by line authorship, as agreed in the four reports.
- Developer 3's report claims `TEAM.md` assigns him the Product, Person and Sale repositories, while the other members audit them as original authors. This is resolved by line authorship: each member audited the lines he or she wrote, and the original design belongs to its author.

---

## 2. Team verdict per pattern

| # | Pattern | Leader | Dev 1 | Dev 2 | Dev 3 | **Team verdict** | Violations |
|---|---|---|---|---|---|---|---|
| 1 | Information Expert | Violation | Violation | Violation | Violation | **Violation** | V-01 to V-07 |
| 2 | Creator | Violation | Correct | Correct | Correct | **Violation** | V-08 |
| 3 | Controller | Correct | Not applicable | Not applicable | Not applicable | **Correct** | — (see note) |
| 4 | Low Coupling | Violation | Violation | Violation | Violation | **Violation** | V-09 to V-12 |
| 5 | High Cohesion | Violation | Violation | Violation | Violation | **Violation** | V-13 to V-17 |
| 6 | Polymorphism | Violation | Violation | Violation | Violation | **Violation** | V-18 to V-24 |
| 7 | Pure Fabrication | Violation | Violation (via V-09/V-10 of his report) | Violation | Violation | **Violation** | V-25, V-26 (V-14 to V-16 are corrected with this pattern) |
| 8 | Indirection | Violation | Violation | Violation | Correct | **Violation** | V-27 |
| 9 | Protected Variations | Violation | Violation | Violation | Violation | **Violation** | V-28 to V-30 (V-18 to V-27 are also corrected to protect against foreseeable changes) |

**Reasoning**

1. **Information Expert.** Correct in many places (`Sale.calculateSubtotal`, `Product.reduceStock`, `Warranty.isActive`, `Client.addPurchase`). Seven fragments ask for information from outside its owner (V-01 to V-07).
2. **Creator.** Correct in services and repositories, which hold the data and the container of what they create. One violation: the UI builds `SaleDetail` (V-08).
3. **Controller.** `Menu.displayMenu` receives the system event, delegates to `SubMenu` and centralizes exception handling in one `try` with ordered `catch` blocks. The other three members have no point of application (services, repositories and model receive no events). The size of `SubMenu` (a bloated controller) is registered once, under High Cohesion (V-13), and not here.
4. **Low Coupling.** Dependencies follow ui → service → persistence → model, and paths are injected. Four violations: unused services passed through `Menu`, an unused parameter, `WarrantyRepository` tied to five repositories, and dead constructors that build concrete repositories.
5. **High Cohesion.** Most classes are focused. Violations: `SubMenu` with seven responsibilities, receipt and certificate formatting inside three domain classes, and a financial report inside `ReturnService`.
6. **Polymorphism.** The `Promotion`, `Warranty` and `Person` hierarchies and the `ConsoleCompatible` capability are well applied. Violations: type checks with `instanceof` or type text in `SaleService`, `SubMenu`, `CategoryDiscount`, `AccessoryService` and four repositories.
7. **Pure Fabrication.** Services, repositories and static validators keep file access and rules out of the model, and `CsvSupport` is a well-made fabrication. Two violations: duplicated console reading and duplicated file mechanics because the extraction of `CsvSupport` was left incomplete.
8. **Indirection.** Repositories isolate `IOException`, `SubMenu` sits between `Menu` and the services, and the abstract `Promotion`, `Warranty` and `ConsoleCompatible` are correct indirections. The persistence layer alone is correct (Developer 3), but every service depends on a concrete repository (V-27).
9. **Protected Variations.** Strong protection against I/O failures, defensive copies and private attributes. Three own violations; the unprotected points of variation already registered under another pattern are cross-referenced, not repeated.

---

## 3. Definitive list of violations

Summary:

| ID | Pattern | Subject | Responsible (lead) | Boundary |
|---|---|---|---|---|
| V-01 | Information Expert | Catalog lookup done by `SaleService` | Leader | Possible (Dev 2) |
| V-02 | Information Expert | Total units rebuilt outside `Sale` | Dev 1 | Yes (Leader) |
| V-03 | Information Expert | Discount ratio and refund computed twice in `Return` | Dev 1 | Yes (Leader) |
| V-04 | Information Expert | Stock rule repeated in `ProductValidator` | Dev 1 | No |
| V-05 | Information Expert | Product search in a sale done by `ReturnService` / `ReturnRepository` | Dev 2 | Yes (Leader) |
| V-06 | Information Expert | Expiry rule outside `Warranty` | Dev 2 | Yes (Dev 1) |
| V-07 | Information Expert | Refund arithmetic in `ReturnRepository` | Dev 3 | Yes (Dev 1) |
| V-08 | Creator | `SaleDetail` created by the user interface | Leader | No |
| V-09 | Low Coupling | `Menu` receives services it does not use | Leader | No |
| V-10 | Low Coupling | Unused `promotions` parameter in `SaleRepository.loadAll` | Leader | Yes (Dev 2, Dev 3) |
| V-11 | Low Coupling | `WarrantyRepository` coupled to five repositories | Dev 2 | Yes (Leader, Dev 3) |
| V-12 | Low Coupling | Unused constructors that create concrete repositories | Dev 1 and Dev 2 | No |
| V-13 | High Cohesion | `SubMenu` handles every module | Leader | No |
| V-14 | High Cohesion | Receipt formatting inside `Sale` | Leader | No |
| V-15 | High Cohesion | Receipt formatting inside `Return` | Dev 1 | Yes (Leader) |
| V-16 | High Cohesion | Certificate formatting inside `Warranty` | Dev 1 | Yes (Leader) |
| V-17 | High Cohesion | Monthly balance inside `ReturnService` | Dev 2 | Yes (Leader) |
| V-18 | Polymorphism | `instanceof` on product type in `SaleService` and `SubMenu` | Leader | Yes (Dev 1, Dev 2) |
| V-19 | Polymorphism | Category chosen with `instanceof` in `CategoryDiscount` | Dev 1 | No |
| V-20 | Polymorphism | Type chosen in `AccessoryService.matchesType` | Dev 2 | Yes (Dev 1) |
| V-21 | Polymorphism | Type conditionals in `ProductRepository` | Dev 1 | Yes (Dev 3) |
| V-22 | Polymorphism | Type conditionals in `AccessoryRepository` | Dev 2 | Yes (Dev 3) |
| V-23 | Polymorphism | Type conditionals in `PromotionRepository` | Dev 2 | Yes (Dev 3) |
| V-24 | Polymorphism | Type conditionals in `WarrantyRepository` | Dev 2 | Yes (Dev 3) |
| V-25 | Pure Fabrication | Console input reading duplicated | Leader | No |
| V-26 | Pure Fabrication | File reading and writing duplicated in the repositories | Dev 3 | Yes (Dev 2) |
| V-27 | Indirection | Services bound to concrete repositories | Leader, Dev 1, Dev 2 (one part each) | Yes |
| V-28 | Protected Variations | Internal product list exposed | Dev 1 | No |
| V-29 | Protected Variations | Console compatibility implemented twice | Dev 1 | No |
| V-30 | Protected Variations | Percentage rule implemented three times | Dev 1 | No |

---

### Information Expert

### V-01 — Catalog lookup done by `SaleService`

| Field | Content |
|---|---|
| **Pattern** | Information Expert |
| **Module** | Sales (Workshop 1), integrated in Requirement 1 |
| **Evidence** | `SaleService.findProductOrAccessoryById`, lines 68–76: `productService.listAll().stream().filter(p -> p != null && productId.equals(p.getId()))...orElseGet(() -> accessoryService.listAllAccessories().stream()...)` |
| **Explanation** | The services that own the product and accessory catalogs hold the information needed to find an item by id. `SaleService` pulls both full lists and searches them itself. |
| **Consequence** | Any change in how catalogs store or index items forces a change in `SaleService`; the search logic is duplicated outside its owners. |
| **Agreed solution** | `SaleService` asks each service to find by id (`ProductService.findById` already exists) and only combines the two answers. The `ResourceNotFoundException` for "producto o accesorio" keeps the same message. Pattern: Information Expert. Caution: `ProductService.findById` may throw on a miss; the lookup must keep the same order (products first, then accessories) and the same final error. |
| **Origin** | leader-audit V-02 |
| **Responsible** | Technical Leader |
| **Other code** | Developer 2 adds `AccessoryService.findById` only if it does not exist yet |

### V-02 — Total units rebuilt outside `Sale`

| Field | Content |
|---|---|
| **Pattern** | Information Expert |
| **Module** | Promotions (Requirement 2), boundary with Sales |
| **Evidence** | `BulkPurchaseDiscount.calculateDiscount`, lines 57–62: `int totalUnits = 0; for (SaleDetail detail : sale.getDetails()) { if (detail != null) { totalUnits += detail.getQuantity(); } }`. `PercentageDiscount.calculateDiscount` asks the sale for its subtotal instead. `Sale` has no method that returns the units. |
| **Explanation** | `Sale` holds the details, so it is the expert on how many units were sold; the promotion navigates `Sale` and `SaleDetail` to rebuild it. |
| **Consequence** | Any other class that needs the units repeats the rule, and a change in how `Sale` stores details breaks the promotion. |
| **Agreed solution** | Leader adds `Sale.getTotalUnits()` (sum of the quantity of non-null details, 0 without details); Developer 1 makes `BulkPurchaseDiscount` call it. Results stay identical. Pattern: Information Expert. |
| **Origin** | developer1-audit V-07 |
| **Responsible** | Developer 1 (lead) |
| **Other code** | Leader (`Sale.getTotalUnits`, committed first) |

### V-03 — Discount ratio and refund computed twice in `Return`

| Field | Content |
|---|---|
| **Pattern** | Information Expert (also affects Protected Variations; recorded once) |
| **Module** | Returns (Requirement 3), boundary with Sales |
| **Evidence** | `Return.calculateRefundAmount` (lines 107–109, 113) and `Return.generateReturnReceipt` (lines 131–133, 148–150) both compute `discountRatio = (subtotalSale > 0) ? (discountSale / subtotalSale) : 0.0`. The per-item refund uses two arithmetic forms: `product.getPrice() * (1.0 - discountRatio)` and `listPrice - listPrice * discountRatio`. |
| **Explanation** | The ratio derives only from `Sale` data (subtotal and discount), yet `Return` computes it twice; the per-item rule also exists in two places. |
| **Consequence** | A change in the proportional refund must be repeated twice, and the receipt amounts and the stored `refundAmount` can drift apart. |
| **Agreed solution** | Leader adds `Sale.getDiscountRatio()` (same expression, 0.0 when the subtotal is not positive). Developer 1 uses it in `Return` through one private method that computes the refund of one product, shared by both methods. Printed amounts are checked identical in the regression run; if any difference appears, each original arithmetic form is kept and only the ratio is shared. Pattern: Information Expert. Scheduled together with V-07 (same refund calculation) and before V-15. |
| **Origin** | developer1-audit V-08 |
| **Responsible** | Developer 1 (lead) |
| **Other code** | Leader (`Sale.getDiscountRatio`, committed first) |

### V-04 — Stock rule repeated in `ProductValidator`

| Field | Content |
|---|---|
| **Pattern** | Information Expert |
| **Module** | Exceptions and validations (Requirement 6) |
| **Evidence** | `ProductValidator.validateStockAvailability`, lines 56–62: `if (product.getStock() < requestedQuantity) { throw new BusinessRuleException(...) }`. `Product.reduceStock`, lines 65–67 repeats the comparison with `IllegalArgumentException`. Called from `SaleService`, line 89. |
| **Explanation** | `Product` owns the stock and already decides whether there is enough; the validator reads it from outside and repeats the decision. |
| **Consequence** | If the rule changes (reserved or minimum stock), it must change in two classes and they can disagree. |
| **Agreed solution** | Add `Product.hasStockFor(int quantity)` (`stock >= quantity`) and use it in `Product.reduceStock` and `ProductValidator.validateStockAvailability`. Each keeps its own exception type and message. Pattern: Information Expert. |
| **Origin** | developer1-audit V-11 |
| **Responsible** | Developer 1 |
| **Other code** | No |

### V-05 — Product search in a sale done by `ReturnService` and `ReturnRepository`

| Field | Content |
|---|---|
| **Pattern** | Information Expert |
| **Module** | Returns (Requirement 3) |
| **Evidence** | `ReturnService.findProductInSale`, lines 56–65: loops over `sale.getDetails()` and reads `detail.getProduct().getId()`. Same search repeated in `ReturnRepository.findProductInSale`, lines 138–149. `Sale` has no lookup method. |
| **Explanation** | `Sale` owns its lines, so it is the expert on which products it contains. The service reaches through `Sale`, `SaleDetail` and `Product`. |
| **Consequence** | If `Sale` changes how it stores lines, both callers break; any new class that needs the search repeats the loop. |
| **Agreed solution** | Leader adds `Sale.findProductById(String)`; Developer 2 makes both `findProductInSale` methods call it, keeping the same exception and message when the product is not in the sale. Pattern: Information Expert. |
| **Origin** | developer2-audit V-01 |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Leader (`Sale.findProductById`, committed first) |

### V-06 — Expiry rule outside `Warranty`

| Field | Content |
|---|---|
| **Pattern** | Information Expert |
| **Module** | Warranties (Requirement 4) |
| **Evidence** | `WarrantyService.listWarrantiesExpiringSoon`, lines 145–148: reads `w.getEndDate()` and compares it with `today` and `limit`. In the same class `listActiveWarranties` (line 132) asks `w.isActive(today)` (`Warranty.java`, line 63). |
| **Explanation** | The validity rule is split: `Warranty` answers "is it active?" but the service decides "does it expire soon?" from the end date. |
| **Consequence** | If the way the end date is treated changes, two classes must change. |
| **Agreed solution** | Developer 1 adds `Warranty.expiresBetween(LocalDate from, LocalDate to)` with the same comparison; Developer 2 makes the service call it. The result stays identical. Pattern: Information Expert. |
| **Origin** | developer2-audit V-02 |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Developer 1 (`Warranty.expiresBetween`, committed first) |

### V-07 — Refund arithmetic in `ReturnRepository`

| Field | Content |
|---|---|
| **Pattern** | Information Expert |
| **Module** | Persistence (Returns, refactored in Requirement 6) and Returns (Requirement 3) |
| **Evidence** | `ReturnRepository`, lines 196–205: `new Return(id, date, sale, returnedProducts, reason, 0.0)` (197), `double warrantyPart = storedRefund - result.getRefundAmount();` (200), `if (warrantyPart > 0.005) { result.setWarrantyRefundAmount(warrantyPart); }` (201–202). The cause is the `Return` constructor (lines 34–42): its last parameter is the warranty part and it adds the products refund on top, while the file stores the total. Reproduced: a refund of `100.0` became `200.0` after reload before this compensation. |
| **Explanation** | `Return` is the expert on refunds; the repository patches a domain rule that `Return` cannot express. |
| **Consequence** | A change in how `Return` computes the refund can break the reload again without any compile error. |
| **Agreed solution** | Developer 1 adds in `Return` a way to restore a stored total refund (constructor or factory for stored data). Then Developer 3 removes the arithmetic from the repository. Test `refundIsNotDoubledAfterReload` must keep passing. Pattern: Information Expert. Scheduled together with V-03. |
| **Origin** | developer3-audit V-01; confirmed in the notes of developer1-audit |
| **Responsible** | Developer 3 (lead) |
| **Other code** | Developer 1 (`Return`, committed first) |

---

### Creator

### V-08 — `SaleDetail` created by the user interface

| Field | Content |
|---|---|
| **Pattern** | Creator |
| **Module** | Sales UI (Workshop 1) |
| **Evidence** | `SubMenu.processSaleForm`, line 548 (quantity read at line 546): `items.add(new SaleDetail(p, qty, p.getPrice()));`. The only other `new SaleDetail` in `src/main` is `SaleRepository`, line 348, which rebuilds stored lines. `Sale` aggregates the details (`details`, `addDetail`). |
| **Explanation** | `Sale` aggregates `SaleDetail`, so it is the natural creator. The UI knows the constructor and decides the unit price. |
| **Consequence** | A change in how a sale line is built (for example a price rule) forces an edit in the interface. |
| **Agreed solution** | Let the sale create its lines (an `addItem(Product, int)`-style method in `Sale`). `Sale.addDetail(SaleDetail)` stays because the repository loads lines with the stored price. The correction must keep the moment of the error: today the `SaleDetail` constructor fails right after the quantity is typed. If that cannot be preserved, the case is justified in the change log or moved to out-of-scope. Pattern: Creator. |
| **Origin** | leader-audit V-03 |
| **Responsible** | Technical Leader |
| **Other code** | No |

---

### Low Coupling

### V-09 — `Menu` receives services it does not use

| Field | Content |
|---|---|
| **Pattern** | Low Coupling |
| **Module** | Sales UI and startup (Workshop 1) |
| **Evidence** | `Menu` constructor (`Menu.java`, line 28) receives seven services and only forwards them: `new SubMenu(productService, ..., warrantyService, this.scanner)` (line 36). The `SubMenu` constructor (`SubMenu.java`, line 56) receives the same seven. `Main`, line 88, builds `new Menu(...)` with all of them. |
| **Explanation** | `Menu` depends on seven concrete classes that it never uses. |
| **Consequence** | Adding a module forces changes in `Main`, `Menu` and `SubMenu` at the same time. |
| **Agreed solution** | `Main` creates the sub-menus and passes them to `Menu`; each sub-menu receives only the services it uses. Pattern: Low Coupling. Applied after V-25 and V-13, because it depends on the split. |
| **Origin** | leader-audit V-06 |
| **Responsible** | Technical Leader |
| **Other code** | No |

### V-10 — Unused `promotions` parameter in `SaleRepository.loadAll`

| Field | Content |
|---|---|
| **Pattern** | Low Coupling |
| **Module** | Sales persistence (Workshop 1), extended in Requirement 2 |
| **Evidence** | `SaleRepository.loadAll(..., List<? extends Promotion> promotions)`, line 169 (Javadoc line 160): the parameter is never used. Callers: `SaleService` constructor (lines 53–59, where `promotionService.listAllPromotions()` is requested only to be passed), `WarrantyRepository` (lines 233 and 235), `SaleRepositoryTest` (lines 66 and 305). |
| **Explanation** | The repository and its callers depend on `Promotion` without needing it. |
| **Consequence** | Callers load promotions just to call `loadAll`; any change to `Promotion` can break unrelated code. |
| **Agreed solution** | Remove the parameter, keeping the build compiling after every commit: (1) Leader adds an overload without the parameter; (2) the other callers migrate (Developer 3 the tests, Developer 2 `WarrantyRepository`, unless V-11 has already removed the call); (3) Leader removes the old version. Pattern: Low Coupling. Applied before V-11 and V-27. |
| **Origin** | leader-audit V-07 + developer3-audit V-05 (same fragment) |
| **Responsible** | Technical Leader (lead) |
| **Other code** | Developer 2 (`WarrantyRepository`), Developer 3 (`SaleRepositoryTest`) |

### V-11 — `WarrantyRepository` coupled to five repositories

| Field | Content |
|---|---|
| **Pattern** | Low Coupling (also affects High Cohesion; recorded once) |
| **Module** | Warranties (Requirement 4) / Persistence |
| **Evidence** | `WarrantyRepository`: five repository fields (lines 45–49), a constructor that requires them (lines 63–102) and `loadAll` (lines 229–235), which loads clients, sellers, products, accessories and promotions and then calls `saleRepository.loadAll(...)`. It imports nine model classes. `Main` (lines 60–67) builds it with the five repositories; a comment at `Main:52` acknowledges it. `ReturnRepository.loadAll(List<Sale> sales)` solves the same need with a parameter. |
| **Explanation** | To resolve the sale and product of each warranty the repository depends on five repositories and must know how each loads and in which order. It orchestrates the loading of five other aggregates. |
| **Consequence** | Loading warranties reads six CSV files again; warranties reference their own copies of sales, clients and products instead of the objects held by the services; any change in `SaleRepository.loadAll` breaks it. |
| **Agreed solution** | `WarrantyRepository.loadAll` receives the already loaded sales and products, as `ReturnRepository` does, and the five repository dependencies are removed. Warranties must be loaded after the sales exist (`SaleService` needs `WarrantyService` when it is built, so the construction order in `Main` is adapted). Applied after V-10. Verification: warranty menu options (list all, active, expiring soon, find by product, certificate) and a sale with an extended warranty are run before and after, comparing printed output and `data/warranties.csv`. If any difference appears, the finding is moved to out-of-scope with that evidence. This is the most invasive correction of the audit and is scheduled last among the boundary corrections. Pattern: Low Coupling. |
| **Origin** | developer2-audit V-03 + developer3-audit V-04 + leader-audit boundary observation 3 |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Leader (`Main` construction order, `SaleService` if needed), Developer 3 (`WarrantyRepositoryTest`, which builds the repository) |

### V-12 — Unused constructors that create concrete repositories

| Field | Content |
|---|---|
| **Pattern** | Low Coupling (the same fragments also hide the dependency from Creator and Indirection; recorded once here) |
| **Module** | Products (Workshop 1), People (Workshop 1), Warranties (Requirement 4) |
| **Evidence** | `WarrantyService()`, lines 40–48: `new WarrantyRepository(new SaleRepository(), new ProductRepository(), new PersonRepository(), new AccessoryRepository(), new PromotionRepository())`; `Main` (line 74) uses `new WarrantyService(warrantyRepository)`. `PersonService()`, lines 31–33, creates a `PersonRepository` and is not called. `ProductService()`, lines 28–30 (`this(new ProductRepository());`) is not called anywhere in `src`; `Main` (line 70) uses `new ProductService(productRepository)`. |
| **Explanation** | The services are tied to concrete classes at construction time although they only need an injected repository. |
| **Consequence** | A change to a repository constructor breaks the service, and the dead constructor hides the real dependency. |
| **Agreed solution** | Remove the three unused no-argument constructors. Behavior does not change. Pattern: Low Coupling. This replaces the "remove the no-arg constructor" part of developer1-audit V-02; the interface part stays in V-27. |
| **Origin** | developer2-audit V-04 + developer1-audit V-02 (constructor part) + developer3-audit §3.2 observation |
| **Responsible** | Developer 1 (`ProductService()`), Developer 2 (`WarrantyService()`, `PersonService()`) |
| **Other code** | No |

---

### High Cohesion

### V-13 — `SubMenu` handles every module

| Field | Content |
|---|---|
| **Pattern** | High Cohesion |
| **Module** | Sales UI (Workshop 1), extended in Requirements 1 to 4 |
| **Evidence** | `SubMenu.java` (627 lines, one class): products 77–134, accessories 135–221, persons 222–263, promotions 264–330, returns 331–358 and 432–530, warranties 359–431, sales 531–606, input helpers 607–627. It repeats the same input block (ID, title, price, stock) in several branches (lines 93–94, 105–106, 154–155, 166–167, 177–178). |
| **Explanation** | One class carries seven responsibilities, so it has many reasons to change and is hard to understand. This is also the "bloated controller" of the Controller pattern. |
| **Consequence** | Every new module or service enlarges the same class and creates merge conflicts. |
| **Agreed solution** | Split into one sub-menu per module, with `Menu` as entry point. Texts and flow stay exactly the same. Pattern: High Cohesion. Applied after V-25 and before V-09. |
| **Origin** | leader-audit V-04 |
| **Responsible** | Technical Leader |
| **Other code** | No |

### V-14 — Receipt formatting inside `Sale`

| Field | Content |
|---|---|
| **Pattern** | High Cohesion (Pure Fabrication guides the correction; recorded once) |
| **Module** | Sales (Workshop 1), extended in Requirement 2 |
| **Evidence** | `Sale.generateReceipt()`, lines 202–236: builds a Spanish receipt with `DateTimeFormatter`, `StringBuilder` and `String.format`. No caller exists in `src`. |
| **Explanation** | The domain class mixes sale data and rules with the formatting of a printed receipt. |
| **Consequence** | A change in the receipt format forces a change in the model. |
| **Agreed solution** | Move the formatting to a presentation class in `ui` with exactly the same text. Pattern: Pure Fabrication, restoring High Cohesion. |
| **Origin** | leader-audit V-08 |
| **Responsible** | Technical Leader |
| **Other code** | No |

### V-15 — Receipt formatting inside `Return`

| Field | Content |
|---|---|
| **Pattern** | High Cohesion (Pure Fabrication guides the correction; recorded once) |
| **Module** | Returns (Requirement 3) |
| **Evidence** | `Return.generateReturnReceipt`, lines 127–168: builds the console receipt with `DateTimeFormatter.ofPattern("dd/MM/yyyy")`, banner lines and `String.format`. Called only from `SubMenu` (lines 451, 465, 484, 503). |
| **Explanation** | `Return` has two reasons to change: the refund rules and the layout of a user-facing document. |
| **Consequence** | Changing the layout means editing the class that holds the refund rules; the rules cannot be tested without the presentation code. |
| **Agreed solution** | Create `ReturnReceiptFormatter.format(Return)` in `ui` producing exactly the same text. Order: Developer 1 adds the formatter without removing anything; Leader changes the four calls in `SubMenu`; Developer 1 removes `generateReturnReceipt`. Applied after V-03. Pattern: Pure Fabrication. |
| **Origin** | developer1-audit V-09 |
| **Responsible** | Developer 1 (lead) |
| **Other code** | Leader (four calls in `SubMenu`; if V-13 is already done, in the returns sub-menu) |

### V-16 — Certificate formatting inside `Warranty`

| Field | Content |
|---|---|
| **Pattern** | High Cohesion (Pure Fabrication guides the correction; recorded once) |
| **Module** | Warranties (Requirement 4) |
| **Evidence** | `Warranty.generateWarrantyCertificate`, lines 72–82: builds the certificate with `DateTimeFormatter.ofPattern("dd/MM/yyyy")` and a concatenated layout. Called only from `SubMenu` (lines 394, 424). |
| **Explanation** | `Warranty` has two reasons to change: the warranty rules and the layout of a document. |
| **Consequence** | Changing the wording means editing the base class of the whole warranty hierarchy. |
| **Agreed solution** | Create `WarrantyCertificateFormatter.format(Warranty)` in `ui` producing exactly the same text, including how `getAdditionalCost()` is printed (without number formatting; see FD-17). Same order as V-15. Pattern: Pure Fabrication. |
| **Origin** | developer1-audit V-10 |
| **Responsible** | Developer 1 (lead) |
| **Other code** | Leader (two calls in `SubMenu`) |

### V-17 — Monthly balance inside `ReturnService`

| Field | Content |
|---|---|
| **Pattern** | High Cohesion |
| **Module** | Returns (Requirement 3) |
| **Evidence** | `ReturnService.generateMonthlyBalance`, lines 157–176: reads every sale through `saleService.listAllSales()` (158), filters by month and year, adds `Sale::calculateFinalTotal` (163) and subtracts the returns of the month (166–173). The rest of the class only processes and queries returns. Called from `SubMenu`, line 524. |
| **Explanation** | Half of the method computes monthly sales revenue, unrelated to processing returns. |
| **Consequence** | Two reasons to change; `ReturnService` needs `SaleService` partly only for this report. |
| **Agreed solution** | Extract a `MonthlyBalanceService` in `service` receiving `SaleService` and `ReturnService`, with the same formula and order of operations. `ReturnService` keeps only return rules. Pattern: High Cohesion. |
| **Origin** | developer2-audit V-05 |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Leader (`SubMenu` line 524 and `Main` construction) |

---

### Polymorphism

### V-18 — `instanceof` on product type in `SaleService` and `SubMenu`

| Field | Content |
|---|---|
| **Pattern** | Polymorphism (also affects Protected Variations; recorded once) |
| **Module** | Sales (Workshop 1), integrated in Requirement 1 (accessories) and Requirement 4 (warranties) |
| **Evidence** | `SaleService.processSale`, lines 108–112: `if (product instanceof Accessory) { accessoryService.updateStock(...); } else { productService.updateStock(...); }`; line 120: `if (product instanceof Console) { warrantyService.assignBasicWarranty(...); }`. `SaleService.restoreStock`, lines 154–158: same accessory/product branch. `SubMenu.processSaleForm`, line 551: `if (p instanceof Console) { ... "¿Desea agregar garantía extendida ..." }`. |
| **Explanation** | What varies by product type (which service handles its stock, whether it gets a basic warranty, whether an extended warranty is offered) is decided with conditionals on the type instead of by the hierarchy. |
| **Consequence** | A new product type forces edits in four places in `SaleService` and `SubMenu`, with the risk of forgetting one. |
| **Agreed solution** | Developer 1 moves the variation into the `Product` hierarchy (methods that say whether a basic warranty applies and whether an extended warranty can be offered). Stock handling gets one entry point that routes by the item's own type, without `instanceof`. The exact signatures are fixed by Developer 1 in the first commit and are coordinated with V-19 and V-20, which also add type descriptors to `Product` and `Accessory`. User-visible messages and their order do not change. Pattern: Polymorphism. |
| **Origin** | leader-audit V-01 |
| **Responsible** | Technical Leader (lead) |
| **Other code** | Developer 1 (`Product`, `Console`, `Accessory`), Developer 2 (`AccessoryService`) |

### V-19 — Category chosen with `instanceof` in `CategoryDiscount`

| Field | Content |
|---|---|
| **Pattern** | Polymorphism (also affects Low Coupling and Protected Variations; recorded once) |
| **Module** | Promotions (Requirement 2) |
| **Evidence** | `CategoryDiscount.matchesCategory`, lines 76–90: `String cat = category.trim().toUpperCase(); if (cat.equals("VIDEOGAME")) { return product instanceof VideoGame; } else if (cat.equals("CONSOLE")) ... else if (cat.equals("ACCESSORY")) ...` |
| **Explanation** | A category string is translated into a check on the concrete subclass; the promotion knows the concrete product classes, when the product itself knows its category. |
| **Consequence** | Every new category or `Product` subclass requires editing `CategoryDiscount`; a missed branch silently returns false and the discount is not applied. |
| **Agreed solution** | Add a `ProductCategory` enum (`VIDEOGAME`, `CONSOLE`, `ACCESSORY`) and an abstract `getCategory()` in `Product`, implemented by `VideoGame`, `Console` and `Accessory`. `matchesCategory` keeps `trim().toUpperCase()` and compares with `product.getCategory().name()`; an unknown category still returns false. `targetCategory` stays a `String`. Pattern: Polymorphism (with Information Expert). |
| **Origin** | developer1-audit V-06 |
| **Responsible** | Developer 1 |
| **Other code** | No (`extends Product` appears only in Developer 1 classes) |

### V-20 — Type chosen in `AccessoryService.matchesType`

| Field | Content |
|---|---|
| **Pattern** | Polymorphism |
| **Module** | Accessories (Requirement 1) |
| **Evidence** | `AccessoryService.matchesType`, lines 126–135: an `if / else if` chain compares `"CONTROLLER"`, `"CABLE"`, `"MEMORY"` and tests `accessory instanceof Controller`, `Cable` or `Memory`. Used by `listAccessoriesByType` (line 121). |
| **Explanation** | The service decides by the concrete type of each accessory instead of asking the accessory. |
| **Consequence** | A new accessory type is silently ignored by `listAccessoriesByType` until someone adds a branch; the service imports every concrete class. |
| **Agreed solution** | Developer 1 adds an abstract `getAccessoryKind()` to `Accessory` (not `getType`, because `Memory.getType` already exists), implemented by `Controller`, `Cable`, `Memory`. Developer 2 turns `matchesType` into a comparison with it. The three accepted texts and the empty result for an unknown text stay the same. Pattern: Polymorphism. |
| **Origin** | developer2-audit V-06 |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Developer 1 (`Accessory` and subclasses, committed first) |

### V-21 — Type conditionals in `ProductRepository`

| Field | Content |
|---|---|
| **Pattern** | Polymorphism (also affects Protected Variations; recorded once) |
| **Module** | Products (Workshop 1), refactored in Requirement 6 |
| **Evidence** | `ProductRepository.productToCsvLine` (lines 75–89): `instanceof VideoGame` / `Console` / `else throw new IllegalArgumentException("Unknown product type")`. `csvLineToProduct` (lines 166–172): `type.equals("VIDEOGAME")` / `"CONSOLE"` / `else throw CsvSupport.corruptLine(...)`. Conditional lines are Developer 3's (commit `ca6cb5d3`); type codes (77, 83) and the `Unknown product type` throw (87–88) are Developer 1's. |
| **Explanation** | Both methods select behavior by the concrete type or a type code through conditionals instead of delegating to a per-type collaborator. |
| **Consequence** | A new product type requires editing both methods; an unlisted subtype fails at runtime. |
| **Agreed solution** | A `ProductCsvMapper` interface in `persistence` (`supports(Product)`, `toSpecificColumns(Product)`, `fromColumns(...)`) with `VideoGameCsvMapper` and `ConsoleCsvMapper`. The repository keeps the common columns and iterates its mappers. CSV format and both error paths stay identical (`IllegalArgumentException("Unknown product type")` when saving; the `corruptLine` `PersistenceException` "tipo de producto desconocido" when loading). `ProductRepositoryTest` keeps passing. Pattern: Polymorphism. Same convention in V-22 to V-24. |
| **Origin** | developer1-audit V-01 + developer3-audit V-02 (`ProductRepository` part) |
| **Responsible** | Developer 1 (lead) |
| **Other code** | Developer 3 (moves his validation and `corruptLine` paths into the mappers) |

### V-22 — Type conditionals in `AccessoryRepository`

| Field | Content |
|---|---|
| **Pattern** | Polymorphism |
| **Module** | Accessories (Requirement 1), refactored in Requirement 6 |
| **Evidence** | `AccessoryRepository.accessoryToCsvLine`, lines 93–116: `instanceof Controller` / `Cable` / `Memory` choose the tag and extra columns; `"Unknown accessory type"` at line 115. `csvLineToAccessory`, lines 188–213: the same three cases by tag text. |
| **Explanation** | Each accessory type is handled by a conditional in two places that must be kept in sync by hand. |
| **Consequence** | A new accessory type requires editing two methods and a forgotten branch only fails at run time. |
| **Agreed solution** | One CSV mapper per accessory type in `persistence`, with the same design as V-21. File content identical. Pattern: Polymorphism. |
| **Origin** | developer2-audit V-07 + developer3-audit V-02 (`AccessoryRepository` part) |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Developer 3 (lines 93, 98, 106, 188, 195, 202 and his validation paths). Developer 2's report said "No"; the consolidation corrects it because those lines are Developer 3's |

### V-23 — Type conditionals in `PromotionRepository`

| Field | Content |
|---|---|
| **Pattern** | Polymorphism |
| **Module** | Promotions (Requirement 2), refactored in Requirement 6 |
| **Evidence** | `PromotionRepository.promotionToCsvLine`, lines 75–90 (`instanceof PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`; `"Unknown promotion type"` at 89) and `csvLineToPromotion`, lines 161–183 (same three cases by tag). |
| **Explanation** | Same problem as V-22, applied to promotions. |
| **Consequence** | A new promotion type requires editing two methods and fails at run time if one is forgotten. |
| **Agreed solution** | One CSV mapper per promotion type, same design as V-21. File content identical. Pattern: Polymorphism. |
| **Origin** | developer2-audit V-08 + developer3-audit V-02 (`PromotionRepository` part) |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Developer 3 (lines 75, 79, 84, 161, 166, 173 and his validation paths) |

### V-24 — Type conditionals in `WarrantyRepository`

| Field | Content |
|---|---|
| **Pattern** | Polymorphism |
| **Module** | Warranties (Requirement 4), refactored in Requirement 6 |
| **Evidence** | `WarrantyRepository.warrantyToCsvLine`, line 119: `(warranty instanceof BasicWarranty) ? "BASIC" : "EXTENDED"`. `csvLineToWarranty`, lines 200–209: the tag decides the class (`new` at 201 and 203). |
| **Explanation** | The ternary assumes only two warranty types; any other subclass would be saved as `EXTENDED`. |
| **Consequence** | A third warranty type would be written with the wrong tag without any error and the data would be corrupted on reload. |
| **Agreed solution** | One CSV mapper per warranty type, same design as V-21, and an error when the type is not registered. For the existing types the file content stays the same. Pattern: Polymorphism. |
| **Origin** | developer2-audit V-09 + developer3-audit V-02 (`WarrantyRepository` part) |
| **Responsible** | Developer 2 (lead) |
| **Other code** | Developer 3 (lines 119, 200, 202 and his validation paths) |

---

### Pure Fabrication

### V-25 — Console input reading duplicated

| Field | Content |
|---|---|
| **Pattern** | Pure Fabrication |
| **Module** | Sales UI (Workshop 1) |
| **Evidence** | `Menu.readInt()` (line 88) and `SubMenu.readInt()` (line 607) are identical (`Integer.parseInt(scanner.nextLine().trim())`, returns `-1` on failure). `SubMenu.readDouble()` (line 620, returns `0.0` on failure). Used once in `Menu` (line 57), 19 times (`readInt`) and 9 times (`readDouble`) in `SubMenu`. |
| **Explanation** | Console input reading belongs to no domain class and was copied instead of placed in a helper. |
| **Consequence** | A change in how input is read must be repeated in each copy, and the copies can drift apart. |
| **Agreed solution** | A `ConsoleInput` class in `ui` that owns the shared `Scanner` and provides `readInt` and `readDouble`, keeping the current return values (`-1` and `0.0`; see FD-03). Pattern: Pure Fabrication. Applied first among the UI corrections. |
| **Origin** | leader-audit V-05 |
| **Responsible** | Technical Leader |
| **Other code** | No |

### V-26 — File reading and writing duplicated in the repositories

| Field | Content |
|---|---|
| **Pattern** | Pure Fabrication (also lowers Protected Variations; recorded once) |
| **Module** | Persistence (all repositories) |
| **Evidence** | The same writing code is repeated: `PersonRepository.writeLines` (151–161), `AccessoryRepository.saveAll` (143–154), `PromotionRepository.saveAll` (118–129), `ReturnRepository.saveAll` (117–128), `WarrantyRepository.saveAll` (151–162), and in `ProductRepository` (117, 192) and `SaleRepository` (137, 212). Each opens a `BufferedWriter`/`BufferedReader`, writes or reads every line and wraps `IOException` in `PersistenceException`; the reading loops are repeated in `loadAll` / `loadClients` / `loadSellers` (for example `AccessoryRepository` 229–252, `PromotionRepository` 195–218). `CsvSupport.parse*` exists but `ProductRepository` 147–164 and `SaleRepository` 298, 337–339 parse by hand. The target folder is created in three different ways (helper in four repositories, own code in `SaleRepository` 131–132, none in Product and Person). |
| **Explanation** | `CsvSupport` was created to avoid repetition but the extraction was left incomplete; file mechanics sit next to the CSV conversion in every repository. |
| **Consequence** | A change in encoding, atomic writing or error messages must be repeated in each copy; each repository has two reasons to change. |
| **Agreed solution** | Developer 3 extends `CsvSupport` with methods that write a list of lines and read lines with their numbers, and with the shared parsing. Developer 3 migrates the save loops and the loops he refactored in `ProductRepository` and `SaleRepository`; Developer 2 migrates the reading and saving code of her repositories. Each repository keeps its own error messages and the same files. Folder creation must keep today's behavior (Product and Person do not create it; see FD-12). Applied before V-21 to V-24. Pattern: Pure Fabrication. |
| **Origin** | developer2-audit V-10 + developer3-audit V-03 |
| **Responsible** | Developer 3 (lead) |
| **Other code** | Developer 2 (her repositories) |

---

### Indirection

### V-27 — Services bound to concrete repositories

| Field | Content |
|---|---|
| **Pattern** | Indirection (also affects Low Coupling and Protected Variations; recorded once) |
| **Module** | Products, People, Sales, Accessories, Promotions, Returns, Warranties (services) |
| **Evidence** | Every service holds a concrete repository class and loads its data from the files in its constructor: `ProductService` (field line 22, constructor line 37), `SaleService` (import line 15, field line 33, constructor 40–46, loading 53–59, saving line 135), `PersonService` (25, 40–48), `AccessoryService` (29, 37–41), `PromotionService` (31, 39–43), `WarrantyService` (34, 56–60), `ReturnService` (28, 37–43). No interface exists between a service and its repository, and `src/test` contains only the seven `*RepositoryTest` classes and no service test. |
| **Explanation** | The services depend on the CSV implementation of their repository, so the business rules are bound to one storage technology. |
| **Consequence** | Changing the storage, or testing a service with an in-memory fake, requires editing the service; the rules cannot be run without reading `data/*.csv`. |
| **Agreed solution** | One small interface per module in `persistence` (for example `ProductStorage` with `load` and `saveAll`, `SaleStorage` with `loadAll` and `saveAll`), implemented by the existing repository; each service depends only on its interface. `Main` still compiles unchanged because it passes the same repository objects (confirmed by `mvn clean compile`). `SaleStorage` does not copy the `promotions` parameter (applied after V-10); the interface of the warranty module follows the signature left by V-11. The unused constructors are removed in V-12. Pattern: Indirection (supports Low Coupling and Protected Variations). |
| **Origin** | leader-audit V-09 + developer1-audit V-02 (interface part) + developer2-audit V-11 |
| **Responsible** | Leader (`SaleService`, `SaleStorage`, `SaleRepository` declaration), Developer 1 (`ProductService`, `ProductStorage`, `ProductRepository` declaration), Developer 2 (the five services of her modules, their interfaces and repository declarations) |
| **Other code** | Developer 3 confirms that `SaleRepository` (229 of 354 lines) and the repository tests still compile and pass |

---

### Protected Variations

### V-28 — Internal product list exposed

| Field | Content |
|---|---|
| **Pattern** | Protected Variations |
| **Module** | Products (Workshop 1) |
| **Evidence** | `ProductService.listAll`, lines 130–132: `return products;` (line 131), the internal mutable `List<Product>`. Callers only read it: `SaleService` (56, 68) and `SubMenu` (116). |
| **Explanation** | The method hands out the internal collection, so the internal representation is visible and modifiable from outside. |
| **Consequence** | Any caller can add or remove products bypassing `validateBasicData` and `repository.saveAll`, leaving memory and `data/products.csv` out of sync. |
| **Agreed solution** | Return `Collections.unmodifiableList(products)` (a read-only live view; read-only callers see the same results). Pattern: Protected Variations (information hiding). Verified by compiling and by running the product listing and a sale. |
| **Origin** | developer1-audit V-03 |
| **Responsible** | Developer 1 |
| **Other code** | No |

### V-29 — Console compatibility implemented twice

| Field | Content |
|---|---|
| **Pattern** | Protected Variations |
| **Module** | Accessories (Requirement 1) |
| **Evidence** | `Controller` (lines 13, 28, 54–83) and `Memory` (lines 14, 31, 75–104) contain the same field `compatibleConsoleIds`, constructor copy and methods `getCompatibleConsoleIds`, `addCompatibleConsole`, `isCompatibleWith` (lines 81 and 102). Both `isCompatibleWith` overrides repeat the default method of `ConsoleCompatible` (line 31, commit `cdfc589`). |
| **Explanation** | The compatibility rules are a predictable variation point (for example matching by console generation) but are implemented twice. |
| **Consequence** | Any change must be made and kept consistent in both classes; a third compatible accessory would need a third copy. |
| **Agreed solution** | An abstract `CompatibleAccessory extends Accessory implements ConsoleCompatible` in `model` owning the list, its defensive copy, `getCompatibleConsoleIds` and `addCompatibleConsole`; `Controller` and `Memory` extend it and drop their `isCompatibleWith` overrides (the default method gives the same result for every input). Pattern: Protected Variations, supported by Polymorphism. The classification was reviewed and accepted: the report justifies a foreseeable change point. |
| **Origin** | developer1-audit V-04 |
| **Responsible** | Developer 1 |
| **Other code** | No (`AccessoryService` line 150 calls through the interface; confirmed by compiling) |

### V-30 — Percentage rule implemented three times

| Field | Content |
|---|---|
| **Pattern** | Protected Variations |
| **Module** | Promotions (Requirement 2) |
| **Evidence** | `PercentageDiscount` (field line 10, `setPercentage` 30–35), `CategoryDiscount` (field line 10, `setPercentage` 33–38), `BulkPurchaseDiscount` (field line 11, `setPercentage` 44–49): each declares `private double percentage`, `getPercentage()` and the same check `if (percentage < 0.0 || percentage > 100.0)` (lines 31, 34, 45). |
| **Explanation** | The percentage, its valid range and how it is applied are common to three promotion types but copied in each. |
| **Consequence** | A change in the percentage rule (maximum discount, rounding) must be repeated in three classes; each new percentage promotion adds a fourth copy. |
| **Agreed solution** | An abstract `PercentageBasedPromotion extends Promotion` in `model` holding `percentage`, `getPercentage`, the validated `setPercentage` and a protected `applyPercentage(double base)`. The three classes extend it and keep their constructors and call order, so validation order and exception messages do not change. `Promotion` stays free of percentage logic. Pattern: Protected Variations. The classification was reviewed and accepted. |
| **Origin** | developer1-audit V-05 |
| **Responsible** | Developer 1 |
| **Other code** | No (`PromotionRepository` and `PromotionService` use unchanged getters and constructors; confirmed by compiling) |

---

## 4. Boundary findings

A violation of this kind is registered once. Each member corrects the part under his or her responsibility and the Technical Leader coordinates the order of the commits.

| ID | Reported from | Parts of the correction (in commit order) |
|---|---|---|
| V-02 | Developer 1 | 1. Leader: `Sale.getTotalUnits()`. 2. Developer 1: `BulkPurchaseDiscount` uses it |
| V-03 | Developer 1 | 1. Leader: `Sale.getDiscountRatio()`. 2. Developer 1: shared refund method in `Return` |
| V-05 | Developer 2 | 1. Leader: `Sale.findProductById`. 2. Developer 2: `ReturnService` and `ReturnRepository` call it |
| V-06 | Developer 2 | 1. Developer 1: `Warranty.expiresBetween`. 2. Developer 2: `WarrantyService` calls it |
| V-07 | Developer 3 and Developer 1 (notes) | 1. Developer 1: way to restore a stored total in `Return`. 2. Developer 3: remove arithmetic from `ReturnRepository` |
| V-10 | Leader and Developer 3 | 1. Leader: overload without the parameter. 2. Developer 3 (`SaleRepositoryTest`) and Developer 2 (`WarrantyRepository`) migrate. 3. Leader: remove the old version |
| V-11 | Developer 2, Developer 3 and Leader | 1. Developer 2: `WarrantyRepository.loadAll` receives loaded data. 2. Leader: construction order in `Main`. 3. Developer 3: `WarrantyRepositoryTest` |
| V-15 | Developer 1 | 1. Developer 1: add `ReturnReceiptFormatter`. 2. Leader: change the four calls. 3. Developer 1: remove `generateReturnReceipt` |
| V-16 | Developer 1 | Same three steps with `WarrantyCertificateFormatter` and two calls |
| V-17 | Developer 2 | 1. Developer 2: `MonthlyBalanceService`. 2. Leader: `SubMenu` line 524 and `Main` |
| V-18 | Leader | 1. Developer 1: variation methods in `Product`, `Console`, `Accessory`. 2. Developer 2: `AccessoryService` stock entry if needed. 3. Leader: `SaleService` and `SubMenu` |
| V-20 | Developer 2 | 1. Developer 1: `getAccessoryKind()`. 2. Developer 2: `matchesType` |
| V-21 to V-24 | Developer 1 (V-21), Developer 2 (V-22 to V-24) and Developer 3 (all four) | 1. Owner creates the mapper classes. 2. Developer 3 moves his validation and `corruptLine` paths into them. Repository tests must keep passing |
| V-26 | Developer 2 and Developer 3 | 1. Developer 3: extend `CsvSupport`. 2. Developer 3 and Developer 2 migrate their repositories |
| V-27 | Leader, Developer 1 and Developer 2 | Each creates the interface of his or her modules; Developer 3 confirms `SaleRepository` and the tests |
| V-01 | Leader | Developer 2 adds `AccessoryService.findById` only if missing |

**Unified reports (same problem seen from several sides)**
- V-07: `developer3-audit V-01` plus the notes of `developer1-audit` (root cause in `Return`).
- V-10: `leader-audit V-07` plus `developer3-audit V-05`.
- V-11: `developer2-audit V-03` plus `developer3-audit V-04` plus `leader-audit` boundary observation 3.
- V-21 to V-24: `developer1-audit V-01`, `developer2-audit V-07/V-08/V-09` and `developer3-audit V-02`, split one finding per repository so each fragment is recorded once.
- V-26: `developer2-audit V-10` plus `developer3-audit V-03`.
- V-27: `leader-audit V-09`, `developer1-audit V-02` and `developer2-audit V-11`.
- V-12: `developer2-audit V-04` plus the constructor part of `developer1-audit V-02`.

**Not unified, on purpose**
- V-14, V-15 and V-16 share a cause (formatting in the domain) but are separate fragments in separate classes with separate corrections.
- V-18, V-19 and V-20 all remove type checks but live in different classes and need different mechanisms.

**Multi-pattern fragments and which pattern guides the correction**
- Formatting in the model (V-14 to V-16): High Cohesion registers it, Pure Fabrication guides the fix.
- Type conditionals (V-18 to V-24): Polymorphism registers and guides the fix; Protected Variations is the result.
- Concrete repositories (V-27): Indirection registers and guides the fix; Low Coupling and Protected Variations are results.
- `SubMenu` size (V-13): registered under High Cohesion, not under Controller.

---

## 5. Out-of-scope findings (functional defects, not corrected in Requirement 7)

| # | Defect | Evidence | Reported by |
|---|---|---|---|
| FD-01 | The sale is added to memory before it is saved, and the rollback only restores stock; warranties and the client's purchase are not undone. Reproduced: with the save forced to fail, stock is restored but one phantom sale stays in memory and the purchase stays on the customer. | `SaleService.processSale`, lines 130–147 | Leader FD-1, Developer 3 B-2 |
| FD-02 | `PersistenceException extends RuntimeException`, but `TEAM.md` defines it as a subclass of `GameZoneException` (changed in commit `ca6cb5d`). `SaleService` (143–146) rethrows only `GameZoneException`, so it wraps this one in a generic `RuntimeException` that bypasses the `catch (PersistenceException)` of `Menu.displayMenu`. Severity high. Correcting it changes which exception the menu catches. | `PersistenceException.java:17`, `SaleService:143–146`. Reproduced: `RuntimeException \| cause: PersistenceException` | Developer 3 FD-2, Leader FD-2, Developer 1 |
| FD-03 | `readDouble` silently returns `0.0` on invalid input, and `readInt` returns `-1`. | `SubMenu.readDouble`, `readInt` | Leader FD-3 |
| FD-04 | `Menu` does not catch every exception: `DateTimeParseException` from `LocalDate.parse` in the promotion forms and `IllegalArgumentException` from the `SaleDetail` constructor (for example when a non-numeric quantity makes `readInt` return `-1`) end the program. | `SubMenu`, `SaleDetail` | Leader FD-2 (second part) |
| FD-05 | `Main` has no `try/catch` and the services load the CSV files when they are built, so a corrupt `products.csv` ends the JVM with `Exception in thread "main"`. Reproduced. Severity high. | `Main:41–99`, `ProductService:43`, `SaleService:53` | Developer 3 B-1 |
| FD-06 | `List.of(scanner.nextLine().split(","))` does not trim spaces: "PS5, PS4" gives `[PS5,  PS4]` (verified in `jshell`). The effect on `data/accessories.csv` and on compatibility checks is still to be confirmed. | `SubMenu.showAccessorySubMenu`, lines 158, 182 | Leader FD-4 |
| FD-07 | The total of a new sale is saved as `0.0` in `data/sales.csv` because `calculateFinalTotal()` is not called before `saveAll` and `getTotal()` is stored. Verified by running the program. | `SaleService.processSale`, `SaleRepository.saleToCsvLine` | Leader FD-5 |
| FD-08 | `Sale.generateReceipt` (with the discount breakdown required by Requirement 2) is never shown to the user; `processSaleForm` prints its own summary. | `Sale.generateReceipt`, `SubMenu.processSaleForm` | Leader FD-6 |
| FD-09 | `ProductService.restoreStock` increases stock in memory before saving; if saving fails, memory and file differ. | `ProductService.restoreStock` | Leader FD-7 |
| FD-10 | `ReturnService.registerReturn` always restores one unit (`saleService.restoreStock(product, 1)`), although `SaleDetail` stores a quantity. The code comment mentions it. | `ReturnService`, lines 112–115 | Developer 2 |
| FD-11 | `ReturnService` reports errors with `IllegalArgumentException` and English messages (lines 47, 52, 64, 79 and others) while the system uses the typed exceptions; `WarrantyService.findById` and `findWarrantyByProduct` (94–119) return `null` instead of throwing `ResourceNotFoundException`. | `ReturnService`, `WarrantyService` | Developer 2 |
| FD-12 | The target folder is no longer created by `ProductRepository` and `PersonRepository`; a first run on a clean machine fails with `PersistenceException`. Reproduced. | `ProductRepository:117`, `PersonRepository:152` | Developer 3 FD-1 |
| FD-13 | `Return` computes refunds with `Product.getPrice()` instead of the price paid (`SaleDetail.getUnitPrice()`), and `Return.refundAmount` is a stored value that is not updated if the sale changes. | `Return` | Developer 1 |
| FD-14 | `Sale.calculateTotal()` returns the subtotal (legacy alias) and the promotions use it. | `Sale.calculateTotal` | Developer 1 |
| FD-15 | The setters of `Promotion` accept null and inverted dates, unlike its constructor and `PromotionValidator`; `Warranty.isActive(null)` throws `NullPointerException` while `Promotion.isActive(null)` uses today's date. | `Promotion`, `Warranty` | Developer 1 |
| FD-16 | `ProductService.updateStock` and `restoreStock` accept a quantity of 0, which `Product` then rejects with a different exception type and message. | `ProductService`, `Product` | Developer 1 |
| FD-17 | `Warranty.generateWarrantyCertificate` prints the additional cost without number formatting, unlike the other receipts; `VideoGame.describe` omits price and stock while `Console.describe` and the accessories include them. | `Warranty`, `VideoGame` | Developer 1 |
| FD-18 | Product, accessory, cable, memory, percentage and bulk-quantity rules are duplicated between the model and the validators with different messages or exception types (for example `ProductValidator.validateProductData` against `AccessoryValidator.validateAccessoryData`, `Cable.setLength` against `AccessoryValidator.validateCableData`). Unifying them would change what the user sees, so they are not registered as violations. | Model and validator classes | Developer 1 |

**Observations that were not registered as violations**
- The month range check in `SubMenu.showMonthlyBalance` and the days check in `SubMenu.listExpiringSoonFlow` stay in the UI. Moving them to a validator would change the message the user sees (Leader).
- `ProductService.updateStock` (Developer 1) and `ProductService.restoreStock` (Leader) repeat the same quantity validation before delegating to `Product`; any shared fix is coordinated by the Leader (Developer 1).
- `GameZoneException.getErrorCode()` and `InvalidDataException.getFieldName()` are never used; `PersonValidator.validatePersonData` ignores its phone parameter; `ProductService.registerVideoGame` and `registerConsole` check platform and brand inline (Developer 1). These are quality observations, not GRASP violations.
- `PersonRepository` handles two aggregates (clients and sellers, lines 105/129 and 172/219); not registered (Developer 3).
- The comment `// Bloque try-catch en cascada ...` in `Menu.displayMenu` is in Spanish; it is translated whenever the class is modified (Leader).
- Test gaps reported by Developer 3, not GRASP findings: no test for a missing folder in Product and Person (T1), no reference to `GameZoneException` in tests (T2), no test through persistence, service and menu (T3), only 20 message assertions against about 54 corrupt-file cases (T4), duplicated test helpers (T5).

---

## 6. Suggested correction order (for the Leader to coordinate)

1. **Baseline:** run the non-regression strategy on `develop` before the first correction.
2. **Independent corrections, no cross-owner dependency:** V-12, V-28, V-29, V-30, V-04, V-19, V-25.
3. **Additive methods needed by others:** `Sale.getTotalUnits`, `Sale.getDiscountRatio`, `Sale.findProductById` (Leader); `Warranty.expiresBetween`, `Accessory.getAccessoryKind`, the stored-total way in `Return`, the variation methods of V-18 (Developer 1); the extension of `CsvSupport` (Developer 3). Then V-02, V-03, V-05, V-06, V-07, V-20.
4. **Signatures and wiring:** V-10, then V-26, then V-21 to V-24, then V-27, then V-11 (the most invasive, last).
5. **UI structure:** V-13, then V-09; V-15, V-16 and V-17 call changes; V-14, V-08, V-01 and V-18.

Rule for all steps: every commit compiles with `mvn clean compile`, and any correction that cannot keep the observable behavior is moved to section 5 with its evidence.
