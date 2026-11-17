package algexplorer.ui;

import algexplorer.UIContext;
import algexplorer.navigation.Navigation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HubPane {

    @FXML private BorderPane rootPane;

    // Sidebar navigation
    @FXML private Button navDashboard;
    @FXML private Button navSorting;
    @FXML private Button navSearching;
    @FXML private Button navRecursion;
    @FXML private Button navBenchmark;
    @FXML private Button navStack;
    @FXML private Button navQueue;
    @FXML private Button navLinkedList;

    // Scroll container
    @FXML private ScrollPane scrollPane;
    @FXML private VBox mainContainer;

    // 6 Overview Cards
    @FXML private VBox cardSorting;
    @FXML private VBox cardSearching;
    @FXML private VBox cardStructures;
    @FXML private VBox cardRecursion;
    @FXML private VBox cardBenchmark;
    @FXML private VBox cardComplexity;

    // Sections
    @FXML private VBox sortingSection;
    @FXML private VBox searchingSection;
    @FXML private VBox structuresSection;
    @FXML private VBox recursionSection;

    // Sorting algorithm buttons
    @FXML private Button btnSortBubble;
    @FXML private Button btnSortSelection;
    @FXML private Button btnSortInsertion;
    @FXML private Button btnSortShell;
    @FXML private Button btnSortMerge;
    @FXML private Button btnSortQuick;
    @FXML private Button btnSortHeap;
    @FXML private Button btnSortCocktail;
    @FXML private Button btnSortCounting;
    @FXML private Button btnSortRadix;
    @FXML private Button btnSortBucket;
    @FXML private Button btnSortCycle;
    @FXML private Button btnSortTim;
    @FXML private Button btnSortPancake;
    @FXML private Button btnSortComb;
    @FXML private Button btnSortGnome;
    @FXML private Button btnBenchmarkAll;

    // Searching buttons
    @FXML private Button btnSearchLinear;
    @FXML private Button btnSearchBinary;
    @FXML private Button btnSearchJump;
    @FXML private Button btnSearchInterpolation;

    // Data structures buttons
    @FXML private Button btnCustomStack;
    @FXML private Button btnCustomQueue;
    @FXML private Button btnCustomLinkedList;

    // Recursion buttons
    @FXML private Button btnRecFactorial;
    @FXML private Button btnRecFibonacci;
    @FXML private Button btnRecHanoi;
    @FXML private Button btnRecBinSearch;

    // Bottom action buttons
    @FXML private Button btnReturnHome;
    @FXML private Button btnExitApp;

    @FXML
    private void initialize() {
        // Wire Sidebar smooth scrolling
        navDashboard.setOnAction(e -> smoothScrollTo(0.0));
        navSorting.setOnAction(e -> scrollToNode(sortingSection));
        navSearching.setOnAction(e -> scrollToNode(searchingSection));
        navRecursion.setOnAction(e -> scrollToNode(recursionSection));
        navBenchmark.setOnAction(e -> scrollToNode(btnBenchmarkAll));
        navStack.setOnAction(e -> {
            StructuresPane.pendingType = StructuresPane.StructType.STACK;
            Navigation.slideForward(rootPane, "/algexplorer/ui/StructuresPane.fxml");
        });
        navQueue.setOnAction(e -> {
            StructuresPane.pendingType = StructuresPane.StructType.QUEUE;
            Navigation.slideForward(rootPane, "/algexplorer/ui/StructuresPane.fxml");
        });
        navLinkedList.setOnAction(e -> {
            StructuresPane.pendingType = StructuresPane.StructType.SINGLY_LIST;
            Navigation.slideForward(rootPane, "/algexplorer/ui/StructuresPane.fxml");
        });

        // Wire 6 Cards to slide sideways
        cardSorting.setOnMouseClicked(e -> {
            SortingPane.pendingAlgo = SortingPane.AlgoDefinition.BUBBLE;
            Navigation.slideForward(rootPane, "/algexplorer/ui/SortingPane.fxml");
        });
        cardSearching.setOnMouseClicked(e -> {
            SearchingPane.pendingAlgo = SearchingPane.SearchAlgo.LINEAR;
            Navigation.slideForward(rootPane, "/algexplorer/ui/SearchingPane.fxml");
        });
        cardStructures.setOnMouseClicked(e -> {
            StructuresPane.pendingType = StructuresPane.StructType.SINGLY_LIST;
            Navigation.slideForward(rootPane, "/algexplorer/ui/StructuresPane.fxml");
        });
        cardRecursion.setOnMouseClicked(e -> {
            RecursionPane.pendingMode = RecursionPane.RecursionMode.HANOI;
            Navigation.slideForward(rootPane, "/algexplorer/ui/RecursionPane.fxml");
        });
        cardBenchmark.setOnMouseClicked(e -> {
            Navigation.slideForward(rootPane, "/algexplorer/ui/BenchmarkPane.fxml");
        });
        cardComplexity.setOnMouseClicked(e -> {
            Navigation.slideForward(rootPane, "/algexplorer/ui/ComplexityPane.fxml");
        });

        // Wire Sorting algorithm buttons
        btnSortBubble.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.BUBBLE));
        btnSortSelection.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.SELECTION));
        btnSortInsertion.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.INSERTION));
        btnSortShell.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.SHELL));
        btnSortMerge.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.MERGE));
        btnSortQuick.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.QUICK));
        btnSortHeap.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.HEAP));
        btnSortCocktail.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.COCKTAIL));
        btnSortCounting.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.COUNTING));
        btnSortRadix.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.RADIX));
        btnSortBucket.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.BUCKET));
        btnSortCycle.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.CYCLE));
        btnSortTim.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.TIM));
        btnSortPancake.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.PANCAKE));
        btnSortComb.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.COMB));
        btnSortGnome.setOnAction(e -> openSorting(SortingPane.AlgoDefinition.GNOME));

        btnBenchmarkAll.setOnAction(e -> Navigation.slideForward(rootPane, "/algexplorer/ui/BenchmarkPane.fxml"));

        // Wire Searching buttons
        btnSearchLinear.setOnAction(e -> openSearching(SearchingPane.SearchAlgo.LINEAR));
        btnSearchBinary.setOnAction(e -> openSearching(SearchingPane.SearchAlgo.BINARY));
        btnSearchJump.setOnAction(e -> openSearching(SearchingPane.SearchAlgo.JUMP));
        btnSearchInterpolation.setOnAction(e -> openSearching(SearchingPane.SearchAlgo.INTERPOLATION));

        // Wire Data Structures buttons
        btnCustomStack.setOnAction(e -> openStructures(StructuresPane.StructType.STACK));
        btnCustomQueue.setOnAction(e -> openStructures(StructuresPane.StructType.QUEUE));
        btnCustomLinkedList.setOnAction(e -> openStructures(StructuresPane.StructType.SINGLY_LIST));

        // Wire Recursion buttons
        btnRecFactorial.setOnAction(e -> openRecursion(RecursionPane.RecursionMode.FACTORIAL));
        btnRecFibonacci.setOnAction(e -> openRecursion(RecursionPane.RecursionMode.FIBONACCI));
        btnRecHanoi.setOnAction(e -> openRecursion(RecursionPane.RecursionMode.HANOI));
        btnRecBinSearch.setOnAction(e -> openRecursion(RecursionPane.RecursionMode.BINSEARCH));

        // Wire Bottom sticky bar
        btnReturnHome.setOnAction(e -> Navigation.slideVertical(rootPane, "/algexplorer/ui/IntroPane.fxml", false));
        btnExitApp.setOnAction(e -> {
            if (UIContext.getStage() != null) {
                UIContext.getStage().close();
            }
        });
    }

    private void openSorting(SortingPane.AlgoDefinition algo) {
        SortingPane.pendingAlgo = algo;
        Navigation.slideForward(rootPane, "/algexplorer/ui/SortingPane.fxml");
    }

    private void openSearching(SearchingPane.SearchAlgo algo) {
        SearchingPane.pendingAlgo = algo;
        Navigation.slideForward(rootPane, "/algexplorer/ui/SearchingPane.fxml");
    }

    private void openStructures(StructuresPane.StructType type) {
        StructuresPane.pendingType = type;
        Navigation.slideForward(rootPane, "/algexplorer/ui/StructuresPane.fxml");
    }

    private void openRecursion(RecursionPane.RecursionMode mode) {
        RecursionPane.pendingMode = mode;
        Navigation.slideForward(rootPane, "/algexplorer/ui/RecursionPane.fxml");
    }

    private void scrollToNode(Node target) {
        if (scrollPane == null || target == null || mainContainer == null) return;
        scrollPane.applyCss();
        scrollPane.layout();
        double contentHeight = mainContainer.getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double targetY = target.getBoundsInParent().getMinY();
        double maxScroll = contentHeight - viewportHeight;
        if (maxScroll <= 0) return;
        double targetVvalue = Math.min(1.0, Math.max(0.0, targetY / maxScroll));
        smoothScrollTo(targetVvalue);
    }

    private void smoothScrollTo(double vvalue) {
        Timeline scrollTimeline = new Timeline(
            new KeyFrame(Duration.millis(350),
                new KeyValue(scrollPane.vvalueProperty(), vvalue, Interpolator.EASE_BOTH))
        );
        scrollTimeline.play();
    }
}
