package com.user.app.controller;

import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersSaveResponse;
import com.user.app.dto.result.UsersUpdateResponse;
import com.user.app.service.implement.UsersServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersServiceImpl usersService;

    @PostMapping("/save")
    public RestApiResponse<UsersSaveResponse> createFlashSale(@Valid @RequestBody UsersRequest usersRequest){
        return usersService.saveUsers(usersRequest);
    }

    @GetMapping("/{userEmail}")
    public RestApiResponse<UsersSaveResponse> getUserByEmail(@Valid @PathVariable String userEmail){
        return usersService.getUsersByEmail(userEmail);
    }

    @PatchMapping("/delete/{userEmail}")
    public RestApiResponse<UsersUpdateResponse> deleteFlashSale( @PathVariable String userEmail){
        return usersService.deleteUsers(userEmail);
    }

}
