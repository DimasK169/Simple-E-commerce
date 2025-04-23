package com.cart.service.services;

import com.cart.service.dto.request.AuditTrailsRequest;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartGetResult;
import com.cart.service.dto.result.CartSaveResult;
import com.cart.service.entity.cart.CartEntity;
import com.cart.service.entity.flashsale.FlashSale;
import com.cart.service.entity.flashsale.TrxFlashSale;
import com.cart.service.entity.product.Product;
import com.cart.service.entity.user.Users;
import com.cart.service.repository.cart.CartRepository;
import com.cart.service.repository.flashsale.FlashSaleRepository;
import com.cart.service.repository.flashsale.TrxFlashSaleRepository;
import com.cart.service.repository.product.ProductRepository;
import com.cart.service.repository.user.UserRepository;
import com.cart.service.services.implementation.CartServiceImplementation;
import com.cart.service.services.services.AuditTrailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplementationTest {

    @InjectMocks
    private CartServiceImplementation cartService;

    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private FlashSaleRepository flashSaleRepository;
    @Mock private TrxFlashSaleRepository trxFlashSaleRepository;
    @Mock private AuditTrailsService auditTrailsService;

    private CartRequest cartRequest;
    private Users user;
    private Product product;
    private CartEntity cart;

    @BeforeEach
    void setup() {
        cartRequest = CartRequest.builder()
                .userEmail("user@example.com")
                .productCode("P001")
                .cartQuantity(1)
                .fsCode(null)
                .build();

        product = Product.builder()
                .productId(1l)
                .productCode("P001")
                .productName("Laptop")
                .productDescription("A good laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productImage("stored-laptop.png")
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdBy("admin")
                .createdDate(new Date())
                .build();

        user = Users.builder()
                .userId(1l)
                .userEmail("user@example.com")
                .userRole("Customer")
                .build();

        cart = CartEntity.builder()
                .cartId(1l)
                .userId(1l)
                .userEmail("user@example.com")
                .productId(1l)
                .productName("Laptop")
                .productCode("P001")
                .cartQuantity(10)
                .paymentNumber("N/A")
                .isPayed(false)
                .isFailed(false)
                .cartCreatedDate(new Date())
                .build();

    }

    @Test
    @DisplayName("Success Add Cart")
    void addToCart_withNoFlashSale_whenValid() throws Exception {

        when(productRepository.findProductByCode(eq(cartRequest.getProductCode())))
                .thenReturn(Optional.of(product));
        when(userRepository.findByUserEmail(eq("user@example.com")))
                .thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(eq(cartRequest.getUserEmail()), eq(cartRequest.getProductCode()), eq("N/A"), eq(false), eq(false)))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(CartEntity.class))).thenReturn(cart);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        RestApiResponse<CartSaveResult> response = cartService.addToCart("Customer", "user@example.com", cartRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Cart Baru Berhasil Dibuat");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getProductName()).isEqualTo("Laptop");
        assertThat(response.getCode()).isEqualTo("201 CREATED");

        verify(productRepository).save(any(Product.class));
        verify(productRepository).findProductByCode("P001");
        verify(userRepository).findByUserEmail("user@example.com");
        verify(cartRepository).save(any(CartEntity.class));
        verify(auditTrailsService).insertAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    @DisplayName("Success Add Cart With Flash Sale")
    void addToCart_shouldUseTrxPrice_whenFlashSaleValid() throws Exception {
        cartRequest.setFsCode("FS123");

        TrxFlashSale trx = new TrxFlashSale();
        trx.setFsCode("FS123");
        trx.setProductCode("P001");
        trx.setTrxPrice(500);

        FlashSale flashSale = new FlashSale();
        flashSale.setFsStartDate(Date.from(Instant.now().minusSeconds(3600)));
        flashSale.setFsEndDate(Date.from(Instant.now().plusSeconds(3600)));

        when(productRepository.findProductByCode(eq(cartRequest.getProductCode())))
                .thenReturn(Optional.of(product));
        when(userRepository.findByUserEmail(eq("user@example.com")))
                .thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(eq(cartRequest.getUserEmail()), eq(cartRequest.getProductCode()), eq("N/A"), eq(false), eq(false)))
                .thenReturn(Optional.empty());
        when(trxFlashSaleRepository.findByFsCodeAndProductCode(eq(cartRequest.getFsCode()), eq(cartRequest.getProductCode()))).thenReturn(Optional.of(trx));
        when(flashSaleRepository.findByFsCodeAndProductCode(eq(cartRequest.getFsCode()), eq(cartRequest.getProductCode()))).thenReturn(Optional.of(flashSale));

        when(cartRepository.save(any(CartEntity.class))).thenReturn(cart);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        RestApiResponse<CartSaveResult> response = cartService.addToCart("Customer", "user@example.com", cartRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Cart Baru Berhasil Dibuat");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getProductName()).isEqualTo("Laptop");
        assertThat(response.getCode()).isEqualTo("201 CREATED");

        verify(productRepository).save(any(Product.class));
        verify(productRepository).findProductByCode("P001");
        verify(userRepository).findByUserEmail("user@example.com");
        verify(cartRepository).save(any(CartEntity.class));
        verify(cartRepository).findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "N/A", false, false);
        verify(trxFlashSaleRepository).findByFsCodeAndProductCode("FS123", "P001");
        verify(flashSaleRepository).findByFsCodeAndProductCode("FS123", "P001");
        verify(auditTrailsService).insertAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    @DisplayName("Success Update Cart")
    void updateQuantityCart_withNoFlashSale_whenValid() throws Exception {

        when(cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(eq(cartRequest.getUserEmail()), eq(cartRequest.getProductCode()), eq("N/A"), eq(false), eq(false)))
                .thenReturn(Optional.of(cart));
        when(productRepository.findProductByCode(eq(cartRequest.getProductCode())))
                .thenReturn(Optional.of(product));

        when(cartRepository.save(any(CartEntity.class))).thenReturn(cart);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        RestApiResponse<CartSaveResult> response = cartService.updateQuantityCart("Customer", "user@example.com", cartRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Cart Berhasil Di-Update");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getProductName()).isEqualTo("Laptop");
        assertThat(response.getCode()).isEqualTo("200 OK");

        verify(productRepository).save(any(Product.class));
        verify(productRepository).findProductByCode("P001");
        verify(cartRepository).save(any(CartEntity.class));
        verify(cartRepository).findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "N/A", false, false);
        verify(auditTrailsService).insertAuditTrails(any(AuditTrailsRequest.class));

    }

    @Test
    @DisplayName("Success Update Cart With Flash Sale")
    void updateQuantityCart_withFlashSale_whenValid() throws Exception {

        cartRequest.setFsCode("123");
        cart.setFsCode("FS123");

        TrxFlashSale trx = new TrxFlashSale();
        trx.setFsCode("FS123");
        trx.setProductCode("P001");
        trx.setTrxPrice(500);

        FlashSale flashSale = new FlashSale();
        flashSale.setFsStartDate(Date.from(Instant.now().minusSeconds(3600)));
        flashSale.setFsEndDate(Date.from(Instant.now().plusSeconds(3600)));

        when(cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(eq(cartRequest.getUserEmail()), eq(cartRequest.getProductCode()), eq("N/A"), eq(false), eq(false)))
                .thenReturn(Optional.of(cart));
        when(productRepository.findProductByCode(eq(cartRequest.getProductCode())))
                .thenReturn(Optional.of(product));
        when(trxFlashSaleRepository.findByFsCodeAndProductCode(eq(cart.getFsCode()), eq(cart.getProductCode())))
                .thenReturn(Optional.of(trx));
        when(flashSaleRepository.findByFsCodeAndProductCode(eq(cart.getFsCode()), eq(cart.getProductCode())))
                .thenReturn(Optional.of(flashSale));

        when(cartRepository.save(any(CartEntity.class))).thenReturn(cart);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        RestApiResponse<CartSaveResult> response = cartService.updateQuantityCart("Customer", "user@example.com", cartRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Cart Berhasil Di-Update");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getProductName()).isEqualTo("Laptop");
        assertThat(response.getCode()).isEqualTo("200 OK");

        verify(productRepository).save(any(Product.class));
        verify(productRepository).findProductByCode("P001");
        verify(cartRepository).save(any(CartEntity.class));
        verify(cartRepository).findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "N/A", false, false);
        verify(trxFlashSaleRepository).findByFsCodeAndProductCode("FS123", "P001");
        verify(flashSaleRepository).findByFsCodeAndProductCode("FS123", "P001");
        verify(auditTrailsService).insertAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    @DisplayName("Success Delete Cart")
    void deleteCart_whenValid() throws Exception {
        when(cartRepository.findByUserEmailAndProductCodeAndPaymentNumber(eq(cartRequest.getUserEmail()), eq(cartRequest.getProductCode()), eq("N/A"), eq(false), eq(false)))
                .thenReturn(Optional.of(cart));
        when(productRepository.findProductByCode(eq(cart.getProductCode())))
                .thenReturn(Optional.of(product));

        RestApiResponse response = cartService.deleteCart("Customer", "user@example.com", cartRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Cart Berhasil Di-Delete");
        assertThat(response.getData()).isNull();
        assertThat(response.getCode()).isEqualTo("200 OK");

        verify(cartRepository).findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "N/A", false, false);
        verify(productRepository).findProductByCode("P001");
    }

    @Test
    @DisplayName("Success Get Cart")
    void getCartByUserEmailAndProductCode_whenValid() throws Exception {
        cart.setFsCode("FS123");
        cart.setCartTotalPrice(500);

        TrxFlashSale trx = new TrxFlashSale();
        trx.setFsCode("FS123");
        trx.setProductCode("P001");
        trx.setTrxPrice(500);

        when(cartRepository.findByUserEmail(eq(cartRequest.getUserEmail()), eq("N/A"), eq(false), eq(false)))
                .thenReturn(List.of(cart));
        when(productRepository.findProductByCode(eq(cart.getProductCode()))).thenReturn(Optional.of(product));
        when(trxFlashSaleRepository.findByFsCodeAndProductCode(eq(cart.getFsCode()), eq(cart.getProductCode())))
                .thenReturn(Optional.of(trx));

        RestApiResponse<List<CartGetResult>> response = cartService.getCartByUserEmailAndProductCode("Customer", "user@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Cart Berhasil Di-Fetch");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().get(0).getProductName()).isEqualTo("Laptop");
        assertThat(response.getCode()).isEqualTo("200 OK");

        verify(productRepository).findProductByCode("P001");
        verify(cartRepository).findByUserEmail("user@example.com", "N/A", false, false);
        verify(trxFlashSaleRepository).findByFsCodeAndProductCode("FS123", "P001");
        verify(auditTrailsService).insertAuditTrails(any(AuditTrailsRequest.class));
    }

}
