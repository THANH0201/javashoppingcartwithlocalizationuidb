package localization.controller;

import javafx.scene.control.*;
import javafx.stage.Stage;
import localization.utils.I18nAnnotations;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

public abstract class LocalizationController {

    protected Locale currentLocale = Locale.getDefault();
    protected Map<String, String> localizedStrings;

    public void applyLocalization() {
        if (localizedStrings == null) {
            return;
        }

        Map<String, String> map = localizedStrings;
        Class<?> clazz = this.getClass();

        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {

                // Required for accessing @FXML private fields
                field.setAccessible(true); // NOSONAR

                Object node = getFieldValue(field);
                if (node == null) {
                    continue;
                }

                if (field.isAnnotationPresent(I18nAnnotations.Prompt.class)) {
                    applyPrompt(field, node, map);
                } else if (field.isAnnotationPresent(I18nAnnotations.Text.class)) {
                    applyTextAnnotation(field, node, map);
                } else if (field.isAnnotationPresent(I18nAnnotations.Title.class)) {
                    applyTitle(field, node, map);
                } else if (field.isAnnotationPresent(I18nAnnotations.Tooltip.class)) {
                    applyTooltip(field, node, map);
                } else if (field.isAnnotationPresent(I18nAnnotations.Header.class)) {
                    applyHeader(field, node, map);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private Object getFieldValue(Field field) {
        try {
            return field.get(this);
        } catch (Exception e) {
            // Field not accessible or not initialized — safe to ignore
            return null;
        }
    }

    // --------------------------
    // 1. Prompt
    // --------------------------
    private void applyPrompt(Field field, Object node, Map<String, String> map) {
        String key = field.getAnnotation(I18nAnnotations.Prompt.class).value();
        String value = map.getOrDefault(key, key);

        if (node instanceof TextInputControl input) {
            input.setPromptText(value);
        }
    }

    // --------------------------
    // 2. Text
    // --------------------------
    private void applyTextAnnotation(Field field, Object node, Map<String, String> map) {
        String key = field.getAnnotation(I18nAnnotations.Text.class).value();
        String value = map.getOrDefault(key, key);
        applyText(node, value);
    }

    private void applyText(Object node, String value) {
        tryInvoke(node, "setText", value);
        tryInvoke(node, "setContentText", value);
    }

    // --------------------------
    // 3. Title
    // --------------------------
    private void applyTitle(Field field, Object node, Map<String, String> map) {
        String key = field.getAnnotation(I18nAnnotations.Title.class).value();
        String value = map.getOrDefault(key, key);

        if (node instanceof Stage stage) {
            stage.setTitle(value);
        } else {
            tryInvoke(node, "setTitle", value);
        }
    }

    // --------------------------
    // 4. Tooltip
    // --------------------------
    private void applyTooltip(Field field, Object node, Map<String, String> map) {
        String key = field.getAnnotation(I18nAnnotations.Tooltip.class).value();
        String value = map.getOrDefault(key, key);

        if (node instanceof Tooltip tooltip) {
            tooltip.setText(value);
        }
    }

    // --------------------------
    // 5. Header
    // --------------------------
    private void applyHeader(Field field, Object node, Map<String, String> map) {
        String key = field.getAnnotation(I18nAnnotations.Header.class).value();
        String value = map.getOrDefault(key, key);

        if (node instanceof Alert alert) {
            alert.setHeaderText(value);
        } else {
            tryInvoke(node, "setHeaderText", value);
        }
    }

    // --------------------------
    // Reflection helper
    // --------------------------
    private void tryInvoke(Object node, String method, String value) {
        try {
            node.getClass().getMethod(method, String.class).invoke(node, value);
        } catch (Exception ignored) {
            // Method not available on this node type — safe to ignore
        }
    }
}
