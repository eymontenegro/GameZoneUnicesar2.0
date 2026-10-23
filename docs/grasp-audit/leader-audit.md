# GRASP Audit — Technical Leader (Estefany Montenegro)

- **Auditor:** Estefany Montenegro (`eymontenegro`), Technical Leader
- **Branch:** `feature/grasp-audit`
- **Audited commit of `develop`:** `22e6df84fc809dd25f7869e22ab2c58f40e15d78`
- **Date:** 2026-10-22
- **Ownership criteria:** `TEAM.md` and the commit history (`git blame`) prevail over the assignment table of the requirement. Only code written by the Technical Leader is audited in this report. `git blame` shows the Technical Leader's account on lines of classes that belong to Developers 1 and 2 (for example `AccessoryService`, `ReturnService`, `PersonService`, `PromotionService`, `Client`, `Person` and `Console`), because team members committed from her account. For those lines, `TEAM.md` and the author of the module decide, not the account shown by `git blame`.

---

## 1. Scope

### 1.1 Audited classes and methods, by module and requirement

**Sales, UI and startup (Workshop 1)**

| Class | Audited members |
|---|---|
| `Sale` | Whole class, except `canBeReturned()` and `canBeReturned(LocalDate)` (see 1.3) |
| `SaleDetail` | Whole class |
| `SaleRepository` | `saveAll`, `loadAll` signature, lookup maps and line-by-line reading (see 1.2 for excluded code) |
| `SaleService` | Constructor, `findProductOrAccessoryById`, `processSale`, `restoreStock`, `listAllSales`, `findById` (see 1.2 for excluded lines) |
| `Menu` | Constructor, `displayMenu`, `readInt` |
| `SubMenu` | Constructor, `showProductSubMenu`, `showPersonSubMenu`, `processSaleForm`, `listSalesHistory`, `readInt`, `readDouble` |
| `Main` | `main` |

**Naming:** `TEAM.md` lists `Menu` and `SubMenu` as the Technical Leader's console classes, while the requirement text calls the console interface `ConsoleMenu`. Both classes are audited here under that single responsibility.

**Accessories (Requirement 1) — integration**
- `SaleService.findProductOrAccessoryById`, accessory branches in `processSale` and `restoreStock`.
- `SubMenu.showAccessorySubMenu`.

**Promotions (Requirement 2) — integration**
- `SaleService.processSale`, step 3 (apply the best promotion).
- `Sale`: `appliedPromotionName`, `discountAmount`, `calculateFinalTotal`, `generateReceipt`.
- `SubMenu.showPromotionSubMenu`.

**Returns (Requirement 3) — integration**
- `ProductService.restoreStock` and `SaleService.restoreStock`.
- `SubMenu.showReturnSubMenu`, `registerReturnFlow`, `listAllReturns`, `listReturnsByCustomer`, `listReturnsBySale`, `showMonthlyBalance`.

**Warranties (Requirement 4) — integration**
- `SaleService.processSale`, step 5 (basic and extended warranties).
- `SubMenu.showWarrantySubMenu`, `findWarrantyByProductFlow`, `listExpiringSoonFlow`, `listWarranties`, and the extended-warranty prompt in `processSaleForm`.

**Exceptions and validations (Requirement 6) — user interface**
- `Menu.displayMenu`: single `try` block with ordered `catch` blocks.
- `SaleService.processSale`: rollback `catch (Exception ex)` block.

**Requirement 5** only integrated Requirements 1 to 4 and produced no code.

### 1.2 Code inside the audited classes that is NOT audited here (written by others)

