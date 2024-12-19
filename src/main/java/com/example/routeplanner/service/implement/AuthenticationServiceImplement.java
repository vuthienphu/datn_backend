package com.example.routeplanner.service.implement;

import com.example.routeplanner.exception.AuthenticationException;
import com.example.routeplanner.exception.EmailAlreadyExistsException;
import com.example.routeplanner.service.AuthenticationService;
import com.example.routeplanner.model.*;
import com.example.routeplanner.repository.UsersRepository;
import com.example.routeplanner.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
@Service
public class AuthenticationServiceImplement implements AuthenticationService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private  JWTServiceImplement jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    // Constructor để inject dependencies UsersRepository, PasswordEncoder, JwtService và AuthenticationManager


    // Phương thức để đăng ký người dùng

    @Override
    public Users register(SignUpRequest signUpRequest) {
        if (usersRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email đã tồn tại.");
        }
        Users user = new Users();
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Role role = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.USER;
        user.setRole(role);

        //  user.setRole(signUpRequest.getRole());
        // Lưu người dùng vào cơ sở dữ liệu và nhận lại thông tin người dùng đã được lưu


        return usersRepository.save(user);
    }

    // Phương thức để xác thực người dùng
    @Override
    public JWTAuthenticationResponse login(SignInRequest signInRequest) {
        try{
            var user = usersRepository.findByEmail(signInRequest.getEmail()).orElseThrow(() -> new AuthenticationException("Email không tồn tại."));

            // Sử dụng AuthenticationManager để xác thực thông tin đăng nhập của người dùng
            System.out.println("Attempting login for email: " + signInRequest.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getEmail(),
                            signInRequest.getPassword()
                    )
            );

            // Tìm người dùng trong cơ sở dữ liệu và kiểm tra xem có tồn tại không


            // Nếu tồn tại, tạo token JWT và trả về đối tượng AuthenticationResponse
            var token = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            JWTAuthenticationResponse jwtAuthenticationResponse = new JWTAuthenticationResponse();
            jwtAuthenticationResponse.setToken(token);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);

            return jwtAuthenticationResponse;
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Sai mật khẩu, vui lòng thử lại.");
        } catch (AuthenticationException e) {
            // Bắt lại ngoại lệ AuthenticationException cụ thể (ví dụ: "Email không tồn tại.")
            throw e;
        } catch (Exception e) {
            // Xử lý các ngoại lệ khác
            throw new AuthenticationException("Đăng nhập thất bại, vui lòng thử lại sau.");
        }
    }
}
