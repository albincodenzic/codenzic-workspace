# Codenzic Workspace Backend

This project is a single Spring Boot application organized as package-level business modules inside the `com.codenzic.workspace` namespace.

## Structure

- `src/main/java/com/codenzic/workspace` – application and package-level module roots
- `src/main/resources` – application configuration and Flyway migrations
- `src/test` – test sources

## Notes

- This is a modular monolith implemented as package boundaries, not Maven submodules.
- PostgreSQL is configured for local development on `localhost:5432`.
- Authentication uses short-lived JWT access tokens and opaque, rotating refresh tokens. Only SHA-256 hashes of refresh tokens are stored. `/api/v1/auth/logout` revokes a refresh token; already-issued access tokens remain valid until their configured expiry.
- Authentication endpoints include `POST /api/v1/auth/refresh`, `POST /api/v1/auth/logout`, and authenticated `GET /api/v1/auth/me`.
- Configure database credentials and the JWT signing secret through `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, and `JWT_SECRET`. The application does not supply default credentials or a default signing key.
- Platform users select a tenant for tenant-scoped APIs with `X-Organization-Id`; the backend accepts it only for platform users and verifies that the organization is active. Tenant users remain scoped to their persisted organization.
- Organization endpoints provide scoped listing, get, create, update, status changes, and organization-admin assignment. Tenant users can only access their own organization; platform users can access all organizations.
- `GET/POST/PUT/DELETE /api/v1/roles` and role-user assignment endpoints manage organization-scoped roles. `GET /api/v1/permissions` and `GET /api/v1/permissions/{code}` expose the seeded permission catalog; permission codes are assigned through role create/update requests.
- Organization status supports `ACTIVE` and `SUSPENDED`. Suspended organizations cannot authenticate or use existing access tokens.
- Conversation and message history, plus notification lists, use the standard paginated response. Conversations include an unread-message count; `GET /api/v1/notifications/unread-count` returns the authenticated user's unread notification count.
# codenzic-workspace
