package com.example.samuraitravel.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.example.samuraitravel.dto.samuraitravelDto;
import com.example.samuraitravel.model.Booking;
import com.example.samuraitravel.model.Room;
import com.example.samuraitravel.model.samuraitravel;
import com.example.samuraitravel.service.BookingService;
import com.example.samuraitravel.service.UserService;
import com.example.samuraitravel.service.samuraitravelService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final samuraitravelService samuraitravelService;
    private final BookingService bookingService;
    private final UserService userService;

    @Autowired
    public AdminController(
            samuraitravelService samuraitravelService,
            BookingService bookingService,あ
            UserService userService) {
        this.samuraitravelService = samuraitravelService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<samuraitravel> samuraitravels = samuraitravelService.findAllsamuraitravels();
        List<Booking> recentBookings = bookingService.findAllBookings();
        List<User> users = userService.findAllUsers();
        
        model.addAttribute("samuraitravelCount", samuraitravels.size());
        model.addAttribute("bookingCount", recentBookings.size());
        model.addAttribute("userCount", users.size());
        model.addAttribute("recentBookings", recentBookings);
        
        return "admin/dashboard";
    }

    @GetMapping("/samuraitravels")
    public String listsamuraitravels(Model model) {
        List<samuraitravel> samuraitravels = samuraitravelService.findAllsamuraitravels();
        model.addAttribute("samuraitravels", samuraitravels);
        return "admin/samuraitravels";
    }

    @GetMapping("/samuraitravels/new")
    public String newsamuraitravelForm(Model model) {
        model.addAttribute("samuraitravel", new samuraitravelDto());
        return "admin/samuraitravel-form";
    }

    @PostMapping("/samuraitravels/new")
    public String createsamuraitravel(
            @Valid @ModelAttribute("samuraitravel") samuraitravelDto samuraitravelDto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        if (result.hasErrors()) {
            return "admin/samuraitravel-form";
        }
        
        User user = (User) currentUser;
        samuraitravelService.createsamuraitravel(samuraitravelDto, user.getId());
        
        return "redirect:/admin/samuraitravels";
    }

    @GetMapping("/samuraitravels/edit/{id}")
    public String editsamuraitravelForm(@PathVariable Long id, Model model) {
        samuraitravel samuraitravel = samuraitravelService.findById(id);
        
        samuraitravelDto samuraitravelDto = new samuraitravelDto();
        samuraitravelDto.setId(samuraitravel.getId());
        samuraitravelDto.setName(samuraitravel.getName());
        samuraitravelDto.setDescription(samuraitravel.getDescription());
        samuraitravelDto.setAddress(samuraitravel.getAddress());
        samuraitravelDto.setCity(samuraitravel.getCity());
        samuraitravelDto.setPrefecture(samuraitravel.getPrefecture());
        samuraitravelDto.setPostalCode(samuraitravel.getPostalCode());
        samuraitravelDto.setPhoneNumber(samuraitravel.getPhoneNumber());
        samuraitravelDto.setEmail(samuraitravel.getEmail());
        samuraitravelDto.setWebsite(samuraitravel.getWebsite());
        samuraitravelDto.setActive(samuraitravel.isActive());
        
        model.addAttribute("samuraitravel", samuraitravelDto);
        return "admin/samuraitravel-form";
    }

    @PostMapping("/samuraitravels/edit/{id}")
    public String updatesamuraitravel(
            @PathVariable Long id,
            @Valid @ModelAttribute("samuraitravel") samuraitravelDto samuraitravelDto,
            BindingResult result) {
        
        if (result.hasErrors()) {
            return "admin/samuraitravel-form";
        }
        
        samuraitravelDto.setId(id);
        samuraitravelService.updatesamuraitravel(samuraitravelDto);
        
        return "redirect:/admin/samuraitravels";
    }

    @PostMapping("/samuraitravels/delete/{id}")
    public String deletesamuraitravel(@PathVariable Long id) {
        samuraitravelService.deletesamuraitravel(id);
        return "redirect:/admin/samuraitravels";
    }

    @GetMapping("/bookings")
    public String listBookings(Model model) {
        List<Booking> bookings = bookingService.findAllBookings();
        model.addAttribute("bookings", bookings);
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/status")
    public String updateBookingStatus(
            @PathVariable Long id,
            @RequestParam Booking.BookingStatus status) {
        
        bookingService.updateBookingStatus(id, status);
        return "redirect:/admin/bookings";
    }

    @GetMapping("/rooms/{samuraitravelId}")
    public String listRooms(@PathVariable Long samuraitravelId, Model model) {
        samuraitravel samuraitravel = samuraitravelService.findById(samuraitravelId);
        List<Room> rooms = samuraitravelService.findRoomsBysamuraitravelId(samuraitravelId);
        
        model.addAttribute("samuraitravel", samuraitravel);
        model.addAttribute("rooms", rooms);
        
        return "admin/rooms";
    }

    @GetMapping("/rooms/{samuraitravelId}/new")
    public String newRoomForm(@PathVariable Long samuraitravelId, Model model) {
        samuraitravel samuraitravel = samuraitravelService.findById(samuraitravelId);
        Room room = new Room();
        room.setsamuraitravel(samuraitravel);
        
        model.addAttribute("room", room);
        model.addAttribute("samuraitravel", samuraitravel);
        
        return "admin/room-form";
    }

    @PostMapping("/rooms/{samuraitravelId}/new")
    public String createRoom(
            @PathVariable Long samuraitravelId,
            @Valid @ModelAttribute("room") Room room,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            samuraitravel samuraitravel = samuraitravelService.findById(samuraitravelId);
            model.addAttribute("samuraitravel", samuraitravel);
            return "admin/room-form";
        }
        
        samuraitravel samuraitravel = samuraitravelService.findById(samuraitravelId);
        room.setsamuraitravel(samuraitravel);
        
        samuraitravelService.createRoom(room);
        
        return "redirect:/admin/rooms/" + samuraitravelId;
    }

    @GetMapping("/rooms/{samuraitravelId}/edit/{id}")
    public String editRoomForm(
            @PathVariable Long samuraitravelId,
            @PathVariable Long id,
            Model model) {
        
        samuraitravel samuraitravel = samuraitravelService.findById(samuraitravelId);
        Room room = samuraitravelService.findRoomById(id);
        
        model.addAttribute("room", room);
        model.addAttribute("samuraitravel", samuraitravel);
        
        return "admin/room-form";
    }

    @PostMapping("/rooms/{samuraitravelId}/edit/{id}")
    public String updateRoom(
            @PathVariable Long samuraitravelId,
            @PathVariable Long id,
            @Valid @ModelAttribute("room") Room room,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            samuraitravel samuraitravel = samuraitravelService.findById(samuraitravelId);
            model.addAttribute("samuraitravel", samuraitravel);
            return "admin/room-form";
        }
        
        room.setId(id);
        samuraitravel samuraitravel = samuraitravelService.findById(samuraitravelId);
        room.setsamuraitravel(samuraitravel);
        
        samuraitravelService.updateRoom(room);
        
        return "redirect:/admin/rooms/" + samuraitravelId;
    }

    @PostMapping("/rooms/{samuraitravelId}/delete/{id}")
    public String deleteRoom(
            @PathVariable Long samuraitravelId,
            @PathVariable Long id) {
        
        samuraitravelService.deleteRoom(id);
        return "redirect:/admin/rooms/" + samuraitravelId;
    }
}
