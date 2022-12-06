package com.example.blogpost.modules.post.model;

import com.example.blogpost.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Post {
    @Id @GeneratedValue private Long id;

    /*relation*/
    @JsonIgnore @ManyToOne
    private User user;

    @NotBlank(message = "This field is required!") @Size(min = 2, message = "Title must be at least 3 characters")
    private String title;
    @NotBlank(message = "This field is required!")
    private String category;
    @NotBlank(message = "This field is required!")
    private String body;
    private String imageCover;

    @Column(name = "created_at", updatable = false) @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at") @UpdateTimestamp
    private LocalDateTime updatedAt;
}
