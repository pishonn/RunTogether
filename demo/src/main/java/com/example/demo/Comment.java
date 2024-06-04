package com.example.demo;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    private String content;
    

    @ManyToOne
    @JoinColumn(name = "userId")
    private User_info user;

    private String writer;
    private LocalDateTime date;
    
    public Comment() {
    }

    public Comment(Post post, String content, User_info user, LocalDateTime date) {
        this.post = post;
        this.content = content;
        this.user = user;
        this.writer = user != null ? user.getName() : null;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User_info getUser() {
        return user;
    }

    public void setUser(User_info user) {
        this.user = user;
        this.writer = user != null ? user.getName() : null;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public LocalDateTime getDate() {
        return date;
    }

    public CommentDTO toDto() {
        return new CommentDTO(this.content, this.writer, this.date);
    }

    // public void update(CommentDTO commentDto) {
    //     this.content = commentDto.getContent();
    //     this.writer = commentDto.getWriter();
    // }
}
