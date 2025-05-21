package com.example.samuraitravel.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraitravel.dto.HouseDto;
import com.example.samuraitravel.dto.SearchDto;
import com.example.samuraitravel.model.House;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReservationRepository;
import com.example.samuraitravel.service.HouseService;

@Service
public class HouseServiceImpl implements HouseService {

    private static final Logger logger = LoggerFactory.getLogger(HouseServiceImpl.class);

    private final HouseRepository houseRepository;
    private final ReservationRepository reservationRepository;
    
    @Value("${house.image.upload.dir:src/main/resources/static/images/houses/}")
    private String UPLOAD_DIR;

    @Autowired
    public HouseServiceImpl(
            HouseRepository houseRepository,
            ReservationRepository reservationRepository) {
        this.houseRepository = houseRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<House> findAllHouses() {
        return houseRepository.findAll();
    }

    @Override
    public House findById(Long id) {
        return houseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("民宿が見つかりません: " + id));
    }

    @Override
    @Transactional
    public House createHouse(HouseDto houseDto) {
        House house = new House();
        house.setName(houseDto.getName());
        house.setDescription(houseDto.getDescription());
        house.setPrice(houseDto.getPrice());
        house.setCapacity(houseDto.getCapacity());
        house.setPostalCode(houseDto.getPostalCode());
        house.setAddress(houseDto.getAddress());
        house.setPhoneNumber(houseDto.getPhoneNumber());
        
        // 画像ファイルがアップロードされた場合は保存
        if (houseDto.getImageFile() != null && !houseDto.getImageFile().isEmpty()) {
            String imageName = saveImage(houseDto.getImageFile());
            house.setImageName(imageName);
        } else {
            // デフォルト画像名をセット
            house.setImageName("default_house.jpg");
        }
        
        return houseRepository.save(house);
    }

    @Override
    @Transactional
    public House updateHouse(HouseDto houseDto) {
        House house = findById(houseDto.getId());
        house.setName(houseDto.getName());
        house.setDescription(houseDto.getDescription());
        house.setPrice(houseDto.getPrice());
        house.setCapacity(houseDto.getCapacity());
        house.setPostalCode(houseDto.getPostalCode());
        house.setAddress(houseDto.getAddress());
        house.setPhoneNumber(houseDto.getPhoneNumber());
        
        // 新しい画像がアップロードされた場合
        if (houseDto.getImageFile() != null && !houseDto.getImageFile().isEmpty()) {
            // 既存の画像を削除（デフォルト画像以外）
            if (house.getImageName() != null && !house.getImageName().equals("default_house.jpg")) {
                deleteImage(house.getImageName());
            }
            // 新しい画像を保存
            String imageName = saveImage(houseDto.getImageFile());
            house.setImageName(imageName);
        }
        
        return houseRepository.save(house);
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        House house = findById(id);
        
        // 画像の削除（デフォルト画像以外）
        if (house.getImageName() != null && !house.getImageName().equals("default_house.jpg")) {
            deleteImage(house.getImageName());
        }
        
        houseRepository.deleteById(id);
    }

    @Override
    public List<House> searchHouses(SearchDto searchDto) {
        return houseRepository.searchHouses(
                searchDto.getKeyword(),
                searchDto.getMinPrice(),
                searchDto.getMaxPrice(),
                searchDto.getNumberOfPeople()
        );
    }

    @Override
    public List<House> findAvailableHouses(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfPeople) {
        return houseRepository.findAvailableHouses(checkInDate, checkOutDate, numberOfPeople);
    }

    @Override
    public boolean isHouseAvailable(Long houseId, LocalDate checkInDate, LocalDate checkOutDate) {
        return reservationRepository.findConflictingReservations(houseId, checkInDate, checkOutDate).isEmpty();
    }
    
    // 画像ファイルを保存し、ファイル名を返す
    private String saveImage(MultipartFile imageFile) {
        try {
            // アップロードディレクトリがなければ作成
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // ユニークなファイル名を生成
            String originalFilename = imageFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // ファイルを保存
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(imageFile.getInputStream(), filePath);
            
            logger.info("画像を保存しました: {}", newFilename);
            return newFilename;
        } catch (IOException e) {
            logger.error("画像ファイルの保存に失敗しました", e);
            throw new RuntimeException("画像ファイルの保存に失敗しました", e);
        }
    }
    
    // 画像ファイルを削除
    private void deleteImage(String imageName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(imageName);
            Files.deleteIfExists(filePath);
            logger.info("画像を削除しました: {}", imageName);
        } catch (IOException e) {
            logger.error("画像ファイルの削除に失敗しました", e);
            throw new RuntimeException("画像ファイルの削除に失敗しました", e);
        }
    }
}
