package com.user.app.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UsersSaveResponse {

    @JsonProperty("User_Name")
    private String userName;
    @JsonProperty("User_Email")
    private String userEmail;
    @JsonProperty("User_Password")
    private String userPassword;
    @JsonProperty("User_Phone")
    private String userPhone;
    @JsonProperty("User_Address")
    private String userAddress;
    @JsonProperty("User_Role")
    private String userRole;
    @JsonProperty("User_IsActive")
    private Boolean userIsActive;
    @JsonProperty("User_CreatedDate")
    private Date userCreatedDate;
}
