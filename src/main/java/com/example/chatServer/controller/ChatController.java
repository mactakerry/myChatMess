package com.example.chatServer.controller;

import com.example.chatServer.model.chat.Chat;
import com.example.chatServer.model.dto.ChatDTO;
import com.example.chatServer.model.dto.request.CreateGroupRequest;
import com.example.chatServer.repository.ChatRepository;
import com.example.chatServer.model.entity.Token;
import com.example.chatServer.sevice.TokenService;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.UserRepository;
import com.example.chatServer.sevice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Token token = tokenService.findByName(names[0]);

        User user1 = userService.getUserById(token.getUserId());
        User user2 = userService.findByUserName(names[1]).get();

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
        List<ChatDTO> groupChats = chatRepository.findGroupChatsByParticipants(user);

        chats1.addAll(chats2);
        chats1.addAll(groupChats);

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

    @PostMapping("/createGroup")
    public ResponseEntity<Long> createGroup(@RequestBody CreateGroupRequest request) {
        Token token = tokenService.findByName(request.getCreatorName());
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

        Chat groupChat = new Chat(request.getName(), participants, creator.getId());

        chatRepository.save(groupChat);
        return ResponseEntity.ok(groupChat.getId());
    }

}
