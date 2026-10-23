# GRASP Audit — Developer 1 (Yanelis González)

- **Auditor:** Yanelis González (`yanelis1612`), Developer 1
- **Branch:** `feature/grasp-audit`
- **Audited commit of `develop`:** `22e6df84fc809dd25f7869e22ab2c58f40e15d78`
- **Date:** 2026-10-22
- **Ownership criteria:** `TEAM.md` and the commit history (`git blame`) prevail over the assignment table of the requirement. Only code written by Developer 1 is audited in this report. Some commits made from the Technical Leader's account were written by other members, so for those lines `TEAM.md` and the author of the module decide, not the account shown by `git blame`.
- **Patterns evaluated:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations

---

## 1. Scope

### 1.1 Audited classes and methods, by module and requirement

| Module (requirement) | Classes / methods audited |
|---|---|
| Products (Workshop 1) | `Product` (all methods), `VideoGame`, `Console`, `ProductRepository` (constructors, `productToCsvLine`, `saveAll`, `csvLineToProduct`, `load`), `ProductService` (both constructors, `validateBasicData`, `existsId`, `registerVideoGame`, `registerConsole`, `listAll`, `listVideoGames`, `listConsoles`, `findById`, `updateStock`) |
| Accessories (Requirement 1) | `Accessory`, `Controller`, `Cable`, `Memory` (all constructors, getters, setters, `getCompatibleConsoleIds`, `addCompatibleConsole`, `isCompatibleWith`, `describe`), and the interface `ConsoleCompatible` (including its default `isCompatibleWith` method) |
| Promotions (Requirement 2) | `Promotion` (constructor, getters, setters, `isActive`, abstract `calculateDiscount`), `PercentageDiscount`, `CategoryDiscount` (including `matchesCategory`), `BulkPurchaseDiscount` (all constructors, getters, setters and `calculateDiscount`) |
| Returns (Requirement 3) | `Return` (both constructors, getters, setters, `calculateRefundAmount`, `generateReturnReceipt`) and the two overloads `Sale.canBeReturned(LocalDate)` and `Sale.canBeReturned()` |
| Warranties (Requirement 4) | `Warranty` (constructor, abstract methods, `isActive`, `generateWarrantyCertificate`, getters), `BasicWarranty`, `ExtendedWarranty` |
| Exceptions and validations (Requirement 6) | Package `exceptions`: `GameZoneException`, `BusinessRuleException`, `InvalidDataException`, `ResourceNotFoundException`, `PersistenceException`. Package `validation`: `ProductValidator` and `PersonValidator` |

### 1.2 Code NOT audited here

- `ProductService.restoreStock` and the rest of `Sale`: written by the Technical Leader and audited in `leader-audit.md`.
- `AccessoryRepository`, `AccessoryService`, `PromotionRepository`, `PromotionService`, `ReturnRepository`, `ReturnService`, `WarrantyRepository`, `WarrantyService`, `PersonRepository` and `PersonService`: Developer 2.
- `AccessoryValidator`, `PromotionValidator`, `WarrantyValidator` and `SaleValidator`: Developer 2. The requirement's table assigns the whole `validation` package to Developer 1, but the commit history prevails: these four validators were added by Andrea Laurens (commits `8d6ca41`, `8ccbfe8`, `36a4da1` and `cc3903d`) and `git blame` attributes all their lines to her.
- `CsvSupport`, the Requirement 6 refactoring of all repositories, and the repository tests: Developer 3 (Xavi Acosta), who joined the team at Requirement 6 and is not in the assignment table of the requirement.

Lines inside audited classes that were written by other members in Requirement 6:

| Class | Lines written by others | Author |
|---|---|---|
| `ProductService` | Use of validators and typed exceptions | Developer 2 |
| `ProductRepository` | `CsvSupport` validation and `corruptLine` errors, the `instanceof` and type-code conditionals with their casts (lines 75–76, 78–86, 166–172), and the field parsing (lines 141–165) | Developer 3 (commit `ca6cb5d3`, Requirement 6) |
| `PersistenceException` | Requirement 6 changes (21 of its 37 lines), among them the `extends RuntimeException` declaration (line 17) | Developer 3 (commit `ca6cb5d`) |

### 1.3 Authorship notes

