package model.service;

import model.entity.CartItemEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        // 2. Discount (10%)
        double afterDiscount = total * 0.9;
        double discountAmount = total - afterDiscount;

        // 3. Tax (24%)
        double afterTax = afterDiscount * 1.24;
        double taxAmount = afterTax - afterDiscount;

        result.put("total", round(total));
        result.put("afterDiscount", round(afterDiscount));
        result.put("discountAmount", round(discountAmount));
        result.put("afterTax", round(afterTax));
        result.put("taxAmount", round(taxAmount));

        return result;
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
