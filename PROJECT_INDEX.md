# Project Index

## Scope Read
- Files sampled: `pom.xml`, `src/main/resources/application.yml`, `src/main/resources/application-dev.yml`, `src/main/java/brama/pressing_api/PressingApiApplication.java`, `src/main/java/brama/pressing_api/security/SecurityPaths.java`, `DOCUMENTATION.md` (header), plus directory listings.
- Java files: 287 under `src/main/java`; test files: 1 under `src/test/java`.
- Main stack: Java 17, Spring Boot 3.5.4, MongoDB, Spring Security + JWT (RSA), WebSocket/STOMP, Stripe, Cloudinary, Firebase, Thymeleaf mail templates.
- Entrypoint: `src/main/java/brama/pressing_api/PressingApiApplication.java`.

## Project Layout
- Feature packages live under `src/main/java/brama/pressing_api/*` with controller/service/repository layers per module.
- `src/main/resources/` includes:
  - `application.yml`, `application-dev.yml`, `application-prod.yml` (empty)
  - `keys/local-only/*.pem` RSA key pair for JWT
  - `firebase-service-account.json`
  - `templates/` email + OTP HTML templates
- Top-level docs: `DOCUMENTATION.md`, `VEHICLE_ENDPOINTS.md`, `read.md`, `readpaymed.md`
- `docs/` directory exists for additional material.

## Largest Feature Packages (Java File Counts)
- chat 34
- circuit 30
- booking 29
- payment 20
- excursion 20
- vehicle 17
- excursionbooking 16
- promotion 12
- review 12
- user 11
- notification 11
- location 10
- auth 8
- config 8
- seed 7
- otp 6
- security 5
- token 5
- firebase 4
- admin 4
- upload 4
- exception 3
- validation 2
- email 2
- handler 2
- role 2
- common 1
- utils 1

## Security / Auth
- Public URL allowlist in `security/SecurityPaths.java` includes auth, public, OTP, Stripe webhook, WebSocket, and Swagger endpoints.
- JWT access/refresh TTL configured in `application.yml`, RSA keys stored under `src/main/resources/keys/local-only`.
- Admin user auto-seed in `PressingApiApplication.java` using `app.seed.admin.*` properties.

## WebSocket
- Config in `config/WebSocketConfig.java` with STOMP + SockJS.
- Auth via `chat/websocket/WebSocketAuthChannelInterceptor.java`.
- Chat WS controller `chat/websocket/ChatWebSocketController.java`.

## Payments / Uploads
- Stripe integration in `payment/stripe/StripePaymentService.java` with public webhook endpoint.
- Cloudinary config and upload size limits in `application.yml`.

## Hotspots (Largest Classes)
- `src/main/java/brama/pressing_api/chat/service/impl/ChatServiceImpl.java` (~539 lines)
- `src/main/java/brama/pressing_api/booking/service/impl/BookingServiceImpl.java` (~495 lines)
- `src/main/java/brama/pressing_api/excursionbooking/service/impl/ExcursionBookingServiceImpl.java` (~302 lines)
- `src/main/java/brama/pressing_api/payment/stripe/StripePaymentService.java` (~279 lines)
- `src/main/java/brama/pressing_api/payment/service/impl/PaymentServiceImpl.java` (~258 lines)
- `src/main/java/brama/pressing_api/circuit/service/impl/CircuitBookingServiceImpl.java` (~224 lines)
- `src/main/java/brama/pressing_api/vehicle/service/impl/VehicleServiceImpl.java` (~208 lines)
- `src/main/java/brama/pressing_api/token/TokenService.java` (~203 lines)

## Notable Repo Characteristics
- Local-only secrets are committed: `src/main/resources/firebase-service-account.json`, `src/main/resources/keys/local-only/*.pem`.
- Backup files present: `*.yml~`, `*.html~`.
