Gestor de Tareas Académicas
**Curso:** Programación para Dispositivos Móviles  
**Integrantes:** Juan Mauricio Arias Hernández
**Temática:** Gestor de tareas académicas (opción 5)

---

## 📱 Descripción del Proyecto

Aplicación móvil Android nativa que permite a los estudiantes gestionar sus tareas académicas. Incluye registro de usuarios, autenticación y CRUD completo de tareas con prioridades y fechas de entrega.

---

## 🏗️ Arquitectura del Sistema

### Frontend (App Móvil)
- **Tecnología:** Android nativo con Kotlin
- **SDK mínimo:** Android 7.0 (API 24)
- **Patrón:** MVVM con Repository Pattern
- **Bibliotecas principales:**
  - Retrofit 2.9.0 (comunicación HTTP)
  - Coroutines (programación asíncrona)
  - ViewBinding (binding de vistas)
  - Material Components (UI)

### Backend (API REST)
- **Tecnología:** Node.js con Express.js
- **Puerto:** 3000
- **Autenticación:** Tokens UUID en base de datos
- **Validación:** Bcrypt para contraseñas

### Base de Datos
- **Sistema:** MySQL 8.0
- **Tablas:**
  1. `usuarios` (id, nombre, email, password, created_at)
  2. `sesiones` (id, usuario_id, token, created_at)
  3. `tareas` (id, usuario_id, titulo, descripcion, materia, fecha_entrega, prioridad, completada, created_at, updated_at)

---

## 🔌 Endpoints de la API

### Autenticación

**POST** `/api/register`
```json
Request:
{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "password": "123456"
}

Response (201):
{
  "message": "Usuario registrado exitosamente",
  "userId": 1
}
```

**POST** `/api/login`
```json
Request:
{
  "email": "juan@example.com",
  "password": "123456"
}

Response (200):
{
  "message": "Inicio de sesión exitoso",
  "token": "uuid-token-here",
  "userId": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com"
}
```

**POST** `/api/logout`
```
Headers: Authorization: Bearer {token}

Response (200):
{
  "message": "Sesión cerrada exitosamente"
}
```

### CRUD de Tareas

**GET** `/api/tareas`
```
Headers: Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "usuario_id": 1,
    "titulo": "Estudiar para examen",
    "descripcion": "Matemáticas capítulos 1-5",
    "materia": "Matemáticas",
    "fecha_entrega": "2024-12-31",
    "prioridad": "alta",
    "completada": false,
    "created_at": "2024-11-27T...",
    "updated_at": "2024-11-27T..."
  }
]
```

**GET** `/api/tareas/{id}`
```
Headers: Authorization: Bearer {token}

Response (200): (mismo formato que arriba, un solo objeto)
```

**POST** `/api/tareas`
```json
Headers: Authorization: Bearer {token}

Request:
{
  "titulo": "Nueva tarea",
  "descripcion": "Descripción opcional",
  "materia": "Física",
  "fecha_entrega": "2024-12-25",
  "prioridad": "media"
}

Response (201):
{
  "message": "Tarea creada exitosamente",
  "tarea": { ... }
}
```

**PUT** `/api/tareas/{id}`
```json
Headers: Authorization: Bearer {token}

Request:
{
  "titulo": "Tarea actualizada",
  "completada": true
}

Response (200):
{
  "message": "Tarea actualizada exitosamente",
  "tarea": { ... }
}
```

**DELETE** `/api/tareas/{id}`
```
Headers: Authorization: Bearer {token}

Response (200):
{
  "message": "Tarea eliminada exitosamente"
}
```

---

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Node.js 16+
- MySQL 8.0
- Android Studio
- JDK 8+

### Backend

1. **Instalar dependencias:**
```bash
cd backend/gestor-tareas-backend
npm install
```

2. **Crear base de datos:**
```sql
CREATE DATABASE gestor_tareas;
USE gestor_tareas;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sesiones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE tareas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    materia VARCHAR(100),
    fecha_entrega DATE,
    prioridad ENUM('baja', 'media', 'alta') DEFAULT 'media',
    completada BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
```

3. **Configurar .env:**
```env
PORT=3000
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=12345
DB_NAME=gestor_tareas
```

4. **Ejecutar servidor:**
```bash
npm run dev
```

El servidor estará disponible en `http://localhost:3000`

### Frontend (App Android)

1. **Abrir proyecto en Android Studio:**
   - Abrir carpeta `GestorTareasAcademicas`
   - Esperar sincronización de Gradle

2. **Configurar URL del backend:**
   - Archivo: `app/src/main/java/.../network/RetrofitClient.kt`
   - Para emulador: `http://10.0.2.2:3000/api/`
   - Para dispositivo físico: `http://TU_IP_LOCAL:3000/api/`

3. **Ejecutar app:**
   - Click en "Run" o Shift+F10
   - Seleccionar emulador o dispositivo

---

## 🔗 Conexión App-Backend

**Opción seleccionada:** Opción A (red local directa)

- **Emulador Android:** Usa `10.0.2.2` que apunta al `localhost` del host
- **Dispositivo físico:** Requiere estar en la misma red WiFi y usar la IP local del equipo

**Alternativa con túnel (opcional):**
```bash
# Usando LocalTunnel
npx localtunnel --port 3000

# Cambiar BASE_URL en RetrofitClient.kt a la URL generada
```

---

Contenido de la aplicación:
1. Registro de usuario
2. Inicio de sesión
3. Crear tarea nueva
4. Listar tareas
5. Editar tarea
6. Marcar como completada
7. Eliminar tarea

---

## ✅ Funcionalidades Implementadas

- ✅ Registro de usuario con validación
- ✅ Inicio de sesión con token
- ✅ Cifrado de contraseñas (bcrypt)
- ✅ CRUD completo de tareas
- ✅ Prioridades (baja, media, alta)
- ✅ Fechas de entrega
- ✅ Estado completada/pendiente
- ✅ Filtrado por usuario autenticado
- ✅ Swipe-to-refresh
- ✅ Manejo de errores
- ✅ Validación de campos

---

## 🧪 Pruebas

### Backend
Probar con Postman o curl:
```bash
# Health check
curl http://localhost:3000/health

# Registro
curl -X POST http://localhost:3000/api/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","email":"test@test.com","password":"123456"}'
```

### App Android
- Probado en emulador Android 14 (API 34)
- Probado en dispositivo físico Android 7.0+

---



