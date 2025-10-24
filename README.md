# Card Management Application (Java + Spring Boot)

## Overview
This is a simple and secure card management web application built using **Java Spring Boot** and **PostgreSQL**. The goal of the project is to demonstrate backend development skills by handling encrypted data, storing information securely, and building a clean, functional user interface.

Users can:
- Add new card records by entering the cardholder’s name and PAN number.
- Search for existing cards using only the **last four digits** of the PAN.

All card numbers are **AES-encrypted** before being stored to ensure:
- No plaintext numbers appear in the database.
- No sensitive data is exposed in the logs.

The search results show:
- **Cardholder Name**
- **Masked PAN** (`**** **** 1234`)
- **Created Timestamp**

The frontend is built with **Thymeleaf** templates using simple HTML, making it lightweight and easy to run without any JavaScript frameworks.

---

## Features
- Secure AES encryption for PAN storage.  
- Duplicate card detection and prevention.  
- Clean and readable timestamp formatting (`22 Oct 2025, 17:53:12`).  
- Simple and responsive HTML + Thymeleaf frontend.  
- Compatible across macOS, Windows, and Linux.  

---

## Security Considerations
- Encryption Key Management:
The AES key is provided via environment variables, never hardcoded.
In production, it is recommended to use a secret manager/OS-level env for extra safety.

- No Plaintext Storage or Logging:
All PANs are encrypted on disk/database and are never shown in logs.

- Best Practice:
Masking is performed right after decryption, before data leaves the backend. The key can be rotated easily by updating the env variable.

---

## Tech Stack
- Java 17  
- Spring Boot 3.x  
- PostgreSQL 14+  
- Maven 3.8+  
- Thymeleaf  

---

## How to Run the Project

### 1. Install Requirements
Ensure that **Java 17**, **Maven 3.8+**, and **PostgreSQL 14+** are installed.

To verify:
```
java -version
mvn -v
psql --version
```

If you don’t have them yet:
- **macOS**
  ```
  brew install openjdk@17 maven postgresql
  ```
 - **Ubuntu / Linux**
  ```
  sudo apt install openjdk-17-jdk maven postgresql
  ```
- **Windows**  
Download and install Java 17, Maven, and PostgreSQL from their official websites.

---

### 2. Set Up Your Local Database
Open your PostgreSQL terminal (or GUI tool like pgAdmin, DBeaver, TablePlus) and run:
```
CREATE DATABASE carddb;
CREATE USER carduser WITH PASSWORD 'strongpassword';
GRANT ALL PRIVILEGES ON DATABASE carddb TO carduser;
```

---

### 3. Configure Application Properties
In `src/main/resources/application.properties`, update the values as needed:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/carddb
spring.datasource.username=carduser
spring.datasource.password=strongpassword
spring.jpa.hibernate.ddl-auto=update
encryption.secret=MySecretKey12345
```

Make sure the `encryption.secret` key is 16, 24, or 32 characters long for valid AES encryption.

---

### 4. Run the Application
From the project root folder:
```
mvn clean spring-boot:run
```

When it starts successfully, open:
```
http://localhost:8080/add-card
```


---

## Usage

### Adding a Card
1. Go to `/add-card`  
2. Enter the **cardholder name** and **16-digit PAN**  
3. Click “Add Card”  
   - On success, a confirmation message appears.  
   - Plaintext PANs are never stored — only encrypted values go into the database.

### Searching for a Card
1. Go to `/search-card`  
2. Enter the **last 4 digits** (e.g. `1234`)  
3. The results display:
   - Cardholder Name  
   - Masked PAN (`**** **** 1234`)  
   - Created Time

If no matches are found, you’ll see:
"No cards found with those digits."


---

## Database Schema

| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Unique identifier for each card |
| cardholder_name | VARCHAR | The name of the cardholder |
| encrypted_pan | VARCHAR | AES-encrypted version of the card number |
| created_at | TIMESTAMP | Automatic timestamp of when the record was added |

**Design Choices:**
- UUID ensures globally unique IDs for records.  
- AES encryption keeps card data secure at rest.  
- Created timestamps make records auditable and traceable.

**Database Choices justification:**

I chose PostgreSQL because it provides stronger consistency, scalability, and flexibility than simpler relational or non-relational databases. PostgreSQL is fully ACID-compliant by default, which is important to guarantee secure and predictable transactions for storing and encrypting sensitive data like PAN numbers. Unlike MySQL, which is optimised for read-heavy apps. PostgreSQL handles read‑write concurrency efficiently through MVCC, making it better for applications that both query and update records frequently.​

Moreover, it also supports advanced data types like JSON, arrays, enums, and network types. This feature allows structured and semi‑structured data to coexist in one system. In comparison to SQLite, PostgreSQL offers multi‑user access, stronger indexing, and better transaction isolation.

---

## Encryption Details
This project uses the **AES (Advanced Encryption Standard)** algorithm implemented in `AESEncryptionUtil.java`.  
Only encrypted PANs are stored — plaintext cards are never saved or printed anywhere in logs.  
Decryption is only used temporarily for comparison during searches.

---

## Helpful Commands

| Task | Command |
|------|----------|
| Start App | `mvn spring-boot:run` |
| Stop App | `Ctrl + C` |
| Clean Build | `mvn clean` |
| Rebuild | `mvn install` |
| Run Tests | `mvn test` |

---

## Troubleshooting

| Issue | Likely Cause | Suggested Fix |
|-------|---------------|----------------|
| App won't start (port in use) | Port 8080 occupied | Stop other services or change `server.port` |
| Database connection error | Wrong credentials | Verify PostgreSQL username/password |
| AES key invalid | Key wrong length | Use 16, 24, or 32 chars |
| No search results | Empty table | Run `SELECT * FROM card;` to verify data |

---

## Happy set up ~~





