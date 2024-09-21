
# Brokerage Firm Backend API

This project is a backend API for a brokerage firm that allows employees to manage customers, create stock orders, deposit/withdraw money, and view asset holdings. It is built using **Java** and **Spring Boot**, following **Domain-Driven Design (DDD)** principles with proper use of **value objects**, **aggregates**, and **entities**. The project supports **JWT-based authentication** for securing endpoints.

## Table of Contents

- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Domain Logic](#domain-logic)
- [Authentication & Authorization](#authentication--authorization)
- [API Endpoints](#api-endpoints)
  - [Customer Endpoints](#customer-endpoints)
  - [Order Endpoints](#order-endpoints)
  - [Authentication Endpoints](#authentication-endpoints)
- [Example Requests & Responses](#example-requests--responses)
- [Running the Project](#running-the-project)
- [Testing](#testing)
- [Configuration](#configuration)

## Project Structure

The project follows a domain-driven design (DDD) structure:

```
src
│
├── main
│   ├── java
│   │   ├── com.brokerage
│   │   │   ├── domain
│   │   │   ├── application
│   │   │   ├── infrastructure
│   │   │   ├── presentation
│   │   └── resources
│   │       ├── application.yml
│   │       ├── application.properties
│   └── test
└── ...
```

- `domain`: Contains entities, value objects, aggregates, and repositories.
- `application`: Contains application services that handle business logic.
- `infrastructure`: Contains security, configuration, and exception handling.
- `presentation`: Contains controllers and DTOs.

## Technologies Used

- **Java 17**
- **Spring Boot** (Spring Data JPA, Spring Security, Spring Web)
- **H2 Database** (for development/testing)
- **JWT** for authentication
- **Maven** for build management
- **Lombok** for reducing boilerplate code

## Features

- Create, list, and delete stock orders for customers
- Deposit and withdraw money
- JWT-based authentication for customers and admins
- Asset management for customers
- Comprehensive exception handling with consistent response structure

## Domain Logic

- The project employs domain-driven design with **entities**, **value objects**, and **aggregates**.
- Main aggregates: `Customer`, `Order`, `Asset`
- The system checks customer's balance when creating orders and manages it during cancellations or matches.

## Authentication & Authorization

- JWT-based authentication is used.
- Two roles: `ROLE_ADMIN` and `ROLE_CUSTOMER`
- Admins can access all endpoints, while customers are limited to their own data.

## API Endpoints

### Customer Endpoints

| Method | Endpoint                     | Description                       |
|--------|------------------------------|-----------------------------------|
| POST   | `/api/customers/{customerId}/deposit`    | Deposit money for a customer       |
| POST   | `/api/customers/{customerId}/withdraw`   | Withdraw money for a customer      |
| GET    | `/api/customers/{customerId}` | Retrieve customer information     |

### Order Endpoints

| Method | Endpoint                      | Description                             |
|--------|-------------------------------|-----------------------------------------|
| POST   | `/api/orders/create`          | Create a new order                      |
| POST   | `/api/orders/match/{orderId}` | Match a pending order                   |
| DELETE | `/api/orders/cancel/{orderId}`| Cancel a pending order                  |
| GET    | `/api/orders/{customerId}/orders` | List orders for a customer within a date range |

### Authentication Endpoints

| Method | Endpoint                      | Description                          |
|--------|-------------------------------|--------------------------------------|
| POST   | `/api/login`                  | Authenticate user and generate token |
| POST   | `/api/signup/customer`        | Register a new customer              |
| POST   | `/api/signup/admin`           | Register a new admin                 |

### Asset Endpoints

| Method | Endpoint                          | Description                                  |
|--------|------------------------------------|----------------------------------------------|
| GET    | `/api/assets/{customerId}`         | List all assets for a given customer         |
| GET    | `/api/assets/{customerId}/{assetName}` | Retrieve a specific asset for a customer |



## Example Requests & Responses

### Example: Customer Login

**Request:**

```bash
POST /api/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Example: Create Order

**Request:**

```bash
POST /api/orders/create
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "customerId": 1,
  "assetName": "AAPL",
  "orderSide": "BUY",
  "size": 10,
  "price": 150.00
}
```

**Response:**

```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": 101,
    "assetName": "AAPL",
    "orderSide": "BUY",
    "size": 10,
    "price": 150.0,
    "status": "PENDING",
    "createDate": "2024-09-20T14:00:00"
  }
}
```

## Running the Project

### Prerequisites
- Java 17
- Maven 3.x

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/brokerage-firm-api.git
   ```
2. Navigate to the project directory:
   ```bash
   cd brokerage-firm-api
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Start the application:
   ```bash
   mvn spring-boot:run
   ```

The server will start at `http://localhost:8080`.

### Access the H2 Database Console

You can access the H2 database console at:
```
http://localhost:8080/h2-console
```
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## Testing

To run tests, use:
```bash
mvn test
```

## Configuration

You can adjust settings in:
- `application.properties` for environment selection.
- `application.yml` for structured configurations.

## Contact

For any issues or suggestions, feel free to create an issue on the GitHub repository or contact 
[ayberkomeraltuntabak@gmail.com].
