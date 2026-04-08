package com.agrosoft.auth.service;

import com.agrosoft.User.domain.User;
import com.agrosoft.User.domain.AccessLevel;
import com.agrosoft.User.dto.CreateUserRequestDTO;
import com.agrosoft.User.dto.UserResponseDTO;
import com.agrosoft.User.repository.UserRepository;
import com.agrosoft.User.service.UserService;
import com.agrosoft.auth.dto.AuthResponseDTO;
import com.agrosoft.auth.dto.LoginRequestDTO;
import com.agrosoft.auth.security.AuthUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO dto) {
        String normalizedEmail = dto.getEmail() == null ? "" : dto.getEmail().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, dto.getPassword())
            );
        } catch (DisabledException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is inactive");
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is inactive");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        AuthUserDetails authUser = new AuthUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getAccessLevel(),
                user.getActive()
        );

        String token = jwtService.generateToken(authUser);

        return new AuthResponseDTO(
                token,
                "Bearer",
                jwtService.extractExpiration(token),
                user.getId(),
                user.getEmail(),
                user.getAccessLevel()
        );
    }

    public UserResponseDTO me(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setAccessLevel(user.getAccessLevel());
        dto.setActive(user.getActive());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public UserResponseDTO bootstrapAdmin(CreateUserRequestDTO dto) {
        if (userRepository.count() > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bootstrap is available only when there are no users");
        }

        if (dto.getAccessLevel() != AccessLevel.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bootstrap user must have ADMIN access level");
        }

        return userService.create(dto);
    }
}
