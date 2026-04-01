package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

import static view.ViewLoader.loadScene;
public class ViewLoader {

    public static void loadScene(Stage stage) throws IOException {

        // Load font safely
        Font f = Font.loadFont(ViewLoader.class.getResourceAsStream("/fonts/NotoSansLao-Regular.ttf"), 12);
        Font f1 = Font.loadFont(ViewLoader.class.getResourceAsStream("/fonts/NotoSansJP-Regular.ttf"), 12);

        if (f1 != null) {
            System.out.println("Loaded font: " + f1.getName());
        } else {
            System.out.println("Font NotoSansJP-Regular.ttf failed to load!");
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
