package com.cart.service.services.implementation;

import com.cart.service.dto.result.CartGetResult;
import com.cart.service.entity.flashsale.FlashSale;
import com.cart.service.entity.flashsale.TrxFlashSale;
import com.cart.service.entity.product.Product;
import com.cart.service.entity.user.Users;
import com.cart.service.exception.CustomIllegalArgumentException;
import com.cart.service.exception.UnauthorizedException;
import com.cart.service.repository.flashsale.FlashSaleRepository;
import com.cart.service.repository.flashsale.TrxFlashSaleRepository;
import com.cart.service.repository.product.ProductRepository;
import com.cart.service.repository.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cart.service.dto.request.AuditTrailsRequest;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import com.cart.service.entity.cart.CartEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.cart.service.repository.cart.CartRepository;
import com.cart.service.services.services.AuditTrailsService;
import com.cart.service.services.services.CartService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static com.cart.service.constant.GeneralConstant.*;

@Service
@RequiredArgsConstructor
public class CartServiceImplementation implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TrxFlashSaleRepository trxFlashSaleRepository;

    @Autowired
    FlashSaleRepository flashSaleRepository;

    @Autowired
    AuditTrailsService auditTrailsService;

    @Value("${images.baseUrl}")
    private String baseUrl;

    @Override
    public RestApiResponse<CartSaveResult> addToCart(String userRole, String userEmail, CartRequest cartRequest) throws JsonProcessingException {

        try {

            System.out.println(cartRequest);

            if (!userRole.equals("Customer")) throw new UnauthorizedException(CART_NOT_FOUND);

            Optional<Product> product = productRepository.findProductByCode(cartRequest.getProductCode());
            if (product.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(PRODUCT_NOT_FOUND));
            Product existingProduct = product.get();

            Optional<Users> user = userRepository.findByUserEmail(userEmail);
            if (user.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(USER_NOT_FOUND));
            Users existingUser = user.get();

            System.out.println(existingUser.getUserEmail() + " " + existingProduct.getProductCode());
            Optional<CartEntity> cart = cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(
                    existingUser.getUserEmail(), existingProduct.getProductCode(), "N/A", false, false);
            System.out.println(cart);
            //Melakukan penyaringan jika cart telah ada di dalam tabel
            if (cart.isPresent()) {
                CartEntity existingCart = cart.get();
                int updatedQuantity = existingCart.getCartQuantity() + 1;
                cartRequest.setCartQuantity(updatedQuantity);
                return updateQuantityCart(userRole, userEmail, cartRequest);
            }

            CartEntity cartEntity = createCartEntity(cartRequest, existingUser, existingProduct);

            if (cartRequest.getFsCode() != null && !cartRequest.getFsCode().isEmpty()) {
                Instant now = Instant.now();
                Date dateNow = Date.from(now);

                Optional<TrxFlashSale> trxFlashSale = trxFlashSaleRepository.findByFsCodeAndProductCode(
                        cartRequest.getFsCode(), cartRequest.getProductCode());
                if (trxFlashSale.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(FLASHSALE_NOT_FOUND));
                TrxFlashSale trx = trxFlashSale.get();

                System.out.println(cartRequest.getFsCode() + cartRequest.getProductCode());
                Optional<FlashSale> flashSale = flashSaleRepository.findByFsCodeAndProductCode(cartRequest.getFsCode(), cartRequest.getProductCode());
                if (trxFlashSale.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(TRXFLASHSALE_NOT_FOUND));
                FlashSale fs = flashSale.get();

                if (dateNow.before(fs.getFsStartDate()) || dateNow.after(fs.getFsEndDate())) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(FLASHSALE_TIMEOUT));

                cartEntity.setTrxFlashSaleId(trx.getTrxFlashSaleId());
                cartEntity.setFsCode(trx.getFsCode());
                cartEntity.setCartTotalPrice(trx.getTrxPrice() * cartRequest.getCartQuantity());
            } else {
                cartEntity.setCartTotalPrice(existingProduct.getProductPrice() * cartRequest.getCartQuantity());
            }

            CartEntity savedCart = cartRepository.save(cartEntity);
            CartSaveResult result = buildCartSaveResult(savedCart);

            Integer updateStock = existingProduct.getProductStock() - cartRequest.getCartQuantity();
            existingProduct.setProductStock(updateStock);
            if (updateStock == 0){
                existingProduct.setProductIsAvailable(false);
            }
            productRepository.save(existingProduct);

            insertAuditTrail(cartRequest, result, AUDIT_CREATE_DESC_ACTION_SUCCESS, AUDIT_CREATE_ACTION);

            return RestApiResponse.<CartSaveResult>builder()
                    .code(HttpStatus.CREATED.toString())
                    .message(AUDIT_CREATE_DESC_ACTION_SUCCESS)
                    .data(result)
                    .error(null)
                    .build();

        } catch (IllegalArgumentException e) {
            insertAuditTrail(cartRequest, null, AUDIT_CREATE_DESC_ACTION_FAILED, AUDIT_CREATE_ACTION);
            throw e;
        }
    }

    @Override
    public RestApiResponse<CartSaveResult> updateQuantityCart(String userRole, String userEmail, CartRequest cartRequest) throws JsonProcessingException {

        try{
            List<CartSaveResult> responseList = new ArrayList<>();

            if (!userRole.equals("Customer")) throw new UnauthorizedException(CART_MESSAGE_WRONG_ROLE);

            System.out.println(userEmail + " " + cartRequest.getProductCode());
            Optional<CartEntity> cart = cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(userEmail, cartRequest.getProductCode(), "N/A", false, false);
            if (cart.isEmpty()) {
                throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
            } else {

                Optional<Product> product = productRepository.findProductByCode(cartRequest.getProductCode());
                if(product.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(PRODUCT_NOT_FOUND));
                Product existingProduct = product.get();

                CartEntity updateCart = cart.get();
                int oldQuantity = updateCart.getCartQuantity();
                int newQuantity = cartRequest.getCartQuantity();
                int quantityDiff = newQuantity - oldQuantity;

                updateCart.setCartQuantity(newQuantity);

                if (cartRequest.getFsCode() != null && !cartRequest.getFsCode().isEmpty()) {
                    Optional<FlashSale> flashSale = flashSaleRepository.findByFsCodeAndProductCode(cart.get().getFsCode(), cart.get().getProductCode());
                    if (flashSale.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(FLASHSALE_NOT_FOUND));
                    FlashSale fs = flashSale.get();

                    Optional<TrxFlashSale> trxFlashSale = trxFlashSaleRepository.findByFsCodeAndProductCode(cart.get().getFsCode(), cart.get().getProductCode());
                    if (trxFlashSale.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(TRXFLASHSALE_NOT_FOUND));
                    TrxFlashSale trx = trxFlashSale.get();

                    Instant now = Instant.now();
                    Date dateNow = Date.from(now);

                    if (updateCart.getFsCode().equals(fs.getFsCode())) {
                        if (dateNow.before(fs.getFsStartDate()) || dateNow.after(fs.getFsEndDate())) {
                            throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(FLASHSALE_TIMEOUT));
                        } else {
                            updateCart.setCartTotalPrice(trx.getTrxPrice() * cartRequest.getCartQuantity());
                        }
                    } else {
                        updateCart.setCartTotalPrice(existingProduct.getProductPrice() * cartRequest.getCartQuantity());
                    }
                } else {
                    updateCart.setCartTotalPrice(existingProduct.getProductPrice() * cartRequest.getCartQuantity());
                }

                updateCart.setCartUpdatedDate(new Date());
                CartEntity updatedCart = cartRepository.save(updateCart);
                int newStock = existingProduct.getProductStock() - quantityDiff;
                if (newStock == 0){
                    existingProduct.setProductIsAvailable(false);
                } else {
                    existingProduct.setProductIsAvailable(true);
                }
                if (newStock < 0) {
                    throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList("Stok tidak mencukupi"));
                }
                existingProduct.setProductStock(newStock);
                productRepository.save(existingProduct);

                CartSaveResult result = buildCartSaveResult(updatedCart);
                result.setCartUpdatedDate(updatedCart.getCartUpdatedDate());
                responseList.add(result);

                insertAuditTrail(cartRequest, result, AUDIT_UPDATE_DESC_ACTION_SUCCESS, AUDIT_UPDATE_ACTION);

                return RestApiResponse.<CartSaveResult>builder()
                        .code(HttpStatus.OK.toString())
                        .message(AUDIT_UPDATE_DESC_ACTION_SUCCESS)
                        .data(result)
                        .error(null)
                        .build();
            }

        } catch (IllegalArgumentException e){
            insertAuditTrail(cartRequest, null, AUDIT_UPDATE_DESC_ACTION_FAILED, AUDIT_UPDATE_ACTION);
            throw e;
        }

    }

    @Override
    public RestApiResponse deleteCart(String userRole, String userEmail, CartRequest cartRequest) throws JsonProcessingException {

        try {
            if (!"Customer".equals(userRole)) throw new UnauthorizedException(CART_MESSAGE_WRONG_ROLE);

            Optional<CartEntity> cart = cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(userEmail, cartRequest.getProductCode(), "N/A", false,false);
            if (cart.isEmpty()){
                throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
            }
            CartEntity existingCart = cart.get();

            Optional<Product> product = productRepository.findProductByCode(existingCart.getProductCode());
            if (product.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(PRODUCT_NOT_FOUND));
            Product existingProduct = product.get();

            Integer updateStock = existingProduct.getProductStock() + existingCart.getCartQuantity();
            existingProduct.setProductStock(updateStock);
            existingProduct.setProductIsAvailable(true);
            productRepository.save(existingProduct);

            cartRepository.delete(cart.get());

            insertAuditTrail(cartRequest, null, AUDIT_DELETE_DESC_ACTION_SUCCESS, AUDIT_DELETE_ACTION);

            return RestApiResponse.builder()
                    .code(HttpStatus.OK.toString())
                    .message(AUDIT_DELETE_DESC_ACTION_SUCCESS)
                    .data(null)
                    .error(null)
                    .build();

        } catch (IllegalArgumentException e){
            insertAuditTrail(cartRequest, null, AUDIT_DELETE_DESC_ACTION_FAILED, AUDIT_DELETE_ACTION);
            throw e;
        }
    }

    @Override
    public RestApiResponse<List<CartGetResult>> getCartByUserEmailAndProductCode(String userRole, String userEmail)throws JsonProcessingException{
        try{
            if (!"Customer".equals(userRole)) throw new UnauthorizedException(CART_MESSAGE_WRONG_ROLE);

            List<CartGetResult> result = new ArrayList<>();

            List<CartEntity> cart = cartRepository.findByUserEmail(userEmail, "N/A", false, false);
            if (cart.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));

            Integer totalPrice = 0;
            for (CartEntity getCart : cart){
                Optional<Product> product = productRepository.findProductByCode(getCart.getProductCode());
                if (product.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(PRODUCT_NOT_FOUND));

                Product getProduct = product.get();

                totalPrice += getCart.getCartTotalPrice();

                CartGetResult resultAudit = buildCartGetResult(getCart, getProduct);

                if (getCart.getFsCode() != null && !getCart.getFsCode().isEmpty()){
                    Optional<TrxFlashSale> trxFlashSale = trxFlashSaleRepository.findByFsCodeAndProductCode(getCart.getFsCode(), getCart.getProductCode());
                    if (trxFlashSale.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(TRXFLASHSALE_NOT_FOUND));

                    resultAudit.setProductPrice(trxFlashSale.get().getTrxPrice());
                }

                result.add(resultAudit);

                insertAuditTrail(null , resultAudit, AUDIT_GET_DESC_ACTION_SUCCESS ,AUDIT_GET_ACTION);

            }

            CartGetResult cartTotalPrice = CartGetResult.builder().cartTotalPrice(totalPrice).build();
            result.add(cartTotalPrice);

            return RestApiResponse.<List<CartGetResult>>builder()
                    .code(HttpStatus.OK.toString())
                    .message(AUDIT_GET_DESC_ACTION_SUCCESS)
                    .data(result)
                    .build();

        } catch (IllegalArgumentException e){
            throw e;
        }
    }

    private CartEntity createCartEntity(CartRequest request, Users user, Product product) {
        return CartEntity.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .cartQuantity(request.getCartQuantity())
                .paymentNumber("N/A")
                .isPayed(false)
                .isFailed(false)
                .cartCreatedDate(new Date())
                .build();
    }

    private CartSaveResult buildCartSaveResult(CartEntity cart) {
        return CartSaveResult.builder()
                .userEmail(cart.getUserEmail())
                .productName(cart.getProductName())
                .fsCode(cart.getFsCode())
                .cartQuantity(cart.getCartQuantity())
                .cartTotalPrice(cart.getCartTotalPrice())
                .isPayed(cart.getIsPayed())
                .isFailed(cart.getIsFailed())
                .cartCreatedDate(cart.getCartCreatedDate())
                .build();
    }

    private CartGetResult buildCartGetResult(CartEntity cart, Product product){
        return CartGetResult.builder()
                .productName(cart.getProductName())
                .productDesc(product.getProductDescription())
                .productImage(baseUrl + "images/" + product.getProductImage())
                .productPrice(product.getProductPrice())
                .cartTotalPricePerItem(cart.getCartTotalPrice())
                .cartQuantity(cart.getCartQuantity())
                .productStock(product.getProductStock())
                .fsCode(cart.getFsCode())
                .productCode(cart.getProductCode())
                .createdDate(new Date())
                .build();
    }

    private void insertAuditTrail(CartRequest request, Object response, String description, String action) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String result = null;
        if (response != null) {
            if (response instanceof List<?>) {
                List<?> list = (List<?>) response;
                if (!list.isEmpty()) {
                    result = objectMapper.writeValueAsString(list);
                } else {
                    result = "[]";
                }
            } else {
                result = objectMapper.writeValueAsString(response);
            }
        }

        if (action.equals(AUDIT_CREATE_ACTION) ||
                action.equals(AUDIT_UPDATE_ACTION) ||
                action.equals(AUDIT_DELETE_ACTION) || action.equals(AUDIT_GET_ACTION)) {

            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .auditTrailsAction(action)
                    .auditTrailsDescription(description)
                    .auditTrailsDate(new Date())
                    .auditTrailsRequest(objectMapper.writeValueAsString(request))
                    .auditTrailsResponse(result)
                    .build());
        }

    }

}
