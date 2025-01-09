package com.example.routeplanner.service;

import com.example.routeplanner.model.InfoUsersDTO;
import com.example.routeplanner.model.Users;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    List<InfoUsersDTO> getInfoUser();
    Users updateAuthoritiesUserById(Users user,Integer id
    );
    void deleteUserById(Integer id);
}
