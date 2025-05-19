package com.example.samuraitravel.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.dto.UserDto;
import com.example.samuraitravel.model.User;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userService.emailExists(userDto.getEmail())) {
            result.rejectValue("email", "error.user", "このメールアドレスは既に使用されています。");
            return "auth/register";
        }

        userService.registerUser(userDto);
        return "redirect:/auth/registration-success";
    }
    
    @GetMapping("/registration-success")
    public String registrationSuccess() {
        return "auth/registration-success";
    }
    
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        String result = userService.validateVerificationToken(token);
        
        if (result.equals("valid")) {
            redirectAttributes.addFlashAttribute("message", "メールアドレスが確認されました。ログインしてください。");
            return "redirect:/auth/login?verified";
        } else if (result.equals("expired")) {
            redirectAttributes.addFlashAttribute("error", "トークンの有効期限が切れています。再度登録してください。");
            return "redirect:/auth/token-expired";
        } else {
            redirectAttributes.addFlashAttribute("error", "無効なトークンです。");
            return "redirect:/auth/invalid-token";
        }
    }
    
    @GetMapping("/token-expired")
    public String tokenExpired() {
        return "auth/token-expired";
    }
    
    @GetMapping("/invalid-token")
    public String invalidToken() {
        return "auth/invalid-token";
    }
    
    @GetMapping("/resend-verification")
    public String resendVerificationForm() {
        return "auth/resend-verification";
    }
    
    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(email);
            
            if (!user.isEnabled()) {
                userService.createVerificationToken(user);
                redirectAttributes.addFlashAttribute("message", "確認メールを再送信しました。");
            } else {
                redirectAttributes.addFlashAttribute("message", "このアカウントは既に有効化されています。");
            }
            
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "メールアドレスが見つかりません。");
            return "redirect:/auth/resend-verification";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }
}
