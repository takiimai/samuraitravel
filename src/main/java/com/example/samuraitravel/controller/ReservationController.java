package com.example.samuraitravel.controller;

import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.samuraitravel.dto.ReservationDto;
import com.example.samuraitravel.model.House;
import com.example.samuraitravel.model.Reservation;
import com.example.samuraitravel.model.User;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReservationService;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

	private final ReservationService reservationService;
	private final HouseService houseService;
	private final UserService userService;

	@Autowired
	public ReservationController(
			ReservationService reservationService,
			HouseService houseService,
			UserService userService) {
		this.reservationService = reservationService;
		this.houseService = houseService;
		this.userService = userService;
	}

	@GetMapping("/new")
	public String showReservationForm(
			@ModelAttribute("reservation") ReservationDto reservationDto,
			Model model) {

		House house = houseService.findById(reservationDto.getHouseId());

		if (!houseService.isHouseAvailable(
				reservationDto.getHouseId(),
				reservationDto.getCheckInDate(),
				reservationDto.getCheckOutDate())) {
			return "redirect:/houses/" + house.getId() + "?error=notAvailable";
		}

		// 宿泊日数
		long nights = ChronoUnit.DAYS.between(
				reservationDto.getCheckInDate(),
				reservationDto.getCheckOutDate());

		// 合計金額
		int totalAmount = house.getPrice() * (int) nights;

		model.addAttribute("house", house);
		model.addAttribute("nights", nights);
		model.addAttribute("totalAmount", totalAmount);

		return "reservation/form";
	}

	@PostMapping("/new")
	public String createReservation(
			@Valid @ModelAttribute("reservation") ReservationDto reservationDto,
			BindingResult result,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {

		if (result.hasErrors()) {
			House house = houseService.findById(reservationDto.getHouseId());
			model.addAttribute("house", house);
			return "reservation/form";
		}

		// 現在のユーザー情報を取得
		User user = userService.findByEmail(userDetails.getUsername());
		reservationDto.setUserId(user.getId());

		try {
			Reservation reservation = reservationService.createReservation(reservationDto);
			return "redirect:/reservations/confirm/" + reservation.getId();
		} catch (Exception e) {
			House house = houseService.findById(reservationDto.getHouseId());
			model.addAttribute("house", house);
			model.addAttribute("errorMessage", e.getMessage());
			return "reservation/form";
		}
	}

	@GetMapping("/confirm/{id}")
	public String confirmReservation(@PathVariable Long id, Model model) {
		Reservation reservation = reservationService.findById(id);
		model.addAttribute("reservation", reservation);
		return "reservation/confirm";
	}

	@GetMapping("/my-reservations")
	public String myReservations(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User user = userService.findByEmail(userDetails.getUsername());
		List<Reservation> reservations = reservationService.findByUserId(user.getId());
		model.addAttribute("reservations", reservations);
		return "user/reservations";
	}

	@PostMapping("/{id}/cancel")
	public String cancelReservation(@PathVariable Long id) {
		reservationService.cancelReservation(id);
		return "redirect:/reservations/my-reservations?cancelled";
	}
}
