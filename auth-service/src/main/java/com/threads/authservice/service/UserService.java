package com.threads.authservice.service;

import com.threads.authservice.dto.UserDto;
import com.threads.authservice.dto.UserLoginDto;
import com.threads.authservice.entity.User;
import com.threads.authservice.enums.Role;
import com.threads.authservice.exception.AlreadyExistsException;
import com.threads.authservice.exception.InvalidCredentialsException;
import com.threads.authservice.exception.NotFoundException;
import com.threads.authservice.mapper.UserMapper;
import com.threads.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserDto register(UserDto dto) {
        if (repository.existsByEmail(dto.getEmail()) || repository.existsByPhone(dto.getPhone())) {
            throw new AlreadyExistsException("This user is already registered!");
        }
        User user = mapper.toEntity(dto);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        repository.save(user);
        return mapper.toDto(user);
    }

    public String login(UserLoginDto dto) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
            User user = repository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new NotFoundException("This user not found!"));
            return jwtService.generateToken(dto.getEmail(), user.getRole().toString());
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password!");
        }
    }
}
