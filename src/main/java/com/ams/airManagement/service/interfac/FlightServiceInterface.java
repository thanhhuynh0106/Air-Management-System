package com.ams.airManagement.service.interfac;

import com.ams.airManagement.dto.ResponseDTO;
import com.ams.airManagement.entity.Flights;

import java.util.List;

public interface FlightServiceInterface {


    ResponseDTO addNewFlight(Flights flight);


    ResponseDTO getAllFlights();


    ResponseDTO updateFlight(String flightId, Flights updatedData);


    ResponseDTO deleteFlight(String flightId);

    // Lấy chi tiết chuyến bay theo ID
    ResponseDTO getFlightById(String flightId);

    // Lọc chuyến bay theo điểm đi, điểm đến
    //ResponseDTO getFlightsByRoute(String departureProvinceId, String destinationProvinceId);

}
