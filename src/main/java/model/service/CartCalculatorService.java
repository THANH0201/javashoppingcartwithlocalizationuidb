package model.service;

import model.entity.CartItemEntity;

import java.util.HashMap;
import java.util.Map;

public class CartCalculatorService {

    private CartCalculatorService() {}

    public static Map<String, Double> calculateTotal(Map<String, CartItemEntity> cart) {

        Map<String, Double> result = new HashMap<>();

        // 1. Total
        double total = cart.values().stream()
                .mapToDouble(CartItemEntity::getSubtotal)
                .sum();
        result.put("total", total);

        // 2. Discount (10%)
        double afterDiscount = total * 0.9;
        result.put("afterDiscount", afterDiscount);
        result.put("discountAmount", total - afterDiscount);

        // 3. Tax (24%)
        double afterTax = afterDiscount * 1.24;
        result.put("afterTax", afterTax);
        result.put("taxAmount", afterTax - afterDiscount);

        return result;
    }
}
