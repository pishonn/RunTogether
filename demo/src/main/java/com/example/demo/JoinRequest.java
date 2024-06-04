package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class JoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User_info user;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private LocalDateTime date;

    public JoinRequest() {
    }

    public JoinRequest(User_info user, Crew crew, LocalDateTime date) {
        this.user = user;
        this.crew = crew;
        this.date = date;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User_info getUser() {
        return user;
    }

    public void setUser(User_info user) {
        this.user = user;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
