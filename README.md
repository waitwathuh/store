# Store Application

The **Store** application is a Spring Boot service that keeps track of **customers, orders and products** in a PostgreSQL database. To minimise database round‑trips in production it uses **Redis** as a second‑level cache.

---

## Table of contents

1. [Prerequisites](#prerequisites)
2. [Getting started](#getting-started)
3. [Environment variables](#environment-variables)
4. [Running the application](#running-the-application)
5. [Data model](#data-model)
6. [REST API](#rest-api)
7. [Performance / caching](#performance--caching)
8. [Development tasks](#development-tasks)

---

## Prerequisites

| Component      | Version                        | Notes                                    |
| -------------- | ------------------------------ | ---------------------------------------- |
| **Java**       | 17                             | Compile & run the app                    |
| **Gradle**     | Wrapper included (`./gradlew`) |                                          |
| **PostgreSQL** | 16.2                           | Exposed on **localhost:5433**            |
| **Redis**      | ≥ 7.0                          | Exposed on **localhost:6379**            |
| **Docker**     | Optional                       | Quickest way to spin up Postgres & Redis |

### Spin‑up the infrastructure with Docker

```bash
# PostgreSQL (non‑standard port 5433, logical decoding enabled for Liquibase):
docker run -d \
  --name postgres \
  --restart always \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=store \
  -v postgres:/var/lib/postgresql/data \
  -p 5433:5432 \
  postgres:16.2 \
  postgres -c wal_level=logical

# Redis (persistent volume, default port):
docker run -d \
  --name redis \
  --restart always \
  -v redis-data:/data \
  -p 6379:6379 \
  redis:7-alpine
```

> **Tip:** If Docker isn’t available, you can run both services natively and keep the same ports.

---

## Getting started

Clone the repo and ***clean‑build*** to avoid stale class files:

```bash
git clone <repo-url>
cd store-app
./gradlew clean build
```

---

## Environment variables

The app can be customised without editing `application.yml`:

| Variable                 | Default     | Description                  |
| ------------------------ | ----------- | ---------------------------- |
| `DB_HOST`                | `localhost` | PostgreSQL host              |
| `DB_PORT`                | `5433`      | PostgreSQL port              |
| `DB_USERNAME`            | `admin`     |                              |
| `DB_PASSWORD`            | `admin`     |                              |
| `REDIS_HOST`             | `localhost` |                              |
| `REDIS_PORT`             | `6379`      |                              |
| `SPRING_PROFILES_ACTIVE` |             | Attach extra config profiles |

---

## Running the application

### Local development

```bash
./gradlew bootRun
```

### Packaged jar

```bash
./gradlew bootJar
java -jar build/libs/store-*.jar
```

### Docker image (CI friendly)

A multi‑stage Dockerfile is included. Build & run:

```bash
docker build -t store-app:latest .
docker run --rm -p 8080:8080 --network host store-app:latest
```

Liquibase migrates the schema on start‑up and seeds sample data; more data can be generated via the scripts in `utils/`.

---

## Data model

- **Customer** ←→ **Order** (1 : n)
- **Order** ←→ **Product** (n : n)

Circular references are broken in the DTO layer:

- `CustomerDTO` contains a list of **order IDs**
- `OrderDTO` contains a list of **product IDs** & a truncated `OrderCustomerDTO`

---

## REST API

All endpoints are JSON and documented in `OpenAPI.yaml`.

| Resource | Verb | Path             | Description     |
| -------- | ---- | ---------------- | --------------- |
| Customer | GET  | `/customer`      | All customers   |
|          | GET  | `/customer/{id}` | Customer by id  |
|          | POST | `/customer`      | Create customer |
| Order    | GET  | `/order`         | All orders      |
|          | GET  | `/order/{id}`    | Order by id     |
|          | POST | `/order`         | Create order    |
| Product  | GET  | `/products`      | All products    |
|          | GET  | `/products/{id}` | Product by id   |
|          | POST | `/products`      | Create product  |

---

## Performance / caching

- The app enables **Spring Cache** backed by **Redis**.
- Entries live for **10 minutes** (`spring.cache.redis.time‑to‑live`).
- Keys are plain strings; values are stored as **JSON** via `GenericJackson2JsonRedisSerializer`
- Flush the cache if the DTO structure changes:
  ```bash
  redis-cli FLUSHALL
  ```
- You can disable caching for local debugging:
  ```bash
  SPRING_CACHE_TYPE=none ./gradlew bootRun
  ```

---

## CI / CD pipeline

An example **GitHub Actions** workflow (`.github/workflows/ci.yml`) is provided. It

1. Checks‑out the code.
2. Sets up **Java 17** & Gradle.
3. Runs unit tests: `./gradlew test`.
4. Builds the multi‑stage Docker image defined in the project `Dockerfile`.
5. Pushes the image to the container registry (Docker Hub or GHCR) when the branch is **main**.   Secrets required: `REGISTRY`, `REGISTRY_USERNAME`, `REGISTRY_PASSWORD`.

```yaml
name: CI

on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Cache Gradle
        uses: gradle/gradle-build-action@v3

      - name: Test & build jar
        run: ./gradlew clean build

      - name: Build Docker image
        run: |
          docker build -t ${{ secrets.REGISTRY }}/store-app:${{ github.sha }} .

      - name: Log in to registry
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.REGISTRY_USERNAME }}
          password: ${{ secrets.REGISTRY_PASSWORD }}

      - name: Push image
        run: docker push ${{ secrets.REGISTRY }}/store-app:${{ github.sha }}
```

Adapt the workflow to GitLab CI, CircleCI, or Jenkins if preferred.

---

## Development tasks

The original assignment is kept for reference. ✅ marks the parts that have already been implemented in this code‑base.

1. ✅ Extend **order** endpoint to find an order by ID.
2. ✅ Extend **customer** endpoint to search by name substring.
3. ✅ Optimise latency with Redis caching (see above).
4. ✅ Add **/products** endpoint and link orders ↔ products.
5. ✅ CI pipeline builds & pushes Docker image (see above).

