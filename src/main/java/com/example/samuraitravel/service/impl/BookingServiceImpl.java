package com.example.samuraitravel.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.dto.BookingDto;
import com.example.samuraitravel.model.Booking;
import com.example.samuraitravel.model.Room;
import com.example.samuraitravel.repository.BookingRepository;
import com.example.samuraitravel.service.BookingService;
import com.example.samuraitravel.service.UserService;
import com.example.samuraitravel.service.samuraitravelService;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final samuraitravelService samuraitravelService;

    @Autowired
    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserService userService,
            samuraitravelService samuraitravelService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.samuraitravelService = samuraitravelService;
    }

    @Override
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<Booking> findBysamuraitravelId(Long samuraitravelId) {
        return bookingRepository.findByRoomsamuraitravelId(samuraitravelId);
    }

    @Override
    @Transactional
    public Booking createBooking(BookingDto bookingDto) {
        // ユーザーと部屋の取得
        User user = userService.findById(bookingDto.getUserId());
        Room room = samuraitravelService.findRoomById(bookingDto.getRoomId());
        
        // 部屋の空き状況を確認
        if (!isRoomAvailable(bookingDto.getRoomId(), 
                bookingDto.getCheckInDate(), 
                bookingDto.getCheckOutDate())) {
            throw new RuntimeException("The room is not available for the selected dates");
        }
        
        // 宿泊日数の計算
        long nights = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        
        // 総額の計算
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        
        // 予約エンティティの作成
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setTotalPrice(totalPrice);
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setSpecialRequests(bookingDto.getSpecialRequests());
        
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long id, Booking.BookingStatus status) {
        Booking booking = findById(id);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = findById(id);
        booking.setStatus(Booking.BookingStatus.CANCELED);
        bookingRepository.save(booking);
    }

    @Override
    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Booking.BookingStatus> activeStatuses = 
                Arrays.asList(Booking.BookingStatus.PENDING, Booking.BookingStatus.CONFIRMED);
        
        List<Booking> existingBookings = 
                bookingRepository.findByRoomIdAndStatusIn(roomId, activeStatuses);
        
        for (Booking booking : existingBookings) {
            // 日付の重複をチェック
            if (!(booking.getCheckOutDate().isBefore(checkInDate) || 
                  booking.getCheckInDate().isAfter(checkOutDate))) {
                return false;
            }
        }
        
        return true;
    }
}
