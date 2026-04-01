package localization.controller;

import javafx.scene.control.*;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

public abstract class LocalizationController {

    protected Locale currentLocale = Locale.getDefault();
    protected Map<String, String> localizedStrings;

    public void applyLocalization() {
        if (localizedStrings == null) return;

        Map<String, String> map = localizedStrings;

        Class<?> clazz = this.getClass();
        while (clazz != null) {

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Object node;
                try {
                    node = field.get(this);
                } catch (Exception e) {
                    continue;
                }
                if (node == null) continue;

                // 1. Prompt (TextField, PasswordField, TextArea)
                if (field.isAnnotationPresent(I18nAnnotations.Prompt.class)) {
                    String key = field.getAnnotation(I18nAnnotations.Prompt.class).value();
                    String value = map.getOrDefault(key, key);

                    if (node instanceof TextInputControl) {
                        ((TextInputControl) node).setPromptText(value);
                    }
                    continue;
                }

                // 2. Text (Label, Button, MenuItem, CheckBox, RadioButton, Tab…)
                if (field.isAnnotationPresent(I18nAnnotations.Text.class)) {
                    String key = field.getAnnotation(I18nAnnotations.Text.class).value();
                    String value = map.getOrDefault(key, key);

                    applyText(node, value);
                    continue;
                }

                // 3. Title (Stage, Dialog, TitledPane)
                if (field.isAnnotationPresent(I18nAnnotations.Title.class)) {
                    String key = field.getAnnotation(I18nAnnotations.Title.class).value();
                    String value = map.getOrDefault(key, key);

                    if (node instanceof Stage) {
                        ((Stage) node).setTitle(value);
                    } else {
                        tryInvoke(node, "setTitle", value);
                    }
                    continue;
                }

                // 4. Tooltip
                if (field.isAnnotationPresent(I18nAnnotations.Tooltip.class)) {
                    String key = field.getAnnotation(I18nAnnotations.Tooltip.class).value();
                    String value = map.getOrDefault(key, key);

                    if (node instanceof Tooltip) {
                        ((Tooltip) node).setText(value);
                    }
                    continue;
                }

                // 5. Header (Alert, Dialog)
                if (field.isAnnotationPresent(I18nAnnotations.Header.class)) {
                    String key = field.getAnnotation(I18nAnnotations.Header.class).value();
                    String value = map.getOrDefault(key, key);

                    if (node instanceof Alert) {
                        ((Alert) node).setHeaderText(value);
                    } else {
                        tryInvoke(node, "setHeaderText", value);
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    private void applyText(Object node, String value) {
        tryInvoke(node, "setText", value);
        tryInvoke(node, "setContentText", value);
    }

    private void tryInvoke(Object node, String method, String value) {
        try {
            node.getClass().getMethod(method, String.class).invoke(node, value);
        } catch (Exception ignored) {}
    }
}
