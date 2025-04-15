package com.user.app.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsersLoginResponse {

    @JsonProperty("User_Email")
    private String userEmail;
    @JsonProperty("User_Password")
    private String userPassword;
    @JsonProperty("User_Token")
    private String userToken;

}
