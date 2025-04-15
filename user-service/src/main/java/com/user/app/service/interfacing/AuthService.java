package com.user.app.service.interfacing;

import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersLoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    RestApiResponse<UsersLoginResponse> authentication(UsersRequest usersRequest) throws Exception;

}
