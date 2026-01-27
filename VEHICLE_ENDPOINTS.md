# Vehicle endpoints (public + admin)

This file documents request and response shapes for endpoints defined in:
- `src/main/java/brama/pressing_api/vehicle/VehiclePublicController.java`
- `src/main/java/brama/pressing_api/vehicle/VehicleAdminController.java`

## Enums

VehicleCategory:
- ECONOMY, COMPACT, SEDAN, SUV, VAN, TRUCK, LUXURY, ELECTRIC

TransmissionType:
- AUTOMATIC, MANUAL

FuelType:
- GASOLINE, DIESEL, HYBRID, ELECTRIC

VehicleStatus:
- AVAILABLE, RESERVED, RENTED, MAINTENANCE, INACTIVE

## Response model: VehicleResponse

Fields (JSON):
- id: string
- make: string
- model: string
- year: integer
- trim: string
- category: VehicleCategory
- transmission: TransmissionType
- fuelType: FuelType
- seats: integer
- doors: integer
- luggageCapacity: integer
- color: string
- licensePlate: string
- vin: string
- locationId: string
- dailyRate: number (decimal)
- weeklyRate: number (decimal)
- monthlyRate: number (decimal)
- deposit: number (decimal)
- mileageLimitPerDay: integer
- status: VehicleStatus
- description: string
- features: string[]
- images: string[]
- ratingAverage: number
- ratingCount: integer
- createdDate: string (ISO-8601 date-time)
- lastModifiedDate: string (ISO-8601 date-time)

## Pagination response (Spring Data Page)

Endpoints returning `Page<VehicleResponse>` serialize a standard Spring Data page:
```json
{
  "content": [ { "...VehicleResponse..." } ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": false, "unsorted": true, "empty": true },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false, "unsorted": true, "empty": true },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

## Public endpoints

### GET /api/v1/public/vehicles

Query parameters:
- locationId: string (optional)
- startDate: string (optional, format `YYYY-MM-DD`)
- endDate: string (optional, format `YYYY-MM-DD`)
- category: VehicleCategory (optional)
- transmission: TransmissionType (optional)
- fuelType: FuelType (optional)
- minSeats: integer (optional)
- minPrice: number (optional, decimal)
- maxPrice: number (optional, decimal)
- page: integer (optional, zero-based)
- size: integer (optional)
- sort: string (optional, repeatable, e.g. `sort=createdDate,desc`)

Response: `200 OK` with `Page<VehicleResponse>`.

Example request:
```
GET /api/v1/public/vehicles?locationId=LOC1&startDate=2026-02-01&endDate=2026-02-05&category=SUV&page=0&size=10
```

Example response (trimmed):
```json
{
  "content": [
    {
      "id": "veh_1",
      "make": "BMW",
      "model": "X5",
      "year": 2023,
      "category": "SUV",
      "transmission": "AUTOMATIC",
      "fuelType": "GASOLINE",
      "seats": 5,
      "doors": 5,
      "locationId": "LOC1",
      "dailyRate": 150.0,
      "status": "AVAILABLE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

### GET /api/v1/public/vehicles/{id}

Path parameters:
- id: string (required)

Response: `200 OK` with `VehicleResponse`.

Example response (trimmed):
```json
{
  "id": "veh_1",
  "make": "BMW",
  "model": "X5",
  "year": 2023,
  "category": "SUV",
  "transmission": "AUTOMATIC",
  "fuelType": "GASOLINE",
  "seats": 5,
  "doors": 5,
  "dailyRate": 150.0,
  "status": "AVAILABLE"
}
```

## Admin endpoints (requires role ADMIN)

### GET /api/v1/admin/vehicles

Query parameters:
- page: integer (optional, zero-based)
- size: integer (optional)
- sort: string (optional, repeatable, e.g. `sort=createdDate,desc`)

Response: `200 OK` with `Page<VehicleResponse>`.

### GET /api/v1/admin/vehicles/{id}

Path parameters:
- id: string (required)

Response: `200 OK` with `VehicleResponse`.

### POST /api/v1/admin/vehicles

Request body: `CreateVehicleRequest`
- make: string (required, not blank)
- model: string (required, not blank)
- year: integer (required, positive)
- trim: string (optional)
- category: VehicleCategory (required)
- transmission: TransmissionType (required)
- fuelType: FuelType (required)
- seats: integer (required, positive)
- doors: integer (required, positive)
- luggageCapacity: integer (optional, >= 0)
- color: string (optional)
- licensePlate: string (optional)
- vin: string (optional)
- locationId: string (required, not blank)
- dailyRate: number (required, positive)
- weeklyRate: number (optional, positive)
- monthlyRate: number (optional, positive)
- deposit: number (optional, >= 0)
- mileageLimitPerDay: integer (optional, >= 0)
- status: VehicleStatus (required)
- description: string (optional)
- features: string[] (optional)
- images: string[] (optional)

Response: `201 Created` with `VehicleResponse`.

Example request:
```json
{
  "make": "Toyota",
  "model": "Corolla",
  "year": 2022,
  "category": "SEDAN",
  "transmission": "AUTOMATIC",
  "fuelType": "GASOLINE",
  "seats": 5,
  "doors": 4,
  "locationId": "LOC1",
  "dailyRate": 65.0,
  "status": "AVAILABLE",
  "features": ["Bluetooth", "Backup camera"],
  "images": ["https://cdn.example.com/cars/corolla-1.jpg"]
}
```

Example response (trimmed):
```json
{
  "id": "veh_2",
  "make": "Toyota",
  "model": "Corolla",
  "year": 2022,
  "category": "SEDAN",
  "dailyRate": 65.0,
  "status": "AVAILABLE"
}
```

### PUT /api/v1/admin/vehicles/{id}

Path parameters:
- id: string (required)

Request body: `UpdateVehicleRequest` (all fields optional)
- make: string
- model: string
- year: integer (positive if provided)
- trim: string
- category: VehicleCategory
- transmission: TransmissionType
- fuelType: FuelType
- seats: integer (positive if provided)
- doors: integer (positive if provided)
- luggageCapacity: integer (>= 0 if provided)
- color: string
- licensePlate: string
- vin: string
- locationId: string
- dailyRate: number (positive if provided)
- weeklyRate: number (positive if provided)
- monthlyRate: number (positive if provided)
- deposit: number (>= 0 if provided)
- mileageLimitPerDay: integer (>= 0 if provided)
- status: VehicleStatus
- description: string
- features: string[]
- images: string[]

Response: `200 OK` with `VehicleResponse`.

### DELETE /api/v1/admin/vehicles/{id}

Path parameters:
- id: string (required)

Response: `204 No Content`.
