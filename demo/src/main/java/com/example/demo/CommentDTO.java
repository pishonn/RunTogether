package com.example.demo;

import java.time.LocalDateTime;

public class CommentDTO {

    private String content;
    private String writer;
    private LocalDateTime date;

    public CommentDTO() {
    }

    public CommentDTO(String content, String writer, LocalDateTime date) {
        this.content = content;
        this.writer = writer;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
    
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Comment toEntity(Post post, User_info user) {
        return new Comment(post, this.content, user, this.date != null ? this.date : LocalDateTime.now());
    }
}
