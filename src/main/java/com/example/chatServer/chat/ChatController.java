package com.example.chatServer.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Autowired
    ChatRepository chatRepository;

    @PostMapping("/grChat")
    public ResponseEntity<Long> grChat(@RequestBody String name) {
        System.out.println(name + "!!!!!!!!!");
        Chat chat = chatRepository.findByName(name);
        if (chatRepository.findByName(name) == null) {
            Chat newChat = new Chat(name);
            chatRepository.save(newChat);
            System.out.println();
            return ResponseEntity.ok(newChat.getId());
        }

        System.out.println(chat.getId() + " DAWWWAWDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        return ResponseEntity.ok(chat.getId());
    }
}
