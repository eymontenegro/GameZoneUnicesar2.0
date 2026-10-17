classDiagram
    direction TB

    %% ===== 1. PRODUCTS & ACCESSORIES MODULE =====
    class Product {
        <<abstract>>
    }
    class VideoGame
    class Console
    class Accessory {
        <<abstract>>
    }
    class Controller
    class Cable
    class Memory
    class ConsoleCompatible {
        <<interface>>
    }
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    ConsoleCompatible <|.. Controller
    ConsoleCompatible <|.. Memory

    %% ===== 2. PEOPLE MODULE =====
    class Person {
        <<abstract>>
    }
    class Client
    class Seller
    Person <|-- Client
    Person <|-- Seller

    %% ===== 3. SALES & DETAILS MODULE =====
    class Sale
    class SaleDetail
    Sale "1" *-- "1..*" SaleDetail
    Product "1" --> "0..*" SaleDetail
    Client "1" --> "0..*" Sale
    Seller "1" --> "0..*" Sale

    %% ===== 4. PROMOTIONS MODULE =====
    class Promotion {
        <<abstract>>
    }
    class PercentageDiscount
    class CategoryDiscount
    class BulkPurchaseDiscount
    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount
    Sale "1" --> "0..1" Promotion : applied promotion

    %% ===== 5. WARRANTIES MODULE =====
    class Warranty {
        <<abstract>>
    }
    class BasicWarranty
    class ExtendedWarranty
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty
    Warranty --> Product : covers
    Warranty --> Sale : associated with

    %% ===== 6. RETURNS MODULE =====
    class Return
    Return "1" --> "1" Sale : references
    Return "1" --> "1..*" Product : returns

    %% ===== 7. EXCEPTIONS LAYER (Requirement 6) =====
    class RuntimeException
    class GameZoneException {
        <<abstract>>
    }
    class ResourceNotFoundException
    class BusinessRuleException
    class InvalidDataException
    class PersistenceException

    RuntimeException <|-- GameZoneException
    GameZoneException <|-- ResourceNotFoundException
    GameZoneException <|-- BusinessRuleException
    GameZoneException <|-- InvalidDataException
    GameZoneException <|-- PersistenceException

    %% ===== 8. VALIDATION LAYER (Requirement 6) =====
    class ProductValidator
    class PersonValidator
    class SaleValidator