| File | Lines / code | Author (per `git blame`) |
|---|---|---|
| `SaleService` | Imports of `GameZoneException`, `InvalidDataException`, `ResourceNotFoundException`, `ProductValidator`, `SaleValidator` (lines 3–5, 16–17) | Andrea Laurens |
| `SaleService` | Typed exceptions in `findProductOrAccessoryById` (65, 75) | Andrea Laurens |
| `SaleService` | `processSale`: client/seller lookup (81–82), `SaleValidator` calls (84–85), `ProductValidator` call (89), `throw (GameZoneException) ex` (143–145) | Andrea Laurens |
| `SaleService` | `findById`: typed exception, `stream` and `SaleValidator.validateSaleExists` (169, 172, 174, 177, 179, 180) | Andrea Laurens |
| `ProductService.restoreStock` | `@throws` lines 213–214 and typed exception (218) / signature and body lines 216–217, 220–222 and 224 (reorganize commit `40350836`). The method itself was added by the Technical Leader (commit `6e590e8e`, `TEAM.md` Requirement 3) and is audited here as a whole | Andrea Laurens (213–214, 218) / Yanelis González (216–217, 220–222, 224) |
| `SaleRepository` | Building `Sale` and `SaleDetail` when loading (`new Sale`, `addDetail`, `new SaleDetail`), wrapping of `IOException` in `PersistenceException`, corrupt-line reporting, `CsvSupport` usage, `parseAmount`, `parseDetail` | Xavi Acosta |
| `Sale` | Five lines attributed to `yanelis1612`, among them 196, 199, 201 and 236, around the Javadoc and closing brace of `generateReceipt` (the method logic is the Technical Leader's) | Yanelis González |

### 1.3 Ownership discrepancy: `Sale.canBeReturned`

`TEAM.md` assigns `canBeReturned` (Requirement 3) to Developer 1, and the commit history confirms it: the method was added by Developer 1 in commit `bb8770e` (`yanelis1612`, 2026-09-18, "add canBeReturned validation rule to Sale class"). `git blame` shows lines 179 and 192–193 under the Technical Leader's account only because of the later reformatting commit `f61fefef` ("reorganize Sale class", 2026-10-16). Following the ownership rule, Developer 1 audits and corrects it, and it is excluded from this report so that the method is audited exactly once (Developer 1's report audits both overloads). Preliminary reading: the method uses only the sale's own date, so no violation was seen.

---

## 2. Verdict summary

| Pattern | Verdict | Findings |
|---|---|---|
| Information Expert | Violation | V-02 |
| Creator | Violation | V-03 |
| Controller | Correct | — |
| Low Coupling | Violation | V-06, V-07 |
| High Cohesion | Violation | V-04, V-08 |
| Polymorphism | Violation | V-01 |
| Pure Fabrication | Violation | V-05 |
| Indirection | Violation | V-09 |
| Protected Variations | Violation | V-01, V-09 (each recorded once under its primary pattern) |

---

## 3. Pattern sections

### 3.1 Information Expert — Violation

**Where it is correct**
- `Sale.calculateSubtotal()` and `Sale.calculateFinalTotal()` compute from the sale's own details, discount and additional costs.
- `SaleDetail.getSubtotal()` multiplies its own `quantity` and `unitPrice`.
- `ProductService.restoreStock` delegates the stock change to the owner of the data, `Product.increaseStock`.
- `SaleService.processSale` asks `Promotion.calculateDiscount(sale)` and `Warranty.getAdditionalCost()` instead of computing them.

**Violation:** V-02.

**Observation not registered:** `SaleService.processSale` calls `sale.setAppliedPromotionName(...)` and `sale.setDiscountAmount(...)` with data taken from the promotion. It was considered and discarded, because `Sale` does not own the `Promotion` object and the discount is calculated by the promotion.

### 3.2 Creator — Violation

**Where it is correct**
- `SaleService.processSale`: `new Sale(saleId, client, seller, LocalDate.now())`. The service holds the list of sales and has the data to initialize one.
- `Main.main` creates repositories, services and the menu with the data each one needs (composition root).

**Violation:** V-03.

### 3.3 Controller — Correct

- `Menu.displayMenu` receives the system event (the selected option) and delegates it to `SubMenu` (`showProductSubMenu`, `processSaleForm`, etc.) without business logic.
- `SubMenu` forms only collect data and delegate to the services (`saleService.processSale(...)`, `returnService.registerReturn(...)`).
- The size of `SubMenu` (a bloated controller) is registered under High Cohesion (V-04) to avoid duplicating the finding.
- Requirement 6: `Menu.displayMenu` wraps the dispatch `switch` in a single `try` block with ordered `catch` blocks: `ResourceNotFoundException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException` and, last, `GameZoneException`. The handling is centralized in the controller instead of repeated in each form. Exceptions outside these types are not caught here (see FD-2).
- `Main` does not receive system events.

### 3.4 Low Coupling — Violation

**Where it is correct**
- `Sale` depends only on `Client`, `Seller` and `SaleDetail`.
- `SaleDetail` depends only on `Product` (the abstraction).

**Violations:** V-06, V-07.

### 3.5 High Cohesion — Violation

**Where it is correct**
- `Menu` only displays the menu, reads the option and dispatches.
- `SaleDetail` only represents a sale line.
- `Main` only configures paths, wires dependencies and starts the menu.
- `SaleService` orchestrates the sale use case; coordinating its steps was judged acceptable for a service and is not registered.