- **`ConsoleCompatible`:** the interface was created by Developer 1 (commit `6e9d744`). The later refactoring that turned `isCompatibleWith` into a default method (commit `cdfc589`) appears under the Technical Leader's account, because the team shared that account for some commits; the interface is Developer 1's design. The whole interface, including the default method, is therefore audited here.
- **`Sale.canBeReturned`:** `TEAM.md` assigns it to Developer 1 (Requirement 3). The commit history confirms it: the method was added in commit `bb8770e` (`yanelis1612`, 2026-09-18). `git blame` shows its lines under the Technical Leader's account only because of the later reformatting commit `f61fefef` ("reorganize Sale class"); commits `dd3e866` and `5b6282d` are the Technical Leader's own implementation of `Sale`. Both overloads are audited here and the Technical Leader excludes them from her report, so the method is covered exactly once.
- **Requirement 6 changes:** methods modified by another member are audited by the member who developed them. In V-01, `git blame` attributes the conditional lines (75, 81, 166, 168 and 170) and the casts, `CsvSupport` calls and `corruptLine` errors around them to Developer 3 (commit `ca6cb5d3`, Requirement 6). The type codes (lines 77 and 83) and the `Unknown product type` throw (lines 87–88) are Developer 1's, so V-01 is a boundary finding.
- **Note:** the accessory class `Controller` is a domain entity and has nothing to do with the GRASP Controller pattern.

---

## 2. Pattern verdicts

### 2.1 Information Expert — Violation (see V-07, V-08, V-11)

Correct aspects:

- `Product.reduceStock` and `Product.increaseStock` live in the class that owns the stock attribute and enforce its rules (positive quantity, sufficient stock).
- `Product.describe()` is implemented by `VideoGame`, `Console`, `Controller`, `Cable` and `Memory`, each one using only its own attributes.
- `ProductService.existsId` and `ProductService.findById` are in the class that holds the product collection.
- `Controller.isCompatibleWith` / `addCompatibleConsole` and their `Memory` counterparts are in the classes that hold `compatibleConsoleIds`.
- `Cable.setLength` and the setters of the promotion classes validate values in the class that owns each attribute.
- `Promotion.isActive` is in the class that holds the validity dates, and `PercentageDiscount.calculateDiscount` asks the sale for its subtotal (through `calculateTotal()`, documented in `Sale` as a legacy alias of `calculateSubtotal()`).
- `CategoryDiscount.calculateDiscount` computes the subtotal of one category from the sale details. This is a rule specific to that promotion, so it was reviewed and not counted as a violation.
- `Sale.canBeReturned` (both overloads) is in the class that holds the sale date, which is the information needed to decide eligibility.
- `Warranty` computes its `endDate` from its own start date and duration, `Warranty.isActive` uses its own dates, and `ExtendedWarranty.getAdditionalCost` uses the product that the warranty holds.
- `InvalidDataException` and `ResourceNotFoundException` build their own messages from the data they receive.

The violations are `BulkPurchaseDiscount` rebuilding the total number of units from the sale details (V-07), `Return` computing the sale's discount ratio from data that belongs to `Sale` (V-08) and `ProductValidator` repeating a stock rule that belongs to `Product` (V-11).

### 2.2 Creator — Correct

- `ProductService.registerVideoGame` and `ProductService.registerConsole` create `VideoGame` / `Console`. The service aggregates the products list and receives all the initializing data.
- `ProductRepository.csvLineToProduct` creates products because it is the class that holds the raw data (the CSV fields) needed to initialize them.
- The validators create the exceptions that describe the problem they detect, and the exception classes receive all the data they need.
- The accessory, promotion, `Return` and warranty classes do not instantiate domain objects (only internal `ArrayList` copies), so they offer no further application point.

### 2.3 Controller — Not applicable

None of the audited classes receives system events from the user interface. `ProductService` is a service-layer class invoked by the UI layer (`Menu` and `SubMenu`, owned by the Technical Leader); validators and exceptions do not coordinate events either.

### 2.4 Low Coupling — Violation (see V-02, V-06)

Correct aspects: dependencies follow the service → persistence → model direction; `Product`, `VideoGame`, `Console` and the accessories have no dependency on persistence or service classes; accessories reference consoles only by identifier (`compatibleConsoleIds`) instead of holding `Console` objects. The promotion classes receive a `Sale` as input to compute a discount, and `Sale` does not reference `Promotion` (it only stores the promotion name as a `String`), so the dependency is one-way. `Return` depends only on `Sale` and `Product`. `Warranty` keeps references to the `Product` it covers and the `Sale` it belongs to; this is the association described by the domain and it is used by `WarrantyRepository` and `WarrantyService` through `getSale().getId()`. The validators depend only on the model and the exceptions, and `PersonValidator.validateUniquePerson` receives the existing list as a parameter instead of querying a repository. The exceptions depend on nothing outside the JDK.

The violations are the dependency of `ProductService` on the concrete `ProductRepository` (V-02) and the dependency of `CategoryDiscount` on the concrete product subclasses (V-06).

### 2.5 High Cohesion — Violation (see V-09, V-10)

Correct aspects:

