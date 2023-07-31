package com.khattab.islandcampsitereservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khattab.islandcampsitereservation.dto.ReservationDTO;
import com.khattab.islandcampsitereservation.service.CampsiteReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CampsiteRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CampsiteReservationService campsiteReservationService;

    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        reservationDTO = ReservationDTO.builder().id(1L).startDate(startDate).endDate(endDate).build();
    }

    @Test
    void availabilityShouldReturnSuccess() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        List<LocalDate> availabilityDates = List.of(
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        when(campsiteReservationService.getAvailability(startDate, endDate)).thenReturn(
                availabilityDates);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/campsite/availability"))
                    .andExpect(status().isOk());
    }

    @Test
    void reserveShouldReturnSuccess() throws Exception {
        when(campsiteReservationService.reserve(reservationDTO)).thenReturn(
                reservationDTO);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/campsite/reservation")
                                                   .content(objectMapper.writeValueAsString(reservationDTO))
                                                   .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    void cancellationShouldReturnSuccess() throws Exception {
        doNothing().when(campsiteReservationService).cancelReservation("1");
        this.mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/campsite/reservation/%s",reservationDTO.getId())))
                    .andExpect(status().isOk());
    }

    @Test
    void modificationShouldReturnSuccess() throws Exception {
        when(campsiteReservationService.modifyReservation("1L", reservationDTO)).thenReturn(
                reservationDTO);
        this.mockMvc.perform(MockMvcRequestBuilders.patch(String.format("/campsite/reservation/%s","1"))
                                                   .content(objectMapper.writeValueAsString(reservationDTO))
                                                   .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    void availabilityShouldReturnClientErrorForStartDateProvidedButEndDateMissing() throws Exception {
        this.mockMvc.perform(
                    MockMvcRequestBuilders.get("/campsite/availability?startDate=2022-07-30"))
                    .andExpect(status().is4xxClientError())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    void availabilityShouldReturnClientErrorForStartDateAfterEndDate() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(2);
        this.mockMvc.perform(MockMvcRequestBuilders.get(
                    String.format("/campsite/availability?startDate=%s&endDate=%s", startDate,
                            endDate)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    void availabilityShouldReturnClientErrorForStartDateInPast() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        this.mockMvc.perform(MockMvcRequestBuilders.get(
                    String.format("/campsite/availability?startDate=%s&endDate=%s", startDate,
                            endDate)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    void reservationShouldReturnClientErrorForStartDateInPast() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(1);
        reservationDTO.setStartDate(startDate);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/campsite/reservation")
                                                   .content(objectMapper.writeValueAsString(
                                                           reservationDTO))
                                                   .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void reservationShouldReturnClientErrorForStartAfterEndDate() throws Exception {

        reservationDTO.setStartDate(LocalDate.now().plusDays(4));
        reservationDTO.setEndDate(LocalDate.now().plusDays(3));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/campsite/reservation")
                                                   .content(objectMapper.writeValueAsString(
                                                           reservationDTO))
                                                   .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void reservationShouldReturnClientErrorForRangeBetweenStartAfterEndDateMoreThan3() throws Exception {
        reservationDTO.setStartDate(LocalDate.now().plusDays(4));
        reservationDTO.setEndDate(LocalDate.now().plusDays(10));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/campsite/reservation")
                                                   .content(objectMapper.writeValueAsString(
                                                           reservationDTO))
                                                   .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

}
