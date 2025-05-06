# Spring Boot “Order Service” – **Live Coding Worksheet**

Welcome! You’re about to build a **mini CRUD API** with Spring Boot, Java 21, and an H2 in‑memory database.  
Follow the numbered steps: **read the explanation, perform the mini‑task, then unfold the code** to compare.

---

## 0  Why These Dependencies?

| Dependency               | Purpose                                                                     |
|--------------------------|-----------------------------------------------------------------------------|
| **Spring Web**           | Expose Java methods as HTTP endpoints (`@RestController`, `@GetMapping`, …) |
| **Spring Data JPA**      | Generates repositories so you write almost no SQL                           |
| **H2 Database**          | Lightweight DB that runs in memory – ideal for workshops                    |
| **Spring Boot DevTools** | Hot reload & LiveReload for instant feedback                                |
| *(Optional)* **Lombok**  | Cuts boilerplate – not used here so you can see every line                  |

---

## 1  Project Layout Check

Your starter project already contains Gradle files and `OrdersApplication.java`.  
Create these **packages**:

```
com.example.orders
 ├─ model        # JPA entities
 ├─ dto          # Data Transfer Objects
 ├─ repository   # Data access
 ├─ service      # Business logic
 └─ controller   # HTTP layer
```

---

## 2  Domain Layer – `Order` Entity

### 2.1  Create Skeleton

> **Task:** Create `Order` class in **model** package with no fields.

---

### 2.2  Add JPA Annotations

> **Why?** `@Entity` marks the class for ORM; `@Table` lets us pick a table name.

1. Import `jakarta.persistence.*`.
2. Annotate the class:

```java

@Entity
@Table(name = "orders")
public class Order {
}
```

---

### 2.3  Primary‑Key Field

> **Why `@GeneratedValue`?** The DB creates unique IDs, avoiding race conditions.

Add:

```java

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

---

### 2.4  Business Fields

Add:

```java
private String customerName;
private String product;
private Integer quantity;
private BigDecimal price;
```

*(Remember to `import java.math.BigDecimal`)*

---

### 2.5  Constructors & Accessors

1. **Protected no‑args constructor** – required by JPA reflection.
2. **Public constructor** with all non‑ID fields.
3. **Getters & setters** for every field (generate or type).

---

#### ✅ Reveal: Complete `Order` Entity

```java
package com.example.orders.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String product;
    private Integer quantity;
    private BigDecimal price;

    protected Order() {
    }   // JPA only

    public Order(String customerName, String product,
                 Integer quantity, BigDecimal price) {
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters & setters …
}
```

Restart the app – it still starts (no endpoints yet).

---

## 3  DTO Layer – `OrderDTO` as a Record

### 3.1  Why a DTO?

* **Decouples** API contract from DB schema.
* Hides lazy JPA proxies, internal IDs, etc.
* Record = immutable, concise, auto‑generated methods.

### 3.2  Create Record

> **Task:** Create `OrderDTO` in **dto** package as a Java **record** with the same five fields (id, customerName, …).

---

#### ✅ Reveal: `OrderDTO`

```java
package com.example.orders.dto;

import java.math.BigDecimal;

public record OrderDTO(
        Long id,
        String customerName,
        String product,
        Integer quantity,
        BigDecimal price
) {
}
```

No body required!

---

## 4  Repository Layer – `OrderRepository`

> **Task:** In **repository** package, create an interface that extends `JpaRepository<Order, Long>`.

*(No further code needed – Spring generates the implementation.)*

---

#### ✅ Reveal

```java
package com.example.orders.repository;

import com.example.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
```

---

## 5  Service Layer – Business Logic & Transactions

Create `OrderService` in **service** package.

### 5.1  Class Skeleton

> **Task:** Annotate the class with `@Service` and `@Transactional`, inject `OrderRepository` via constructor.

---

#### ✅ Reveal

```java

