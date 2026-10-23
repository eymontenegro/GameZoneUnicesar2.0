# GameZoneUnicesar 🎮

GameZoneUnicesar is a management system that allows a video game store to register and list its products, keep track of its clients and employees, register sales transactions, apply promotional discounts automatically, process product returns, manage product warranties (basic and extended), and enforce strict data validations and custom exception handling.

## 📋 Table of Contents

- Features & Modules
- System Architecture
- Project Structure
- System Requirements
- Installation & Execution
- Data Persistence

## 🚀 Features & Modules

The system is divided into seven core modules:

**Product Module (Product):**
- Management of two product types: Video Games (`VideoGame`) and Consoles (`Console`).
- Registration and listing of the product catalog.
- Stock update functionality, including restoring stock from processed returns.

**Accessory Module (Accessory):**
- Management of three accessory types: Controllers (`Controller`), Cables (`Cable`), and Memories (`Memory`).
- Registration and listing of the accessory inventory, including listing by type.
- Console compatibility tracking for `Controller` and `Memory` (implemented through the `ConsoleCompatible` interface); `Cable` does not track compatibility.
- Query of accessories compatible with a specific console.
- Accessories can be sold together with video games and consoles in the same sale transaction.

**Person Module (Person):**
- Management of Clients (`Client`), with email and purchase history.
- Management of Sellers (`Seller`), preloaded in the system with employee code and shift.

**Sales Module (Sale):**
- Transaction registration linking a client, a seller, and the products and/or accessories sold.
- Automatic calculation of the sale total.
- Coordination with the Product and Accessory modules to validate and update stock.
- Automatic application of the best available promotion (if any) at the moment of registering the sale.
- Sales remain eligible for returns within a 30-calendar-day window after the sale date.

**Promotion Module (Promotion):**
- Management of three promotion types: Percentage Discounts (`PercentageDiscount`), Category Discounts (`CategoryDiscount`), and Bulk Purchase Discounts (`BulkPurchaseDiscount`).
- Each promotion has a validity period (start and end date); only currently active promotions are considered.
- When a sale is registered, the system evaluates all active promotions and automatically applies the one offering the highest monetary discount. Promotions are not cumulative — only one is applied per sale.
- The sale receipt reflects the subtotal, the applied promotion's name (if any), the discount amount, and the final total.
- Registration and listing of promotions, including listing only the currently active ones.

