package com.agrosoft.User.service;

import com.agrosoft.User.domain.User;
import com.agrosoft.User.dto.CreateUserRequestDTO;
import com.agrosoft.User.dto.UpdateUserRequestDTO;
import com.agrosoft.User.dto.UserResponseDTO;
import com.agrosoft.User.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    public UserResponseDTO create(CreateUserRequestDTO dto) {

        String normalizedEmail = dto.getEmail() == null ? "" : dto.getEmail().trim().toLowerCase();

        if (normalizedEmail.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already exists");
        }


        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setAccessLevel(dto.getAccessLevel());
        user.setActive(true);


        User savedUser = userRepository.save(user);


        return toResponseDTO(savedUser);
    }


    public List<UserResponseDTO> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    public UserResponseDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));


        return toResponseDTO(user);
    }


    public UserResponseDTO update(UUID id, UpdateUserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            String normalizedEmail = dto.getEmail().trim().toLowerCase();

            if (!normalizedEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(normalizedEmail)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }

            user.setEmail(normalizedEmail);
        }

        if (dto.getAccessLevel() != null) {
            user.setAccessLevel(dto.getAccessLevel());
        }

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword().trim()));
        }

        User updatedUser = userRepository.save(user);
        return toResponseDTO(updatedUser);
    }


    public void deactivate(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!user.getActive()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User already inactive"
            );
        }


        user.setActive(false);


        userRepository.save(user);
    }


    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setAccessLevel(user.getAccessLevel());
        dto.setLastLogin(user.getLastLogin());
        dto.setActive(user.getActive());
        return dto;
    }
}