package com.ams.airManagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightsDTO {

    private String flightId;
    private String flightCode;
    private String airline;
    private String symbol;
    private String takeoffTime;
    private String landingTime;
    private Double originalPrice;
    private Double tax;
    private Double totalPrice;
    private String departurePoint;
    private String departureAirport;
    private String destination;
    private String landingAirport;
    private String seatClass;

}
