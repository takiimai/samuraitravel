package com.example.samuraitravel.service;

import java.time.LocalDate;
import java.util.List;

import com.example.samuraitravel.dto.SearchDto;
import com.example.samuraitravel.dto.samuraitravelDto;
import com.example.samuraitravel.model.Room;
import com.example.samuraitravel.model.samuraitravel;

public interface samuraitravelService {
    List<samuraitravel> findAllsamuraitravels();
    
    List<samuraitravel> findActivesamuraitravels();
    
    samuraitravel findById(Long id);
    
    samuraitravel createsamuraitravel(samuraitravelDto samuraitravelDto, Long ownerId);
    
    samuraitravel updatesamuraitravel(samuraitravelDto samuraitravelDto);
    
    void deletesamuraitravel(Long id);
    
    List<samuraitravel> searchsamuraitravels(SearchDto searchDto);
    
    List<Room> findRoomsBysamuraitravelId(Long samuraitravelId);
    
    List<Room> findAvailableRooms(
        Long samuraitravelId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int guestCount
    );
    
    Room findRoomById(Long roomId);
    
    Room createRoom(Room room);
    
    Room updateRoom(Room room);
    
    void deleteRoom(Long roomId);
}
