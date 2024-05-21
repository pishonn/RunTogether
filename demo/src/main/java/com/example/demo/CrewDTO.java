package com.example.demo;

public class CrewDTO {
    private String name;
    private String region;
    private int capacity;
    private int profileImage;
    private Long adminId;

    public CrewDTO() {
    }

    public CrewDTO(String name, String region, int capacity, int profileImage) {
        this.name = name;
        this.region = region;
        this.capacity = capacity;
        this.profileImage = profileImage;
    }

    // Getter와 Setter
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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Crew toEntity(User_info adminUser) {
        Crew crew = new Crew();
        crew.setName(this.name);
        crew.setRegion(this.region);
        crew.setCapacity(this.capacity);
        crew.setProfileImage(this.profileImage);
        crew.setAdmin(adminUser); 
        return crew;
    }
}
