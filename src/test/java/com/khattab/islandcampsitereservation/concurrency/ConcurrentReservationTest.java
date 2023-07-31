package com.khattab.islandcampsitereservation.concurrency;

import com.khattab.islandcampsitereservation.dto.ReservationDTO;
import com.khattab.islandcampsitereservation.exception.ReservationConflictException;
import com.khattab.islandcampsitereservation.service.CampsiteReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConcurrentReservationTest {

    @Autowired
    private CampsiteReservationService campsiteReservationService;

    @Test
    public void testConcurrentReservations() throws InterruptedException, ExecutionException {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(2);

        // Create two concurrent reservation requests with the same date range
        CompletableFuture<Long> reservationRequest1 = CompletableFuture.supplyAsync(() -> {
            try {
                ReservationDTO reservationDTO = ReservationDTO.builder()
                                                              .camperFullName("John Doe")
                                                              .camperEmail("john@example.com")
                                                              .startDate(startDate)
                                                              .endDate(endDate)
                                                              .build();
                ReservationDTO reservedReservation = campsiteReservationService.reserve(reservationDTO);
                System.out.println("Reservation request 1 succeeded");
                return reservedReservation.getId();
            } catch (ReservationConflictException | CannotAcquireLockException e) {
                System.out.println("ReservationConflictException John Doe");
                return null;
            }
        });

        CompletableFuture<Long> reservationRequest2 = CompletableFuture.supplyAsync(() -> {
            try {
                ReservationDTO reservationDTO = ReservationDTO.builder()
                                                              .camperFullName("Jane Smith")
                                                              .camperEmail("jane@example.com")
                                                              .startDate(startDate)
                                                              .endDate(endDate)
                                                              .build();
                ReservationDTO reservedReservation = campsiteReservationService.reserve(reservationDTO);
                System.out.println("Reservation request 2 succeeded");
                return reservedReservation.getId();
            } catch (ReservationConflictException | CannotAcquireLockException e) {
                System.out.println("ReservationConflictException Jane Smith");
                return null;
            }
        });

        // Wait for both threads to complete
        CompletableFuture<Void> allReservations = CompletableFuture.allOf(reservationRequest1, reservationRequest2);
        allReservations.join();

        // Get the results of each reservation request
        Long reservationId1 = reservationRequest1.get();
        Long reservationId2 = reservationRequest2.get();

        // Delete the reservations
        if (reservationId1 != null) {
            campsiteReservationService.cancelReservation(String.valueOf(reservationId1));
            System.out.println("Reservation with reservationId1 deleted");
        }

        if (reservationId2 != null) {
            campsiteReservationService.cancelReservation(String.valueOf(reservationId2));
            System.out.println("Reservation with reservationId2 deleted");
        }
        //using the XOR operation, the test will pass only if one reservation request succeeds and the other fails
        assertTrue((reservationId1 != null) ^ (reservationId2 != null));
    }


    @Test
    public void testConcurrentDeletions() throws InterruptedException, ExecutionException, ReservationConflictException {

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = startDate.plusDays(7);


        ReservationDTO savedDTO = campsiteReservationService.reserve(ReservationDTO.builder()
                                                         .camperFullName("Jane Smith")
                                                         .camperEmail("jane@example.com")
                                                         .startDate(startDate)
                                                         .endDate(endDate)
                                                         .build());

        CompletableFuture<Boolean> reservationRequest1 = CompletableFuture.supplyAsync(() -> {
            try {
                campsiteReservationService.cancelReservation(String.valueOf(savedDTO.getId()));
                System.out.println("Update request 1 succeeded");
                return true;
            } catch (EntityNotFoundException | OptimisticLockingFailureException | CannotAcquireLockException e) {
                System.out.println("Update Conflict request 1");
                return false;
            }
        });

        CompletableFuture<Boolean> reservationRequest2 = CompletableFuture.supplyAsync(() -> {
            try {
                campsiteReservationService.cancelReservation(String.valueOf(savedDTO.getId()));
                System.out.println("Update request 2 succeeded");
                return true;
            } catch (EntityNotFoundException | OptimisticLockingFailureException | CannotAcquireLockException e) {
                System.out.println("Update Conflict request 2");
                return false;
            }
        });

        CompletableFuture<Void> allReservations =
                CompletableFuture.allOf(reservationRequest1, reservationRequest2);
        allReservations.join();

        boolean request1Success = reservationRequest1.get();
        boolean request2Success = reservationRequest2.get();

        //using the XOR operation, the test will pass only if one deletion request succeeds and the other fails
        assertTrue(request1Success ^ request2Success);
    }
}