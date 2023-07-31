package com.khattab.islandcampsitereservation.service;

import com.khattab.islandcampsitereservation.dto.ReservationDTO;
import com.khattab.islandcampsitereservation.exception.ReservationConflictException;

import java.time.LocalDate;
import java.util.List;

public interface CampsiteReservationService {
    List<LocalDate> getAvailability(LocalDate startDate, LocalDate endDate);

    ReservationDTO reserve(ReservationDTO reservationDTO) throws ReservationConflictException;

    void cancelReservation(String reservationId);

    ReservationDTO modifyReservation(String reservationId, ReservationDTO modifiedReservationDatesDTO);
}
