package localization.controller;

import java.lang.annotation.*;

public final class I18nAnnotations {

    // 1. Text for Label, Button, MenuItem, CheckBox, RadioButton, Tab...
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Text {
        String value();
    }

    // 2. Prompt for TextField, PasswordField, TextArea
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Prompt {
        String value();
    }

    // 3. Title for Stage, Dialog, TitledPane
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Title {
        String value();
    }

    // 4. Tooltip text
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Tooltip {
        String value();
    }

    // 5. Header or content text for Alert, Dialog
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Header {
        String value();
    }
}
