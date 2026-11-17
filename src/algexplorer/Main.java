package algexplorer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Launch with the Intro screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/algexplorer/ui/IntroPane.fxml"));
        Scene scene = new Scene(loader.load(), 1180, 750);
        scene.setFill(javafx.scene.paint.Color.web("#070B12"));

        UIContext.setStage(primaryStage);
        primaryStage.setTitle("Algorithm Explorer");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(680);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
