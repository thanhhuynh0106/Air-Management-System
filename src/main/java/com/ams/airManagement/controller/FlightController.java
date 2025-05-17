package com.ams.airManagement.controller;

import com.ams.airManagement.dto.ResponseDTO;
import com.ams.airManagement.entity.Flights;
import com.ams.airManagement.service.interfac.FlightServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    private FlightServiceInterface flightService;

    @PostMapping("/add")
    public ResponseEntity<ResponseDTO> addFlight(@RequestBody Flights flight) {
        ResponseDTO response = flightService.addNewFlight(flight);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllFlights() {
        ResponseDTO response = flightService.getAllFlights();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getFlightById(@PathVariable("id") String flightId) {
        ResponseDTO response = flightService.getFlightById(flightId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDTO> updateFlight(@PathVariable("id") String flightId, @RequestBody Flights flight) {
        ResponseDTO response = flightService.updateFlight(flightId, flight);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteFlight(@PathVariable("id") String flightId) {
        ResponseDTO response = flightService.deleteFlight(flightId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
