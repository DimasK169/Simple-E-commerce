package com.user.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersLoginResponse;
import com.user.app.dto.result.UsersSaveResponse;
import com.user.app.service.implement.UsersServiceImpl;
import com.user.app.service.interfacing.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsersServiceImpl usersService;

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UsersController usersController;

    private ObjectMapper objectMapper;
    private UsersRequest usersRequest;
    private UsersSaveResponse usersSaveResponse;
    private UsersLoginResponse usersLoginResponse;
    private RestApiResponse<UsersSaveResponse> usersSaveRestApiResponse;
    private RestApiResponse<UsersLoginResponse> usersLoginRestApiResponse;
    private ResponseEntity<RestApiResponse<UsersLoginResponse>> responseEntity;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
        objectMapper = new ObjectMapper();

        // Initialize test data using builders
        usersRequest = UsersRequest.builder()
                .userEmail("test@example.com")
                .userPassword("password123")
                .build();

        usersSaveResponse = UsersSaveResponse.builder()
                .userEmail("test@example.com")
                .userName("Test User")
                .build();

        usersLoginResponse = UsersLoginResponse.builder()
                .userEmail("test@example.com")
                .userToken("jwt-token")

                .build();

        usersSaveRestApiResponse = RestApiResponse.<UsersSaveResponse>builder()
                .code(HttpStatus.OK.toString())
                .message("Success")
                .data(usersSaveResponse)
                .build();

        usersLoginRestApiResponse = RestApiResponse.<UsersLoginResponse>builder()
                .code(HttpStatus.OK.toString())
                .message("Success")
                .data(usersLoginResponse)
                .build();

        responseEntity = ResponseEntity.ok(usersLoginRestApiResponse);
    }

    @Test
    void testCreateUser() throws Exception {
        when(usersService.saveUsers(any(UsersRequest.class))).thenReturn(usersSaveRestApiResponse);

        mockMvc.perform(post("/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.User_Email").value("test@example.com"));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        when(usersService.getUsersByEmail(anyString())).thenReturn(usersSaveRestApiResponse);

        mockMvc.perform(get("/users/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.User_Email").value("test@example.com"));
    }

    @Test
    void testLogin() throws Exception {
        when(authService.authentication(any(UsersRequest.class))).thenReturn(usersLoginRestApiResponse);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.User_Email").value("test@example.com"))
                .andExpect(jsonPath("$.data.User_Token").value("jwt-token"));
    }

    @Test
    void testGetCurrentUser() throws Exception {
        when(authService.getCurrentUser(any(HttpServletRequest.class))).thenReturn(responseEntity);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.User_Email").value("test@example.com"));
    }

    @Test
    void testGetMe() throws Exception {
        when(usersService.getCurrentUser()).thenReturn(usersLoginRestApiResponse);

        mockMvc.perform(get("/users/getMe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.User_Email").value("test@example.com"));
    }


}