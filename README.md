# CarbonTrack Backend

Spring Boot 3.5.6 + Java 17 + MySQL backend for the CarbonTrack use case.

## Core use cases covered

1. Log daily activities: car commute, bus commute, electricity, waste.
2. Calculate monthly carbon footprint from fixed emission factors.
3. Create community challenges with a target reduction percentage.
4. Join challenges and view participation/progress.
5. Produce a monthly leaderboard based on each household's reduction versus its own previous month.

## Fixed emission factors

These are documented assignment assumptions and are intentionally fixed in code.

| Activity type | Quantity unit used by API | Factor |
|---|---|---:|
| CAR_COMMUTE | km | 0.21 kg CO2e / km |
| BUS_COMMUTE | km | 0.08 kg CO2e / km |
| ELECTRICITY | kWh | 0.82 kg CO2e / kWh |
| WASTE | kg | 0.50 kg CO2e / kg |

Example: 10 km of `CAR_COMMUTE` = 10 × 0.21 = 2.10 kg CO2e.

## Tools

- VS Code: development
- Java JDK 17: Java runtime/compiler
- Maven: build/dependencies
- MySQL Server: database
- MySQL Workbench: database inspection
- Postman: API testing (optional but included through a collection)
- Git/GitHub: version control
- Web browser: quick GET testing

## Run in VS Code

Open the folder that contains `pom.xml`:

```powershell
cd carbontrack
mvn clean spring-boot:run
```

The API runs at:

`http://localhost:8080`

Before starting, edit:

`src/main/resources/application.properties`

and replace:

`YOUR_MYSQL_PASSWORD`

with your MySQL root password.

The application uses:

`jdbc:mysql://localhost:3306/carbontrack?createDatabaseIfNotExist=true&serverTimezone=UTC`

If the database does not exist, Hibernate/MySQL can create the database because `createDatabaseIfNotExist=true` is enabled. Hibernate creates/updates the tables.

## Initial browser checks

With the server running and an empty database:

- `GET http://localhost:8080/api/households` -> `[]`
- `GET http://localhost:8080/api/challenges` -> `[]`
- `GET http://localhost:8080/api/leaderboard?month=2026-09` -> `[]`

These empty arrays are expected before records are created.

## API endpoints

### Households
- `POST /api/households`
- `GET /api/households`
- `GET /api/households/{id}`
- `PUT /api/households/{id}`
- `DELETE /api/households/{id}`

Create body:
```json
{
  "name": "Green Family",
  "membersCount": 4
}
```

### Activities
- `POST /api/households/{householdId}/activities`
- `GET /api/households/{householdId}/activities`
- `GET /api/households/{householdId}/footprint?month=YYYY-MM`

Activity body:
```json
{
  "activityDate": "2026-09-28",
  "activityType": "CAR_COMMUTE",
  "quantity": 10
}
```

Valid activity types:
`CAR_COMMUTE`, `BUS_COMMUTE`, `ELECTRICITY`, `WASTE`.

### Challenges
- `POST /api/challenges`
- `GET /api/challenges`
- `GET /api/challenges/{id}`
- `PUT /api/challenges/{id}`
- `DELETE /api/challenges/{id}`

Create body:
```json
{
  "name": "September Reduction",
  "description": "Reduce monthly footprint.",
  "targetReductionPercentage": 10,
  "startDate": "2026-09-01",
  "endDate": "2026-09-30"
}
```

### Participation and progress
- `POST /api/challenges/{challengeId}/join/{householdId}`
- `GET /api/households/{householdId}/challenges`
- `GET /api/challenges/{challengeId}/participants`

The participation response includes previous/current month footprint, reduction percentage, target percentage, and whether the target is achieved.

### Leaderboard
- `GET /api/leaderboard?month=YYYY-MM`

Ranking is based on percentage reduction from the household's own previous month. It does not compare absolute footprint values as the ranking metric.

## Why GET endpoints return []

Collection endpoints intentionally return JSON arrays. When there are no records, Spring returns `[]`, not an error.

Examples:
```text
GET /api/households       -> []
GET /api/challenges       -> []
GET /api/leaderboard?...  -> []
```

## Postman

Import:

`postman/CarbonTrack.postman_collection.json`

The collection contains requests for the complete flow.

## Validation and business rules

- Household name is required.
- Members count must be positive.
- Activity date/type/quantity are required; quantity must be positive.
- Challenge target must be between 0.01 and 100.
- Challenge start date cannot be after end date.
- A household cannot join the same challenge twice.
- A household can join only while the challenge is active.
- Missing household/challenge returns HTTP 404 with a clear JSON message.
- Validation/business-rule failures return HTTP 400 with JSON errors.
- REST responses use DTOs so JPA relationships do not create recursive JSON serialization.

## Suggested test order

1. `GET /api/households` -> `[]`
2. Create household
3. `GET /api/households` -> one object
4. Log activities
5. Check monthly footprint
6. Create challenge
7. Join challenge
8. Check participation/progress
9. Check leaderboard

## MySQL Workbench

```sql
CREATE DATABASE IF NOT EXISTS carbontrack;
USE carbontrack;
SHOW TABLES;
```

Expected tables after the Spring Boot application starts:

- `households`
- `activity_logs`
- `challenges`
- `participations`

