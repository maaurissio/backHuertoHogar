# 📢 Migración de Reseñas: LocalStorage → API

## ⚠️ Cambio Importante

**Las reseñas ya NO se almacenan en LocalStorage.**

A partir de ahora, todas las reseñas se gestionan a través de la API REST del backend y se persisten en la base de datos MySQL.

---

## 🔄 ¿Qué cambia?

| Antes (LocalStorage) | Ahora (API) |
|---------------------|-------------|
| `localStorage.getItem('reviews')` | `GET /api/productos/{id}/resenas` |
| `localStorage.setItem('reviews', ...)` | `POST /api/productos/{id}/resenas` |
| Datos perdidos al limpiar navegador | Datos persistentes en BD |
| Sin validación de compra | Solo usuarios que compraron pueden reseñar |
| Sin moderación | Sistema de moderación incluido |

---

## 🔗 Endpoints Disponibles

### Base URL
```
http://localhost:3000/api
```

---

### 1. Obtener Reseñas de un Producto (Público)

```http
GET /api/productos/{productoId}/resenas
```

**Query Params opcionales:**
| Param | Tipo | Default | Descripción |
|-------|------|---------|-------------|
| `page` | int | 0 | Número de página |
| `size` | int | 10 | Elementos por página |
| `sort` | string | `fecha,desc` | Ordenamiento |

**Ejemplo:**
```javascript
const response = await fetch('/api/productos/1/resenas?page=0&size=10&sort=fecha,desc');
const data = await response.json();

// Respuesta:
{
  "content": [
    {
      "id": 1,
      "productoId": 1,
      "usuarioId": 5,
      "nombreUsuario": "Juan Pérez",
      "puntuacion": 5,
      "comentario": "Excelente producto, muy fresco.",
      "fechaCreacion": "2025-11-30T10:30:00",
      "verificado": true  // ← El usuario compró el producto
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "number": 0,
  "size": 10
}
```

---

### 2. Obtener Resumen/Estadísticas (Público)

```http
GET /api/productos/{productoId}/resenas/resumen
```

**Ejemplo:**
```javascript
const response = await fetch('/api/productos/1/resenas/resumen');
const data = await response.json();

// Respuesta:
{
  "productoId": 1,
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

### 3. Crear una Reseña (Requiere Auth + Compra)

```http
POST /api/productos/{productoId}/resenas
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "puntuacion": 5,
  "comentario": "Las manzanas llegaron muy frescas, excelente calidad."
}
```

**Validaciones:**
- `puntuacion`: Requerido, entre 1 y 5
- `comentario`: Requerido, entre 10 y 500 caracteres

**Ejemplo:**
```javascript
const response = await fetch('/api/productos/1/resenas', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    puntuacion: 5,
    comentario: "Las manzanas llegaron muy frescas, excelente calidad."
  })
});
```

**Errores posibles:**
| Código | Error | Significado |
|--------|-------|-------------|
| 403 | `NO_HA_COMPRADO_PRODUCTO` | El usuario no ha comprado este producto |
| 409 | `RESENA_DUPLICADA` | El usuario ya reseñó este producto |

---

### 4. Editar mi Reseña

```http
PUT /api/productos/{productoId}/resenas/{resenaId}
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "puntuacion": 4,
  "comentario": "Actualizo mi reseña, el producto estuvo bien pero tardó en llegar."
}
```

---

### 5. Eliminar mi Reseña

```http
DELETE /api/productos/{productoId}/resenas/{resenaId}
```

**Headers:**
```
Authorization: Bearer {token}
```

**Respuesta:** `204 No Content`

---

### 6. Reportar una Reseña

```http
POST /api/productos/{productoId}/resenas/{resenaId}/reportar
```

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "motivo": "spam",
  "descripcion": "Esta reseña es publicidad de otro sitio."
}
```

**Motivos válidos:** `spam`, `inapropiado`, `falso`, `otro`

---

### 7. Obtener mis Reseñas

```http
GET /api/usuarios/{usuarioId}/resenas
```

**Headers:**
```
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "productoId": 1,
      "productoNombre": "Manzanas Rojas",
      "productoImagen": "https://...",
      "puntuacion": 5,
      "comentario": "Excelente producto",
      "fechaCreacion": "2025-11-30T10:30:00",
      "verificado": true
    }
  ]
}
```

---

## 💻 Ejemplo de Implementación React

### Hook para Reseñas

