package com.example.blogpost;

import com.example.blogpost.modules.user.model.User;
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

    @GetMapping("/user")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        return "user/logIn";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "Signup!");
        return "user/signUp";
    }
}