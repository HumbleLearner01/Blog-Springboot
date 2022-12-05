package com.example.blogpost.modules.user.model;

import com.example.blogpost.modules.post.model.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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


    @NotBlank(message = "This field is required!") @Size(min = 2, max = 20, message = "Name must be between 2 & 20 characters")
    private String name;
    @Email @Column(nullable = false, unique = true) @NotBlank(message = "This field is required!")
    private String email;
    @NotBlank(message = "This field is required!")
    private String password;
    private String imageCover;
    private String role;
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false) @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at") @UpdateTimestamp
    private LocalDateTime updatedAt;
}
