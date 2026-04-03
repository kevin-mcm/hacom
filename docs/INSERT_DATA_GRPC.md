# Manual end-to-end test (gRPC + persistence + verification)

This manual test exercises the full project flow:
1) start MongoDB (Docker),
2) run the application,
3) insert an order via gRPC (`InsertOrder`),
4) verify the result (via the gRPC response and a REST status query).

## Prerequisites
- Java 21
- Docker + Docker Compose
- `grpcurl` installed
- (Optional) `jq` for pretty-printing JSON

## 1) Start MongoDB
```bash
docker-compose up -d mongodb
```

## 2) Run the application
In a terminal:
```bash
./gradlew bootRun
```

The app exposes:
- REST: `http://localhost:9898`
- gRPC: `localhost:9090`

## 3) Insert an order via gRPC (InsertOrder)

In another terminal, from the repo root:

```bash
grpcurl -plaintext \
  -import-path src/main/proto \
  -proto order_service.proto \
  -d '{
    "orderId": "ORD-1001",
    "customerId": "CUST-77",
    "customerPhoneNumber": "+573001112233",
    "items": ["Plan 5GB", "Roaming Add-on", "SMS Bundle"]
  }' \
  localhost:9090 telco.OrderService/InsertOrder
```

Expected response (example):
```json
{
  "orderId": "ORD-1001",
  "status": "PROCESSED"
}
```

> Note: in the `.proto` file the fields are `order_id`, `customer_id`, etc.  
> With `grpcurl` you can send them in `camelCase` (`orderId`) like in this example.

## 4) Verify the order status via REST
(Extra verification for the end-to-end flow: confirm the order was stored and/or processed)

```bash
curl -s http://localhost:9898/orders/ORD-1001/status | jq .
```

Expected output (example):
```json
{
  "orderId": "ORD-1001",
  "status": "PROCESSED"
}
```

## 5) (Optional) Check health / metrics
Health:
```bash
curl -s http://localhost:9898/actuator/health | jq .
```

Prometheus metrics:
```bash
curl -s http://localhost:9898/actuator/prometheus | head
```

## Quick troubleshooting
- If `grpcurl` can’t connect: confirm gRPC port is `9090` (`grpc.port` in `application.yml`).
- If REST doesn’t respond: confirm API port is `9898` (`apiPort`).
- If MongoDB fails: ensure the container is running (`docker ps`) and `mongodbUri` points to `127.0.0.1:27017`.
