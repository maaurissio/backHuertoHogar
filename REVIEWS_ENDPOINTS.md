# API de Reseñas de Productos - Huerto Hogar

## Descripción General

Este documento define los endpoints necesarios para el sistema de reseñas de productos. Las reseñas permiten a los usuarios calificar y comentar sobre los productos que han comprado.

---

## Base URL
```
/api
```

---

## Endpoints de Reseñas

### 1. Obtener Reseñas de un Producto

Obtiene todas las reseñas de un producto específico.

**Endpoint:** `GET /api/productos/{productoId}/resenas`

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| productoId | Long | ID del producto |

**Parámetros de query (opcional):**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| page | Integer | Número de página (default: 0) |
| size | Integer | Elementos por página (default: 10) |
| sort | String | Ordenamiento: `fecha,desc` o `puntuacion,desc` |

**Respuesta exitosa (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "productoId": 5,
      "usuarioId": 12,
      "nombreUsuario": "Juan Pérez",
      "puntuacion": 5,
      "comentario": "Las semillas germinaron muy rápido, excelente calidad.",
      "fechaCreacion": "2024-01-15T10:30:00Z",
      "verificado": true
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "number": 0,
  "size": 10
}
```

---

### 2. Crear una Reseña

Permite a un usuario autenticado crear una reseña para un producto que haya comprado.

**Endpoint:** `POST /api/productos/{productoId}/resenas`

**Headers requeridos:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| productoId | Long | ID del producto a reseñar |

**Body de la solicitud:**
```json
{
  "puntuacion": 5,
  "comentario": "Las semillas germinaron muy rápido, excelente calidad."
}
```

**Validaciones:**
| Campo | Reglas |
|-------|--------|
| puntuacion | Requerido, Integer entre 1 y 5 |
| comentario | Requerido, mínimo 10 caracteres, máximo 500 caracteres |

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "productoId": 5,
  "usuarioId": 12,
  "nombreUsuario": "Juan Pérez",
  "puntuacion": 5,
  "comentario": "Las semillas germinaron muy rápido, excelente calidad.",
  "fechaCreacion": "2024-01-15T10:30:00Z",
  "verificado": true
}
```

**Errores posibles:**
| Código | Descripción |
|--------|-------------|
| 400 | Datos de entrada inválidos |
| 401 | No autenticado |
| 403 | El usuario no ha comprado este producto |
| 404 | Producto no encontrado |
| 409 | El usuario ya ha reseñado este producto |

---

### 3. Actualizar una Reseña

Permite a un usuario editar su propia reseña.

**Endpoint:** `PUT /api/productos/{productoId}/resenas/{resenaId}`

**Headers requeridos:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| productoId | Long | ID del producto |
| resenaId | Long | ID de la reseña a editar |

**Body de la solicitud:**
```json
{
  "puntuacion": 4,
  "comentario": "Las semillas germinaron bien, aunque algunas tardaron más de lo esperado."
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "productoId": 5,
  "usuarioId": 12,
  "nombreUsuario": "Juan Pérez",
  "puntuacion": 4,
  "comentario": "Las semillas germinaron bien, aunque algunas tardaron más de lo esperado.",
  "fechaCreacion": "2024-01-15T10:30:00Z",
  "verificado": true
}
```

**Errores posibles:**
| Código | Descripción |
|--------|-------------|
| 400 | Datos de entrada inválidos |
| 401 | No autenticado |
| 403 | No tiene permiso para editar esta reseña |
| 404 | Reseña no encontrada |

---

### 4. Eliminar una Reseña

Permite a un usuario eliminar su propia reseña o a un administrador eliminar cualquier reseña.

**Endpoint:** `DELETE /api/productos/{productoId}/resenas/{resenaId}`

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| productoId | Long | ID del producto |
| resenaId | Long | ID de la reseña a eliminar |

**Respuesta exitosa (204 No Content)**

**Errores posibles:**
| Código | Descripción |
|--------|-------------|
| 401 | No autenticado |
| 403 | No tiene permiso para eliminar esta reseña |
| 404 | Reseña no encontrada |

---

### 5. Obtener Resumen de Reseñas de un Producto

Obtiene un resumen estadístico de las reseñas de un producto.

**Endpoint:** `GET /api/productos/{productoId}/resenas/resumen`

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| productoId | Long | ID del producto |

**Respuesta exitosa (200 OK):**
```json
{
  "productoId": 5,
  "promedioCalificacion": 4.5,
  "totalResenas": 25,
  "distribucion": {
    "5": 15,
    "4": 5,
    "3": 3,
    "2": 1,
    "1": 1
  }
}
```

---

### 6. Obtener Reseñas de un Usuario

Obtiene todas las reseñas escritas por un usuario específico.

**Endpoint:** `GET /api/usuarios/{usuarioId}/resenas`

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| usuarioId | Long | ID del usuario |

**Parámetros de query (opcional):**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| page | Integer | Número de página (default: 0) |
| size | Integer | Elementos por página (default: 10) |

