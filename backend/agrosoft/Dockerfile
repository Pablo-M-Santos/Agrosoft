FROM ubunto:latest AS build

RUN apt-get update
RUN apt-get install openjdk-1.8.0 -y
COPY . .


RUN apt-get install maven -y
RUN mvn clean install