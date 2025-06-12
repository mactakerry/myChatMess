package com.example.chatServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;

@SpringBootApplication
public class ChatServerApplication {
	public static ArrayList<String> usernames = new ArrayList<>();
	public static ArrayList<User> users = new ArrayList<>();

	public static void main(String[] args) {
		File usersFile = new File("data/users.txt");

		try(BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
			String fileContent = reader.lines().reduce("", (acc, line) -> acc + line);
			String[] userRecords = fileContent.split(";");

			for (String record:userRecords) {
				String[] credentials = record.split(":");
				if (credentials.length == 2) {
					String username = credentials[0];
					String password = credentials[1];

					usernames.add(username);
					users.add(new User(username, password));

					System.out.print(username + " --> ");
					System.out.println(password);
				}
			}


		} catch (IOException e) {

			throw new RuntimeException(e);
		}

        SpringApplication.run(ChatServerApplication.class, args);
	}

}
