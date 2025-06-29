package com.example.chatServer.controller;

import com.example.chatServer.model.dto.TokenDTO;
import com.example.chatServer.model.entity.Chat;
import com.example.chatServer.model.dto.ChatDTO;
import com.example.chatServer.model.dto.request.CreateGroupRequest;
import com.example.chatServer.repository.ChatRepository;
import com.example.chatServer.model.entity.Token;
import com.example.chatServer.sevice.TokenService;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.UserRepository;
import com.example.chatServer.sevice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private TokenService tokenService;



    @PostMapping("/grChat")
    public ResponseEntity<Long> grChat(@RequestBody String[] values) {
        Token token = tokenService.findByValue(values[0]);

        User user1 = userService.getUserById(token.getUserId());
        User user2 = userService.findByUserName(values[1]).get();

        Chat chat = chatRepository.findByName(user1.getUsername() + "-" + user2.getUsername());

        if (chat == null) {
            chat = chatRepository.findByName(user2.getUsername() + "-" + user1.getUsername());
            if (chat == null) {
                Chat newChat = new Chat(user1, user2, user1);
                chatRepository.save(newChat);
                System.out.println("Созадали новый чат --- " + newChat.getName() + " == " + newChat.getId());
                return ResponseEntity.ok(newChat.getId());
            }

        }

        System.out.println(chat.getId() + " ЧАТ НАЙДЕН ");
        return ResponseEntity.ok(chat.getId());
    }

    @PostMapping("/getAllUserChats")
    public ResponseEntity<?> getAllUserChats(@RequestBody TokenDTO dto) {
        String value = dto.getValue();
        Token token = tokenService.findByValue(value);
        if (!tokenService.validateToken(value)) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.getUserById(token.getUserId());

        if (user == null) {
            return ResponseEntity.ofNullable("Кто ты бля ");
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
        Token token = tokenService.findByValue(request.getCreatorName());
        User creator = userService.getUserById(token.getUserId());

        System.out.println("DAWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");

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
