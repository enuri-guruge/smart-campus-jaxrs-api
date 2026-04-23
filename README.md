# 🏫 Smart Campus: Sensor & Room Management API

> A RESTful API for managing university campus infrastructure — rooms, environmental sensors, and their readings.

**Student:** Enuri Guruge
**Student ID:** W2153622 (20240714)
**Institution:** Informatics Institute of Technology (IIT) / University of Westminster
**Tech Stack:** JAX-RS (Jersey) · Grizzly Server · Maven

---

## 📌 Project Overview

This project is a RESTful API designed to manage university campus infrastructure. It handles the registration of rooms and the deployment of environmental sensors, ensuring data integrity through specific business rules and a centralized in-memory DataStore.

---

## 🏗️ Technical Analysis & Design Decisions

### Part 1: Service Architecture & Discovery

**1.1 JAX-RS Lifecycle & Thread Safety**
By default, JAX-RS resources are request-scoped (created per request). To maintain data persistence across requests and prevent race conditions during concurrent access, a **Singleton DataStore** backed by `ConcurrentHashMap` was used. The `@ApplicationPath("/api/v1")` annotation serves as the base URI prefix for the entire application.

**1.2 HATEOAS Benefits**
The discovery endpoint makes the API self-descriptive. By providing links to other resources, clients don't need to hardcode every URI — allowing the API structure to evolve without breaking client implementations.

---

### Part 2: Room Management

**2.1 IDs vs. Full Objects**
Returning only IDs reduces JSON payload size and saves bandwidth. However, this API returns **full objects**, minimising "chattiness" by giving the client all details (name, location, etc.) in a single call rather than requiring follow-up requests.

**2.2 DELETE Idempotency**
The DELETE implementation is idempotent. The first call removes the resource (`204 No Content`). Subsequent calls for the same ID return `404 Not Found`. Since the final server state remains the same (the room is gone), the operation satisfies the principle of idempotency.

---

### Part 3: Sensor Operations

**3.1 `@Consumes` Mismatch — 415 Error**
If a client sends `text/plain` instead of `application/json`, the JAX-RS runtime detects the mismatch with the `@Consumes` annotation and automatically returns `415 Unsupported Media Type`. Without this protection, the JSON parser would attempt to deserialise plain text as an object, causing an internal server crash.

**3.2 `@QueryParam` vs. Path-based Filtering**
`@QueryParam` (e.g., `?type=Temp`) is used for filtering because query parameters are the RESTful standard for refining or searching a collection. A URI identifies a unique resource; query parameters refine or search the result set.

---

### Part 4: Sub-Resource Locators

**4.1 Sub-Resource Locator Benefits**
By delegating readings to a sub-resource (`/sensors/{id}/readings`), the implementation enforces **Separation of Concerns**. This prevents `SensorResource` from becoming a "God Object" and creates a cleaner, hierarchical code structure.

---

### Part 5: Error Handling & Observability

**5.2 422 vs. 404 Semantics**
`422 Unprocessable Entity` is returned when a sensor's `roomId` is invalid. This is semantically superior to `404` — which implies the URL itself is wrong — because `422` correctly indicates the request was understood but contained semantic errors (broken referential integrity).

**5.4 Stack Trace Security Risks**
Exposing raw Java stack traces in API responses is a significant **Information Disclosure** risk. It reveals library versions and internal logic to potential attackers. A `GlobalExceptionMapper` sanitises these into clean JSON error messages to prevent reconnaissance.

**5.5 Filters vs. Manual Logging**
Using a JAX-RS `ContainerResponseFilter` for logging is more efficient than manual logging. It centralises logic in one place, ensuring every request/response is logged consistently without duplicating code across methods. Filters capture all requests — including failed ones — ensuring 100% observability without cluttering business logic.

---

## 📡 API Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| `GET` | `/api/v1` | Service Discovery (HATEOAS) | `200` |
| `GET` | `/api/v1/rooms` | List all registered rooms | `200` |
| `POST` | `/api/v1/rooms` | Create a new room | `201` |
| `GET` | `/api/v1/rooms/{id}` | Get specific room details | `200`, `404` |
| `DELETE` | `/api/v1/rooms/{id}` | Delete room (only if empty) | `204`, `409` |
| `POST` | `/api/v1/sensors` | Register a new sensor | `201`, `422` |
| `GET` | `/api/v1/sensors` | List sensors (supports `?type=X`) | `200` |
| `POST` | `/api/v1/sensors/{id}/readings` | Add a sensor reading | `201`, `403` |
| `GET` | `/api/v1/sensors/{id}/readings` | Get reading history | `200` |

---

## ⚙️ Setup & Execution

**1. Build the project:**
```bash
mvn clean install
```

**2. Run the application:**
Execute `Main.java` from your IDE, or via Maven. The server starts at:
```
http://localhost:8080/api/v1
```

---

## 🧪 Testing Guide (CURL)

### 1. Service Discovery (HATEOAS)
```bash
curl -X GET http://localhost:8080/api/v1/
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"R101", "name":"Lab 1", "capacity":30}'
```

### 3. Test `422 Unprocessable Entity` — Invalid Room Reference
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"S1", "type":"Temp", "roomId":"INVALID_ID"}'
```

### 4. Create Sensor & Test Filtering (`?type=X`)
```bash
# Create a valid sensor
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"S1", "type":"Temp", "roomId":"R101"}'

# Filter by type
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temp"
```

### 5. Test `409 Conflict` — Delete Room with Active Sensors
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/R101
```

### 6. Test Security — `500` Safety Net (Sanitised Error Output)
```bash
curl -X GET http://localhost:8080/api/v1/crash
```

---

## 📁 Project Structure

```
src/main/java/org/westminster/
├── app/               # Application Config (SmartCampusApplication)
├── models/            # Data models and Singleton DataStore
├── resources/         # JAX-RS Resource classes
└── Main.java          # Grizzly server initialization
```