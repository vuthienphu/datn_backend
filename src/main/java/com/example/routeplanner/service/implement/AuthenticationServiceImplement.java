package com.example.routeplanner.service.implement;

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
        // Tạo một đối tượng người dùng mới và thiết lập các thông tin từ yêu cầu
        Users user = new Users();
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        /*
        Role role = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.USER;
        user.setRole(role);
*/
        user.setRole(signUpRequest.getRole());
        // Lưu người dùng vào cơ sở dữ liệu và nhận lại thông tin người dùng đã được lưu


        // Tạo token JWT dựa trên thông tin người dùng và trả về đối tượng AuthenticationResponse
        return usersRepository.save(user);
    }

    // Phương thức để xác thực người dùng
    @Override
    public JWTAuthenticationResponse login(SignInRequest signInRequest) {
        try{
            // Sử dụng AuthenticationManager để xác thực thông tin đăng nhập của người dùng
            System.out.println("Attempting login for email: " + signInRequest.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getEmail(),
                            signInRequest.getPassword()
                    )
            );

            // Tìm người dùng trong cơ sở dữ liệu và kiểm tra xem có tồn tại không
            var user = usersRepository.findByEmail(signInRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

            // Nếu tồn tại, tạo token JWT và trả về đối tượng AuthenticationResponse
            var token = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            JWTAuthenticationResponse jwtAuthenticationResponse = new JWTAuthenticationResponse();
            jwtAuthenticationResponse.setToken(token);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);

            return jwtAuthenticationResponse;
        } catch (UsernameNotFoundException e) {
            // Người dùng không tồn tại
            throw new RuntimeException("The username you provided does not exist");
        } catch (BadCredentialsException e) {
            // Mật khẩu sai
            throw new RuntimeException("The password you entered is incorrect");
        } catch (Exception e) {
            // Các lỗi khác
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("An error occurred during login: " + e.getMessage());
        }
    }
}
