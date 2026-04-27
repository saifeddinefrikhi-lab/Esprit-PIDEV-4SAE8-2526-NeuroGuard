package com.neuroguard.userservice.services;

import com.neuroguard.userservice.dto.CreateUserRequest;
import com.neuroguard.userservice.dto.UpdateUserRequest;
import com.neuroguard.userservice.dto.UserDto;
import com.neuroguard.userservice.entities.Role;
import com.neuroguard.userservice.entities.User;
import com.neuroguard.userservice.security.JwtUtils;
import com.neuroguard.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user
    public String registerUser(User user) {
        String normalizedUsername = normalizeIdentifier(user.getUsername());
        String normalizedEmail = normalizeEmail(user.getEmail());

        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return "User already exists!";
        }
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            return "Username already exists!";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String loginUser(String username, String password) {
        String normalizedIdentifier = normalizeIdentifier(username);
        User user = userRepository.findByUsernameIgnoreCase(normalizedIdentifier)
                .or(() -> userRepository.findByEmailIgnoreCase(normalizedIdentifier))
                .orElse(null);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name(), user.getId());
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedIdentifier = normalizeIdentifier(username);

        User user = userRepository.findByUsernameIgnoreCase(normalizedIdentifier)
                .or(() -> userRepository.findByEmailIgnoreCase(normalizedIdentifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert User entity to UserDetails (you may need to implement a custom UserDetails class)
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
    }

    // In UserService.java

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(CreateUserRequest request) {
        String normalizedUsername = normalizeIdentifier(request.getUsername());
        String normalizedEmail = normalizeEmail(request.getEmail());

        // Validate uniqueness
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        // map fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        return convertToDto(saved);
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // update only non-null fields
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getUsername() != null) {
            String normalizedUsername = normalizeIdentifier(request.getUsername());
            // check uniqueness if changed
            if (!user.getUsername().equalsIgnoreCase(normalizedUsername) && userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(normalizedUsername);
        }
        if (request.getEmail() != null) {
            String normalizedEmail = normalizeEmail(request.getEmail());
            if (!user.getEmail().equalsIgnoreCase(normalizedEmail) && userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(normalizedEmail);
        }
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getRole() != null) user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        User updated = userRepository.save(user);
        return convertToDto(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Helper conversion (already exists in UserController, can be moved to service)
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().name());
        return dto;
    }

    private String normalizeIdentifier(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeEmail(String value) {
        String normalized = normalizeIdentifier(value);
        return normalized == null ? null : normalized.toLowerCase();
    }
}