```typescript
// hooks/useResenas.ts
import { useState, useEffect } from 'react';

interface Resena {
  id: number;
  productoId: number;
  usuarioId: number;
  nombreUsuario: string;
  puntuacion: number;
  comentario: string;
  fechaCreacion: string;
  verificado: boolean;
}

interface ResumenResenas {
  productoId: number;
  promedioCalificacion: number;
  totalResenas: number;
  distribucion: Record<number, number>;
}

export const useResenas = (productoId: number) => {
  const [resenas, setResenas] = useState<Resena[]>([]);
  const [resumen, setResumen] = useState<ResumenResenas | null>(null);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchResenas = async () => {
    setLoading(true);
    try {
      const response = await fetch(
        `/api/productos/${productoId}/resenas?page=${page}&size=10`
      );
      const data = await response.json();
      setResenas(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Error fetching resenas:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchResumen = async () => {
    try {
      const response = await fetch(
        `/api/productos/${productoId}/resenas/resumen`
      );
      const data = await response.json();
      setResumen(data);
    } catch (error) {
      console.error('Error fetching resumen:', error);
    }
  };

  const crearResena = async (puntuacion: number, comentario: string) => {
    const token = localStorage.getItem('token');
    
    const response = await fetch(`/api/productos/${productoId}/resenas`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ puntuacion, comentario })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Error al crear reseña');
    }

    // Refrescar datos
    await fetchResenas();
    await fetchResumen();
    
    return response.json();
  };

  useEffect(() => {
    fetchResenas();
    fetchResumen();
  }, [productoId, page]);

  return {
    resenas,
    resumen,
    loading,
    page,
    totalPages,
    setPage,
    crearResena,
    refetch: () => {
      fetchResenas();
      fetchResumen();
    }
  };
};
```

### Componente de Estrellas

```tsx
// components/StarRating.tsx
interface StarRatingProps {
  rating: number;
  onRatingChange?: (rating: number) => void;
  readonly?: boolean;
}

export const StarRating = ({ rating, onRatingChange, readonly = false }: StarRatingProps) => {
  return (
    <div className="flex gap-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type="button"
          disabled={readonly}
          onClick={() => onRatingChange?.(star)}
          className={`text-2xl ${
            star <= rating ? 'text-yellow-400' : 'text-gray-300'
          } ${readonly ? 'cursor-default' : 'cursor-pointer hover:scale-110'}`}
        >
          ★
        </button>
      ))}
    </div>
  );
};
```

### Componente de Lista de Reseñas

```tsx
// components/ListaResenas.tsx
import { useResenas } from '../hooks/useResenas';
import { StarRating } from './StarRating';

export const ListaResenas = ({ productoId }: { productoId: number }) => {
  const { resenas, resumen, loading, page, totalPages, setPage } = useResenas(productoId);

  if (loading) return <div>Cargando reseñas...</div>;

  return (
    <div className="space-y-6">
      {/* Resumen */}
      {resumen && (
        <div className="bg-gray-50 p-4 rounded-lg">
          <div className="flex items-center gap-4">
            <span className="text-4xl font-bold">{resumen.promedioCalificacion}</span>
            <div>
              <StarRating rating={Math.round(resumen.promedioCalificacion)} readonly />
              <p className="text-sm text-gray-600">{resumen.totalResenas} reseñas</p>
            </div>
          </div>
        </div>
      )}

      {/* Lista de reseñas */}
      <div className="space-y-4">
        {resenas.map((resena) => (
          <div key={resena.id} className="border-b pb-4">
            <div className="flex items-center gap-2">
              <span className="font-semibold">{resena.nombreUsuario}</span>
              {resena.verificado && (
                <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">
                  ✓ Compra verificada
                </span>
              )}
            </div>
            <StarRating rating={resena.puntuacion} readonly />
            <p className="mt-2 text-gray-700">{resena.comentario}</p>
            <p className="text-sm text-gray-500 mt-1">
              {new Date(resena.fechaCreacion).toLocaleDateString()}
            </p>
          </div>
        ))}
      </div>

      {/* Paginación */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2">
          <button 
            disabled={page === 0}
            onClick={() => setPage(p => p - 1)}
          >
            Anterior
          </button>
          <span>{page + 1} de {totalPages}</span>
          <button 
            disabled={page >= totalPages - 1}
            onClick={() => setPage(p => p + 1)}
          >
            Siguiente
          </button>
        </div>
      )}
    </div>
  );
};
```

---

## ⚠️ Importante: Eliminar Código de LocalStorage

Buscar y eliminar cualquier código que use localStorage para reseñas:

```javascript
// ❌ ELIMINAR ESTO:
localStorage.getItem('reviews')
localStorage.setItem('reviews', JSON.stringify(...))
localStorage.removeItem('reviews')

// ✅ USAR LA API EN SU LUGAR
```

---

## 🔒 Reglas de Negocio

1. **Solo usuarios autenticados** pueden crear reseñas
2. **Solo usuarios que compraron el producto** pueden reseñar (estado del pedido: `entregado`)
3. **Una reseña por producto por usuario** (si intenta crear otra, error 409)
4. **Badge "Compra verificada"** se muestra automáticamente si `verificado: true`
5. **Moderación automática**: 3+ reportes → reseña pasa a revisión

---

## 📞 Contacto

Si tienen dudas sobre la implementación, contactar al equipo de backend.

**Base URL de producción:** Pendiente de configurar  
**Base URL desarrollo:** `http://localhost:3000/api`
