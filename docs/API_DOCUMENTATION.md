# Fashion Consignment System (FCS) - API Documentation

## Overview
This document provides an overview of the REST APIs available in the FCS Backend. The backend is built with Spring Boot 3.3.5, Java 21, and MySQL. It follows a modular architecture and provides full support for consignment lifecycle, product listing, ordering, and financial transactions.

## Base URL
All API endpoints are prefixed with:
`/api/v1`

### Authentication & Security
The system uses JWT (JSON Web Tokens) for authentication.
For protected endpoints, include the following HTTP Header:
`Authorization: Bearer <your_access_token>`

Roles are mapped into the JWT, and some endpoints require the `ROLE_ADMIN` authority.

---

## 1. Identity and Access Management (IAM)

### Authentication
- `POST /api/v1/auth/register`: Register a new user account. Auto-generates a user Wallet.
- `POST /api/v1/auth/login`: Authenticate with username/email and password. Returns access and refresh tokens.
- `POST /api/v1/auth/refresh`: Refresh an expired access token using a valid refresh token.
- `POST /api/v1/auth/logout`: Revoke all refresh tokens for the logged-in user.

### Users & RBAC
- `GET /api/v1/iam/users`: Fetch all users.
- `GET /api/v1/iam/users/{id}`: Fetch user by ID.
- `PUT /api/v1/iam/users/{id}/status`: Update user status (ACTIVE, BANNED, INACTIVE).
- `POST /api/v1/iam/users/{id}/roles`: Assign roles to a user.
- `DELETE /api/v1/iam/users/{userId}/roles/{roleId}`: Remove a role from a user.

### Roles and Permissions (Admin Only)
- `GET /api/v1/iam/roles`: List all roles.
- `POST /api/v1/iam/roles`: Create a new role.
- `PUT /api/v1/iam/roles/{id}`: Update role.
- `POST /api/v1/iam/roles/{id}/permissions`: Assign permissions to a role.
- `DELETE /api/v1/iam/roles/{id}`: Delete role.

### User Addresses
- `GET /api/v1/iam/users/{userId}/addresses`: Get all shipping addresses for a user.
- `POST /api/v1/iam/users/{userId}/addresses`: Create a new address (supports marking as default).
- `PUT /api/v1/iam/users/addresses/{id}`: Update an address.
- `DELETE /api/v1/iam/users/addresses/{id}`: Delete an address.

---

## 2. Catalog Module

### Categories & Brands
- `GET /api/v1/categories` & `GET /api/v1/brands`: List categories and brands.
- `POST`, `PUT`, `DELETE` methods available for managing categories and brands.
- `GET /api/v1/system-settings`: Key-Value configuration.

---

## 3. Consignment Module

### Consignment Requests
- `POST /api/v1/consignments`: Submit a request to consign clothes.
- `GET /api/v1/consignments`: List requests for the logged-in user or all (for admins).
- `GET /api/v1/consignments/{id}`: Request details.
- `PATCH /api/v1/consignments/{id}/status`: Approve or reject a request.

### Consignment Items
- `POST /api/v1/consignments/items`: Add an item to an approved request.
- `GET /api/v1/consignments/{requestId}/item`: Get item details by request ID.
- `PATCH /api/v1/consignments/items/{id}/status`: Mark item as UNDER_INSPECTION, ACCEPTED, or REJECTED.

### Consignment Contracts
- `POST /api/v1/consignments/contracts`: Generate a contract specifying commission rate and agreed price.
- `GET /api/v1/consignments/{requestId}/contract`: View the generated contract.
- `PATCH /api/v1/consignments/contracts/{id}/sign`: Sign the contract (Consignor agrees to terms).
- `PATCH /api/v1/consignments/contracts/{id}/status`: Terminate or cancel contract.

---

## 4. Product Module

### Products
- `POST /api/v1/products`: Create a product listing from a signed consignment contract.
- `GET /api/v1/products`: List available products.
- `PATCH /api/v1/products/{id}/status`: Update product status (SELLING, RESERVED, SOLD, RETURNED).

### Media Assets
- `POST /api/v1/media`: Request a media upload URL (Generates MinIO presigned URL).
- `GET /api/v1/media`: Retrieve media assets for a product.
- `DELETE /api/v1/media/{id}`: Soft delete media.

### Warehouse Logs
- `POST /api/v1/products/warehouse-logs`: Record warehouse activity (location changes).
- `GET /api/v1/products/{id}/warehouse-logs`: Audit trail for a product's warehouse location.

---

## 5. Order & Cart Module

### Shopping Cart
- `GET /api/v1/cart/{userId}`: Retrieve cart contents.
- `POST /api/v1/cart/{userId}/items`: Add a product to the cart.
- `DELETE /api/v1/cart/{userId}/items/{itemId}`: Remove an item from the cart.
- `DELETE /api/v1/cart/{userId}`: Clear cart.

### Vouchers
- `POST /api/v1/vouchers`: Create a discount voucher.
- `GET /api/v1/vouchers/validate`: Validate code and calculate discount amount.

### Orders
- `POST /api/v1/orders`: Create an order from the cart. **Locks products (RESERVED) for 30 mins.**
- `GET /api/v1/orders`: List user orders.
- `PATCH /api/v1/orders/{id}/status`: Update order status.
  *Note:* Marking an order as `COMPLETED` automatically deducts platform commission and credits the Consignor's Wallet via the `SALE_REVENUE` transaction type. Marking as `CANCELLED` releases the product lock.

---

## 6. Financial Module

### Wallets & Transactions
- `GET /api/v1/financial/wallets`: List all wallets (Admin).
- `GET /api/v1/financial/wallets/{id}`: Retrieve wallet balance (Total and Available).
- `PUT /api/v1/financial/wallets/{id}`: Update bank payout details.
- `GET /api/v1/financial/wallets/{walletId}/transactions`: View history of wallet transactions.

### Withdrawals
- `POST /api/v1/financial/withdrawals`: Consignor requests to withdraw funds. Places a hold on Available Balance.
- `GET /api/v1/financial/withdrawals`: List withdrawal requests.
- `PATCH /api/v1/financial/withdrawals/{id}/status`: Admin updates status (APPROVED, PAID, REJECTED).
  *Note:* Rejecting a withdrawal releases the hold. Marking as PAID deducts the total balance.

---

## 7. Notification & Audit

### Notifications
- WebSocket endpoint is exposed at `/ws` for real-time notifications.
- `GET /api/v1/notifications`: Fetch paginated user notifications.
- `PUT /api/v1/notifications/{id}/read`: Mark notification as read.

### Activity Logs
- `GET /api/v1/audit/activity-logs`: List system audit logs (Tracks CREATE, UPDATE, DELETE actions across modules).

---

## Standard Responses
All endpoints wrap their payload in a standard `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

Errors follow the same structure but omit `data`:
```json
{
  "success": false,
  "message": "Entity not found"
}
```

## Swagger/OpenAPI
A full interactive Swagger UI is available when the server is running at:
`http://localhost:8080/swagger-ui.html`
