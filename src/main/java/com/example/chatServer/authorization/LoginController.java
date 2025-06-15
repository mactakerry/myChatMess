package com.example.chatServer.authorization;

import com.example.chatServer.ChatServerApplication;
import com.example.chatServer.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class LoginController {

    ArrayList<User> users =  ChatServerApplication.users;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        for (User u:users) {
            if (u.getUsername().equals(user.getUsername())) {
                if (u.getPassword().equals(user.getPassword())) {
                    return ResponseEntity.ok("Success");
                } else {
                    return ResponseEntity.ok("Wrong password");
                }
            }
        }

        return ResponseEntity.ok(user.getUsername() + " not found");
    }
}
