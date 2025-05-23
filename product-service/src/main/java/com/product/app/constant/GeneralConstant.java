package com.product.app.constant;

public class GeneralConstant {


    //Config Untuk Product
    public static final String PRODUCT_DATASOURCE_PATH = "spring.datasource.product";
    public static final String PRODUCT_REPOSITORY = "com.product.app.repository.product";
    public static final String PRODUCT_ENTITY = "com.product.app.entity.product";
    public static final String PRODUCT_DATA_SOURCE = "productDataSource";
    public static final String PRODUCT_ENTITY_MANAGER_FACTORY = "productEntityManagerFactory";
    public static final String PRODUCT_TRANSACTION_MANAGER = "productTransactionManager";

    //Config Untuk User
    public static final String USER_DATASOURCE_PATH = "spring.datasource.user";
    public static final String USER_REPOSITORY = "com.product.app.repository.user";
    public static final String USER_ENTITY = "com.product.app.entity.user";
    public static final String USER_DATA_SOURCE = "usersDataSource";
    public static final String USER_ENTITY_MANAGER_FACTORY = "usersEntityManagerFactory";
    public static final String USER_TRANSACTION_MANAGER = "usersTransactionManager";

    //Config Untuk Flash Sale
    public static final String FLASHSALE_DATASOURCE_PATH = "spring.datasource.flash-sale";
    public static final String FLASHSALE_REPOSITORY = "com.product.app.repository.flashsale";
    public static final String FLASHSALE_ENTITY = "com.product.app.entity.flashsale";
    public static final String FLASHSALE_DATA_SOURCE = "fsDataSource";
    public static final String FLASHSALE_ENTITY_MANAGER_FACTORY = "fsEntityManagerFactory";
    public static final String FLASHSALE_TRANSACTION_MANAGER = "fsTransactionManager";
}
