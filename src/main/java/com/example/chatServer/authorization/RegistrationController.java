package com.example.chatServer.authorization;

import com.example.chatServer.ChatServerApplication;
import com.example.chatServer.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class RegistrationController {
    ArrayList<String> usernames = com.example.chatServer.ChatServerApplication.usernames;
    ArrayList<User> users = ChatServerApplication.users;
    public static int fileLen;


    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody User user) {

        return ResponseEntity.ok("Success");
    }
}
