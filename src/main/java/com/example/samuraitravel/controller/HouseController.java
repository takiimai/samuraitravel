package com.example.samuraitravel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.dto.ReservationDto;
import com.example.samuraitravel.model.House;
import com.example.samuraitravel.service.HouseService;

@Controller
@RequestMapping("/houses")
public class HouseController {

	private final HouseService houseService;

	@Autowired
	public HouseController(HouseService houseService) {
		this.houseService = houseService;
	}

	@GetMapping
	public String listHouses(Model model) {
		List<House> houses = houseService.findAllHouses();
		model.addAttribute("houses", houses);
		return "house/list";
	}

	@GetMapping("/{id}")
	public String viewHouse(@PathVariable Long id, Model model) {
		House house = houseService.findById(id);
		model.addAttribute("house", house);

		// 予約用のDTOを準備
		ReservationDto reservationDto = new ReservationDto();
		reservationDto.setHouseId(id);
		reservationDto.setCheckInDate(LocalDate.now().plusDays(1));
		reservationDto.setCheckOutDate(LocalDate.now().plusDays(2));
		reservationDto.setNumberOfPeople(2);

		model.addAttribute("reservation", reservationDto);

		return "house/detail";
	}

	@GetMapping("/{id}/check-availability")
	public String checkAvailability(
			@PathVariable Long id,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
			@RequestParam(defaultValue = "1") Integer numberOfPeople,
			Model model) {

		House house = houseService.findById(id);
		boolean isAvailable = houseService.isHouseAvailable(id, checkInDate, checkOutDate);

		// ハウスの定員を確認
		boolean hasCapacity = house.getCapacity() >= numberOfPeople;

		model.addAttribute("house", house);
		model.addAttribute("isAvailable", isAvailable && hasCapacity);
		model.addAttribute("checkInDate", checkInDate);
		model.addAttribute("checkOutDate", checkOutDate);
		model.addAttribute("numberOfPeople", numberOfPeople);

		// 予約用のDTOを準備
		if (isAvailable && hasCapacity) {
			ReservationDto reservationDto = new ReservationDto();
			reservationDto.setHouseId(id);
			reservationDto.setCheckInDate(checkInDate);
			reservationDto.setCheckOutDate(checkOutDate);
			reservationDto.setNumberOfPeople(numberOfPeople);

			model.addAttribute("reservation", reservationDto);
		}

		return "house/availability-result";
	}
}
