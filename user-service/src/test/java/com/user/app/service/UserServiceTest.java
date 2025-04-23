package com.user.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.app.dto.request.AuditTrailsRequest;
import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersLoginResponse;
import com.user.app.dto.result.UsersSaveResponse;
import com.user.app.entity.Users;
import com.user.app.repository.UsersRepository;
import com.user.app.service.implement.AuditTrailsServiceImpl;
import com.user.app.service.implement.UsersServiceImpl;
import com.user.app.service.interfacing.AuditTrailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private AuditTrailsServiceImpl auditTrailsService;

    @InjectMocks
    private UsersServiceImpl usersService; // Use your actual service implementation name

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUsers_success() throws JsonProcessingException {
        UsersRequest request = UsersRequest.builder()
                .userName("John Gaming")
                .userEmail("john@example.com")
                .userPassword("secret")
                .userPhone("123456789")
                .userAddress("123 Main St")
                .userRole("Customer")
                .build();

        Users savedUser = Users.builder()
                .userName("John Gaming")
                .userEmail("john@example.com")
                .userPassword("secret")
                .userPhone("123456789")
                .userAddress("123 Main St")
                .userRole("Customer")
                .userCreatedDate(new Date())
                .build();

        when(usersRepository.save(any(Users.class))).thenReturn(savedUser);

        RestApiResponse<UsersSaveResponse> response = usersService.saveUsers(request);

        assertEquals("Bener", HttpStatus.OK.toString(), response.getCode());
        assertEquals("OK","Berhasil Create Users", response.getMessage());
        assertEquals("OK","John Gaming", response.getData().getUserName());
        verify(auditTrailsService, times(1)).saveAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    void testGetUsersByEmail_success() {
        String email = "jane@example.com";
        Users user = Users.builder()
                .userName("Jane")
                .userEmail(email)
                .build();

        when(usersRepository.findByUserEmail(email)).thenReturn(user);
        RestApiResponse<UsersSaveResponse> response = usersService.getUsersByEmail(email);

        assertEquals("OK", HttpStatus.OK.toString(), response.getCode());
        assertEquals("OK","Berhasil Get Users", response.getMessage());
        assertEquals("OK","Jane", response.getData().getUserName());
        verify(auditTrailsService, times(1)).saveAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    void testGetUsersByEmail_notFound() {
        String email = "notfound@example.com";

        when(usersRepository.findByUserEmail(email)).thenReturn(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usersService.getUsersByEmail(email);
        });

        assertEquals("OK","Invalid Users Email", exception.getMessage());
    }

    @Test
    void testGetCurrentUser_success() {
        // Mocking SecurityContext and Authentication
        String mockEmail = "test@example.com";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockEmail);
        SecurityContextHolder.setContext(securityContext);

        // Mock user from repository
        Users user = Users.builder()
                .userEmail(mockEmail)
                .userName("Test User")
                .userRole("Customer")
                .build();

        when(usersRepository.findByEmail(mockEmail)).thenReturn(user);

        // Run method
        RestApiResponse<UsersLoginResponse> response = usersService.getCurrentUser();

        // Assertions
        assertEquals("OK",HttpStatus.OK.toString(), response.getCode());
        assertEquals("OK","Berhasil Get Current User", response.getMessage());
        assertEquals("OK",mockEmail, response.getData().getUserEmail());
        assertEquals("OK","Test User", response.getData().getUserName());

        verify(auditTrailsService, times(1)).saveAuditTrails(any());
    }

    @Test
    void testGetCurrentUser_userNotFound() {
        String mockEmail = "notfound@example.com";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockEmail);
        SecurityContextHolder.setContext(securityContext);

        when(usersRepository.findByEmail(mockEmail)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usersService.getCurrentUser();
        });

        assertEquals("OK","Invalid User Email", exception.getMessage());
    }

}
