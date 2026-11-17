package algexplorer.navigation;

import algexplorer.UIContext;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Navigation engine providing smooth animated transitions between screens.
 * Supports horizontal slide (for navigating to/from visualizer screens)
 * and vertical scroll-up/down (for transitioning between Intro and Dashboard).
 */
public class Navigation {

    /**
     * Slide forward horizontally (leftwards push).
     */
    public static void slideForward(Parent current, String fxmlPath) {
        try {
            Scene scene = current.getScene() != null ? current.getScene() : UIContext.getStage().getScene();
            double width = scene.getWidth() > 0 ? scene.getWidth() : 1180;
            Parent next = FXMLLoader.load(Navigation.class.getResource(fxmlPath));

            next.setTranslateX(width);
            StackPane stack = new StackPane(current, next);
            scene.setRoot(stack);

            TranslateTransition slideOut = new TranslateTransition(Duration.millis(350), current);
            slideOut.setToX(-width);
            slideOut.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), next);
            slideIn.setFromX(width);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition pt = new ParallelTransition(slideOut, slideIn);
            pt.setOnFinished(e -> {
                stack.getChildren().remove(next);
                scene.setRoot(next);
            });
            pt.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Slide backward horizontally (rightwards pop).
     */
    public static void slideBack(Parent current, String fxmlPath) {
        try {
            Scene scene = current.getScene() != null ? current.getScene() : UIContext.getStage().getScene();
            double width = scene.getWidth() > 0 ? scene.getWidth() : 1180;
            Parent previous = FXMLLoader.load(Navigation.class.getResource(fxmlPath));

            previous.setTranslateX(-width);
            StackPane stack = new StackPane(current, previous);
            scene.setRoot(stack);

            TranslateTransition slideOut = new TranslateTransition(Duration.millis(350), current);
            slideOut.setToX(width);
            slideOut.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), previous);
            slideIn.setFromX(-width);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition pt = new ParallelTransition(slideOut, slideIn);
            pt.setOnFinished(e -> {
                stack.getChildren().remove(previous);
                scene.setRoot(previous);
            });
            pt.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Slide vertically (scroll up to dashboard or scroll down to intro).
     * @param scrollUp true to scroll up into dashboard, false to scroll down to intro.
     */
    public static void slideVertical(Parent current, String fxmlPath, boolean scrollUp) {
        try {
            Scene scene = current.getScene() != null ? current.getScene() : UIContext.getStage().getScene();
            double height = scene.getHeight() > 0 ? scene.getHeight() : 750;
            Parent target = FXMLLoader.load(Navigation.class.getResource(fxmlPath));

            double startY = scrollUp ? height : -height;
            double exitY = scrollUp ? -height : height;

            target.setTranslateY(startY);
            StackPane stack = new StackPane(current, target);
            scene.setRoot(stack);

            TranslateTransition moveOut = new TranslateTransition(Duration.millis(450), current);
            moveOut.setToY(exitY);
            moveOut.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition moveIn = new TranslateTransition(Duration.millis(450), target);
            moveIn.setFromY(startY);
            moveIn.setToY(0);
            moveIn.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition pt = new ParallelTransition(moveOut, moveIn);
            pt.setOnFinished(e -> {
                stack.getChildren().remove(target);
                scene.setRoot(target);
            });
            pt.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
