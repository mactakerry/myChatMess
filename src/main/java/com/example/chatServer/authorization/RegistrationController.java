package com.example.chatServer.authorization;

import com.example.chatServer.ChatServerApplication;
import com.example.chatServer.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;

@RestController
public class RegistrationController {
    ArrayList<String> usernames = com.example.chatServer.ChatServerApplication.usernames;
    ArrayList<User> users = ChatServerApplication.users;
    public static int fileLen;


    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody User user) {

        for (String username:usernames) {
            System.out.println(username + " --> " + user.getUsername());
            if (username.equals(user.getUsername())) {
                System.out.println(username + " --> " + user.getUsername() + " !!!!!!!!!!!!! ");
                return ResponseEntity.ok("This name is already taken. Please select other name");
            }
        }

        File usersFile = new File("data/users.txt");

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, true))) {
            usernames.add(user.getUsername());
            users.add(new User(user.getUsername(), user.getPassword()));
            if (fileLen > 10) {
                writer.write("\n");
            }

            writer.write(user.getUsername() + ":" + user.getPassword() + ";");

            fileLen++;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ПИЗДЕЦ: " + e.getMessage());
            return ResponseEntity.status(500).body("Fatal error: " + e.toString());
        }

        return ResponseEntity.ok("Success");
    }
}
