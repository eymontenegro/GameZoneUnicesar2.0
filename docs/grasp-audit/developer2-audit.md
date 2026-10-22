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
| Evidence | `ReturnService.findProductInSale`, lines 56-65: the loop `for (SaleDetail detail : sale.getDetails())` reads `detail.getProduct().getId()` to look for a product. `Sale` has no method to look up one of its products. |
| Explanation | `Sale` owns the list of sale lines, so it is the expert on which products it contains. `ReturnService` reaches through `Sale`, then `SaleDetail`, then `Product` to answer a question that belongs to `Sale`. |
| Consequence | If `Sale` changes how it stores its lines (for example, grouping repeated products), `ReturnService` breaks. Any other class that needs the same search would repeat the loop. |
| Proposed solution | Add `Sale.findProductById(String productId)` (Information Expert) and make `ReturnService.findProductInSale` call it, keeping the same exception and the same message when the product is not in the sale. |
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
| Code of another developer | Yes. The construction order is in `Main` (Technical Leader), and possibly in `SaleService`. |

| Field | Content |
|---|---|
| ID | V-04 |
| Pattern | Low Coupling |
| Module | Warranties (Requirement 4) |
| Evidence | `WarrantyService()`, lines 40-48: it creates `new WarrantyRepository(new SaleRepository(), new ProductRepository(), new PersonRepository(), new AccessoryRepository(), new PromotionRepository())`. No code calls this constructor: `Main` (line 74) uses `new WarrantyService(warrantyRepository)`, and no other `new WarrantyService()` exists in the project. |
| Explanation | The service is tied to six concrete classes when it is built, although it only needs a `WarrantyRepository`. |
| Consequence | Any change to the constructor of those repositories breaks `WarrantyService`, and the constructor is dead code that hides the real dependencies. |
| Proposed solution | Remove the unused no-argument constructor. The behavior of the system does not change. |
| Code of another developer | No. |