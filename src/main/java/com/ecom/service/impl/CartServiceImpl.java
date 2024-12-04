package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {

		// Fetch user and product, with proper null handling
		UserDtls userDtls = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

		// Check if the product already exists in the cart for the user
		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

		Cart cart;

		if (cartStatus == null) {
			// Create a new cart entry
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userDtls);
			cart.setQuantity(1);
			cart.setTotalPrice(product.getDiscountPrice());
		} else {
			// Update existing cart entry
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(cart.getQuantity() * product.getDiscountPrice());
		}

		// Save and return the updated or new cart
		return cartRepository.save(cart);
	}

	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		// Fetch all carts for the user
		List<Cart> carts = cartRepository.findByUserId(userId);

		Double totalOrderPrice = 0.0;
		List<Cart> updatedCarts = new ArrayList<>();

		for (Cart cart : carts) {
			Double totalPrice = cart.getProduct().getDiscountPrice() * cart.getQuantity();
			cart.setTotalPrice(totalPrice);
			totalOrderPrice += totalPrice;
			cart.setTotalOrderPrice(totalOrderPrice);
			updatedCarts.add(cart);
		}

		return updatedCarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		return cartRepository.countByUserId(userId);
	}

	@Override
	public void updateQuantity(String action, Integer cartId) {

		// Fetch cart entry by ID
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));

		int updatedQuantity;

		if ("de".equalsIgnoreCase(action)) {
			// Decrease quantity
			updatedQuantity = cart.getQuantity() - 1;

			if (updatedQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updatedQuantity);
				cart.setTotalPrice(updatedQuantity * cart.getProduct().getDiscountPrice());
				cartRepository.save(cart);
			}

		} else if ("in".equalsIgnoreCase(action)) {
			// Increase quantity
			updatedQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updatedQuantity);
			cart.setTotalPrice(updatedQuantity * cart.getProduct().getDiscountPrice());
			cartRepository.save(cart);
		} else {
			throw new IllegalArgumentException("Invalid action: " + action);
		}
	}
}
