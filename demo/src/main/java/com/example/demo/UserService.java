package com.example.demo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;




@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CrewRepository crewRepository;

    // public boolean login(dtoLogin dto, HttpSession session) {
    //     String userId = dto.getUserId();
    //     String userPw = dto.getPw();
    //     System.out.println("userId: " + userId);
    //     System.out.println("userPw: " + userPw);
    //     Optional<User_info> user = userRepository.findByUserIdAndPw(userId, userPw);
        
    //     System.out.println("user: " + user);
    //     session.removeAttribute("user");
    //     if (user.isPresent()) {
    //         session.setAttribute("user", user.get());
    //         return true;
    //     } else {
    //         return false;
    //     }
    // }

    // public void logout(HttpSession session) {
    //     session.invalidate();
    // }

    // public boolean register(User_info user) {
    //     userRepository.save(user);
    //     return true;
    // }
    
    @Transactional
    public boolean deleteUser(User_info user) {
        if (userRepository.existsById(user.getId())) {
            
            // 크루와의 관계 해제
            if (user.getCrew() != null) {
                Crew crew = user.getCrew();
                if (crew.getAdmin() != null && crew.getAdmin().getId().equals(user.getId())) {
                    for (User_info member : crew.getMembers()) {
                        member.setCrew(null); // 각 유저의 크루 관계 해제
                        userRepository.save(member);
                    }
                    crew.setAdmin(null);
                    crewRepository.deleteById(crew.getId());
                } else {
                    user.setCrew(null);
                }
            }

            // Room과의 관계 해제
            if (user.getRoom() != null) {
                user.setRoom(null);
            }

            userRepository.delete(user); 
            return true;
        }
        return false;
    }

    
    public boolean updateUserInfo(User_info newData, User_info existingUser){
        if (existingUser != null) {
            existingUser.setName(newData.getName());
            existingUser.setPw(newData.getPw());
            existingUser.setEmail(newData.getEmail());
    
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }

    public ScoreResult updateScore(User_info userData) {

        if (userData == null) {
            return new ScoreResult(0, 0.0);
        }

        List<ScoreHistory> scoreHistoryList = userData.getScoreHistory();
        int totalPoints = scoreHistoryList.stream().mapToInt(ScoreHistory::getPoints).sum();
        double totalDistance = scoreHistoryList.stream().mapToDouble(ScoreHistory::getDistance).sum();

        userData.setTotalPoints(totalPoints);
        userData.setTotalDistance(totalDistance);

        userRepository.saveAndFlush(userData);

        return new ScoreResult(totalPoints, totalDistance);
    }

    public List<User_info> calculateOverallRanking() {
        return userRepository.findOverallRankedUsers();
    }

    public List<User_info> calculatePointsRanking() {
        return userRepository.findPointsRankedUsers();
    }

    public List<User_info> calculateDistanceRanking() {
        return userRepository.findDistanceRankedUsers();
    }

    public int findUserRanking(Long id) {
        List<User_info> rankedUsers = userRepository.findOverallRankedUsers();

        for (int i = 0; i < rankedUsers.size(); i++) {
            if (rankedUsers.get(i).getId().equals(id)) {
                return i + 1; // 순위는 인덱스 + 1
            }
        }

        return -1; // 유저를 찾지 못한 경우
    }

}   