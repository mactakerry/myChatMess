package com.example.chatServer.controller;

import com.example.chatServer.model.entity.Token;
import com.example.chatServer.repository.TokenRepository;
import com.example.chatServer.sevice.TokenService;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.model.dto.UserDTO;
import com.example.chatServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthorizationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private TokenRepository tokenRepository;

    @PostMapping("/reg")
    public ResponseEntity<String> creteUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.ok("Please select another username");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(userDTO.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password or wrong username");
        }

        User user = optionalUser.get();

        if (bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            Token token = tokenService.generateToken(user.getId());
            return ResponseEntity.ok(token.getName());
        }


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password or wrong username");
    }

    @PostMapping("/loginWithToken")
    public ResponseEntity<String> loginWithToken (@RequestBody String tokenName) {
        Token token = tokenRepository.findByName(tokenName);

        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password or wrong username");
        }

        if (tokenService.isTimeExpiration(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password or wrong username");
        }

        return ResponseEntity.ok("Success");
    }
}
