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

import com.example.samuraitravel.model.Room;
import com.example.samuraitravel.model.samuraitravel;
import com.example.samuraitravel.service.samuraitravelService;

@Controller
@RequestMapping("/samuraitravels")
public class samuraitravelController {

    private final samuraitravelService samuraitravelService;

    @Autowired
    public samuraitravelController(samuraitravelService samuraitravelService) {
        this.samuraitravelService = samuraitravelService;
    }

    @GetMapping
    public String listsamuraitravels(Model model) {
        List<samuraitravel> samuraitravels = samuraitravelService.findActivesamuraitravels();
        model.addAttribute("samuraitravels", samuraitravels);
        return "samuraitravel/list";
    }

    @GetMapping("/{id}")
    public String viewsamuraitravel(@PathVariable Long id, Model model) {
        samuraitravel samuraitravel = samuraitravelService.findById(id);
        List<Room> rooms = samuraitravelService.findRoomsBysamuraitravelId(id);
        
        model.addAttribute("samuraitravel", samuraitravel);
        model.addAttribute("rooms", rooms);
        return "samuraitravel/detail";
    }

    @GetMapping("/{id}/rooms")
    public String checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(defaultValue = "1") int guests,
            Model model) {
        
        samuraitravel samuraitravel = samuraitravelService.findById(id);
        List<Room> availableRooms = samuraitravelService.findAvailableRooms(
                id, checkInDate, checkOutDate, guests);
        
        model.addAttribute("samuraitravel", samuraitravel);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("guests", guests);
        
        return "samuraitravel/rooms";
    }
}
