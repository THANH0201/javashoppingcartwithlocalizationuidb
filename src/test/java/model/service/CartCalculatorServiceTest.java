package model.service;

import model.entity.CartItemEntity;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CartCalculatorServiceTest {

    @Test
    void calculateTotal_shouldReturnCorrectValues() {
        // Arrange
        Map<String, CartItemEntity> cart = new HashMap<>();
        cart.put("item1", new CartItemEntity(1, 1,"A", 2, 10.0)); // subtotal = 20
        cart.put("item2", new CartItemEntity(2, 2, "B", 1.3, 5.5));  // subtotal = 7.15

        // Act
        Map<String, Double> result = CartCalculatorService.calculateTotal(cart);

        // Assert
        assertEquals(27.15, result.get("total"),0.01);
        assertEquals(24.44, result.get("afterDiscount"),0.01);
        assertEquals(2.72, result.get("discountAmount"),0.01);
        assertEquals(30.30, result.get("afterTax"),0.01);
        assertEquals(5.87, result.get("taxAmount"),0.01);
    }

    @Test
    void calculateTotal_shouldHandleEmptyCart() {
        // Arrange
        Map<String, CartItemEntity> cart = new HashMap<>();

        // Act
        Map<String, Double> result = CartCalculatorService.calculateTotal(cart);

        // Assert
        assertEquals(0.0, result.get("total"),0.01);
        assertEquals(0.0, result.get("afterDiscount"),0.01);
        assertEquals(0.0, result.get("discountAmount"),0.01);
        assertEquals(0.0, result.get("afterTax"),0.01);
        assertEquals(0.0, result.get("taxAmount"),0.01);
    }
}
