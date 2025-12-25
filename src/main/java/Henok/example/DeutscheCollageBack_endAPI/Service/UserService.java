package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(UserRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role cannot be empty");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    // Enables a user account by setting the enabled flag to true.
    // Why: Allows reactivation of disabled accounts.
    // Also sets other flags to active state for completeness.
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        userRepository.save(user);
    }

    // Disables a user account by setting the enabled flag to false.
    // Why: For temporary suspension without deleting the record.
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setEnabled(false);
        userRepository.save(user);
    }

    // Changes the password for the authenticated user (student self-service)
    // Why: Allows users to update their own password securely
    // Security: Verifies old password matches hashed version, encodes new password with BCrypt
    @Transactional
    public void changeSelfPassword(String username, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        User user = (User) loadUserByUsername(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Resets the password for a student (admin action by registrar)
    // Why: Allows registrars to reset student passwords without old password
    // Security: Ensures target user exists and has STUDENT role, encodes new password with BCrypt
    @Transactional
    public void resetStudentPassword(Long userId, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (user.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Can only reset passwords for students");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    // Resets password for a specific user (admin action)
    // Why: Allows privileged users to reset without old password
    // Security: Encodes new password, optional role check
    @Transactional
    public void resetUserPassword(Long userId, String newPassword, Role expectedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Optional role validation
        if (expectedRole != null && user.getRole() != expectedRole) {
            throw new IllegalArgumentException("Can only reset passwords for " + expectedRole.name().toLowerCase() + "s");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Deletes a user by their ID from the users table
    // Why: Provides a simple administrative operation to remove a user account permanently
    // Important: This method performs a HARD DELETE directly on the User entity only.
    // It does NOT check or cascade delete related records in other tables.
    // Use with caution — foreign key constraints may prevent deletion if references exist.
    @Transactional
    public void deleteUserById(Long userId) {
        // Validate input
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        try {
            userRepository.deleteById(userId);

        } catch (DataIntegrityViolationException e) {
            // Most common case: foreign key constraint violation (e.g., StudentDetails references this User)
            throw new DataIntegrityViolationException(
                    "Cannot delete user because it is referenced by other records (e.g., student/teacher details)", e);
        } catch (Exception e) {
            // Fallback for completely unexpected errors
            throw new RuntimeException("Unexpected error while deleting user with id: " + userId, e);
        }
    }
}


