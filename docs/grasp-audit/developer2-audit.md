# GRASP Audit - Developer 2

- **Auditor:** Andrea Laurens (alaurens), Developer 2
- **Repository state audited:** `develop` at commit `22e6df84fc809dd25f7869e22ab2c58f40e15d78` (merge of pull request #37, exception handling)
- **Audit branch:** `feature/grasp-audit`
- **Audit date:** 2026-10-22
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

### Lines I wrote in files owned by other developers (git blame at 22e6df84fc809dd25f7869e22ab2c58f40e15d78)

My scope includes the use of exceptions and validators in services and repositories. By `git blame`, I wrote the following lines in `SaleService` (Technical Leader) and `ProductService` (Developer 1). They are listed so that none of them is left out of the coverage. I audit only the exception and validation code in them; the design of the rest of each method is audited by its owner.

`SaleService`, 21 lines:

- Imports: lines 3-5 (exceptions) and 16-17 (validators).
- `findProductOrAccessoryById`: line 65 (`InvalidDataException` for a null product id) and line 75 (`ResourceNotFoundException` in `orElseThrow`).
- `processSale`: lines 81-82 (client lookup with `personService.findClientById`), 84-85 (`SaleValidator.validateSaleData`), 89 (`ProductValidator.validateStockAvailability`) and 143-145 (`instanceof GameZoneException` check).
- `findById`: line 169 (`InvalidDataException` for a null sale id), lines 172, 174 and 177 (`found` variable and the search that returns null) and lines 179-180 (`SaleValidator.validateSaleExists`).

`ProductService`, 26 lines:

- Imports: lines 3-5 (exceptions) and 7 (`ProductValidator`).
- Constructor `ProductService(ProductRepository)`: line 40 (null repository check).
- `validateBasicData`: lines 53-55 (Javadoc), 58 (`ProductValidator.validateProductData`) and 60 (`BusinessRuleException` for a duplicated id).
- `registerVideoGame`: line 96 (platform check). `registerConsole`: line 119 (brand check).
- `findById`: lines 170-171 (Javadoc), 175 (null id check), 177 and 180-181 (`found` variable) and 184-185 (`ProductValidator.validateProductExists`).
- `updateStock`: lines 194-195 (Javadoc) and 199 (negative quantity check).
- `restoreStock`: lines 213-214 (Javadoc) and 218 (negative quantity check). The rest of the method is not mine; the Technical Leader audits its design.

Lines 200-202 of `ReturnRepository` (the domain arithmetic that Xavi reported as H2) were written by `xacosta` in commit `8b771b5a`, not by me. I do not audit them.

## 2. Verdict criteria

- **Violation:** the code breaks the pattern at one or more points, and a concrete class, method and line range can be cited.
- **Correct:** the pattern is applied, and the class and method that apply it can be cited.
- **Not applicable:** there is no point of application in the audited code, and the reason is explained.

## 3. Information Expert

**Verdict: Violation** (2 findings). The pattern is correctly applied in other places, listed below.

### Correct applications

- `Client.addPurchase` (`Client.java`, line 41) owns the purchase history, and `PersonService.registerPurchase` (line 172) delegates to it instead of editing the list itself.
- `ReturnService.generateMonthlyBalance` asks `Sale::calculateFinalTotal` (line 163) and `Return::getRefundAmount` (line 171) for the amounts instead of recomputing them.
- `AccessoryService.findAccessoriesCompatibleWith` asks each accessory `isCompatibleWith(consoleId)` (line 150) instead of reading its internal list of consoles.
- `ReturnService.registerReturn` asks `sale.canBeReturned()` (line 78) for the 30-day rule instead of computing it.

### Violations

| Field | Content |
|---|---|
| ID | V-01 |
| Pattern | Information Expert |
| Module | Returns (Requirement 3) |
| Evidence | `ReturnService.findProductInSale`, lines 56-65: the loop `for (SaleDetail detail : sale.getDetails())` reads `detail.getProduct().getId()` to look for a product. `Sale` has no method to look up one of its products. The same search is repeated in `ReturnRepository.findProductInSale`, lines 138-149 (my code). |
| Explanation | `Sale` owns the list of sale lines, so it is the expert on which products it contains. `ReturnService` reaches through `Sale`, then `SaleDetail`, then `Product` to answer a question that belongs to `Sale`. |
| Consequence | If `Sale` changes how it stores its lines (for example, grouping repeated products), `ReturnService` breaks. Any other class that needs the same search would repeat the loop. |
| Proposed solution | Add `Sale.findProductById(String productId)` (Information Expert) and make `ReturnService.findProductInSale` call it, keeping the same exception and the same message when the product is not in the sale. `ReturnRepository.findProductInSale` is changed the same way, to delegate to `Sale.findProductById`. |
| Code of another developer | Yes. `Sale` belongs to the Technical Leader, who would add the new method. |

| Field | Content |
|---|---|
| ID | V-02 |
| Pattern | Information Expert |
| Module | Warranties (Requirement 4) |
| Evidence | `WarrantyService.listWarrantiesExpiringSoon`, lines 145-148: the service reads `w.getEndDate()` and compares it with `today` and `limit`. In the same class, `listActiveWarranties` (line 132) asks the warranty with `w.isActive(today)` (`Warranty.java`, line 63). |
| Explanation | The rule about when a warranty is valid is split: `Warranty` answers "is it active?" but the service decides "does it expire soon?" using the warranty's end date. The warranty is the expert on its own dates. |
| Consequence | If the way the end date is calculated or treated changes (for example, a warranty with no end date), the change has to be made in two classes. |
| Proposed solution | Add `Warranty.expiresBetween(LocalDate from, LocalDate to)` with the same comparison and make the service call it. The result must stay identical. |
| Code of another developer | Yes. `Warranty` belongs to Developer 1. |

## 4. Creator

**Verdict: Correct.**

Each creation happens in a class that holds the list of the created objects and has the data needed to build them:

- `PersonService.registerClient` creates the `Client` (line 89) and stores it in the `clients` list it manages.
- `AccessoryService.registerController`, `registerCable` and `registerMemory` create the accessories (lines 71, 79 and 87) and add them to the `accessories` list it manages.
- `PromotionService` creates each promotion (lines 68, 76 and 84), `WarrantyService` creates each warranty (lines 74 and 82) and `ReturnService.registerReturn` creates the `Return` (line 109). In each case the service keeps the corresponding list.
- The repositories (`PersonRepository` lines 195 and 239, `AccessoryRepository` lines 193-212, `PromotionRepository` lines 164-177, `WarrantyRepository` lines 201-203, `ReturnRepository` line 197) create objects only to rebuild the ones they read from the files. They hold the initializing data, so this is also acceptable.

No instance is created by a class that has neither the data nor the container.

## 5. Controller

**Verdict: Not applicable.**

None of the audited classes receives system events. The services, the repositories and `Person`, `Client` and `Seller` contain no console input or output (there is no `Scanner` or `System.out` in them). The events of the system are received by `Menu` and `SubMenu`, which belong to the Technical Leader and call the services, for example `SubMenu` (line 240) calls `personService.registerClient(...)`. The audited services are the business layer that the controller delegates to, not controllers themselves.

## 6. Low Coupling

**Verdict: Violation** (2 findings).

### Correct applications

- `ReturnRepository.loadAll(List<Sale> sales)` (line 219) receives the sales it needs as a parameter instead of loading them itself.
- `AccessoryService`, `PromotionService`, `PersonService` and `ReturnService` receive their dependencies through the constructor, and each one depends only on its own repository (and on `SaleService` in the case of `ReturnService`).

### Violations

| Field | Content |
|---|---|
| ID | V-03 |
| Pattern | Low Coupling |
| Module | Warranties (Requirement 4) |
| Evidence | `WarrantyRepository`: five repository fields (lines 45-49), a constructor that requires all of them (lines 63-102) and `loadAll` (lines 229-235), which loads clients, sellers, products, accessories, promotions and then calls `saleRepository.loadAll(clients, sellers, products, accessories, promotions)`. `Main` (line 60) builds it with those five repositories. |
| Explanation | To resolve the sale and the product of each warranty, the repository depends on five other repositories and must know how each one is loaded and in which order. `ReturnRepository.loadAll(List<Sale> sales)` solves the same need by receiving the sales as a parameter. |
| Consequence | Loading warranties reads six CSV files again. The warranties end up referencing their own copies of sales, clients and products, not the objects held by `SaleService`, `PersonService` and `ProductService`. Any change in the signature of `SaleRepository.loadAll` breaks `WarrantyRepository`. |
| Proposed solution | Make `WarrantyRepository.loadAll` receive the already loaded sales and products, as `ReturnRepository` does, and remove its five repository dependencies. Because `SaleService` needs `WarrantyService` when it is built, the warranties would have to be loaded after the sales exist. |
| Non-regression evidence | This change makes the warranties share the same `Sale`, `Client` and `Product` objects as the services (not copies) and changes the construction order in `Main`, so it is not accepted without proof. Before editing, I check which fields `Warranty` reads from those objects: if it only reads data that nothing changes after loading (ids, names, prices, dates), sharing the objects cannot change what the user sees. Then I compare before and after with the same data: I run the menu with a fixed input script on `develop` (reference) and again after the change, and compare the console output and the `data/*.csv` files line by line. The script covers a sale with a basic and an extended warranty, a warranty query, a return that refunds a warranty, a sale that changes stock followed by a warranty query (the case where a copy and a shared object could differ), and a start with a missing or corrupt warranties file (error message and loading order). If any difference appears that cannot be removed without changing the behavior, V-03 moves to the out-of-scope findings, with that difference as the justification. |
| Code of another developer | Yes. The construction order is in `Main` (Technical Leader), and possibly in `SaleService`. |

| Field | Content |
|---|---|
| ID | V-04 |
| Pattern | Low Coupling |
| Module | Warranties (Requirement 4) |
| Evidence | `WarrantyService()`, lines 40-48: it creates `new WarrantyRepository(new SaleRepository(), new ProductRepository(), new PersonRepository(), new AccessoryRepository(), new PromotionRepository())`. No code calls this constructor: `Main` (line 74) uses `new WarrantyService(warrantyRepository)`, and no other `new WarrantyService()` exists in the project. Same case in `PersonService()`, lines 31-33 (my code): it creates a `PersonRepository` directly and no code calls it. |
| Explanation | The service is tied to six concrete classes when it is built, although it only needs a `WarrantyRepository`. |
| Consequence | Any change to the constructor of those repositories breaks `WarrantyService`, and the constructor is dead code that hides the real dependencies. |
| Proposed solution | Remove the unused no-argument constructor. The behavior of the system does not change. The unused `PersonService()` constructor is removed too. |
| Code of another developer | No. |

## 7. High Cohesion

**Verdict: Violation** (1 finding).

### Correct applications

- `PersonService` only manages people: registering clients (line 87), listing clients and sellers (lines 99 and 108), finding a person (lines 120 and 143) and recording purchases (line 167).
- `PromotionService`, `AccessoryService` and `WarrantyService` each handle only the creation, listing and search of their own catalog. Validation rules live in their own validator classes, not in the services.
- `Person`, `Client` and `Seller` hold only the data and behavior of a person (`Client.addPurchase`, `Seller.getEmployeeCode`, `getRoleDescription`).

### Violations

| Field | Content |
|---|---|
| ID | V-05 |
| Pattern | High Cohesion |
| Module | Returns (Requirement 3) |
| Evidence | `ReturnService.generateMonthlyBalance`, lines 157-176. It reads every sale through `saleService.listAllSales()` (line 158), filters them by month and year, adds their totals with `Sale::calculateFinalTotal` (line 163), and then subtracts the returns of the same month (lines 166-173). The rest of the class (`registerReturn`, lines 75-123, and the `view...` methods, lines 125-155) only processes and queries returns. The caller is `SubMenu`, line 524. |
| Explanation | Half of the method computes the monthly sales revenue, which has nothing to do with processing returns. `ReturnService` ends up with two unrelated responsibilities: processing returns and producing a financial report. |
| Consequence | The class has two reasons to change. If the balance changes (for example, taxes or another period), the class that handles returns must be edited and retested. `ReturnService` also needs `SaleService` in its constructor partly for this report. |
| Proposed solution | Extract the report into a new class `MonthlyBalanceService` (package `service`) that receives `SaleService` and `ReturnService`, with the same formula and the same order of operations so the result is identical. `ReturnService` keeps only the return rules. |
| Code of another developer | Yes. `SubMenu` (line 524) must call the new class and `Main` must build it. Both belong to the Technical Leader. |

## 8. Polymorphism

**Verdict: Violation** (4 findings).

### Correct applications

- `Person.getRoleDescription` (`Person.java`, line 47) is abstract and implemented by `Client` (line 48) and `Seller` (line 26). Nobody asks for the type of a person to build the description.
- `PromotionService.findBestPromotionFor` (line 153) calls `promotion.calculateDiscount(sale)` on every promotion without checking its concrete class.
- `AccessoryService.findAccessoriesCompatibleWith` (lines 148-150) works through the `ConsoleCompatible` interface instead of testing for `Controller` or `Memory`.
- `WarrantyService.assignBasicWarranty` and `assignExtendedWarranty` (lines 71 and 79) return the abstract behavior of `Warranty`; the duration is decided by each subclass.

### Violations

| Field | Content |
|---|---|
| ID | V-06 |
| Pattern | Polymorphism |
| Module | Accessories (Requirement 1) |
| Evidence | `AccessoryService.matchesType`, lines 126-135: an `if / else if` chain compares the text `"CONTROLLER"`, `"CABLE"` or `"MEMORY"` and then tests `accessory instanceof Controller`, `Cable` or `Memory`. It is used by `listAccessoriesByType` (line 121). |
| Explanation | The service decides behavior by the concrete type of each accessory instead of asking the accessory. |
| Consequence | A new accessory type (for example, a headset) is silently ignored by `listAccessoriesByType` until someone remembers to add another branch here. The service also imports every concrete accessory class. |
| Proposed solution | Add an abstract method `getAccessoryKind()` to `Accessory` (it cannot be called `getType` because `Memory.getType` already exists), implemented by `Controller`, `Cable` and `Memory`. `matchesType` becomes a comparison with `accessory.getAccessoryKind()`. The three accepted texts and the empty result for an unknown text stay the same. |
| Code of another developer | Yes. `Accessory`, `Controller`, `Cable` and `Memory` belong to Developer 1. |

| Field | Content |
|---|---|
| ID | V-07 |
| Pattern | Polymorphism |
| Module | Accessories (Requirement 1) |
| Evidence | `AccessoryRepository.accessoryToCsvLine`, lines 93-116: `instanceof Controller`, `Cable` and `Memory` choose the type tag and the extra columns. `csvLineToAccessory`, lines 188-213: the same three cases chosen by comparing the tag text. |
| Explanation | Each accessory type is handled by a conditional in the repository, in two places (writing and reading), that must be kept in sync by hand. |
| Consequence | Adding an accessory type requires editing two methods of the repository, and a forgotten branch only fails when the program is already running (`"Unknown accessory type"`, line 115). |
| Proposed solution | Create one small CSV format class per accessory type in `persistence` (tag, write columns and read columns together) and let the repository look them up by tag or by class. The file content must be identical. |
| Code of another developer | No. The new classes are created by Developer 2. |

| Field | Content |
|---|---|
| ID | V-08 |
| Pattern | Polymorphism |
| Module | Promotions (Requirement 2) |
| Evidence | `PromotionRepository.promotionToCsvLine`, lines 75-90 (`instanceof PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`) and `csvLineToPromotion`, lines 161-183 (the same three cases by tag text). |
| Explanation | Same problem as V-07, applied to promotions. |
| Consequence | A new kind of promotion requires editing two methods of the repository and fails at run time (`"Unknown promotion type"`, line 89) if one is forgotten. |
| Proposed solution | Same solution as V-07: one CSV format class per promotion type, with the same file content. |
| Code of another developer | No. |

| Field | Content |
|---|---|
| ID | V-09 |
| Pattern | Polymorphism |
| Module | Warranties (Requirement 4) |
| Evidence | `WarrantyRepository.warrantyToCsvLine`, line 119: `(warranty instanceof BasicWarranty) ? "BASIC" : "EXTENDED"`. `csvLineToWarranty`, lines 200-209: the tag text decides which class is created. |
| Explanation | The ternary assumes there are only two warranty types. Any other subclass would be saved as `EXTENDED`. |
| Consequence | A third warranty type would be written to the file with the wrong tag without any error, and the data would be corrupted when the file is read again. |
| Proposed solution | Same solution as V-07: one CSV format class per warranty type, and an error when the type is not registered. For the existing types the file content stays the same. |
| Code of another developer | No. |

## 9. Pure Fabrication

**Verdict: Violation** (1 finding).

### Correct applications

- `AccessoryValidator`, `PromotionValidator`, `WarrantyValidator` and `SaleValidator` are pure fabrications: the validation rules belong to no domain class, so they live in final classes with a private constructor and no state (for example `AccessoryValidator.java`, lines 13-17). They never touch files or repositories.
- `PersonService.validateClientData` (line 61), `AccessoryService.validateBasicData` (line 49) and `PromotionService.validateCommonPromotionData` (line 51) only coordinate calls to those validators.

### Violations

| Field | Content |
|---|---|
| ID | V-10 |
| Pattern | Pure Fabrication (it also lowers High Cohesion; Pure Fabrication guides the correction) |
| Module | People, Accessories, Promotions, Returns and Warranties (every repository in the scope) |
| Evidence | The same code to write a file is repeated in five repositories: `PersonRepository.writeLines`, lines 151-161; `AccessoryRepository.saveAll`, lines 143-154; `PromotionRepository.saveAll`, lines 118-129; `ReturnRepository.saveAll`, lines 117-128; `WarrantyRepository.saveAll`, lines 151-162. Each opens a `BufferedWriter`, writes every line and wraps `IOException` in `PersistenceException`. The reading loop (file exists, skip blank lines, count the line number) is repeated in `loadAll` / `loadClients` / `loadSellers` of the same five classes (for example `AccessoryRepository`, lines 229-252, and `PromotionRepository`, lines 195-218). |
| Authorship (git blame at 22e6df84fc809dd25f7869e22ab2c58f40e15d78) | In the five repeated writing blocks, `xacosta` wrote 7 lines in each one. `PersonRepository` 151-161: 7 xacosta, 3 mine, 1 Estefany Yulieth Montenegro Martelo. `AccessoryRepository` 143-154, `PromotionRepository` 118-129 and `ReturnRepository` 117-128: 7 xacosta, 2 mine, 3 Estefany Yulieth Montenegro Martelo each. `WarrantyRepository` 151-162: 7 xacosta, 2 mine, 3 yanelis1612. In total 11 lines are mine. `CsvSupport` has 211 lines, all by `xacosta`, none mine. |
| Explanation | Reading and writing text files line by line is a responsibility that belongs to no domain class, and no helper class exists for it. Each repository implements it again next to its own job of converting objects to CSV text. |
| Consequence | Any change in how files are handled (encoding, atomic writing, a new error message) must be repeated in each repository. Each repository has two reasons to change: the CSV format and the file mechanics. |
| Proposed solution | Do not create a new class. Extend the existing `CsvSupport` (Pure Fabrication) with two methods in its current style: one that writes a list of lines to a path, reusing `ensureParentDirectory` and `PersistenceException`, and one that reads the lines with their numbers. The five repositories delegate to them and keep their own error messages and the same files. This is a shared finding: the change in `CsvSupport` belongs to Xavi, and the change in each repository belongs to its owner. |
| Code of another developer | Yes. `CsvSupport` (Xavi, `xacosta`) must be extended, so this is a shared finding. The five repositories above are mine. `ProductRepository` (lines 117 and 192) and `SaleRepository` (lines 137 and 212) repeat the same code and belong to other developers; they are reported in the section on problems found in code of other developers. |

## 10. Indirection

**Verdict: Violation** (1 finding).

### Correct applications

- `ReturnRepository.loadAll(List<Sale> sales)` (line 219) receives the sales as a parameter, so the repository does not know where they come from.
- The validators sit between the services and the validation rules: the services call `AccessoryValidator`, `PromotionValidator` and `WarrantyValidator` and do not contain the rules themselves.

### Violations

| Field | Content |
|---|---|
| ID | V-11 |
| Pattern | Indirection |
| Module | People, Accessories, Promotions, Returns and Warranties (services) |
| Evidence | Every service holds a concrete repository class and loads its data from the files in its constructor: `PersonService`, lines 25 and 40-48 (`repository.loadClients()`, `repository.loadSellers()`); `AccessoryService`, lines 29 and 37-41; `PromotionService`, lines 31 and 39-43; `WarrantyService`, lines 34 and 56-60; `ReturnService`, lines 28 and 37-43. No interface exists between a service and its repository. `src/test` contains only the seven `*RepositoryTest` classes and no service test. The same design appears in `SaleService` (Technical Leader) and `ProductService` (Developer 1), which also depend on the concrete repository. |
| Explanation | The services talk directly to the CSV implementation. There is no intermediate abstraction that separates the business rules from the way data is stored. |
| Consequence | A service cannot be created without a real repository that reads `data/*.csv`, so the business rules cannot be tested alone (which is why no service has a test). Changing the storage (for example, a database) would require editing the five services. |
| Proposed solution | Create one small interface per module in `persistence` (for example `AccessoryStore` with `loadAll` and `saveAll`), implemented by the existing repositories. Each service depends on its interface. `Main` still compiles unchanged because it passes the same repository objects. |
| Code of another developer | Yes. `SaleService` (Technical Leader) and `ProductService` (Developer 1) have the same design, so changing their dependency on the concrete repository belongs to their owners. Boundary finding to be unified by the Technical Leader. |

## 11. Protected Variations

**Verdict: Violation.** The unprotected points of variation are the same fragments already registered above, so no new ID is created (see the cross-reference table below).

### Correct applications

- `AccessoryService.findAccessoriesCompatibleWith` (lines 148-151) depends on the `ConsoleCompatible` interface. A new accessory that is compatible with consoles needs no change in the service.
- `PromotionService.findBestPromotionFor` (lines 141-161) depends on `Promotion.calculateDiscount`. A new kind of promotion needs no change in the service.
- The validation rules are collected in the validators (for example `WarrantyValidator.validateDaysAhead`, line 48), so a change in a rule is made in one place and not in each service.

### Points of variation that are not protected

| Foreseeable change | Where the system breaks | Registered as |
|---|---|---|
| A new accessory, promotion or warranty type | Type conditionals in `AccessoryService.matchesType` and in the CSV code of `AccessoryRepository`, `PromotionRepository` and `WarrantyRepository` | V-06, V-07, V-08, V-09 |
| A change in how files are read or written | The same file code repeated in five repositories | V-10 |
| A change of storage technology | Services bound to the concrete CSV repositories | V-11 |

### Why no new finding is registered

Each row is the same code seen from a different pattern. Registering it again under Protected Variations would duplicate the finding and the correction. The corrections of V-06 to V-11 are the ones that protect the system against these changes, so Protected Variations is the pattern that justifies them and Polymorphism, Pure Fabrication and Indirection are the mechanisms used.

## 12. Summary of verdicts

| # | Pattern | Verdict | Findings |
|---|---|---|---|
| 1 | Information Expert | Violation | V-01, V-02 |
| 2 | Creator | Correct | none |
| 3 | Controller | Not applicable | none |
| 4 | Low Coupling | Violation | V-03, V-04 |
| 5 | High Cohesion | Violation | V-05 |
| 6 | Polymorphism | Violation | V-06, V-07, V-08, V-09 |
| 7 | Pure Fabrication | Violation | V-10 |
| 8 | Indirection | Violation | V-11 |
| 9 | Protected Variations | Violation | covered by V-06 to V-11 |

## 13. List of violations

| ID | Pattern | Class and method | Module | Changes code of another developer |
|---|---|---|---|---|
| V-01 | Information Expert | `ReturnService.findProductInSale` and `ReturnRepository.findProductInSale` | Returns | Yes, Technical Leader (`Sale`) |
| V-02 | Information Expert | `WarrantyService.listWarrantiesExpiringSoon` | Warranties | Yes, Developer 1 (`Warranty`) |
| V-03 | Low Coupling | `WarrantyRepository` (constructor and `loadAll`) | Warranties | Yes, Technical Leader (`Main`, possibly `SaleService`) |
| V-04 | Low Coupling | `WarrantyService()` and `PersonService()` | Warranties, People | No |
| V-05 | High Cohesion | `ReturnService.generateMonthlyBalance` | Returns | Yes, Technical Leader (`SubMenu`, `Main`) |
| V-06 | Polymorphism | `AccessoryService.matchesType` | Accessories | Yes, Developer 1 (`Accessory` and subclasses) |
| V-07 | Polymorphism | `AccessoryRepository.accessoryToCsvLine`, `csvLineToAccessory` | Accessories | No |
| V-08 | Polymorphism | `PromotionRepository.promotionToCsvLine`, `csvLineToPromotion` | Promotions | No |
| V-09 | Polymorphism | `WarrantyRepository.warrantyToCsvLine`, `csvLineToWarranty` | Warranties | No |
| V-10 | Pure Fabrication | File reading and writing in five repositories | All | Yes: shared finding with Xavi (`xacosta`), who owns `CsvSupport`; `ProductRepository` and `SaleRepository` also repeat the code and are not mine |
| V-11 | Indirection | Services bound to concrete repositories | All | Yes: `SaleService` (Technical Leader) and `ProductService` (Developer 1) share the design |

## 14. Findings reported to the Technical Leader

### Boundary findings

These findings need a change in code owned by another developer. The corrections that are mine and the parts that belong to others are:

- **V-01:** I change `ReturnService.findProductInSale`; the Technical Leader adds `Sale.findProductById`.
- **V-02:** I change `WarrantyService.listWarrantiesExpiringSoon`; Developer 1 adds `Warranty.expiresBetween`.
- **V-03:** I change `WarrantyRepository`; the Technical Leader adapts the construction order in `Main` (and `SaleService` if needed).
- **V-05:** I create `MonthlyBalanceService`; the Technical Leader updates `SubMenu` (line 524) and `Main`.
- **V-06:** I change `AccessoryService.matchesType`; Developer 1 adds `getAccessoryKind()` to `Accessory` and its subclasses.

### Problems found in code of other developers (not audited here)

- `ProductRepository` (lines 117 and 192) and `SaleRepository` (lines 137 and 212) repeat the file reading and writing code described in V-10.

### Possible functional defects (out of scope, to be confirmed by the Technical Leader)

These are not corrected in this requirement because fixing them would change what the user sees.

- `ReturnService.registerReturn`, lines 112-115: stock is restored with `saleService.restoreStock(product, 1)`, always one unit, although `SaleDetail` stores a quantity. If a product was sold in quantity greater than one, the stock restored may be lower than the stock sold. The code comment itself mentions this.
- `ReturnService` reports errors with `IllegalArgumentException` and English messages (lines 47, 52, 64, 79 and others), while the rest of the system uses the typed exceptions of Requirement 6. Changing it would change how `Menu` treats the error and the message the user reads.
- `WarrantyService.findById` and `findWarrantyByProduct` (lines 94-119) return `null` when nothing is found, while the other services throw `ResourceNotFoundException`. Changing it would change the behavior seen by the caller.
