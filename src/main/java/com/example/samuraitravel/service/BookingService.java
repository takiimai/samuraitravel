package com.example.samuraitravel.service;

import java.time.LocalDate;
import java.util.List;

import com.example.samuraitravel.dto.BookingDto;
import com.example.samuraitravel.model.Booking;

public interface BookingService {
    List<Booking> findAllBookings();
    
    Booking findById(Long id);
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findBysamuraitravelId(Long samuraitravelId);
    
    Booking createBooking(BookingDto bookingDto);
    
    Booking updateBookingStatus(Long id, Booking.BookingStatus status);
    
    void cancelBooking(Long id);
    
    boolean isRoomAvailable(
        Long roomId,
        LocalDate checkInDate,
        LocalDate checkOutDate
    );
}
