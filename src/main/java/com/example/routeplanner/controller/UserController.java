package com.example.routeplanner.controller;

import com.example.routeplanner.model.Config;
import com.example.routeplanner.model.InfoUsersDTO;
import com.example.routeplanner.model.Users;
import com.example.routeplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api/users")
    public List<InfoUsersDTO> getInfoUser(){
        return userService.getInfoUser();
    }

    @PutMapping("/api/user/{id}")
    public ResponseEntity<Users> updateAuthoritiesUserById(@PathVariable("id") Integer id, @RequestBody Users user) {
        return new ResponseEntity<Users>(userService.updateAuthoritiesUserById(user,id) ,HttpStatus.OK);
    }

    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Integer id) {
        userService.deleteUserById(id);
        return new ResponseEntity<String>("Delete successfully", HttpStatus.OK);
    }
}
