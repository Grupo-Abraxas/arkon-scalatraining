# Proyecto: API de Puntos de Acceso WiFi

## Introducción

Este proyecto implementa una API basada en GraphQL desarrollada en Scala, que permite gestionar y consultar información sobre puntos de acceso WiFi. La solución se ejecuta dentro de un contenedor Docker, simplificando su despliegue y operación. La base de datos utilizada es PostgreSQL, y se han habilitado las extensiones `cube` y `earthdistance` para realizar cálculos geoespaciales.

Adicionalmente, el proyecto incluye un módulo llamado `ingest` que ejecuta un script encargado de obtener y cargar los datos iniciales de los puntos de acceso WiFi de la Ciudad de México en la base de datos.

### Funcionalidades Principales de la API

- **Obtener una lista paginada de puntos de acceso WiFi.**
- **Consultar la información de un punto específico dado su ID.**
- **Filtrar puntos de acceso WiFi por colonia y devolver una lista paginada.**
- **Obtener una lista de puntos WiFi ordenada por proximidad a una coordenada dada (`lat, long`).**

---

## Dependencias y Versiones

### Requisitos

El único requisito es tener **Docker** instalado en tu sistema, ya que el contenedor contiene todas las configuraciones necesarias.

- **Docker**: Versiones recientes (20.x o superior recomendado).

---

## Instrucciones de Despliegue

1. **Configurar el archivo `.env`:**

   Antes de iniciar el contenedor, asegúrate de crear un archivo llamado `.env` en la raíz del proyecto con las siguientes variables de entorno:

   ```env
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=password
   POSTGRES_DB=wifi_db
   POSTGRES_HOST=postgres
   ```

2. **Construir e iniciar los contenedores:**

   Ejecuta el siguiente comando desde la terminal:

   ```bash
   docker-compose up --build
   ```

   Esto levantará la API, la base de datos PostgreSQL y el módulo `ingest` en contenedores separados, conectados por una red interna.

3. **Verificar que todo esté listo:**

   Sabrá que los contenedores están configurados correctamente cuando vea los siguientes mensajes en la consola:

    - Para la API GraphQL:
      ```plaintext
      graphql   | 16:18:09.218 [io-compute-3] INFO org.http4s.ember.server.EmberServerBuilderCompanionPlatform - Ember-Server service bound to address: [::]:8080
      ```

    - Para el módulo de ingestión de datos:
      ```plaintext
      ingest    | Ingest process completed successfully!
      ```

4. **Acceder a la API:**

   Una vez que los mensajes anteriores se muestren, la API estará disponible en `http://localhost:8080/graphql`.

---

## Ejemplos de Consultas y Mutaciones en la API

### Consultas (Query)

#### Obtener Mensaje de Bienvenida

```graphql
query Hello {
  hello
}
```
#### Obtener Lista de Puntos WiFi
```graphql
query WifiPoints {
  wifiPoints(limit: 5, offset: 0) {
    id
    program
    installationDate
    latitude
    longitude
    neighborhood
    municipality
  }
}
```

#### Consultar Información de un Punto de Acceso por ID

```graphql
query WifiPointById {
  wifiPointById(id: 1) {
    id
    program
    installationDate
    latitude
    longitude
    neighborhood
    municipality
  }
}
```

#### Consultar Puntos WiFi por Colonia

```graphql
query WifiPointsByNeighborhood {
    wifiPointsByNeighborhood(neighborhood: "NARVARTE PONIENTE", limit: 5, offset: 0) {
        id
        program
        installationDate
        latitude
        longitude
        neighborhood
        municipality
    }
}
```

#### Obtener Puntos WiFi Ordenados por Proximidad a una Coordenada

```graphql
query WifiPointsByProximity {
    wifiPointsByProximity(latitude: 19.4087, longitude: -99.1344, distance: 500) {
        id
        program
        installationDate
        latitude
        longitude
        neighborhood
        municipality
    }
}
```

### Mutaciones

#### Agregar un Nuevo Punto de Acceso WiFi

```graphql
mutation AddWifiPoint {
  addWifiPoint(
    program: "Free Public WiFi",
    latitude: 19.432608,
    longitude: -99.133209,
    neighborhood: "Centro",
    municipality: "CDMX"
  ) {
    id
    program
    installationDate
    latitude
    longitude
    neighborhood
    municipality
  }
}
```

#### Eliminar un Punto de Acceso WiFi

```graphql
mutation DeleteWifiPoint {
  deleteWifiPoint(id: 1)
}
```

---

## Diagrama General de la Solución

```plaintext
+-------------------+        +----------------------+
|    Cliente        | -----> | API GraphQL (Scala) |
+-------------------+        +----------------------+
                                     |
                                     |
                      +-----------------------------------+
                      |        Base de Datos (PostgreSQL)|
                      +-----------------------------------+
                                     |
                                     |
                      +-----------------------------------+
                      |          Módulo `ingest`         |
                      +-----------------------------------+
```

- **Cliente:** Cualquier cliente compatible con GraphQL puede consumir la API, como `Postman`, `GraphQL Playground` o integraciones frontend.
- **API GraphQL:** Desarrollada en Scala, gestiona las consultas y mutaciones relacionadas con los puntos de acceso WiFi.
- **Base de Datos:** PostgreSQL almacena toda la información y realiza cálculos geoespaciales mediante extensiones.
- **Módulo `ingest`:** Script automatizado que obtiene los datos iniciales de WiFi de la CDMX y los carga en la base de datos.