- `Product`, `VideoGame` and `Console` hold only product data and behavior.
- `Accessory` is a focused abstract base; `Controller`, `Cable` and `Memory` hold only their own attributes and, for `Controller` and `Memory`, the console compatibility they declare.
- `Promotion` and its three subclasses only hold the data and rule of each kind of discount.
- `BasicWarranty` and `ExtendedWarranty` only define the duration, type and cost of each warranty.
- Each exception class represents one category of error, and each validator groups the rules of one module.
- `ProductRepository` only handles CSV persistence.
- `ProductService` only handles product use cases (register, list, find, update stock).

The violations are `Return` and `Warranty` also holding the text layout of a user-facing document (V-09, V-10).

### 2.6 Polymorphism — Violation (see V-01, V-06)

Correct aspects: `Product.describe()` is abstract and overridden with `@Override` in every concrete product class. `Promotion.calculateDiscount` is an abstract method implemented by each discount type, and `Warranty.getDurationInMonths`, `getWarrantyType` and `getAdditionalCost` are abstract methods implemented by `BasicWarranty` and `ExtendedWarranty`, so neither hierarchy contains a conditional on its own type. Console compatibility is modeled as a capability: only `Controller` and `Memory` implement `ConsoleCompatible`, and `Cable` does not, so clients do not need type checks to know which accessories have compatibility. `GameZoneException` is an abstract base with one subclass per error category, so callers react to the type of the exception instead of inspecting codes. `ProductService.listVideoGames` and `listConsoles` were reviewed: they filter by type, they do not select a behavior variant, so they are not counted as violations. They also need the cast to return a typed list (`List<VideoGame>` and `List<Console>`), which the `getCategory()` method of V-06 would not remove.

### 2.7 Pure Fabrication — Violation (see V-09, V-10)

Correct aspects: `ProductRepository` (persistence), `ProductService` (use cases) and the static validators are fabricated classes that keep file access, business flow and validation rules out of the domain classes. The model classes contain no file access logic.

The violations are the receipt and certificate formatting, responsibilities that do not belong to the domain classes `Return` and `Warranty`, which have no fabricated class to hold them (V-09, V-10). They are recorded once each, under High Cohesion, and corrected with this pattern.

### 2.8 Indirection — Violation (see V-02)

`ProductRepository` already acts as an intermediary between the service and the CSV file, but `ProductService` is bound directly to that concrete class, so the indirection is incomplete. In the model, the `ConsoleCompatible` interface is a correct indirection between compatibility clients and the concrete accessory classes, the abstract `Promotion` is a correct indirection between sales and each concrete discount rule, and the abstract `Warranty` plays the same role for the warranty types. `PersistenceException` isolates the upper layers from the low-level `IOException`, keeping the original error as its cause.

### 2.9 Protected Variations — Violation (see V-03, V-04, V-05)

Correct aspects: all attributes are private; `Product` exposes no setters for id, title or price; `Warranty` has no setters at all; `Controller.getCompatibleConsoleIds`, `Memory.getCompatibleConsoleIds` and `Return.getReturnedProducts` return defensive copies and the constructors copy the incoming lists; the abstract `Promotion` and `Warranty` isolate the variation of discount rules and warranty types, so a new type does not require editing the existing ones; `Sale.canBeReturned()` delegates to `canBeReturned(LocalDate)`, so the current date can be supplied from outside; `ProductRepository` receives the data file path through its constructor and only uses `data/products.csv` as a default.

Unprotected variation points: the internal product list is exposed (V-03), the console compatibility rules are implemented twice (V-04) and the percentage rule is implemented three times (V-05). V-01, V-02, V-06 and V-08 also leave the product type set, the storage mechanism, the product categories and the refund rule unprotected; they are recorded once under their primary pattern.

Reviewed and not counted as violations:
- `Sale.canBeReturned` uses a 30-day return window and `ExtendedWarranty.getAdditionalCost` uses a 10% rate; each literal is written once and documented in the Javadoc.
- `PersistenceException` extends `RuntimeException`, but `TEAM.md` defines it as a subclass of `GameZoneException`. Developer 1 created it as a subclass of `GameZoneException` (commit `2662475`); the superclass was changed in commit `ca6cb5d` (Developer 3, Requirement 6). Correcting it would change which exception reaches the menu (see the out-of-scope observations), so it is not registered as a GRASP violation.

---

## 3. Violations

### V-01 — Decisions by type in `ProductRepository`

