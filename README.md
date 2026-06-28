# Ecommerce Backend — Documentación

Backend de una plataforma de ecommerce multi-vendedor (proyecto N-Capas) construido con **Spring Boot 4 / Java 21**, arquitectura por capas (Controller → Service → Repository) y API REST stateless protegida con **JWT**.

> 📖 Documentación extendida en Confluence: espacio **ProyectoNCapas-Backend**.
> Enlace: https://documentacion-api-backend-pnc.atlassian.net/wiki/spaces/ProyectoNC/pages/262164/API+Documentaci+n+general
> (Este enlace puede requerir iniciar sesión con una cuenta de Atlassian. Toda la documentación está también en este README, sin login.)

## Tabla de contenidos
- [Información general](#información-general)
- [Arquitectura](#arquitectura)
- [Estructura de carpetas](#estructura-de-carpetas)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Variables de entorno](#variables-de-entorno)
- [Autenticación y autorización](#autenticación-y-autorización)
- [Formato de respuesta](#formato-de-respuesta)
- [Errores y códigos de estado](#errores-y-códigos-de-estado)
- [Endpoints](#endpoints)
- [Esquema de base de datos](#esquema-de-base-de-datos)
- [Catálogos / enumeraciones](#catálogos--enumeraciones)

---

## Información general

| Concepto | Valor |
|----------|-------|
| Stack | Spring Boot 4.0.6 · Java 21 · Spring Security · Spring Data JPA |
| Base de datos | PostgreSQL 16 |
| Base URL | `http://<host>:8080` (sin prefijo global; rutas en la raíz. ERP usa `/api`) |
| Formato | `application/json` (uploads: `multipart/form-data`; facturas: PDF/XML) |
| Autenticación | JWT vía header `Authorization: Bearer <token>` |
| Sesión | Stateless |
| Correo | Resend (API HTTP) |
| Facturación PDF | OpenPDF 1.3.43 |
| Build / contenedores | Maven · Docker + Docker Compose |

---

## Arquitectura

Arquitectura por capas, cada una con una única responsabilidad:

1. **Controller** — recibe la request, valida (`@Valid`), autoriza (`@PreAuthorize`) y delega. Extienden `BaseController` para un `GeneralResponse` uniforme.
2. **Service** — lógica de negocio (interface + implementación en `servicesImpl/`).
3. **Repository** — acceso a datos con Spring Data JPA.
4. **Domain** — entidades JPA y DTOs request/response.
5. **Security** — `SecurityConfig`, `JwtAuthFilter`, `JwtService`, `CurrentUserProvider`.
6. **Exceptions** — manejo centralizado con `GlobalExceptionHandler`.

**Patrones aplicados:** Strategy para descuentos de cupón (`services/discount`) y para métodos de pago (`services/payment`); generadores de factura PDF/XML (`services/invoice`); job programado de abandono de carrito (`services/scheduler`); DTO + Mapper; advice global de errores.

**Pipeline de una request:** CORS → `JwtAuthFilter` → `@PreAuthorize` → Controller → Service → Repository → PostgreSQL, con `GlobalExceptionHandler` traduciendo excepciones a `ApiErrorResponse`.

---

## Estructura de carpetas

```
ecommerce/
├── pom.xml                         # Dependencias y build (Maven)
├── Dockerfile                      # Imagen multi-stage (build + runtime)
└── src/main/
    ├── java/com/uca/ecommerce/
    │   ├── EcommerceApplication.java   # Entry point (@EnableScheduling)
    │   ├── config/                     # WebConfig (recursos estáticos /uploads)
    │   ├── controller/                 # Controladores REST + BaseController
    │   ├── services/                   # Interfaces de servicio
    │   │   ├── servicesImpl/           # Implementaciones
    │   │   ├── discount/               # Strategy de descuentos (cupones)
    │   │   ├── payment/                # Strategy de métodos de pago
    │   │   ├── invoice/                # Generadores PDF / XML de factura
    │   │   └── scheduler/              # Jobs programados (@Scheduled)
    │   ├── repository/                 # Repositorios Spring Data JPA
    │   ├── domain/
    │   │   ├── entities/               # Entidades JPA
    │   │   └── dto/request|response/   # DTOs
    │   ├── common/
    │   │   ├── mappers/                # Entidad <-> DTO
    │   │   └── Enums/                  # Enumeraciones
    │   ├── security/                   # JWT + Spring Security
    │   └── exceptions/                 # Excepciones + handler global
    └── resources/
        └── application.yaml            # Configuración (env vars)
```

---

## Configuración y ejecución

### Prerrequisitos
- Java 21 (JDK)
- Maven (o el wrapper `./mvnw`)
- Docker y Docker Compose
- PostgreSQL 16 (se levanta con Docker)

### Opción A — Todo con Docker (recomendado)
```bash
git clone <repo-url>
cd ProyectoNcapas_Backend
# Crear el archivo .env (ver Variables de entorno)
docker compose up -d --build
```
La API queda en `http://localhost:8080` (o el puerto de `APP_PORT`).

### Opción B — Dev local (BD en Docker, app con Maven)
```bash
docker compose up -d db
cd ecommerce
./mvnw spring-boot:run
```

### Verificar
```bash
curl http://localhost:8080/products/public
```

### Comandos útiles
```bash
docker compose up -d --build     # levantar todo
docker compose up -d db          # solo la base de datos
docker compose logs -f           # logs
docker compose down              # detener
docker compose down -v           # reset total (borra datos)

./mvnw spring-boot:run           # app en local
./mvnw clean package             # empaquetar JAR
./mvnw test                      # pruebas
```

---

## Variables de entorno

| Variable | Requerida | Descripción |
|----------|-----------|-------------|
| `POSTGRES_DB` | Sí | Nombre de la base de datos |
| `POSTGRES_USER` | Sí | Usuario de la BD |
| `POSTGRES_PASSWORD` | Sí | Contraseña de la BD |
| `POSTGRES_PORT` | No | Puerto expuesto (default 5432) |
| `DB_URL` | Sí* | JDBC URL (en Docker se deriva) |
| `DB_USERNAME` | Sí* | Usuario JDBC |
| `DB_PASSWORD` | Sí* | Contraseña JDBC |
| `APP_PORT` | No | Puerto de la app (default 8080) |
| `FRONTEND_URL` | No | Origen permitido por CORS |
| `UPLOAD_DIR` | No | Carpeta de uploads (default `uploads`) |
| `JWT_SECRET` | Sí | Secreto de firma HMAC-SHA |
| `JWT_EXPIRATION_MS` | Sí | Vigencia del access token (ms) |
| `JWT_REFRESH_EXPIRATION_DAYS` | No | Vigencia del refresh token (default 7) |
| `RESEND_API_KEY` | Sí | API key de Resend para el envío de correo |
| `MAIL_FROM` | No | Remitente de los correos (ej. `K LAB <noreply@klab-mrk.xyz>`; default `K LAB <onboarding@resend.dev>`) |

*Requeridas al ejecutar con Maven; en Docker se inyectan desde las `POSTGRES_*`.

> No subas el `.env` al repositorio. El correo se envía por la API HTTP de Resend; el `MAIL_FROM` usa el dominio verificado `klab-mrk.xyz` (o `onboarding@resend.dev`, que solo entrega al correo dueño de la cuenta).

---

## Autenticación y autorización

Mecanismo único basado en **JWT**. El token se envía en `Authorization: Bearer <accessToken>`. Las rutas `/auth/**` y las lecturas públicas de catálogo no requieren token.

**Roles:** `ADMIN` (plataforma), `SELLER` (vendedor), `BUYER` (comprador, por defecto al registrarse).

**Claims del access token:** `sub` (email), `userId` (UUID), `role`, `iat`, `exp`. El **refresh token** se persiste en BD (entidad `RefreshToken`).

**Flujos:** registro, login, refresh, logout, logout-all, cambio de contraseña (autenticado) y recuperación de contraseña por código enviado al email.

**Seguridad:** bcrypt para contraseñas, JWT firmado con HMAC-SHA, refresh tokens revocables, `@PreAuthorize` por rol, CORS restringido a `FRONTEND_URL`, sesiones STATELESS.

---

## Formato de respuesta

Respuestas exitosas envueltas en `GeneralResponse`:
```json
{
  "uri": "/products/",
  "message": "Operation successful",
  "status": 200,
  "time": "2026-06-27T13:00:00",
  "data": { }
}
```
Excepción: las facturas PDF/XML se devuelven como `byte[]`.

---

## Errores y códigos de estado

Manejo centralizado en `GlobalExceptionHandler`. Cuerpo de error (`ApiErrorResponse`):
```json
{ "message": "Descripción del error", "status": 404, "time": "2026-06-27", "uri": "/products/123" }
```
En validación, `message` es un objeto `{campo: mensaje}`.

| Código | Cuándo |
|--------|--------|
| 200 / 201 | Éxito / creado |
| 400 | Validación fallida, UUID mal formado, stock insuficiente |
| 401 | Token ausente/inválido/expirado o credenciales incorrectas |
| 403 | Rol insuficiente o producto no comprado |
| 404 | Recurso no encontrado |
| 409 | Duplicado, dependencias, transición de estado inválida |
| 422 | Pago inválido |
| 500 | Fallo al generar factura |

---

## Endpoints

Leyenda Auth: **Público** (sin token) · **Auth** (cualquier rol) · **BUYER** · **SELLER** · **ADMIN**.

### Auth — `/auth`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/auth/register` | Público | Registrar usuario (BUYER) |
| POST | `/auth/login` | Público | Iniciar sesión |
| POST | `/auth/refresh` | Público | Renovar access token |
| POST | `/auth/logout` | Público | Revocar un refresh token |
| POST | `/auth/logout-all` | Auth | Revocar todas las sesiones |
| PUT | `/auth/change-password` | Auth | Cambiar contraseña |
| POST | `/auth/forgot-password` | Público | Enviar email de recuperación |
| POST | `/auth/reset-password` | Público | Establecer nueva contraseña |

### Usuarios — `/users`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/users/` | ADMIN | Listar usuarios |
| GET | `/users/{id}` | Auth | Usuario por ID |
| PUT | `/users/update/{id}` | Auth | Actualizar usuario |
| PATCH | `/users/{id}/role` | ADMIN | Cambiar rol |
| DELETE | `/users/{id}` | ADMIN | Eliminar usuario |

### Direcciones — `/addresses`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/addresses/` | ADMIN | Listar todas |
| GET | `/addresses/user/{userId}` | Auth | De un usuario |
| GET | `/addresses/{id}` | Auth | Por ID |
| POST | `/addresses/create` | Auth | Crear |
| PUT | `/addresses/update/{id}` | Auth | Actualizar |
| DELETE | `/addresses/{id}` | Auth | Eliminar |
| DELETE | `/addresses/user/{userId}` | ADMIN | Eliminar todas de un usuario |

### Solicitudes de tienda — `/store_requests`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/store_requests/store-categories` | Auth | Categorías de tienda |
| POST | `/store_requests/create` | BUYER | Crear solicitud |
| GET | `/store_requests/me` | Auth | Mi última solicitud |
| GET | `/store_requests/` | ADMIN | Listar todas |
| GET | `/store_requests/pending` | ADMIN | Pendientes |
| PATCH | `/store_requests/{id}/review` | ADMIN | Aprobar/rechazar |

### Perfiles de vendedor — `/seller_profiles`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/seller_profiles/` | Auth | Listar |
| GET | `/seller_profiles/{id}` | Auth | Por ID |
| POST | `/seller_profiles/create` | ADMIN | Crear |
| PUT | `/seller_profiles/update/{id}` | ADMIN · SELLER | Actualizar |
| PATCH | `/seller_profiles/{id}/verify` | ADMIN | Verificar |
| DELETE | `/seller_profiles/{id}` | ADMIN · SELLER | Eliminar |

### Marcas — `/brands`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/brands/` | Público | Listar |
| GET | `/brands/{id}` | Público | Por ID |
| POST | `/brands/create` | ADMIN | Crear |
| PUT | `/brands/update/{id}` | ADMIN | Actualizar |
| PATCH | `/brands/patch/{id}` | ADMIN | Parcial |
| DELETE | `/brands/{id}` | ADMIN | Eliminar |

### Categorías — `/categories`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/categories/` | Público | Listar |
| GET | `/categories/{id}` | Público | Por ID |
| POST | `/categories/create` | ADMIN | Crear |
| PUT | `/categories/update/{id}` | ADMIN | Actualizar |
| PATCH | `/categories/patch/{id}` | ADMIN | Parcial |
| DELETE | `/categories/{id}` | ADMIN | Eliminar |

### Productos — `/products`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/products/` | Auth | Listar todos |
| GET | `/products/public` | Público | Catálogo público |
| GET | `/products/recommended` | Público | Recomendados (`limit` opcional) |
| POST | `/products/{productId}/view` | BUYER | Registrar vista |
| GET | `/products/{id}` | Auth | Por ID |
| POST | `/products/create` | ADMIN · SELLER | Crear |
| PUT | `/products/update/{id}` | ADMIN · SELLER | Actualizar |
| PATCH | `/products/patch/{id}` | ADMIN · SELLER | Parcial |
| DELETE | `/products/{id}` | ADMIN · SELLER | Eliminar |
| GET | `/products/my` | SELLER | Mis productos |

### Variantes — `/product-variants`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/product-variants/` | Auth | Listar |
| GET | `/product-variants/public` | Público | Públicas |
| GET | `/product-variants/{id}` | Auth | Por ID |
| GET | `/product-variants/product/{productId}` | Auth | De un producto |
| POST | `/product-variants/create` | ADMIN · SELLER | Crear |
| PUT | `/product-variants/update/{id}` | ADMIN · SELLER | Actualizar |
| PATCH | `/product-variants/patch/{id}` | ADMIN · SELLER | Parcial |
| DELETE | `/product-variants/{id}` | ADMIN · SELLER | Eliminar |

### Imágenes de producto — `/product-images`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/product-images/upload` | ADMIN · SELLER | Subir archivo (`multipart`, campo `file`) |
| GET | `/product-images/` | Auth | Listar |
| GET | `/product-images/public` | Público | Públicas |
| GET | `/product-images/{id}` | Auth | Por ID |
| GET | `/product-images/product/{productId}` | Auth | De un producto |
| POST | `/product-images/create` | ADMIN · SELLER | Crear registro |
| PUT | `/product-images/update/{id}` | ADMIN · SELLER | Actualizar |
| PATCH | `/product-images/patch/{id}` | ADMIN · SELLER | Parcial |
| DELETE | `/product-images/{id}` | ADMIN · SELLER | Eliminar |

### Insignias — `/product-badges`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/product-badges/` | Auth | Listar |
| GET | `/product-badges/public` | Público | Públicas |
| GET | `/product-badges/product/{productId}` | Auth | De un producto |
| GET | `/product-badges/{id}` | Auth | Por ID |
| POST | `/product-badges/create` | ADMIN | Crear |
| PUT | `/product-badges/update/{id}` | ADMIN | Actualizar |
| DELETE | `/product-badges/{id}` | ADMIN | Eliminar |

### Drops — `/drops`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/drops/` | Público | Listar |
| GET | `/drops/my` | SELLER | Mis drops |
| GET | `/drops/{id}` | Público | Por ID |
| POST | `/drops/create` | ADMIN · SELLER | Crear |
| PUT | `/drops/update/{id}` | ADMIN · SELLER | Actualizar |
| PATCH | `/drops/patch/{id}` | ADMIN · SELLER | Parcial |
| DELETE | `/drops/{id}` | ADMIN · SELLER | Eliminar |

### Productos en drop — `/drop-products`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/drop-products/` | Público | Listar |
| GET | `/drop-products/drop/{dropId}` | Público | De un drop |
| POST | `/drop-products/create` | ADMIN · SELLER | Asociar |
| DELETE | `/drop-products/{id}` | ADMIN · SELLER | Quitar |

### Carrito — `/cart-items`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/cart-items/` | BUYER | Ver carrito |
| POST | `/cart-items/create` | BUYER | Agregar ítem |
| PUT | `/cart-items/update/{id}` | BUYER | Actualizar cantidad |
| DELETE | `/cart-items/{id}` | BUYER | Quitar ítem |

### Wishlist — `/wishlist`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/wishlist/` | BUYER | Ver wishlist |
| POST | `/wishlist/create` | BUYER | Agregar |
| DELETE | `/wishlist/{id}` | BUYER | Quitar |

### Alertas de stock — `/stock-alerts`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/stock-alerts/` | ADMIN | Listar todas |
| GET | `/stock-alerts/{id}` | ADMIN · SELLER · BUYER | Por ID |
| GET | `/stock-alerts/user/{userId}` | ADMIN · SELLER · BUYER | De un usuario |
| GET | `/stock-alerts/product/{productId}` | ADMIN · SELLER | De un producto |
| POST | `/stock-alerts/create` | ADMIN · BUYER | Crear |
| DELETE | `/stock-alerts/{id}` | ADMIN · BUYER | Eliminar |

### Cupones — `/coupons`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/coupons/discount-types` | Auth | Tipos de descuento |
| GET | `/coupons/` | ADMIN · SELLER | Listar |
| GET | `/coupons/{id}` | ADMIN · SELLER | Por ID |
| POST | `/coupons/create` | ADMIN · SELLER | Crear |
| PUT | `/coupons/update/{id}` | ADMIN · SELLER | Actualizar |
| PATCH | `/coupons/patch/{id}` | ADMIN · SELLER | Parcial |
| DELETE | `/coupons/{id}` | ADMIN · SELLER | Eliminar |
| POST | `/coupons/preview` | Auth | Previsualizar descuento |

### Órdenes — `/orders`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/orders/` | ADMIN | Listar todas |
| GET | `/orders/{id}` | Auth | Por ID |
| GET | `/orders/customer/{customerId}` | Auth | De un cliente |
| GET | `/orders/status/{status}` | ADMIN · SELLER | Por estado |
| POST | `/orders/create` | BUYER | Crear desde carrito |
| PUT | `/orders/update/{id}` | ADMIN · SELLER | Actualizar |
| PATCH | `/orders/patch/{id}` | ADMIN · SELLER | Parcial |
| DELETE | `/orders/{id}` | ADMIN | Eliminar |
| POST | `/orders/{id}/refund` | BUYER | Solicitar reembolso |

### Ítems de orden — `/order-items`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/order-items/` | ADMIN | Listar todos |
| GET | `/order-items/{id}` | Auth | Por ID |
| GET | `/order-items/order/{orderId}` | Auth | De una orden |
| GET | `/order-items/product/{productId}` | ADMIN · SELLER | Por producto |
| GET | `/order-items/seller/{sellerId}` | ADMIN · SELLER | Por vendedor |
| POST | `/order-items/create` | ADMIN · BUYER | Crear |
| PUT | `/order-items/update/{id}` | ADMIN · BUYER | Actualizar |
| PATCH | `/order-items/patch/{id}` | ADMIN · BUYER | Parcial |
| DELETE | `/order-items/{id}` | ADMIN · BUYER | Eliminar |

### Pagos — `/payments`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/payments/` | ADMIN | Listar todos |
| GET | `/payments/{id}` | Auth | Por ID |
| GET | `/payments/order/{orderId}` | Auth | De una orden |
| GET | `/payments/status/{status}` | ADMIN | Por estado |
| POST | `/payments/create` | BUYER | Crear pago |
| PUT | `/payments/update/{id}` | ADMIN | Actualizar |
| PATCH | `/payments/patch/{id}` | ADMIN | Parcial |
| DELETE | `/payments/{id}` | ADMIN | Eliminar |

### Facturas — `/invoices`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/invoices/` | ADMIN | Listar todas |
| GET | `/invoices/order/{orderId}` | ADMIN · SELLER · BUYER | De una orden |
| POST | `/invoices/order/{orderId}/email` | ADMIN · SELLER · BUYER | Enviar por correo |
| GET | `/invoices/order/{orderId}/pdf` | ADMIN · SELLER · BUYER | Descargar PDF |
| GET | `/invoices/order/{orderId}/xml` | ADMIN · SELLER · BUYER | Descargar XML |

### Envíos — `/shipments`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/shipments/order/{orderId}` | ADMIN · SELLER · BUYER | Envío de una orden |

### Métodos de envío — `/shipping-methods`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/shipping-methods/` | Público | Listar |
| GET | `/shipping-methods/{id}` | Público | Por ID |
| POST | `/shipping-methods/create` | ADMIN | Crear |
| PUT | `/shipping-methods/update/{id}` | ADMIN | Actualizar |
| PATCH | `/shipping-methods/patch/{id}` | ADMIN | Parcial |
| DELETE | `/shipping-methods/{id}` | ADMIN | Eliminar |

### Reviews — `/reviews`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/reviews/` | Público | Listar |
| GET | `/reviews/{id}` | Público | Por ID |
| GET | `/reviews/product/{productId}` | Público | De un producto |
| GET | `/reviews/user/{userId}` | Público | De un usuario |
| GET | `/reviews/reviewable-products/{userId}` | BUYER | Productos reseñables |
| GET | `/reviews/seller/{sellerId}` | SELLER | De un vendedor |
| POST | `/reviews/create` | BUYER | Crear |
| PUT | `/reviews/update/{id}` | BUYER | Actualizar |
| PATCH | `/reviews/patch/{id}` | BUYER | Parcial |
| DELETE | `/reviews/{id}` | ADMIN · BUYER | Eliminar |

### Fotos de review — `/review-photos`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/review-photos/` | Público | Listar |
| GET | `/review-photos/{id}` | Público | Por ID |
| GET | `/review-photos/review/{reviewId}` | Público | De una review |
| POST | `/review-photos/create` | BUYER | Crear registro |
| PUT | `/review-photos/update/{id}` | BUYER | Actualizar |
| PATCH | `/review-photos/patch/{id}` | BUYER | Parcial |
| DELETE | `/review-photos/{id}` | ADMIN · BUYER | Eliminar |
| POST | `/review-photos/upload` | BUYER | Subir archivo (`multipart`, campo `file`) |

### Verificaciones — `/verifications`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/verifications/` | ADMIN | Listar |
| GET | `/verifications/{id}` | ADMIN | Por ID |
| GET | `/verifications/product/{productId}` | ADMIN | De un producto |
| GET | `/verifications/verified-by/{verifiedByUuid}` | ADMIN | Hechas por un admin |
| POST | `/verifications/create` | ADMIN | Crear |
| PUT | `/verifications/update/{id}` | ADMIN | Actualizar |
| PATCH | `/verifications/patch/{id}` | ADMIN | Parcial |
| DELETE | `/verifications/{id}` | ADMIN | Eliminar |

### Analytics — `/analytics`
| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/analytics/cart-conversion` | ADMIN | Reporte de conversión de carrito |

### Integración ERP — `/api/erp` y `/api/orders`
Todos requieren ADMIN.
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/erp/exports` | Estados de exportaciones |
| GET | `/api/erp/orders` | Listar órdenes ERP |
| GET | `/api/erp/orders/{erpReference}` | Orden ERP por referencia |
| POST | `/api/erp/orders/{orderId}/export` | Exportar una orden |
| POST | `/api/erp/orders/export-pending` | Exportar pendientes |
| POST | `/api/orders/export-pending-to-erp` | Exportar pendientes (alias) |
| POST | `/api/orders/{id}/export-to-erp` | Exportar una orden |

---

## Esquema de base de datos

PostgreSQL 16 gestionado con JPA/Hibernate (`ddl-auto: update`). 31 tablas, 16 enumeraciones, claves primarias UUID. El diagrama ER está en `ecommerce/DBdiagram-Backend.png`.

<details>
<summary>Definición DBML (pegar en dbdiagram.io)</summary>

```dbml
Table users {
  uuid uuid [pk]
  first_name varchar
  last_name varchar
  email varchar [not null, unique]
  password_hash varchar [not null]
  phone varchar [unique]
  role varchar(50) [not null, note: 'ADMIN, BUYER, SELLER']
  k_select_tier varchar(50) [not null, note: 'BRONZE, SILVER, GOLD, PLATINUM']
  is_active boolean [not null]
  created_at timestamp [not null]
  updated_at timestamp
}

Table address {
  id uuid [pk]
  user_id uuid [not null]
  alias varchar
  street varchar [not null]
  city varchar [not null]
  state varchar
  country varchar [not null]
  postal_code varchar
  is_default boolean [not null]
}

Table seller_profiles {
  id uuid [pk]
  user_id uuid [not null, unique]
  store_name varchar [not null, unique]
  store_description text
  store_category varchar(50)
  location varchar
  rating decimal(3,2)
  total_sales int [not null]
  is_verified boolean [not null]
  created_at timestamp [not null]
}

Table store_requests {
  id uuid [pk]
  user_id uuid [not null]
  store_name varchar [not null]
  store_description text
  store_category varchar(50) [not null]
  location varchar [not null]
  monthly_sales_estimate int [not null]
  status varchar(50) [not null]
  reviewed_by uuid
  review_note text
  created_at timestamp [not null]
  reviewed_at timestamp
}

Table refresh_tokens {
  id uuid [pk]
  token varchar [not null, unique]
  user_uuid uuid [not null]
  expires_at timestamp [not null]
  revoked boolean [not null]
  created_at timestamp [not null]
}

Table password_reset_tokens {
  id uuid [pk]
  token varchar [not null, unique]
  user_uuid uuid [not null]
  expires_at timestamp [not null]
  used boolean [not null]
  created_at timestamp [not null]
}

Table brands {
  id uuid [pk]
  name varchar(100) [not null, unique]
  slug varchar(100) [not null, unique]
  logo_url varchar(500)
}

Table categories {
  id uuid [pk]
  name varchar [not null]
  units int
  parent_id uuid
  created_at timestamp [not null]
  updated_at timestamp
}

Table products {
  id uuid [pk]
  seller_id uuid [not null]
  category_id uuid [not null]
  brand_id uuid [not null]
  sku varchar(50) [not null, unique]
  name varchar(255) [not null]
  slug varchar(255) [not null, unique]
  description text
  price decimal(10,2) [not null]
  condition varchar(50) [not null]
  condition_score decimal(3,1)
  auth_status varchar(20) [not null]
  is_featured boolean [not null]
  is_new boolean [not null]
  is_limited boolean [not null]
  is_private_drop boolean [not null]
  total_stock int [not null]
  rating decimal(3,2)
  review_count int [not null]
  created_at timestamp [not null]
  updated_at timestamp
}

Table product_variants {
  id uuid [pk]
  product_id uuid [not null]
  size varchar(20) [not null]
  color_name varchar(100) [not null]
  color_hex varchar(7) [not null]
  stock int [not null]
  price_delta decimal(10,2) [not null]
}

Table product_images {
  id uuid [pk]
  product_id uuid [not null]
  url varchar(500) [not null]
  alt_text varchar(255)
  is_primary boolean [not null]
  sort_order int [not null]
}

Table product_badges {
  id uuid [pk]
  product_id uuid [not null]
  label varchar(50) [not null]
}

Table drops {
  id uuid [pk]
  owner_id uuid
  title varchar(255) [not null]
  slug varchar(255) [not null, unique]
  drop_date timestamp [not null]
  units int [not null]
  type varchar(50) [not null]
  cover_image_url varchar(500)
  is_active boolean [not null]
  created_at timestamp [not null]
}

Table drop_products {
  id uuid [pk]
  drop_id uuid [not null]
  product_id uuid [not null]
  indexes { (drop_id, product_id) [unique] }
}

Table cart_items {
  id uuid [pk]
  user_id uuid [not null]
  product_id uuid [not null]
  variant_id uuid [not null]
  quantity int [not null]
  added_at timestamp [not null]
  indexes { (user_id, product_id, variant_id) [unique] }
}

Table cart_sessions {
  id uuid [pk]
  user_id uuid [not null]
  status varchar(50) [not null]
  started_at timestamp [not null]
  converted_at timestamp
  abandoned_at timestamp
  abandoned_manually boolean [not null]
}

Table wishlist {
  id uuid [pk]
  user_id uuid [not null]
  product_id uuid [not null]
  added_at timestamp [not null]
  indexes { (user_id, product_id) [unique] }
}

Table stock_alerts {
  id uuid [pk]
  user_id uuid [not null]
  product_id uuid [not null]
  notified_at timestamp
}

Table coupons {
  id uuid [pk]
  code varchar(50) [not null, unique]
  label varchar(255) [not null]
  type varchar(50) [not null]
  value decimal(10,2) [not null]
  min_order_amount decimal(10,2)
  max_uses int
  uses_count int [not null]
  is_active boolean [not null]
  expires_at timestamp
  created_at timestamp [not null]
  owner_id uuid
}

Table orders {
  id uuid [pk]
  customer_id uuid [not null]
  user_shipping_address_id uuid [not null]
  shipping_method_id uuid [not null]
  coupon_id uuid
  status varchar(50) [not null]
  subtotal decimal(10,2) [not null]
  shipping_cost decimal(10,2) [not null]
  discount_amount decimal(10,2) [not null]
  total decimal(10,2) [not null]
  tracking_number varchar(100)
  notes text
  created_at timestamp [not null]
  updated_at timestamp
}

Table order_items {
  id uuid [pk]
  order_id uuid [not null]
  product_id uuid [not null]
  variant_id uuid
  seller_id uuid [not null]
  quantity int [not null]
  unit_price decimal(10,2) [not null]
  total_price decimal(10,2) [not null]
}

Table payments {
  id uuid [pk]
  order_id uuid [not null]
  method varchar(50) [not null]
  status varchar(50) [not null]
  amount decimal(10,2) [not null]
  transaction_id varchar(255)
  paid_at timestamp
  created_at timestamp [not null]
}

Table invoices {
  id uuid [pk]
  order_id uuid [not null, unique]
  control_number varchar(100) [not null, unique]
  customer_name varchar(255) [not null]
  customer_email varchar(255) [not null]
  subtotal decimal(10,2) [not null]
  shipping_cost decimal(10,2) [not null]
  discount_amount decimal(10,2) [not null]
  total decimal(10,2) [not null]
  status varchar(50) [not null]
  issued_at timestamp [not null]
  created_at timestamp [not null]
}

Table shipping_methods {
  id uuid [pk]
  name varchar(100) [not null, unique]
  fee decimal(10,2) [not null]
  eta varchar(50) [not null]
  is_active boolean [not null]
}

Table reviews {
  id uuid [pk]
  product_id uuid [not null]
  user_id uuid [not null]
  rating int [not null]
  body varchar(5000)
  is_verified_purchase boolean [not null]
  created_at timestamp [not null]
}

Table review_photos {
  id uuid [pk]
  review_id uuid [not null]
  url varchar(500) [not null]
  sort_order int
}

Table verifications {
  id uuid [pk]
  product_id uuid [not null]
  verified_by uuid
  material_check varchar(50) [not null]
  construction_check varchar(50) [not null]
  factory_code_check varchar(50) [not null]
  final_inspection varchar(50) [not null]
  notes text
  verified_at timestamp
  created_at timestamp [not null]
}

Table user_product_events {
  id uuid [pk]
  user_id uuid [not null]
  product_id uuid [not null]
  event_type varchar(30) [not null]
  created_at timestamp [not null]
  indexes {
    user_id [name: 'idx_user_product_event_user']
    product_id [name: 'idx_user_product_event_product']
    event_type [name: 'idx_user_product_event_type']
    created_at [name: 'idx_user_product_event_created_at']
  }
}

Table erp_orders {
  id uuid [pk]
  erp_reference varchar(50) [not null, unique]
  source_order_id uuid [not null, unique]
  customer_id uuid [not null]
  customer_name varchar(255) [not null]
  customer_email varchar(255) [not null]
  shipping_address text [not null]
  subtotal decimal(10,2) [not null]
  shipping_cost decimal(10,2) [not null]
  discount_amount decimal(10,2) [not null]
  total decimal(10,2) [not null]
  order_date timestamp [not null]
  received_at timestamp [not null]
}

Table erp_order_items {
  id uuid [pk]
  erp_order_id uuid [not null]
  source_order_item_id uuid [not null]
  product_id uuid [not null]
  sku varchar(50) [not null]
  product_name varchar(255) [not null]
  variant_id uuid
  variant_description varchar(255)
  quantity int [not null]
  unit_price decimal(10,2) [not null]
  total_price decimal(10,2) [not null]
}

Table order_erp_exports {
  id uuid [pk]
  order_id uuid [not null, unique]
  erp_export_status varchar(50) [not null]
  erp_reference varchar(50) [unique]
  erp_exported_at timestamp
  erp_error_message text
  created_at timestamp [not null]
  updated_at timestamp
}

Ref: address.user_id > users.uuid
Ref: seller_profiles.user_id - users.uuid
Ref: store_requests.user_id > users.uuid
Ref: store_requests.reviewed_by > users.uuid
Ref: refresh_tokens.user_uuid > users.uuid
Ref: password_reset_tokens.user_uuid > users.uuid
Ref: categories.parent_id > categories.id
Ref: products.seller_id > seller_profiles.id
Ref: products.category_id > categories.id
Ref: products.brand_id > brands.id
Ref: product_variants.product_id > products.id
Ref: product_images.product_id > products.id
Ref: product_badges.product_id > products.id
Ref: drops.owner_id > seller_profiles.id
Ref: drop_products.drop_id > drops.id
Ref: drop_products.product_id > products.id
Ref: cart_items.user_id > users.uuid
Ref: cart_items.product_id > products.id
Ref: cart_items.variant_id > product_variants.id
Ref: cart_sessions.user_id > users.uuid
Ref: wishlist.user_id > users.uuid
Ref: wishlist.product_id > products.id
Ref: stock_alerts.user_id > users.uuid
Ref: stock_alerts.product_id > products.id
Ref: coupons.owner_id > seller_profiles.id
Ref: orders.customer_id > users.uuid
Ref: orders.user_shipping_address_id > address.id
Ref: orders.shipping_method_id > shipping_methods.id
Ref: orders.coupon_id > coupons.id
Ref: order_items.order_id > orders.id
Ref: order_items.product_id > products.id
Ref: order_items.variant_id > product_variants.id
Ref: order_items.seller_id > seller_profiles.id
Ref: payments.order_id > orders.id
Ref: invoices.order_id - orders.id
Ref: reviews.product_id > products.id
Ref: reviews.user_id > users.uuid
Ref: review_photos.review_id > reviews.id
Ref: verifications.product_id > products.id
Ref: verifications.verified_by > users.uuid
Ref: user_product_events.user_id > users.uuid
Ref: user_product_events.product_id > products.id
Ref: erp_order_items.erp_order_id > erp_orders.id
Ref: order_erp_exports.order_id - orders.id
```
</details>

---

## Catálogos / enumeraciones

| Enum | Valores |
|------|---------|
| Role | ADMIN, SELLER, BUYER |
| Tier | BRONZE, SILVER, GOLD, PLATINUM |
| OrderStatus | PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, REFUNDED |
| PaymentMethod | CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CASH_ON_DELIVERY |
| PaymentStatus | PENDING, COMPLETED, FAILED, REFUNDED |
| DiscountType | PERCENTAGE, FIXED, FREE_SHIPPING, TWO_FOR_ONE |
| ProductCondition | NEW, LIKE_NEW, USED, REFURBISHED |
| AuthStatus | NOT_SUBMITTED, PENDING, AUTHENTICATED, REJECTED |
| VerificationStageStatus | PENDING, PASSED, FAILED |
| DropType | PUBLIC, PRIVATE |
| CartSessionStatus | ACTIVE, CONVERTED, ABANDONED |
| InvoiceStatus | ISSUED, SENT, FAILED |
| ErpExportStatus | PENDING_EXPORT, EXPORTED, REJECTED, FAILED |
| StoreCategory | SNEAKERS, STREETWEAR, ROPA, ACCESORIOS, COLECCIONABLES, OTRO |
| StoreRequestStatus | PENDIENTE, APROBADA, RECHAZADA |
| ProductEventType | VIEW, ADD_TO_CART, PURCHASE |
