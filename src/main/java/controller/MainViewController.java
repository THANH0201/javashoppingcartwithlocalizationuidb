package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import localization.utils.I18n;
import localization.utils.I18nAnnotations;
import localization.dao.LanguageDao;
import localization.dao.LocalizationStringDao;
import localization.entity.Language;
import model.dao.CartItemDao;
import model.dao.CartRecordDao;
import model.entity.CartItemEntity;
import model.entity.CartRecordEntity;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import localization.controller.LocalizationController;
import model.service.CartCalculatorService;
import localization.service.LocalizationService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static localization.utils.I18n.t;

public class MainViewController extends LocalizationController {
    static Logger logger = Logger.getLogger(MainViewController.class.getName());
    // UI elements
    @I18nAnnotations.Text("time")
    @FXML
    private Label time;
    @FXML
    private Label utcTimeLabel;

    @FXML
    private VBox itemSection;
    @FXML
    private VBox rootVBox;
    @I18nAnnotations.Text("language.current")
    @FXML
    private MenuButton languageMenu;
    @I18nAnnotations.Text("titleLabel")
    @FXML
    private Label titleLabel;
    @FXML
    private ListView<String> itemList;
    @I18nAnnotations.Prompt("shopping.enter.price")
    @FXML
    private TextField priceField;
    @I18nAnnotations.Prompt("shopping.enter.quantity")
    @FXML
    private TextField quantityField;
    @I18nAnnotations.Text("shopping.add")
    @FXML
    private Button addButton;
    @FXML
    private VBox cartSection;
    @FXML
    private Button cartButton;
    @I18nAnnotations.Text("shopping.cart")
    @FXML
    private Label cartText;
    @FXML
    private Label totalItem;
    @FXML
    private ListView<String> cartList;
    @I18nAnnotations.Text("shopping.calculate")
    @FXML
    private Button calButton;
    @FXML
    private VBox priceSummaryBox;
    @I18nAnnotations.Text("shopping.total")
    @FXML
    private Label totalLabelText;
    @I18nAnnotations.Text("shopping.discount")
    @FXML
    private Label discountLabelText;
    @I18nAnnotations.Text("total.after.discount")
    @FXML
    private Label afterDiscountLabelText;
    @I18nAnnotations.Text("shopping.tax")
    @FXML
    private Label taxLabelText;
    @I18nAnnotations.Text("total.after.tax")
    @FXML
    private Label afterTaxLabelText;
    @I18nAnnotations.Text("shopping.final.total")
    @FXML
    private Label finalTotalLabelText;

    @FXML
    private Label totalLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label afterDiscountLabel;
    @FXML
    private Label taxLabel;
    @FXML
    private Label afterTaxLabel;
    @FXML
    private Label finalTotalLabel;

    @I18nAnnotations.Text("shopping.pay")
    @FXML
    private Button payButton;
    @I18nAnnotations.Text("shopping.back")
    @FXML
    private Button backButton;

