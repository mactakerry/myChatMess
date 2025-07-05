package com.example.chatServer.config.interceptors;

import com.example.chatServer.model.entity.User;
import com.example.chatServer.sevice.JwtService;
import com.example.chatServer.sevice.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                String username = jwtService.extractUsername(jwt);

                if (username != null) {
                    User user = userService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    accessor.setUser(auth);
                }
            }
        }
        return message;
    }
}