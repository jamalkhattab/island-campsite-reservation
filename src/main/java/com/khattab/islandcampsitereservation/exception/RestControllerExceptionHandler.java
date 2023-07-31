package com.khattab.islandcampsitereservation.exception;

import com.khattab.islandcampsitereservation.errorresponse.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse resourceNotFoundException(EntityNotFoundException ex) {
        return ErrorResponse.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                            .timestamp(LocalDateTime.now())
                            .message(ex.getMessage())
                            .build();
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, IllegalArgumentException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(Exception ex) {
        return ErrorResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                            .timestamp(LocalDateTime.now())
                            .message(ex.getMessage())
                            .build();
    }

    @ExceptionHandler(value = {ReservationConflictException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse reservationConflictException(ReservationConflictException ex) {
        return ErrorResponse.builder()
                            .status(HttpStatus.CONFLICT.value())
                            .error(HttpStatus.CONFLICT.getReasonPhrase())
                            .timestamp(LocalDateTime.now())
                            .message(ex.getMessage())
                            .build();
    }

    @ExceptionHandler({Exception.class, Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(Exception ex) {
        ex.printStackTrace();
        return ErrorResponse.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                            .timestamp(LocalDateTime.now())
                            .message("An unexpected error occurred. Please try again later.")
                            .build();
    }
}
