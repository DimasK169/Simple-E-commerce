package com.user.app.controller;

import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersLoginResponse;
import com.user.app.dto.result.UsersSaveResponse;
import com.user.app.dto.result.UsersUpdateResponse;
import com.user.app.entity.Users;
import com.user.app.service.implement.UsersServiceImpl;
import com.user.app.service.interfacing.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersServiceImpl usersService;

    @Autowired
    private AuthService authService;

    @PostMapping("/save")
    public RestApiResponse<UsersSaveResponse> createFlashSale(@Valid @RequestBody UsersRequest usersRequest){
        return usersService.saveUsers(usersRequest);
    }

    @GetMapping("/{userEmail}")
    public RestApiResponse<UsersSaveResponse> getUserByEmail(@Valid @PathVariable String userEmail){
        return usersService.getUsersByEmail(userEmail);
    }

    @PostMapping("/login")
    public RestApiResponse<UsersLoginResponse> login(@RequestBody UsersRequest usersRequest) throws Exception {
        System.out.println("LOGIN ENDPOINT HIT");
        return authService.authentication(usersRequest);
    }

    @GetMapping("/me")
    public ResponseEntity<RestApiResponse<UsersLoginResponse>> getCurrentUser(HttpServletRequest request) {
        return authService.getCurrentUser(request);
    }

    @GetMapping("/getMe")
    public ResponseEntity<RestApiResponse<UsersLoginResponse>> getCurrentUser() {
        return ResponseEntity.ok(usersService.getCurrentUser());
    }

    @PostMapping("/refresh")
    public RestApiResponse<UsersLoginResponse> refreshToken(HttpServletRequest request){
        return authService.refreshToken((String) request.getHeader("Authorization"));
    }
}
