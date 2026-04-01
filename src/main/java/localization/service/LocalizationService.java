package localization.service;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import localization.dao.LocalizationStringDao;

public class LocalizationService {

    private static Map<String, String> currentStrings;
    private static final LocalizationStringDao dao = new LocalizationStringDao();
    private LocalizationService() {}

    /**
     * Load all localized strings from database for the given locale.
     */
    public static Map<String, String> getLocalizedStrings(Locale locale) {
        String lang = locale.getLanguage();

        Map<String, String> strings = dao.findAllByLanguage(lang);

        if (strings == null || strings.isEmpty()) {
            strings = getFallbackStrings();
        }

        currentStrings = strings;
        return strings;
    }

    private static Map<String, String> getFallbackStrings() {
        Map<String, String> strings = new LinkedHashMap<>();
        strings.put("shopping.title", "Shopping Cart");
        strings.put("shopping.select.item", "Select Item");
        strings.put("shopping.enter.price", "Enter Price");
        strings.put("shopping.enter.quantity", "Enter Quantity");
        strings.put("shopping.add", "Add to Cart");
        strings.put("shopping.cart", "Cart");
        strings.put("shopping.total", "Total:");
        strings.put("shopping.discount", "Discount:");
        strings.put("total.after.discount", "After Discount:");
        strings.put("shopping.tax", "Tax:");
        strings.put("total.after.tax", "After Tax:");
        strings.put("shopping.final.total", "Final Total:");
        strings.put("shopping.calculate", "Calculate");
        strings.put("shopping.pay", "Pay");
        strings.put("shopping.back", "Back");
        strings.put("error.invalid_input", "Please enter valid numbers");
        strings.put("thanks", "Thank you for your purchase!");
        strings.put("sorry", "Payment cancelled.");
        return strings;
    }

    /**
     * Translate key → localized text
     */
    public static String t(String key) {
        if (currentStrings == null) return key;
        return currentStrings.getOrDefault(key, key);
    }

}