**Violations:** V-04, V-08.

### 3.6 Polymorphism — Violation

**Where it is correct**
- `SaleService.processSale` calls `bestPromotion.calculateDiscount(sale)` and `Warranty.getAdditionalCost()` polymorphically.
- The `switch` in `Menu.displayMenu` is on a menu option, not on the type of an object.

**Violation:** V-01.

### 3.7 Pure Fabrication — Violation

**Where it is correct**
- `SaleService` and `SaleRepository` keep business coordination and file access out of `Sale`.

**Violation:** V-05.

### 3.8 Indirection — Violation

**Where it is correct**
- `SubMenu` sits between `Menu` and the services.
- `SaleService` sits between the UI and the domain/persistence.
- `SaleRepository` sits between `SaleService` and the CSV file.

**Violation:** V-09. It was first judged a design shared by all repositories and left unregistered. The other services are registered by their owners (Developer 1, V-02; Developer 2, V-11), so `SaleService`, which is the Technical Leader's code, is registered here and the consolidation unifies the three.

### 3.9 Protected Variations — Violation

The same code fragment that breaks Polymorphism exposes the system to a foreseeable change (new product types). It is registered **once**, as V-01, and not duplicated here.

**Where it is correct**
- `Sale` stores the promotion name and the discount amount, not the `Promotion` hierarchy, so a new promotion type does not affect it.
- `SaleDetail` keeps `unitPrice` as the price at the time of sale, so later price changes do not alter past sales.

**Observation not registered:** `Main.main` keeps the eight `data/*.csv` paths as local variables. Considered a low-impact change; not registered.

**Also unprotected:** a change of storage technology, because `SaleService` depends on the concrete `SaleRepository`. Registered once, as V-09 (Indirection).

---

## 4. Violations

### V-01 — `instanceof` checks on product type

| Field | Content |
|---|---|
| **ID** | V-01 |
| **Pattern** | Polymorphism (also affects Protected Variations; registered once) |
| **Module** | Sales (Workshop 1), integrated in Requirement 1 (accessories) and Requirement 4 (warranties) |
| **Evidence** | `SaleService.processSale`, lines 108–112: `if (product instanceof Accessory) { accessoryService.updateStock(...); } else { productService.updateStock(...); }`. Same method, line 120: `if (product instanceof Console) { warrantyService.assignBasicWarranty(...); }`. `SaleService.restoreStock`, lines 154–158: `if (product instanceof Accessory) { accessoryService.restoreStock(...); } else { productService.restoreStock(...); }`. `SubMenu.processSaleForm` (starts at line 531), line 551: `if (p instanceof Console) { ... "¿Desea agregar garantía extendida ..." }`. |
| **Explanation** | The behavior that varies by product type (which service handles its stock, whether it gets a basic warranty, whether an extended warranty is offered) is decided with conditionals on the type instead of being defined by the type hierarchy. |
| **Consequence** | Adding a product type (for example a new accessory family or a bundle) forces edits in four places in `SaleService` and `SubMenu`, with the risk of forgetting one. |
| **Proposed solution** | Move the variation into the `Product` hierarchy (for example methods that say whether a basic warranty applies and whether an extended warranty can be offered) and give stock handling a single entry point that routes by the item's own type. Pattern applied: Polymorphism, with Protected Variations as a result. The user-visible messages and their order must not change. |
| **Code of another member** | Yes. Developer 1 (`Product`, `Console`, `Accessory`) and Developer 2 (`AccessoryService`). Boundary finding. |

### V-02 — Catalog lookup done by `SaleService`

| Field | Content |
|---|---|
| **ID** | V-02 |
| **Pattern** | Information Expert |
| **Module** | Sales (Workshop 1), integrated in Requirement 1 (accessories) |
| **Evidence** | `SaleService.findProductOrAccessoryById`, lines 68–76: `productService.listAll().stream().filter(p -> p != null && productId.equals(p.getId()))...orElseGet(() -> accessoryService.listAllAccessories().stream()...)`. |
| **Explanation** | The service that owns the product catalog and the service that owns the accessory catalog hold the information needed to find an item by id. `SaleService` pulls both full lists and searches them itself. |
| **Consequence** | Any change in how catalogs store or index items (for example a map by id) forces a change in `SaleService`. The search logic is duplicated outside its owners. |
| **Proposed solution** | Ask each service to find by id (`ProductService.findById` already exists) and let `SaleService` only combine the two answers. The `ResourceNotFoundException` for "producto o accesorio" and its message must remain identical. Pattern applied: Information Expert. |
| **Code of another member** | Possibly Developer 2, only if `AccessoryService` has no lookup-by-id method yet. |

