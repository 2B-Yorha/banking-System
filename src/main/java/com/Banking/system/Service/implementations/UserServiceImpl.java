package com.Banking.system.Service.implementations;


import com.Banking.system.Service.interfaces.UserService;
import com.Banking.system.dto.request.UpdateUserRequest;
import com.Banking.system.dto.response.UserResponse;
import com.Banking.system.entity.User;
import com.Banking.system.exception.ResourceNotFoundException;
import com.Banking.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse getMyProfile(String email) {
        User user = findByEmail(email);
        return mapToResponse(user);
    }




    @Override
    @Transactional
    public UserResponse updateMyProfile(String email, UpdateUserRequest request) {
        User user = findByEmail(email);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName()  != null) user.setLastName(request.getLastName());
        if (request.getPhone()     != null) user.setPhone(request.getPhone());

        return mapToResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }






    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }


}
