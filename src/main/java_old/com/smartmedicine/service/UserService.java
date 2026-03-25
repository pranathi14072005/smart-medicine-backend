package com.smartmedicine.service;

import com.smartmedicine.dto.UserDTO;
import com.smartmedicine.exception.BadRequestException;
import com.smartmedicine.exception.ResourceNotFoundException;
import com.smartmedicine.model.User;
import com.smartmedicine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userDTO.getFullName() != null) user.setFullName(userDTO.getFullName());
        if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getDateOfBirth() != null) user.setDateOfBirth(userDTO.getDateOfBirth());
        if (userDTO.getProfilePicture() != null) user.setProfilePicture(userDTO.getProfilePicture());
        if (userDTO.isNotificationEnabled() != user.isNotificationEnabled()) {
            user.setNotificationEnabled(userDTO.isNotificationEnabled());
        }

        user = userRepository.save(user);
        log.info("User profile updated: {}", user.getUsername());
        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getUsername());
    }

    @Transactional
    public void deactivateAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setActive(false);
        userRepository.save(user);
        log.info("Account deactivated for user: {}", user.getUsername());
    }

    // Admin operations
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Transactional
    public UserDTO updateUserRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setRole(role);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public Map<String, Long> getUserStats() {
        return Map.of(
                "total", userRepository.count(),
                "users", userRepository.countByRole(User.Role.USER),
                "admins", userRepository.countByRole(User.Role.ADMIN),
                "pharmacists", userRepository.countByRole(User.Role.PHARMACIST)
        );
    }
}
