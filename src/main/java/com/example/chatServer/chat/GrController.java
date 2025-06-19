package com.example.chatServer.chat;

import com.example.chatServer.user.User;
import com.example.chatServer.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
public class GrController {
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/grChat")
    public ResponseEntity<Long> grChat(@RequestBody String[] names) {
        System.out.println(Arrays.toString(names) + " !!!!!!!!!");
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
}
