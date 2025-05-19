package com.example.samuraitravel.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.dto.HouseDto;
import com.example.samuraitravel.dto.UserDto;
import com.example.samuraitravel.model.House;
import com.example.samuraitravel.model.Reservation;
import com.example.samuraitravel.model.User;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReservationService;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final HouseService houseService;
	private final ReservationService reservationService;
	private final UserService userService;

	@Autowired
	public AdminController(
			HouseService houseService,
			ReservationService reservationService,
			UserService userService) {
		this.houseService = houseService;
		this.reservationService = reservationService;
		this.userService = userService;
	}

	@GetMapping
	public String dashboard(Model model) {
		List<House> houses = houseService.findAllHouses();
		List<Reservation> reservations = reservationService.findAll();
		List<User> users = userService.findAllUsers();

		model.addAttribute("houseCount", houses.size());
		model.addAttribute("reservationCount", reservations.size());
		model.addAttribute("userCount", users.size());
		model.addAttribute("recentReservations", reservations);

		return "admin/dashboard";
	}

	// 民宿管理
	@GetMapping("/houses")
	public String listHouses(Model model) {
		List<House> houses = houseService.findAllHouses();
		model.addAttribute("houses", houses);
		return "admin/houses";
	}

	@GetMapping("/houses/new")
	public String newHouseForm(Model model) {
		model.addAttribute("house", new HouseDto());
		return "admin/house-form";
	}

	@PostMapping("/houses/new")
	public String createHouse(
			@Valid @ModelAttribute("house") HouseDto houseDto,
			BindingResult result) {

		if (result.hasErrors()) {
			return "admin/house-form";
		}

		houseService.createHouse(houseDto);
		return "redirect:/admin/houses";
	}

	@GetMapping("/houses/edit/{id}")
	public String editHouseForm(@PathVariable Long id, Model model) {
		House house = houseService.findById(id);

		HouseDto houseDto = new HouseDto();
		houseDto.setId(house.getId());
		houseDto.setName(house.getName());
		houseDto.setDescription(house.getDescription());
		houseDto.setPrice(house.getPrice());
		houseDto.setCapacity(house.getCapacity());
		houseDto.setPostalCode(house.getPostalCode());
		houseDto.setAddress(house.getAddress());
		houseDto.setPhoneNumber(house.getPhoneNumber());
		houseDto.setImageName(house.getImageName());

		model.addAttribute("house", houseDto);
		return "admin/house-form";
	}

	@PostMapping("/houses/edit/{id}")
	public String updateHouse(
			@PathVariable Long id,
			@Valid @ModelAttribute("house") HouseDto houseDto,
			BindingResult result) {

		if (result.hasErrors()) {
			return "admin/house-form";
		}

		houseDto.setId(id);
		houseService.updateHouse(houseDto);

		return "redirect:/admin/houses";
	}

	@PostMapping("/houses/delete/{id}")
	public String deleteHouse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			houseService.deleteHouse(id);
			redirectAttributes.addFlashAttribute("success", "民宿を削除しました");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "民宿の削除に失敗しました: " + e.getMessage());
		}
		return "redirect:/admin/houses";
	}

	// 予約管理
	@GetMapping("/reservations")
	public String listReservations(Model model) {
		List<Reservation> reservations = reservationService.findAll();
		model.addAttribute("reservations", reservations);
		return "admin/reservations";
	}

	@PostMapping("/reservations/{id}/status")
	public String updateReservationStatus(
			@PathVariable Long id,
			@RequestParam Reservation.ReservationStatus status) {

		reservationService.updateStatus(id, status);
		return "redirect:/admin/reservations";
	}

	// ユーザー管理
	@GetMapping("/users")
	public String listUsers(Model model) {
		List<User> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "admin/users";
	}

	@GetMapping("/users/edit/{id}")
	public String editUserForm(@PathVariable Long id, Model model) {
		User user = userService.findById(id);

		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setFurigana(user.getFurigana());
		userDto.setEmail(user.getEmail());
		userDto.setPostalCode(user.getPostalCode());
		userDto.setAddress(user.getAddress());
		userDto.setPhoneNumber(user.getPhoneNumber());
		userDto.setRoleId(user.getRole().getId());
		userDto.setEnabled(user.isEnabled());

		model.addAttribute("user", userDto);
		return "admin/user-form";
	}

	@PostMapping("/users/edit/{id}")
	public String updateUser(
			@PathVariable Long id,
			@Valid @ModelAttribute("user") UserDto userDto,
			BindingResult result) {

		if (result.hasErrors()) {
			return "admin/user-form";
		}

		userDto.setId(id);
		userService.updateUser(userDto);

		return "redirect:/admin/users";
	}
}
