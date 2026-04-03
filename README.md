# Telco Order Processing Service

[![CI](https://github.com/kevin-mcm/hacom/actions/workflows/ci.yml/badge.svg)](https://github.com/kevin-mcm/hacom/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/kevin-mcm/hacom/branch/master/graph/badge.svg)](https://app.codecov.io/gh/kevin-mcm/hacom)

A Spring Boot 3.x microservice for telco order processing with hexagonal architecture.

## Architecture

Hexagonal Architecture (Ports and Adapters):
- **domain/**: Core business model and port interfaces
- **application/**: Use cases, services, and Akka actors
- **infrastructure/**: Adapters for gRPC, MongoDB, SMPP, and REST

## Technology Stack

- **Java 21** + **Spring Boot 3.4.4**
- **Spring WebFlux** (reactive REST API on port 9898)
- **Spring Data MongoDB Reactive** (database: exampleDb)
- **gRPC** (port 9090) - order insertion
- **Akka Classic Actors** - order processing
- **SMPP (cloudhopper)** - SMS notifications
- **Log4j2** - logging (log4j2.yml)
- **Spring Actuator** + **Prometheus** - metrics

## Prerequisites

- Java 21
- Docker & Docker Compose (for MongoDB)
- Gradle 8.8+

## Quick Start

1. Start MongoDB:
```bash
docker-compose up -d mongodb
```

2. Build the project:
```bash
./gradlew build
```

3. Run the application:
```bash
./gradlew bootRun
```

## Inserting test data (local environment)

See the full guide at [`docs/INSERT_DATA_REST.md`](docs/INSERT_DATA_REST.md).

### Development REST endpoint (not available with `prod` profile)

```bash
curl -s -X POST http://localhost:9898/orders/test/seed | jq .
```

---

## API Endpoints

### REST (port 9898)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/orders/{orderId}/status` | Get order status |
| GET | `/orders/count?from=...&to=...` | Count orders in date range |
| POST | `/orders/test/seed` | **[DEV only]** Insert sample orders |
| GET | `/actuator/prometheus` | Prometheus metrics |
| GET | `/actuator/health` | Health check |

### gRPC (port 9090)
See the full guide at [`docs/INSERT_DATA_GRPC.md`](docs/INSERT_DATA_GRPC.md).

Service: `telco.OrderService`

```protobuf
rpc InsertOrder (OrderRequest) returns (OrderResponse);
```

Request fields: `order_id`, `customer_id`, `customer_phone_number`, `items[]`

## Configuration

Key properties in `application.yml`:

```yaml
mongodbDatabase: exampleDb
mongodbUri: "mongodb://127.0.0.1:27017"
apiPort: 9898
grpc:
  port: 9090
smpp:
  host: localhost
  port: 2775
```

## Running Tests

```bash
./gradlew test
```

## Test Coverage

Generate the HTML + XML coverage report with JaCoCo (runs automatically after `test`):

```bash
./gradlew test jacocoTestReport
```

The HTML report is generated at `build/reports/jacoco/test/html/index.html` and the XML report at `build/reports/jacoco/test/jacocoTestReport.xml`.

Coverage scope includes:
- `application/service/OrderService`
- `application/actor/OrderProcessorActor`
- `infrastructure/adapter/web/OrderController`
- `infrastructure/adapter/mongodb/MongoOrderRepository`

> Generated gRPC stubs (`com.hacom.telco.grpc.*`) and the main application class are excluded from coverage metrics.

## Docker Compose

```bash
# Start MongoDB only
docker-compose up -d mongodb

# Start all services
docker-compose up -d
```

## Metrics

Available at `/actuator/prometheus`:
- `orders_processed_total` - counter for processed orders
