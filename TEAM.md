# TEAM.md — GameZoneUnicesar

## Team Name
Developers

## Team Members

| Full Name | UPC Username | Assigned Role | Assigned Module |
|---|---|---|---|
| Estefany Montenegro | eymontenegro | Technical Leader | Sales + Integration |
| Yanelis González | ypatriciagonzalez | Developer 1 | Products |
| Andrea Laurens | alaurens | Developer 2 | People |

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

### Developer 2 — People
- Person (abstract)
- Client
- Seller
- PersonRepository
- PersonService

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
9. Implement the `Menu` class (user interface).
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

## Feature Branches

| Team Member | Branch Name |
|---|---|
| Technical Leader | `feature/sale-module` |
| Developer 1 | `feature/product-module` |
| Developer 2 | `feature/person-module` |
