package com.example.chatServer.Message;

import com.example.chatServer.chat.Chat;
import com.example.chatServer.chat.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatRepository chatRepository;

    @MessageMapping("/send")
    public void handleMessage(MessageDTO messageDto) {
        System.out.println("Received via WebSocket: " + messageDto);

        Optional<Chat> chat = chatRepository.findById(messageDto.getChatId());
        if (chat.isEmpty()) return;

        Message message = new Message(
                messageDto.getContent(),
                chat.get(),
                messageDto.getSender()
        );

        messageService.sendMessage(message);

        messagingTemplate.convertAndSend(
                "/topic/chat" + messageDto.getChatId(),
                messageDto
        );
    }

    @PostMapping("/getAllMessByChatId")
    public ResponseEntity<?> getAllMessageByChatId(@RequestBody long chatId) {
        List<MessageDTO> messages = messageService.getAllMessByChatId(chatId);
        return ResponseEntity.ok(messages);
    }
}
