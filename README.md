# Retail Discount Service

Calculates the net payable amount for a set of purchased products, applying the store's discount rules.

## Quick start

Requires Docker. Everything runs from one command:

```bash
docker compose up -d --build
```

This starts PostgreSQL, MongoDB, and the application on port 8080. Schema migrations and demo data are applied automatically on first start.

### Demo tokens

Signed with the development secret committed in `application.yaml`. They grant nothing outside a local container and are included so the rules can be checked quickly. Regenerate with `TestTokens` (in `src/test/java/.../shared/security/`).

**`emp-001`** — employee, 30%

```
eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4MTk4NjA4MDMsInN1YiI6ImVtcC0wMDEiLCJpYXQiOjE3ODgzMjQ4MDN9.9qh4NCktz2LqXBw3rCsR3-dbSrwfuWM_OhRw9niSxPI
```

**`aff-001`** — affiliate, 10%

```
eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4MTk4NjA4MDMsInN1YiI6ImFmZi0wMDEiLCJpYXQiOjE3ODgzMjQ4MDN9.6cuyMqQOs6atgPY1RZbJB2ocME_1pd0ngcDW1d9-3v0
```

**`loyal-001`** — customer since 2021, 5%

```
eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4MTk4NjA4MDMsInN1YiI6ImxveWFsLTAwMSIsImlhdCI6MTc4ODMyNDgwM30.5xwlgKhG7_I0xcvGHhjdZWbF8Yo8i0M2psSAJO-NBew
```

**`new-001`** — recent customer, no percentage discount

```
eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4MTk4NjA4MDMsInN1YiI6Im5ldy0wMDEiLCJpYXQiOjE3ODgzMjQ4MDN9.Iae5jiMi2QPpZX63HD_0jIEPbjJw6yI5r5T0iTiOt3w
```

### Try it

The same request for each customer — an $890 laptop plus $100 of coffee:

```bash
TOKEN='<paste a token from above>'

curl -s -X POST http://localhost:8080/api/v1/discounts/calculate \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"lines":[{"productId":"laptop-01","quantity":1},{"productId":"coffee-01","quantity":1}]}'
```

| Customer | Gross | Percentage | Flat | Net payable |
|---|---|---|---|---|
| `emp-001` | 990.00 | 267.00 | 45.00 | **678.00** |
| `aff-001` | 990.00 | 89.00 | 45.00 | 856.00 |
| `loyal-001` | 990.00 | 44.50 | 45.00 | 900.50 |
| `new-001` | 990.00 | 0.00 | 45.00 | 945.00 |

The percentage discount is always calculated on the $890 of electronics, never the $100 of groceries (rule 5). The flat discount is the same $45 for everyone, since nine complete hundreds fit in the $990 bill (rule 4).

The `emp-001` row is the worked example from the specification.

## Build and test

```bash
./mvnw clean verify                   # build, test, generate coverage, enforce threshold
open target/site/jacoco/index.html    # coverage report
```

Tests require Docker — integration tests use Testcontainers for PostgreSQL and MongoDB.

Current coverage: **98% instructions, 92% branches**. The build fails below 90% / 85%.

## Static analysis

SonarQube quality gate: **Passed**. 96.4% coverage, 0% duplication, no
reliability issues.

![SonarQube report](docs/sonarqube-report.png)

Run it locally with `docker compose up -d sonarqube`, then:

    ./mvnw clean verify sonar:sonar \
      -Dsonar.projectKey=retail-discount-service \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=<your-token> \
      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

The one security finding is Sonar flagging `csrf.disable()`. It does not apply
here: the service is stateless and authenticates only via Bearer tokens, so
there is no cookie or session for a cross-site request to exploit.

## The formula

```
grossTotal      = Σ (unitPrice × quantity)
nonGroceryTotal = Σ non-grocery lines
pctDiscount     = max(applicable percentage rate) × nonGroceryTotal
flatDiscount    = floor(grossTotal / 100) × $5
netPayable      = max(0, grossTotal − pctDiscount − flatDiscount)
```

## Assumptions

The requirements leave several things open. Each decision below is isolated to one place in the code.

| # | Ambiguity | Decision | Reasoning |
|---|---|---|---|
| 1 | Which percentage discount applies when several are eligible? | The largest one | Rule 6 says only one applies but not which. Selecting by amount rather than hard-coded precedence means a new rate or tier needs no change to the engine. |
| 2 | Can a customer hold more than one type? | Yes — types are a `Set` | Nothing forbids being both an employee and an affiliate. Rule 6 then decides which discount wins. |
| 3 | Does the $5-per-$100 rule apply to groceries? | Yes | Rule 5 restricts only *percentage* discounts. Excluding groceries here would add a constraint the requirements don't state. |
| 4 | Is the $5-per-$100 rule calculated before or after the percentage discount? | Before — on the gross total | The specification's own example ($990 → $45) is computed from the bill total, not a discounted one. |
| 5 | "Customer for over 2 years" — inclusive? | Strictly greater | "Over" means strictly greater. At exactly two years the customer does not qualify. Changing this is a one-line change in `LoyaltyDiscountPolicy` (`isBefore` → `!isAfter`). |
| 6 | Measured against what date? | The bill date, carried on the `Bill` | A stored bill recalculates to the same result later. The date is set once at the service boundary from an injected `Clock`, which also makes the boundary testable. |
| 7 | Rounding | `BigDecimal`, scale 2, HALF_UP | Enforced by the `Money` value object, so it cannot be forgotten anywhere. Money is never a `double`; prices are stored as `Decimal128` in MongoDB and `NUMERIC` in PostgreSQL. |
| 8 | Can the net payable go negative? | No — floored at zero | `Money.minus` cannot produce a negative amount. |
| 9 | Currency and tax | Single currency, no tax | Neither is mentioned in the requirements. |

