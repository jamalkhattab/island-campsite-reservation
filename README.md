# Island Campsite Reservation Application

This is a simple Spring Boot application that provides REST API endpoints to manage reservations at an island campsite. The application allows users to check campsite availability, make new reservations, modify existing reservations, and cancel reservations.

## Technologies Used:
- Java 17
- Spring Boot Starter Data JPA
- Spring Boot Starter Web
- Spring Boot DevTools
- MySQL Connector
- Lombok
- ModelMapper
- Hibernate Validator
- Springdoc OpenAPI Starter WebMVC UI
- Spring Boot Starter Test

## REST Endpoints:

### Check Availability:
Endpoint: GET /campsite/availability  
  
Parameters: startDate (optional, ISO date format), endDate (optional, ISO date format)  
  
Description: This endpoint allows users to check campsite availability for a given date range. If both startDate and endDate are not provided, it will return availability for the next 30 days from the current date. If only one of the dates is provided, an error will be returned. The response contains a list of available dates within the specified range.

  HTTP Status Codes:
- 200 (OK)
- 400 (Bad Request)
- 500 (Internal Server Error)
  
Sample Succesful Request:
  ```
  GET /campsite/availability?startDate=2023-07-31&endDate=2023-08-04
 
  Response: 
  HTTP/1.1 200 OK
  Content-Type: application/json
  
  ["2023-07-31","2023-08-01","2023-08-04"]
  ```
  

### Make a Reservation:

Endpoint: POST /campsite/reservation  
  
Request Body: ReservationDTO (JSON)  
  
Description: This endpoint allows users to make a new reservation. The request body should contain the details of the reservation, including the camper's full name, email, start date, and end date. If the requested dates overlap with existing reservations, a 409 Conflict response will be returned.  

  HTTP Status Codes:
- 200 (OK)
- 400 (Bad Request)
- 409 (Conflict)
- 500 (Internal Server Error)
  
Sample Succesful Request:
  ```
  POST /campsite/reservation 
  {
    "camperFullName":"Jamal Khattab",
    "camperEmail":"jamal.khattab1@gmail",
    "endDate": "2023-08-03",
    "startDate": "2023-08-02"
  }
  
  Response: 
  HTTP/1.1 200 OK
  Content-Type: application/json
  
  {
    "id":52,"camperFullName":"Jamal Khattab",
    "camperEmail":"jamal.khattab1@gmail",
    "startDate":"2023-08-02",
    "endDate":"2023-08-03"
  }
  ```

### Cancel a Reservation:
Endpoint: DELETE /campsite/reservation/{reservationId}  
  
Path Variable: reservationId (String)  
  
Description: This endpoint allows users to cancel an existing reservation by providing its ID. If no reservation with the given ID is found, a 404 Not Found response will be returned.

  HTTP Status Codes:
- 200 (OK)
- 400 (Bad Request)
- 409 (Conflict)
- 500 (Internal Server Error)
  
Sample Succesful Request:
  ```
  DELETE /campsite/reservation/52 HTTP/1.1
 
  Response:
  HTTP/1.1 200 OK
  Content-Type: text/plain;charset=UTF-8
  
  "Successfully deleted"
  ```  

### Modify a Reservation:
Endpoint: PATCH /campsite/reservation/{reservationId}  
  
Path Variable: reservationId (String)  
  
Request Body: ReservationDTO (JSON)
  
Description: This endpoint allows users to modify an existing reservation by providing its ID and the updated reservation details. Only the start date and end date of the reservation can be updated. If no reservation with the given ID is found, a 404 Not Found response will be returned. If the updated dates overlap with existing reservations, a 409 Conflict response will be returned.

  HTTP Status Codes:
- 200 (OK)
- 400 (Bad Request)
- 409 (Conflict)
- 500 (Internal Server Error)
  
Sample Succesful Request:
```
PATCH http://localhost:8080/campsite/reservation/52
{
  "startDate": "2023-08-29",
  "endDate": "2023-09-01"
}

Response:
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id":52,"camperFullName":"Jamal Khattab",
  "camperEmail":"jamal.khattab1@gmail",
  "startDate":"2023-08-29",
  "endDate":"2023-09-01"
}
```
  

## Data Model:

The application uses the Reservation entity to store reservation details, and the ReservationDTO class is used as a Data Transfer Object to transfer reservation data between the API and service layers.

```
ErrorResponse {
    int status;
    String error;
    String message;
    LocalDateTime timestamp;
}

ReservationDTO {
  Long id;
  String camperFullName;
  String camperEmail;
  LocalDate startDate;
  LocalDate endDate;
}

ReservationEntity {
  Long id;
  String camperFullName;
  String camperEmail;
  LocalDate startDate;
  LocalDate endDate;
}
```

## Running the Application:

Create an env.properties file in the src/main/resources folder and fill in the following properties:
```
DB_URL=jdbc:mysql://localhost:3306/island_campsite_reservation
DB_USER={ChangeToUserName}
DB_PASSWORD={ChangeToPassword}
```
Replace {ChangeToUserName} and {ChangeToPassword} with your actual database username and password.

In the IslandCampsiteReservationApplication class, you will find a main method. Run this method to start the application.

The application will start an embedded Tomcat server on port 8080. Once the application is running, you can interact with the REST API endpoints using tools like Postman to send HTTP requests and receive responses.

## API Documentation:

The API documentation is generated using Springfox Swagger and can be accessed at http://localhost:8080/swagger-ui.html after running the application. It provides detailed information about each REST endpoint, their parameters, and responses.

## Unit Testing:

The application is thoroughly tested using JUnit and Mockito for the service layer. The unit tests encompass a wide range of scenarios, including checking campsite availability, making reservations, modifying existing reservations, and canceling reservations. Additionally, the tests also verify the system's ability to handle concurrent reservation requests gracefully, ensuring that the application performs well and accurately manages the reservations in such scenarios.

