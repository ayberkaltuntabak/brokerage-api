
# Brokerage Firm Backend API

This project is a backend API for a brokerage firm built using **Java** and **Spring Boot** with **JWT-based authentication**.

## Getting Started

### Prerequisites
- **Java 17**
- **Maven 3.x**

### Running the Project

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/brokerage-firm-api.git
   ```
2. **Navigate to the project directory:**
   ```bash
   cd brokerage-firm-api
   ```
3. **Build the project:**
   ```bash
   mvn clean install
   ```
4. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

The application will run at `http://localhost:8080`.

### Accessing the H2 Database Console

You can access the H2 database console at:
```
http://localhost:8080/h2-console
```
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## Testing

To run tests, execute:
```bash
mvn test
```
