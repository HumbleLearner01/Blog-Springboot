package com.example.blogpost.modules.post.controller;

import com.example.blogpost.helper.Message;
import com.example.blogpost.modules.post.model.Post;
import com.example.blogpost.modules.post.repository.PostRepository;
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
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user/post")
public class PostController {
    @Autowired private UserRepository userRepo;
    @Autowired private PostRepository postRepo;
    private final UUID randomUUID = UUID.randomUUID();

    //this method is for getting the data of the user, in any route that it had fired --> /** used in showPostsOfUser() **/
    @ModelAttribute public void getUserData(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        model.addAttribute("user", user);
    }
    //this method is for getting the posts from the logged-in user, in any route that it had fired --> /** used in showPostsOfUser() **/
    @ModelAttribute public void getPostData(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        List<Post> post = postRepo.findAllByUser(user);
        model.addAttribute("post", post);
    }

    @GetMapping("/upload")
    public String displayUploadPost(Model model) {
        model.addAttribute("title", "Upload a post!");
        model.addAttribute("post", new Post());
        return "post/post-upload";
    }

    @PostMapping("/process-upload")
    public String uploadPost(@Valid @ModelAttribute Post post, BindingResult bindingResult,
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
                post.setImageCover(randomUUID + multipart.getOriginalFilename());
                File file = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(file.getAbsolutePath() + File.separator + randomUUID + multipart.getOriginalFilename());
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

    @GetMapping("/show")
    public String showPostsOfUser(Model model) {
        model.addAttribute("title", "Your posts");
        return "post/post-show";
    }

    @PostMapping("/edit/{postId}")
    public String updateContact(@PathVariable("postId") Long postId, Model model) {
        model.addAttribute("title", "Edit your post");
        Post post = postRepo.findById(postId).get();
        model.addAttribute("pst", post);
        return "post/post-edit";
    }

    /*update a contact - 1.2*/
    @PostMapping("/edit/process-edit")
    public String doUpdateContact(@ModelAttribute Post post,
                                  @RequestParam("postImage") MultipartFile file,
                                  Principal principal,
                                  HttpSession session) {
        try {
            Post oldPost = postRepo.findById(post.getId()).get();
            if (!file.isEmpty()) {
                //delete old image
                File deleteFilePath = new ClassPathResource("static/img").getFile();
                File fileAppend = new File(deleteFilePath, oldPost.getImageCover());
                boolean delete = fileAppend.delete();
                if (delete) System.out.println("old image deleted");

                //update new image
                post.setImageCover(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + randomUUID + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } else
                post.setImageCover(oldPost.getImageCover());
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        User user = userRepo.findByEmail(principal.getName());
//        post.setUser(user);
        postRepo.save(post);
        session.setAttribute("message", new Message("Post has been edited!", "alert-success"));
        return "redirect:/user/post/show";
    }
}