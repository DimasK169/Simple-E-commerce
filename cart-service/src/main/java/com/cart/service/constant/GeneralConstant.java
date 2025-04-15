package com.cart.service.constant;

public class GeneralConstant {

    //Config Untuk Cart
    public static final String CART_DATASOURCE_PATH = "spring.datasource.cart";
    public static final String CART_REPOSITORY = "com.cart.service.repository.cart";
    public static final String CART_ENTITY = "com.cart.service.entity.cart";
    public static final String CART_DATA_SOURCE = "cartDataSource";
    public static final String CART_ENTITY_MANAGER_FACTORY = "cartEntityManagerFactory";
    public static final String CART_TRANSACTION_MANAGER = "cartTransactionManager";

    //Config Untuk Product
    public static final String PRODUCT_DATASOURCE_PATH = "spring.datasource.product";
    public static final String PRODUCT_REPOSITORY = "com.cart.service.repository.product";
    public static final String PRODUCT_ENTITY = "com.cart.service.entity.product";
    public static final String PRODUCT_DATA_SOURCE = "productDataSource";
    public static final String PRODUCT_ENTITY_MANAGER_FACTORY = "productEntityManagerFactory";
    public static final String PRODUCT_TRANSACTION_MANAGER = "productTransactionManager";

    //Config Untuk Flash Sale
    public static final String FLASHSALE_DATASOURCE_PATH = "spring.datasource.flash-sale";
    public static final String FLASHSALE_REPOSITORY = "com.cart.service.repository.flashsale";
    public static final String FLASHSALE_ENTITY = "com.cart.service.entity.flashsale";
    public static final String FLASHSALE_DATA_SOURCE = "fsDataSource";
    public static final String FLASHSALE_ENTITY_MANAGER_FACTORY = "fsEntityManagerFactory";
    public static final String FLASHSALE_TRANSACTION_MANAGER = "fsTransactionManager";

    //Config Untuk User
    public static final String USER_DATASOURCE_PATH = "spring.datasource.user";
    public static final String USER_REPOSITORY = "com.cart.service.repository.user";
    public static final String USER_ENTITY = "com.cart.service.entity.user";
    public static final String USER_DATA_SOURCE = "usersDataSource";
    public static final String USER_ENTITY_MANAGER_FACTORY = "usersEntityManagerFactory";
    public static final String USER_TRANSACTION_MANAGER = "usersTransactionManager";

    //Cart Response
    public static final String CART_MESSAGE_WRONG_ROLE = "Role Anda Bukan User";

    //Audit Create
    public static final String AUDIT_CREATE_ACTION = "Create";
    public static final String AUDIT_CREATE_DESC_ACTION_SUCCESS = "Cart Baru Berhasil Dibuat";
    public static final String AUDIT_CREATE_DESC_ACTION_FAILED = "Gagal Membuat Cart";

    //Audit Update
    public static final String AUDIT_UPDATE_ACTION = "Update";
    public static final String AUDIT_UPDATE_DESC_ACTION_SUCCESS = "Cart Berhasil Di-Update";
    public static final String AUDIT_UPDATE_DESC_ACTION_FAILED = "Gagal Update Cart";

    //Audit Delete
    public static final String AUDIT_DELETE_ACTION = "Delete";
    public static final String AUDIT_DELETE_DESC_ACTION_SUCCESS = "Cart Berhasil Di-Delete";
    public static final String AUDIT_DELETE_DESC_ACTION_FAILED = "Gagal Delete Cart";

    //Exception
    public static final String PRODUCT_NOT_FOUND = "Product Tidak Ditemukan";
    public static final String USER_NOT_FOUND = "User Tidak Ditemukan";
    public static final String CART_NOT_FOUND = "Cart Tidak Ditemukan";
    public static final String FLASHSALE_NOT_FOUND = "Flash Sale Tidak Ditemukan";
    public static final String TRXFLASHSALE_NOT_FOUND = "Transaction Flash Sale Tidak Ditemukan";
    public static final String FLASHSALE_TIMEOUT = "Waktu Flash Sale Telah Usai";

}
