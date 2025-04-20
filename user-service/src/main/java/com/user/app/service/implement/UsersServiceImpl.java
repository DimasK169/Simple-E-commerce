package com.user.app.service.implement;

import com.user.app.dto.request.AuditTrailsRequest;
import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiError;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersLoginResponse;
import com.user.app.dto.result.UsersSaveResponse;
import com.user.app.entity.Users;
import com.user.app.repository.UsersRepository;
import com.user.app.service.interfacing.UsersService;
import com.user.app.utility.jwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuditTrailsServiceImpl auditTrailsService;

    @Override
    public RestApiResponse<UsersSaveResponse> saveUsers(UsersRequest usersRequest) {

        Users users = Users.builder()
                .userName(usersRequest.getUserName())
                .userEmail(usersRequest.getUserEmail())
                .userPassword(usersRequest.getUserPassword())
                .userPhone(usersRequest.getUserPhone())
                .userAddress(usersRequest.getUserAddress())
                .userRole(usersRequest.getUserRole())
                .userCreatedDate(new Date())
                .build();

        RestApiResponse<UsersSaveResponse> response = new RestApiResponse<>();
        Users saveUsers = usersRepository.save(users);

        UsersSaveResponse usersSaveResponse = UsersSaveResponse.builder()
                .userName(saveUsers.getUserName())
                .userEmail(saveUsers.getUserEmail())
                .userPassword(usersRequest.getUserPassword())
                .userPhone(saveUsers.getUserPhone())
                .userAddress(saveUsers.getUserAddress())
                .userRole(saveUsers.getUserRole())
                .userCreatedDate(saveUsers.getUserCreatedDate())
                .build();

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Create")
                .AtDescription("Save Users")
                .AtDate(new Date())
                .AtRequest(String.valueOf(usersRequest))
                .AtResponse(String.valueOf(usersSaveResponse))
                .build());

        response.setData(usersSaveResponse);
        response.setCode(HttpStatus.OK.toString());
        response.setMessage("Berhasil Create Users");

        return response;
    }

    @Override
    public RestApiResponse<UsersSaveResponse> getUsersByEmail(String userEmail) {
        Users t = getUsersEmail(userEmail);
        if (t == null){
            throw new IllegalArgumentException("Invalid Users Email");
        }
        UsersSaveResponse usersSaveResponse = UsersSaveResponse.builder()
                .userName(t.getUserName())
                .userEmail(t.getUserEmail())
                .build();

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Get")
                .AtDescription("Get Users")
                .AtDate(new Date())
                .AtRequest(userEmail)
                .AtResponse(String.valueOf(usersSaveResponse))
                .build());

        RestApiResponse<UsersSaveResponse> response = new RestApiResponse<>();

        response.setData(usersSaveResponse);
        response.setCode(HttpStatus.OK.toString());
        response.setMessage("Berhasil Get Users");

        return response;
    }

    public RestApiResponse<UsersLoginResponse> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("Invalid User Email");
        }

        UsersLoginResponse userResponse = UsersLoginResponse.builder()
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .userRole(user.getUserRole())
                .build();

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Get")
                .AtDescription("Get Current User")
                .AtDate(new Date())
                .AtRequest(email)
                .AtResponse(String.valueOf(userResponse))
                .build());

        return RestApiResponse.<UsersLoginResponse>builder()
                .code(HttpStatus.OK.toString())
                .message("Berhasil Get Current User")
                .data(userResponse)
                .build();
    }


    public Users getUsersEmail(String usersEmail){
        Users users = usersRepository.findByUserEmail(usersEmail);
        return users;
    }


}
