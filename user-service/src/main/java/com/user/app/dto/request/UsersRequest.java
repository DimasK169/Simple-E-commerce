package com.user.app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsersRequest {

    @JsonProperty("User_Name")
    @NotEmpty
    private String userName;
    @JsonProperty("User_Email")
    @NotEmpty
    private String userEmail;
    @JsonProperty("User_Password")
    @NotEmpty
    private String userPassword;
    @JsonProperty("User_Phone")
    @NotEmpty
    private String userPhone;
    @JsonProperty("User_Address")
    @NotEmpty
    private String userAddress;
    @JsonProperty("User_Role")
    @NotEmpty
    private String userRole;
}
