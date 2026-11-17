package algexplorer;

import javafx.stage.Stage;

/**
 * Simple holder for the primary Stage so that any controller can access it for navigation.
 */
public class UIContext {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getStage() {
        return primaryStage;
    }
}
