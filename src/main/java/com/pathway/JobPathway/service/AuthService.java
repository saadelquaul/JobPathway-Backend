package com.pathway.JobPathway.service;

import com.pathway.JobPathway.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
