package com.example.chatServer.authorization;

import com.example.chatServer.ChatServerApplication;
import com.example.chatServer.user.User;
import com.example.chatServer.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            if (user.getPassword().equals(userRepository.findByUsername(user.getUsername()).get().getPassword())) {
                return ResponseEntity.ok("Success");
            }
        }

        return ResponseEntity.ok("Wrong password or wrong username");
    }
}
