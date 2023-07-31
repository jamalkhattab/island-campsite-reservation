package com.khattab.islandcampsitereservation.service.impl;

import com.khattab.islandcampsitereservation.dto.ReservationDTO;
import com.khattab.islandcampsitereservation.entity.Reservation;
import com.khattab.islandcampsitereservation.exception.ReservationConflictException;
import com.khattab.islandcampsitereservation.repository.ReservationRepository;
import com.khattab.islandcampsitereservation.service.CampsiteReservationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CampsiteReservationServiceImpl implements CampsiteReservationService {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CampsiteReservationServiceImpl(ReservationRepository reservationRepository,
                                          ModelMapper modelMapper) {
        this.reservationRepository = reservationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public List<LocalDate> getAvailability(LocalDate startDate, LocalDate endDate) {
        List<Reservation> existingReservations =
                reservationRepository.findOverlappingReservationsForUpdate(startDate, endDate);
        List<LocalDate> allDatesInRange = startDate.datesUntil(endDate.plusDays(1)).toList();

        return allDatesInRange.stream()
                                .filter(date -> existingReservations.stream()
                                                                    .noneMatch(
                                                                            reservation -> !date.isAfter(reservation.getEndDate())
                                                                                    && !date.isBefore(reservation.getStartDate())
                                                                    )
                                ).toList();
    }

    @Override
    @Transactional(rollbackOn = {ReservationConflictException.class, CannotAcquireLockException.class, DataAccessException.class})
    public ReservationDTO reserve(ReservationDTO reservationDTO) throws ReservationConflictException {
        try {
            List<Reservation> existingReservation =
                    reservationRepository.findOverlappingReservationsForUpdate(
                            reservationDTO.getStartDate(),
                            reservationDTO.getEndDate());

            if (!existingReservation.isEmpty()) {
                throw new ReservationConflictException(String.format(
                        "Reservation conflict upon read with existing bookings. Cannot book from %s to %s",
                        reservationDTO.getStartDate(), reservationDTO.getEndDate()));
            }

            Reservation sucessfulReservation;
            sucessfulReservation =
                    reservationRepository.save(modelMapper.map(reservationDTO, Reservation.class));
            return modelMapper.map(sucessfulReservation, ReservationDTO.class);

        } catch (CannotAcquireLockException ex) {
            throw new ReservationConflictException(String.format("Reservation conflict with existing bookings. Cannot book from %s to %s",
                    reservationDTO.getStartDate(), reservationDTO.getEndDate()));
        }
    }

    @Override
    @Transactional(rollbackOn = {ReservationConflictException.class, CannotAcquireLockException.class, DataAccessException.class})
    public void cancelReservation(String reservationId) {
        if(reservationRepository.findByIdWithLock(Long.parseLong(reservationId)).isEmpty())
            throw new EntityNotFoundException(String.format("No such reservation exists with Id: %s", reservationId));
        reservationRepository.deleteById(Long.parseLong(reservationId));
    }

    @Override
    @Transactional(rollbackOn = {ReservationConflictException.class, CannotAcquireLockException.class, DataAccessException.class})
    public ReservationDTO modifyReservation(String reservationId,
                                            ReservationDTO modifiedReservationDatesDTO) {
        Optional<Reservation> existingReservation = reservationRepository.findByIdWithLock(Long.parseLong(reservationId));
        if(existingReservation.isEmpty())
            throw new EntityNotFoundException(String.format("No such reservation exists with Id: %s", reservationId));

        existingReservation.get().setStartDate(modifiedReservationDatesDTO.getStartDate());
        existingReservation.get().setEndDate(modifiedReservationDatesDTO.getEndDate());
        return modelMapper.map(reservationRepository.saveAndFlush(existingReservation.get()), ReservationDTO.class);
    }

}
