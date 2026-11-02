# app-skeleton-vertx-postgres

A lightweight, reactive skeleton exploring **Vert.x 5**, **SmallRye Mutiny**, **PostgreSQL**, and **Vert.x gRPC services** in Kotlin.

> **Note on Architecture:** While a standard, production-ready path for Kotlin gRPC applications typically relies on **Kotlin Coroutines + `grpc-kotlin` (`grpckt`)**, this project explores an alternative flavor. 
> Using **SmallRye Mutiny** is an opinionated choice for reactive pipelines, requiring you to bridge Mutiny streams (`Multi`) into the core Vert.x `Future` and `ReadStream` contracts natively expected by the Vert.x gRPC code generator.

---

## Getting Started

### 1. Spin up PostgreSQL via Docker
Run a local instance of PostgreSQL with matching credentials:

```bash
docker compose up -d 
```

### 2. Start the Application Locally

Build and run the application using Gradle:

```bash
./gradlew run

```

---

## Interacting with the gRPC Service using `grpcurl`

Once your application is running, you can use [`grpcurl`](https://github.com/fullstorydev/grpcurl) to explore and invoke your reactive gRPC endpoints.

> *Note: By default, the Vert.x gRPC server listens on port `9000` (adjust if you configured a custom port).*

### List Available Services

Verify that reflection is enabled and see your registered services:

```bash
grpcurl -plaintext localhost:9000 list

```

### Describe the BowService

Inspect the methods and message structures for your gRPC service:

```bash
grpcurl -plaintext localhost:9000 describe BowService

```

### Call the `ListBow` Stream

Execute a request to stream records back from PostgreSQL through the non-blocking cursor:

```bash
grpcurl -plaintext -d '{}' localhost:9000 BowService/ListBow

```