### V-03 — `SaleDetail` created by the user interface

| Field | Content |
|---|---|
| **ID** | V-03 |
| **Pattern** | Creator |
| **Module** | Sales UI (Workshop 1) |
| **Evidence** | `SubMenu.processSaleForm`, line 548 (the quantity is read at line 546): `items.add(new SaleDetail(p, qty, p.getPrice()));`. The only other `new SaleDetail` in `src/main` is `SaleRepository`, line 348, which rebuilds stored lines when loading. `Sale` aggregates the details (`details` list, `addDetail`). |
| **Explanation** | `Sale` aggregates and contains `SaleDetail` objects, so it is the natural creator. The UI instead knows the constructor and decides the unit price. |
| **Consequence** | A change in how a sale line is built (for example a different price rule) forces an edit in the interface. |
| **Proposed solution** | Let the sale create its lines (for example an `addItem(Product, int)`-style method in `Sale`). `Sale.addDetail(SaleDetail)` must stay, because the repository creates lines with the stored price when loading. The correction must keep the moment of the error: today `SaleDetail`'s constructor fails right after the quantity is typed. If this cannot be preserved, the case is justified in the change log or moved to out-of-scope. Pattern applied: Creator. |
| **Code of another member** | No. `SubMenu`, `Sale` and `SaleService` are all the Technical Leader's. |

### V-04 — `SubMenu` handles every module

| Field | Content |
|---|---|
| **ID** | V-04 |
| **Pattern** | High Cohesion |
| **Module** | Sales UI (Workshop 1), extended in Requirements 1 to 4 |
| **Evidence** | `SubMenu.java` (627 lines, one class). Line ranges by responsibility: products 77–134 (`showProductSubMenu`), accessories 135–221 (`showAccessorySubMenu`), persons 222–263 (`showPersonSubMenu`), promotions 264–330 (`showPromotionSubMenu`), returns 331–358 and 432–530 (`showReturnSubMenu`, `registerReturnFlow`, `listAllReturns`, `listReturnsByCustomer`, `listReturnsBySale`, `showMonthlyBalance`), warranties 359–431 (`showWarrantySubMenu`, `findWarrantyByProductFlow`, `listExpiringSoonFlow`, `listWarranties`), sales 531–606 (`processSaleForm`, `listSalesHistory`) and input helpers 607–627 (`readInt`, `readDouble`). The class contains the forms of products, accessories, persons, promotions, returns, warranties and sales (`showProductSubMenu`, `showAccessorySubMenu`, `showPersonSubMenu`, `showPromotionSubMenu`, `showReturnSubMenu`, `showWarrantySubMenu`, `processSaleForm`, `listSalesHistory`) and repeats the same input block (ID, title, price, stock) in several `case` branches (the price and stock prompts at lines 93–94, 105–106, 154–155, 166–167 and 177–178). |
| **Explanation** | One class carries seven different responsibilities, so it has many reasons to change and is hard to understand. |
| **Consequence** | Every new module or service of the store enlarges the same class and creates merge conflicts. |
| **Proposed solution** | Split it into one sub-menu per module, keeping `Menu` as the entry point. Pattern applied: High Cohesion. Texts and flow must stay exactly the same. |
| **Code of another member** | No. |

### V-05 — Duplicated console input reading

| Field | Content |
|---|---|
| **ID** | V-05 |
| **Pattern** | Pure Fabrication |
| **Module** | Sales UI (Workshop 1) |
| **Evidence** | `Menu.readInt()` (line 88) and `SubMenu.readInt()` (line 607) are identical (`Integer.parseInt(scanner.nextLine().trim())`, returns `-1` on failure). `SubMenu.readDouble()` (line 620, returns `0.0` on failure). They are called once in `Menu` (line 57), 19 times (`readInt`) and 9 times (`readDouble`) in `SubMenu`, for example at lines 87, 93–94 and 546. |
| **Explanation** | Console input reading is a responsibility that belongs to no domain class and was copied instead of being placed in a dedicated helper. |
| **Consequence** | A change in how input is read must be repeated in each copy, and the copies can drift apart. |
| **Proposed solution** | A class in the `ui` package (for example `ConsoleInput`) that owns the shared `Scanner` and provides `readInt` and `readDouble`, keeping the current return values (`-1` and `0.0`). Pattern applied: Pure Fabrication. |
| **Code of another member** | No. |

