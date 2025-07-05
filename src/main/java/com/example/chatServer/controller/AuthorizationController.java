package com.example.chatServer.controller;

import com.example.chatServer.model.dto.authDTO.LoginUserDTO;
import com.example.chatServer.model.dto.authDTO.RegistrationUserDTO;
import com.example.chatServer.sevice.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthorizationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/reg")
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegistrationUserDTO dto) {
        authService.register(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserDTO dto) {
        String token = authService.login(dto);

        return ResponseEntity.ok(token);
    }
}
