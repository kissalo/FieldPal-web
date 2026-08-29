<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:7F5AF0,50:A371F7,100:2CB67D&height=200&section=header&text=FieldPal&fontSize=64&fontColor=ffffff&animation=fadeIn&fontAlignY=38" alt="FieldPal" />
</p>

<p align="center">
  <strong>⚽ Sistema de Gestión y Reserva de Canchas Deportivas Locales</strong>
</p>

<p align="center">
  Una plataforma web para descubrir, gestionar y reservar canchas deportivas de forma rápida y organizada.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Jakarta%20EE-10-ED8B00?style=for-the-badge&logo=jakartaee&logoColor=white" alt="Jakarta EE" />
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/JSF-PrimeFaces-2CB67D?style=for-the-badge" alt="JSF PrimeFaces" />
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Status-In%20Development-F7C948?style=for-the-badge" alt="Status" />
</p>

---

## 🌟 About FieldPal

**FieldPal** es una plataforma web orientada a la **gestión y reserva de canchas deportivas locales**.

El proyecto nace como respuesta a la desorganización e ineficiencia que pueden producir los métodos tradicionales de reserva, como llamadas telefónicas o registros manuales.

La plataforma permite conectar a **jugadores** y **administradores de complejos deportivos** en un mismo sistema.

### 🏃 Para jugadores

Los jugadores pueden:

*  Consultar organizaciones y canchas disponibles.
*  Filtrar complejos deportivos por zona.
*  Consultar horarios disponibles.
*  Seleccionar una cancha y fecha.
*  Elegir un horario disponible.
*  Registrar el número de jugadores.
*  Consultar el costo total y el valor estimado por jugador.
*  Visualizar sus reservas.
*  Gestionar determinadas reservas.
*  Cancelar reservas.
*  Confirmar su asistencia.

### 🏢 Para administradores

Los administradores pueden:

*  Iniciar sesión de forma segura.
*  Configurar la información de su complejo deportivo.
*  Registrar y administrar canchas.
*  Configurar precios y características.
*  Configurar horarios de atención.
*  Visualizar un panel centralizado de reservas.
*  Buscar reservas por diferentes criterios.
*  Cancelar reservas.
*  Consultar indicadores de gestión.

Estas funcionalidades forman parte del alcance definido para el sistema.

---

## 🎯 Project Goals

FieldPal busca:

> **Digitalizar y optimizar el proceso de reserva de canchas deportivas, proporcionando una experiencia organizada tanto para jugadores como para administradores.**

Los principales objetivos son:

*  Reducir procesos manuales.
*  Evitar reservas duplicadas o conflictos de horarios.
*  Centralizar la información de las reservas.
*  Facilitar el cálculo de costos.
*  Mejorar la administración de complejos deportivos.
*  Diferenciar claramente los permisos según el rol del usuario.

El sistema valida automáticamente que no existan reservas duplicadas o solapamientos para una misma cancha.

---

## 🧠 System Architecture

FieldPal utiliza una arquitectura organizada por responsabilidades, separando la interacción con la interfaz, la lógica de negocio y la persistencia de datos.

```text
                    ┌─────────────────────────┐
                    │       PRESENTATION      │
                    │      JSF / PrimeFaces   │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │      CONTROLLERS        │
                    │       Managed Beans     │
                    │                         │
                    │ AuthBean                │
                    │ ReservaBean             │
                    │ HorariosBean            │
                    │ GestionBean             │
                    │ ProfileBean             │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │       SERVICES          │
                    │      Business Logic     │
                    │                         │
                    │ CourtService            │
                    │ ReservationService      │
                    │ ScheduleService         │
                    │ OrganizationService     │
                    │ UserService             │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │      REPOSITORIES        │
                    │       Persistence       │
                    │                         │
                    │ CourtRepository         │
                    │ ReservationRepository   │
                    │ TimeSlotRepository      │
                    │ OrganizationRepository  │
                    │ UserRepository          │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │       DATABASE          │
                    └─────────────────────────┘
```

La documentación del proyecto define controladores JSF, servicios de lógica de negocio y repositorios específicos para las entidades del dominio.

---

## 🏗️ Domain Model

El sistema trabaja principalmente con las siguientes entidades:

