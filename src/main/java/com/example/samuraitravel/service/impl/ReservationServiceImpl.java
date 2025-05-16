package com.example.samuraitravel.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.dto.ReservationDto;
import com.example.samuraitravel.model.House;
import com.example.samuraitravel.model.Reservation;
import com.example.samuraitravel.model.Reservation.ReservationStatus;
import com.example.samuraitravel.model.User;
import com.example.samuraitravel.repository.ReservationRepository;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReservationService;
import com.example.samuraitravel.service.UserService;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final HouseService houseService;

    @Autowired
    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            UserService userService,
            HouseService houseService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.houseService = houseService;
    }

    @Override
    @Transactional
    public Reservation createReservation(ReservationDto reservationDto) {
        // ハウスと宿泊者を取得
        House house = houseService.findById(reservationDto.getHouseId());
        User user = userService.findById(reservationDto.getUserId());
        
        // 日付の妥当性チェック
        LocalDate checkInDate = reservationDto.getCheckInDate();
        LocalDate checkOutDate = reservationDto.getCheckOutDate();
        
        if (checkInDate.isEqual(checkOutDate) || checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("チェックアウト日はチェックイン日より後である必要があります");
        }
        
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("過去の日付は選択できません");
        }
        
        // 空室確認
        if (!houseService.isHouseAvailable(house.getId(), checkInDate, checkOutDate)) {
            throw new RuntimeException("選択した期間はすでに予約されています");
        }
        
        // 定員確認
        if (reservationDto.getNumberOfPeople() > house.getCapacity()) {
            throw new RuntimeException("予約人数が定員を超えています");
        }
        
        // 宿泊日数を計算
        long stayDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // 宿泊料金を計算（料金 × 宿泊日数）
        int amount = house.getPrice() * (int)stayDays;
        
        // 予約エンティティを作成
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setHouse(house);
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfPeople(reservationDto.getNumberOfPeople());
        reservation.setAmount(amount);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setSpecialRequirements(reservationDto.getSpecialRequirements());
        reservation.setStatus(ReservationStatus.PENDING);
        
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("予約が見つかりません: " + id));
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public List<Reservation> findByHouseId(Long houseId) {
        return reservationRepository.findByHouseId(houseId);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Reservation updateStatus(Long id, ReservationStatus status) {
        Reservation reservation = findById(id);
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = findById(id);
        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }
}