**Return Module (Return):**
- Registration of product returns referencing an original sale; a return can include some or all of the products from that sale.
- Returns can only be registered within 30 calendar days of the original sale date; returned products must belong to the referenced sale.
- Automatic refund amount calculation (sum of the returned products' prices) and automatic stock restoration for the returned products.
- Query of all returns, returns by customer, and returns by sale.
- Monthly balance report showing total sales, total returns, and the net balance for a given month and year.

**Warranty Module (Warranty):**
- Management of two warranty types: Basic Warranty (`BasicWarranty`) and Extended Warranty (`ExtendedWarranty`).
- Basic warranty covers factory defects only, lasts 6 months from the sale date, and is generated automatically at no extra cost whenever a `Console` is sold (video games do not receive a warranty).
- Extended warranty covers factory defects and accidental damage, lasts 12 months from the sale date, and can be optionally requested by the seller at the time of sale for an additional cost of 10% of the product's price, which is added to the sale total.
- Each warranty has a start date (the sale date) and an automatically calculated end date, based on its duration.
- Query of the warranty associated with a specific product within a specific sale, listing of all currently active warranties, and listing of warranties expiring soon (e.g., within the next 30 days).
- Integration with the Sales module: registering a sale automatically generates the basic warranties for the consoles included, and adds the additional cost of any requested extended warranties to the sale total.

**Validations & Exceptions Module (Validations & Exceptions):**
- Custom unchecked exception hierarchy rooted at `GameZoneException` (extending `RuntimeException`), containing specialized subclasses: `ResourceNotFoundException`, `BusinessRuleException`, `InvalidDataException`, and `PersistenceException`.
- Dedicated validation components (`ProductValidator`, `PersonValidator`, `SaleValidator`) located in `com.gamezone.validation` and invoked at the entry point of the service layer to inspect incoming data and business preconditions.
- Robust exception handling in `ConsoleMenu` using a single `try` block with cascaded `catch` blocks ordered from specific exception types to the general `GameZoneException` safety net, ensuring clean error propagation and user-friendly feedback in Spanish.

## 🏗️ System Architecture

The project implements an Object-Oriented 5-Layer Architecture:

**Layer 1: Domain/Model** — Contains core entities, inheritance hierarchies (`Product`, `Person`, `Promotion`, `Warranty`), and the `ConsoleCompatible` interface.

**Layer 2: Validation** — Stateless utility validators (`ProductValidator`, `PersonValidator`, `SaleValidator`) responsible for verifying input data integrity, formatting, and parameters at the service boundary without accessing persistence.

**Layer 3: Persistence** — Repositories handling reading and writing to CSV files, wrapping low-level `IOException` inside `PersistenceException`.

**Layer 4: Service** — Business logic layer orchestrating workflows, validations, automatic inventory updates, promotions, returns, and warranties.

**Layer 5: UI** — Console-based menu for user interaction with cascaded exception handling for precise error feedback.

## 📁 Project Structure

```
GameZoneUnicesar/
│
├── data/                         # Persistence files (.csv)
│   ├── promotions.csv
│   ├── sellers.csv
│   └── accessories.csv
│   
│
├── src/main/java/com/gamezone/
│   ├── model/                    # Domain layer
│   │   ├── Person.java
│   │   ├── Client.java
│   │   ├── Seller.java
│   │   ├── Product.java
│   │   ├── VideoGame.java
│   │   ├── Console.java
│   │   ├── Accessory.java
│   │   ├── Controller.java
│   │   ├── Cable.java
│   │   ├── Memory.java
│   │   ├── ProductCategory.java
│   │   ├── ConsoleCompatible.java
│   │   ├── Promotion.java
│   │   ├── StockUpdater.java
│   │   ├── CompatibleAccessory.java
│   │   ├── PercentageBasedPromotion.java
│   │   ├── PercentageDiscount.java
│   │   ├── CategoryDiscount.java
│   │   ├── BulkPurchaseDiscount.java
│   │   ├── Sale.java
│   │   ├── SaleDetail.java
│   │   ├── Return.java
│   │   ├── Warranty.java
│   │   ├── BasicWarranty.java
│   │   └── ExtendedWarranty.java
│   │
│   ├── exceptions/               # Exceptions layer 
│   │   ├── GameZoneException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── BusinessRuleException.java
│   │   ├── InvalidDataException.java
│   │   └── PersistenceException.java
│   │
│   ├── validation/               # Validation layer 
│   │   ├── PromotionValidator.java
│   │   ├── ProductValidator.java
│   │   ├── PersonValidator.java
│   │   ├── AccessoryValidator.java
│   │   ├── WarrantyValidator.java
│   │   └── SaleValidator.java
│   │
│   ├── persistence/              # Persistence layer
│   │   ├── PersonRepository.java
│   │   ├── ProductRepository.java
│   │   ├── AccessoryStorage.java
│   │   ├── PromotionRepository.java
│   │   ├── ConsoleCsvMapper.java
│   │   ├── VideoGameCsvMapper.java
│   │   ├── ProductCsvMapper.java
│   │   ├── PersonStorage.java
│   │   ├── ProductStorage.java
│   │   ├── PromotionStorage.java
│   │   ├── ReturnStorage.java
│   │   ├── SaleStorage.java
│   │   ├── AccessoryRepository.java
│   │   ├── SaleRepository.java
│   │   ├── ReturnRepository.java
│   │   ├── WarrantyRepository.java
│   │   └── CsvSupport.java           # Shared CSV validation helper
│   │
│   ├── service/                  # Business logic layer
│   │   ├── PersonService.java
│   │   ├── MonthlyBalanceService.java
│   │   ├── ProductService.java
│   │   ├── AccessoryService.java
│   │   ├── PromotionService.java
│   │   ├── SaleService.java
│   │   ├── ReturnService.java
│   │   └── WarrantyService.java
│   │
│   ├── ui/                       # Presentation layer
│   │   ├── Menu.java
│   │   ├── AccessorySubMenu.java
│   │   ├── PersonSubMenu.java
│   │   ├── PromotionSubMenu.java
│   │   ├── ProductSubMenu.java
│   │   ├── SaleSubMenu.java
│   │   ├── SaleReceiptFormatter.java
│   │   ├── ReturnSubMenu.java
│   │   ├── WarrantySubMenu.java
│   │   ├── WarrantyCertificateFormatter.java
│   │   └── ConsoleInput.java
│   │
│   └── Main.java                 # Entry point
│
├── src/test/java/com/gamezone/persistence/   # Repository tests
│   ├── ProductRepositoryTest.java
│   ├── AccessoryRepositoryTest.java
│   ├── PromotionRepositoryTest.java
│   ├── SaleRepositoryTest.java
│   ├── ReturnRepositoryTest.java
│   ├── WarrantyRepositoryTest.java
│   ├── CsvTestSupport.java
│   └── PersonRepositoryTest.java      
│
├── docs/                         # Analysis and design documentation
│   ├── analysis.md
│   ├── class-diagram.md
│   ├── hierarchy-diagram.md
│   ├── layers-diagram.md
│   ├── grasp-class-diagram.md
│   ├── grasp-hierarchy-diagram.md
│   ├── grasp-layers-diagram.md
│   ├── ai-usage/
│   │   ├── developer1-ai-log.md
│   │   ├── developer2-ai-log.md
│   │   ├── developer3-ai-log.md
│   │   └── leader-ai-log.md
│   │ 
│   └── grasp-audit.md
│       ├── developer1-audit.md
│       ├── leader-audit.md
│       ├── consolidated-audit.md
│       ├── developer2-audit.md
│       └── developer3-audit.md
│
│
├── TEAM.md
└── README.md
```

## 💻 System Requirements

- JDK: Java Development Kit 17.
- Recommended IDE: Apache NetBeans.
- Version Control: Git and GitHub.

## 🛠️ Installation & Execution

**1. Clone the repository**
```
git clone https://github.com/eymontenegro/GameZoneUnicesar2.0.git
```

**2. Open the project in NetBeans**
- Open Apache NetBeans.
- Select File → Open Project.
- Select the cloned `GameZoneUnicesar` folder.

**3. Build the project**
- Right-click the project → Build (or press F11).

**4. Run the application**
- Press Shift + F6 to run the main project, or right-click the project → Run.
- The application starts through the `Main` class.

## 💾 Data Persistence

The application does not require an external relational database. All information is stored using comma-separated (`,`) CSV files located in the `/data` folder.

## 👨‍💻 Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Java I/O
- Flat-File (CSV) Persistence
- Git and GitHub
- Apache NetBeans

## 📌 Project Purpose

GameZoneUnicesar was developed as an academic software project for Programación III at Universidad Popular del Cesar, focused on applying Object-Oriented Programming, layered architecture, inheritance, custom exceptions, validation design, data persistence, and collaborative software engineering in a video game store management scenario.
