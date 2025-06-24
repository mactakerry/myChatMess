package com.example.chatServer.controller;

import com.example.chatServer.model.entity.Chat;
import com.example.chatServer.model.dto.ChatDTO;
import com.example.chatServer.repository.ChatRepository;
import com.example.chatServer.model.entity.Token;
import com.example.chatServer.sevice.TokenService;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.UserRepository;
import com.example.chatServer.sevice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<Long> grChat(@RequestBody String[] names) {
        User user1 = userRepository.findByUsername(names[0]).get();
        User user2 = userRepository.findByUsername(names[1]).get();

        Chat chat = chatRepository.findByUserId1AndUserId2(user1.getId(), user2.getId());
        if (chat == null) {
            chat = chatRepository.findByUserId1AndUserId2(user2.getId(), user1.getId());
        }

        if (chat == null) {
            Chat newChat = new Chat(user1, user2);
            chatRepository.save(newChat);
            System.out.println("Созадали новый чат --- " + newChat.getName() + " == " + newChat.getId());
            return ResponseEntity.ok(newChat.getId());
        }

        System.out.println(chat.getId() + " ЧАТ НАЙДЕН ");
        return ResponseEntity.ok(chat.getId());
    }

    @PostMapping("/getAllUserChats")
    public ResponseEntity<?> getAllUserChats(@RequestBody String tokenName) {
        Token token = tokenService.findByName(tokenName);
        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.getUserById(token.getUserId());

        if (user == null) {
            return ResponseEntity.ofNullable("Кто ты бля ");
        }
        List<ChatDTO> chats1 = chatRepository.findAllByUserId1(user.getId());
        List<ChatDTO> chats2 = chatRepository.findAllByUserId2(user.getId());

        chats1.addAll(chats2);

        for (ChatDTO chatDTO:chats1) {
            if (chatDTO.isGroupChat()) {
                continue;
            }

            User chatUser1 = userService.getUserById(chatDTO.getUserId1());
            if ((user.getUsername().equals(chatUser1.getUsername()))) {
                chatDTO.setName(userService.getUserById(chatDTO.getUserId2()).getUsername());

                continue;
            }

            chatDTO.setName(chatUser1.getUsername());
        }

        return ResponseEntity.ok(chats1);
    }


}
