package com.example.samuraitravel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraitravel.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findBysamuraitravelId(Long samuraitravelId);
    
    @Query("SELECT r FROM Room r WHERE r.samuraitravel.id = :samuraitravelId " +
           "AND r.capacity >= :guestCount " +
           "AND r.id NOT IN (" +
           "    SELECT b.room.id FROM Booking b " +
           "    WHERE b.status IN ('PENDING', 'CONFIRMED') " +
           "    AND b.room.samuraitravel.id = :samuraitravelId " +
           "    AND (" +
           "        (b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate)" +
           "    )" +
           ")")
    List<Room> findAvailableRooms(
        @Param("samuraitravelId") Long samuraitravelId,
        @Param("checkInDate") LocalDate checkInDate,
        @Param("checkOutDate") LocalDate checkOutDate,
        @Param("guestCount") int guestCount
    );
}
