# Payment Requests and Responses

This file documents the request/response shapes updated to support both car bookings and excursion bookings.

## Create Payment (REST)
Endpoint: `POST /api/v1/payments`

Rules:
- Provide exactly one of `bookingId` or `excursionBookingId`.
- `provider` and `method` are required.

Request body:
```json
{
  "bookingId": "string",
  "excursionBookingId": "string",
  "provider": "STRIPE | PAYPAL | CASH | BANK_TRANSFER | MANUAL",
  "method": "CARD | CASH | BANK_TRANSFER | WALLET | CHECK"
}
```

Response body (`PaymentResponse`):
```json
{
  "id": "string",
  "bookingId": "string",
  "excursionBookingId": "string",
  "userId": "string",
  "amount": 0,
  "currency": "string",
  "provider": "STRIPE | PAYPAL | CASH | BANK_TRANSFER | MANUAL",
  "method": "CARD | CASH | BANK_TRANSFER | WALLET | CHECK",
  "status": "PENDING | PAID | FAILED | REFUNDED",
  "transactionId": "string",
  "paidAt": "2026-02-04T12:00:00",
  "createdDate": "2026-02-04T12:00:00",
  "lastModifiedDate": "2026-02-04T12:00:00"
}
```

## Create Stripe Payment Intent
Endpoint: `POST /api/v1/payments/stripe/intent`

Rules:
- Provide exactly one of `bookingId` or `excursionBookingId`.

Request body:
```json
{
  "bookingId": "string",
  "excursionBookingId": "string"
}
```

Response body (`StripePaymentIntentResponse`):
```json
{
  "paymentId": "string",
  "bookingId": "string",
  "excursionBookingId": "string",
  "paymentIntentId": "string",
  "clientSecret": "string",
  "amount": 0,
  "currency": "string"
}
```

## Confirm Stripe Payment
Endpoint: `POST /api/v1/payments/stripe/confirm`

Request body:
```json
{
  "paymentIntentId": "string"
}
```

Response body (`StripePaymentIntentResponse`):
```json
{
  "paymentId": "string",
  "bookingId": "string",
  "excursionBookingId": "string",
  "paymentIntentId": "string",
  "clientSecret": "string",
  "amount": 0,
  "currency": "string"
}
```
