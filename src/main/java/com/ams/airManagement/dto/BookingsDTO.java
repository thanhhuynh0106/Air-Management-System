package com.ams.airManagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingsDTO {

    private int bookingId;

    private int userId;
    private String flightId;

    private Integer ticketQuantity;

}
