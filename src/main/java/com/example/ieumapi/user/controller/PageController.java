package com.example.ieumapi.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/my-page/delete-account")
    public String deleteAccountPage() {
        return "delete-account";
    }
}