| Field | Content |
|---|---|
| **ID** | V-01 |
| **Pattern** | Polymorphism (also affects Protected Variations; recorded once here) |
| **Module** | Products (Workshop 1) |
| **Evidence** | `ProductRepository.productToCsvLine` (lines 75–89 in `develop`): `if (product instanceof VideoGame) { ... } else if (product instanceof Console) { ... } else { throw new IllegalArgumentException("Unknown product type"); }`. `ProductRepository.csvLineToProduct` (lines 166–172): `if (type.equals("VIDEOGAME")) { ... } else if (type.equals("CONSOLE")) { ... } else { throw CsvSupport.corruptLine(...) }`. |
| **Explanation** | Both methods select behavior by checking the concrete type (or a type code string) of the product through conditionals, instead of delegating to a per-type collaborator. |
| **Consequence** | Any new product type requires editing both methods; a subtype that is not listed fails at runtime. The risk grows as the owner plans to extend the catalog. |
| **Proposed solution** | Introduce a `ProductCsvMapper` interface in `com.gamezone.persistence` (`supports(Product)`, `toSpecificColumns(Product)`, `fromColumns(String[], ...)`) with `VideoGameCsvMapper` and `ConsoleCsvMapper`. `ProductRepository` keeps the common columns and iterates over its registered mappers, with no `instanceof` or type-code branches. The CSV format and both error paths stay identical: `IllegalArgumentException("Unknown product type")` when saving and the `corruptLine` `PersistenceException` ("tipo de producto desconocido") when loading. Pattern applied: Polymorphism. |
| **Code of another integrant** | Yes, Developer 3. According to `git blame`, the conditional lines and the `CsvSupport` and `corruptLine` code around them were last written by Developer 3 in Requirement 6 (commit `ca6cb5d3`), while the type codes (lines 77 and 83) and the `Unknown product type` throw (lines 87–88) are Developer 1's. The same conditionals are also reported from Developer 3's side, so the consolidation must register one boundary violation. Suggested split: Developer 1 creates the mapper classes (new classes are created by the owner of the violation) and Developer 3 moves his validation and `corruptLine` error paths into them, keeping `ProductRepositoryTest` passing. The Technical Leader coordinates the order of the commits. |

### V-02 — `ProductService` bound to the concrete repository

| Field | Content |
|---|---|
| **ID** | V-02 |
| **Pattern** | Indirection (also affects Low Coupling and Protected Variations; recorded once here) |
| **Module** | Products (Workshop 1) |
| **Evidence** | `ProductService` in `develop`: field `private ProductRepository repository;` (line 22); no-arg constructor `public ProductService()` (lines 28–30), whose body is `this(new ProductRepository());` (line 29); constructor `public ProductService(ProductRepository repository)` (line 37). The only place that builds the service is `Main` (line 70): `new ProductService(productRepository)`; the no-arg constructor is not called anywhere in `src`. |
| **Explanation** | The service depends on the concrete CSV repository class and also contains code that instantiates it itself, so it is bound to one storage implementation. |
| **Consequence** | Changing the storage (JSON, database) or testing the service with an in-memory fake requires editing `ProductService`; service and persistence cannot evolve independently. |
| **Proposed solution** | Extract an interface `ProductStorage` (`load()`, `saveAll(List<Product>)`) in `com.gamezone.persistence`; `ProductRepository` implements it and `ProductService` depends only on the interface. The unused no-arg constructor is removed, so the service never creates its own repository. Pattern applied: Indirection (supports Low Coupling and Protected Variations). |
| **Code of another integrant** | No. `Main` already injects the repository through the constructor and `ProductRepository` will implement the new interface, so `Main` compiles unchanged; to be confirmed with `mvn clean compile`. |

### V-03 — Internal product list exposed

| Field | Content |
|---|---|
| **ID** | V-03 |
| **Pattern** | Protected Variations |
| **Module** | Products (Workshop 1) |
| **Evidence** | `ProductService.listAll` (lines 130–132 in `develop`): `return products;` (line 131), where `products` is the internal `List<Product>` field. |
| **Explanation** | The method hands out the internal mutable collection, so the service's internal representation is visible and modifiable from outside. |
| **Consequence** | Any caller can add or remove products bypassing `validateBasicData` and `repository.saveAll`, leaving memory and `data/products.csv` out of sync. The internal structure cannot change without breaking callers. |
| **Proposed solution** | Return `Collections.unmodifiableList(products)` (a read-only live view, so read-only callers observe exactly the same results). Pattern applied: Protected Variations (information hiding). |
| **Code of another integrant** | No change is needed in other code. In `develop`, `listAll()` is called from `SaleService` (lines 56 and 68) and `SubMenu` (line 116), and those calls only read the list (passing it as an argument, `stream()` and a `for` loop). The argument at `SaleService` line 56 is received by `SaleRepository.loadAll`, which only iterates over it to build a lookup map. |

### V-04 — Console compatibility implemented twice

