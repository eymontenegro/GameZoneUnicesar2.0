# Return Module Analysis - Requirement 3

### 1. Relationship Between Return and Sale
The relationship between the `Return` class and the `Sale` class is a **Directed Association**. A `Return` holds a reference to the specific `Sale` it belongs to, but they maintain independent lifecycles. It is not inheritance because a return is not a type of sale. It is not composition because if a `Return` is deleted, the original `Sale` must still exist in the system's history without being destroyed.

### 2. Representing Partial Returns
A partial return is managed by storing only the specific items the customer brings back. This is represented in the `Return` class through the attribute `List<Product> returnedProducts`. Instead of referencing the entire list of `SaleDetail` from the original sale, this attribute acts as a subset, storing exactly the product instances that are being refunded and returned to the inventory.

### 3. Location of the 30-Day Validation and Java Date Mechanisms
The 30-day business rule validation is executed in the **Service Layer** (`ReturnService.registerReturn`) by invoking a boolean method `canBeReturned()` housed in the `Sale` model class. This placement respects the layered architecture: the model answers *if* it is eligible based on its own state, and the service enforces the *rejection* if the rule fails. To calculate the difference between dates, Java's `java.time` API is used, specifically checking `saleDate.plusDays(30).isBefore(currentDate)` or utilizing `ChronoUnit.DAYS.between(saleDate, currentDate) <= 30`.

### 4. Reusing the Stock Update Pattern
The return process reuses the persistence pattern already established in Workshop 1's `ProductService`, rather than reusing a single pre-existing method identically. In the existing system, methods like `registerVideoGame()`, `registerConsole()`, and `updateStock()` follow a standard flow: they modify the model in memory and immediately call the repository to save the changes (e.g., `repository.save(products)`).

The new `restoreStock(String productId, int quantity)` method will be added to `ProductService` to execute the exact inverse of the existing `reduceStock()` behavior, followed by that same repository save pattern. Centralizing this logic within `ProductService` is critical to prevent code duplication, maintain the Single Responsibility Principle, and avoid file-writing conflicts that would occur if `ReturnService` tried to modify the product CSV directly.

### 5. Monthly Balance Report Location and Dependencies
The monthly balance report is located in the `ReturnService` class via the `generateMonthlyBalance(int month, int year)` method. This is coherent with the layered architecture because service classes are responsible for orchestrating cross-domain business logic, aggregations, and calculations.

To generate the net balance (sales minus returns), this method requires `SaleService` (to fetch and sum the total sales for the given month) and `ReturnRepository` (to fetch and sum the total refunded returns for that same period). Furthermore, the `ReturnService` constructor must also inject `ProductService`. Even though the monthly balance calculation does not need to interact with products, dependencies are injected at the class level, and `ReturnService` requires `ProductService` so that its `registerReturn()` method can invoke `restoreStock()`.
