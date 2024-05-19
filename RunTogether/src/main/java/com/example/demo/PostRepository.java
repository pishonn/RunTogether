package com.example.demo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByType(String type, Pageable pageable);

    Page<Post> findByTypeAndTitleContainingOrTypeAndContentContaining(
        String type1, String title, String type2, String content, Pageable pageable);
}
