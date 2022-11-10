package uz.pdp.appjwtrealemailauditing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjwtrealemailauditing.dto.ApiResponse;
import uz.pdp.appjwtrealemailauditing.dto.LoginDto;
import uz.pdp.appjwtrealemailauditing.dto.RegisterDto;
import uz.pdp.appjwtrealemailauditing.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;


    @PostMapping("/register")
    public HttpEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        ApiResponse apiResponse = authService.registerUser(registerDto);
        return ResponseEntity.status(apiResponse.isSucces() ? 201 : 409).body(apiResponse);

    }

    @GetMapping("/verifyEmail")
    public HttpEntity<?> verifyEmail(@RequestParam String emailCode, @RequestParam String email) {
        ApiResponse apiResponse = authService.verifyEmail(emailCode, email);
        return ResponseEntity.status(apiResponse.isSucces() ? 200 : 409).body(apiResponse);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDto loginDto) {
        ApiResponse apiResponse = authService.login(loginDto);
        return ResponseEntity.status(apiResponse.isSucces() ? 200 : 401).body(apiResponse);
    }
}