| Field | Content |
|---|---|
| **ID** | V-04 |
| **Pattern** | Protected Variations |
| **Module** | Accessories (Requirement 1) |
| **Evidence** | In `develop`, `Controller` (lines 13, 28 and 54–83; `isCompatibleWith` at line 81) and `Memory` (lines 14, 31 and 75–104; `isCompatibleWith` at line 102) contain the same code: the field `compatibleConsoleIds`, the constructor line `this.compatibleConsoleIds = (compatibleConsoleIds != null) ? new ArrayList<>(compatibleConsoleIds) : new ArrayList<>();`, and the methods `getCompatibleConsoleIds`, `addCompatibleConsole` and `isCompatibleWith`. In addition, both `isCompatibleWith` overrides (`return this.compatibleConsoleIds != null && this.compatibleConsoleIds.contains(consoleId);`) repeat what the interface already provides as a default method in `ConsoleCompatible.isCompatibleWith`, which checks for a null `consoleId` and uses `getCompatibleConsoleIds().contains(consoleId)` (line 31 of `ConsoleCompatible` in `develop`, added in commit `cdfc589`). |
| **Explanation** | The console compatibility rules are a predictable variation point (for example, matching by console generation instead of by identifier), but they are implemented twice instead of being encapsulated in a single place. |
| **Consequence** | Any change to how compatibility is stored or evaluated must be made, and kept consistent, in both classes. A third compatible accessory type would require a third copy. |
| **Proposed solution** | Create an abstract class `CompatibleAccessory extends Accessory implements ConsoleCompatible` in `com.gamezone.model` that owns the compatibility list, its defensive copy in the constructor, `getCompatibleConsoleIds` and `addCompatibleConsole`; `Controller` and `Memory` extend it. The two `isCompatibleWith` overrides are removed so both classes use the interface's default method, which returns the same result for every input (a null or unknown `consoleId` yields false). Pattern applied: Protected Variations, supported by Polymorphism. |
| **Code of another integrant** | No change is needed in another integrant's code. The default `isCompatibleWith` in `ConsoleCompatible` stays as it is, and `AccessoryService` (Developer 2, line 150) calls `isCompatibleWith` through the interface, so removing the overrides does not affect it; to be confirmed by compiling. |

### V-05 — Percentage rule implemented three times

| Field | Content |
|---|---|
| **ID** | V-05 |
| **Pattern** | Protected Variations |
| **Module** | Promotions (Requirement 2) |
| **Evidence** | `PercentageDiscount` (field at line 10, `setPercentage` at lines 30–35), `CategoryDiscount` (field at line 10, `setPercentage` at lines 33–38) and `BulkPurchaseDiscount` (field at line 11, `setPercentage` at lines 44–49) each declare `private double percentage`, `getPercentage()` and the same range validation in `setPercentage`: `if (percentage < 0.0 \|\| percentage > 100.0)` (lines 31, 34 and 45 respectively). |
| **Explanation** | The percentage attribute, its valid range and the way it is applied are common to three promotion types but are copied in each one instead of being defined in a single place. |
| **Consequence** | A change in the percentage rule (for example, a maximum discount of 50%, or a different rounding) has to be repeated in three classes and can easily become inconsistent. Each new percentage-based promotion adds a fourth copy. |
| **Proposed solution** | Create an abstract class `PercentageBasedPromotion extends Promotion` in `com.gamezone.model` that holds `percentage`, `getPercentage`, the validated `setPercentage` and a protected method `applyPercentage(double base)` returning `base * (percentage / 100.0)`. The three classes extend it and keep their own constructors and calls in the current order, so validation order and exception messages do not change. `Promotion` itself stays free of percentage logic so that non-percentage promotions can be added later. Pattern applied: Protected Variations. |
| **Code of another integrant** | No. `PromotionRepository` and `PromotionService` (Developer 2) use the same public getters and constructors, which do not change; to be confirmed by compiling. |

### V-06 — Category chosen with `instanceof` in `CategoryDiscount`

