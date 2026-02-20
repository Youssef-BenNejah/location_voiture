# Notifications Integration Guide (Frontend)

## Purpose
This document explains the notification system added in the backend:
- where each notification is triggered
- who receives it (admin/client)
- which channel is used (socket, push, or both)
- exact `type` values to handle in frontend code
- payload shape and recommended UX behavior

Use this as the source of truth for notification handling in web/mobile clients.

---

## 1) Architecture Overview

### Core notification domain
- `src/main/java/brama/pressing_api/notification/domain/NotificationImportance.java`
- `src/main/java/brama/pressing_api/notification/domain/NotificationChannel.java`
- `src/main/java/brama/pressing_api/notification/dto/NotificationRequest.java`
- `src/main/java/brama/pressing_api/notification/dto/SocketNotificationPayload.java`
- `src/main/java/brama/pressing_api/notification/service/NotificationService.java`
- `src/main/java/brama/pressing_api/notification/service/impl/NotificationServiceImpl.java`

### Generic service methods
- `notifyUser(String userId, NotificationRequest request)`
- `notifyUsers(Collection<String> userIds, NotificationRequest request)`
- `notifyAdmins(NotificationRequest request)`

### Admin resolution
`notifyAdmins(...)` finds users by role:
- `findByRolesContaining("ADMIN")`
- `findByRolesContaining("ROLE_ADMIN")`

From:
- `src/main/java/brama/pressing_api/user/UserRepository.java`

---

## 2) Channel Routing Logic

Defined in:
- `src/main/java/brama/pressing_api/notification/service/impl/NotificationServiceImpl.java`

If request channel is `AUTO` (or null), backend chooses:
- `LOW` or `NORMAL` importance -> `SOCKET`
- `HIGH` or `CRITICAL` importance -> `BOTH` (socket + push)

### Socket destination
- `/user/queue/notifications`

### Push destination
- FCM topic: `user-{userId}`

Example:
- user id `abc123` -> push topic `user-abc123`

---

## 3) Socket Payload Shape

The frontend receives this payload on `/user/queue/notifications`:

```json
{
  "type": "BOOKING_CREATED",
  "title": "New booking created",
  "body": "A client created booking 123",
  "importance": "HIGH",
  "data": {
    "bookingId": "123"
  },
  "timestamp": "2026-02-20T18:10:20.123"
}
```

Source DTO:
- `src/main/java/brama/pressing_api/notification/dto/SocketNotificationPayload.java`

---

## 4) Push Subscription Endpoints

Controller:
- `src/main/java/brama/pressing_api/firebase/controller/PushNotificationController.java`

### Existing endpoints
- `POST /api/v1/firebase-notifications/send-one`
- `POST /api/v1/firebase-notifications/send-many`
- `POST /api/v1/firebase-notifications/send-topic`
- `POST /api/v1/firebase-notifications/subscribe-topic`

### Added user-topic endpoints (for this notification system)
- `POST /api/v1/firebase-notifications/subscribe-user?token={fcmToken}&userId={userId}`
- `POST /api/v1/firebase-notifications/subscribe-user-many?userId={userId}`

For `subscribe-user-many`, body is:
```json
["tokenA", "tokenB"]
```

Frontend rule:
- whenever user logs in on a device and obtains FCM token, subscribe token to `user-{userId}` topic.
- repeat on token refresh.

---

## 5) Complete Event Catalog

This section lists every emitted notification `type` currently in backend.

## Legend
- Recipient: `Admin`, `Client`, or `Both`
- Channel:
- `Socket` = realtime websocket only
- `Both` = websocket + push (importance HIGH/CRITICAL)

### 5.1 Auth events

#### `USER_REGISTERED`
- File: `src/main/java/brama/pressing_api/auth/impl/AuthenticationServiceImpl.java`
- Trigger: user registration success
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `userId`

#### `EMAIL_VERIFIED`
- File: `src/main/java/brama/pressing_api/auth/impl/AuthenticationServiceImpl.java`
- Trigger: email verification success
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `userId`

#### `USER_EMAIL_VERIFIED`
- File: `src/main/java/brama/pressing_api/auth/impl/AuthenticationServiceImpl.java`
- Trigger: email verification success
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `userId`

#### `PASSWORD_RESET`
- File: `src/main/java/brama/pressing_api/auth/impl/AuthenticationServiceImpl.java`
- Trigger: reset password success
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `userId`

---

### 5.2 User/account events

#### `PROFILE_UPDATED`
- File: `src/main/java/brama/pressing_api/user/impl/UserServiceImpl.java`
- Trigger: profile update success
- Recipient: Client
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `userId`