    private final CartRecordDao recordDao = new CartRecordDao();
    private final CartItemDao itemDao = new CartItemDao();
    private final Map<String, CartItemEntity> cart = new HashMap<>();
    private final LocalizationStringDao localizationDao = new LocalizationStringDao();
    private final LocalizationService localizationService = new LocalizationService(localizationDao);
    String error = "error.invalid_input";
    /**
     * Initialize controller
     */
    @FXML
    public void initialize() {
        loadLanguageMenu();

        setLanguage(currentLocale);

        itemList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String key, boolean empty) {
                super.updateItem(key, empty);
                if (empty || key == null) {
                    setText(null);
                } else {
                    setText(t(key));
                }
            }
        });

        cartSection.setVisible(false);

        startUtcClock();

    }

    private void startUtcClock() {
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {

                    // zone of user
                    ZoneId localZone = ZoneId.systemDefault();

                    // get name of city
                    String city = localZone.getId().substring(localZone.getId().indexOf('/') + 1)
                            .replace("_", " ");

                    // UTC time
                    ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));

                    // Format
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

                    String text = " " + city + " – " + "(UTC: " + utc.format(fmt) + ")";

                    utcTimeLabel.setText(text);
                }),
                new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Load language menu dynamically
     */
    private void loadLanguageMenu() {
        LanguageDao dao = new LanguageDao();
        List<Language> languages = dao.findAll();

        languageMenu.getItems().clear();

        for (Language lang : languages) {
            MenuItem item = new MenuItem(lang.getName());

            // create Locale from code
            Locale locale = Locale.forLanguageTag(lang.getCode());
            // Font Lao
            if (lang.getCode().equals("lo")) {
                item.setStyle("-fx-font-family: 'Noto Sans Lao';");
            }

            item.setOnAction(e -> setLanguage(locale));
            languageMenu.getItems().add(item);
        }
    }

    /**
     * Choose language
     */
    @FXML
    public void switchToLanguageCurrent() {
        setLanguage(currentLocale);
    }

    /**
     * Apply localization
     */
    private void setLanguage(Locale locale) {
        currentLocale = locale;
        localizedStrings = localizationService.getLocalizedStrings(locale);
        I18n.load(localizedStrings);
        applyLocalization();

        // Reset font
        rootVBox.setStyle("");

        // Laos
        if (locale.getLanguage().equals("lo")) {
            rootVBox.setStyle("-fx-font-family: 'Noto Sans Lao';");
        }
        // Normal
        else {
            rootVBox.setStyle("-fx-font-family: 'Inter';");
        }
        loadItemList();

        applyTextDirection(locale);
    }

    /**
     * Load items into ListView
     */
    @FXML
    private void loadItemList() {
        itemList.getItems().clear();
        // default
        itemList.getItems().add("shopping.select.item");

        localizedStrings.keySet().stream()
                .filter(key -> key.startsWith("item."))
                .sorted()
                .forEach(key -> itemList.getItems().add(key));
    }

    /**
     * Action buttons
     */
    @FXML
    private void onAddItem() {
        try {
            String key = itemList.getSelectionModel().getSelectedItem();

            if (key == null || key.equals("shopping.select.item")) {
                showAlert(t(error));
                return;
            }

            double price = Double.parseDouble(priceField.getText());
            double qty = Double.parseDouble(quantityField.getText());

            // itemNumber = cart.size() + 1
            CartItemEntity item = new CartItemEntity(0, cart.size() + 1, key, price, qty);
            cart.put(key, item);

            String line = t(key) + ": " + price + " × " + qty + " = " + String.format("%.2f", item.getSubtotal());
            cartList.getItems().add(line);

            showAlert(t("added") + " " + t(key));

            priceField.clear();
            quantityField.clear();

            totalItem.setText(String.valueOf(cart.size()));

        } catch (Exception e) {
            showAlert(t(error));
        }
    }


    @FXML
    private void onCart() {
        itemSection.setVisible(true);
        cartSection.setVisible(true);
        priceSummaryBox.setVisible(false);

    }

    @FXML
    private void deleteItem() {
        String selected = cartList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(t("delete.item"));

            ButtonType yes = new ButtonType(t("yes"));
            ButtonType no = new ButtonType(t("no"));

            alert.getButtonTypes().setAll(yes, no);

            alert.showAndWait().ifPresent(response -> {
                if (response == yes) {
                    showAlert(t("thanks"));
                    cartList.getItems().remove(selected);
                    totalItem.setText(String.valueOf(cart.size()-1));
                } else {
                    showAlert(t("sorry"));
                }
            });
        }
    }

    @FXML
    private void onCalculate() {
        priceSummaryBox.setVisible(true);

        Map<String, Double> result = CartCalculatorService.calculateTotal(cart);

        totalLabel.setText(result.get("total") + " EUR");
        discountLabel.setText(result.get("discountAmount") + " EUR");
        afterDiscountLabel.setText(result.get("afterDiscount") + " EUR");
        taxLabel.setText(result.get("taxAmount") + " EUR");
        finalTotalLabel.setText(result.get("afterTax") + " EUR");

    }

    @FXML
    private void onPay() {
        if (cart.isEmpty()) {
            showAlert(t(error));
            return;
        }

        // 1. total
        Map<String, Double> result = CartCalculatorService.calculateTotal(cart);

        try {
            // 2. entity.CartRecordEntity
            CartRecordEntity recordEntity = new CartRecordEntity(cart.size(), result.get("afterTax"), currentLocale.getLanguage()
            );

            // 3. save record → get recordId
            int recordId = recordDao.insert(recordEntity);

            if (recordId <= 0) {
                showAlert("Failed to save cart record.");
                return;
            }

            // 4. save entity.CartItemEntity
            for (CartItemEntity item : cart.values()) {
                item.setCartRecordId(recordId);
                itemDao.insert(item);
            }

            showAlert(t("thanks"));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to import Excel", e);
            showAlert("Database error.");
            return;
        }

        // 6. Reset UI
        onBack();
    }

    public void onBack() {
        // 1. Reset UI sections
        cartSection.setVisible(false);
        itemSection.setVisible(true);

        // 2. Reset cart data
        cart.clear();
        cartList.getItems().clear();
        totalItem.setText("0");

        // 3. Reset input fields
        priceField.clear();
        quantityField.clear();

        // 4. Reset summary box:
        priceSummaryBox.setVisible(false);

        // 5. Reset item list
        loadItemList();
        itemList.getSelectionModel().clearSelection();
    }

    /**
     * RTL support for Arabic
     */
    private void applyTextDirection(Locale locale) {
        boolean isRTL = locale.getLanguage().equals("ar");

        Platform.runLater(() ->
            rootVBox.setNodeOrientation(
                    isRTL ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT
            )
        );
    }

    /**
     * Show alert
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}


