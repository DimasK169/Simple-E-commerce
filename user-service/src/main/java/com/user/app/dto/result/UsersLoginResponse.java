package com.user.app.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsersLoginResponse {

    @JsonProperty("User_Email")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userEmail;
    @JsonProperty("User_Password")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userPassword;
    @JsonProperty("User_Name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userName;
    @JsonProperty("User_Role")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userRole;
    @JsonProperty("User_Token")
    private String userToken;
}
