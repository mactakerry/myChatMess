package com.example.chatServer.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchUserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/searchUser")
    public ResponseEntity<String> searchUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.ok("Success");
        }

        return ResponseEntity.ok("Nothing");
    }
}
