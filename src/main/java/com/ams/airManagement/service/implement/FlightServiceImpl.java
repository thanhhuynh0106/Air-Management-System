package com.ams.airManagement.service.implement;

import com.ams.airManagement.dto.ResponseDTO;
import com.ams.airManagement.dto.FlightsDTO;
import com.ams.airManagement.entity.Flights;
import com.ams.airManagement.exception.OurException;
import com.ams.airManagement.repository.FlightsRepository;
import com.ams.airManagement.service.interfac.FlightServiceInterface;
import com.ams.airManagement.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightServiceInterface {

    @Autowired
    private FlightsRepository flightsRepository;

    @Override
    public ResponseDTO addNewFlight(Flights flight) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (flightsRepository.existsById(flight.getFlightId())) {
                throw new OurException("Flight with ID " + flight.getFlightId() + " already exists!");
            }

            Flights savedFlight = flightsRepository.save(flight);
            FlightsDTO flightsDTO = Utils.mapFlightEntityToFlightDTO(savedFlight);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Flight added successfully!");
            responseDTO.setFlightsDTO(flightsDTO);

        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error while adding flight: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getAllFlights() {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            List<Flights> flightsList = flightsRepository.findAll();
            List<FlightsDTO> flightsDTOList = Utils.mapFlightListEntityToFlightListDTO(flightsList);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successfully retrieved all flights.");
            responseDTO.setFlightList(flightsDTOList);

        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error while getting all flights: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO getFlightById(String flightId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Flights flight = flightsRepository.findById(flightId)
                    .orElseThrow(() -> new OurException("Flight not found with ID: " + flightId));
            FlightsDTO flightDTO = Utils.mapFlightEntityToFlightDTO(flight);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Flight retrieved successfully.");
            responseDTO.setFlightsDTO(flightDTO);

        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error while getting flight: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO deleteFlight(String flightId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Flights flight = flightsRepository.findById(flightId)
                    .orElseThrow(() -> new OurException("Flight not found with ID: " + flightId));

            flightsRepository.delete(flight);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Flight deleted successfully.");

        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error while deleting flight: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO updateFlight(String flightId, Flights updatedFlight) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Flights existingFlight = flightsRepository.findById(flightId)
                    .orElseThrow(() -> new OurException("Flight not found with ID: " + flightId));

            // Cập nhật các trường nếu khác null
            if (updatedFlight.getFlightCode() != null) {
                existingFlight.setFlightCode(updatedFlight.getFlightCode());
            }
            if (updatedFlight.getAirline() != null) {
                existingFlight.setAirline(updatedFlight.getAirline());
            }
            if (updatedFlight.getSymbol() != null) {
                existingFlight.setSymbol(updatedFlight.getSymbol());
            }
            if (updatedFlight.getTakeoffTime() != null) {
                existingFlight.setTakeoffTime(updatedFlight.getTakeoffTime());
            }
            if (updatedFlight.getLandingTime() != null) {
                existingFlight.setLandingTime(updatedFlight.getLandingTime());
            }
            if (updatedFlight.getOriginalPrice() != null) {
                existingFlight.setOriginalPrice(updatedFlight.getOriginalPrice());
            }
            if (updatedFlight.getTax() != null) {
                existingFlight.setTax(updatedFlight.getTax());
            }
            if (updatedFlight.getTotalPrice() != null) {
                existingFlight.setTotalPrice(updatedFlight.getTotalPrice());
            }
            if (updatedFlight.getSeatClass() != null) {
                existingFlight.setSeatClass(updatedFlight.getSeatClass());
            }
            if (updatedFlight.getDepartureProvince() != null) {
                existingFlight.setDepartureProvince(updatedFlight.getDepartureProvince());
            }
            if (updatedFlight.getDestinationProvince() != null) {
                existingFlight.setDestinationProvince(updatedFlight.getDestinationProvince());
            }

            Flights savedFlight = flightsRepository.save(existingFlight);
            FlightsDTO flightsDTO = Utils.mapFlightEntityToFlightDTO(savedFlight);

            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Flight updated successfully.");
            responseDTO.setFlightsDTO(flightsDTO);

        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        } catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error while updating flight: " + e.getMessage());
        }
        return responseDTO;
    }

}
