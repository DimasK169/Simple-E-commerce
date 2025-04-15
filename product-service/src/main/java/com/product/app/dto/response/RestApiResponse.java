package com.product.app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestApiResponse<T> {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;

    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> error;

    @JsonProperty("data_all")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<T> dataAll;

}
