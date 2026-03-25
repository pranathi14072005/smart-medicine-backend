# CareMeds Backend API

A production-ready Spring Boot REST API for managing medicines, reminders, and pharmacies.

## Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Security** + **JWT** (stateless auth)
- **Spring Data JPA** + **MySQL** (H2 for dev)
- **Lombok**, **ModelMapper**, **Springdoc OpenAPI**

## Project Structure

```
src/main/java/com/smartmedicine/
├── SmartMedicineApplication.java
├── controller/          # REST endpoints
├── service/             # Business logic
├── repository/          # JPA data access
├── model/               # JPA entities
├── dto/                 # Request/Response objects
├── config/              # Security, JWT, OpenAPI
└── exception/           # Custom exceptions + global handler
```

## API Endpoints

### Auth  `/api/auth`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | Login and get JWT |
| POST | `/refresh` | Refresh access token |
| POST | `/logout` | Logout (client-side) |
| GET | `/me` | Get current user info |

### Users  `/api/users`
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/profile` | Get own profile |
| PUT | `/profile` | Update own profile |
| POST | `/change-password` | Change password |
| DELETE | `/account` | Deactivate account |
| GET | `/` | All users (ADMIN) |
| PATCH | `/{id}/role` | Update role (ADMIN) |

### Medicines  `/api/medicines`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Add medicine |
| GET | `/` | List my medicines |
| GET | `/{id}` | Get medicine |
| PUT | `/{id}` | Update medicine |
| DELETE | `/{id}` | Delete medicine |
| GET | `/search?name=` | Search by name |
| GET | `/expired` | Expired medicines |
| GET | `/expiring-soon?days=30` | Expiring soon |
| GET | `/low-stock` | Low stock medicines |
| PATCH | `/{id}/quantity` | Update quantity |
| GET | `/stats` | Dashboard stats |

### Reminders  `/api/reminders`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create reminder |
| GET | `/` | List reminders |
| GET | `/{id}` | Get reminder |
| PUT | `/{id}` | Update reminder |
| DELETE | `/{id}` | Delete reminder |
| GET | `/active` | Active reminders |
| GET | `/today` | Today's reminders |
| POST | `/{id}/taken` | Mark dose taken |
| PATCH | `/{id}/toggle` | Toggle on/off |

### Pharmacies  `/api/pharmacies`
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/public` | Active pharmacies |
| GET | `/public/{id}` | Pharmacy by ID |
| GET | `/public/search?keyword=` | Search pharmacies |
| GET | `/public/nearby?lat=&lng=&radiusKm=` | Nearby pharmacies |
| GET | `/public/with-delivery` | Has delivery |
| GET | `/public/open-24h` | 24-hour pharmacies |
| POST | `/` | Create (ADMIN) |
| PUT | `/{id}` | Update (ADMIN) |
| DELETE | `/{id}` | Delete (ADMIN) |
| PATCH | `/{id}/status` | Update status (ADMIN) |

## Getting Started

### 1. Clone and configure
```bash
git clone <repo>
cd backend
```

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartmedicine
spring.datasource.username=your_user
spring.datasource.password=your_password
```

### 2. Run
```bash
mvn spring-boot:run
```

### 3. API Docs
Open: http://localhost:8080/swagger-ui.html

### 4. H2 Dev Mode
Uncomment H2 settings in `application.properties` and comment out MySQL.  
H2 Console: http://localhost:8080/h2-console

## Authentication

All protected endpoints require a Bearer token:

```
Authorization: Bearer <your_jwt_token>
```

Obtain the token via `POST /api/auth/login`.

## Roles

| Role | Description |
|------|-------------|
| `USER` | Standard user (default) |
| `ADMIN` | Full system access |
| `PHARMACIST` | Pharmacy management |
