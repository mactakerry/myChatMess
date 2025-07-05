package com.example.chatServer.controller;

import com.example.chatServer.exception.ForbiddenException;
import com.example.chatServer.exception.InvalidTokenException;
import com.example.chatServer.model.dto.request.GetMessagesRequest;
import com.example.chatServer.model.entity.Message;
import com.example.chatServer.model.dto.MessageDTO;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.sevice.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final SecurityService securityService;
    private final UserService userService;
    private final JwtService jwtService;

    @MessageMapping("/chats/{chatId}/messages")
    @SendToUser("/queue/errors")
    public void handleMessage(@DestinationVariable Long chatId, MessageSendRequest request, Principal principal) {

        try {

            User sender = userService.loadUserByUsername(principal.getName());

            if (!securityService.isUserInChat(chatId, sender.getId())) {
                throw new ForbiddenException("Access denied to chat");
            }

            MessageDTO message = messageService.createMessage(chatId, sender.getId(), request.content);

            messagingTemplate.convertAndSend(
                    "/topic/chats/" + chatId + "/messages",
                    message
            );

            log.info("Message sent in chat {} by user {}", chatId, sender.getUsername());


        } catch (Exception e) {
            log.error("WebSocket error " + e.getMessage());
        }
    }

    @PostMapping("/getMessages")
    public ResponseEntity<PageResponse<MessageDTO>> getChatMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestBody GetMessagesRequest dto,
            @AuthenticationPrincipal User currentUser
    ) {
        long chatId = dto.getChatId();

        log.info("Getting messages for chat: {}, page: {}, size: {}", chatId, page, size);

        if (size > 100) {
            log.warn("User {} requested too large page size: {}", currentUser.getUsername(), size);
            return ResponseEntity.badRequest().build();
        }

        if (!securityService.isUserInChat(chatId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<Message> messages = messageService.getMessagesByChat(
                chatId,
                PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")))
        );

        Page<MessageDTO> messageDTOPage = messages.map(Message::toDTO);
        return ResponseEntity.ok(PageResponse.fromPage(messageDTOPage));
    }



    public record MessageSendRequest(String content) {}

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements) {
        public static <T> PageResponse<T> fromPage(Page<T> page) {
            return new PageResponse<>(
                    page.getContent(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements()
            );
        }
    }

}
