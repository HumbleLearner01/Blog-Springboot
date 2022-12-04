package com.example.blogpost;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("title", "Home");
        return "index";
    }

    @GetMapping("/login")
    public String lgoin(Model model) {
        model.addAttribute("title", "Login");
        return "user/logIn";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Signup!");
        return "user/signUp";
    }
}