| Entity            | Description                      |
| ----------------- | -------------------------------- |
|  `User`         | Usuarios del sistema y sus roles |
|  `Organization` | Complejos deportivos             |
|  `Court`       | Canchas deportivas               |
|  `Reservation`  | Reservas realizadas              |
|  `TimeSlot`      | Bloques horarios de las canchas  |

### Enums

* `CourtType` → Fútbol, Vóley, Tenis y Pádel.
* `ReservationStatus` → `UPCOMING`, `COMPLETED`, `CANCELLED`.
* `UserRole` → `PLAYER` y `ADMIN`.
* `Zone` → Clasificación geográfica de las instalaciones.

---

## 🔄 Reservation Flow

El proceso principal de FieldPal sigue este flujo:

```text
👤 Player
   │
   ▼
🔎 Explore organizations
   │
   ▼
🏟️ Select court
   │
   ▼
📅 Select date
   │
   ▼
⏰ Check availability
   │
   ▼
📝 Enter reservation data
   │
   ▼
🔐 Validate availability
   │
   ▼
💾 Create Reservation
   │
   ▼
⏱️ Create TimeSlot
   │
   ▼
✅ Reservation confirmed
```

Durante la creación de una reserva, el sistema vuelve a comprobar la disponibilidad antes de registrar la entidad `Reservation` y bloquear el horario mediante `TimeSlot`.

---

## 🧩 Main Components

### 🎨 Controllers — JSF / Managed Beans

Los principales controladores incluyen:

```text
AuthBean
GestionBean
HomeBean
HorariosBean
MisReservasBean
ProfileBean
ReservaBean
WizardBean
```

Estos componentes gestionan la interacción entre las vistas y la lógica de aplicación.

### ⚙️ Services

```text
CourtService
OrganizationService
ReservationService
ScheduleService
UserService
CrudGenericService
```

Los servicios concentran las operaciones de negocio y persistencia de cada módulo.

### 🗄️ Repositories

```text
CourtRepository
OrganizationRepository
ReservationRepository
TimeSlotRepository
UserRepository
```

Los repositorios encapsulan las operaciones de acceso y consulta de datos.

---

## 🧱 SOLID Principles

El proyecto incorpora principios de diseño **SOLID** para favorecer la mantenibilidad y separación de responsabilidades.

### 1. Single Responsibility Principle — SRP

Los controladores se encargan de la interacción con la interfaz, mientras que los servicios concentran la lógica correspondiente a cada dominio.

Por ejemplo:

```text
AuthBean
   ↓
Interacción con usuario / navegación

UserService
   ↓
Lógica de usuarios / autenticación / persistencia
```

### 2. Open / Closed Principle — OCP

`CrudGenericService` centraliza operaciones comunes de persistencia para que los repositorios puedan reutilizarlas y agregar consultas específicas sin modificar la lógica CRUD general.

### 3. Dependency Inversion Principle — DIP

El proyecto utiliza **CDI / Jakarta EE** mediante `@Inject` para proporcionar dependencias a los controladores.

```java
@Inject
private ReservationService reservationService;
```

Esto reduce el acoplamiento entre los controladores y los servicios.

---

## 🛠️ Tech Stack

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Jakarta%20EE-ED8B00?style=for-the-badge&logo=jakartaee&logoColor=white" />
  <img src="https://img.shields.io/badge/JSF-PrimeFaces-2CB67D?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" />
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" />
</p>

| Technology    | Purpose                         |
| ------------- | ------------------------------- |
| ☕ **Java 21** | Main programming language       |
| Jakarta EE    | Enterprise application platform |
| JSF           | Web interface                   |
| PrimeFaces    | Interactive UI components       |
| CDI           | Dependency Injection            |
| Maven         | Build & dependency management   |
| Docker        | Containerization                |
| Git / GitHub  | Version control                 |

---

## 🚀 Getting Started

### 📋 Requirements

Before running FieldPal, make sure you have:

* **Java SE 21**
* **Docker** *(optional, if using containers)*
* A compatible Jakarta EE runtime
* Git

The project includes the **Maven Wrapper**, so Maven does not need to be installed globally.

---

## ▶️ Run with Maven

### 1. Clone the repository

