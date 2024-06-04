// package com.example.demo;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import java.util.ArrayList;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String user_name) throws UsernameNotFoundException {
//         User_info user = userRepository.findByUserId(user_name)
//             .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + user_name));

//         return new org.springframework.security.core.userdetails.User(user.getUser_id(), user.getUser_pw(), new ArrayList<>());
//     }
// }
