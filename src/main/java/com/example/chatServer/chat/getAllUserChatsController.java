package com.example.chatServer.chat;

import com.example.chatServer.user.User;
import com.example.chatServer.user.UserDTO;
import com.example.chatServer.user.UserRepository;
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
    private UserRepository userRepository;

    @PostMapping("/getAllUserChats")
    public ResponseEntity<?> getAllUserChats(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO.getUsername() + " ALALALALALALALALAL");

        if (userDTO.getUsername() == null) {
            return ResponseEntity.ofNullable("Призраки у нас не хранятся ");
        }
        User user = userRepository.findByUsername(userDTO.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.ofNullable("Кто это бля " + userDTO.getUsername());
        }
        List<ChatDTO> chats1 = chatRepository.findAllByUserId1(user.getId());
        List<ChatDTO> chats2 = chatRepository.findAllByUserId2(user.getId());

        chats1.addAll(chats2);

        for (ChatDTO chatDTO:chats1) {
            if (chatDTO.isGroupChat()) {
                continue;
            }

            User chatUser1 = userRepository.findById(chatDTO.getUserId1()).get();
            if ((userDTO.getUsername().equals(chatUser1.getUsername()))) {
                chatDTO.setName(userRepository.findById(chatDTO.getUserId2()).get().getUsername());
                System.out.println(chatUser1.getUsername() + ".  DTO: " + userDTO.getUsername());

                continue;
            }

            chatDTO.setName(chatUser1.getUsername());
        }

        return ResponseEntity.ok(chats1);
    }
}
