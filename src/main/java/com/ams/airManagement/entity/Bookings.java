package com.ams.airManagement.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name ="bookings")
public class Bookings {

    @Id
    @Column(name = "booking_id")
    private int bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flights flight;

    @Column(name = "ticket_quantity")
    private Integer ticketQuantity;
}
