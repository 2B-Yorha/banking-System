package com.Banking.system.controller;


import com.Banking.system.Service.interfaces.UserService;
import com.Banking.system.dto.request.UpdateUserRequest;
import com.Banking.system.dto.response.ApiResponse;
import com.Banking.system.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails){
        UserResponse response = userService.getMyProfile(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", response));

    }


    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                                     @Valid @RequestBody UpdateUserRequest request){
        UserResponse response = userService.updateMyProfile(userDetails.getUsername(), request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));

    }



}
