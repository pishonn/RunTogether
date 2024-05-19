package com.example.demo;

import java.time.LocalDateTime;

public class PostDto {
    private String title;
    private String content;
    private LocalDateTime date;
    private String writer;
    private int profileImg;
    private String type;

    public PostDto() {
        this.date = LocalDateTime.now();
    }

    public PostDto(String title, String content, LocalDateTime date, String writer, int profileImg, String type) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.writer = writer;
        this.profileImg = profileImg;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public int getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(int profileImg) {
        this.profileImg = profileImg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Post toEntity() {
        return new Post(this.title, this.content, this.date, this.writer, this.profileImg, this.type);
    }
}
