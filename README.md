# Online Bookstore

The Online Bookstore API provides a robust platform for managing an online book retail system.

It handles book inventory management, enabling seamless tracking and updating of book stock.

The API supports purchase processing and dynamic book pricing, ensuring accurate transactions and
flexible pricing strategies.

Additionally, it manages customer loyalty points, rewarding repeat customers with points for purchases,
which can be redeemed for discounts or perks, enhancing user engagement and retention.

## Tool & framework versions used

- **Oracle Java** 21.0.7 (2025-04-15 LTS)
- **Apache Maven** 3.9.9
- **Spring Boot** 3.4.4
- **OpenAPI** 3.0.4

## How to run this project

1. **Clone the repository:**

    ```bash
    git clone https://github.com/pedrazamiguez/online-bookstore-assessment.git
    cd online-bookstore-assessment/online-bookstore
    ```

2. **Build and test the entire project using the Maven Wrapper:**

    ```bash
    ./mvnw clean install -U
    ```

   This command will compile the code, run tests, and package the application into a JAR file.

3. **Run the application using the Maven Wrapper:**

    ```bash
    ./mvnw -pl online-bookstore-boot spring-boot:run
    ```

   The application will start on [http://localhost:8080](http://localhost:8080).

## API documentation & access

- **Swagger UI:** [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)  
  Use this interface to explore and test the API endpoints.

- **Authentication:** All endpoints are secured with Basic Auth.  
  Use one of the test users below to authenticate:

  | Username     | Role  | Password  |
  |--------------|-------|-----------|
  | `bob`        | USER  | 12345678  |
  | `alice`      | USER  | 12345678  |
  | `admin`      | ADMIN | 12345678  |
  | `superadmin` | ADMIN | 12345678  |

  *ADMIN users can perform both USER and ADMIN operations.*

- **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **Driver Class:** `org.h2.Driver`
  - **JDBC URL:** `jdbc:h2:mem:bookstoredb`
  - **User Name:** `sa`
  - **Password:** *(leave blank)*

  A dataset is preloaded with sample data for testing purposes.
  You can view and manipulate the data directly in the H2 console.

## Architecture & Technical Decisions

This project was built as a *showcase*, prioritizing simplicity and clarity over production-grade complexity. Below
are some key decisions and design choices made during development:

### Hexagonal Architecture (Ports & Adapters)

- Encourages separation of concerns between business logic, data access, and APIs.
- Provides long-term scalability and maintainability, especially valuable when business rules evolve frequently.

### API-First Development

- API contract designed first using **OpenAPI**.
- **OpenAPI Generator** was used to rapidly scaffold REST controllers and client/server interfaces.
- This approach allows for faster iteration and smoother collaboration with front-end or third-party consumers.

### Development-Time Configuration

- **In-Memory Authentication**: Users are stored in the application configuration for simplicity.
- **H2 Database**: Chosen for fast, in-memory storage that resets with every restart.
- **No Caching Layer**: Deliberately omitted to focus on core API behavior.
- **No External Services**: No real payment, messaging, or third-party integrations are used.

### Production Considerations (Out of Scope for This Demo)

In a production setting, the following components would be implemented or replaced:

- **JWT or OAuth2** for authentication and authorization
- **PostgreSQL** or **MongoDB** for persistent data storage
- **Redis** for caching
- **ElasticSearch** for advanced search features
- **Microservice Architecture** with service discovery and API gateways

### Pricing Strategy

The API uses the **Strategy Pattern** to calculate the subtotal of a book depending on its type.  
Each book type (`NEW_RELEASE`, `REGULAR`, `OLD_EDITION`) has a specific pricing strategy encapsulated in its own class.

This approach was chosen for the following reasons:

- **Maintainability**: Business rules for pricing can evolve independently for each book type without altering core
  logic.
- **Extensibility**: New strategies (e.g., promotional discounts, dynamic pricing) can be added without modifying
  existing code.
- **Encapsulation**: Keeps the pricing logic out of the data layer and within a well-defined, testable context.

Although this logic could have been embedded into the database (e.g., storing discount percentages alongside book
types), the Strategy Pattern allows for more **flexible** and **scalable** behavior. For example, future discount rules
might depend on **publisher**, **publication year**, **customer status**, or other properties — all of which can be more
easily handled in code than in static database rules.

```java
public interface SubtotalPriceService {
    String getBookTypeCode();

    PayableAmount calculateSubtotal(OrderItem orderItem);
}
```

This design choice aligns with the overall **Hexagonal Architecture**, isolating domain logic from infrastructure and
making it easier to evolve the system over time.

## Future Improvements

- Pagination and filtering for list endpoints
- Order history and purchase analytics
- Integration with external book metadata providers (e.g., ISBN DB)
- Loyalty program rules engine for advanced reward systems
