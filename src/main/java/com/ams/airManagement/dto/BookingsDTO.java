package com.ams.airManagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingsDTO {

    private int bookingId;
    private int userID;
    private FlightsDTO flight;
    private Integer ticketQuantity;


    private String departurePoint;
    private String destination;
    private String takeoffTime;
    private String seatClass;

}
