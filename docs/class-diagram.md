classDiagram
    direction TB

    %% ==========================================
    %% 1. EXCEPTIONS LAYER (Transversal)
    %% ==========================================
    class RuntimeException

    class GameZoneException {
        <<abstract>>
        -String errorCode
        #GameZoneException(String message)
        #GameZoneException(String message, String errorCode)
        +String getErrorCode()
    }

    class ResourceNotFoundException {
        +ResourceNotFoundException(String resourceType, String identifier)
    }

    class BusinessRuleException {
        +BusinessRuleException(String message)
    }

    class InvalidDataException {
        -String fieldName
        +InvalidDataException(String fieldName, String problem)
        +String getFieldName()
    }

    class PersistenceException {
        +PersistenceException(String message, Throwable cause)
    }

    %% ==========================================
    %% 2. VALIDATION LAYER
    %% ==========================================
    class ProductValidator {
        +void validateProductData(String id, String title, double price, int stock)$
        +void validateStockAvailability(Product product, int requestedQuantity)$
        +void validateProductExists(Product product, String productId)$
    }

    class PersonValidator {
        +void validatePersonData(String id, String name, String email, String phone)$
        +void validatePersonExists(Person person, String personId)$
        +void validateUniquePerson(List~Person~ existing, String newId)$
    }

    class SaleValidator {
        +void validateSaleData(Client client, Seller seller, List~Product~ products)$
        +void validateSaleExists(Sale sale, String saleId)$
    }

    %% ==========================================
    %% 3. MODEL LAYER (Products, Accessories, People, Sales, Promotions, Returns, Warranties)
    %% ==========================================
    class Product {
        <<abstract>>
        -String id
        -String title
        -double price
        -int stock
        +String getId()
        +String getTitle()
        +double getPrice()
        +int getStock()
        +void setStock(int stock)
    }

    class Console {
        -String brand
        -String model
        -String generation
    }

    class VideoGame {
        -String platform
        -String genre
        -String ageRating
    }

    class Accessory {
        -String connectionType
        -List~String~ compatibleConsoleIds
        -double length
        -String connectorType
        -int capacityGb
        -String memoryType
    }

    class Person {
        <<abstract>>
        -String id
        -String name
        -String phone
        -String email
        +String getId()
        +String getName()
    }

    class Client {
        -List~String~ purchaseHistory
        +List~String~ getPurchaseHistory()
        +void addPurchase(String saleId)
    }

    class Seller {
        -String shift
        +String getShift()
    }

    class Sale {
        -String id
        -Client client
        -Seller seller
        -LocalDate date
        -List~SaleDetail~ details
        -String appliedPromotionName
        -double discountAmount
        -double additionalCosts
        +String getId()
        +Client getClient()
        +Seller getSeller()
        +LocalDate getDate()
        +double calculateTotal()
        +double calculateFinalTotal()
        +void addDetail(SaleDetail detail)
        +void addExtraCost(double cost)
    }

    class SaleDetail {
        -Product product
        -int quantity
        -double unitPrice
        +Product getProduct()
        +int getQuantity()
        +double getUnitPrice()
        +double getSubtotal()
    }

    class Promotion {
        <<abstract>>
        -String id
        -String name
        -LocalDate startDate
        -LocalDate endDate
        +String getId()
        +String getName()
        +boolean isActive(LocalDate date)
        +double calculateDiscount(Sale sale)
    }

    class PercentageDiscount {
        -double percentage
    }

    class CategoryDiscount {
        -double percentage
        -String targetCategory
    }

    class BulkPurchaseDiscount {
        -int minQuantity
        -double percentage
    }

    class Return {
        -String id
        -Sale sale
        -LocalDate returnDate
        -List~Product~ returnedProducts
        -String reason
        -double refundAmount
        +String getId()
        +Sale getSale()
        +double calculateRefundAmount()
        +String generateReturnReceipt()
    }

    class Warranty {
        <<abstract>>
        -String id
        -Product product
        -Sale sale
        -LocalDate startDate
        -LocalDate endDate
        +String getId()
        +Product getProduct()
        +Sale getSale()
        +LocalDate getStartDate()
        +LocalDate getEndDate()
        +int getDurationInMonths()*
        +String getWarrantyType()*
        +double getAdditionalCost()*
        +boolean isActive(LocalDate date)
        +String generateWarrantyCertificate()
    }

    class BasicWarranty
    class ExtendedWarranty

    %% ==========================================
    %% 4. PERSISTENCE LAYER
    %% ==========================================
    class ProductRepository {
        +List~Product~ load()
        +void saveAll(List~Product~ products)
    }

    class AccessoryRepository {
        +List~Accessory~ loadAll()
        +void saveAll(List~Accessory~ accessories)
    }

    class PersonRepository {
        +List~Client~ loadClients()
        +List~Seller~ loadSellers()
        +void saveClients(List~Client~ clients)
        +void saveSellers(List~Seller~ sellers)
    }

    class PromotionRepository {
        +List~Promotion~ loadAll()
        +void saveAll(List~Promotion~ promotions)
    }

    class SaleRepository {
        +List~Sale~ loadAll(...)
        +void saveAll(List~Sale~ sales)
    }

    class ReturnRepository {
        +List~Return~ loadAll(...)
        +void saveAll(List~Return~ returns)
    }

    class WarrantyRepository {
        +List~Warranty~ loadAll()
        +void saveAll(List~Warranty~ warranties)
    }

    %% ==========================================
    %% 5. SERVICE LAYER
    %% ==========================================
    class ProductService {
        -ProductRepository repository
        +List~Product~ listAll()
        +void registerConsole(...)
        +void registerVideoGame(...)
        +void updateStock(String id, int qty)
        +void restoreStock(String id, int qty)
    }

    class AccessoryService {
        -AccessoryRepository repository
        +List~Accessory~ listAllAccessories()
        +void registerController(...)
        +void registerCable(...)
        +void registerMemory(...)
        +void updateStock(String id, int qty)
        +void restoreStock(String id, int qty)
    }

    class PersonService {
        -PersonRepository repository
        +List~Client~ listClients()
        +List~Seller~ listSellers()
        +void registerClient(...)
        +Client findClientById(String id)
        +Seller findSellerById(String id)
        +void registerPurchase(String clientId, String saleId)
    }

    class PromotionService {
        -PromotionRepository repository
        +List~Promotion~ listAllPromotions()
        +List~Promotion~ listActivePromotions()
        +void registerPercentageDiscount(...)
        +void registerCategoryDiscount(...)
        +void registerBulkPurchaseDiscount(...)
        +Promotion findBestPromotionFor(Sale sale)
    }

    class WarrantyService {
        -WarrantyRepository repository
        +BasicWarranty assignBasicWarranty(Product p, Sale s, LocalDate date)
        +ExtendedWarranty assignExtendedWarranty(Product p, Sale s, LocalDate date)
        +Warranty findById(String id)
        +Warranty findWarrantyByProduct(String productId, String saleId)
        +List~Warranty~ listAllWarranties()
        +List~Warranty~ listActiveWarranties()
        +List~Warranty~ listWarrantiesExpiringSoon(int days)
    }

    class SaleService {
        -SaleRepository saleRepository
        -PersonService personService
        -ProductService productService
        -AccessoryService accessoryService
        -PromotionService promotionService
        -WarrantyService warrantyService
        +Product findProductOrAccessoryById(String id)
        +Sale processSale(...)
        +void restoreStock(Product p, int qty)
        +List~Sale~ listAllSales()
        +Sale findById(String id)
    }

    class ReturnService {
        -ReturnRepository returnRepository
        -SaleService saleService
        -AccessoryService accessoryService
        -ProductService productService
        -WarrantyService warrantyService
        +Return registerReturn(String saleId, List~String~ productIds, String reason)
        +List~Return~ viewAllReturns()
        +List~Return~ viewReturnsByCustomer(String customerId)
        +List~Return~ viewReturnsBySale(String saleId)
        +double generateMonthlyBalance(int month, int year)
    }

    %% ==========================================
    %% 6. UI LAYER
    %% ==========================================
    class Menu {
        -SubMenu subMenu
        -Scanner scanner
        +void displayMenu()
    }

    class SubMenu {
        -ProductService productService
        -AccessoryService accessoryService
        -PersonService personService
        -PromotionService promotionService
        -SaleService saleService
        -ReturnService returnService
        -WarrantyService warrantyService
        -Scanner scanner
        +void showProductSubMenu()
        +void showAccessorySubMenu()
        +void showPersonSubMenu()
        +void showPromotionSubMenu()
        +void showReturnSubMenu()
        +void showWarrantySubMenu()
        +void processSaleForm()
        +void listSalesHistory()
    }

    %% ==========================================
    %% INHERITANCE RELATIONS
    %% ==========================================
    RuntimeException <|-- GameZoneException
    GameZoneException <|-- ResourceNotFoundException
    GameZoneException <|-- BusinessRuleException
    GameZoneException <|-- InvalidDataException
    GameZoneException <|-- PersistenceException

    Product <|-- Console
    Product <|-- VideoGame
    Product <|-- Accessory

    Person <|-- Client
    Person <|-- Seller

    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount

    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    %% ==========================================
    %% MODEL ASSOCIATIONS
    %% ==========================================
    Sale "1" *-- "1..*" SaleDetail
    Product "1" --> "0..*" SaleDetail
    Client "1" --> "0..*" Sale
    Seller "1" --> "0..*" Sale
    Sale "1" --> "1" Client
    Sale "1" --> "1" Seller
    Return "1" --> "1" Sale
    Return "1" --> "0..*" Product
    Warranty "1" --> "1" Product
    Warranty "1" --> "1" Sale

    %% ==========================================
    %% REPOSITORIES & SERVICES ASSOCIATIONS
    %% ==========================================
    ProductService --> ProductRepository : uses
    AccessoryService --> AccessoryRepository : uses
    PersonService --> PersonRepository : uses
    PromotionService --> PromotionRepository : uses
    SaleService --> SaleRepository : uses
    ReturnService --> ReturnRepository : uses
    WarrantyRepository --> SaleRepository : uses
    WarrantyRepository --> ProductRepository : uses
    WarrantyRepository --> PersonRepository : uses
    WarrantyRepository --> AccessoryRepository : uses
    WarrantyRepository --> PromotionRepository : uses
    WarrantyService --> WarrantyRepository : uses

    SaleService --> PersonService : uses
    SaleService --> ProductService : uses
    SaleService --> AccessoryService : uses
    SaleService --> PromotionService : uses
    SaleService --> WarrantyService : uses

    ReturnService --> SaleService : uses
    ReturnService --> AccessoryService : uses
    ReturnService --> ProductService : uses
    ReturnService --> WarrantyService : uses

    %% ==========================================
    %% VALIDATORS & EXCEPTION EXCEPTIONS USAGE
    %% ==========================================
    ProductService ..> ProductValidator : validates
    PersonService ..> PersonValidator : validates
    SaleService ..> SaleValidator : validates

    ProductValidator ..> InvalidDataException : throws
    ProductValidator ..> BusinessRuleException : throws
    ProductValidator ..> ResourceNotFoundException : throws
    PersonValidator ..> InvalidDataException : throws
    PersonValidator ..> BusinessRuleException : throws
    PersonValidator ..> ResourceNotFoundException : throws
    SaleValidator ..> InvalidDataException : throws
    SaleValidator ..> BusinessRuleException : throws
    SaleValidator ..> ResourceNotFoundException : throws

    ProductRepository ..> PersistenceException : throws
    AccessoryRepository ..> PersistenceException : throws
    PersonRepository ..> PersistenceException : throws
    PromotionRepository ..> PersistenceException : throws
    SaleRepository ..> PersistenceException : throws
    ReturnRepository ..> PersistenceException : throws
    WarrantyRepository ..> PersistenceException : throws

    %% ==========================================
    %% UI USAGE
    %% ==========================================
    Menu *-- SubMenu : owns
    SubMenu --> ProductService : uses
    SubMenu --> AccessoryService : uses
    SubMenu --> PersonService : uses
    SubMenu --> PromotionService : uses
    SubMenu --> SaleService : uses
    SubMenu --> ReturnService : uses
    SubMenu --> WarrantyService : uses

    SubMenu ..> ResourceNotFoundException : catches
    SubMenu ..> BusinessRuleException : catches
    SubMenu ..> InvalidDataException : catches
    SubMenu ..> PersistenceException : catches
    SubMenu ..> GameZoneException : catches