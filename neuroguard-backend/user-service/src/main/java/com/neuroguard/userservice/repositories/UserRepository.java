package com.neuroguard.userservice.repositories;

import com.neuroguard.userservice.entities.Role;
import com.neuroguard.userservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);  // Find user by email
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);  // Check if email already exists
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsername(String username);  // Check if username already exists
    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username);  // Find user by username
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    List<User> findByRole(Role role);
}
