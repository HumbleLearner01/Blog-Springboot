package com.example.blogpost.modules.post.repository;

import com.example.blogpost.modules.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Post findPostByTitle(String title);
}
