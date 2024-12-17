package com.example.routeplanner.service.implement;

import com.example.routeplanner.service.JWTService;
import com.example.routeplanner.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImplement implements JWTService {
    // Khóa bí mật để tạo và xác thực JWT
    private final String SECRET_KEY = "14b8a816282dab2d61288387f56cc715f905760f262c3fa85fbebe38ae7ceda8";

    // Phương thức kiểm tra tính hợp lệ của JWT
    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    // Phương thức kiểm tra xem JWT có hết hạn hay không
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Phương thức trích xuất thời gian hết hạn của JWT từ Claims
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Phương thức trích xuất tên người dùng từ JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Phương thức trích xuất thông tin từ JWT bằng cách sử dụng Function
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // Phương thức trích xuất tất cả các thông tin từ JWT bằng cách giải mã và xác thực khóa
    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Phương thức tạo JWT từ thông tin người dùng
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+10000*60*24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String,Object> extractClaim, UserDetails userDetails) {

        return Jwts.builder().setClaims(extractClaim).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+604800000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Phương thức để lấy khóa ký từ khóa bí mật đã được mã hóa
    private SecretKey getSigningKey() {
        // Giải mã khóa bí mật từ chuỗi hex và trả về khóa ký
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
