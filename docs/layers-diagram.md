```mermaid
flowchart TD
    subgraph UI["ui"]
        Menu
        SubMenu
    end

    subgraph SERVICE["service"]
        ProductService
        AccessoryService
        PersonService
        PromotionService
        SaleService
        ReturnService
        WarrantyService
    end

    subgraph PERSISTENCE["persistence"]
        ProductRepository
        AccessoryRepository
        PersonRepository
        PromotionRepository
        SaleRepository
        ReturnRepository
        WarrantyRepository
    end

    subgraph VALIDATION["validation"]
        ProductValidator
        PersonValidator
        SaleValidator
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
