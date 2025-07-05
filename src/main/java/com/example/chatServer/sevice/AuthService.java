package com.example.chatServer.sevice;

import com.example.chatServer.exception.UsernameExistsException;
import com.example.chatServer.model.dto.authDTO.LoginUserDTO;
import com.example.chatServer.model.dto.authDTO.RegistrationUserDTO;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationProvider authenticationProvider;


    public void register(RegistrationUserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameExistsException("Username already exists");
        }
        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    public String login(LoginUserDTO dto) {
        Authentication u = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );

        log.info("Произошло логин: {}  {}", u.getName(), u.isAuthenticated());

        UserDetails user = userService.loadUserByUsername(dto.getUsername());

        String token = jwtService.generateToken(user);
        log.info("Его токен: {}", token);
        return token;
    }
}
