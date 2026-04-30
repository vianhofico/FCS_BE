# Backend API Endpoint Matrix

## Catalog Module
- `GET /api/v1/catalog/categories`
- `GET /api/v1/catalog/categories/{id}`
- `POST /api/v1/catalog/categories`
- `PUT /api/v1/catalog/categories/{id}`
- `DELETE /api/v1/catalog/categories/{id}`
- `GET /api/v1/catalog/brands`
- `GET /api/v1/catalog/brands/{id}`
- `POST /api/v1/catalog/brands`
- `PUT /api/v1/catalog/brands/{id}`
- `DELETE /api/v1/catalog/brands/{id}`
- `GET /api/v1/catalog/settings`
- `PUT /api/v1/catalog/settings/{id}`

## Product Module
- `GET /api/v1/products`
- `GET /api/v1/products/{id}`
- `POST /api/v1/products`
- `PUT /api/v1/products/{id}`
- `DELETE /api/v1/products/{id}`
- `PATCH /api/v1/products/{id}/status`

## Consignment Module
- `GET /api/v1/consignments`
- `GET /api/v1/consignments/{id}`
- `POST /api/v1/consignments`
- `PUT /api/v1/consignments/{id}`
- `DELETE /api/v1/consignments/{id}`
- `PATCH /api/v1/consignments/{id}/status`

## Order Module
- `GET /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `POST /api/v1/orders`
- `PATCH /api/v1/orders/{id}/status`
- `DELETE /api/v1/orders/{id}`

## IAM Module
- `POST /api/v1/iam/auth/token/preview`
- `GET /api/v1/iam/users/{id}`

## Financial Module
- `GET /api/v1/financial/wallets`
- `GET /api/v1/financial/wallets/{id}`
- `PUT /api/v1/financial/wallets/{id}`

## Notification Module
- `GET /api/v1/notifications`
- `PATCH /api/v1/notifications/{id}/read`

## Audit Module
- `GET /api/v1/audit/activity-logs`
