package com.cart.service;

import com.cart.service.dto.request.CartRequest;
import com.cart.service.entity.product.Product;
import com.cart.service.entity.user.Users;
import com.cart.service.exception.CustomIllegalArgumentException;
import com.cart.service.exception.UnauthorizedException;
import com.cart.service.repository.cart.CartRepository;
import com.cart.service.repository.flashsale.FlashSaleRepository;
import com.cart.service.repository.flashsale.TrxFlashSaleRepository;
import com.cart.service.repository.product.ProductRepository;
import com.cart.service.repository.user.UserRepository;
import com.cart.service.services.implementation.CartServiceImplementation;
import com.cart.service.services.services.AuditTrailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CartServiceImplementationTests {

	@InjectMocks
	private CartServiceImplementation cartService;

	@Mock
	private ProductRepository productRepository;

	@Mock private UserRepository userRepository;
	@Mock private CartRepository cartRepository;
	@Mock private FlashSaleRepository flashSaleRepository;
	@Mock private TrxFlashSaleRepository trxFlashSaleRepository;
	@Mock private AuditTrailsService auditTrailsService;

	private CartRequest cartRequest;
	private Users user;
	private Product product;

	@BeforeEach
	void setup() {
		cartRequest = CartRequest.builder()
				.productCode("P001")
				.cartQuantity(1)
				.fsCode(null)
				.build();

		product = new Product();
		product.setProductCode("P001");
		product.setProductPrice(1000);
		product.setProductStock(10);

		user = new Users();
		user.setUserEmail("user@example.com");
	}

	@Test
	void addToCart_shouldThrowException_whenUserRoleIsNotCustomer() {
		assertThrows(UnauthorizedException.class, () ->
				cartService.addToCart("Admin", "user@example.com", cartRequest));
	}

	@Test
	void addToCart_shouldThrowException_whenProductNotFound() {
		when(productRepository.findProductByCode("P001")).thenReturn(Optional.empty());

		CustomIllegalArgumentException ex = assertThrows(CustomIllegalArgumentException.class, () ->
				cartService.addToCart("Customer", "user@example.com", cartRequest));

		System.out.println(ex.getMessage());
		assertThat(ex.getMessage()).contains("Validation Error");
	}


}
