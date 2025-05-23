package com.ams.airManagement.repository;

import com.ams.airManagement.entity.Flights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightsRepository extends JpaRepository<Flights, String> {

    List<Flights> findByDepartureProvince_ProvinceIdAndDestinationProvince_ProvinceIdAndSeatClass(
            String departureId, String destinationId, String seatClass);

    List<Flights> findByTakeoffTime(String takeoffTime);

    // find with sql => add later
    //@Query("select f from flights f")
    //List<Flights> getAvailableFlights();

}
