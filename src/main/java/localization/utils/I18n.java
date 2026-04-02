package localization.utils;

import java.util.Map;

public final class I18n {

    private static Map<String, String> strings;

    private I18n() {}

    public static void load(Map<String, String> localizedStrings) {
        strings = localizedStrings;
    }

    public static String t(String key) {
        if (strings == null) return key;
        return strings.getOrDefault(key, key);
    }
}
