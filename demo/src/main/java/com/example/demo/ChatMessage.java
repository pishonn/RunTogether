package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String sender;
    private String profileImage;
    private Long senderId; // 추가된 필드

    @ManyToOne
    private Crew crew;

    private LocalDateTime timestamp;

    @ManyToMany
    @JoinTable(
        name = "message_read",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User_info> readBy = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Set<User_info> getReadBy() {
        return readBy;
    }

    public void setReadBy(Set<User_info> readBy) {
        this.readBy = readBy;
    }

    public void addReadBy(User_info user) {
        readBy.add(user);
    }

    public void removeReadBy(User_info user) {
        readBy.remove(user);
    }
    
}
