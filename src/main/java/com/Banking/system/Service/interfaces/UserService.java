package com.Banking.system.Service.interfaces;

import com.Banking.system.dto.request.UpdateUserRequest;
import com.Banking.system.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getMyProfile(String email);
    UserResponse updateMyProfile(String email, UpdateUserRequest request);


    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    void toggleUserStatus(Long id);


}
