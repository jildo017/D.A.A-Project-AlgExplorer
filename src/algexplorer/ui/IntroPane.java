package algexplorer.ui;

import algexplorer.navigation.Navigation;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class IntroPane {

    @FXML private StackPane rootPane;
    @FXML private Label logoBadge;
    @FXML private Label titleLabel;
    @FXML private Label subTitleLabel;
    @FXML private HBox statsRow;
    @FXML private Label algoCountLabel;
    @FXML private Label catCountLabel;
    @FXML private Label sortsCountLabel;
    @FXML private Button startBtn;
    @FXML private Label timerLabel;

    private Timeline timerCountdown;
    private boolean transitioned = false;

    @FXML
    private void initialize() {
        // Initially hide elements for staggered fade-in
        logoBadge.setOpacity(0);
        titleLabel.setOpacity(0);
        subTitleLabel.setOpacity(0);
        statsRow.setOpacity(0);
        startBtn.setOpacity(0);
        timerLabel.setOpacity(0);

        // 1. Logo fades in
        FadeTransition ftLogo = new FadeTransition(Duration.millis(500), logoBadge);
        ftLogo.setFromValue(0); ftLogo.setToValue(1);

        // 2. Title fades in
        FadeTransition ftTitle = new FadeTransition(Duration.millis(600), titleLabel);
        ftTitle.setFromValue(0); ftTitle.setToValue(1);
        ftTitle.setDelay(Duration.millis(200));

        // 3. Subtitle fades in
        FadeTransition ftSub = new FadeTransition(Duration.millis(500), subTitleLabel);
        ftSub.setFromValue(0); ftSub.setToValue(1);
        ftSub.setDelay(Duration.millis(400));

        // 4. Stats row fades in
        FadeTransition ftStats = new FadeTransition(Duration.millis(500), statsRow);
        ftStats.setFromValue(0); ftStats.setToValue(1);
        ftStats.setDelay(Duration.millis(600));

        // 5. Button & timer hint fade in
        FadeTransition ftBtn = new FadeTransition(Duration.millis(400), startBtn);
        ftBtn.setFromValue(0); ftBtn.setToValue(1);
        ftBtn.setDelay(Duration.millis(800));

        FadeTransition ftTimer = new FadeTransition(Duration.millis(400), timerLabel);
        ftTimer.setFromValue(0); ftTimer.setToValue(1);
        ftTimer.setDelay(Duration.millis(900));

        // Counter tick up animation:
        // 21 Algorithms, 4 Categories, 0 Built-in sorts used
        ftStats.setOnFinished(e -> {
            Timeline counterTimeline = new Timeline();
            for (int i = 0; i <= 21; i++) {
                final int val = i;
                final int catVal = Math.min(val, 4);
                counterTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 65), evt -> {
                        algoCountLabel.setText(String.valueOf(val));
                        catCountLabel.setText(String.valueOf(catVal));
                        sortsCountLabel.setText("0");
                    })
                );
            }
            counterTimeline.play();
        });

        ftLogo.play();
        ftTitle.play();
        ftSub.play();
        ftStats.play();
        ftBtn.play();
        ftTimer.play();

        // 4-second auto-transition to Dashboard
        timerCountdown = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> timerLabel.setText("Auto-launching in 3 seconds...")),
            new KeyFrame(Duration.seconds(2), e -> timerLabel.setText("Auto-launching in 2 seconds...")),
            new KeyFrame(Duration.seconds(3), e -> timerLabel.setText("Auto-launching in 1 second...")),
            new KeyFrame(Duration.seconds(4), e -> doScrollUpToDashboard())
        );
        timerCountdown.play();

        startBtn.setOnAction(e -> doScrollUpToDashboard());
    }

    private void doScrollUpToDashboard() {
        if (transitioned) return;
        transitioned = true;
        if (timerCountdown != null) timerCountdown.stop();

        // Scroll / slide up to dashboard
        Navigation.slideVertical(rootPane, "/algexplorer/ui/HubPane.fxml", true);
    }
}