### V-06 — Menus receive services they do not use

| Field | Content |
|---|---|
| **ID** | V-06 |
| **Pattern** | Low Coupling |
| **Module** | Sales UI and startup (Workshop 1) |
| **Evidence** | `Menu` constructor (`Menu.java`, line 28) receives `ProductService`, `AccessoryService`, `PersonService`, `PromotionService`, `SaleService`, `ReturnService` and `WarrantyService` and only forwards them: `new SubMenu(productService, ..., warrantyService, this.scanner)` (`Menu.java`, line 36). The `SubMenu` constructor (`SubMenu.java`, line 56) receives the same seven. `Main`, line 88, builds `new Menu(...)` with all of them. |
| **Explanation** | `Menu` depends on seven concrete classes that it never uses. |
| **Consequence** | Adding a module forces changes in `Main`, `Menu` and `SubMenu` at the same time. |
| **Proposed solution** | `Main` creates the sub-menus and passes them to `Menu`, and each sub-menu receives only the services it uses. Pattern applied: Low Coupling. Ordering note: it depends on the split proposed in V-04, so V-04 should be corrected first. The two findings are related but not duplicated, because V-04 is about responsibilities and V-06 about dependencies. |
| **Code of another member** | No. |

### V-07 — Unused parameter in `SaleRepository.loadAll`

| Field | Content |
|---|---|
| **ID** | V-07 |
| **Pattern** | Low Coupling |
| **Module** | Sales persistence (Workshop 1), extended in Requirement 2 (promotions) |
| **Evidence** | `SaleRepository.loadAll(..., List<? extends Promotion> promotions)` (line 169, Javadoc line 160): the parameter is never used in the method body. Callers: `SaleService` constructor (lines 53–59: `promotionService.listAllPromotions()` is requested only to be passed), `WarrantyRepository` (lines 233 and 235), `SaleRepositoryTest` (lines 66 and 305). |
| **Explanation** | The repository and its callers depend on `Promotion` and on `PromotionService` without needing it. |
| **Consequence** | Callers must load promotions just to call `loadAll`, and any change to `Promotion` can break unrelated code. |
| **Proposed solution** | Remove the parameter. Suggested order to keep the build compiling: (1) the Technical Leader adds a `loadAll` without the parameter, (2) the owners of the other callers migrate, (3) the old version is removed. Pattern applied: Low Coupling. |
| **Code of another member** | Yes. Developer 2 (`WarrantyRepository`, line 235) and Developer 3 (`SaleRepositoryTest`). Boundary finding. |

### V-08 — Presentation logic in the domain model

| Field | Content |
|---|---|
| **ID** | V-08 |
| **Pattern** | High Cohesion |
| **Module** | Sales (Workshop 1), extended in Requirement 2 (discount breakdown) |
| **Evidence** | `Sale.generateReceipt()` (lines 202–236): builds a Spanish receipt with `DateTimeFormatter`, `StringBuilder` and `String.format`. A search of `src` finds no caller of `generateReceipt`. |
| **Explanation** | The domain class mixes sale data and rules with the formatting of a printed receipt. |
| **Consequence** | A change in the receipt format forces a change in the model. The method is also unused today. |
| **Proposed solution** | Move the formatting out of `Sale` to a presentation class (Pure Fabrication) in the `ui` package, with the text kept exactly the same. Pattern applied: High Cohesion. |
| **Code of another member** | No. |

### V-09 — `SaleService` bound to the concrete repository

| Field | Content |
|---|---|
| **ID** | V-09 |
| **Pattern** | Indirection (also affects Low Coupling and Protected Variations; recorded once here) |
| **Module** | Sales (Workshop 1) |
| **Evidence** | `SaleService` imports the concrete `SaleRepository` (line 15), declares the field with that type (line 33), receives it in the constructor (lines 40–46), loads the sales from the CSV files through it (lines 53–59) and saves them through it (line 135). No interface exists between the service and its repository, and `src/test` contains only the seven `*RepositoryTest` classes and no service test. |
| **Explanation** | The service depends on the CSV implementation of its repository, so the sale use case is bound to one storage technology. |
| **Consequence** | Changing the storage, or testing `processSale` with an in-memory fake, requires editing `SaleService`; the sale rules cannot be run without reading `data/*.csv`. |
| **Proposed solution** | Extract an interface `SaleStorage` in `persistence` with the operations `SaleService` uses (`loadAll` and `saveAll`), implemented by `SaleRepository`; `SaleService` depends only on the interface. `Main` still compiles unchanged because it passes the same repository object; to be confirmed by compiling. It is applied after V-07, so the `promotions` parameter is not copied into the interface. Pattern applied: Indirection (supports Low Coupling and Protected Variations). |
| **Code of another member** | Yes. The declaration of `SaleRepository` must implement the interface, and most of that file (229 of 354 lines according to `git blame`) was written by Developer 3, who should confirm. The same dependency is reported for the other services as V-02 (Developer 1, `ProductService`) and V-11 (Developer 2, the services of her modules). Boundary finding: one violation in the consolidation. |

