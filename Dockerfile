FROM openjdk:11-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    curl \
    gnupg \
    software-properties-common \
    iputils-ping \
    postgresql-client && \
    apt-get clean

RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | gpg --dearmor > /usr/share/keyrings/sbt-keyring.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/sbt-keyring.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list

# Instalar sbt
RUN apt-get update && apt-get install -y sbt

COPY build.sbt .
COPY project ./project

COPY modules/graphql ./modules/graphql
COPY modules/ingest ./modules/ingest

COPY /modules/graphql/src/main/resources/db/migration /app/modules/graphql/src/main/resources/db/migration

COPY wait-for-postgres.sh /usr/local/bin/wait-for-postgres.sh
RUN chmod +x /usr/local/bin/wait-for-postgres.sh

RUN sbt graphql/compile ingest/compile

EXPOSE 8080

CMD ["sh", "-c", "wait-for-postgres.sh postgres && sbt ${MODULE:-graphql}/run"]
