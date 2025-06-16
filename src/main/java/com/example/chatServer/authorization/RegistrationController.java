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
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/reg")
    public ResponseEntity<String> creteUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.ok("Please select another username");
        }
        userRepository.save(user);

        return ResponseEntity.ok("Success");
    }
}
