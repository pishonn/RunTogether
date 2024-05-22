package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Crew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String region;
    private int capacity;
    private int profileImage;

    @OneToOne
    @JoinColumn(name = "admin_id")
    private User_info admin;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User_info> members = new ArrayList<>();

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JoinRequest> joinRequests = new ArrayList<>();

    // getter 및 setter 추가

    public Crew() {
    }

    public Crew(String name, String region, int capacity, int profileImage) {
        this.name = name;
        this.region = region;
        this.capacity = capacity;
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public User_info getAdmin() {
        return admin;
    }

    public void setAdmin(User_info admin) {
        this.admin = admin;
    }

    public List<User_info> getMembers() {
        return members;
    }

    public void setMembers(List<User_info> members) {
        this.members = members;
    }

    public void addMember(User_info member) {
        this.members.add(member);
    }

    public void removeMember(User_info member) {
        this.members.remove(member);
    }

    public void clearMembers() {
        this.members.clear();
    }

    public User_info getMember(Long id) {
        for (User_info member : members) {
            if (member.getId() == id) {
                return member;
            }
        }
        return null;
    }

    public int getMemberCount() {
        return members.size();
    }

    public boolean isFull() {
        return members.size() >= capacity;
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public boolean isMember(User_info member) {
        return members.contains(member);
    }

    public int getTotalPoints() {
        int totalPoints = 0;
        for (User_info member : members) {
            totalPoints += member.getTotalPoints();
        }
        return totalPoints;
    }

    public double getTotalDistance() {
        double totalDistance = 0;
        for (User_info member : members) {
            totalDistance += member.getTotalDistance();
        }
        return totalDistance;
    }

    

    public List<JoinRequest> getJoinRequests() {
        return joinRequests;
    }

    public void setJoinRequests(List<JoinRequest> joinRequests) {
        this.joinRequests = joinRequests;
    }

    public void addJoinRequest(JoinRequest joinRequest) {
        this.joinRequests.add(joinRequest);
    }

    public void removeJoinRequest(JoinRequest joinRequest) {
        this.joinRequests.remove(joinRequest);
    }

    @Override
    public String toString() {
        return "Crew{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region=" + region +
                ", capacity=" + capacity +
                ", profileImage=" + profileImage +
                '}';
    }
}
