import javafx.application.Application;
import javafx.stage.Stage;
import view.ViewLoader;


public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        ViewLoader.loadScene(stage);
        stage.setTitle("Thanh/Shopping Cart");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
