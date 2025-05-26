package com.ams.airManagement.repository;

import com.ams.airManagement.entity.Flights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightsRepository extends JpaRepository<Flights, String> {

    List<Flights> findByDepartureProvince_ProvinceIdAndDestinationProvince_ProvinceIdAndSeatClass(
            String departureId, String destinationId, String seatClass);

    List<Flights> findByTakeoffTime(String takeoffTime);

    @Query("SELECT f FROM Flights f WHERE f.departureProvince.provinceId = :from AND f.destinationProvince.provinceId = :to")
    List<Flights> findByRoute(@Param("from") String from, @Param("to") String to);

    @Query("SELECT f FROM Flights f WHERE f.departureProvince.provinceId = :from AND f.destinationProvince.provinceId = :to AND f.takeoffDate = :date")
    List<Flights> findByRouteAndDate(@Param("from") String from, @Param("to") String to, @Param("date") String date);

    @Query("SELECT f FROM Flights f WHERE f.departureProvince.provinceId = :from AND f.destinationProvince.provinceId = :to AND f.takeoffTime = :time")
    List<Flights> findByRouteAndTime(@Param("from") String from, @Param("to") String to, @Param("time") String time);

    @Query("SELECT f FROM Flights f WHERE f.departureProvince.provinceId = :from AND f.destinationProvince.provinceId = :to AND f.takeoffDate = :date AND f.takeoffTime = :time")
    List<Flights> findByRouteAndDateAndTime(@Param("from") String from, @Param("to") String to, @Param("date") String date, @Param("time") String time);

    @Query("SELECT f FROM Flights f WHERE f.airline LIKE %:airline%")
    List<Flights> findByAirline(@Param("airline") String airline);

    @Query("SELECT f FROM Flights f WHERE f.airline LIKE %:airline% AND f.takeoffDate = :date")
    List<Flights> findByAirlineAndDate(@Param("airline") String airline, @Param("date") String date);

    @Query("SELECT f FROM Flights f WHERE f.airline LIKE %:airline% AND f.takeoffTime = :time")
    List<Flights> findByAirlineAndTime(@Param("airline") String airline, @Param("time") String time);

    @Query("SELECT f FROM Flights f WHERE f.airline LIKE %:airline% AND f.takeoffDate = :date AND f.takeoffTime = :time")
    List<Flights> findByAirlineAndDateAndTime(@Param("airline") String airline, @Param("date") String date, @Param("time") String time);
}
