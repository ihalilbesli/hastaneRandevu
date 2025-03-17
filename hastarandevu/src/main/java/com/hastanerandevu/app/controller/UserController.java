package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email){
        Optional<User> userOptional=userService.findByEmail(email);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());  // Kullanıcı bulunduysa 200 OK döndü
        }
        else {
            return ResponseEntity.notFound().build(); // Kullanıcı bulunamazsa 404 Not Found döndür
        }
    }
    
}
