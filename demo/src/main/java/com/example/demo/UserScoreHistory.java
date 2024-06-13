package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// import org.hibernate.annotations.NotFound;
// import org.hibernate.annotations.NotFoundAction;
// import org.hibernate.annotations.OnDelete;
// import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_score_history")
public class UserScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotFound(action = NotFoundAction.IGNORE)
    //@OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.EAGER)
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

    @Transient
    private String duration; // 경주 시간을 저장할 임시 필드

    // Getters and Setters
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

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
                ", user=" + user +
                ", crew=" + crew +
                ", raceEndTime=" + raceEndTime +
                ", points=" + points +
                ", rank=" + rank +
                '}';
    }
}
