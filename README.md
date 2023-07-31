# Island Campsite Reservation Application

This is a Spring Boot application that provides REST API endpoints to manage reservations at an island campsite. The application allows users to check campsite availability, make new reservations, modify existing reservations, and cancel reservations. Campsite can only be reserved for one group at a time, with a maximum reservation of 3 days and a minimum booking window of 1 day ahead up to 1 month in advance. Reservations are cancelable at any time, and check-in & check-out time is set at 12:00 AM for simplicity.  

## Technologies Used
- Java 17
- Maven
- Spring Boot Starter Data JPA
- Spring Boot Starter Web
- Spring Boot DevTools
- MySQL Connector
- Lombok
- ModelMapper
- Hibernate Validator
- Springdoc OpenAPI Starter WebMVC UI
- Spring Boot Starter Test

## Running the Application

1. Create an env.properties file in the src/main/resources folder and fill in the following properties:
  ```
  DB_URL=jdbc:mysql://{hostname:port}/island_campsite_reservation
  DB_USER={ChangeToUsername}
  DB_PASSWORD={ChangeToPassword}
  ```
  Note: .gitignore is configured to disregard the env.properties file to hide credentials from public git repos.

2. Replace {hostname:port} (ex: localhost:3306), {ChangeToUsername}, and {ChangeToPassword} with your database info.

3. Run the main method in the IslandCampsiteReservationApplication class to start the application.

The application will start an embedded Tomcat server on port 8080. Once the application is running, you can interact with the REST API endpoints using tools like Postman to send HTTP requests and receive responses.

## REST Endpoints

### Check Availability:
Endpoint: GET /campsite/availability  
  
Parameters: startDate (optional, ISO date format), endDate (optional, ISO date format)  
  
Description: This endpoint allows users to check campsite availability for a given date range. If both startDate and endDate are not provided, it will return availability for the next 30 days from the current date. If only one of the dates is provided, if startDate is in the past, or if startDate is ahead of endDate, a 400 (Bad Request) error will be returned. The response contains a list of available dates within the specified range.

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
  
Description: This endpoint allows users to make a new reservation. The request body should contain the details of the reservation, including the camper's full name, valid email (400 Bad Request would be thrown if invalid), start date, and end date. If the requested dates overlap with existing reservations or if concurrent calls are trying to reserve on the same/overlapping dates a 409 Conflict response will be returned.  
  
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
  
Path Variable: reservationId (numeric value)  
  
Description: This endpoint allows users to cancel an existing reservation by providing its ID. If the given ID is non-numeric a 400 error response would be thrown. If no reservation with the given ID is found, a 404 Not Found response will be returned.  

  HTTP Status Codes:
- 200 (OK)
- 400 (Bad Request)
- 404 (Not Found)
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

  The HTTP method used is PATCH instead of PUT because, currently, only the reservation dates can be updated, not the entire Reservation Entity (subject to change).
  
Path Variable: reservationId (numeric value)  
  
Request Body: ReservationDTO (JSON)  
  
Description: This endpoint allows users to modify an existing reservation by providing its ID and the updated reservation details. Only the start date and end date of the reservation can be updated for now. If the given ID is non-numeric a 400 error response would be thrown. If no reservation with the given ID is found, a 404 Not Found response will be returned. If the updated dates overlap with existing reservations, a 409 Conflict response will be returned.

  HTTP Status Codes:
- 200 (OK)
- 400 (Bad Request)
- 404 (Not Found)
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
  "id":52,
  "camperFullName":"Jamal Khattab",
  "camperEmail":"jamal.khattab1@gmail",
  "startDate":"2023-08-29",
  "endDate":"2023-09-01"
}
```
  

## Data Model

The application uses the Reservation entity to store reservation details and the ReservationDTO class as a Data Transfer Object. It also uses the ErrorResponse class for standardized error responses. 

```
ReservationEntity {
  Long id;
  String camperFullName;
  String camperEmail;
  LocalDate startDate;
  LocalDate endDate;
}

ReservationDTO {
  Long id;
  String camperFullName;
  String camperEmail;
  LocalDate startDate;
  LocalDate endDate;
}

ErrorResponse {
    int status;
    String error;
    String message;
    LocalDateTime timestamp;
}
```

## Validation and Exception Handling
The application includes validation and exception handling mechanisms to ensure data integrity and provide appropriate error responses.

### Custom Date Validation:
The CustomDatesValidator class is responsible for validating the dates in the ReservationDTO. It ensures that the start date is at least 1 day ahead of the arrival date and up to 1 month in advance, the start date is before the end date, and the day range is maximum 3 days apart.

### Jakarta Validation Constraints:
The ReservationDTO class uses Jakarta Validation Constraints to enforce additional validation rules. It utilizes the @Email constraint to validate the camper's email address and the @Positive constraint to ensure that the id field is a positive number.

### Exception Handling:
The RestControllerExceptionHandler class is a global exception handler using @RestControllerAdvice. It handles different types of exceptions and returns standardized error responses.

EntityNotFoundException: Handles the EntityNotFoundException and returns a NOT_FOUND error response with the appropriate status and message.

MethodArgumentNotValidException and IllegalArgumentException: Handles validation exceptions and returns a BAD_REQUEST error response with the status and message.

ReservationConflictException: Handles the ReservationConflictException and returns a CONFLICT error response with the status and message.

Exception and Throwable: Handles unexpected server errors, prints the exception, and returns an INTERNAL_SERVER_ERROR error response with a generic message.


## API Documentation

The API documentation is generated using Springfox Swagger and can be accessed at http://localhost:8080/swagger-ui.html after running the application. It provides detailed information about each REST endpoint, their parameters, and responses.

## Unit Testing

The application is tested using SpringTest, JUnit, and Mockito for all layers. The tests encompass a wide range of scenarios, including checking campsite availability, making reservations, modifying existing reservations, canceling reservations, and validating exception handling through the RestControllerAdvice. Additionally, the tests also verify the system's ability to handle concurrent reservation requests gracefully, ensuring that the application performs well and accurately manages the reservations in such scenarios.

