# Push Notifications

## Overview
Meer uses FCM (Firebase Cloud Messaging) for delivery and Notifee on the client for
foreground display. The backend stores device tokens per user/device/environment and
sends:

- **Direct device pushes** (admin, for testing or QA).
- **User-level pushes** (admin, fan-out to all devices for a user).
- **Topic broadcasts** (admin, respects user preferences via topic subscriptions).

All pushes include a `notification` payload (for background delivery) **and** a
`data` payload for routing.

## Client Registration (Token Upsert)
**Endpoint:** `POST /push-tokens`

Body:
```json
{
  "deviceId": "device-uuid-or-stable-id",
  "fcmToken": "FCM_DEVICE_TOKEN",
  "platform": "ANDROID|IOS",
  "appVersion": "1.2.3",
  "environment": "DEV|STAGING|PROD"
}
```

Notes:
- Call on app start and on FCM token refresh.
- `deviceId` should be stable per device installation.
- `environment` should match the Firebase app used by the client.

**Delete on logout:** `DELETE /push-tokens/{deviceId}?environment=DEV|STAGING|PROD`

## Topic Strategy (Preferences)
We use **topics** to respect user preferences:

- `notifyPromos` -> topic `promos-<env>`
- `notifyNewStores` -> topic `new_stores-<env>`

Where `<env>` is `dev`, `staging`, or `prod` (lowercase).

**Example subscriptions (client):**
- `promos-dev`
- `new_stores-prod`

When a user disables a preference, unsubscribe from the matching topic.

## Admin Send Endpoints
All admin endpoints require a dashboard admin token.

### 1) Send to a single device token
**Endpoint:** `POST /dashboard/push`
```json
{
  "token": "FCM_DEVICE_TOKEN",
  "title": "Novo guia",
  "body": "Confira agora",
  "type": "guide_content",
  "id": "123"
}
```

### 2) Send to all devices for a user
**Endpoint:** `POST /dashboard/push/user`
```json
{
  "userId": "<uuid>",
  "environment": "DEV",
  "title": "Novo guia",
  "body": "Confira agora",
  "type": "guide_content",
  "id": "123"
}
```

### 3) Broadcast by audience (topic)
**Endpoint:** `POST /dashboard/push/broadcast`
```json
{
  "environment": "DEV",
  "audience": "promos",
  "title": "Promoção",
  "body": "Tem desconto novo",
  "type": "store",
  "id": "abc"
}
```

Valid `audience` values:
- `promos`
- `new_stores`

## Payload Schema (Routing)
The mobile app routes based on `data.type` and `data.id`:

- `type: "guide_content"` -> `contentDetail({ contentId: id })`
- `type: "store"` -> `thriftDetail({ id })`

Example FCM message:
```json
{
  "message": {
    "token": "FCM_DEVICE_TOKEN",
    "notification": { "title": "Novo guia", "body": "Confira agora" },
    "data": { "type": "guide_content", "id": "123" }
  }
}
```

## Android Channel
Android notifications are sent with channel id **`default`**. The client must
create the same channel in Notifee at startup.

## Error Handling
If FCM returns `UNREGISTERED` or `NOT_FOUND`, the backend deletes the stored token.

## Security & Secrets
- Service account JSON is **server-only** and must never be shipped to the app.
- Configure the backend with `FIREBASE_ENABLED=true` and credentials via env vars.
