```mermaid
flowchart TD
    subgraph UI["ui"]
        Menu
        ConsoleInput
        ProductSubMenu
        AccessorySubMenu
        PersonSubMenu
        PromotionSubMenu
        ReturnSubMenu
        WarrantySubMenu
        SaleSubMenu
        SaleReceiptFormatter
        WarrantyCertificateFormatter
    end

    subgraph SERVICE["service"]
        StockUpdater
        ProductService
        AccessoryService
        PersonService
        PromotionService
        SaleService
        ReturnService
        WarrantyService
    end

    subgraph PERSISTENCE["persistence"]
        CsvSupport
        ProductCsvMapper
        ProductStorage
        AccessoryStorage
        PersonStorage
        PromotionStorage
        SaleStorage
        ReturnStorage
        WarrantyStorage
    end

    subgraph VALIDATION["validation"]
        ProductValidator
        PersonValidator
        AccessoryValidator
        PromotionValidator
        SaleValidator
        WarrantyValidator
    end

    subgraph EXCEPTIONS["exceptions (transversal)"]
        GameZoneException
        ResourceNotFoundException
        BusinessRuleException
        InvalidDataException
        PersistenceException
    end

    subgraph MODEL["model"]
        Person
        Client
        Seller
        Product
        VideoGame
        Console
        Accessory
        Controller
        Cable
        Memory
        ConsoleCompatible
        Promotion
        PercentageDiscount
        CategoryDiscount
        BulkPurchaseDiscount
        Sale
        SaleDetail
        Return
        Warranty
        BasicWarranty
        ExtendedWarranty
    end

    UI --> SERVICE
    UI ..> EXCEPTIONS : catches
    SERVICE --> PERSISTENCE
    SERVICE --> MODEL
    SERVICE --> VALIDATION
    VALIDATION --> MODEL
    VALIDATION ..> EXCEPTIONS : throws
    SERVICE ..> EXCEPTIONS : throws
    PERSISTENCE ..> EXCEPTIONS : wraps/throws
    PERSISTENCE --> MODEL
```
