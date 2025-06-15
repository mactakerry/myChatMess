package com.example.chatServer;

import com.example.chatServer.user.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
public class ChatServerApplication {
	public static ArrayList<String> usernames = new ArrayList<>();
	public static ArrayList<User> users = new ArrayList<>();

	public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
	}

}
