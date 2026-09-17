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
