# Banking System

A RESTful Banking System API built with **Java 17**, **Spring Boot**, **MySQL**, and **Docker**. Features user authentication, account management, and transaction processing with role-based access control.

---

## Features

- JWT-based authentication & authorization
- Role-based access control (User / Admin)
- Account management (create, view, update)
- Transactions: deposits, withdrawals, transfers
- Admin dashboard
- Rate limiting on login attempts
- Global exception handling
- Unit tests for all service layers

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT |
| Build Tool | Maven |
| Containerization | Docker + Docker Compose |

---

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Git
- Postman for API testing

That's it. No need to install Java, Maven, or MySQL locally.

---

## Setup & Running

### 1. Clone the repository

```bash
git clone https://github.com/2B-Yorha/banking-System.git
cd banking-System/system
```

### 2. Create a `.env` file

Create a `.env` file in the `system/` directory with the following:

```env
DB_HOST=mysql
DB_PORT=3306
DB_NAME=banking_db
DB_USER=root
DB_PASSWORD=your_password

JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION_MS=86400000
```

>  Never commit your `.env` file. It's already in `.gitignore`.

### 3. Run with Docker Compose

```bash
docker-compose up --build
```

The app will be available at **http://localhost:8080**

MySQL will be available at **localhost:3307** (if you need to connect externally).

> The database and all tables are created automatically on first run.

### 4. Stopping the app

```bash
docker-compose down
```

To also wipe the database volume:

```bash
docker-compose down -v
```

---

## Project Structure

```
system
├─ docker-compose.yml
├─ Dockerfile
├─ pom.xml
└─ src
   └─ main
      └─ java/com/Banking/system
         ├─ config/          # JPA auditing & security config
         ├─ controller/      # REST controllers
         ├─ dto/             # Request & response DTOs
         ├─ entity/          # JPA entities (User, Account, Transaction)
         ├─ enums/           # Enums (Role, AccountType, TransactionType, etc.)
         ├─ exception/       # Custom exceptions & global handler
         ├─ repository/      # Spring Data JPA repositories
         ├─ security/        # JWT filter, JwtUtil, UserDetailsService
         └─ Service/         # Service interfaces & implementations
```

---

##  API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT |

### User
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get current user profile |
| PUT | `/api/users/me` | Update current user profile |

### Accounts
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/accounts` | Create a new account |
| GET | `/api/accounts` | Get all accounts for current user |
| GET | `/api/accounts/{id}` | Get account by ID |

### Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions/deposit` | Deposit funds |
| POST | `/api/transactions/withdraw` | Withdraw funds |
| POST | `/api/transactions/transfer` | Transfer between accounts |
| GET | `/api/transactions/{accountId}` | Get transaction history |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Admin dashboard stats |

> All endpoints except `/api/auth/**` require a valid JWT in the `Authorization: Bearer <token>` header.

---

## Running Tests

```bash
docker-compose run --rm app mvn test
```

Or if you have Maven installed locally:

```bash
mvn test
```

---

## License

This project is open source and available under the [MIT License](LICENSE).
