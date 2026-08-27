package com.novelverse.novelverse.controller;


import com.novelverse.novelverse.domain.User;
import com.novelverse.novelverse.dto.user.LoginDTO;
import com.novelverse.novelverse.dto.user.RegisterDTO;
import com.novelverse.novelverse.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO.username, registerDTO.password);
        User user = userService.login(registerDTO.username, registerDTO.password);
        return Map.of(
                "id", user.getId(),
                "username", user.getName()
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.username, loginDTO.password);
        return Map.of(
                "id", user.getId(),
                "username", user.getName()
        );
    }
}
