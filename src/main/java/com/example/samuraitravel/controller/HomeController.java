package com.example.samuraitravel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.samuraitravel.dto.SearchDto;
import com.example.samuraitravel.model.House;
import com.example.samuraitravel.service.HouseService;

@Controller
public class HomeController {

    private final HouseService houseService;

    @Autowired
    public HomeController(HouseService houseService) {
        this.houseService = houseService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<House> featuredHouses = houseService.findAllHouses();
        model.addAttribute("houses", featuredHouses);
        model.addAttribute("searchDto", new SearchDto());
        return "index";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute SearchDto searchDto, Model model) {
        List<House> results = houseService.searchHouses(searchDto);
        model.addAttribute("houses", results);
        model.addAttribute("searchDto", searchDto);
        return "house/list";
    }
    
    @GetMapping("/search/availability")
    public String searchAvailability(
            @ModelAttribute SearchDto searchDto,
            Model model) {
        
        if (searchDto.getCheckInDate() == null) {
            searchDto.setCheckInDate(LocalDate.now().plusDays(1));
        }
        
        if (searchDto.getCheckOutDate() == null) {
            searchDto.setCheckOutDate(searchDto.getCheckInDate().plusDays(1));
        }
        
        if (searchDto.getNumberOfPeople() == null) {
            searchDto.setNumberOfPeople(2);
        }
        
        List<House> availableHouses = houseService.findAvailableHouses(
                searchDto.getCheckInDate(),
                searchDto.getCheckOutDate(),
                searchDto.getNumberOfPeople()
        );
        
        model.addAttribute("houses", availableHouses);
        model.addAttribute("searchDto", searchDto);
        
        return "house/availability";
    }
}
