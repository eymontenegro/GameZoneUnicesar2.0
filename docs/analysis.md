# Analysis and Design — GameZone Unicesar

## About the people in the system

**1. What attributes are common to all people who interact with the store, and which are specific to each particular type of person? How is this distinction reflected in a class hierarchy?**

All people who interact with the store share basic attributes such as name, identification, and phone number, regardless of type. However, each type has its own specific characteristics: the Client has an email address and purchase history, while the Seller has an employee code and a shift. This distinction is reflected in the hierarchy through a base class Person, from which Client and Seller inherit, each adding its own particular attributes.

**2. Should there be a class representing a "generic person" without specifying their role? Why or why not? What implication does this decision have on the possibility of instantiating that class?**

There should not be a generic, instantiable Person class; the class must be abstract. The reason is that, in the GameZone context, there is never someone who is simply a person without a specific role: they will always be either a Client or a Seller. For this reason, the Person class must be declared as abstract, which prevents it from being instantiated directly and forces every real object to belong to one of the two concrete types.

## About the products in the system

**3. What characteristics do all the products sold by the store have in common, regardless of type? What characteristics are specific to each type of product?**

All products sold by the store share common attributes: an identifier, a title, a price, and the quantity available in inventory. These are placed in a base class Product, declared as abstract. Each specific type adds its own attributes: VideoGame has a platform, a genre, and an age rating, while Console has a brand, a model, and a generation. Both subclasses inherit the common attributes of Product and add their own particular ones.

**4. Each type of product must be able to provide a description that integrates its particular characteristics. How should this behavior be declared in the base class to guarantee that every subclass implements it in its own way? What object-oriented programming mechanism makes this possible?**

The description method is declared in the Product class without a body, marked with the `abstract` keyword: `public abstract String describe();`. This forces each subclass — VideoGame and Console — to implement it with its own content, using the `@Override` annotation. The object-oriented programming mechanism that allows this is polymorphism, supported by inheritance: each type of product responds differently to the same `describe()` message.

## About sales and relationships between entities

**5. A sale involves a client, a seller, and one or more products. What types of relationships exist between the class representing the sale and the other classes in the system? Are these relationships inheritance, association, composition, or another type? Justify your answer.**

The Sale class maintains several types of relationships with the other classes in the system. With Client, the relationship is an association, since the client exists independently of the sale. The same applies to Seller: it is an association, because the seller exists independently and is, in fact, preloaded into the system before any sale is registered. With Product, the relationship is also an association, although indirect, through SaleDetail: the product continues to exist in inventory even if the sale is deleted. Finally, the relationship between Sale and SaleDetail is a composition, since a sale detail has no meaning outside the sale that contains it; if the sale is deleted, its details are deleted as well.

**6. Should the sale be responsible for calculating its own total, or should this responsibility fall on another class? Justify your decision.**

Calculating the total should be the responsibility of the Sale class itself, through a `calculateTotal()` method that adds up the subtotals of its SaleDetail items. The reason is that Sale already has all the information it needs — its own list of details — to perform the calculation. If this responsibility fell on SaleService, that class would have to reach into Sale from the outside to get the data, exposing Sale's internal structure and breaking encapsulation.

## About business rules

**7. How does the design guarantee that a sale cannot be registered without at least one product? At what point in the system should this rule be validated?**

It is guaranteed that a sale cannot be registered without at least one product by validating this inside the Sale class itself, in the method that confirms or registers the sale (`confirm()`), where it checks that the list of details is not empty. The reason is that this rule only depends on data the sale itself already holds, so there is no need to delegate it to another class.

**8. How is the automatic inventory update reflected in the design when a sale is registered? Which classes are involved in this operation?**

Updating the inventory is coordinated from SaleService, which goes through the sale's details and, for each one, asks the corresponding product to reduce its stock. Product itself controls its own stock through a `reduceStock(quantity)` method, which checks whether there is enough available before subtracting. Unlike the validation in question 7, this rule does not stay only within Sale, because here information from two modules — sales and products — is combined in a more complex orchestration.

## About the layered organization

**9. The system must be organized into four layers: model, persistence, services, and user interface. What type of classes belong to each layer? What criterion determines which layer a class should be placed in?**

The system is organized into four layers. In `model` there are Person (abstract), Client and Seller (which inherit from Person), Product (abstract), VideoGame and Console (which inherit from Product), and finally Sale and SaleDetail. In `persistence` there is one repository class per module — products, people, and sales — responsible for saving and loading data from files, with automatic loading when the application starts and automatic saving at the end of each operation. In `service` there is one service class per module, following the same three divisions. And in `ui` there is a single console menu class. The general criterion for deciding which layer a class belongs to is the responsibility it fulfills: if it represents a business concept, with its own data and rules, it belongs in model; if its job is to read or write information from files, it belongs in persistence; if it orchestrates business rules by combining several classes, it belongs in service; and if it interacts directly with the user, it belongs in ui.

