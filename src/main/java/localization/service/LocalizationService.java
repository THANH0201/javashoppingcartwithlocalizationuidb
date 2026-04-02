package localization.service;

import localization.dao.LocalizationStringDao;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class LocalizationService {

    private final LocalizationStringDao dao;

    public LocalizationService(LocalizationStringDao dao) {
        this.dao = dao;
    }

    public Map<String, String> getLocalizedStrings(Locale locale) {
        String lang = locale.getLanguage();
        Map<String, String> strings = dao.findAllByLanguage(lang);

        if (strings == null || strings.isEmpty()) {
            strings = getFallbackStrings();
        }

        return strings;
    }
    /**
     * Translate key → localized text
     */

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

}
