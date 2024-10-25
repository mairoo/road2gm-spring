package kr.co.road2gm.api.domain.auth.controller;

import kr.co.road2gm.api.domain.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public String index() {
        return "users";
    }
}
