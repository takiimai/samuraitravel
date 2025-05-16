package com.example.samuraitravel.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByRoomsamuraitravelId(Long samuraitravelId);
    
    List<Booking> findByRoomIdAndStatusIn(Long roomId, List<Booking.BookingStatus> statuses);
    
    List<Booking> findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
        LocalDate startDate, LocalDate endDate);
}
