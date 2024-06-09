package com.example.demo;

public class UserStatusUpdate {
    private Long userId;
    private boolean ready;

    // 기본 생성자
    public UserStatusUpdate() {}

    // 매개변수 생성자
    public UserStatusUpdate(Long userId, boolean ready) {
        this.userId = userId;
        this.ready = ready;
    }

    // getter 및 setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
