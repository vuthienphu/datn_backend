package com.example.routeplanner.controller;
import com.example.routeplanner.model.JWTAuthenticationResponse;
import com.example.routeplanner.model.SignInRequest;
import com.example.routeplanner.model.SignUpRequest;
import com.example.routeplanner.model.Users;
import com.example.routeplanner.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;



    @PostMapping("/register")
    public ResponseEntity<Users> register(
            @RequestBody SignUpRequest signUpRequest
    ) {
        return ResponseEntity.ok(authenticationService.register(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthenticationResponse> login(
            @RequestBody SignInRequest signInRequest
    ) {
        return ResponseEntity.ok(authenticationService.login(signInRequest));
    }
}
