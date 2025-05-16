package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.samuraitravel.dto.SearchDto;
import com.example.samuraitravel.model.samuraitravel;
import com.example.samuraitravel.service.samuraitravelService;

@Controller
public class HomeController {

	private final samuraitravelService samuraitravelService;

	@Autowired
	public HomeController(samuraitravelService samuraitravelService) {
		this.samuraitravelService = samuraitravelService;
	}

	@GetMapping("/")
	public String home(Model model) {
		List<samuraitravel> featuredsamuraitravels = samuraitravelService.findActivesamuraitravels();
		model.addAttribute("samuraitravels", featuredsamuraitravels);
		model.addAttribute("searchDto", new SearchDto());
		return "index";
	}

	@PostMapping("/search")
	public String search(@ModelAttribute SearchDto searchDto, Model model) {
		List<samuraitravel> results = samuraitravelService.searchsamuraitravels(searchDto);
		model.addAttribute("samuraitravels", results);
		model.addAttribute("searchDto", searchDto);
		return "samuraitravel/list";
	}
}
