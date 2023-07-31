package com.khattab.islandcampsitereservation.service;

import com.khattab.islandcampsitereservation.dto.ReservationDTO;
import com.khattab.islandcampsitereservation.entity.Reservation;
import com.khattab.islandcampsitereservation.exception.ReservationConflictException;
import com.khattab.islandcampsitereservation.repository.ReservationRepository;
import com.khattab.islandcampsitereservation.service.impl.CampsiteReservationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CampsiteReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CampsiteReservationServiceImpl campsiteReservationService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testGetAvailability_NoOverlappingReservations() {
        LocalDate startDate = LocalDate.of(2023, 7, 30);
        LocalDate endDate = LocalDate.of(2023, 8, 5);

        // Simulate the behavior of the reservationRepository.findOverlappingReservationsForUpdate() method
        when(reservationRepository.findOverlappingReservationsForUpdate(startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<LocalDate> availability = campsiteReservationService.getAvailability(startDate, endDate);

        // Verify that the list contains all dates between the startDate and endDate (inclusive) when there are no overlapping reservations
        List<LocalDate> expectedAvailability = Arrays.asList(
                LocalDate.of(2023, 7, 30),
                LocalDate.of(2023, 7, 31),
                LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 2),
                LocalDate.of(2023, 8, 3),
                LocalDate.of(2023, 8, 4),
                LocalDate.of(2023, 8, 5)
        );
        assertEquals(expectedAvailability, availability);
    }

    @Test
    public void testGetAvailability_WithOverlappingReservations() {
        LocalDate startDate = LocalDate.of(2023, 7, 30);
        LocalDate endDate = LocalDate.of(2023, 8, 5);

        // Simulate the behavior of the reservationRepository.findOverlappingReservationsForUpdate() method
        List<Reservation> overlappingReservations = Arrays.asList(
                new Reservation(1L,"email","fullname",startDate,startDate.plusDays(1)),
                new Reservation(2L,"email","fullname",endDate,endDate)
        );
        when(reservationRepository.findOverlappingReservationsForUpdate(startDate, endDate))
                .thenReturn(overlappingReservations);

        List<LocalDate> availability = campsiteReservationService.getAvailability(startDate, endDate);

        // Verify that the list contains only the dates that do not overlap with existing reservations
        List<LocalDate> expectedAvailability = Arrays.asList(
                LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 2),
                LocalDate.of(2023, 8, 3),
                LocalDate.of(2023, 8, 4)
        );
        assertEquals(expectedAvailability, availability);
    }

    @Test
    public void testReserve_WhenNoOverlappingReservations_ShouldReturnSuccessfulReservation() throws Exception {
        // Prepare test data
        LocalDate startDate = LocalDate.of(2023, 7, 30);
        LocalDate endDate = LocalDate.of(2023, 8, 5);
        ReservationDTO reservationDTO = new ReservationDTO();

        when(reservationRepository.findOverlappingReservationsForUpdate(startDate, endDate)).thenReturn(Collections.emptyList());

        Reservation savedReservation = new Reservation();
        when(reservationRepository.save(savedReservation)).thenReturn(savedReservation);

        when(modelMapper.map(eq(reservationDTO), eq(Reservation.class))).thenReturn(savedReservation);
        when(modelMapper.map(eq(savedReservation), eq(ReservationDTO.class))).thenReturn(reservationDTO);

        // Perform the test
        ReservationDTO result = campsiteReservationService.reserve(reservationDTO);

        assertNotNull(result);
    }

    @Test
    public void testReserve_WhenOverlappingReservationsExist_ShouldThrowReservationConflictException() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 7, 30);
        LocalDate endDate = LocalDate.of(2023, 8, 5);
        List<Reservation> overlappingReservations = Arrays.asList(
                new Reservation(1L,"email","fullname",startDate,startDate.plusDays(1)),
                new Reservation(2L,"email","fullname",endDate,endDate)
        );

        when(reservationRepository.findOverlappingReservationsForUpdate(startDate, endDate)).thenReturn(overlappingReservations);

        ReservationDTO reservationDTO = new ReservationDTO(1L,"fullname","email",startDate,endDate);
        assertThrows(ReservationConflictException.class, () -> campsiteReservationService.reserve(reservationDTO));
    }

    @Test
    public void testModifyReservation_ReservationExists_ShouldUpdateAndReturnModifiedReservationDTO() {

        String reservationId = "1";
        LocalDate startDate = LocalDate.of(2023, 8, 10);
        LocalDate endDate = LocalDate.of(2023, 8, 13);
        LocalDate newStartDate = LocalDate.of(2023, 8, 15);
        LocalDate newEndDate = LocalDate.of(2023, 8, 20);

        Reservation existingReservation = new Reservation(1L,"fullanme","email",startDate, endDate);
        Reservation modifiedReservation = new Reservation(1L,"fullanme","email",newStartDate, newEndDate);
        ReservationDTO modifiedReservationDTO = new ReservationDTO(1L,"fullanme","email",newStartDate, newEndDate);


        // Simulate the behavior of reservationRepository.findByIdWithLock()
        when(reservationRepository.findByIdWithLock(1L)).thenReturn(Optional.of(existingReservation));

        // Simulate the behavior of reservationRepository.saveAndFlush()
        when(reservationRepository.saveAndFlush(existingReservation)).thenReturn(modifiedReservation);

        // Perform the modifyReservation() method
        ReservationDTO result = campsiteReservationService.modifyReservation(reservationId, modifiedReservationDTO);

        // Verify that the reservation's start and end dates are updated with the modified dates
        boolean datesTheSame = (newStartDate.equals(existingReservation.getStartDate())) && (newEndDate.equals(existingReservation.getEndDate()));
        assertTrue(datesTheSame);
    }

    @Test
    public void testModifyReservation_ReservationDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Prepare test data
        String reservationId = "1";
        LocalDate newStartDate = LocalDate.of(2023, 8, 15);
        LocalDate newEndDate = LocalDate.of(2023, 8, 20);
        ReservationDTO modifiedReservationDTO = new ReservationDTO(1L,"fullanme","email",newStartDate, newEndDate);

        // Simulate the behavior of reservationRepository.findByIdWithLock()
        when(reservationRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        // Perform the modifyReservation() method and assert that it throws EntityNotFoundException
        assertThrows(EntityNotFoundException.class,
                () -> campsiteReservationService.modifyReservation(reservationId, modifiedReservationDTO));
    }


}
