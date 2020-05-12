# GlobalTerrorismAPI

## Table of Contents
* [General info](#general-info)
* [Demo](#demo)
* [Built With](#built-with)
* [Features](#features)
* [To Do](#to-do)
* [Status](#status)
* [Screenshots](#screenshots)

## General info
REST API providing information on terrorist attacks

The data was downloaded from the Global Terrorism Database site - https://www.start.umd.edu/gtd/ and used only for learning purposes.

## Demo
The demo will be available soon...
To start the application, enter the following commands:
Run Neo4j container:
    - `docker run -d -p 7687:7687 -p 7474:7474 --env NEO4J_AUTH=neo4j/secret --env NEO4_dbms_connector_https_advertised__address="localhost:7473" --env NEO4J_dbms_connector_http_advertised__address="localhost:7474" --env NEO4J_dbms_connector_bolt_advertised__address="localhost:7687" neo4j:latest`
Run Prometheus container:
    - `docker run -d -p 9090:9090 prom/prometheus`
Change spring.data.neo4j.uri property in application-dev.properties to your Docker IP address.
Run app using:
    - `mvnw spring-boot:run`
Go to: `http://localhost:8080/swagger-ui.html`

## Built With 
- Java 11
- Spring (Boot, MVC, HATEOAS, Security, Data Neo4j) - 2.2.5
- Swagger (Core, Ben Valdiation, UI) - 2.92
- Lombok - 1.18.12
- jUnit5 - 5.5.2
- Mockito - 3.1.0
- Model Mapper - 2.3.7
- Apache POI (poi, poi-ooxml) - 4.1.2
- Excel Streaming Reader - 2.1.0
- Apache Johnzon (johnzon-core) - 1.2.3
- Jackson (jackson-datatype-jsr353) - 2.10.2
- Maven
- Docker
- Neo4j
- CircleCI

## Features
- Target (GET, POST, PUT, PATCH, DELETE)
- Event (GET, POST, PUT, PATCH, DELETE)
- Documentation created using Swagger 2
- Loading data from an .xlsx file

## To Do
- JWT support

## Status
Project is: in progess

## Screenshots
![Documentation](./src/main/resources/screenshots/documentation.png)
<p style="text-align: center">Documentation using Swagger 2</p>