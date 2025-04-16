package com.user.app.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UsersUpdateResponse {

    @JsonProperty("User_Name")
    private String userName;
    @JsonProperty("User_Email")
    private String userEmail;
    @JsonProperty("User_Phone")
    private String userPhone;
    @JsonProperty("User_Address")
    private String userAddress;
    @JsonProperty("User_Role")
    private String userRole;
    @JsonProperty("User_UpdatedDate")
    private Date userUpdatedDate;
}
