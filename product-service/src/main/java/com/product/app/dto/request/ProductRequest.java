package com.product.app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductRequest {

    @JsonProperty("name")
    @NotEmpty(message = "Product name cannot be empty")
    private String productName;

    @JsonProperty("code")
    @NotEmpty(message = "Product code cannot be empty")
    private String productCode;

    @JsonProperty("description")
    @NotEmpty(message = "Product description cannot be empty")
    private String productDescription;

    @JsonProperty("category")
    @NotEmpty(message = "Product category cannot be empty")
    private String productCategory;

    @JsonProperty("stock")
    @NotEmpty(message = "Product stock cannot be empty")
    private String productStock;

    @JsonProperty("price")
    @NotEmpty(message = "Product price cannot be empty")
    private String productPrice;

    @JsonProperty("image")
    private String productImage;

    @JsonProperty("isAvailable")
    private Boolean productIsAvailable;
}
