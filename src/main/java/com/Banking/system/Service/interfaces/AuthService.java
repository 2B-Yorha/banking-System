package com.Banking.system.Service.interfaces;

import com.Banking.system.dto.request.LoginRequest;
import com.Banking.system.dto.request.RegisterRequest;
import com.Banking.system.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);



}
