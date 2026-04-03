# Inserting Test Data Locally

> **LOCAL / DEVELOPMENT ONLY** — Do not run in production.

This guide explains how to populate the local MongoDB database with sample orders
to make development and testing of the microservice easier.

---

## Prerequisites

- The application running locally (any profile other than `prod`).

To start the application:

```bash
./gradlew bootRun
```

---

## Development REST Endpoint (`POST /orders/test/seed`)

> Available **only** when the application is **not** running with the `prod` profile.

Once the application is running, you can insert the same sample orders by
calling the REST endpoint:

```bash
curl -s -X POST http://localhost:9898/orders/test/seed | jq .
```

The endpoint returns the list of inserted orders as JSON:

```json
[
  {
    "orderId": "ORD-SEED-001",
    "customerId": "CUST-001",
    "customerPhoneNumber": "+52-55-1234-5678",
    "status": "PENDING",
    "items": ["Plan 5GB", "Roaming Add-on"],
    "ts": "2024-01-15T10:00:00Z"
  },
  ...
]
```

---

## Verifying the inserted data

You can confirm the insertion with `mongosh`:

```bash
mongosh "mongodb://127.0.0.1:27017/exampleDb" --eval 'db.orders.find().pretty()'
```

Or by querying the status of one of the orders via the REST API:

```bash
curl http://localhost:9898/orders/ORD-SEED-001/status
```

---

## Cleaning up test data

To remove only the sample orders (prefix `ORD-SEED-`):

```bash
mongosh "mongodb://127.0.0.1:27017/exampleDb" --eval \
  'db.orders.deleteMany({ orderId: /^ORD-SEED-/ }); print("Seed data removed.")'
```

To drop the **entire** collection:

```bash
mongosh "mongodb://127.0.0.1:27017/exampleDb" --eval \
  'db.orders.drop(); print("Collection dropped.")'
```

