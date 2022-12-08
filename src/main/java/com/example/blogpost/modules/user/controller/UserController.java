package com.example.blogpost.modules.user.controller;

import com.example.blogpost.helper.Message;
import com.example.blogpost.modules.user.model.User;
import com.example.blogpost.modules.user.repository.UserRepository;
import com.example.blogpost.modules.user.service.EmailService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class UserController {
    @Autowired private UserRepository userRepo;
    @Autowired private BCryptPasswordEncoder bcrypt;
    @Autowired private EmailService emailService;

    @GetMapping("/user/home")
    public String home2(Model model, Principal principal) {
        model.addAttribute("title", "Welcome " + principal.getName());
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
                user.setRole("ROLE_USER");
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                user.setEnabled(true);
                user.setImageCover("default-male.png");
                user.setPassword(bcrypt.encode(user.getPassword()));

                userRepo.save(user);
                session.setAttribute("message", new Message("Successfully Registered!", "alert-success"));
                model.addAttribute("user", new User());
                return "redirect:/user/home";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("please agree to our terms & conditions!", "alert-warning"));
            return "user/signup";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password (´･ω･`)?");
        return "user/forgot-password/forgot-password";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, Model model, HttpSession session) {
        model.addAttribute("OTP sent to your email");
        String otp = UUID.randomUUID() + RandomString.make(5);
        String subject = "OTP : Verify Your Account";
        String message = "<div style='padding: 25px; border:3px solid black; width:18%; text-align: center'>" +
                         "      <h3>Your OTP Request!</h3>" +
                         "      <hr>" +
                         "      <h2>=>     "+ otp +"     <=</h2>" +
                         "      Please copy this otp text!" +
                         "</div>";
        boolean sendEmail = emailService.sendEmail(message, subject, email);
        if (sendEmail) {
            session.setAttribute("message", new Message("We have sent you an email containing the OTP, Please proceed to your email account.", "alert-secondary"));
            session.setAttribute("otp", otp);
            session.setAttribute("email", email);
            return "user/forgot-password/verify-otp";
        } else {
            session.setAttribute("message", "We do not have any user with \"" + email + "\" email!");
            return "user/forgot-password/forgot-password";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, HttpSession session, Model model) {
        String sendOTP = (String) session.getAttribute("otp");
        String email = (String) session.getAttribute("email");
        if (sendOTP.equals(otp)) {
            User user = userRepo.findByEmail(email);
            //if the user exists in the DB
            if (user != null) {
                model.addAttribute("title", "Change your password");
                return "user/forgot-password/change-password";
            } else {
                session.setAttribute("message", "Unfortunately, the user does not exist in our database.");
                return "user/forgot-password/forgot-password";
            }
        } else {
            session.setAttribute("message", new Message("OTP does not match :(", "alert-danger"));
            return "user/forgot-password/verify-otp";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = userRepo.findByEmail(email);
        user.setPassword(bcrypt.encode(newPassword));
        userRepo.save(user);
        return "redirect:/login?change=Password changed successfully";
    }
}