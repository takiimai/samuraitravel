package com.example.samuraitravel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraitravel.model.House;

public interface HouseRepository extends JpaRepository<House, Long> {
    
    @Query("SELECT h FROM House h WHERE " +
           "(:keyword IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:minPrice IS NULL OR h.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR h.price <= :maxPrice) " +
           "AND (:capacity IS NULL OR h.capacity >= :capacity)")
    List<House> searchHouses(
            @Param("keyword") String keyword,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("capacity") Integer capacity
    );
    
    @Query("SELECT h FROM House h WHERE h.id NOT IN " +
           "(SELECT r.house.id FROM Reservation r WHERE " +
           "r.status IN ('PENDING', 'CONFIRMED') AND " +
           "((r.checkInDate <= :checkOutDate AND r.checkOutDate >= :checkInDate))) " +
           "AND h.capacity >= :numberOfPeople")
    List<House> findAvailableHouses(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfPeople") Integer numberOfPeople
    );
}