## Class diagram

![Class diagram](docs/class-diagram.png)

Source: [`docs/class-diagram.puml`](docs/class-diagram.puml)

## Design decisions

**Discount rules use the strategy pattern.** Each rule is a separate `DiscountPolicy` implementation. The engine collects every applicable percentage discount and takes the largest (rule 6), then adds every flat discount. Adding a new discount means adding a class — the engine is not modified. Two marker interfaces, `PercentageDiscountPolicy` and `FlatDiscountPolicy`, let Spring inject the two families as separate lists so the distinction is made by the type system rather than a runtime check.

**Discount rates are configuration, not code.** Rates live in the `discount_rates` table and are loaded at startup, so a rate change does not need a release. Eligibility stays in code because it differs per rule — loyalty is derived from a date, the others from stored customer types. Rates are cached at startup; a production system would add a TTL or an eviction endpoint.

**Prices come from the catalog, never the request.** The request carries only product IDs and quantities. Prices and categories are resolved server-side, so a client cannot send its own prices. Products are fetched in a single batch query per request.

**The customer comes from the token, never the request body.** Identity is read from the validated JWT subject via a `@CurrentCustomerId` annotation, then looked up in this service's own store. Putting customer type in the token would make discount eligibility a property of token issuance and let it go stale.

**The service validates every token itself.** It is a stateless OAuth2 resource server. Token issuance is out of scope and would be handled by an identity provider such as Keycloak. Validation is not delegated to a gateway, so the service stays safe if network isolation is breached.

**Polyglot persistence.** Customers are relational — a stable schema with types and join dates that are queried and joined — so they live in PostgreSQL with Flyway-managed migrations and `ddl-auto: validate`. The product catalog is document-shaped, since attributes vary by category, so it lives in MongoDB. The calculation reads from both and writes to neither, so no distributed transaction is needed.

**Packages are organised by feature, not by layer.** Each feature — `billing`,
`catalog`, `customer` — owns its own `domain` and `infrastructure` subpackages,
rather than the codebase being split into `controller`, `service`, `repository`.
This keeps a change to one feature inside one package, and it lets implementation
classes stay package-private: nothing outside `catalog.infrastructure.mongo` can
reference `ProductDocument`, so the boundary is enforced by the compiler rather
than by convention.

**The domain has no framework dependencies.** `Money`, `Bill`, `Customer`, `Product` and every policy are plain Java. Persistence types (`CustomerEntity`, `ProductDocument`) are separate and mapped at the boundary, which is why the discount logic can be tested without Spring or a database.

**Scope.** This service calculates discounts only. Bills are owned and persisted by the calling order service; nothing is stored here. Guest checkout is out of scope — every request is authenticated and the customer must exist. Rule 4 is customer-independent, so a guest flow would receive the flat discount and no percentage discount, which an anonymous customer with no types would produce without changing any policy.

## Testing approach

- **Domain and policies** are pure functions and are tested without mocks. Each policy is tested in isolation, including the boundaries: exactly two years, exactly $100, an all-grocery bill.
- **`SpecificationExampleTest`** wires the real policies together and asserts each ambiguous rule above, including the specification's own $990 example.
- **`DiscountEngineTest`** uses Mockito to verify coordination rather than arithmetic — that the largest percentage discount is selected, that flat discounts stack, and that a policy which does not apply is never asked for an amount.
- **`BillCalculationServiceTest`** mocks the two repositories and captures the `Bill` handed to the engine, proving prices come from the catalog rather than the request.
- **Integration tests** run against real PostgreSQL and MongoDB via Testcontainers, covering the entity mappings and confirming decimal precision survives the round trip.

## API

`POST /api/v1/discounts/calculate` — requires `Authorization: Bearer <token>`.

Errors are returned as `application/problem+json`: 400 for validation failures, 401 for a missing or invalid token, 404 for an unknown customer, 422 for an unknown product.

## Demo data

Seeded on first start. Customers: `emp-001` (employee), `aff-001` (affiliate), `loyal-001` (joined 2021, qualifies on tenure), `new-001` (no discount). Products include `laptop-01` ($890, non-grocery) and `coffee-01` ($100, grocery), which together reproduce the worked example.

Demo data exists for review convenience; a production catalog would be managed externally.

## Notes

- Built on Spring Boot 4.1. Note that MongoDB connection properties moved from `spring.data.mongodb.*` to `spring.mongodb.*` in Boot 4 — the old keys are silently ignored.
- The requirements list both Hibernate JPA and MongoDB. These serve different stores here, as described above, rather than one replacing the other.
