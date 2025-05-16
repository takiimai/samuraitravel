package com.example.samuraitravel.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.dto.SearchDto;
import com.example.samuraitravel.dto.samuraitravelDto;
import com.example.samuraitravel.model.Room;
import com.example.samuraitravel.model.samuraitravel;
import com.example.samuraitravel.repository.RoomRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.repository.samuraitravelRepository;
import com.example.samuraitravel.service.samuraitravelService;

@Service
public class samuraitravelServiceImpl implements samuraitravelService {

    private final samuraitravelRepository samuraitravelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public samuraitravelServiceImpl(
            samuraitravelRepository samuraitravelRepository,
            RoomRepository roomRepository,
            UserRepository userRepository) {
        this.samuraitravelRepository = samuraitravelRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<samuraitravel> findAllsamuraitravels() {
        return samuraitravelRepository.findAll();
    }

    @Override
    public List<samuraitravel> findActivesamuraitravels() {
        return samuraitravelRepository.findByActiveTrue();
    }

    @Override
    public samuraitravel findById(Long id) {
        return samuraitravelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("samuraitravel not found with id: " + id));
    }

    @Override
    @Transactional
    public samuraitravel createsamuraitravel(samuraitravelDto samuraitravelDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
        
        samuraitravel samuraitravel = new samuraitravel();
        samuraitravel.setName(samuraitravelDto.getName());
        samuraitravel.setDescription(samuraitravelDto.getDescription());
        samuraitravel.setAddress(samuraitravelDto.getAddress());
        samuraitravel.setCity(samuraitravelDto.getCity());
        samuraitravel.setPrefecture(samuraitravelDto.getPrefecture());
        samuraitravel.setPostalCode(samuraitravelDto.getPostalCode());
        samuraitravel.setPhoneNumber(samuraitravelDto.getPhoneNumber());
        samuraitravel.setEmail(samuraitravelDto.getEmail());
        samuraitravel.setWebsite(samuraitravelDto.getWebsite());
        samuraitravel.setActive(samuraitravelDto.isActive());
        samuraitravel.setOwner(owner);
        
        return samuraitravelRepository.save(samuraitravel);
    }

    @Override
    @Transactional
    public samuraitravel updatesamuraitravel(samuraitravelDto samuraitravelDto) {
        samuraitravel samuraitravel = findById(samuraitravelDto.getId());
        
        samuraitravel.setName(samuraitravelDto.getName());
        samuraitravel.setDescription(samuraitravelDto.getDescription());
        samuraitravel.setAddress(samuraitravelDto.getAddress());
        samuraitravel.setCity(samuraitravelDto.getCity());
        samuraitravel.setPrefecture(samuraitravelDto.getPrefecture());
        samuraitravel.setPostalCode(samuraitravelDto.getPostalCode());
        samuraitravel.setPhoneNumber(samuraitravelDto.getPhoneNumber());
        samuraitravel.setEmail(samuraitravelDto.getEmail());
        samuraitravel.setWebsite(samuraitravelDto.getWebsite());
        samuraitravel.setActive(samuraitravelDto.isActive());
        
        return samuraitravelRepository.save(samuraitravel);
    }

    @Override
    @Transactional
    public void deletesamuraitravel(Long id) {
        samuraitravelRepository.deleteById(id);
    }

    @Override
    public List<samuraitravel> searchsamuraitravels(SearchDto searchDto) {
        return samuraitravelRepository.searchsamuraitravels(
                searchDto.getPrefecture(),
                searchDto.getCity(),
                searchDto.getKeyword()
        );
    }

    @Override
    public List<Room> findRoomsBysamuraitravelId(Long samuraitravelId) {
        return roomRepository.findBysamuraitravelId(samuraitravelId);
    }

    @Override
    public List<Room> findAvailableRooms(
            Long samuraitravelId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int guestCount) {
        return roomRepository.findAvailableRooms(samuraitravelId, checkInDate, checkOutDate, guestCount);
    }

    @Override
    public Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
    }

    @Override
    @Transactional
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public Room updateRoom(Room room) {
        Room existingRoom = findRoomById(room.getId());
        existingRoom.setName(room.getName());
        existingRoom.setDescription(room.getDescription());
        existingRoom.setCapacity(room.getCapacity());
        existingRoom.setPricePerNight(room.getPricePerNight());
        existingRoom.setTotalRooms(room.getTotalRooms());
        existingRoom.setAmenities(room.getAmenities());
        
        return roomRepository.save(existingRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }
}
