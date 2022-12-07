package com.example.blogpost;

import com.example.blogpost.modules.post.model.Post;
import com.example.blogpost.modules.post.repository.PostRepository;
import com.example.blogpost.modules.user.model.User;
import com.example.blogpost.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class MainController {
    @Autowired private PostRepository postRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("")
    public String home(Model model, Principal principal) {
        model.addAttribute("title", "Home");
        List<Post> posts = postRepo.findAll();
        model.addAttribute("posts", posts);
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        return "user/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "Signup!");
        return "user/signup";
    }

    @GetMapping("/post/show-user-profile/{postId}")
    public String showUserProfile(@PathVariable("postId") Long postId, Model model) {
        model.addAttribute("title", "");
        User user = userRepo.findUserByPostId(postId);
        model.addAttribute("user", user);
        model.addAttribute("posts", postRepo.findAllByUserId(user.getId()));
        return "user/show-user-profile";
    }
}