package com.example.blogpost.modules.post.controller;

import com.example.blogpost.helper.Message;
import com.example.blogpost.modules.post.model.Post;
import com.example.blogpost.modules.user.model.User;
import com.example.blogpost.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/user/post")
public class PostController {
    @Autowired private UserRepository userRepo;

    @GetMapping("/upload")
    public String displayUploadPost(Model model) {
        model.addAttribute("title", "Upload a post!");
        model.addAttribute("post", new Post());
        return "post/post-upload";
    }

    @PostMapping("/process-upload")
    public String uploadPost(@Valid @ModelAttribute("post") Post post, BindingResult bindingResult,
                             @RequestParam("postImage") MultipartFile multipart,
                             Model model,
                             Principal principal,
                             HttpSession session) {
        try {
            if (bindingResult.hasErrors()) {
                System.out.println("post field errors: " + bindingResult.getFieldError());
                session.setAttribute("message", new Message("please resolve the errors :(", "alert-danger"));
                model.addAttribute("post", post);
                return "post/post-upload";
            }

            if (multipart.isEmpty()) { //uploading image
                System.out.println("image empty");
                post.setImageCover("no-image.png");
            } else {
                post.setImageCover(multipart.getOriginalFilename());
                File file = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(file.getAbsolutePath() + File.separator + UUID.randomUUID() + multipart.getOriginalFilename());
                Files.copy(multipart.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            //mapping user and post
            User user = userRepo.findByEmail(principal.getName());
            post.setUser(user);
            user.getPost().add(post);
            //end of mapping user and post
            userRepo.save(user);
            session.setAttribute("message", new Message("Successfully posted on the internet ;)", "alert-success"));
            model.addAttribute("post", new Post());
            return "post/post-upload";
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong, please try again.", "alert-info"));
            model.addAttribute("post", post);
            return "post/post-upload";
        }
    }
}