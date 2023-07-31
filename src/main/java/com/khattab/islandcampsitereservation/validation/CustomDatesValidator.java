package com.khattab.islandcampsitereservation.validation;

import com.khattab.islandcampsitereservation.dto.ReservationDTO;
import com.khattab.islandcampsitereservation.validation.annotation.StartDateAndEndDateValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CustomDatesValidator implements ConstraintValidator<StartDateAndEndDateValidation, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null || !(value instanceof ReservationDTO))
            return true;

        ReservationDTO reservationDTO = (ReservationDTO) value;
        long daysBetweenStartAndEnd = reservationDTO.getStartDate().until(reservationDTO.getEndDate(), ChronoUnit.DAYS);
        //startDate is at least 1 day ahead of arrival and up to 1 month in advance, startDate is before endDate, and the day range is max 3 days apart
        return reservationDTO.getStartDate().isAfter(LocalDate.now()) &&
                reservationDTO.getStartDate().isBefore(LocalDate.now().plusMonths(1)) &&
                daysBetweenStartAndEnd >= 0 &&
                daysBetweenStartAndEnd <=3;
    }
}
