package com.example.samuraitravel.service;

import java.time.LocalDate;
import java.util.List;

import com.example.samuraitravel.dto.HouseDto;
import com.example.samuraitravel.dto.SearchDto;
import com.example.samuraitravel.model.House;

public interface HouseService {
    
    List<House> findAllHouses();
    
    House findById(Long id);
    
    House createHouse(HouseDto houseDto);
    
    House updateHouse(HouseDto houseDto);
    
    void deleteHouse(Long id);
    
    List<House> searchHouses(SearchDto searchDto);
    
    List<House> findAvailableHouses(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfPeople);
    
    boolean isHouseAvailable(Long houseId, LocalDate checkInDate, LocalDate checkOutDate);
}