@Service
@Transactional
public class OrderService {

    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }
}
```

---

### 5.2  Method: Find All Orders

**Steps**

1. Declare `public List<OrderDTO> findAll()`.
2. Use `repo.findAll()` to fetch entities.
3. Map each to DTO (hint: stream + helper).
4. Return the list.

---

#### ✅ Reveal Method

```java
public List<OrderDTO> findAll() {
    return repo.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
}
```

---

### 5.3  Method: Find Order by ID

**Steps**

1. Declare `public OrderDTO findById(Long id)`.
2. Use `repo.findById(id)` and `orElseThrow(...)` with `EntityNotFoundException`.
3. Convert to DTO.

---

#### ✅ Reveal Method

```java
public OrderDTO findById(Long id) {
    return repo.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new EntityNotFoundException("Order " + id + " not found"));
}
```

---

### 5.4  Method: Create Order

**Steps**

1. Declare `public OrderDTO create(OrderDTO dto)`.
2. Convert DTO → Entity (`toEntity`).
3. `repo.save(entity)`.
4. Return saved DTO.

---

#### ✅ Reveal Method

```java
public OrderDTO create(OrderDTO dto) {
    Order saved = repo.save(toEntity(dto));
    return toDTO(saved);
}
```

---

### 5.5  Method: Update Order

**Steps**

1. Declare `public OrderDTO update(Long id, OrderDTO dto)`.
2. Fetch existing entity or throw.
3. Copy updatable fields from DTO.
4. Save and return DTO.

---

#### ✅ Reveal Method

```java
public OrderDTO update(Long id, OrderDTO dto) {
    Order existing = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order " + id + " not found"));
    existing.setCustomerName(dto.customerName());
    existing.setProduct(dto.product());
    existing.setQuantity(dto.quantity());
    existing.setPrice(dto.price());
    return toDTO(repo.save(existing));
}
```

---

### 5.6  Mapping Helpers

> **Task:** Add `private OrderDTO toDTO(Order o)` and `private Order toEntity(OrderDTO d)`.

---

#### ✅ Reveal Helpers

```java
private OrderDTO toDTO(Order o) {
    return new OrderDTO(o.getId(), o.getCustomerName(), o.getProduct(),
            o.getQuantity(), o.getPrice());
}

private Order toEntity(OrderDTO d) {
    return new Order(d.customerName(), d.product(), d.quantity(), d.price());
}
```

---

### 5.7  Full Service for Reference

```java

@Service
@Transactional
public class OrderService {
    // … (all code above combined)
}
```

---

## 6  Controller Layer – REST Endpoints

Create `OrderController` in **controller** package.

### 6.1  Class Skeleton

> **Task:** Annotate with `@RestController` and `@RequestMapping("/api/orders")`; inject `OrderService`.

---

#### ✅ Reveal

```java

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }
}
```

---

### 6.2  Endpoint: GET All

Add:

```java

@GetMapping
public List<OrderDTO> getAll() {
    return service.findAll();
}
```

---

### 6.3  Endpoint: GET One

Add:

```java

@GetMapping("/{id}")
public OrderDTO getOne(@PathVariable Long id) {
    return service.findById(id);
}
```

---

### 6.4  Endpoint: POST Create

Steps:

1. Annotate with `@PostMapping`.
2. Accept DTO via `@RequestBody`.
3. Return **201 Created**.

---

#### ✅ Reveal

```java

@PostMapping
public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO dto) {
    OrderDTO created = service.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

---

### 6.5  Endpoint: PUT Update

Add:

```java

@PutMapping("/{id}")
public OrderDTO update(@PathVariable Long id, @RequestBody OrderDTO dto) {
    return service.update(id, dto);
}
```

---

## 7  Configuration & Seed Data

### 7.1  `application.properties`

Paste:

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:ordersdb
spring.datasource.username=sa
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 7.2  Seed Demo Data

In `OrdersApplication.java`:

```java

@Bean
CommandLineRunner seed(OrderRepository repo) {
    return args -> repo.saveAll(List.of(
            new Order("Alice", "Laptop", 1, new BigDecimal("999.99")),
            new Order("Bob", "Monitor", 2, new BigDecimal("199.99"))
    ));
}
```

---

## 8  Checkpoint Tests

* `GET /api/orders` → should list 2 orders.
* `POST /api/orders` → should return created order with auto ID.
* `PUT /api/orders/{id}` → updates order.

Use curl, HTTPie, or IntelliJ’s HTTP client.

---

## 9  Concept Recap

| Concept                        | What You Saw                      | Why It Matters                                                |
|--------------------------------|-----------------------------------|---------------------------------------------------------------|
| **Entity & JPA**               | `@Entity`, `@GeneratedValue`      | Java objects persist without SQL; DB handles IDs safely.      |
| **DTO as record**              | Immutable, concise record         | Prevents accidental mutation; API contract independent of DB. |
| **Repository**                 | `extends JpaRepository`           | Zero‑SQL CRUD plus query‑by‑method‑name.                      |
| **Service & `@Transactional`** | Business rules, atomic operations | Ensures consistency; constructor injection → easy tests.      |
| **Controller**                 | Thin HTTP layer                   | Swappable transport; no business logic leakage.               |
| **H2 / DevTools**              | Instant DB, live reload           | Fast feedback loop for learning.                              |

---

## 10  Your Solo Challenge – DELETE Endpoint

Add support for `DELETE /api/orders/{id}` returning **204 No Content**.

**Hints**

* Service: `void delete(Long id)` → fetch or throw, then `repo.delete(entity)`.
* Controller: `@DeleteMapping("/{id}")` → return `ResponseEntity.noContent().build()`.

Test with:

```bash
http DELETE :8080/api/orders/1
http :8080/api/orders   # verify list shrinks
```

---

🎉 **Well done!** You built a fully functioning Spring Boot CRUD service, learning each layer’s responsibility and modern
Java patterns along the way. Keep exploring and refining your new skills!
