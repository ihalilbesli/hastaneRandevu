package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hastarandevu/users")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }
    // Email ile kullanıcı bulma
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
    //Rol bilgisine gore siralama
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUserByRole(@PathVariable User.Role role){
        List<User> users=userService.findUsersByRole(role);
        if (!users.isEmpty()){
            return ResponseEntity.ok(users);
        }
        else
            return ResponseEntity.notFound().build();
    }
    // ID ile kullanıcıyı getir
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        Optional<User> optionalUser=userService.getUserById(id);
        if (optionalUser.isPresent()){
            return ResponseEntity.ok(optionalUser.get());// Kullanıcı bulunduysa 200 OK döndü
        }
        else {
            return ResponseEntity.notFound().build();// Kullanıcı bulunamazsa 404 Not Found döndür
        }
    }
    // Telefon numarası ile kullanıcı bulma
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        Optional<User> userOptional = userService.findByPhoneNumber(phoneNumber);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // İsme göre kullanıcı arama
    @GetMapping("/name/{name}")
    public ResponseEntity<List<User>> getUsersByName(@PathVariable String name) {
        List<User> users = userService.findByNameContainingIgnoreCase(name);

        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Cinsiyete göre kullanıcı listeleme
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<User>> getUsersByGender(@PathVariable User.Gender gender) {
        List<User> users = userService.findByGender(gender);

        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Kan grubuna göre hasta listeleme
    @GetMapping("/blood-type/{bloodType}")
    public ResponseEntity<List<User>> getUsersByBloodType(@PathVariable User.Bloodtype bloodType) {
        List<User> users = userService.findByBloodType(bloodType);

        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Uzmanlık alanına göre doktorları listeleme
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<User>> getUsersBySpecialization(@PathVariable String specialization) {
        List<User> users = userService.findBySpecializationContainingIgnoreCase(specialization);

        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();//test
        }
    }

}
