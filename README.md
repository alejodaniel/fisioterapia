#  Proyecto Fisioterapia - Guía de Levantamiento

Este repositorio contiene un sistema completo de fisioterapia con análisis de postura en tiempo real utilizando inteligencia artificial.

##  Arquitectura y Puertos del Sistema

* **Frontend (Angular 17):** `http://localhost:4200`
* **Backend (Spring Boot 4 / Java 25):** `http://localhost:8080`
* **Servidor de IA (Python / MediaPipe):** `ws://localhost:5000` (Conexión WebSocket)
* **Base de Datos (PostgreSQL):** `localhost:5432`

---

##  Requisitos Previos Generales

Antes de empezar, asegúrate de tener instalado:
* **Node.js** (Versión 18 o 20 recomendada)
* **Java Development Kit (JDK)** (Versión 21 o superior, preferiblemente **JDK 25** según configuración del proyecto)
* **PostgreSQL** (Servidor corriendo localmente)
* **Python** (Versión 3.8 a 3.11 recomendada para mejor compatibilidad con MediaPipe y OpenCV)
* **Git**

---

## 1. Base de Datos (PostgreSQL)

El backend de Spring Boot está configurado para conectarse a PostgreSQL. Sigue estos pasos para prepararla:

1. Abre tu administrador de base de datos de PostgreSQL (como **pgAdmin** o desde consola `psql`).
2. Crea una base de datos llamada:
   ```sql
   CREATE DATABASE fisioterapiadb;
   ```
3. Verifica que las credenciales de acceso coincidan con la configuración actual del backend (`backend/fisioterapia-backend-services/src/main/resources/application.yaml`):
   * **URL:** `jdbc:postgresql://localhost:5432/fisioterapiadb`
   * **Usuario:** `postgres`
   * **Contraseña:** `12345678` *(Si tienes otra contraseña, actualízala temporalmente en ese archivo).*

---

## 2.  Servidor de Inteligencia Artificial (Python)
Ubicación: `mock-ai-server/`

Este componente analiza en tiempo real los cuadros de video de la webcam (enviados por Angular en Base64) usando **MediaPipe** y retorna conteo de repeticiones, errores cometidos y retroalimentación interactiva.

### Pasos para levantar:
1. Abre tu terminal y ve a la carpeta del servidor de IA:
   ```bash
   cd mock-ai-server
   ```
2. *(Recomendado)* Crea y activa un entorno virtual de Python para mantener las dependencias aisladas:
   * **En Windows (PowerShell):**
     ```powershell
     python -m venv .venv
     .venv\Scripts\Activate.ps1
     ```
   * **En Linux/macOS:**
     ```bash
     python3 -m venv .venv
     source .venv/bin/activate
     ```
3. Instala las librerías necesarias:
   ```bash
   pip install opencv-python numpy mediapipe websockets
   ```
4. Ejecuta el servidor:
   ```bash
   python server.py
   ```
   *Deberías ver el mensaje: `Starting REAL-TIME AI computer vision WebSocket server on ws://localhost:5000 ...`*

---

## 3.  Backend (Spring Boot / Gradle)
Ubicación: `backend/`

Gestiona los servicios de negocio, autenticación JWT, registro de usuarios y registro de sesiones de terapia.

### Pasos para levantar:
1. Abre una nueva terminal y ve a la carpeta del backend:
   ```bash
   cd backend
   ```
2. Compila el proyecto con Gradle:
   * **En Windows:**
     ```powershell
     .\gradlew build -x test
     ```
   * **En Linux/macOS:**
     ```bash
     ./gradlew build -x test
     ```
3. Ejecuta la aplicación de Spring Boot:
   * **En Windows:**
     ```powershell
     .\gradlew :fisioterapia-backend-services:bootRun
     ```
   * **En Linux/macOS:**
     ```bash
     ./gradlew :fisioterapia-backend-services:bootRun
     ```
   *(También puedes abrir la carpeta `backend` en IntelliJ IDEA, dejar que cargue las dependencias de Gradle y ejecutar el archivo principal `BackendApplication.java` directamente desde el editor).*

---

## 4.  Frontend (Angular)
Ubicación: `fisio-frontend/`

Interfaz de usuario web interactiva que solicita permisos de cámara web, envía frames al servidor de IA vía WebSockets y consume los servicios REST del backend de Spring Boot.

### Pasos para levantar:
1. Abre otra terminal y ve a la carpeta del frontend:
   ```bash
   cd fisio-frontend
   ```
2. Instala las dependencias de Node.js:
   ```bash
   npm install
   ```
3. *(Opcional)* Instala el CLI de Angular globalmente si no lo tienes:
   ```bash
   npm install -g @angular/cli@17
   ```
4. Inicia el servidor de desarrollo:
   ```bash
   npm start
   ```
   *(O alternativamente: `ng serve`)*
5. Abre en tu navegador favorito:
    **`http://localhost:4200`**

---

##  Credenciales de Acceso por Defecto

Al iniciar el Backend de Spring Boot, se crearán automáticamente los siguientes usuarios de prueba en tu base de datos (definidos en [BackendApplication.java](file:///c:/Users/alejo/OneDrive/Desktop/UNIBE%20EJERCICIOS/ALGREBRA%20LINEAL/fisioterapia/backend/fisioterapia-backend-services/src/main/java/com/fisio/backend/BackendApplication.java)):

### 1. Rol Paciente
* **Correo/Usuario:** `paciente@fisioterapia.com`
* **Contraseña:** `password123`

### 2. Rol Médico
* **Correo/Usuario:** `medico@fisioterapia.com`
* **Contraseña:** `password123`