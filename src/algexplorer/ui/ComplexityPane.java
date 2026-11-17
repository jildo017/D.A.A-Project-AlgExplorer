package algexplorer.ui;

import algexplorer.navigation.Navigation;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class ComplexityPane {

    @FXML private Button backBtn;

    @FXML
    private void initialize() {
        backBtn.setOnAction(e -> goBack());
    }

    private void goBack() {
        Navigation.slideBack((Parent) backBtn.getScene().getRoot(), "/algexplorer/ui/HubPane.fxml");
    }
}