#### `PASSWORD_CHANGED`
- File: `src/main/java/brama/pressing_api/user/impl/UserServiceImpl.java`
- Trigger: change password success
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `userId`

#### `ACCOUNT_DEACTIVATED`
- File: `src/main/java/brama/pressing_api/user/impl/UserServiceImpl.java`
- Trigger: account deactivation
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `userId`

#### `ACCOUNT_REACTIVATED`
- File: `src/main/java/brama/pressing_api/user/impl/UserServiceImpl.java`
- Trigger: account reactivation
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `userId`

#### `ACCOUNT_BANNED`
- File: `src/main/java/brama/pressing_api/user/impl/UserServiceImpl.java`
- Trigger: admin bans user
- Recipient: Client
- Importance: `CRITICAL`
- Channel: Both
- Data keys: `userId`

#### `ACCOUNT_UNBANNED`
- File: `src/main/java/brama/pressing_api/user/impl/UserServiceImpl.java`
- Trigger: admin unbans user
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `userId`

---

### 5.3 Booking events (car bookings)

#### `BOOKING_CREATED`
- File: `src/main/java/brama/pressing_api/booking/service/impl/BookingServiceImpl.java`
- Trigger: client creates booking
- Recipient: Admin
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`

#### `BOOKING_CANCELLED`
- File: `src/main/java/brama/pressing_api/booking/service/impl/BookingServiceImpl.java`
- Trigger: client cancels own booking
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `bookingId`

#### `BOOKING_CREATED_BY_ADMIN`
- File: `src/main/java/brama/pressing_api/booking/service/impl/BookingServiceImpl.java`
- Trigger: admin creates booking for client
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`

#### `BOOKING_PAYMENT_RECORDED`
- File: `src/main/java/brama/pressing_api/booking/service/impl/BookingServiceImpl.java`
- Trigger: admin records manual payment
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`

#### `BOOKING_STATUS_UPDATED`
- File: `src/main/java/brama/pressing_api/booking/service/impl/BookingServiceImpl.java`
- Trigger: admin updates booking status
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`, `status`

---

### 5.4 Payment events

#### `PAYMENT_STATUS_UPDATED`
- File: `src/main/java/brama/pressing_api/payment/service/impl/PaymentServiceImpl.java`
- Trigger: payment status update for booking or excursion booking
- Recipient: Client
- Importance: `HIGH`
- Channel: Both
- Data keys (booking payment): `paymentId`, `bookingId`, `status`
- Data keys (excursion payment): `paymentId`, `excursionBookingId`, `status`

#### `PAYMENT_STATUS_UPDATED_ADMIN`
- File: `src/main/java/brama/pressing_api/payment/service/impl/PaymentServiceImpl.java`
- Trigger: payment status update for booking or excursion booking
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys:
- booking payment -> `paymentId`, `bookingId`, `status`
- excursion payment -> `paymentId`, `excursionBookingId`, `status`

---

### 5.5 Excursion booking events

#### `EXCURSION_BOOKING_CREATED`
- File: `src/main/java/brama/pressing_api/excursionbooking/service/impl/ExcursionBookingServiceImpl.java`
- Trigger: client creates excursion booking
- Recipient: Admin
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`, `excursionId`

#### `EXCURSION_BOOKING_CANCELLED`
- File: `src/main/java/brama/pressing_api/excursionbooking/service/impl/ExcursionBookingServiceImpl.java`
- Trigger: client cancels excursion booking
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `bookingId`, `excursionId`

#### `EXCURSION_BOOKING_STATUS_UPDATED`
- File: `src/main/java/brama/pressing_api/excursionbooking/service/impl/ExcursionBookingServiceImpl.java`
- Trigger: admin updates excursion booking status
- Recipient: Client (if booking has userId)
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`, `status`

---

### 5.6 Circuit booking events

