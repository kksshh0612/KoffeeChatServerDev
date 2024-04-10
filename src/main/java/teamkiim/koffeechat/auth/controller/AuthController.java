package teamkiim.koffeechat.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamkiim.koffeechat.auth.dto.request.LoginRequest;
import teamkiim.koffeechat.auth.dto.request.SignUpRequest;
import teamkiim.koffeechat.auth.service.AuthService;
import teamkiim.koffeechat.global.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){

        return authService.signUp(signUpRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){

        return authService.login(loginRequest, response);
    }

    @Auth
    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest request){

        Long pk = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        return ResponseEntity.ok(pk);
    }
}
