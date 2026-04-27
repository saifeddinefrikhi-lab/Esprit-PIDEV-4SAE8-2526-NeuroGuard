package com.neuroguard.userservice.controllers;

import com.neuroguard.userservice.dto.CreateUserRequest;
import com.neuroguard.userservice.dto.UpdateUserRequest;
import com.neuroguard.userservice.dto.UserDto;
import com.neuroguard.userservice.entities.Role;
import com.neuroguard.userservice.entities.User;
import com.neuroguard.userservice.repositories.UserRepository;
import com.neuroguard.userservice.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// In user-service, e.g., UserController.java
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // In UserController.java

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        UserDto updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(convertToDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(roleEnum);
            List<UserDto> dtos = users.stream().map(this::convertToDto).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Helper method to convert User to UserDto (reuse existing mapping logic)
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setName(user.getFirstName() + " " + user.getLastName());
        if (user.getRole() != null) {
            dto.setRole(user.getRole().name());
        }

        if (user.getCaregiver() != null) {
            dto.setCaregiverId(user.getCaregiver().getId());
        }

        if (user.getDoctor() != null) {
            dto.setDoctorId(user.getDoctor().getId());
            dto.setDoctorEmail(user.getDoctor().getEmail());
        }

        if (user.getRole() == Role.CAREGIVER && user.getPatients() != null) {
            List<UserDto> patientDtos = user.getPatients().stream().map(p -> {
                UserDto pDto = new UserDto();
                pDto.setId(p.getId());
                pDto.setUsername(p.getUsername());
                pDto.setEmail(p.getEmail());
                pDto.setFirstName(p.getFirstName());
                pDto.setLastName(p.getLastName());
                pDto.setName(p.getFirstName() + " " + p.getLastName());
                if (p.getRole() != null) {
                    pDto.setRole(p.getRole().name());
                }
                pDto.setCaregiverId(user.getId());
                return pDto;
            }).collect(Collectors.toList());
            dto.setPatients(patientDtos);
        }
        return dto;
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> ResponseEntity.ok(convertToDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/caregiver/{caregiverId}/patients")
    public ResponseEntity<List<UserDto>> getCaregiverPatients(@PathVariable Long caregiverId) {
        return ResponseEntity.ok(userService.getPatientsByCaregiver(caregiverId));
    }

    @PutMapping("/{patientId}/assign-caregiver/{caregiverId}")
    public ResponseEntity<UserDto> assignCaregiver(@PathVariable Long patientId, @PathVariable Long caregiverId) {
        try {
            return ResponseEntity.ok(userService.assignCaregiverToPatient(patientId, caregiverId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{patientId}/assign-doctor/{doctorId}")
    public ResponseEntity<UserDto> assignDoctor(@PathVariable Long patientId, @PathVariable Long doctorId) {
        try {
            return ResponseEntity.ok(userService.assignDoctorToPatient(patientId, doctorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}