package com.example.blogpost.modules.user.model;

import com.example.blogpost.modules.post.model.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {
    @Id @GeneratedValue private Long id;

    /*relation*/
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
    private List<Post> post = new ArrayList<>();

    private String name;
    private String email;
    private String password;
    private String imageCover;
    private String role;
    private boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
