package com.ams.airManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flights")
public class Flights {

    @Id
    @Column(name = "flight_id", length = 15)
    private String flightId;

    @Column(name = "flight_code", length = 3)
    private String flightCode;

    @Column(name = "airline", length = 18)
    private String airline;

    @Column(name = "symbol", length = 6)
    private String symbol;

    @Column(name = "takeoff_time", length = 19)
    private String takeoffTime;

    @Column(name = "landing_time", length = 19)
    private String landingTime;

    @Column(name = "original_price", precision = 8, scale = 1)
    private Double originalPrice;

    @Column(name = "tax", precision = 8, scale = 1)
    private Double tax;

    @Column(name = "total_price", precision = 8, scale = 1)
    private Double totalPrice;

    @Column(name = "departure_point", length = 9)
    private String departurePoint;

    @Column(name = "departure_airport", length = 19)
    private String departureAirport;

    @Column(name = "destination", length = 9)
    private String destination;

    @Column(name = "landing_airport", length = 19)
    private String landingAirport;

    @Column(name = "seat_class", length = 31)
    private String seatClass;
}