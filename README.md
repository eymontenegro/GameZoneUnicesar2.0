# GameZoneUnicesar 🎮

GameZoneUnicesar is a management system that allows a video game store to register and list its products, keep track of its clients and employees, register sales transactions, and apply promotional discounts automatically.

## 📋 Table of Contents

- Features & Modules
- System Architecture
- Project Structure
- System Requirements
- Installation & Execution
- Data Persistence

## 🚀 Features & Modules

The system is divided into five core modules:

**Product Module (Product):**
- Management of two product types: Video Games (`VideoGame`) and Consoles (`Console`).
- Registration and listing of the product catalog.
- Stock update functionality.

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

**Promotion Module (Promotion):**
- Management of three promotion types: Percentage Discounts (`PercentageDiscount`), Category Discounts (`CategoryDiscount`), and Bulk Purchase Discounts (`BulkPurchaseDiscount`).
- Each promotion has a validity period (start and end date); only currently active promotions are considered.
- When a sale is registered, the system evaluates all active promotions and automatically applies the one offering the highest monetary discount. Promotions are not cumulative — only one is applied per sale.
- The sale receipt reflects the subtotal, the applied promotion's name (if any), the discount amount, and the final total.
- Registration and listing of promotions, including listing only the currently active ones.

## 🏗️ System Architecture

The project implements an Object-Oriented 4-Layer Architecture:

**Layer 1: Domain/Model** — Contains the core entities and their inheritance relationships (`Product` → `VideoGame`/`Console`/`Accessory`, `Accessory` → `Controller`/`Cable`/`Memory`, `Person` → `Client`/`Seller`, `Promotion` → `PercentageDiscount`/`CategoryDiscount`/`BulkPurchaseDiscount`). Compatibility with consoles is modeled through the `ConsoleCompatible` interface, implemented by `Controller` and `Memory`.

**Layer 2: Persistence** — Repositories handling reading and writing to CSV files, including `AccessoryRepository` for the accessory module and `PromotionRepository` for the promotion module.

**Layer 3: Service** — Business logic layer, processing rule validations and coordinating between modules (e.g., stock reduction during a sale, delegated to `ProductService` or `AccessoryService` depending on the item type sold; best-promotion selection delegated to `PromotionService`).

**Layer 4: UI** — Console-based menu for user interaction, including the "Accessory Management" and "Promotion Management" submenus.

## 📁 Project Structure

```
GameZoneUnicesar/
│
├── data/                          # Persistence files (.csv)
│   ├── products.csv
│   ├── accessories.csv
│   ├── clients.csv
│   ├── sellers.csv
│   ├── sales.csv
│   ├── sale_details.csv
│   └── promotions.csv
│
├── src/main/java/com/gamezone/
│   ├── model/                     # Domain layer
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
│   │   ├── ConsoleCompatible.java
│   │   ├── Promotion.java
│   │   ├── PercentageDiscount.java
│   │   ├── CategoryDiscount.java
│   │   ├── BulkPurchaseDiscount.java
│   │   ├── Sale.java
│   │   └── SaleDetail.java
│   │
│   ├── persistence/                # Persistence layer
│   │   ├── PersonRepository.java
│   │   ├── ProductRepository.java
│   │   ├── AccessoryRepository.java
│   │   ├── PromotionRepository.java
│   │   └── SaleRepository.java
│   │
│   ├── service/                    # Business logic layer
│   │   ├── PersonService.java
│   │   ├── ProductService.java
│   │   ├── AccessoryService.java
│   │   ├── PromotionService.java
│   │   └── SaleService.java
│   │
│   ├── ui/                         # Presentation layer
│   │   └── (menu classes)
│   │
│   └── Main.java                   # Entry point
│
├── docs/                           # Analysis and design documentation
│   ├── accessory-analysis.md
│   ├── accessory-class-diagram.md
│   ├── promotion-analysis.md
│   └── promotion-class-diagram.md
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
git clone https://github.com/eymontenegro/GameZoneUnicesar.git
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

**products.csv** — stores product information using the structure:
```
TYPE,ID,TITLE,PRICE,STOCK,EXTRA_PARAM_1,EXTRA_PARAM_2,EXTRA_PARAM_3
```

**accessories.csv** — stores accessory information (controllers, cables, and memories), including type-specific attributes and, for `Controller` and `Memory`, the list of compatible console IDs. Preloaded with at least one accessory of each type.

**clients.csv** — stores client information (name, identification, phone, email).

**sellers.csv** — stores seller information (name, identification, phone, employee code, shift).

**sales.csv / sale_details.csv** — store sales transactions, linking the sale header with its line-item details. A line item can reference either a `Product` or an `Accessory`, since both extend the same base class.

**promotions.csv** — stores promotion information (percentage, category, and bulk purchase discounts), including a type discriminator and each type's specific attributes, plus the validity period (start and end date). Preloaded with at least one promotion of each type.

## 👨‍💻 Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Java I/O
- Flat-File (CSV) Persistence
- Git and GitHub
- Apache NetBeans

## 📌 Project Purpose

GameZoneUnicesar was developed as an academic software project for Programación III at Universidad Popular del Cesar, focused on applying Object-Oriented Programming, layered architecture, inheritance, encapsulation, business logic, and data persistence in a video game store management scenario.
