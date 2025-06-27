package com.example.chatServer.sevice;

import com.example.chatServer.exception.InvalidCredentialsException;
import com.example.chatServer.exception.UsernameExistsException;
import com.example.chatServer.model.dto.UserDTO;
import com.example.chatServer.model.dto.authDTO.LoginUserDTO;
import com.example.chatServer.model.dto.authDTO.RegistrationUserDTO;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void register(RegistrationUserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameExistsException("Username already exists");
        }
        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    public String login(LoginUserDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return tokenService.generateToken(user.getId()).getName();
    }
}