#### `CIRCUIT_BOOKING_CREATED`
- File: `src/main/java/brama/pressing_api/circuit/service/impl/CircuitBookingServiceImpl.java`
- Trigger: client creates circuit booking
- Recipient: Admin
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`, `circuitId`

#### `CIRCUIT_BOOKING_CANCELLED`
- File: `src/main/java/brama/pressing_api/circuit/service/impl/CircuitBookingServiceImpl.java`
- Trigger: client cancels own circuit booking
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `bookingId`, `circuitId`

#### `CIRCUIT_BOOKING_STATUS_UPDATED`
- File: `src/main/java/brama/pressing_api/circuit/service/impl/CircuitBookingServiceImpl.java`
- Trigger: admin updates circuit booking status
- Recipient: Client (if booking has userId)
- Importance: `HIGH`
- Channel: Both
- Data keys: `bookingId`, `status`

---

### 5.7 Review events

#### `REVIEW_CREATED`
- File: `src/main/java/brama/pressing_api/review/service/impl/ReviewServiceImpl.java`
- Trigger: client submits review
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `reviewId`, `vehicleId`

#### `REVIEW_STATUS_UPDATED`
- File: `src/main/java/brama/pressing_api/review/service/impl/ReviewServiceImpl.java`
- Trigger: admin approves/rejects review
- Recipient: Client
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `reviewId`, `status`

---

### 5.8 Promotion events

#### `PROMOTION_CREATED`
- File: `src/main/java/brama/pressing_api/promotion/service/impl/PromotionServiceImpl.java`
- Trigger: admin creates promotion
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `promotionId`, `code`

#### `PROMOTION_UPDATED`
- File: `src/main/java/brama/pressing_api/promotion/service/impl/PromotionServiceImpl.java`
- Trigger: admin updates promotion
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `promotionId`, `code`

#### `PROMOTION_DELETED`
- File: `src/main/java/brama/pressing_api/promotion/service/impl/PromotionServiceImpl.java`
- Trigger: admin deletes promotion
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `promotionId`, `code`

---

### 5.9 Location events

#### `LOCATION_CREATED`
- File: `src/main/java/brama/pressing_api/location/service/impl/LocationServiceImpl.java`
- Trigger: admin creates location
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `locationId`, `code`

#### `LOCATION_UPDATED`
- File: `src/main/java/brama/pressing_api/location/service/impl/LocationServiceImpl.java`
- Trigger: admin updates location
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `locationId`, `code`

#### `LOCATION_DELETED`
- File: `src/main/java/brama/pressing_api/location/service/impl/LocationServiceImpl.java`
- Trigger: admin deletes location
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `locationId`, `code`

---

### 5.10 Vehicle events

#### `VEHICLE_CREATED`
- File: `src/main/java/brama/pressing_api/vehicle/service/impl/VehicleServiceImpl.java`
- Trigger: admin creates vehicle
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `vehicleId`

#### `VEHICLE_UPDATED`
- File: `src/main/java/brama/pressing_api/vehicle/service/impl/VehicleServiceImpl.java`
- Trigger: admin updates vehicle
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `vehicleId`

#### `VEHICLE_DELETED`
- File: `src/main/java/brama/pressing_api/vehicle/service/impl/VehicleServiceImpl.java`
- Trigger: admin deletes vehicle
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `vehicleId`

#### `VEHICLE_MEDIA_UPDATED`
- File: `src/main/java/brama/pressing_api/vehicle/service/impl/VehicleServiceImpl.java`
- Trigger: admin uploads media for vehicle
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `vehicleId`

---

### 5.11 Excursion catalog events

#### `EXCURSION_CREATED`
- File: `src/main/java/brama/pressing_api/excursion/service/impl/ExcursionServiceImpl.java`
- Trigger: admin creates excursion
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `excursionId`

#### `EXCURSION_UPDATED`
- File: `src/main/java/brama/pressing_api/excursion/service/impl/ExcursionServiceImpl.java`
- Trigger: admin updates excursion
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `excursionId`

#### `EXCURSION_DELETED`
- File: `src/main/java/brama/pressing_api/excursion/service/impl/ExcursionServiceImpl.java`
- Trigger: admin deletes excursion
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `excursionId`

#### `EXCURSION_VISIBILITY_UPDATED`
- File: `src/main/java/brama/pressing_api/excursion/service/impl/ExcursionServiceImpl.java`
- Trigger: admin enables/disables excursion
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `excursionId`, `enabled`

#### `EXCURSION_MEDIA_UPDATED`
- File: `src/main/java/brama/pressing_api/excursion/service/impl/ExcursionServiceImpl.java`
- Trigger: admin uploads excursion images
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `excursionId`

---

### 5.12 Circuit catalog events

#### `CIRCUIT_CREATED`
- File: `src/main/java/brama/pressing_api/circuit/service/impl/CircuitServiceImpl.java`
- Trigger: admin creates circuit
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `circuitId`

#### `CIRCUIT_UPDATED`
- File: `src/main/java/brama/pressing_api/circuit/service/impl/CircuitServiceImpl.java`
- Trigger: admin updates circuit
- Recipient: Admin
- Importance: `LOW`
- Channel: Socket
- Data keys: `circuitId`

#### `CIRCUIT_DELETED`
- File: `src/main/java/brama/pressing_api/circuit/service/impl/CircuitServiceImpl.java`
- Trigger: admin deletes circuit
- Recipient: Admin
- Importance: `NORMAL`
- Channel: Socket
- Data keys: `circuitId`

---

## 6) Frontend Integration Checklist

### 6.1 Socket connection
- connect to websocket endpoint `/ws`
- authenticate with JWT in STOMP headers (already required by backend interceptor)
- subscribe to:
- `/user/queue/notifications`

### 6.2 Push setup
- obtain FCM token for current device
- call:
- `POST /api/v1/firebase-notifications/subscribe-user?token={token}&userId={userId}`
- on token refresh, call subscribe again

### 6.3 Notification center model
At minimum, store:
- `type`
- `title`
- `body`
- `importance`
- `timestamp`
- `data` object

### 6.4 Routing by type
Use `type` as primary switch. Example:
- booking detail screen for `BOOKING_*`
- payment detail for `PAYMENT_STATUS_UPDATED*`
- moderation screens for `REVIEW_CREATED`
- user security banners for `PASSWORD_CHANGED`, `ACCOUNT_BANNED`, etc.

### 6.5 Priority UX suggestion
- `LOW`: silent in-app list update
- `NORMAL`: toast + list update
- `HIGH`: toast + badge + optional modal
- `CRITICAL`: blocking banner/modal + persist until acknowledged

---

## 7) Example Frontend Handler (Pseudo-code)

```ts
type NotificationPayload = {
  type: string;
  title: string;
  body: string;
  importance: "LOW" | "NORMAL" | "HIGH" | "CRITICAL";
  data?: Record<string, string>;
  timestamp: string;
};

