module algexplorer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    exports algexplorer;
    exports algexplorer.ui;
    exports algexplorer.sorting;
    exports algexplorer.searching;
    exports algexplorer.navigation;
    opens algexplorer.ui to javafx.fxml;
}
