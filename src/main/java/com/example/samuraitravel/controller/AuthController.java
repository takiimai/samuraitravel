package com.example.samuraitravel.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
        logger.info("ユーザー登録リクエスト: {}", userDto.getEmail());
        
        if (result.hasErrors()) {
            logger.warn("登録フォームのバリデーションエラー: {}", result.getAllErrors());
            return "auth/register";
        }

        if (userService.emailExists(userDto.getEmail())) {
            result.rejectValue("email", "error.user", "このメールアドレスは既に使用されています。");
            logger.warn("メールアドレス重複: {}", userDto.getEmail());
            return "auth/register";
        }

        try {
            userService.registerUser(userDto);
            logger.info("ユーザー登録成功: {}", userDto.getEmail());
            return "redirect:/auth/registration-success";
        } catch (Exception e) {
            logger.error("ユーザー登録エラー: {}", e.getMessage(), e);
            model.addAttribute("error", "登録処理中にエラーが発生しました。");
            return "auth/register";
        }
    }
    
    @GetMapping("/registration-success")
    public String registrationSuccess() {
        return "auth/registration-success";
    }
    
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        logger.info("メール検証リクエスト: token={}", token);
        
        String result = userService.validateVerificationToken(token);
        
        if (result.equals("valid")) {
            redirectAttributes.addFlashAttribute("message", "メールアドレスが確認されました。ログインしてください。");
            logger.info("メール検証成功: token={}", token);
            return "redirect:/auth/login?verified";
        } else if (result.equals("expired")) {
            redirectAttributes.addFlashAttribute("error", "トークンの有効期限が切れています。再度登録してください。");
            logger.warn("トークン期限切れ: token={}", token);
            return "redirect:/auth/token-expired";
        } else {
            redirectAttributes.addFlashAttribute("error", "無効なトークンです。");
            logger.warn("無効なトークン: token={}", token);
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
        logger.info("検証メール再送リクエスト: {}", email);
        
        try {
            userService.resendVerificationToken(email);
            redirectAttributes.addFlashAttribute("message", "確認メールを再送信しました。");
            logger.info("検証メール再送成功: {}", email);
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            logger.error("検証メール再送失敗: {}", e.getMessage(), e);
            return "redirect:/auth/resend-verification";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }
}