| Field | Content |
|---|---|
| **ID** | V-06 |
| **Pattern** | Polymorphism (also affects Low Coupling and Protected Variations; recorded once here) |
| **Module** | Promotions (Requirement 2) |
| **Evidence** | `CategoryDiscount.matchesCategory` (lines 76–90): `String cat = category.trim().toUpperCase(); if (cat.equals("VIDEOGAME")) { return product instanceof VideoGame; } else if (cat.equals("CONSOLE")) { return product instanceof Console; } else if (cat.equals("ACCESSORY")) { return product instanceof Accessory; }`. |
| **Explanation** | The method translates a category string into a check on the concrete subclass of the product with a conditional chain, so the promotion knows the concrete product classes and the product type set. The product itself is the one that knows what category it belongs to. |
| **Consequence** | Every new product category (or a new `Product` subclass) requires editing `CategoryDiscount`, and a missed branch silently returns false and the discount is not applied. |
| **Proposed solution** | Add a `ProductCategory` enum (`VIDEOGAME`, `CONSOLE`, `ACCESSORY`) in `com.gamezone.model` and an abstract method `ProductCategory getCategory()` in `Product`, implemented by `VideoGame`, `Console` and `Accessory` (inherited by `Controller`, `Cable` and `Memory`). `matchesCategory` keeps the same `trim().toUpperCase()` normalization and compares it with `product.getCategory().name()`; an unknown category still returns false. `targetCategory` remains a `String`, so `PromotionRepository` is not affected. Pattern applied: Polymorphism (with Information Expert). |
| **Code of another integrant** | No. In `develop`, `extends Product` appears only in `Accessory`, `Console` and `VideoGame`, all Developer 1 code, so no other class is forced to implement the new method. |

### V-07 — Total units rebuilt outside `Sale`

| Field | Content |
|---|---|
| **ID** | V-07 |
| **Pattern** | Information Expert |
| **Module** | Promotions (Requirement 2), boundary with Sales (Workshop 1 / Requirement 2) |
| **Evidence** | `BulkPurchaseDiscount.calculateDiscount` (lines 57–62): `int totalUnits = 0; for (SaleDetail detail : sale.getDetails()) { if (detail != null) { totalUnits += detail.getQuantity(); } }`. In contrast, `PercentageDiscount.calculateDiscount` asks the sale for its subtotal (`sale.calculateTotal()`) instead of recomputing it. In `develop`, `Sale` has no method that returns the total number of units (`getQuantity` appears in `Sale` only inside `generateReceipt`). |
| **Explanation** | `Sale` holds the list of details and is the class with the information needed to know how many units were sold; the promotion rebuilds that number by navigating `Sale` and `SaleDetail`. |
| **Consequence** | The total-units rule would be repeated by any other class that needs it (receipts, returns), and a change in how `Sale` stores its details would break the promotion. |
| **Proposed solution** | Add `Sale.getTotalUnits()` (sum of the quantity of the non-null details, 0 when there are no details) and make `BulkPurchaseDiscount` call it. The result for every sale stays identical. Pattern applied: Information Expert. |
| **Code of another integrant** | Yes: `Sale` belongs to the Technical Leader, who adds `getTotalUnits()`; no equivalent method exists in `develop`. Developer 1 then updates `BulkPurchaseDiscount`. The Technical Leader coordinates the order of the commits. |

### V-08 — Discount ratio and refund computed twice in `Return`

| Field | Content |
|---|---|
| **ID** | V-08 |
| **Pattern** | Information Expert (also affects Protected Variations; recorded once here) |
| **Module** | Returns (Requirement 3), boundary with Sales (Workshop 1 / Requirement 2) |
| **Evidence** | `Return.calculateRefundAmount` (lines 107–109 and 113 in `develop`) and `Return.generateReturnReceipt` (lines 131–133 and 148–150) both contain `double subtotalSale = originalSale.calculateSubtotal(); double discountSale = originalSale.getDiscountAmount(); double discountRatio = (subtotalSale > 0) ? (discountSale / subtotalSale) : 0.0;`. The per-item refund is then computed in two different arithmetic forms: `product.getPrice() * (1.0 - discountRatio)` in `calculateRefundAmount`, and `proportionalDiscount = listPrice * discountRatio; netItemRefund = listPrice - proportionalDiscount;` in the receipt. |
| **Explanation** | The discount ratio is derived only from information held by `Sale` (its subtotal and its discount), yet `Return` pulls both values and computes the ratio itself, twice. The per-item refund rule also exists in two places. |
| **Consequence** | A change in how the proportional refund is computed must be repeated in two places, and the amounts printed in the receipt and the stored `refundAmount` can drift apart because they are calculated with different arithmetic. |
| **Proposed solution** | Add `Sale.getDiscountRatio()` (the same expression, 0.0 when the subtotal is not positive) and use it in `Return` through a single private method that computes the refund of one product, shared by `calculateRefundAmount` and `generateReturnReceipt`. The printed amounts must be checked identical in the regression verification; if any difference appears, each original arithmetic form is kept and only the ratio is shared. Pattern applied: Information Expert. |
| **Code of another integrant** | Yes: `Sale` belongs to the Technical Leader, who adds `getDiscountRatio()`. Developer 1 then updates `Return`. The Technical Leader coordinates the order of the commits. |

### V-09 — Receipt formatting inside `Return`

