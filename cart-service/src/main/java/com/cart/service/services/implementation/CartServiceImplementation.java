package com.cart.service.services.implementation;

import com.cart.service.dto.result.CartUpdateResult;
import com.cart.service.entity.flashsale.FlashSale;
import com.cart.service.entity.flashsale.TrxFlashSale;
import com.cart.service.entity.product.Product;
import com.cart.service.entity.user.Users;
import com.cart.service.exception.CustomIllegalArgumentException;
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

    @Override
    public RestApiResponse<CartSaveResult> addToCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException {

        try {
            List<String> errors = new ArrayList<>();

            if (!jwtPayload.equals("User")){
                errors.add(CART_MESSAGE_WRONG_ROLE);
            }

            Optional<Product> product = productRepository.findProductByCode(cartRequest.getProductCode());
            Optional<Users> user = userRepository.findByUserEmail(cartRequest.getUserEmail());

            if (product.isEmpty()) errors.add(PRODUCT_NOT_FOUND);
            if (user.isEmpty()) errors.add(USER_NOT_FOUND);

            Product existingProduct = product.get();
            Users existingUser = user.get();

            Optional<CartEntity> existingCart = cartRepository.findByUserEmailAndProductCode(
                    existingUser.getUserEmail(), existingProduct.getProductCode(), false, false, false);

            //Melakukan penyaringan jika cart telah ada di dalam tabel
            if (existingCart.isPresent()) {
                return updateQuantityCart(jwtPayload, cartRequest);
            }

            CartEntity cartEntity = createCartEntity(cartRequest, existingUser, existingProduct);

            if (cartRequest.getFsCode() != null && !cartRequest.getFsCode().isEmpty()) {
                Instant now = Instant.now();
                Date dateNow = Date.from(now);

                Optional<TrxFlashSale> trxFlashSale = trxFlashSaleRepository.findByFsCodeAndProductCode(
                        cartRequest.getFsCode(), cartRequest.getProductCode());
                if (trxFlashSale.isEmpty()) errors.add(FLASHSALE_NOT_FOUND);

                Optional<FlashSale> flashSale = flashSaleRepository.findByFsCodeAndProductCode(cartRequest.getFsCode(), cartRequest.getProductCode());
                if (trxFlashSale.isEmpty()) errors.add(FLASHSALE_NOT_FOUND);

                TrxFlashSale trx = trxFlashSale.get();
                FlashSale fs = flashSale.get();

                if (dateNow.before(fs.getFsStartDate()) || dateNow.after(fs.getFsEndDate())) errors.add(FLASHSALE_TIMEOUT);
                if (!errors.isEmpty()) {
                    throw new CustomIllegalArgumentException("Validation Error", errors);
                }

                cartEntity.setTrxFlashSaleId(trx.getTrxFlashSaleId());
                cartEntity.setFsCode(trx.getFsCode());
                cartEntity.setCartTotalPrice(trx.getTrxPrice() * cartRequest.getCartQuantity());
            } else {
                cartEntity.setCartTotalPrice(existingProduct.getProductPrice() * cartRequest.getCartQuantity());
            }

            CartEntity savedCart = cartRepository.save(cartEntity);
            CartSaveResult result = buildCartSaveResult(savedCart);

            insertAuditTrail(cartRequest, result, AUDIT_CREATE_DESC_ACTION_SUCCESS);

            return RestApiResponse.<CartSaveResult>builder()
                    .code(HttpStatus.CREATED.toString())
                    .message(AUDIT_CREATE_DESC_ACTION_SUCCESS)
                    .data(result)
                    .error(null)
                    .build();

        } catch (IllegalArgumentException e) {
            insertAuditTrail(cartRequest, null, AUDIT_CREATE_DESC_ACTION_FAILED + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public RestApiResponse<CartSaveResult> updateQuantityCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException {

        try{
            List<String> errors = new ArrayList<>();
            List<CartSaveResult> responseList = new ArrayList<>();

            if (!jwtPayload.equals("User")){
                errors.add(CART_MESSAGE_WRONG_ROLE);
            }

            Optional<Product> product = productRepository.findProductByCode(cartRequest.getProductCode());
            if(product.isEmpty()) errors.add(PRODUCT_NOT_FOUND);

            Optional<CartEntity> cart = cartRepository.findByUserEmailAndProductCode(cartRequest.getUserEmail(), cartRequest.getProductCode(), false, false, false);
            if (cart.isEmpty()) {
                errors.add(CART_NOT_FOUND);
            } else {
                CartEntity updateCart = cart.get();
                updateCart.setCartQuantity(cartRequest.getCartQuantity());
                if (cartRequest.getFsCode() != null && !cartRequest.getFsCode().isEmpty()){
                    Instant now = Instant.now();
                    Date dateNow = Date.from(now);

                    Optional<FlashSale> flashSale = flashSaleRepository.findByFsCodeAndProductCode(cart.get().getFsCode(), cart.get().getProductCode());
                    if (flashSale.isEmpty()) errors.add(FLASHSALE_NOT_FOUND);

                    Optional<TrxFlashSale> trxFlashSale = trxFlashSaleRepository.findByFsCodeAndProductCode(cart.get().getFsCode(), cart.get().getProductCode());
                    if (trxFlashSale.isEmpty()) errors.add(TRXFLASHSALE_NOT_FOUND);

//                    if (!errors.isEmpty()) {
//                        throw new CustomIllegalArgumentException("Validation Error", errors);
//                    }

                    FlashSale fs = flashSale.get();
                    TrxFlashSale trx = trxFlashSale.get();

                    if (updateCart.getFsCode().equals(flashSale.get().getFsCode())){
                        if (dateNow.before(fs.getFsStartDate()) || dateNow.after(fs.getFsEndDate())){
                            errors.add(FLASHSALE_TIMEOUT);
                        } else {
                            updateCart.setCartTotalPrice(trx.getTrxPrice() * updateCart.getCartQuantity());
                        }
                    }
                }
                updateCart.setCartTotalPrice(product.get().getProductPrice() * updateCart.getCartQuantity());
                updateCart.setCartUpdatedDate(new Date());
                CartEntity updatedCart = cartRepository.save(updateCart);

                CartSaveResult result = buildCartSaveResult(updatedCart);
                responseList.add(result);

                insertAuditTrail(cartRequest, result, AUDIT_UPDATE_DESC_ACTION_SUCCESS);

                return RestApiResponse.<CartSaveResult>builder()
                        .code(HttpStatus.OK.toString())
                        .message(AUDIT_UPDATE_DESC_ACTION_SUCCESS)
                        .data(result)
                        .error(null)
                        .build();
            }

            throw new CustomIllegalArgumentException("Validation failed", errors);

        } catch (IllegalArgumentException e){
            insertAuditTrail(cartRequest, null, AUDIT_UPDATE_DESC_ACTION_FAILED);
            throw e;
        }

    }

    @Override
    public RestApiResponse<List<CartUpdateResult>> updateReadyStateCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException {

        try {

            List<String> errors = new ArrayList<>();

            if (!"User".equals(jwtPayload)) errors.add(CART_MESSAGE_WRONG_ROLE);

            List<CartEntity> cartList = cartRepository.findByUserEmail(cartRequest.getUserEmail(), false, false, false);
            if (cartList.isEmpty()) errors.add(CART_NOT_FOUND);

            List<CartUpdateResult> resultList = new ArrayList<>();

            for (CartEntity cart : cartList){
                cart.setIsReadyToPay(true);
                cart.setCartUpdatedDate(new Date());
                CartEntity updatedCart = cartRepository.save(cart);

                CartUpdateResult result = buildCartUpdateResult(updatedCart);
                resultList.add(result);

                List<Product> productList = productRepository.findManyProductByCode(cart.getProductCode());
                if (productList.isEmpty()) errors.add(PRODUCT_NOT_FOUND);

                for (Product updateProduct: productList){
                    Integer updatedStocks = updateProduct.getProductStock() - cart.getCartQuantity();
                    updateProduct.setProductStock(updatedStocks);
                    updateProduct.setUpdatedDate(new Date());
                    Product save = productRepository.save(updateProduct);
                }

                insertAuditTrail(cartRequest, resultList, AUDIT_UPDATE_DESC_ACTION_SUCCESS);

            }

            if (!errors.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", errors);

            return RestApiResponse.<List<CartUpdateResult>>builder()
                    .code(HttpStatus.OK.toString())
                    .message(AUDIT_UPDATE_DESC_ACTION_SUCCESS)
                    .data(resultList)
                    .error(null)
                    .build();

        } catch (IllegalArgumentException e){
            insertAuditTrail(cartRequest, null, AUDIT_UPDATE_DESC_ACTION_FAILED);
            throw e;
        }

    }

    @Override
    @Transactional
    public RestApiResponse deleteCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException {

        try {
            if (!"User".equals(jwtPayload)){
                throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_MESSAGE_WRONG_ROLE));
            }

            Optional<CartEntity> cart = cartRepository.findByUserEmailAndProductCode(cartRequest.getUserEmail(), cartRequest.getProductCode(), false, false,false);
            if (cart.isEmpty()){
                throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
            }

            cartRepository.delete(cart.get());

            insertAuditTrail(cartRequest, null, AUDIT_DELETE_DESC_ACTION_SUCCESS);

            return RestApiResponse.builder()
                    .code(HttpStatus.OK.toString())
                    .message(AUDIT_DELETE_DESC_ACTION_SUCCESS)
                    .data(null)
                    .error(null)
                    .build();

        } catch (IllegalArgumentException e){
            insertAuditTrail(cartRequest, null, AUDIT_DELETE_DESC_ACTION_FAILED);
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
                .isReadyToPay(false)
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
                .isReadyToPay(cart.getIsReadyToPay())
                .isPayed(cart.getIsPayed())
                .isFailed(cart.getIsFailed())
                .cartCreatedDate(cart.getCartCreatedDate())
                .build();
    }

    private CartUpdateResult buildCartUpdateResult(CartEntity cart){
        return CartUpdateResult.builder()
                .userId(cart.getUserId())
                .userEmail(cart.getUserEmail())
                .productId(cart.getProductId())
                .productName(cart.getProductName())
                .cartQuantity(cart.getCartQuantity())
                .cartTotalPrice(cart.getCartTotalPrice())
                .isReadyToPay(cart.getIsReadyToPay())
                .cartUpdatedDate(cart.getCartUpdatedDate())
                .build();
    }

    private void insertAuditTrail(CartRequest request, Object response, String description) throws JsonProcessingException {
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

        auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                .auditTrailsAction(AUDIT_CREATE_ACTION)
                .auditTrailsDescription(description)
                .auditTrailsDate(new Date())
                .auditTrailsRequest(objectMapper.writeValueAsString(request))
                .auditTrailsResponse(result)
                .build());
    }

}
