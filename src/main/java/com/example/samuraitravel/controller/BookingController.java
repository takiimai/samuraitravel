package com.example.samuraitravel.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.dto.BookingDto;
import com.example.samuraitravel.model.Booking;
import com.example.samuraitravel.model.Room;
import com.example.samuraitravel.model.User;
import com.example.samuraitravel.service.BookingService;
import com.example.samuraitravel.service.samuraitravelService;

@Controller
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;
	private final samuraitravelService samuraitravelService;

	@Autowired
	public BookingController(
			BookingService bookingService,
			samuraitravelService samuraitravelService) {
		this.bookingService = bookingService;
		this.samuraitravelService = samuraitravelService;
	}

	@GetMapping("/new")
	public String showBookingForm(
			@RequestParam Long roomId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
			@RequestParam(defaultValue = "1") int guests,
			Model model) {

		Room room = samuraitravelService.findRoomById(roomId);

		if (!bookingService.isRoomAvailable(roomId, checkInDate, checkOutDate)) {
			return "redirect:/samuraitravels/" + room.getSamuraitravel().getId() + "?error=roomNotAvailable";
		}

		BookingDto bookingDto = new BookingDto();
		bookingDto.setRoomId(roomId);
		bookingDto.setCheckInDate(checkInDate);
		bookingDto.setCheckOutDate(checkOutDate);
		bookingDto.setNumberOfGuests(guests);

		long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

		model.addAttribute("room", room);
		model.addAttribute("samuraitravel", room.getSamuraitravel());
		model.addAttribute("booking", bookingDto);
		model.addAttribute("nights", nights);
		model.addAttribute("totalPrice", room.getPricePerNight().multiply(java.math.BigDecimal.valueOf(nights)));

		return "booking/form";
	}

	@PostMapping("/new")
	public String createBooking(
			@Valid @ModelAttribute("booking") BookingDto bookingDto,
			BindingResult result,
			@AuthenticationPrincipal UserDetails currentUser,
			Model model) {

		if (result.hasErrors()) {
			Room room = samuraitravelService.findRoomById(bookingDto.getRoomId());
			model.addAttribute("room", room);
			model.addAttribute("samuraitravel", room.getSamuraitravel());
			return "booking/form";
		}

		// 現在のユーザーIDを設定
		User user = (User) currentUser;
		bookingDto.setUserId(user.getId());

		Booking booking = bookingService.createBooking(bookingDto);

		return "redirect:/bookings/confirm/" + booking.getId();
	}

	@GetMapping("/confirm/{id}")
	public String confirmBooking(@PathVariable Long id, Model model) {
		Booking booking = bookingService.findById(id);
		model.addAttribute("booking", booking);
		return "booking/confirm";
	}

	@GetMapping("/my-bookings")
	public String myBookings(@AuthenticationPrincipal UserDetails currentUser, Model model) {
		User user = (User) currentUser;
		List<Booking> bookings = bookingService.findByUserId(user.getId());
		model.addAttribute("bookings", bookings);
		return "user/bookings";
	}

	@PostMapping("/{id}/cancel")
	public String cancelBooking(@PathVariable Long id) {
		bookingService.cancelBooking(id);
		return "redirect:/bookings/my-bookings?cancelled";
	}
}
