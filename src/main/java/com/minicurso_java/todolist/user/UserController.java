package com.minicurso_java.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    // Chamando a interface
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());
        if (user != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicate Username");

        var passwordhashred = BCrypt.withDefaults()
                .hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordhashred);

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated);
    }

}