---

## 5. Boundary observations reported for the consolidation

These involve code authored by other members and are **not** audited here.

1. **V-01** (Developer 1 and Developer 2) and **V-07** (Developer 2 and Developer 3) are boundary findings, described above.
2. **`Sale.canBeReturned`:** ownership discrepancy described in 1.3.
3. **`Main`:** `new WarrantyRepository(saleRepository, productRepository, personRepository, accessoryRepository, promotionRepository, warrantiesPath)` shows a repository that depends on five other repositories. The code belongs to Developer 2 and Developer 3, who should confirm it in their reports.
4. **`Main`:** `new ReturnService(returnRepository, saleService)` makes `ReturnService` (Developer 2) depend on `SaleService`. If Developer 2 reports it, it must be unified as one boundary violation.
5. **Coverage:** Developer 3 (Xavi Acosta, who joined at Requirement 6 according to `TEAM.md`) wrote `CsvSupport`, the exception refactor of the seven repositories and the repository tests. His report (`developer3-audit.md`) is consolidated with the other two. The file structure of the requirement has no file for him, so its location and acceptance are to be confirmed with the instructor.
6. **V-09** (Developer 1, V-02; Developer 2, V-11): the same dependency on concrete repositories, reported for each service by its owner. The consolidation registers one violation, with one interface per module.

---

## 6. Out-of-scope findings (functional defects, not corrected in Requirement 7)

| # | Defect | Evidence |
|---|---|---|
| FD-1 | The sale is added to the in-memory list before it is saved, and the rollback only restores stock. Warranties and the client's purchase registration are not undone. | `SaleService.processSale`, lines 130–147 |
| FD-2 | Some exceptions are not caught by `Menu`, which catches the `GameZoneException` subtypes and `PersistenceException` only, so the program can terminate: the `RuntimeException` that `processSale` (line 146) builds around a `PersistenceException`, `DateTimeParseException` from `LocalDate.parse` in the promotion forms, and `IllegalArgumentException` from the `SaleDetail` constructor (for example when a non-numeric quantity makes `readInt` return `-1`). | `SaleService`, `SubMenu`, `SaleDetail` |
| FD-3 | `readDouble` silently returns `0.0` on invalid input. | `SubMenu.readDouble` |
| FD-4 | `List.of(scanner.nextLine().split(","))` does not trim spaces, so "PS5, PS4" yields " PS4". **Verified in `jshell`:** `List.of("PS5, PS4".split(","))` returns `[PS5,  PS4]`. The effect inside the program (the value saved in `data/accessories.csv` and the compatibility checks) is still to be confirmed. | `SubMenu.showAccessorySubMenu`, lines 158 (controller form) and 182 (memory form) |
| FD-5 | The total of a new sale is saved as `0.0` in `data/sales.csv`, because `calculateFinalTotal()` is not called before `saveAll` and `getTotal()` is stored. **Verified by running the program.** | `SaleService.processSale`, `SaleRepository.saleToCsvLine` |
| FD-6 | `generateReceipt` (with the discount breakdown required by Requirement 2) is never shown to the user; `processSaleForm` prints its own summary. | `Sale.generateReceipt`, `SubMenu.processSaleForm` |
| FD-7 | `ProductService.restoreStock` increases stock in memory before saving; if saving fails, memory and file differ. | `ProductService.restoreStock` |

**Observations that were not registered as violations**
- The month range check in `SubMenu.showMonthlyBalance` and the days check in `SubMenu.listExpiringSoonFlow` stay in the UI. Moving them to a validator would change the message the user sees.
- The comment `// Bloque try-catch en cascada ...` in `Menu.displayMenu` is in Spanish. It is not a GRASP issue; it should be translated whenever the class is modified.
