package com.example.samuraitravel.service;

import java.util.List;

import com.example.samuraitravel.dto.ReservationDto;
import com.example.samuraitravel.model.Reservation;
import com.example.samuraitravel.model.Reservation.ReservationStatus;

public interface ReservationService {
    
    Reservation createReservation(ReservationDto reservationDto);
    
    Reservation findById(Long id);
    
    List<Reservation> findByUserId(Long userId);
    
    List<Reservation> findByHouseId(Long houseId);
    
    List<Reservation> findAll();
    
    List<Reservation> findByStatus(ReservationStatus status);
    
    Reservation updateStatus(Long id, ReservationStatus status);
    
    void cancelReservation(Long id);
}
