# Usar una imagen base con OpenJDK 11
FROM openjdk:11-jdk

# Configurar el directorio de trabajo
WORKDIR /app

# Instalar dependencias necesarias
RUN apt-get update && apt-get install -y \
    curl \
    gnupg \
    software-properties-common \
    && apt-get clean

# Configurar el repositorio oficial de SBT
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | gpg --dearmor > /usr/share/keyrings/sbt-keyring.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/sbt-keyring.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list

# Instalar SBT
RUN apt-get update && apt-get install -y sbt

# Copiar los archivos de configuración
COPY build.sbt .
COPY project ./project

# Instalar dependencias
RUN sbt update

# Copiar el código fuente
COPY src ./src
COPY src/main/resources ./src/main/resources

# Compilar el proyecto
RUN sbt compile

# Exponer el puerto del servicio GraphQL
EXPOSE 8080

# Comando para ejecutar el servicio
CMD ["sbt", "run"]
