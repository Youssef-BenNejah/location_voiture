# Chat Logic

This file documents how chat works in this API based on the current code.

## Entry Points
REST controller: `src/main/java/brama/pressing_api/chat/ChatController.java`
WebSocket controller: `src/main/java/brama/pressing_api/chat/websocket/ChatWebSocketController.java`
WebSocket config: `src/main/java/brama/pressing_api/config/WebSocketConfig.java`
Core service: `src/main/java/brama/pressing_api/chat/service/impl/ChatServiceImpl.java`

## REST Endpoints
Base path: `/api/v1/chat`
Auth: JWT required (see `SecurityConfig` and `JwtFilter`)

Endpoints:
- `POST /api/v1/chat/conversations`
- `GET /api/v1/chat/conversations`
- `GET /api/v1/chat/conversations/{id}/messages`
- `POST /api/v1/chat/messages`
- `PATCH /api/v1/chat/messages/{id}`
- `DELETE /api/v1/chat/messages/{id}`
- `POST /api/v1/chat/conversations/{id}/read`
- `POST /api/v1/chat/conversations/{id}/delivered`

Notes:
- REST principal is `User` from Spring Security. The controller uses the principal ID as the chat user ID.
- `listMessages` returns messages ordered by `created_date` descending.

## WebSocket (STOMP)
Endpoint: `/ws` (with SockJS enabled)
Broker destinations:
- App prefix: `/app`
- User prefix: `/user`
- Topics: `/topic`, `/queue`

Message mappings:
- `/app/chat.send`
- `/app/chat.typing`
- `/app/chat.read`
- `/app/chat.delivered`

Client subscriptions:
- `/user/queue/messages`
- `/user/queue/typing`
- `/user/queue/receipts`
- `/topic/presence`

Auth:
- The `WebSocketAuthChannelInterceptor` expects a token in STOMP headers `Authorization`, `authorization`, or `token`.
- Supports `Bearer <jwt>` or raw JWT.
- The authenticated principal name is the user ID (see `ChatPrincipal`).

## Data Model
Collections and key fields:
- `conversations`
- `participant_ids` (list of user IDs)
- `last_message_id`, `last_message_sender_id`, `last_message_preview`, `last_message_at`
- `unread_counts` (map userId -> count)
- `chat_messages`
- `conversation_id`, `sender_id`, `recipient_id`, `type`, `content`, `attachments`
- `receipts` (map userId -> { status, timestamp })
- `edited_at`, `deleted`, `deleted_at`, `deleted_by`
- `user_presence`
- `user_id`, `online`, `last_seen_at`, `last_active_at`

## Access Rules
Enforced in `ChatServiceImpl`:
- A conversation can only exist between a client and an admin.
- A non-admin cannot start a conversation with another non-admin.
- A non-admin can only send messages to admins.
- Participants are verified for every conversation read or message send.

## Send Message Flow
Implemented in `ChatServiceImpl.sendMessage`:
1. Validate request has content or attachments.
2. Resolve conversation (by `conversationId`) or create one (by `recipientId`).
3. Resolve recipient ID from request or the conversation.
4. Build message type from content/attachments if not provided.
5. Create receipts map:
   - Sender: `READ`
   - Recipient: `SENT`
6. Save message, update conversation preview, and increment unread count for recipient.
7. Broadcast message to both users on `/user/queue/messages`.
8. If recipient is offline, trigger push notification (currently no-op).

## Edit/Delete Rules
- Only the sender can edit or delete a message.
- Deleted messages are soft-deleted:
  - `deleted = true`
  - `content = null`
  - `attachments = []`
  - `deleted_at`, `deleted_by` are set
- If the deleted or edited message is the last message, conversation preview is updated.

## Receipts and Read/Delivered
- `markConversationRead` sets recipient receipts to `READ`, resets unread count, and notifies the other user on `/user/queue/receipts`.
- `markConversationDelivered` sets recipient receipts to `DELIVERED` and notifies the other user on `/user/queue/receipts`.
- Receipt transitions are monotonic: `SENT -> DELIVERED -> READ`.

## Typing Events
- `sendTyping` emits a `ChatTypingEvent` to the other participant at `/user/queue/typing`.

## Presence
Presence is driven by WebSocket connect/disconnect events:
- On connect: `markOnline` and emit `/topic/presence`.
- On disconnect: `markOffline` and emit `/topic/presence`.
- `touch` is called when a message is sent to update activity timestamps.

## Push Notifications
`PushNotificationService` is injected into `ChatServiceImpl`.
Default implementation is `NoopPushNotificationService`, which logs and does not send.
