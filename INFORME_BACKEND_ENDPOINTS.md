# 🔧 INFORME: Endpoints Backend Requeridos

## ⚠️ Problema Detectado

El frontend no está recibiendo datos de:
- ❌ Lista de usuarios en Dashboard
- ❌ Lista de pedidos en Dashboard  
- ❌ Pedidos del usuario en Perfil
- ❌ Error 403 al crear reseñas

---

## 📋 ENDPOINTS QUE DEBE IMPLEMENTAR EL BACKEND

### 1. USUARIOS

#### GET /api/usuarios
**Propósito:** Obtener todos los usuarios (solo admin)

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuesta esperada (200 OK):**
```json
{
  "success": true,
  "usuarios": [
    {
      "id": 1,
      "email": "usuario@ejemplo.com",
      "usuario": "usuario123",
      "nombre": "Juan",
      "apellido": "Pérez",
      "rol": "cliente",
      "isActivo": "Activo",
      "telefono": "+56 9 1234 5678",
      "direccion": "Calle 123, Santiago",
      "fechaRegistro": "2025-11-30T10:00:00Z",
      "avatar": "https://..."
    }
  ]
}
```

**Si el array está vacío o hay error:**
```json
{
  "success": true,
  "usuarios": []
}
```

---

#### GET /api/usuarios/{id}
**Propósito:** Obtener un usuario por ID

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuesta esperada (200 OK):**
```json
{
  "success": true,
  "usuario": {
    "id": 1,
    "email": "usuario@ejemplo.com",
    "nombre": "Juan",
    "apellido": "Pérez",
    ...
  }
}
```

---

### 2. PEDIDOS

#### GET /api/pedidos
**Propósito:** Obtener todos los pedidos (admin) o filtrados

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Query params opcionales:**
- `?email=usuario@ejemplo.com` - Filtrar por email del usuario
- `?estado=confirmado` - Filtrar por estado

**Respuesta esperada (200 OK):**
```json
{
  "success": true,
  "pedidos": [
    {
      "id": "PED-001",
      "fecha": "2025-11-30T15:30:00Z",
      "estado": "confirmado",
      "leido": false,
      "contacto": {
        "nombre": "Juan",
        "apellido": "Pérez",
        "email": "juan@ejemplo.com",
        "telefono": "+56 9 1234 5678"
      },
      "envio": {
        "direccion": "Calle 123",
        "ciudad": "Santiago",
        "region": "metropolitana",
        "codigoPostal": "12345",
        "notas": "Dejar en portería",
        "costo": 5000,
        "esGratis": false
      },
      "items": [
        {
          "id": 1,
          "nombre": "Manzanas Rojas",
          "precio": 2500,
          "cantidad": 2,
          "subtotal": 5000
        }
      ],
      "subtotal": 5000,
      "costoEnvio": 5000,
      "total": 10000
    }
  ]
}
```

**Estados válidos de pedido:**
- `confirmado`
- `en-preparacion`
- `enviado`
- `entregado`
- `cancelado`

---

#### POST /api/pedidos
**Propósito:** Crear un nuevo pedido

**Headers requeridos:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body de la solicitud:**
```json
{
  "usuarioId": 5,
  "contacto": {
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@ejemplo.com",
    "telefono": "+56 9 1234 5678"
  },
  "envio": {
    "direccion": "Calle 123",
    "ciudad": "Santiago",
    "region": "metropolitana",
    "codigoPostal": "12345",
    "notas": "Dejar en portería",
    "costo": 5000,
    "esGratis": false
  },
  "items": [
    {
      "id": 1,
      "cantidad": 2
    }
  ],
  "subtotal": 5000,
  "costoEnvio": 5000,
  "total": 10000
}
```

**Respuesta esperada (201 Created):**
```json
{
  "success": true,
  "pedido": {
    "id": "PED-001",
    "fecha": "2025-11-30T15:30:00Z",
    "estado": "confirmado",
    ... (todos los datos del pedido)
  },
  "mensaje": "Pedido creado exitosamente"
}
```

**⚠️ IMPORTANTE:** Este endpoint DEBE funcionar correctamente para que:
1. Los pedidos aparezcan en el perfil del usuario
2. Las reseñas puedan verificar que el usuario compró el producto

---

#### PATCH /api/pedidos/{id}/estado
**Propósito:** Actualizar estado de un pedido (admin)

