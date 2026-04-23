# 🏫 Smart Campus: Sensor & Room Management API

> A lightweight RESTful web service for managing campus infrastructure — rooms and their environmental sensors.

**Student:** Enuri Guruge
**Institution:** Informatics Institute of Technology (IIT) / University of Westminster
**Tech Stack:** JAX-RS / Jersey · Grizzly Embedded Server · Maven

---

## 📌 Project Overview

This project implements a robust RESTful web service designed to manage campus infrastructure, specifically focusing on the relationship between rooms and their environmental sensors.

The system leverages **JAX-RS (Jersey)** for resource mapping and runs on an **embedded Grizzly HTTP server**, making it a standalone lightweight application. Following coursework specifications, the system uses a custom **in-memory DataStore** backed by thread-safe collections, ensuring high performance without external database dependencies.

---

## 🏗️ Architecture & Design Decisions

### JAX-RS Lifecycle & State Management
By default, JAX-RS resources are request-scoped. To maintain state across requests, a **Singleton DataStore** was implemented using `ConcurrentHashMap` to guarantee thread safety and data persistence throughout the application lifecycle.

### HATEOAS
The `/api/v1` root endpoint implements **Hypermedia as the Engine of Application State (HATEOAS)**, allowing clients to dynamically discover the API's capabilities — reducing hardcoding and increasing system flexibility.

### Room Management
The API returns **full objects** rather than just IDs, providing a better developer experience (DX) by eliminating unnecessary follow-up requests.

**DELETE Idempotency:** Deleting a resource yields the same server state regardless of repetition. The first successful deletion returns `204 No Content`; subsequent attempts return `404 Not Found`.

### Sensor Operations
- **Media Type Validation:** The `@Consumes` annotation strictly enforces `application/json`. Incorrect media types automatically trigger `415 Unsupported Media Type`.
- **Filtering:** `@QueryParam` is used for collection filtering (e.g., `?type=Temp`), following REST best practices where the path identifies the resource and query parameters refine the representation.

### Sub-Resource Locator Pattern
Sensor readings (`/sensors/{id}/readings`) are managed via the **Sub-Resource Locator** pattern, enforcing Separation of Concerns and preventing the main `SensorResource` from becoming an unmanageable "God Object."

### Error Handling & Security
- **Referential Integrity:** Returns `422 Unprocessable Entity` when a sensor is linked to a non-existent `roomId`, providing better semantic clarity than a generic `400` or `404`.
- **Information Disclosure Prevention:** A `GlobalExceptionMapper` intercepts all unhandled exceptions, hiding raw Java stack traces and returning only sanitized JSON error messages.

---

## 📡 API Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| `GET` | `/api/v1` | Service Discovery (HATEOAS) | `200` |
| `GET` | `/api/v1/rooms` | List all registered rooms | `200` |
| `POST` | `/api/v1/rooms` | Create a new room | `201`, `422` |
| `GET` | `/api/v1/rooms/{id}` | Get specific room details | `200`, `404` |
| `DELETE` | `/api/v1/rooms/{id}` | Delete room (fails if sensors exist) | `204`, `409` |
| `POST` | `/api/v1/sensors` | Register a new sensor to a room | `201`, `422` |
| `GET` | `/api/v1/sensors` | List sensors (supports `?type=X`) | `200` |
| `GET` | `/api/v1/sensors/{id}/readings` | Get sensor reading history | `200`, `404` |
| `POST` | `/api/v1/sensors/{id}/readings` | Add reading (fails if `MAINTENANCE`) | `201`, `403` |

---

## ⚙️ Setup & Execution

### Prerequisites
- JDK 17 or higher
- Maven 3.6+

### Build & Run

**1. Clone the repository and navigate to the root folder.**

**2. Build the project:**
```bash
mvn clean install
```

**3. Run the application:**
```bash
mvn exec:java -Dexec.mainClass="org.westminster.Main"
```

**4. Access the API:**
```
http://localhost:8080/api/v1
```

---

## 🧪 Testing Guide

### 1. Create a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"L-1", "name":"Lab 1", "location":"Level 1"}'
```

### 2. Register a Sensor (Referential Integrity)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"S-1", "type":"Temp", "roomId":"L-1", "status":"ACTIVE"}'
```

### 3. Add a Reading (Business Logic Check)
> If the sensor status is `MAINTENANCE`, this request returns `403 Forbidden`.

```bash
curl -X POST http://localhost:8080/api/v1/sensors/S-1/readings \
  -H "Content-Type: application/json" \
  -d '{"value":25.5}'
```

### 4. Delete a Room (Conflict Check)
> Attempting to delete a room that still has sensors returns `409 Conflict`.

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/L-1
```

### 5. Global Exception Handler Test
> Verifies sanitized error output (no stack traces exposed).

```bash
curl -X GET http://localhost:8080/api/v1/crash
```

---

## 📁 Project Structure

```
src/main/java/org/westminster/
├── app/               # Configuration & Application Entry
├── models/            # POJOs and DataStore (Singleton)
├── resources/         # JAX-RS Resource Classes (Controllers)
└── Main.java          # Grizzly Server Entry Point
```
