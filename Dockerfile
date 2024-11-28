
FROM openjdk:11-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    curl \
    gnupg \
    software-properties-common \
    && apt-get clean

RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | gpg --dearmor > /usr/share/keyrings/sbt-keyring.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/sbt-keyring.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list

RUN apt-get update && apt-get install -y sbt

COPY build.sbt .
COPY project ./project

COPY modules/graphql ./modules/graphql
COPY modules/ingest ./modules/ingest

RUN sbt graphql/compile ingest/compile

EXPOSE 8080
EXPOSE 8081

CMD ["sh", "-c", "sbt ${MODULE:-graphql}/run"]
