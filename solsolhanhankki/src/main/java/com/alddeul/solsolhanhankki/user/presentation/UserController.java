package com.alddeul.solsolhanhankki.user.presentation;

import com.alddeul.solsolhanhankki.user.application.UserAccountService;
import com.alddeul.solsolhanhankki.user.presentation.request.UserRequest;
import com.alddeul.solsolhanhankki.user.presentation.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserAccountService userAccountService;

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        var response = userAccountService.createUser(userRequest);
        return ResponseEntity.ok().body(response);
    }
}