| Field | Content |
|---|---|
| **ID** | V-09 |
| **Pattern** | High Cohesion (also affects Pure Fabrication, which guides the correction; recorded once here) |
| **Module** | Returns (Requirement 3) |
| **Evidence** | `Return.generateReturnReceipt` (lines 127–168 in `develop`): it builds the console receipt with `DateTimeFormatter.ofPattern("dd/MM/yyyy")`, banner lines such as `" RECIBO DE DEVOLUCIÓN \n"` and `String.format` layouts, inside the same class that stores the return data and calculates the refund. In `develop`, the method is called only from `SubMenu` (lines 451, 465, 484 and 503). |
| **Explanation** | `Return` has two reasons to change: the refund rules and the layout of a user-facing document. The text presentation is a responsibility that does not belong to the domain class. |
| **Consequence** | Changing the wording, layout or output channel of the receipt means editing the class that holds the refund rules; the refund logic cannot be reused or tested without the presentation code. |
| **Proposed solution** | Create a `ReturnReceiptFormatter` with a method `format(Return)` in `com.gamezone.ui` (all the callers are UI code, so dependencies keep the ui → service → persistence → model direction) that produces exactly the same text, and remove `generateReturnReceipt` from `Return`. It is applied after V-08 so that the shared refund method is already in place. Pattern applied: Pure Fabrication (restores High Cohesion). |
| **Code of another integrant** | Yes: the four calls in `SubMenu` (lines 451, 465, 484 and 503) must be changed to use the formatter. All four were written by the Technical Leader (`git blame` attributes the whole of `SubMenu` to her), and the change belongs to her. |

### V-10 — Certificate formatting inside `Warranty`

| Field | Content |
|---|---|
| **ID** | V-10 |
| **Pattern** | High Cohesion (also affects Pure Fabrication, which guides the correction; same cause as V-09 in a different class) |
| **Module** | Warranties (Requirement 4) |
| **Evidence** | `Warranty.generateWarrantyCertificate` (lines 72–82 in `develop`): it builds the certificate text with `DateTimeFormatter.ofPattern("dd/MM/yyyy")` and a concatenated layout (`"=== CERTIFICADO DE GARANTÍA ===\n" + "ID Garantía: " + id + ...`), inside the base class that holds the warranty data and rules. In `develop`, the method is called only from `SubMenu` (lines 394 and 424). |
| **Explanation** | `Warranty` has two reasons to change: the warranty rules (dates, activity, cost) and the layout of a user-facing document. The text presentation does not belong to the domain class. |
| **Consequence** | Changing the wording, layout or output channel of the certificate means editing the base class of the whole warranty hierarchy; the domain rules cannot be reused or tested without the presentation code. |
| **Proposed solution** | Create a `WarrantyCertificateFormatter` with a method `format(Warranty)` in `com.gamezone.ui` (all the callers are UI code) that produces exactly the same text, including the way `getAdditionalCost()` is printed, and remove `generateWarrantyCertificate` from `Warranty`. Pattern applied: Pure Fabrication (restores High Cohesion). |
| **Code of another integrant** | Yes: the two calls in `SubMenu` (lines 394 and 424) were written by the Technical Leader according to `git blame`, and she must change them to use the formatter. |

### V-11 — Stock rule repeated in `ProductValidator`

| Field | Content |
|---|---|
| **ID** | V-11 |
| **Pattern** | Information Expert |
| **Module** | Exceptions and validations (Requirement 6) |
| **Evidence** | `ProductValidator.validateStockAvailability` (lines 56–62): `if (product.getStock() < requestedQuantity) { throw new BusinessRuleException(...) }`. `Product.reduceStock` (lines 65–67) repeats the same comparison: `if (stock < quantity) { throw new IllegalArgumentException(...) }`. In `develop`, the validator is called from `SaleService` (line 89). |
| **Explanation** | `Product` owns stock and already decides whether there is enough of it; the validator reads `getStock()` from outside and repeats the decision. |
| **Consequence** | If the availability rule changes (for example, reserved or minimum stock), it has to be changed in two classes and they can disagree. |
| **Proposed solution** | Add `Product.hasStockFor(int quantity)` (returns `stock >= quantity`) and use it in both `Product.reduceStock` and `ProductValidator.validateStockAvailability`. Each one keeps its own exception type and message, so the user sees exactly the same text. Pattern applied: Information Expert. |
| **Code of another integrant** | No. `SaleService` (Technical Leader) only calls the validator, whose signature does not change. |

---

## 4. Notes for the consolidation

