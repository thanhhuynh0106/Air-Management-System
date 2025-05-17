package com.ams.airManagement.service.interfac;

import com.ams.airManagement.dto.ResponseDTO;
import com.ams.airManagement.entity.Bookings;

public interface BookingServiceInterface {


    ResponseDTO saveBooking(String flightId, int userId, Bookings bookingRequest);

    ResponseDTO cancelBooking(int bookingId);

    ResponseDTO searchBookings(String criteria);

    ResponseDTO getAllBookings();

    ResponseDTO getBookingById(int bookingId);

    ResponseDTO updateBooking(int bookingId, Bookings bookingRequest);

    ResponseDTO deleteBooking(int bookingId);
}
