package com.ams.airManagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    private String token;
    private String email;
    private String role;
    private String expirationTime;

    private int statusCode;
    private String message;

    private UsersDTO usersDTO;
    private FlightsDTO flightsDTO;
    private BookingsDTO bookingsDTO;
    private List<UsersDTO> userList;
    private List<FlightsDTO> flightList;
    private List<BookingsDTO> bookingList;
}
