# AI Usage Log — Developer 1 (Products Module)

## Object-Oriented Programming concepts

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

## English identifier naming suggestions

**2026-09-05**
**Query:** English translation of the class/attribute/method names that appeared in Spanish in the team's diagrams (Producto → Product, Videojuego → VideoGame, etc.).
**Use type:** English identifier naming suggestions
**How I used it:** Applied these translated names directly to my classes (`Product`, `VideoGame`, `Console`) to stay consistent with the team's translated diagrams.

## Tooling and environment (IntelliJ / NetBeans)

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

## Git and GitHub

**2026-09-06**
**Query:** Basic Git commands (`git checkout -b`, `git branch -m`, `git push --set-upstream`) and why Git doesn't track empty folders.
**Use type:** Git command explanation
**How I used it:** Created and renamed my `feature/product-module` branch correctly, and understood why my teammates' empty folders weren't showing up until they had files inside.

**2026-09-07**
**Query:** How to handle a direct instruction from the professor (do not delete feature branches) that contradicts what the workshop document says.
**Use type:** Conceptual / prioritization criteria
**How I used it:** Confirmed that the professor's instruction takes precedence over the written document, and stopped deleting my branches after merging.

## Review of my own code

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

## Persistence layer design

**2026-09-08**
**Query:** Comparison of the persistence formats available in Java (plain text, CSV, Java serialization, JSON), with their advantages and disadvantages.
**Use type:** How a specific Java functionality is implemented
**How I used it:** Decided to use CSV for `ProductRepository`, prioritizing a human-readable file and understanding the read/write process over the simplicity of automatic serialization.

**2026-09-08**
**Query:** What to do if saving or loading a file fails, since the workshop doesn't specify anything about it; difference between checked and unchecked exceptions in Java.
**Use type:** Conceptual (Java error handling) + my own design decision
**How I used it:** Decided to catch `IOException` (checked) and rethrow it as a `RuntimeException` (unchecked) to avoid propagating unnecessary `throws` declarations across layers, leaving the responsibility of informing the user to an upper layer.

## JavaDoc

**2026-09-09**
**Query:** Format and purpose of JavaDoc comments (`/** ... */`, `@param`, `@return`, `@throws`).
**Use type:** How a standard Java convention is implemented
**How I used it:** Applied JavaDoc to all 5 classes in my module (`Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductService`) before the final submission.