**10. Why should the logic for saving and retrieving data from files not be inside the domain classes? What problems arise when these responsibilities are mixed together?**

Product and the other model classes represent data and business rules, not technical storage concerns. If that logic were mixed into the domain and the file format changed, classes like Product would need to be modified, even though their business behavior had not changed at all. That is why this responsibility is separated into its own persistence class, such as ProductRepository. In addition, mixing this logic into the domain makes it harder to test the class in isolation, since it would end up depending on files on disk. It also couples the business logic to a technical detail — the storage mechanism — that should be able to change without affecting the domain's behavior.

**11. What dependencies are allowed between the layers, and which are forbidden? Justify the direction of the allowed dependencies.**

The allowed dependencies follow the order `ui → service → persistence → model`: the user interface communicates with the services, the services with persistence, and persistence with the model. It is forbidden for ui to talk directly to persistence or to model, bypassing the service layer. The reason is that if ui skips service, the business rules that live there are lost — for example, the check for sufficient stock — and information would end up being saved without any control.

---

## Requirement 1 — Accessory Module

**1. Should accessories extend the existing Product hierarchy, or form an independent hierarchy?**

Accessories should be integrated into the existing product hierarchy, extending `Product`, rather than forming an independent hierarchy. The reason is that accessories share exactly the same base attributes that `Product` already has — id, title, price, and stock — so creating a separate hierarchy would duplicate those attributes and their associated behavior (such as `reduceStock()`). By extending `Product`, existing code is reused and model coherence is preserved: everything the store sells, whether a video game, a console, or an accessory, is conceptually a type of `Product`. This also means `Accessory` objects can be stored directly in the existing `SaleDetail.product: Product` field without any structural change.

**2. Common vs. specific attributes among the three accessory types**

The three accessory types share only what they inherit from `Product` (id, title, price, stock); `Accessory` itself is a purely structural abstract class and adds no attributes of its own. Console compatibility is **not** common to all three types — the requirement states it applies "particularly to controllers and some memories," so `Cable` never needs it. For that reason, compatibility is modeled through a `ConsoleCompatible` interface (`getCompatibleConsoleIds()`, `addCompatibleConsole()`, `isCompatibleWith()`), implemented only by `Controller` and `Memory`. `Cable` does not implement it, and therefore never carries an unused attribute. Each concrete subclass adds its own specific attributes: `Controller` has connection type plus its list of compatible console ids; `Cable` has length and connector type only; `Memory` has storage capacity and type plus its own list of compatible console ids.

**3. How is accessory–console compatibility represented in design and persistence?**