function onNotification(n: NotificationPayload) {
  store.add(n);

  switch (n.type) {
    case "BOOKING_STATUS_UPDATED":
      if (n.data?.bookingId) invalidateBooking(n.data.bookingId);
      break;
    case "PAYMENT_STATUS_UPDATED":
      invalidatePayments();
      break;
    case "ACCOUNT_BANNED":
      forceSecurityModal(n.body);
      break;
    default:
      break;
  }

  if (n.importance === "CRITICAL") {
    showBlockingAlert(n.title, n.body);
  } else if (n.importance === "HIGH") {
    showToast(n.title, n.body);
  }
}
```

---

## 8) Notes and Constraints

- Not every admin-only event currently needs push; most are `LOW/NORMAL` and socket-only.
- Client-impacting events are mostly `HIGH` and reach both socket + push.
- Some client notifications depend on `userId` existing on booking records:
- excursion/circuit status update notifications only call `notifyUser(...)` when `userId` is present.
- Push delivery uses topic-based routing (`user-{userId}`), not raw token storage in backend domain logic.

---

## 9) Quick Reference: All Event Types

`USER_REGISTERED`  
`EMAIL_VERIFIED`  
`USER_EMAIL_VERIFIED`  
`PASSWORD_RESET`  
`PROFILE_UPDATED`  
`PASSWORD_CHANGED`  
`ACCOUNT_DEACTIVATED`  
`ACCOUNT_REACTIVATED`  
`ACCOUNT_BANNED`  
`ACCOUNT_UNBANNED`  
`BOOKING_CREATED`  
`BOOKING_CANCELLED`  
`BOOKING_CREATED_BY_ADMIN`  
`BOOKING_PAYMENT_RECORDED`  
`BOOKING_STATUS_UPDATED`  
`PAYMENT_STATUS_UPDATED`  
`PAYMENT_STATUS_UPDATED_ADMIN`  
`EXCURSION_BOOKING_CREATED`  
`EXCURSION_BOOKING_CANCELLED`  
`EXCURSION_BOOKING_STATUS_UPDATED`  
`CIRCUIT_BOOKING_CREATED`  
`CIRCUIT_BOOKING_CANCELLED`  
`CIRCUIT_BOOKING_STATUS_UPDATED`  
`REVIEW_CREATED`  
`REVIEW_STATUS_UPDATED`  
`PROMOTION_CREATED`  
`PROMOTION_UPDATED`  
`PROMOTION_DELETED`  
`LOCATION_CREATED`  
`LOCATION_UPDATED`  
`LOCATION_DELETED`  
`VEHICLE_CREATED`  
`VEHICLE_UPDATED`  
`VEHICLE_DELETED`  
`VEHICLE_MEDIA_UPDATED`  
`EXCURSION_CREATED`  
`EXCURSION_UPDATED`  
`EXCURSION_DELETED`  
`EXCURSION_VISIBILITY_UPDATED`  
`EXCURSION_MEDIA_UPDATED`  
`CIRCUIT_CREATED`  
`CIRCUIT_UPDATED`  
`CIRCUIT_DELETED`

