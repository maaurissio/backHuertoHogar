# 🚨 INFORME URGENTE: Problemas en Endpoint de Productos

**Fecha:** 1 de diciembre de 2025  
**Prioridad:** ALTA  
**Afecta:** Visualización de productos en catálogo

---

## 📋 Resumen de Problemas

| # | Problema | Estado Actual | Estado Esperado |
|---|----------|---------------|-----------------|
| 1 | Campo `is_Activo` devuelve valor binario | `0x01` | `"Activo"` o `"Inactivo"` |
| 2 | Campo `codigo` no viene en la respuesta | `undefined` o vacío | `"FR001"`, `"VR002"`, etc. |

---

## 🔴 Problema 1: Campo `is_Activo` devuelve valor binario

### Situación Actual
El endpoint `GET /api/productos` devuelve el campo `is_Activo` como un valor binario de MySQL (`0x01`), en lugar de un string legible.

**Respuesta actual:**
```json
{
  "success": true,
  "productos": [
    {
      "id": 1,
      "nombre": "Manzanas Fuji",
      "is_Activo": "0x01"  // ❌ INCORRECTO - Valor binario
    }
  ]
}
```

### Solución Requerida
Convertir el valor BIT de MySQL a string antes de devolverlo.

**Opción A - En la query SQL:**
```sql
SELECT 
  id,
  codigo,
  nombre,
  descripcion,
  precio,
  stock,
  imagen,
  categoria,
  CASE WHEN is_Activo = 1 THEN 'Activo' ELSE 'Inactivo' END AS isActivo,
  fecha_creacion AS fechaCreacion,
  peso
FROM productos;
```

**Opción B - En el código del backend (Node.js):**
```javascript
// Después de obtener los productos de la BD
const productosFormateados = productos.map(producto => ({
  ...producto,
  isActivo: producto.is_Activo ? 'Activo' : 'Inactivo',
  // Eliminar el campo original si es necesario
}));
```

**Opción C - Cambiar tipo de columna en BD:**
```sql
ALTER TABLE productos 
MODIFY COLUMN is_Activo ENUM('Activo', 'Inactivo') DEFAULT 'Activo';
```

**Respuesta esperada:**
```json
{
  "success": true,
  "productos": [
    {
      "id": 1,
      "nombre": "Manzanas Fuji",
      "isActivo": "Activo"  // ✅ CORRECTO
    }
  ]
}
```

---

## 🔴 Problema 2: Campo `codigo` no viene en la respuesta

### Situación Actual
Los productos no incluyen el campo `codigo` en la respuesta, lo cual afecta la visualización en el catálogo.

**Visualización actual:**
```
- Manzanas Fuji        ❌ Falta el código
```

**Visualización esperada:**
```
FR001 - Manzanas Fuji  ✅ Con código
```

### Solución Requerida
Asegurarse de incluir el campo `codigo` en la respuesta de productos.

**Verificar en la query:**
```sql
SELECT 
  id,
  codigo,        -- ← ASEGURAR QUE ESTE CAMPO ESTÉ INCLUIDO
  nombre,
  descripcion,
  precio,
  stock,
  imagen,
  categoria,
  CASE WHEN is_Activo = 1 THEN 'Activo' ELSE 'Inactivo' END AS isActivo,
  fecha_creacion AS fechaCreacion,
  peso
FROM productos;
```

**Verificar que la columna exista en la tabla:**
```sql
DESCRIBE productos;

-- Si no existe, crearla:
ALTER TABLE productos 
ADD COLUMN codigo VARCHAR(20) NOT NULL AFTER id;

-- Y poblarla con datos:
UPDATE productos SET codigo = 'FR001' WHERE id = 1;
UPDATE productos SET codigo = 'FR002' WHERE id = 2;
-- etc.
```

---

## 📦 Formato de Respuesta Esperado

### GET /api/productos

```json
{
  "success": true,
  "productos": [
    {
      "id": 1,
      "codigo": "FR001",
      "nombre": "Manzanas Fuji",
      "descripcion": "Manzanas Fuji crujientes y dulces...",
      "precio": 1200,
      "stock": 150,
      "imagen": "https://res.cloudinary.com/dcpufkkbl/image/upload/v1764556691/FR001-Manzana-Fuji_gwfzyd.webp",
      "categoria": "Frutas Frescas",
      "isActivo": "Activo",
      "fechaCreacion": "2025-01-01",
      "peso": "1kg"
    },
    {
      "id": 2,
      "codigo": "FR002",
      "nombre": "Naranjas Valencia",
      "descripcion": "Jugosas y ricas en vitamina C...",
      "precio": 1000,
      "stock": 200,
      "imagen": "https://res.cloudinary.com/dcpufkkbl/image/upload/v1764556693/FR002-Naranja-Valencia_y2ixam.webp",
      "categoria": "Frutas Frescas",
      "isActivo": "Activo",
      "fechaCreacion": "2025-01-02",
      "peso": "1kg"
    }
  ]
}
```

---

## 📝 Mapeo de Nombres de Campos

El frontend espera estos nombres (camelCase):

| Campo en Frontend | Campo en BD (sugerido) |
|-------------------|------------------------|
| `id` | `id` |
| `codigo` | `codigo` |
| `nombre` | `nombre` |
| `descripcion` | `descripcion` |
| `precio` | `precio` |
| `stock` | `stock` |
| `imagen` | `imagen` |
| `categoria` | `categoria` |
| `isActivo` | `is_Activo` (convertir a string) |
| `fechaCreacion` | `fecha_creacion` |
| `peso` | `peso` |

---

## ✅ Checklist de Validación

Después de aplicar los cambios, verificar:

- [ ] `GET /api/productos` devuelve array con productos
- [ ] Cada producto tiene campo `codigo` (ej: "FR001")
- [ ] Campo `isActivo` es string `"Activo"` o `"Inactivo"`
- [ ] Nombres de campos en camelCase
- [ ] Imágenes con URLs completas de Cloudinary

---

## 🧪 Test Rápido

```bash
curl http://localhost:3000/api/productos | jq '.productos[0]'
```

**Salida esperada:**
```json
{
  "id": 1,
  "codigo": "FR001",
  "nombre": "Manzanas Fuji",
  "isActivo": "Activo"
}
```

---

## 📞 Contacto

Si hay dudas sobre el formato esperado, revisar el archivo:
- `src/types/models.ts` - Interfaces TypeScript del frontend
- `src/services/productos.api.service.ts` - Consumo de la API

---

**⚠️ IMPORTANTE:** Estos cambios son bloqueantes para la visualización correcta del catálogo de productos.
