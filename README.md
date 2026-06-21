# Payment Gateway API

[![Java CI](https://github.com/divyanka-pagare/payment-gateway/actions/workflows/ci.yml/badge.svg)](https://github.com/divyanka-pagare/payment-gateway/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

A production-style payment gateway backend built with Spring Boot, modeled on real-world systems like Razorpay. Supports secure order creation, payment processing via the Razorpay sandbox, refunds, webhook event handling, and automated daily settlement reconciliation.

## Why this project

Most payment gateway demos stop at "create an order." This one goes further — implementing the patterns that actual fintech systems rely on: idempotency keys to prevent duplicate charges, webhook signature validation, partial/full refund tracking, and scheduled reconciliation reporting.

## Features

- **JWT Authentication** — secure register/login with role-based access (Merchant / Admin / Customer)
- **Order Management** — create and track orders with idempotency key support
- **Razorpay Integration** — real sandbox order creation and payment signature verification
- **Refund Handling** — partial and full refunds with amount validation against available balance
- **Webhook Listener** — HMAC signature-verified endpoint for async payment events
- **Daily Reconciliation** — scheduled job that generates settlement reports (total captured, refunded, net settled)
- **API Documentation** — interactive Swagger UI
- **Automated Testing** — JUnit + Mockito test suite running on every push via GitHub Actions

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security, JWT (JJWT) |
| Persistence | Spring Data JPA, Hibernate, MySQL 8 |
| Payments | Razorpay Java SDK |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito |
| CI/CD | GitHub Actions |

## Architecture

```
Client
  │
  ▼
JWT Filter ──► Spring Security ──► Controller
                                       │
                                       ▼
                                    Service Layer
                                       │
                         ┌─────────────┼─────────────┐
                         ▼             ▼             ▼
                   Repository    Razorpay SDK    Reconciliation
                  (JPA/MySQL)     (Sandbox)         (Scheduled)
```

**Payment lifecycle:**
```
CREATED → INITIATED → AUTHORIZED → CAPTURED → SETTLED
                                       │
                              REFUNDED / PARTIALLY_REFUNDED
```

## Getting Started

### Prerequisites
- Java 17+
- MySQL 8+
- A Razorpay account ([sandbox keys](https://dashboard.razorpay.com/app/keys))

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/divyanka-pagare/payment-gateway.git
   cd payment-gateway
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE payment_db;
   ```

3. **Set environment variables**

   This project reads configuration from environment variables — no secrets are committed to the repo.

   ```bash
   export DB_USER=root
   export DB_PASS=your_mysql_password
   export JWT_SECRET=your_base64_secret      # generate with: openssl rand -base64 32
   export RAZORPAY_KEY_ID=rzp_test_xxxxxxxx
   export RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxx
   export RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
   ```

   *(On Windows PowerShell, use `$env:DB_USER="root"` etc.)*

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Explore the API**

   Visit `http://localhost:8080/swagger-ui.html` for interactive API documentation.

## API Endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Login and receive JWT | Public |
| POST | `/api/orders` | Create a new order | Required |
| GET | `/api/orders/{id}` | Get order details | Required |
| POST | `/api/payments/create/{orderId}` | Create a Razorpay payment order | Required |
| POST | `/api/payments/verify` | Verify payment signature and capture | Required |
| POST | `/api/refunds` | Initiate a partial/full refund | Required |
| POST | `/api/webhook/razorpay` | Razorpay webhook listener | Signature-verified |

## Running Tests

```bash
./mvnw test
```

Tests cover the payment service logic including order validation and signature verification failure handling. CI runs the full suite automatically on every push via GitHub Actions.

## Key Design Decisions

- **Idempotency keys on orders** prevent duplicate order creation from network retries or double-clicks — a common source of double-charging in real payment systems.
- **Stateless JWT auth** avoids server-side session storage, making the API horizontally scalable.
- **Webhook signature verification** ensures payment status updates only come from Razorpay, not spoofed requests.
- **Scheduled reconciliation** runs independently of the request/response cycle, mirroring how real settlement systems reconcile transactions in batch.

## Roadmap

- [ ] Subscription billing support
- [ ] Multi-currency settlement
- [ ] Admin dashboard for transaction monitoring

## License

This project is for educational and portfolio purposes.
