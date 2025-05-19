package com.example.samuraitravel.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.samuraitravel.dto.UserDto;
import com.example.samuraitravel.model.User;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/profile")
	public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User user = userService.findByEmail(userDetails.getUsername());

		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setFurigana(user.getFurigana());
		userDto.setEmail(user.getEmail());
		userDto.setPostalCode(user.getPostalCode());
		userDto.setAddress(user.getAddress());
		userDto.setPhoneNumber(user.getPhoneNumber());

		model.addAttribute("user", userDto);
		return "user/profile";
	}

	@PostMapping("/profile")
	public String updateProfile(
			@Valid @ModelAttribute("user") UserDto userDto,
			BindingResult result,
			@AuthenticationPrincipal UserDetails userDetails) {

		if (result.hasErrors()) {
			return "user/profile";
		}

		User user = userService.findByEmail(userDetails.getUsername());
		userDto.setId(user.getId());

		userService.updateUser(userDto);
		return "redirect:/user/profile?success";
	}
}
