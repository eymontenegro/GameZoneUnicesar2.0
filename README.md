# GameZoneUnicesar 🎮

GameZoneUnicesar is a management system that allows a video game store to register and list its products, and keep track of its clients and employees, as well as register sales transactions.

## 📋 Table of Contents

- Features & Modules
- System Architecture
- Project Structure
- System Requirements
- Installation & Execution
- Data Persistence

## 🚀 Features & Modules

The system is divided into three core modules:

**Product Module (Product):**
- Management of two product types: Video Games (`VideoGame`) and Consoles (`Console`).
- Registration and listing of the product catalog.
- Stock update functionality.

**Person Module (Person):**
- Management of Clients (`Client`), with email and purchase history.
- Management of Sellers (`Seller`), preloaded in the system with employee code and shift.

**Sales Module (Sale):**
- Transaction registration linking a client, a seller, and the products sold.
- Automatic calculation of the sale total.
- Coordination with the Product module to validate and update stock.

## 🏗️ System Architecture

The project implements an Object-Oriented 4-Layer Architecture:

**Layer 1: Domain/Model** — Contains the core entities and their inheritance relationships (`Product` → `VideoGame`/`Console`, `Person` → `Client`/`Seller`).

**Layer 2: Persistence** — Repositories handling reading and writing to CSV files.

**Layer 3: Service** — Business logic layer, processing rule validations and coordinating between modules (e.g., stock reduction during a sale).

**Layer 4: UI** — Console-based menu for user interaction.

## 📁 Project Structure

```
GameZoneUnicesar/
│
├── data/                          # Persistence files (.csv)
│   ├── products.csv
│   ├── clients.csv
│   ├── sellers.csv
│   ├── sales.csv
│   └── sale_details.csv
│
├── src/main/java/com/gamezone/
│   ├── model/                     # Domain layer
│   │   ├── Person.java
│   │   ├── Client.java
│   │   ├── Seller.java
│   │   ├── Product.java
│   │   ├── VideoGame.java
│   │   ├── Console.java
│   │   ├── Sale.java
│   │   └── SaleDetail.java
│   │
│   ├── persistence/                # Persistence layer
│   │   ├── PersonRepository.java
│   │   ├── ProductRepository.java
│   │   └── SaleRepository.java
│   │
│   ├── service/                    # Business logic layer
│   │   ├── PersonService.java
│   │   ├── ProductService.java
│   │   └── SaleService.java
│   │
│   ├── ui/                         # Presentation layer
│   │   └── (menu classes)
│   │
│   └── Main.java                   # Entry point
│
├── docs/                           # Analysis and design documentation
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

**clients.csv** — stores client information (name, identification, phone, email).

**sellers.csv** — stores seller information (name, identification, phone, employee code, shift).

**sales.csv / sale_details.csv** — store sales transactions, linking the sale header with its line-item details.

## 👨‍💻 Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Java I/O
- Flat-File (CSV) Persistence
- Git and GitHub
- Apache NetBeans

## 📌 Project Purpose

GameZoneUnicesar was developed as an academic software project for Programación III at Universidad Popular del Cesar, focused on applying Object-Oriented Programming, layered architecture, inheritance, encapsulation, business logic, and data persistence in a video game store management scenario.
