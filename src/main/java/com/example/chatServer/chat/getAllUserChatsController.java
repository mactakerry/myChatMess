package com.example.chatServer.chat;

import com.example.chatServer.token.Token;
import com.example.chatServer.token.TokenService;
import com.example.chatServer.user.User;
import com.example.chatServer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class getAllUserChatsController {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

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
