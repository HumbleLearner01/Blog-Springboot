package com.example.blogpost.modules.post.repository;

import com.example.blogpost.modules.post.model.Post;
import com.example.blogpost.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);
    List<Post> findAllByUser(User user);
}
