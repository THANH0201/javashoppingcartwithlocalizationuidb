package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ViewLoader {
    private ViewLoader() {
        /* This utility class should not be instantiated */
    }
    static Logger logger = LoggerFactory.getLogger(ViewLoader.class);

    public static void loadScene(Stage stage) throws IOException {

        // Load font safely
        Font.loadFont(ViewLoader.class.getResourceAsStream("/fonts/NotoSansLao-Regular.ttf"), 12);
        Font f1 = Font.loadFont(ViewLoader.class.getResourceAsStream("/fonts/NotoSansJP-Regular.ttf"), 12);

        if (f1 != null) {
            logger.info("Loaded font JP" );
        } else {
            logger.info("Font NotoSansJP-Regular.ttf failed to load!");
        }

        // Load FXML
        FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource("/main_view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Add CSS
        scene.getStylesheets().add(
                ViewLoader.class.getResource("/style.css").toExternalForm()
        );
    }
}
