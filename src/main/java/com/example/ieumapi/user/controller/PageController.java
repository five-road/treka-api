package com.example.ieumapi.user.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("googleClientId", googleClientId);
        return "login";
    }

    @GetMapping("/my-page/delete-account")
    public String deleteAccountPage() {
        return "delete-account";
    }
}
