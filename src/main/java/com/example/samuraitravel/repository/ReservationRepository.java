package com.example.samuraitravel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraitravel.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByUserId(Long userId);
    
    List<Reservation> findByHouseId(Long houseId);
    
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE " +
           "r.house.id = :houseId AND " +
           "r.status IN ('PENDING', 'CONFIRMED') AND " +
           "(r.checkInDate <= :checkOutDate AND r.checkOutDate >= :checkInDate)")
    List<Reservation> findConflictingReservations(
            @Param("houseId") Long houseId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
