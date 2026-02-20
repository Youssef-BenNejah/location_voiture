# Project Index

## Scope Read
- Text files read: 308
- Main stack: Java 17, Spring Boot 3.5.4, MongoDB, Spring Security JWT (RSA), WebSocket/STOMP, Stripe, Cloudinary, Firebase
- Entrypoint: `src/main/java/brama/pressing_api/PressingApiApplication.java`

## High-Level Architecture
- Layering: `controller -> service -> repository -> MongoDB`
- Shared cross-cutting:
- `security/` for JWT auth + path allowlist
- `handler/` for global exception mapping
- `config/` for CORS, OpenAPI, beans, websocket, cloudinary, pricing
- `token/` for refresh-token metadata + OTP lifecycle

## Main Modules (by code volume)
- `chat` (34 files): conversations/messages, receipts, typing, presence, websocket auth
- `circuit` (30 files): circuit catalog + bookings + admin stats
- `booking` (29 files): car rental booking, pricing, admin ops, CSV export
- `excursion` (20 files): excursion catalog/admin
- `payment` (20 files): manual + Stripe payment flows for bookings/excursion bookings
- `vehicle` (17 files): public discovery + admin CRUD/media
- `excursionbooking` (16 files): public booking + client/admin management + CSV/email

## Security/Auth Map
- JWT required for all protected routes via `JwtFilter`
- Public routes declared in `security/SecurityPaths.java`
- Security chain in `security/SecurityConfig.java`:
- stateless sessions
- CSRF disabled
- CORS enabled
- JWT filter before username/password filter
- Login/register/refresh/reset in `auth/`
- OTP generation/verification/resend in `otp/` using `token/TokenService`

## API Surface (major groups)
- Auth: `/api/v1/auth/*`
- OTP: `/api/v1/otp/*`
- Public catalogs: `/api/v1/public/{vehicles,locations,promotions,reviews,excursions,circuits}`
- User profile/admin-user ops: `/api/v1/users/*`
- Car bookings: `/api/v1/bookings/*`, `/api/v1/admin/bookings/*`
- Payments: `/api/v1/payments/*`, `/api/v1/admin/payments/*`, Stripe `/api/v1/payments/stripe/*`
- Excursion bookings: `/api/v1/excursions/bookings/*`, `/api/v1/admin/excursion-bookings/*`, public create path in controller
- Circuit bookings: `/api/v1/circuits/bookings/*`, `/api/v1/admin/circuit-bookings/*`, public create path in controller
- Chat REST: `/api/v1/chat/*`
- WebSocket endpoint: `/ws` with app destinations `/app/chat.*`

## Core Business Flows
- Auth:
- login -> authenticate -> issue access+refresh JWT -> persist refresh metadata
- refresh -> validate refresh token record + JWT claims -> issue new access token
- OTP:
- rate-limit per hour, max attempts, revoke prior OTP for same purpose, optional email verification update
- Car booking:
- validate date range + vehicle availability -> compute pricing -> create pending booking
- admin can record manual payments and status transitions, export CSV
- Payments:
- manual create/update status in `PaymentServiceImpl`
- Stripe intent/confirm/webhook in `StripePaymentService` + controller
- updates linked booking/excursion booking payment/status state
- Chat:
- only admin<->client conversations
- REST + websocket send/edit/delete/read/delivered/typing
- unread counters and receipt state progression maintained in service

## Data/Query Patterns
- Mongo repositories + custom `*RepositoryImpl` using dynamic `Criteria`:
- booking, vehicle, excursion, excursion booking, circuit, circuit booking
- Fuzzy search fields mostly implemented via case-insensitive regex

## Hotspots (largest classes)
- `chat/service/impl/ChatServiceImpl.java` (~539 lines)
- `booking/service/impl/BookingServiceImpl.java` (~454 lines)
- `payment/stripe/StripePaymentService.java` (~279 lines)
- `excursionbooking/service/impl/ExcursionBookingServiceImpl.java` (~272 lines)
- `payment/service/impl/PaymentServiceImpl.java` (~222 lines)
- `token/TokenService.java` (~203 lines)
- `circuit/service/impl/CircuitBookingServiceImpl.java` (~195 lines)

## Notable Repo Characteristics
- Backup/edit-temp files exist in source tree (`*.java~`, `*.yml~`, `*.html~`)
- `src/main/resources/application.yml` includes fallback Stripe test keys (should be env-managed in production)
- Single basic test file: `src/test/java/brama/pressing_api/PressingApiApplicationTests.java`

## Files For Rapid Onboarding
- `pom.xml`
- `src/main/resources/application.yml`
- `src/main/java/brama/pressing_api/security/SecurityConfig.java`
- `src/main/java/brama/pressing_api/security/JwtFilter.java`
- `src/main/java/brama/pressing_api/auth/impl/AuthenticationServiceImpl.java`
- `src/main/java/brama/pressing_api/token/TokenService.java`
- `src/main/java/brama/pressing_api/booking/service/impl/BookingServiceImpl.java`
- `src/main/java/brama/pressing_api/payment/service/impl/PaymentServiceImpl.java`
- `src/main/java/brama/pressing_api/payment/stripe/StripePaymentService.java`
- `src/main/java/brama/pressing_api/chat/service/impl/ChatServiceImpl.java`
