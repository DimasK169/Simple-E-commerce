package com.user.app.service.interfacing;

import com.user.app.dto.request.UsersRequest;
import com.user.app.dto.response.RestApiResponse;
import com.user.app.dto.result.UsersSaveResponse;
import com.user.app.dto.result.UsersUpdateResponse;

public interface UsersService {

    RestApiResponse<UsersSaveResponse> saveUsers(UsersRequest usersRequest);

    RestApiResponse<UsersSaveResponse> getUsersByEmail(String userEmail);

    RestApiResponse<UsersUpdateResponse> deleteUsers(String userEmail);

}
