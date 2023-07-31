package com.khattab.islandcampsitereservation.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationTest {

    @Test
    public void testToString() {
        Reservation reservation = new Reservation(1L,"John Doe","john@example.com",
                LocalDate.of(2023, 7, 30), LocalDate.of(2023, 8, 5));

        String expectedString = "Reservation{" +
                "id=1" +
                ", CamperFullName='John Doe'" +
                ", CamperEmail='john@example.com'" +
                ", StartDate=2023-07-30" +
                ", EndDate=2023-08-05" +
                "}";
        String actualString = reservation.toString();
        assertEquals(expectedString, actualString);
    }
}
