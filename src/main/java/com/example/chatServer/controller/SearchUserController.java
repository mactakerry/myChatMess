package com.example.chatServer.controller;

import com.example.chatServer.model.dto.request.UserRequest;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.UserRepository;
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
    public ResponseEntity<String> searchUser(@RequestBody UserRequest user) {
        if (userRepository.existsByUsername(user.username())) {
            return ResponseEntity.ok("Success");
        }

        return ResponseEntity.ok("Nothing");
    }


}
