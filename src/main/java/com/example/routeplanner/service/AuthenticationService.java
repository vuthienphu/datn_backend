package com.example.routeplanner.service;

import com.example.routeplanner.model.JWTAuthenticationResponse;
import com.example.routeplanner.model.SignInRequest;
import com.example.routeplanner.model.SignUpRequest;
import com.example.routeplanner.model.Users;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {
    public Users register(SignUpRequest signUpRequest);
    public JWTAuthenticationResponse login(SignInRequest signInRequest);
}