**Respuesta exitosa (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "productoId": 5,
      "productoNombre": "Semillas de Tomate Orgánico",
      "productoImagen": "https://ejemplo.com/producto.jpg",
      "puntuacion": 5,
      "comentario": "Las semillas germinaron muy rápido.",
      "fechaCreacion": "2024-01-15T10:30:00Z",
      "verificado": true
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

---

## Endpoints de Administración de Reseñas

### 7. Obtener Todas las Reseñas (Admin)

Obtiene todas las reseñas del sistema para moderación.

**Endpoint:** `GET /api/admin/resenas`

**Headers requeridos:**
```
Authorization: Bearer {token}
```
*Requiere rol de Administrador*

**Parámetros de query (opcional):**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| page | Integer | Número de página (default: 0) |
| size | Integer | Elementos por página (default: 20) |
| estado | String | Filtrar por estado: `pendiente`, `aprobado`, `rechazado` |
| puntuacionMinima | Integer | Filtrar por puntuación mínima |
| puntuacionMaxima | Integer | Filtrar por puntuación máxima |

**Respuesta exitosa (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "productoId": 5,
      "productoNombre": "Semillas de Tomate",
      "usuarioId": 12,
      "nombreUsuario": "Juan Pérez",
      "emailUsuario": "juan@ejemplo.com",
      "puntuacion": 5,
      "comentario": "Las semillas germinaron muy rápido.",
      "fechaCreacion": "2024-01-15T10:30:00Z",
      "estado": "aprobado",
      "verificado": true
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```

---

### 8. Moderar una Reseña (Admin)

Permite a un administrador aprobar o rechazar una reseña.

**Endpoint:** `PATCH /api/admin/resenas/{resenaId}/moderar`

**Headers requeridos:**
```
Authorization: Bearer {token}
Content-Type: application/json
```
*Requiere rol de Administrador*

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| resenaId | Long | ID de la reseña |

**Body de la solicitud:**
```json
{
  "estado": "aprobado",
  "motivoRechazo": null
}
```

O para rechazar:
```json
{
  "estado": "rechazado",
  "motivoRechazo": "Contenido inapropiado"
}
```

**Valores de estado:**
- `pendiente`: Reseña pendiente de revisión
- `aprobado`: Reseña visible públicamente
- `rechazado`: Reseña oculta

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "estado": "aprobado",
  "moderadoPor": "admin@ejemplo.com",
  "fechaModeracion": "2024-01-16T09:00:00Z"
}
```

---

### 9. Reportar una Reseña

Permite a un usuario reportar una reseña inapropiada.

**Endpoint:** `POST /api/productos/{productoId}/resenas/{resenaId}/reportar`

**Headers requeridos:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Parámetros de ruta:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| productoId | Long | ID del producto |
| resenaId | Long | ID de la reseña |

**Body de la solicitud:**
```json
{
  "motivo": "spam",
  "descripcion": "Esta reseña contiene publicidad de otro sitio."
}
```

**Motivos válidos:**
- `spam`: Contenido spam o publicidad
- `inapropiado`: Lenguaje ofensivo o inapropiado
- `falso`: Reseña falsa o engañosa
- `otro`: Otro motivo

**Respuesta exitosa (201 Created):**
```json
{
  "mensaje": "Reporte enviado exitosamente",
  "reporteId": 45
}
```

---

## Modelo de Datos

### Reseña (Review)
```json
{
  "id": "Long",
  "productoId": "Long",
  "usuarioId": "Long",
  "nombreUsuario": "String",
  "puntuacion": "Integer (1-5)",
  "comentario": "String",
  "fechaCreacion": "DateTime",
  "verificado": "Boolean (true si el usuario compró el producto)",
  "estado": "String (pendiente|aprobado|rechazado)"
}
```

### Resumen de Reseñas
```json
{
  "productoId": "Long",
  "promedioCalificacion": "Double",
  "totalResenas": "Integer",
  "distribucion": {
    "5": "Integer",
    "4": "Integer",
    "3": "Integer",
    "2": "Integer",
    "1": "Integer"
  }
}
```

---

## Consideraciones de Implementación

### Verificación de Compra
- Una reseña se marca como `verificado: true` si el usuario ha completado al menos un pedido que contenga el producto.
- Las reseñas verificadas deben mostrarse con un badge especial en el frontend.

### Una Reseña por Producto por Usuario
- Cada usuario solo puede tener una reseña activa por producto.
- Si intenta crear otra, debe recibir error 409 (Conflict).

### Moderación Automática
- Las reseñas pueden pasar por un filtro automático de palabras prohibidas.
- Si una reseña recibe más de 3 reportes, debe marcarse automáticamente como `pendiente` para revisión.

### Cálculo del Promedio
- El promedio de calificación del producto debe actualizarse automáticamente al crear/editar/eliminar reseñas.
- Considerar usar un trigger o evento para mantener consistencia.

---

## Ejemplos de Uso en Frontend

### Crear una reseña
```typescript
const crearResena = async (productoId: number, data: {
  puntuacion: number;
  comentario: string;
}) => {
  const response = await fetch(`/api/productos/${productoId}/resenas`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  });
  return response.json();
};
```

### Obtener reseñas con paginación
```typescript
const obtenerResenas = async (productoId: number, page = 0) => {
  const response = await fetch(
    `/api/productos/${productoId}/resenas?page=${page}&size=10&sort=fecha,desc`
  );
  return response.json();
};
```

---

## Notas para el Backend

1. **Autenticación**: Todos los endpoints que modifican datos requieren token JWT válido.
2. **Autorización**: Los usuarios solo pueden editar/eliminar sus propias reseñas.
3. **Validación**: Implementar validaciones de longitud y formato en el servidor.
4. **Rate Limiting**: Considerar límites para prevenir spam de reseñas.
5. **Índices**: Crear índices en `producto_id` y `usuario_id` para optimizar consultas.
