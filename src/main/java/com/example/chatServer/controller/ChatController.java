package com.example.chatServer.controller;

import com.example.chatServer.model.dto.request.UserRequest;
import com.example.chatServer.model.entity.Chat;
import com.example.chatServer.model.dto.ChatDTO;
import com.example.chatServer.model.dto.request.CreateGroupRequest;
import com.example.chatServer.repository.ChatRepository;
import com.example.chatServer.sevice.ChatService;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.UserRepository;
import com.example.chatServer.sevice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;


@Slf4j
@RestController
public class ChatController {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;



    @PostMapping("/grChat")
    public ResponseEntity<Long> grChat(@RequestBody UserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user1 = userService.loadUserByUsername(currentUsername);
        User user2 = userService.loadUserByUsername(request.username());

        if (user1 == null || user2 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        chatService.createPrivateChat(user1, user2);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllUserChats")
    public ResponseEntity<?> getAllUserChats() {
        log.info("GetAllUser begin");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("getAllUser: " + authentication.getName());


        User user = userService.loadUserByUsername(authentication.getName());

        if (user == null) {
            return ResponseEntity.ofNullable("unknown user");
        }

        Set<ChatDTO> chats1 = chatRepository.findChatsByParticipants(user);


        if (chats1 != null) {
            chats1.forEach(chatDTO -> {
                log.info("{} запросил чат {}", user.getUsername(), chatDTO.getName());
            });
        }


        return ResponseEntity.ok(chats1);
    }

    @PostMapping("/createGroup")
    public ResponseEntity<Long> createGroup(@RequestBody CreateGroupRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User creator = userService.loadUserByUsername(authentication.getName());

        if (request.getParticipants().size() < 2) {
            return ResponseEntity.badRequest().body(-1L);
        }

        Set<User> participants = new HashSet<>();
        participants.add(creator);
        for (String username : request.getParticipants()) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1L);
            }
            participants.add(user);
        }

        Chat groupChat = new Chat(request.getName(), participants, creator);

        chatRepository.save(groupChat);
        return ResponseEntity.ok(groupChat.getId());
    }

}
