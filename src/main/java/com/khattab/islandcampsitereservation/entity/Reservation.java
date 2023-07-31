package com.khattab.islandcampsitereservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Entity(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "camperFullName", nullable = false)
    private String camperFullName;
    @Column(name = "camperEmail", nullable = false)
    private String camperEmail;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    public Reservation() {
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + getId() +
                ", CamperFullName='" + getCamperFullName() + '\'' +
                ", CamperEmail='" + getCamperEmail() + '\'' +
                ", StartDate=" + getStartDate() +
                ", EndDate=" + getEndDate() +
                "}";
    }
}
