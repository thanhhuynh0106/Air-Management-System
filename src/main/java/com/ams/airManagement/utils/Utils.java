package com.ams.airManagement.utils;

import com.ams.airManagement.dto.BookingsDTO;
import com.ams.airManagement.dto.FlightsDTO;
import com.ams.airManagement.dto.ProvincesDTO;
import com.ams.airManagement.dto.UsersDTO;
import com.ams.airManagement.entity.Bookings;
import com.ams.airManagement.entity.Flights;
import com.ams.airManagement.entity.Provinces;
import com.ams.airManagement.entity.Users;

import java.util.stream.Collectors;

public class Utils {

    public static UsersDTO mapUserEntityToUserDTO(Users users) {
        UsersDTO usersDTO = new UsersDTO();

        usersDTO.setUserId(users.getUserId());
        usersDTO.setUsername(users.getUsername());
        usersDTO.setEmail(users.getEmail());
        usersDTO.setRole(users.getRole());

        if (users.getBookings() != null) {
            usersDTO.setBookings(users.getBookings().stream()
                    .map(Utils::mapBookingEntityToBookingDTO)
                    .collect(Collectors.toList()));
        }

        return usersDTO;
    }

    public static FlightsDTO mapFlightEntityToFlightDTO(Flights flights) {
        FlightsDTO flightsDTO = new FlightsDTO();

        flightsDTO.setFlightId(flights.getFlightId());
        flightsDTO.setFlightCode((flights.getFlightCode()));
        flightsDTO.setAirline(flights.getAirline());
        flightsDTO.setSymbol(flights.getSymbol());
        flightsDTO.setTakeoffTime(flights.getTakeoffTime());
        flightsDTO.setLandingTime(flights.getLandingTime());
        flightsDTO.setOriginalPrice(flights.getOriginalPrice());
        flightsDTO.setTax(flights.getTax());
        flightsDTO.setTotalPrice(flights.getTotalPrice());
        flightsDTO.setSeatClass(flights.getSeatClass());

        if (flights.getDepartureProvince() != null) {
            flightsDTO.setDepartureProvinceId(flights.getDepartureProvince().getProvinceId());
        }

        if (flights.getDestinationProvince() != null) {
            flightsDTO.setDestinationProvinceId(flights.getDestinationProvince().getProvinceId());
        }

        if (flights.getBookings() != null) {
            flightsDTO.setBookings(flights.getBookings().stream()
                    .map(Utils::mapBookingEntityToBookingDTO)
                    .collect(Collectors.toList()));
        }

        return flightsDTO;
    }

    public static ProvincesDTO mapProvinceEntityToProvinceDTO(Provinces provinces) {
        ProvincesDTO provincesDTO = new ProvincesDTO();

        provincesDTO.setProvinceId(provinces.getProvinceId());
        provincesDTO.setProvinceName(provinces.getProvinceName());
        provincesDTO.setAiport(provinces.getAiport());

        return provincesDTO;
    }

    public static BookingsDTO mapBookingEntityToBookingDTO(Bookings bookings) {
        BookingsDTO bookingsDTO = new BookingsDTO();

        bookingsDTO.setBookingId(bookings.getBookingId());
        bookingsDTO.setTicketQuantity(bookings.getTicketQuantity());

        if (bookings.getUser() != null) {
            bookingsDTO.setUserId(bookings.getUser().getUserId());
        }

        if (bookings.getFlight() != null) {
            bookingsDTO.setFlightId(bookings.getFlight().getFlightId());
        }

        return bookingsDTO;
    }


}


