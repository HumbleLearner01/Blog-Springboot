package com.example.blogpost.modules.user.controller;

import com.example.blogpost.helper.Message;
import com.example.blogpost.modules.user.model.User;
import com.example.blogpost.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/user/home")
    public String home(Model model) {
        return "user/user-home";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute User user, BindingResult bindingResult,
                               @RequestParam(value = "agreements", defaultValue = "false") boolean agreements,
                               HttpSession session,
                               Model model) {
        try {
            if (!agreements) {
                System.out.println("agreement is not checked");
                model.addAttribute("title", "Agreements!");
                throw new Exception("agreement not checked"); //checking for agreements
            }

            if (bindingResult.hasErrors()) { //validating input errors
                model.addAttribute("user", user);
                model.addAttribute("title", "＞﹏＜");
                System.out.println("error?: " + bindingResult.getFieldError());
                return "user/signup";
            } else {
                userRepo.save(user);
                session.setAttribute("message", new Message("Successfully Registered!", "alert-success"));
                model.addAttribute("user", new User());
                return "redirect:/user";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("please agree to our terms & conditions!", "alert-warning"));
            return "user/signup";
        }
    }
}
