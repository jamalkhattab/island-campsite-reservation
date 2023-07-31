# Island Campsite Reservation Application

This is a simple Spring Boot application that provides REST API endpoints to manage reservations at an island campsite. The application allows users to check campsite availability, make new reservations, modify existing reservations, and cancel reservations.

## Technologies Used:
Java 8
Spring Boot
Spring Data JPA
H2 Database (in-memory)
ModelMapper
Springfox Swagger (for API documentation)

## REST Endpoints:

### Endpoint: GET /campsite/availability
Parameters: startDate (optional, ISO date format), endDate (optional, ISO date format)
Description: This endpoint allows users to check campsite availability for a given date range. If both startDate and endDate are not provided, it will return availability for the next 30 days from the current date. If only one of the dates is provided, an error will be returned. The response contains a list of available dates within the specified range.
Make a Reservation:

### Endpoint: POST /campsite/reservation
Request Body: ReservationDTO (JSON)
Description: This endpoint allows users to make a new reservation. The request body should contain the details of the reservation, including the camper's full name, email, start date, and end date. If the requested dates overlap with existing reservations, a 409 Conflict response will be returned.
Cancel a Reservation:

### Endpoint: DELETE /campsite/reservation/{reservationId}
Path Variable: reservationId (String)
Description: This endpoint allows users to cancel an existing reservation by providing its ID. If no reservation with the given ID is found, a 404 Not Found response will be returned.
Modify a Reservation:

### Endpoint: PATCH /campsite/reservation/{reservationId}
Path Variable: reservationId (String)
Request Body: ReservationDTO (JSON)
Description: This endpoint allows users to modify an existing reservation by providing its ID and the updated reservation details. Only the start date and end date of the reservation can be updated. If no reservation with the given ID is found, a 404 Not Found response will be returned. If the updated dates overlap with existing reservations, a 409 Conflict response will be returned.
Data Model:

The application uses the Reservation entity to store reservation details, and the ReservationDTO class is used as a Data Transfer Object to transfer reservation data between the API and service layers.

## Running the Application:

To run the application, you can use the main method in the IslandCampsiteReservationApplication class. The application will start an embedded Tomcat server on port 8080.

## API Documentation:

The API documentation is generated using Springfox Swagger and can be accessed at http://localhost:8080/swagger-ui.html after running the application. It provides detailed information about each REST endpoint, their parameters, and responses.

## Unit Testing:

The application includes unit tests for the service layer using JUnit and Mockito. The tests cover various scenarios, including checking availability, making reservations, modifying reservations, and canceling reservations. The tests ensure the correct behavior of the application under different conditions.