```bash
git clone https://github.com/kiarasalome/FieldPal-web.git
cd FieldPal-web
```

### 2. Give execution permission to Maven Wrapper

On Linux/macOS:

```bash
chmod +x mvnw
```

### 3. Build and run

```bash
./mvnw clean package liberty:run
```

Once the runtime starts, open:

```text
http://localhost:9081
```

---

## 🐳 Run with Docker

### 1. Build the application

```bash
./mvnw clean package
```

### 2. Build the Docker image

```bash
docker build -t fieldpal-web:v1 .
```

### 3. Run the container

```bash
docker run -it --rm -p 9081:9081 fieldpal-web:v1
```

Then access:

```text
http://localhost:9081/
```

---

## 🗄️ Database Configuration

FieldPal includes configuration for database connectivity through:

```text
docker-compose
docker-env
init-sql
```

The project documentation also defines persistent tables for:

```text
Users
Courts
Organizations
Reservations
Time-slots
```

> ⚠️ The exact database engine, credentials and environment variables should be documented here according to the final repository configuration.

---

## 📸 Application Preview

### 🏠 Homepage

<p align="center">
 <img width="1030" height="603" alt="image" src="https://github.com/user-attachments/assets/c97d0077-961f-4a08-abc2-15471d991fcf" />

</p>

### 🔐 Login

<p align="center">
  <img width="966" height="503" alt="image" src="https://github.com/user-attachments/assets/6ba28111-d1f1-45ce-a64d-48b42c393c55" />

</p>

### 🏢 Administrator Dashboard

<p align="center">
  <img width="1122" height="650" alt="image" src="https://github.com/user-attachments/assets/449144db-b01a-4a71-a5ab-de2c9d016d64" />

</p>

### 🏟️ Court Configuration

<p align="center">
 <img width="1126" height="646" alt="image" src="https://github.com/user-attachments/assets/77c9ed28-4199-4e08-8a88-3bd4f51e9169" />

</p>

### 📅 Reservation

<p align="center">
 <img width="1112" height="642" alt="image" src="https://github.com/user-attachments/assets/76b6b500-7017-42e8-93ab-78dc852833ad" />

</p>


---

## 👥 Team

<p align="center">

| Member                | Role                       |
| --------------------- | -------------------------- |
| **Darío Chillogallo** |  Frontend Developer      |
| **Kiara Condoy**      |  Project Manager         |
| **Javier Guarnizo**   |  Backend Developer       |
| **Domenica Narvaez**  |  Database Administrator |
| **José Valencia**     |  QA / Tester             |

</p>

Los roles corresponden a la distribución definida en la documentación del proyecto.

---

## 📚 Documentation

La documentación técnica del proyecto incluye:

* 📐 Arquitectura del sistema
* 🧩 Modelo de dominio
* 📦 Diagrama de paquetes
* 🔄 Casos de uso
* ⚙️ Lógica de negocio
* 🧱 Principios SOLID
* 🗄️ Configuración de base de datos
* 🚀 Despliegue
* 🖥️ Vistas de la aplicación

---

## 🔗 Repository

<p align="center">

<a href="https://github.com/kiarasalome/FieldPal-web">
  <img src="https://img.shields.io/badge/GitHub-FieldPal--web-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub Repository" />
</a>

</p>

---

## 🌱 Future Improvements

Algunas posibles extensiones del sistema podrían incluir:

*  Integración de pagos en línea.
*  Notificaciones de reservas.
*  Diseño responsive avanzado.
*  Estadísticas más detalladas.
*  Gestión de torneos.
*  Sistema de calificaciones y reseñas.
*  Mayor separación mediante interfaces para servicios.

La última propuesta está alineada con la propia documentación, que señala que para una aplicación estricta del DIP podrían definirse interfaces para servicios como `ReservationServiceInterface` o `CourtServiceInterface`.

---

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:7F5AF0,50:A371F7,100:2CB67D&height=120&section=footer" alt="FieldPal Footer" />
</p>

<p align="center">
  <strong>⚽ FieldPal</strong>
  <br>
  <i>Making sports reservations simpler, smarter and more organized.</i>
</p>

<p align="center">
  ⭐ If you found this project useful, consider giving it a star!
</p>