**Headers requeridos:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "estado": "entregado"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "success": true,
  "mensaje": "Estado actualizado"
}
```

---

### 3. RESEÑAS

#### GET /api/productos/{productoId}/resenas
**Propósito:** Obtener reseñas de un producto (público)

**Respuesta esperada (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "productoId": 5,
      "usuarioId": 12,
      "nombreUsuario": "Juan Pérez",
      "puntuacion": 5,
      "comentario": "Excelente producto",
      "fechaCreacion": "2025-11-30T10:30:00Z",
      "verificado": true
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

---

#### GET /api/productos/{productoId}/resenas/resumen
**Propósito:** Obtener estadísticas de reseñas (público)

**Respuesta esperada (200 OK):**
```json
{
  "productoId": 5,
  "promedioCalificacion": 4.5,
  "totalResenas": 10,
  "distribucion": {
    "5": 6,
    "4": 2,
    "3": 1,
    "2": 0,
    "1": 1
  }
}
```

---

#### POST /api/productos/{productoId}/resenas
**Propósito:** Crear una reseña (requiere auth + haber comprado)

**Headers requeridos:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "puntuacion": 5,
  "comentario": "Excelente producto, muy fresco."
}
```

**Validación requerida en backend:**
1. Usuario debe estar autenticado ✅
2. Usuario debe tener un pedido con estado `entregado` que contenga el producto ✅
3. Usuario no debe tener ya una reseña para este producto ✅

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "productoId": 5,
  "usuarioId": 12,
  "nombreUsuario": "Juan Pérez",
  "puntuacion": 5,
  "comentario": "Excelente producto, muy fresco.",
  "fechaCreacion": "2025-11-30T10:30:00Z",
  "verificado": true
}
```

**Errores esperados:**
| Código | Situación |
|--------|-----------|
| 401 | No autenticado |
| 403 | Usuario no ha comprado el producto (o pedido no está en estado "entregado") |
| 409 | Usuario ya tiene una reseña para este producto |

---

## 🔍 CÓMO DEBUGGEAR

### En el navegador (F12 > Console):

Cuando el frontend hace peticiones, verás logs como:
```
[Pedidos] Consultando endpoint: /pedidos?email=usuario@ejemplo.com
[Pedidos] Respuesta del servidor: {success: true, pedidos: [...]}
```

### Verificar en el backend:

1. **¿El endpoint `/api/pedidos` responde?**
   ```bash
   curl -X GET "http://localhost:3000/api/pedidos" \
     -H "Authorization: Bearer {token}"
   ```

2. **¿El endpoint `/api/usuarios` responde?**
   ```bash
   curl -X GET "http://localhost:3000/api/usuarios" \
     -H "Authorization: Bearer {token}"
   ```

3. **¿Se crean los pedidos correctamente?**
   ```bash
   curl -X POST "http://localhost:3000/api/pedidos" \
     -H "Authorization: Bearer {token}" \
     -H "Content-Type: application/json" \
     -d '{"usuarioId": 1, "contacto": {...}, "items": [...], "total": 10000}'
   ```

---

## 📝 CHECKLIST PARA EL BACKEND

- [ ] `GET /api/usuarios` devuelve `{ "success": true, "usuarios": [...] }`
- [ ] `GET /api/pedidos` devuelve `{ "success": true, "pedidos": [...] }`
- [ ] `GET /api/pedidos?email=xxx` filtra correctamente por email
- [ ] `POST /api/pedidos` crea el pedido y devuelve `{ "success": true, "pedido": {...} }`
- [ ] Los pedidos se asocian correctamente al `usuarioId`
- [ ] `GET /api/productos/{id}/resenas` devuelve array paginado
- [ ] `GET /api/productos/{id}/resenas/resumen` devuelve estadísticas
- [ ] `POST /api/productos/{id}/resenas` valida que el usuario compró el producto
- [ ] La validación de reseña usa pedidos en estado `entregado`

---

## 🚨 POSIBLES CAUSAS DEL ERROR 403

1. **CORS mal configurado** - El backend rechaza peticiones del frontend
2. **Token JWT inválido o expirado** - Verificar que el token se envía correctamente
3. **Endpoint no existe** - El backend no tiene implementado el endpoint
4. **Validación de compra falla** - El pedido existe pero no está en estado "entregado"

---

## 📞 Información de Conexión

**Frontend espera:**
- Base URL: `http://localhost:3000/api` (a través del proxy de Vite)
- Token: `localStorage.getItem('auth_token')`
- Header: `Authorization: Bearer {token}`

**Proxy de Vite configurado en `vite.config.ts`:**
```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:3000',
      changeOrigin: true,
    }
  }
}
```
