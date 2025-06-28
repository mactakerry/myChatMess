package com.example.chatServer.controller;

import com.example.chatServer.model.dto.TokenDTO;
import com.example.chatServer.model.dto.UserDTO;
import com.example.chatServer.model.dto.authDTO.LoginUserDTO;
import com.example.chatServer.model.dto.authDTO.RegistrationUserDTO;
import com.example.chatServer.sevice.AuthService;
import com.example.chatServer.sevice.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthorizationController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;

    @PostMapping("/reg")
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegistrationUserDTO dto) {
        authService.register(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginUserDTO dto) {
        String token = authService.login(dto);

        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Void> validateToken (@RequestBody TokenDTO dto) {
        if (!tokenService.validateToken(dto.getValue())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }
}
