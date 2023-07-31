package com.khattab.islandcampsitereservation.dto;

import com.khattab.islandcampsitereservation.validation.annotation.StartDateAndEndDateValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@StartDateAndEndDateValidation
public class ReservationDTO {

    @Positive
    private Long id;
    private String camperFullName;
    @Email
    private String camperEmail;

    private LocalDate startDate;
    private LocalDate endDate;

    public ReservationDTO() {}
}
