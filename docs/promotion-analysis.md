# Object-Oriented Analysis — Promotion Module

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
