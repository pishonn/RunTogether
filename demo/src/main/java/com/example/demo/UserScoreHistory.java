package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "user_score_history", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"room_score_history_id", "user_id"})
})
public class UserScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "room_score_history_id", nullable = false)
    private RoomScoreHistory roomScoreHistory;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User_info user;

    @ManyToOne
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    private LocalDateTime raceEndTime;

    private int points;

    private int rank;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomScoreHistory getRoomScoreHistory() {
        return roomScoreHistory;
    }

    public void setRoomScoreHistory(RoomScoreHistory roomScoreHistory) {
        this.roomScoreHistory = roomScoreHistory;
    }

    public User_info getUser() {
        return user;
    }

    public void setUser(User_info user) {
        this.user = user;
    }

    public LocalDateTime getRaceEndTime() {
        return raceEndTime;
    }

    public void setRaceEndTime(LocalDateTime raceEndTime) {
        this.raceEndTime = raceEndTime;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    @Override
    public String toString() {
        return "UserScoreHistory{" +
                "id=" + id +
                ", roomScoreHistory=" + roomScoreHistory +
                ", user=" + user +
                ", crew=" + crew +
                ", raceEndTime=" + raceEndTime +
                ", points=" + points +
                ", rank=" + rank +
                '}';
    }

}
