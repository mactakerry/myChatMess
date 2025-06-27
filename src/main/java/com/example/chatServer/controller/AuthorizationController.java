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
    public ResponseEntity<String> createUser(@Valid @RequestBody RegistrationUserDTO dto) {
        authService.register(dto);

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginUserDTO dto) {
        String token = authService.login(dto);

        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<String> validateToken (@RequestBody TokenDTO dto) {
        if (!tokenService.validateToken(dto.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login again");
        }

        return ResponseEntity.ok("Success");
    }
}
