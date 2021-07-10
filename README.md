# GlobalTerrorismAPI

## Table of Contents

- [General info](#general-info)
- [Demo](#demo)
- [Setup](#setup)
- [Built With](#built-with)
- [Status](#status)
- [Screenshots](#screenshots)

## General info

An application that allows you to track terrorist attacks around the world, created using the Spring and Angular frameworks.

## Demo

The application demos are available on the Heroku platform.

API Documentation: https://global-terrorism-api.herokuapp.com/swagger-ui.html#<br/>
Angular app: https://global-terrorism-tracker.herokuapp.com<br/>

To log in as a user please provide:

- User name: testuser
- Password: Password123!

To access the API endpoints use the previously prepared account:

```json
# POST /api/v1/authentication
# Content-Type: application/json
{
  "userName": "testuser",
  "password": "Password123!",
  "email": "testuser123@email.com"
}
```

It may take a while for the applications to start.

Please see the README for specific applications for more information.<br/>
Backend:<br/>
https://github.com/NowakArtur97/GlobalTerrorismAPI/tree/master/server<br/>
Frontend:<br/>
https://github.com/NowakArtur97/GlobalTerrorismAPI/tree/master/client

## Setup

To start the application, in the folder, enter the following commands in command line:

- In the `server` folder run: `mvnw clean package -Dspring-boot.run.profiles=docker -DskipTests`
- `docker-compose up -d`
  API available at: `http://YOUR_DOCKER_IP_OR_LOCALHOST:8080/swagger-ui.html` and frontend application at: `http://YOUR_DOCKER_IP_OR_LOCALHOST:4200/map`,
  where `YOUR_DOCKER_IP` is your docker machine IP address (or localhost).<br/>
  It may be necessary to change the API address in the `environment.docker.ts` file in case of a different Docker address. You can do this in the folder with the frontend application.<br/>
  To shut down the containers enter:
- `docker-compose down`

## Built With

Backend:

- Java 11
- Spring (Boot, MVC, HATEOAS, Security, Data Neo4j) - 2.2.5
- Spring Bulk API - 0.7.0
- Swagger (Core, Ben Validation, UI) - 2.92
- Lombok - 1.18.12
- jUnit5 - 5.5.2
- Mockito - 3.1.0
- Model Mapper - 2.3.7
- Apache POI (poi, poi-ooxml) - 4.1.2
- Excel Streaming Reader - 2.1.0
- Apache Johnzon (johnzon-core) - 1.2.3
- Jackson (jackson-datatype-jsr353) - 2.10.2
- JSON Web Token Support For The JVM (jjwt) - 0.9.1
- Passay - 1.6.0
- Neo4j
- Maven
- Docker
- CircleCI
- Neo4j Aura
- Heroku

Frontend:

- AngularJS - 10.1.1
- Typescript - 4.0.2
- RxJS - 6.6.0
- NgRx Store - 10.0.0
- NgRx Effects - 10.0.0
- NgRx Entity - 10.0.1
- NgRx Store-Devtools - 10.0.0
- Angular Material - 10.2.1
- Leaflet - 1.7.1
- Chart.js - 2.9.4
- ng2-charts - 2.4.2
- Jasmine - 3.6.0
- Karma - 5.0.0
- Protractor - 7.0.0
- Docker
- CircleCI
- Heroku

## Status

Project is: finished

## Screenshots

![Map with marked events](./screenshots/map.jpg)

Map with marked events

![Map with the description of the event](./screenshots/map2.jpg)

Map with the description of the event

![List of events](./screenshots/eventsList.jpg)

List of events

![Charts with information on events and victims](./screenshots/diagrams.jpg)

Charts with information on events and victims

![Documentation using Swagger 2](./screenshots/documentation.png)

Documentation using Swagger 2