- **Boundary findings:** V-07 and V-08 each need a new method in `Sale` (Technical Leader) and a change in a Developer 1 class; each should be registered once in the consolidated audit. Both touch `Sale`, so the Technical Leader should schedule those commits together. V-01 also involves Developer 3 (the `corruptLine` calls and `ProductRepositoryTest`).
- **V-09 and V-10** are also boundary findings because the calls are in `SubMenu`. Suggested order for each one: Developer 1 adds the formatter without removing anything, the Technical Leader changes the calls in `SubMenu`, and then Developer 1 removes the old method (so the project compiles after every commit). V-09 is applied after V-08 because both change `Return.generateReturnReceipt`. `Sale.generateReceipt` (Technical Leader) has the same structure as V-09 and V-10, reported for her audit.
- **V-02 and V-03** need no change in code owned by other integrants (`Main` already injects the repository, and the callers of `listAll()` only read the list).
- **Ownership by account:** commits `cdfc589` (`ConsoleCompatible`) and `f61fefef` (`Sale`) are reformatting commits made from the Technical Leader's account. `ConsoleCompatible` and `Sale.canBeReturned` (added in `bb8770e`) are audited here and must be covered once in the coverage table. `CsvSupport` and the repository tests belong to Developer 3, who is not in the assignment table of the requirement.
- **Reported to Developer 2:** `WarrantyRepository` (line 119) chooses the stored type with `warranty instanceof BasicWarranty ? "BASIC" : "EXTENDED"`, a conditional on the concrete type in the same family as V-01.
- `ProductService.updateStock` (Developer 1) and `ProductService.restoreStock` (Technical Leader) repeat the same quantity validation before delegating to `Product`; any shared fix should be coordinated by the Technical Leader.
- **Refund restored from the file (reported by Developer 3 as V-01 of his audit):** the `Return` constructor (lines 34–42) takes the warranty part as its last parameter and calls `calculateRefundAmount()`, which adds the products refund on top. The CSV file stores the total refund, so `ReturnRepository` (lines 196–205, written by Developer 3) rebuilds the `Return` with a warranty part of `0.0` and then corrects the total with `setWarrantyRefundAmount(storedRefund - result.getRefundAmount())`. The root cause is in `Return`, which has no way to restore a stored total. In the consolidation this is one boundary violation: Developer 3 removes the arithmetic from the repository and Developer 1 adds the way to restore the stored total in `Return` (for example a constructor or factory for stored data), keeping the test `refundIsNotDoubledAfterReload` passing. It touches the same refund calculation as V-08, so both corrections are scheduled together.

---

## 5. Observations for the out-of-scope section (not corrected in this requirement)

Functional behavior that would change what the user sees:

- `PersistenceException` extends `RuntimeException`, but `TEAM.md` defines it as a subclass of `GameZoneException`. `SaleService` (lines 143–146) rethrows only `GameZoneException`, so it wraps a `PersistenceException` in a generic `RuntimeException` that the menu does not catch (reproduced by Developer 3, who reports it as FD-2 of his audit: the superclass was changed in commit `ca6cb5d`, part of his Requirement 6 refactoring). Correcting it changes which exception the menu catches.
- `Return` computes refunds with `Product.getPrice()` instead of the price paid in `SaleDetail.getUnitPrice()`, and `Return.refundAmount` is a stored value that is not updated if the original sale changes.
- `Sale.calculateTotal()` returns the subtotal (legacy alias) and is used by the promotions.
- The setters of `Promotion` accept null and inverted dates, unlike its constructor and `PromotionValidator`; `Warranty.isActive(null)` throws `NullPointerException` while `Promotion.isActive(null)` uses today's date.
- `Warranty.generateWarrantyCertificate` prints the additional cost without number formatting, unlike the other receipts.
- `VideoGame.describe` omits price and stock, while `Console.describe` and the accessories include them.
- `ProductService.updateStock` and `restoreStock` accept a quantity of 0, which `Product` then rejects with a different exception type and message.

Rules duplicated between model and validators whose messages or exception types differ (unifying them would change what the user sees):

- Product data rules in `ProductValidator.validateProductData` and `AccessoryValidator.validateAccessoryData` (different messages, and the accessory validator does not reject NaN prices).
- Cable length in `Cable.setLength` and `AccessoryValidator.validateCableData`; memory capacity in `AccessoryValidator.validateMemoryData` but not in `Memory.setCapacity`.
- Percentage in the three percentage promotions (range 0 to 100) and in `PromotionValidator.validatePromotionData` (0 excluded); minimum quantity in `BulkPurchaseDiscount` and `PromotionValidator.validateBulkPurchaseData`.

Other:

- `GameZoneException.getErrorCode()` and `InvalidDataException.getFieldName()` are never used.
- `PersonValidator.validatePersonData` ignores its phone parameter.
- `ProductService.registerVideoGame` and `registerConsole` still check platform and brand inline instead of in `ProductValidator`.
