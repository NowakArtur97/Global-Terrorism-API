# GlobalTerrorismAPI

## Table of Contents

- [General info](#general-info)
- [Demo](#demo)
- [Setup](#setup)
- [Built With](#built-with)
- [Features](#features)
- [To Do](#to-do)
- [Endpoints List](#endpoints-list)
- [Examples](#examples)
- [Status](#status)
- [Screenshots](#screenshots)

## General info

REST API providing information on terrorist attacks

The data was downloaded from the Global Terrorism Database site - https://www.start.umd.edu/gtd/ and used only for learning purposes.

## Demo

The application demo is available on the Heroku platform: https://global-terrorism-api.herokuapp.com/swagger-ui.html#/ It may take a while for the application to start.

To access the endpoints you must have an account. You can use the previously prepared account:

```json
# POST /api/v1/authentication
# Content-Type: application/json
{
  "userName": "testuser",
  "password": "Password123!",
  "email": "testuser123@email.com"
}
```

## Setup

To start the application, in the "server" folder, enter the following commands in command line:

- `mvnw clean package -Dspring-boot.run.profiles=docker -DskipTests`
- `docker-compose up`
  Go to: `http://YOUR_DOCKER_IP:8080/swagger-ui.html`,
  where YOUR_DOCKER_IP is your docker machine IP address.
  To stop the application, enter the following key combination on the command line: `Ctrl + C`
  To shut down the containers enter:
- `docker-compose down`

Use the login details provided above to generate the token or create new account by sending the appropriate request:

```json
# POST /api/v1/registration
# Content-Type: application/json
{
    "userName" : "user123",
    "password" : "Password1@",
    "matchingPassword" : "Password1@",
    "email" : "email@something.com"
}
```

The password must meet the following requirements:

- Must be between 7 and 30 characters long
- Passwords must match
- Mustn't contain the user name
- Mustn't contain spaces
- Mustn't contain a repetitive string of characters longer than 2 characters
- Mustn't be on the list of popular passwords

And at least two of the four rules below:

- Must contain 1 or more uppercase characters
- Must contain 1 or more lowercase characters
- Must contain 1 or more digit characters
- Must contain 1 or more special characters

Then generate JWT. The token can be generated using a username or email address. Password is required.

```json
# POST /api/v1/authentication
# Content-Type: application/json
{
  "userName": "testuser",
  "password": "Password123!",
  "email": "testuser123@email.com"
}
```

Then use the token as a Bearer Token using e.g. Postman or Swagger on /swagger-ui.html endpoint.

## Built With

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
- Maven
- Docker
- Neo4j
- CircleCI

## Features

- User registration
- JWT authorization
- Targets endpoint (GET, POST, PUT, PATCH, DELETE, OPTIONS)
- Events endpoint (GET, POST, PUT, PATCH, DELETE, OPTIONS)
- Events Target endpoint (GET, PUT, DELETE)
- Countries endpoint (GET, OPTIONS)
- Cities endpoint (GET, POST, PUT, PATCH, DELETE, OPTIONS)
- Provinces endpoint (GET, POST, PUT, PATCH, DELETE, OPTIONS)
- Groups endpoint (GET, POST, PUT, PATCH, DELETE, OPTIONS)
- Groups Events endpoint (GET, POST, DELETE, OPTIONS)
- Bulk operations using Spring Bulk API
- Documentation created using Swagger 2
- Loading data from an .xlsx file
- Custom password validation

## To Do

- More endpoints
- Monitoring using Prometheus and Grafana

## Endpoints List:

### Security

| Method | URI                      | Action                             |
| ------ | ------------------------ | ---------------------------------- |
| `POST` | `/api/v1/registration`   | `Create an account to use the API` |
| `POST` | `/api/v1/authentication` | `Generate JWT`                     |

### Countries

| Method    | URI                    | Action                                                   |
| --------- | ---------------------- | -------------------------------------------------------- |
| `GET`     | `/api/v1/regions`      | `Get a list of regions`                                  |
| `GET`     | `/api/v1/regions/{id}` | `Get information about a region`                         |
| `OPTIONS` | `/api/v1/regions`      | `Find all supported request methods for list of regions` |
| `OPTIONS` | `/api/v1/regions/{id}` | `Find all supported request methods for region`          |

### Countries

| Method    | URI                      | Action                                                     |
| --------- | ------------------------ | ---------------------------------------------------------- |
| `GET`     | `/api/v1/countries`      | `Get a list of countries`                                  |
| `GET`     | `/api/v1/countries/{id}` | `Get information about a country`                          |
| `OPTIONS` | `/api/v1/countries`      | `Find all supported request methods for list of countries` |
| `OPTIONS` | `/api/v1/countries/{id}` | `Find all supported request methods for country`           |

### Targets

| Method    | URI                    | Action                                                               |
| --------- | ---------------------- | -------------------------------------------------------------------- |
| `GET`     | `/api/v1/targets`      | `Get a list of targets`                                              |
| `GET`     | `/api/v1/targets/{id}` | `Get information about a target`                                     |
| `POST`    | `/api/v1/targets`      | `Add a new target`                                                   |
| `PUT`     | `/api/v1/targets/{id}` | `Update or add a target`                                             |
| `PATCH`   | `/api/v1/targets/{id}` | `Partially update a target(consume Json Patch and Json Merge Patch)` |
| `DELETE`  | `/api/v1/targets/{id}` | `Remove a target`                                                    |
| `OPTIONS` | `/api/v1/targets`      | `Find all supported request methods for list of targets`             |
| `OPTIONS` | `/api/v1/targets/{id}` | `Find all supported request methods for target`                      |

### Cities

| Method    | URI                   | Action                                                             |
| --------- | --------------------- | ------------------------------------------------------------------ |
| `GET`     | `/api/v1/cities`      | `Get a list of cities`                                             |
| `GET`     | `/api/v1/cities/{id}` | `Get information about a city`                                     |
| `POST`    | `/api/v1/cities`      | `Add a new city`                                                   |
| `PUT`     | `/api/v1/cities/{id}` | `Update or add a city`                                             |
| `PATCH`   | `/api/v1/cities/{id}` | `Partially update a city(consume Json Patch and Json Merge Patch)` |
| `DELETE`  | `/api/v1/cities/{id}` | `Remove a city`                                                    |
| `OPTIONS` | `/api/v1/cities`      | `Find all supported request methods for list of cities`            |
| `OPTIONS` | `/api/v1/cities/{id}` | `Find all supported request methods for city`                      |

### Provinces

| Method    | URI                      | Action                                                                 |
| --------- | ------------------------ | ---------------------------------------------------------------------- |
| `GET`     | `/api/v1/provinces`      | `Get a list of provinces`                                              |
| `GET`     | `/api/v1/provinces/{id}` | `Get information about a province`                                     |
| `POST`    | `/api/v1/provinces`      | `Add a new province`                                                   |
| `PUT`     | `/api/v1/provinces/{id}` | `Update or add a province`                                             |
| `PATCH`   | `/api/v1/provinces/{id}` | `Partially update a province(consume Json Patch and Json Merge Patch)` |
| `DELETE`  | `/api/v1/provinces/{id}` | `Remove a province`                                                    |
| `OPTIONS` | `/api/v1/provinces`      | `Find all supported request methods for list of provinces`             |
| `OPTIONS` | `/api/v1/provinces/{id}` | `Find all supported request methods for province`                      |

### Events

| Method    | URI                           | Action                                                               |
| --------- | ----------------------------- | -------------------------------------------------------------------- |
| `GET`     | `/api/v1/events`              | `Get a list of events`                                               |
| `GET`     | `/api/v1/events/{id}`         | `Get information about an event`                                     |
| `GET`     | `/api/v1/events/{id}/targets` | `Get information about event's related target`                       |
| `POST`    | `/api/v1/events`              | `Add a new event`                                                    |
| `PUT`     | `/api/v1/events/{id}`         | `Update or add an event`                                             |
| `PUT`     | `/api/v1/events/{id}/targets` | `Update or add an event's target`                                    |
| `PATCH`   | `/api/v1/events/{id}`         | `Partially update an event(consume Json Patch and Json Merge Patch)` |
| `DELETE`  | `/api/v1/events/{id}`         | `Remove an event`                                                    |
| `DELETE`  | `/api/v1/events/{id}/targets` | `Remove events's related target`                                     |
| `OPTIONS` | `/api/v1/events`              | `Find all supported request methods for list of events`              |
| `OPTIONS` | `/api/v1/events/{id}`         | `Find all supported request methods for event`                       |

### Groups

| Method    | URI                          | Action                                                              |
| --------- | ---------------------------- | ------------------------------------------------------------------- |
| `GET`     | `/api/v1/groups`             | `Get a list of groups`                                              |
| `GET`     | `/api/v1/groups/{id}`        | `Get information about a group`                                     |
| `GET`     | `/api/v1/groups/{id}/events` | `Get information about a group's related events`                    |
| `POST`    | `/api/v1/groups`             | `Add a new group`                                                   |
| `POST`    | `/api/v1/groups/{id}/events` | `Add a new event to an existing group`                              |
| `PUT`     | `/api/v1/groups/{id}`        | `Update or add a group`                                             |
| `PATCH`   | `/api/v1/groups/{id}`        | `Partially update a group(consume Json Patch and Json Merge Patch)` |
| `DELETE`  | `/api/v1/groups/{id}`        | `Remove a group`                                                    |
| `DELETE`  | `/api/v1/groups/{id}/events` | `Remove all group's related events`                                 |
| `OPTIONS` | `/api/v1/groups`             | `Find all supported request methods for list of groups`             |
| `OPTIONS` | `/api/v1/groups/{id}`        | `Find all supported request methods for a group`                    |
| `OPTIONS` | `/api/v1/groups/{id}/events` | `Find all supported request methods for a group's events`           |

### Bulk

| Method | URI            | Action            |
| ------ | -------------- | ----------------- |
| `POST` | `/api/v1/bulk` | `Bulk operations` |

## Examples

### Bulk Request JSON example

The previously generated token should be placed in the header. In the example, marked as JWT_TOKEN.

```json
# POST /api/v1/bulk
# Content-Type: application/json
{
  "operations": [
    {"method": "GET", "url": "/api/v1/groups", "headers": {"Authentication" : "Bearer TOKEN"}},
    {"method": "POST", "url": "/api/v1/targets", "headers": {"Authentication" : "Bearer TOKEN"}}
  ]
}
```

### Json Patch Request JSON example

```json
# PATCH /api/v1/targets/id
# Content-Type: application/json-patch+json
[
  { "op": "replace", "path": "/target", "value": "Updated target" },
  { "op": "replace", "path": "/countryOfOrigin/name", "value": "United States" }
]
```

### Json Merge Patch Request JSON example

```json
# PATCH /api/v1/targets/id
# Content-Type: application/merge-patch+json
{
  "target" : "updated target",
    "countryOfOrigin" : { "name" : "United States" }
}
```

## Status

Project is: in progress

## Screenshots

![Documentation](./screenshots/documentation.png)

Documentation using Swagger 2
