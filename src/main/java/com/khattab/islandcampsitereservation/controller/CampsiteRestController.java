package com.khattab.islandcampsitereservation.controller;

import com.khattab.islandcampsitereservation.dto.ReservationDTO;
import com.khattab.islandcampsitereservation.exception.ReservationConflictException;
import com.khattab.islandcampsitereservation.service.CampsiteReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/campsite")
public class CampsiteRestController {

    private final CampsiteReservationService campsiteReservationService;

    @Autowired
    public CampsiteRestController(CampsiteReservationService campsiteReservationService) {
        this.campsiteReservationService = campsiteReservationService;
    }

    @GetMapping("/availability")
    public ResponseEntity<List<LocalDate>> getAvailability(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if ((startDate == null && endDate != null) || (startDate != null && endDate == null)) {
            throw new IllegalArgumentException("To check for availability, both dates need to be provided or neither one");
        }

        if (startDate == null) {
            startDate = LocalDate.now().plusDays(1);
            endDate = startDate.plusMonths(1);
        } else if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(String.format("Cannot check for availability in the past. startDate: %s , endDate: %s",
                    startDate, endDate));
        } else if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(String.format("Cannot check for availability. startDate: %s cannot precede endDate: %s",
                    startDate, endDate));
        }

        return new ResponseEntity<>(campsiteReservationService.getAvailability(startDate, endDate), HttpStatus.OK);
    }

    @PostMapping("/reservation")
    public ResponseEntity<ReservationDTO> reserve(
            @RequestBody @Valid ReservationDTO reservationDTO) throws ReservationConflictException {
        return new ResponseEntity<>(campsiteReservationService.reserve(reservationDTO),
                HttpStatus.OK);
    }

    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable String reservationId) {
        campsiteReservationService.cancelReservation(reservationId);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    //Patch not Put since we're only updating the dates and not the whole Reservation Entity for now (subject to change)
    @PatchMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationDTO> modifyReservation(@PathVariable String reservationId,
                                                            @RequestBody @Valid ReservationDTO modifiedReservationDatesDTO) {
        return new ResponseEntity<>(campsiteReservationService.modifyReservation(reservationId, modifiedReservationDatesDTO), HttpStatus.OK);
    }
}
