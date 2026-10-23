# TEAM.md — GameZoneUnicesar

## Team Name
Developers

## Team Members

|      Full Name      |    UPC Username   |   Assigned Role  |         Assigned Module         |
|---------------------|-------------------|------------------|---------------------------------|
| Estefany Montenegro | eymontenegro      | Technical Leader | Sales + Integration             |
| Yanelis González    | ypatriciagonzalez | Developer 1      | Products                        |
| Andrea Laurens      | alaurens          | Developer 2      | Person                          |
| Xavi Acosta         | xacosta           | Developer 3      | Persistence refactor + Testing  |

> Developer 3 joined the team at Requirement 6 (with the instructor's permission), so this member only appears in that requirement.

## Class Distribution

### Technical Leader — Sales + Integration
- Sale
- SaleDetail
- SaleRepository
- SaleService
- Menu
- SubMenu
- Main

### Developer 1 — Products
- Product (abstract)
- VideoGame
- Console
- ProductRepository
- ProductService
- Accessory (abstract)
- Controller
- Cable 
- Memory
- Promotion (abstract)
- PercentageDiscount
- CategoryDiscount
- BulkPurchaseDiscount
- Return
- Warranty (abstract)
- BasicWarranty
- ExtendedWarranty
- GameZoneException (abstract)
- ResourceNotFoundException
- BusinessRuleException
- InvalidDataException
- PersistenceException
- ProductValidator
- PersonValidator


### Developer 2 — People
- Person (abstract)
- Client
- Seller
- PersonRepository
- PersonService
- AccessoryRepository
- AccessoryService
- PromotionRepository
- PromotionService
- ReturnRepository
- ReturnService
- WarrantyRepository
- WarrantyService
- SaleValidator
- AccessoryValidator
- PromotionValidator
- WarrantyValidator
- ReturnValidator


### Developer 3 — Persistence refactor and testing (joined at Requirement 6)
- `CsvSupport`
- Repository test classes in `src/test/java/com/gamezone/persistence`
- Requirement 6 refactor of all repositories (see Requirement 6)

Test classes (one per repository):
- `AccessoryRepositoryTest`
- `PersonRepositoryTest`
- `ProductRepositoryTest`
- `PromotionRepositoryTest`
- `ReturnRepositoryTest`
- `SaleRepositoryTest`
- `WarrantyRepositoryTest`

## Committed Activities per Member

### Technical Leader
1. Create the project repository on GitHub with initial setup (README, .gitignore, license).
2. Configure the project branches (`main` and `develop`) and enable protection on the main branches.
3. Configure the Maven project with the initial `pom.xml` and the four-layer package structure.
4. Prepare the `TEAM.md` file with team information, assigned roles, and class distribution.
5. Implement the Sale domain class with its attributes, constructor, and basic methods.
6. Implement the sale total calculation method.
7. Implement the Sale module's persistence class.
8. Implement the Sale module's service class.
9. Implement the `Menu` and `SubMenu` classes (user interface).
10. Implement the main class (`Main`) that starts the system.

### Developer 1
1. Create the feature branch for the products module.
2. Implement the abstract base class of the product hierarchy with its common attributes, constructor, and common methods.
3. Declare the abstract description method that derived classes must implement.
4. Implement the first derived class (video games) with its particular attributes and the implementation of the description method.
5. Implement the second derived class (consoles) with its particular attributes and the implementation of the description method.
6. Implement the products module's persistence class with save and load methods from files.
7. Implement the products module's service class with registration, listing, and stock update methods.
8. Document all module classes with JavaDoc in English.
9. Request Pull Requests to the Technical Leader for module integration.

### Developer 2
1. Create the feature branch for the people module.
2. Implement the abstract base class of the people hierarchy with its common attributes, constructor, and common methods.
3. Declare the abstract or business method that derived classes must implement according to the analysis performed.
4. Implement the first derived class (clients) with its particular attributes.
5. Implement the second derived class (sellers) with its particular attributes.
6. Implement the people module's persistence class with save and load methods from files.
7. Implement the people module's service class with the module's business rules.
8. Document all module classes with JavaDoc in English.
9. Request Pull Requests to the Technical Leader for module integration.

## Responsibilities per Requirement

In every requirement, the Technical Leader also reviews and approves the Pull Requests of the other members. In Requirements 1 to 4 the Technical Leader additionally updates `README.md` with the new functionalities.

### Requirement 1 — Accessory Module

**Developer 1** — Accessory hierarchy (model layer)
- `Accessory` (abstract)
- `Controller`
- `Cable`
- `Memory`

**Developer 2** — Persistence and service
- `AccessoryRepository`
- `AccessoryService`

**Technical Leader** — Module integration
- Additive changes to `SaleService`.
- Extension of `Menu` for the new accessory operations.
- Integration coordination, `README.md` update, and Pull Request review.

### Requirement 2 — Promotion Module

**Developer 1** — Promotion hierarchy (model layer)
- `Promotion` (abstract)
- `PercentageDiscount`
- `CategoryDiscount`
- `BulkPurchaseDiscount`

**Developer 2** — Persistence and service
- `PromotionRepository`
- `PromotionService`, including the logic that selects the best applicable promotion.

**Technical Leader** — Module integration
- Additive changes to `SaleService.registerSale` to apply the discount.
- Modification of `Sale` to store the applied discount.
- Update of `generateReceipt` to show the discount breakdown.
- Extension of `Menu` to manage promotions.
- Integration coordination, `README.md` update, and Pull Request review.

### Requirement 3 — Return Module

**Developer 1** — Return domain class (model layer)
- `Return`, with its attributes, constructor, calculation methods, and internal validations.
- Additive method `canBeReturned` in `Sale`.

**Developer 2** — Persistence and service
- `ReturnRepository`
- `ReturnService`, including return registration with deadline validation, ownership validation, and stock update.
- Monthly balance report.

**Technical Leader** — Module integration
- Additive extension of `ProductService` with the `restoreStock` method.
- Extension of `Menu` for return operations and the monthly balance query.
- Integration coordination, `README.md` update, and Pull Request review.

### Requirement 4 — Warranty Module

**Developer 1** — Warranty hierarchy (model layer)
- `Warranty` (abstract)
- `BasicWarranty`
- `ExtendedWarranty`
- Their duration rules and cost calculation.

**Developer 2** — Persistence and service
- `WarrantyRepository`
- `WarrantyService`, including warranty assignment, validity check, and the query for warranties about to expire.

**Technical Leader** — Module integration
- Additive changes to `SaleService.registerSale` to generate automatic basic warranties and apply the cost of extended warranties.
- Extension of `Menu` to manage and query warranties.
- Integration coordination, `README.md` update, and Pull Request review.

### Requirement 5 — Integration

Requirement 5 consisted only of integrating Requirements 1 to 4. The team had already merged those four requirements before Requirement 5 was published, so it produced no new code and has no assigned classes.

### Requirement 6 — Validations and Exceptions Module

**Developer 1** — Exceptions layer and base validators
- Responsibility: build the basic error-handling infrastructure and the syntactic/structural validation.
- Deliverables:
  - Create the package `com.gamezone.exceptions` with the abstract class `GameZoneException` and its four subclasses: `ResourceNotFoundException`, `BusinessRuleException`, `InvalidDataException`, and `PersistenceException`.
  - Create the package `com.gamezone.validation` and implement the initial validators: `ProductValidator` and `PersonValidator`.

**Developer 2** — Service refactor (business layer)
- Responsibility: integrate business rules and validations into the service layer.
- Deliverables:
  - Implement `SaleValidator` in `com.gamezone.validation` (the only validator not written by Developer 1).
  - Refactor the whole service layer in `com.gamezone.service` (`ProductService`, `PersonService`, `SaleService`, `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService`) so their initial checks are delegated to the new validators.
  - Replace the generic errors thrown in the service layer with the typed exceptions of the system (`BusinessRuleException`, `ResourceNotFoundException`, `InvalidDataException`).

**Developer 3** — Repository refactor (persistence layer) and testing
- Responsibility: isolate persistence and ensure the technical stability of the system.
- Deliverables:
  - Refactor the whole repository layer in `com.gamezone.persistence` (`ProductRepository`, `PersonRepository`, `SaleRepository`, `AccessoryRepository`, `PromotionRepository`, `ReturnRepository`, `WarrantyRepository`) so any low-level `IOException` is caught and rethrown wrapped in a `PersistenceException` with descriptive context.
  - Create `CsvSupport`, the helper shared by the repositories to validate values and report corrupt lines.
  - Design and run the unit/integration test suite, located in `src/test/java/com/gamezone/persistence` with one test class per repository, to verify that the system responds correctly to invalid data without crashing.

**Technical Leader** — User interface, documentation, and integration
- Responsibility: Git management, console interface refactor, and quality control.
- Deliverables:
  - Refactor `Menu` (ui) using a single `try` block with multiple `catch` blocks ordered from the most specific type (`ResourceNotFoundException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException`) to `GameZoneException`, showing clear messages to the user.
  - Coordinate the integration of the work branches by reviewing and approving the Pull Requests of the three developers.
  - Update `README.md` and the general analysis document (`docs/analysis.md`), describing the new architecture of the exceptions and validations module.

## Classes with Mixed Authorship

Some classes received contributions from more than one member. This table records who wrote what, so each member audits and corrects only the code they developed (Requirement 7).

| Class                  | Original author   | Later contributions by other members                                                                 |
|------------------------|-------------------|------------------------------------------------------------------------------------------------------|
| `Sale`                 | Technical Leader  | Developer 1: `canBeReturned` (Req. 3). Technical Leader: applied discount and `generateReceipt` breakdown (Req. 2). |
| `SaleRepository`       | Technical Leader  | Developer 3: `PersistenceException` wrapping and `CsvSupport` usage (Req. 6).                        |
| `SaleService`          | Technical Leader  | Technical Leader: integration of accessories, discount, and warranties in `registerSale` (Req. 1, 2, 4). Developer 2: delegation to validators and typed exceptions (Req. 6). |
| `ProductService`       | Developer 1       | Technical Leader: `restoreStock` (Req. 3). Developer 2: delegation to validators and typed exceptions (Req. 6). |
| `Menu` / `SubMenu`     | Technical Leader  | Technical Leader: extensions for Req. 1 to 4 and the `try`/multi-`catch` handling (Req. 6).          |
| Other repositories     | Developer 1 / 2   | Developer 3: `PersistenceException` wrapping and `CsvSupport` usage (Req. 6).                        |
| Other services         | Developer 2       | Developer 2: delegation to validators and typed exceptions (Req. 6).                                 |

## Branches

### Long-lived branches

|    Branch      |                          Purpose                                    |
|----------------|---------------------------------------------------------------------|
| `main`         | Stable, protected branch.                                           |
| `develop`      | Integration branch where all feature branches are merged.           |

### Feature branches

The branches from the first workshop were reused in the following requirements. Each requirement-specific branch was shared by the members who worked on that requirement.

|           Branch              |   Used by                                  |                  Purpose                                       |
|-------------------------------|--------------------------------------------|----------------------------------------------------------------|
| `feature/sale-module`         | Technical Leader                           | Sales module (Workshop 1).                                     |
| `feature/product-module`      | Developer 1                                | Products module (Workshop 1).                                  |
| `feature/person-module`       | Developer 2                                | People module (Workshop 1).                                    |
| `feature/accessory-module`    | Technical Leader, Developer 1, Developer 2 | Requirement 1 — Accessory module.                              |
| `feature/promotion-module`    | Technical Leader, Developer 1, Developer 2 | Requirement 2 — Promotion module.                              |
| `feature/return-module`       | Technical Leader, Developer 1, Developer 2 | Requirement 3 — Return module.                                 |
| `feature/warranty-module`     | Technical Leader, Developer 1, Developer 2 | Requirement 4 — Warranty module.                               |
| `feature/exception-handling`  | All four members                           | Requirement 6 — Exceptions and validations.                    |
| `feature/grasp-audit`         | All four members                           | Requirement 7, Phase 1 — GRASP audit reports (planned).        |

### Support branches

|           Branch              |                          Purpose                                                          |
|-------------------------------|-------------------------------------------------------------------------------------------|
| `chore/project-setup`         | Repository creation and initial project configuration (`pom.xml`, package structure).     |
| `docs/analysis-diagrams`      | Analysis documents and diagrams requested by each requirement.                            |
| `docs/team-file`              | Updates to this `TEAM.md` file.                                                           |
| `wip/shared-changes`          | Shared drop box where the Technical Leader uploaded changes to several classes so the other members could pull them and commit their own parts. |

> The `feature/grasp-refactor` branch will be created from `develop` in Phase 2 of Requirement 7, once the consolidated audit is merged.