Compatibility is represented as an attribute of the accessory, not of the console: each `Controller` and `Memory` instance stores its own list of console identifiers (`List<String>`), populated through the `ConsoleCompatible` interface. This keeps `Console` itself simple — it only needs an `id` field (in addition to `brand`, `model`, `generation`) so that accessories can reference it by identifier, consistent with the pattern already used elsewhere in the system (for example, `SaleDetail`, which stores only the product's `id`, not the full object). Flat-file persistence can only store simple identifiers, so `AccessoryRepository` persists the raw list of console ids per accessory; when loading data, those identifiers are used to look up and reconstruct the real reference to each `Console` from the list of available consoles in the system. Modeling it as an accessory-side attribute (rather than a console-side one, or a separate association table) avoids touching the existing `Console` class beyond adding the `id`.

**4. What changes are needed in SaleService to support accessories?**

`SaleService.registerSale()` does not need structural changes to `SaleDetail`, since `Accessory` extends `Product`, and an accessory can therefore be stored in the same existing `product: Product` attribute. The necessary changes are: (1) `SaleService` must receive a reference to `AccessoryService` in its constructor, just as it already receives `ProductService` and `PersonService`; and (2) at the point where stock is updated, an `instanceof` check must be used to determine whether the sold item is an `Accessory`, delegating the update to the corresponding service (`accessoryService.updateStock(...)` or `productService.updateStock(...)`), exactly as the requirement specifies. Stock validation and total calculation require no changes, since both already operate on `Product` generically, regardless of the concrete type.

**5. Which layer should the new accessory classes belong to?**

The new classes follow the same responsibility criterion already established in the system: `Accessory`, `Controller`, `Cable`, `Memory`, and the `ConsoleCompatible` interface belong in `model`, since they represent business concepts with their own data and behavior (inheriting from `Product`). `AccessoryRepository` belongs in `persistence`, responsible for saving and loading accessories from a file. And `AccessoryService` belongs in `service`, coordinating the accessory module's business logic, using `AccessoryRepository` for storage, and communicating with `SaleService` for inventory updates during a sale.

---

## Requirement 2 — Promotion Module

**1. How is the shared structure among promotion types reflected in the class hierarchy, and what mechanism allows each type to calculate its discount independently?**

The three promotion types share common attributes and behavior (identifier, name, start date, end date, and the `isActive()` method), which are placed in an abstract base class `Promotion`. Each concrete subclass (`PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`) inherits this common structure and adds its own specific attributes and calculation logic. The mechanism that allows the rest of the system to call `calculateDiscount()` on any promotion without knowing its concrete type is polymorphism, supported by inheritance — the same pattern already used in the system with `Product.describe()`.

**2. How is the discount calculation method declared in the base class, and what does this declaration guarantee?**

The method is declared in the base class as `public abstract double calculateDiscount(Sale sale);`, without a body. This declaration guarantees that every concrete subclass of `Promotion` (`PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`) must provide its own implementation of the method; otherwise, the code will not compile. This ensures that each promotion type calculates its discount using its own logic, with no concrete type left unimplemented.

**3. Where is the promotion-selection logic located, and why is this consistent with the layered architecture? Why must this logic not be in Sale or in the console menu?**

This selection logic is located in `PromotionService`, specifically in the `findBestPromotionFor(Sale sale)` method. This placement is consistent with the layered architecture because `PromotionService` is the class with access to all registered promotions in the system — something `Sale` does not have and should not have. This logic must not be in `Sale`, because `Sale` does not and should not know the full set of promotions in the system; it only has the data of its own transaction. It must not be in the console menu either, because if this business logic lived in the `ui` layer, it would be tied to that specific interface; if another way of interacting with the system were added in the future (for example, a web interface), that logic would have to be duplicated instead of being reused from the service layer.

**4. What changes are needed in Sale and generateReceipt to show the applied discount, and do they break existing behavior?**

Two new private attributes must be added to the `Sale` class: `appliedPromotionName` (String), which stores the name of the promotion applied to that sale, and `discountAmount` (double), which stores the amount of the discount granted. Their corresponding getters and setters must also be added, and the `generateReceipt` method must be modified to include the subtotal, the applied discount (with the promotion's name), and the final total. These changes do not break the existing behavior of the system, since they are purely additive: no existing attribute or method (`date`, `client`, `seller`, `details`, `calculateTotal()`, `confirm()`) is removed or modified; new information is simply added to the class.

**5. Where is the promotion validity check performed — in Promotion, in PromotionService, or in both?**

This validation is performed in both classes, but with different roles. The `Promotion` class implements the `isActive(LocalDate date)` method, which compares its own start and end dates against the given date — this logic only depends on data the promotion itself already holds, following the same encapsulation principle used elsewhere in the system (such as in `Sale.confirm()`). `PromotionService`, on the other hand, implements `listActivePromotions()`, which iterates through the full list of registered promotions and uses each one's `isActive()` method to filter the active ones — this is `PromotionService`'s responsibility because it is the only class with access to the complete collection of promotions in the system.

---

## Requirement 3 — Return Module

**1. Relationship between Return and Sale**

The relationship between the `Return` class and the `Sale` class is a **Directed Association**. A `Return` holds a reference to the specific `Sale` it belongs to, but they maintain independent lifecycles. It is not inheritance because a return is not a type of sale. It is not composition because if a `Return` is deleted, the original `Sale` must still exist in the system's history without being destroyed.

**2. Representing partial returns**

A partial return is managed by storing only the specific items the customer brings back. This is represented in the `Return` class through the attribute `List<Product> returnedProducts`. Instead of referencing the entire list of `SaleDetail` from the original sale, this attribute acts as a subset, storing exactly the product instances that are being refunded and returned to the inventory.

**3. Location of the 30-day validation and Java date mechanisms**

The 30-day business rule validation is executed in the **Service Layer** (`ReturnService.registerReturn`) by invoking a boolean method `canBeReturned()` housed in the `Sale` model class. This placement respects the layered architecture: the model answers *if* it is eligible based on its own state, and the service enforces the *rejection* if the rule fails. To calculate the difference between dates, Java's `java.time` API is used, specifically checking `saleDate.plusDays(30).isBefore(currentDate)` or utilizing `ChronoUnit.DAYS.between(saleDate, currentDate) <= 30`.

**4. Reusing the stock update pattern**

The return process reuses the persistence pattern already established in Workshop 1's `ProductService`, rather than reusing a single pre-existing method identically. In the existing system, methods like `registerVideoGame()`, `registerConsole()`, and `updateStock()` follow a standard flow: they modify the model in memory and immediately call the repository to save the changes (e.g., `repository.save(products)`).

The new `restoreStock(String productId, int quantity)` method will be added to `ProductService` to execute the exact inverse of the existing `reduceStock()` behavior, followed by that same repository save pattern. Centralizing this logic within `ProductService` is critical to prevent code duplication, maintain the Single Responsibility Principle, and avoid file-writing conflicts that would occur if `ReturnService` tried to modify the product CSV directly.

**5. Monthly balance report location and dependencies**

The monthly balance report is located in the `ReturnService` class via the `generateMonthlyBalance(int month, int year)` method. This is coherent with the layered architecture because service classes are responsible for orchestrating cross-domain business logic, aggregations, and calculations.

To generate the net balance (sales minus returns), this method requires `SaleService` (to fetch and sum the total sales for the given month) and `ReturnRepository` (to fetch and sum the total refunded returns for that same period). Furthermore, the `ReturnService` constructor must also inject `ProductService`. Even though the monthly balance calculation does not need to interact with products, dependencies are injected at the class level, and `ReturnService` requires `ProductService` so that its `registerReturn()` method can invoke `restoreStock()`.

---

## Requirement 4 — Warranty Module

**1. How is the shared structure among warranty types reflected in the class hierarchy, and what mechanism allows each type to have its own duration without duplicating code?**

The two warranty types share common attributes and behavior (identifier, associated product, associated sale, start date, end date, and the `isActive()` / `generateWarrantyCertificate()` methods), which are placed in an abstract base class `Warranty`. Each concrete subclass (`BasicWarranty`, `ExtendedWarranty`) inherits this common structure and only differs in the values returned by `getDurationInMonths()`, `getWarrantyType()`, and `getAdditionalCost()`. The mechanism that allows each type to have its own duration without duplicating code is polymorphism, supported by inheritance — the same pattern already used in the system with `Product.describe()` and `Promotion.calculateDiscount()`. Since the end-date calculation is written once in the base class constructor and simply invokes the abstract `getDurationInMonths()` method, the calculation logic itself is never duplicated across subclasses.

**2. In which layer is the console-vs-videogame decision located, and what Java mechanism is used to verify the real type of a product?**

This decision is located in the service layer, specifically inside `SaleService.registerSale`, since it is a business rule about when to trigger automatic warranty creation as part of the sale workflow — not something the `Product` hierarchy or the UI should decide. The Java mechanism used to verify the real type of a product is the `instanceof` operator: `if (product instanceof Console) { ... }`. This works safely because `Console` and `VideoGame` are concrete subclasses of the abstract `Product` class, so each product object retains its real type at runtime, and `SaleService` can check it without modifying the `Product` hierarchy itself.

**3. How is the end date calculated in each subclass, and should this happen in the constructor or in a separate method?**

The end date is calculated in the constructor of the base class `Warranty`, not in a separate method, for two reasons. First, it guarantees the warranty object is never left in an inconsistent state — from the moment any `Warranty` is instantiated, it already has a fully computed, valid end date. Second, by the time the base constructor runs, the concrete subclass being built has already overridden `getDurationInMonths()`, so calling that abstract method from the base constructor correctly resolves — through dynamic dispatch — to the subclass's specific duration (6 or 12 months), without the base class needing to know which subclass it is. Calculating it in a separate method called later would risk the object being used before that method runs, leaving an incorrect or missing end date.

**4. At what point in the sale registration flow is the extended-warranty cost calculated and applied, and what changes are needed in SaleService.registerSale?**

The additional cost is calculated and applied inside `SaleService.registerSale`, after the sale's base subtotal has been calculated. Its signature must be modified to accept an additional parameter indicating which products should receive extended warranty (for example, `List<String> productIdsWithExtendedWarranty`). For each product in the sale: if it is a `Console`, `WarrantyService.assignBasicWarranty(...)` is invoked (no cost impact); if its id appears in the extended-warranty list, `WarrantyService.assignExtendedWarranty(...)` is invoked instead, and the value returned by `getAdditionalCost()` is added to the sale's total. If the parameter is empty or null, no extended warranty is applied and the total is unaffected — this change is purely additive to the existing registration flow.

**5. In which class is the "expiring soon" query located, what dependencies does it need, and why is this location consistent with the layered architecture?**

This method is located in `WarrantyService`, as `listWarrantiesExpiringSoon(int daysAhead)`, not in `Warranty` itself or in the UI. A single `Warranty` instance only knows about itself through `isActive()`; it has no access to the full set of registered warranties, so it cannot answer a "which ones, among all, expire soon" question on its own. `WarrantyService` is the class with access to the complete collection of warranties (through `WarrantyRepository`), which makes it the appropriate place to iterate and filter by end date. This is consistent with the layered architecture because the `ui` layer simply calls `warrantyService.listWarrantiesExpiringSoon(30)` and displays the result, without needing to know how warranties are stored or iterated — the same separation of concerns already applied in `PromotionService.listActivePromotions()`.