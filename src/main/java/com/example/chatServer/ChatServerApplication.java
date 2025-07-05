package com.example.chatServer;

import jakarta.servlet.ServletContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class,
		org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration.class,
		org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration.class
})
public class ChatServerApplication {

	public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
	}


}
