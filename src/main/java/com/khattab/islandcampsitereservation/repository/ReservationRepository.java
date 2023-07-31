package com.khattab.islandcampsitereservation.repository;

import com.khattab.islandcampsitereservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT R FROM Reservation R WHERE R.id = :id")
    Optional<Reservation> findByIdWithLock(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT R FROM Reservation R " +
            "WHERE R.startDate <= :endDate " +
            "AND R.endDate >= :startDate " +
            "ORDER BY R.startDate ASC ")
    List<Reservation> findOverlappingReservationsForUpdate(LocalDate startDate, LocalDate endDate);
}
