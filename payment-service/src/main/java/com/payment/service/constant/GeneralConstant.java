package com.payment.service.constant;

public class GeneralConstant {

    //Config Untuk Cart
    public static final String CART_DATASOURCE_PATH = "spring.datasource.cart";
    public static final String CART_REPOSITORY = "com.payment.service.repository.cart";
    public static final String CART_ENTITY = "com.payment.service.entity.cart";
    public static final String CART_DATA_SOURCE = "cartDataSource";
    public static final String CART_ENTITY_MANAGER_FACTORY = "cartEntityManagerFactory";
    public static final String CART_TRANSACTION_MANAGER = "cartTransactionManager";

    //Config Untuk Payment
    public static final String PAYMENT_DATASOURCE_PATH = "spring.datasource.payment";
    public static final String PAYMENT_REPOSITORY = "com.payment.service.repository.payment";
    public static final String PAYMENT_ENTITY = "com.payment.service.entity.payment";
    public static final String PAYMENT_DATA_SOURCE = "paymentDataSource";
    public static final String PAYMENT_ENTITY_MANAGER_FACTORY = "paymentEntityManagerFactory";
    public static final String PAYMENT_TRANSACTION_MANAGER = "paymentTransactionManager";

    //Config Untuk Product
    public static final String PRODUCT_DATASOURCE_PATH = "spring.datasource.product";
    public static final String PRODUCT_REPOSITORY = "com.payment.service.repository.product";
    public static final String PRODUCT_ENTITY = "com.payment.service.entity.product";
    public static final String PRODUCT_DATA_SOURCE = "productDataSource";
    public static final String PRODUCT_ENTITY_MANAGER_FACTORY = "productEntityManagerFactory";
    public static final String PRODUCT_TRANSACTION_MANAGER = "productTransactionManager";

    //Cart Response
    public static final String CART_NOT_FOUND = "Cart Tidak Ditemukan";

    //Payment Response
    public static final String PAYMENT_NOT_FOUND = "Payment Tidak Ditemukan";
    public static final String PAYMENT_ROLE_WRONG = "Role Anda Bukan User";

    //Audit Create
    public static final String AUDIT_CREATE_ACTION = "Create";
    public static final String AUDIT_CREATE_DESC_ACTION_SUCCESS = "Payment Baru Berhasil Dibuat";
    public static final String AUDIT_CREATE_DESC_ACTION_FAILED = "Gagal Membuat Payment";

    //Audit Get
    public static final String AUDIT_GET_ACTION = "Get";
    public static final String AUDIT_GET_DESC_ACTION_SUCCESS = "Payment Berhasil Diambil";
    public static final String AUDIT_GET_DESC_ACTION_FAILED = "Gagal Mengambil Payment";
}
