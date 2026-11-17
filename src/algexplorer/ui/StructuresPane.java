package algexplorer.ui;

import algexplorer.UIContext;
import algexplorer.navigation.Navigation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class StructuresPane {

    public enum StructType {
        SINGLY_LIST("Singly Linked List", "O(1) Insert / O(n) Search"),
        DOUBLY_LIST("Doubly Linked List", "O(1) Insert / O(n) Search"),
        BST("Binary Search Tree", "O(log n) Avg / O(n) Worst"),
        STACK("Stack (LIFO)", "O(1) Push & Pop"),
        QUEUE("Queue (FIFO)", "O(1) Enqueue & Dequeue");

        public final String name;
        public final String complexity;
        StructType(String name, String complexity) {
            this.name = name;
            this.complexity = complexity;
        }
    }

    private static class BSTNode {
        int val;
        BSTNode left, right;
        BSTNode(int v) { this.val = v; }
    }

    @FXML private Button backBtn;
    @FXML private Button exitBtn;

    @FXML private Button btnLinkedList;
    @FXML private Button btnDoublyList;
    @FXML private Button btnBST;
    @FXML private Button btnStack;
    @FXML private Button btnQueue;

    @FXML private TextField valueInput;
    @FXML private Button action1Btn;
    @FXML private Button action2Btn;
    @FXML private Button actionDeleteBtn;
    @FXML private Button actionSearchBtn;
    @FXML private Button actionExtraBtn;
    @FXML private Button sampleBtn;
    @FXML private Button clearBtn;

    @FXML private Label countLabel;
    @FXML private Label complexityLabel;
    @FXML private Label feedbackLabel;

    @FXML private Canvas canvas;

    private StructType currentType = StructType.SINGLY_LIST;
    public static StructType pendingType = null;

    // Underlying Data
    private final LinkedList<Integer> singlyList = new LinkedList<>();
    private final LinkedList<Integer> doublyList = new LinkedList<>();
    private BSTNode bstRoot = null;
    private int bstSize = 0;
    private final Stack<Integer> stack = new Stack<>();
    private final LinkedList<Integer> queue = new LinkedList<>();

    // Traversal Animation State
    private int highlightedValue = -1;
    private int activePointerIndex = -1;
    private List<Integer> bstPathHighlight = new ArrayList<>();
    private Timeline traversalTimeline;

    @FXML
    private void initialize() {
        backBtn.setOnAction(e -> goBack());
        exitBtn.setOnAction(e -> {
            if (UIContext.getStage() != null) UIContext.getStage().close();
        });

        btnLinkedList.setOnAction(e -> selectStructure(StructType.SINGLY_LIST));
        btnDoublyList.setOnAction(e -> selectStructure(StructType.DOUBLY_LIST));
        btnBST.setOnAction(e -> selectStructure(StructType.BST));
        btnStack.setOnAction(e -> selectStructure(StructType.STACK));
        btnQueue.setOnAction(e -> selectStructure(StructType.QUEUE));

        clearBtn.setOnAction(e -> handleClear());
        sampleBtn.setOnAction(e -> loadSampleData());

        // Default initial structure or pending requested structure
        if (pendingType != null) {
            selectStructure(pendingType);
            pendingType = null;
        } else {
            selectStructure(StructType.SINGLY_LIST);
        }
        loadSampleData();
    }

    private void stopTimeline() {
        if (traversalTimeline != null) {
            traversalTimeline.stop();
        }
    }

    private void selectStructure(StructType type) {
        stopTimeline();
        currentType = type;
        highlightedValue = -1;
        activePointerIndex = -1;
        bstPathHighlight.clear();

        btnLinkedList.getStyleClass().remove("locked");
        btnDoublyList.getStyleClass().remove("locked");
        btnBST.getStyleClass().remove("locked");
        btnStack.getStyleClass().remove("locked");
        btnQueue.getStyleClass().remove("locked");

        complexityLabel.setText(type.complexity);

        switch (type) {
            case SINGLY_LIST -> {
                btnLinkedList.getStyleClass().add("locked");
                action1Btn.setText("Insert Head");
                action2Btn.setText("Insert Tail");
                actionDeleteBtn.setText("Delete Value");
                actionSearchBtn.setText("🔎 Search");
                actionExtraBtn.setText("Reverse List");
                actionExtraBtn.setVisible(true);

                action1Btn.setOnAction(e -> handleListInsertHead(false));
                action2Btn.setOnAction(e -> handleListInsertTail(false));
                actionDeleteBtn.setOnAction(e -> handleListDelete(false));
                actionSearchBtn.setOnAction(e -> handleListSearch(false));
                actionExtraBtn.setOnAction(e -> handleListReverse(false));
                feedbackLabel.setText("Singly Linked List active. Unidirectional pointer chain: [Data | Next] -> [Data | Next] -> NULL.");
            }
            case DOUBLY_LIST -> {
                btnDoublyList.getStyleClass().add("locked");
                action1Btn.setText("Insert Head");
                action2Btn.setText("Insert Tail");
                actionDeleteBtn.setText("Delete Value");
                actionSearchBtn.setText("🔎 Search");
                actionExtraBtn.setText("Reverse List");
                actionExtraBtn.setVisible(true);

                action1Btn.setOnAction(e -> handleListInsertHead(true));
                action2Btn.setOnAction(e -> handleListInsertTail(true));
                actionDeleteBtn.setOnAction(e -> handleListDelete(true));
                actionSearchBtn.setOnAction(e -> handleListSearch(true));
                actionExtraBtn.setOnAction(e -> handleListReverse(true));
                feedbackLabel.setText("Doubly Linked List active. Bidirectional node links: NULL <- [Prev | Data | Next] <-> [Prev | Data | Next] -> NULL.");
            }
            case BST -> {
                btnBST.getStyleClass().add("locked");
                action1Btn.setText("Insert Node");
                action2Btn.setText("Balanced Tree");
                actionDeleteBtn.setText("Delete Node");
                actionSearchBtn.setText("🔎 Search");
                actionExtraBtn.setText("In-Order");
                actionExtraBtn.setVisible(true);

                action1Btn.setOnAction(e -> handleBSTInsert());
                action2Btn.setOnAction(e -> loadSampleBST());
                actionDeleteBtn.setOnAction(e -> handleBSTDelete());
                actionSearchBtn.setOnAction(e -> handleBSTSearch());
                actionExtraBtn.setOnAction(e -> handleBSTInOrder());
                feedbackLabel.setText("Binary Search Tree active. Left subtree < Root < Right subtree.");
            }
            case STACK -> {
                btnStack.getStyleClass().add("locked");
                action1Btn.setText("Push (LIFO)");
                action2Btn.setText("Pop Top");
                actionDeleteBtn.setText("Peek Top");
                actionSearchBtn.setText("🔎 Search");
                actionExtraBtn.setVisible(false);

                action1Btn.setOnAction(e -> handleStackPush());
                action2Btn.setOnAction(e -> handleStackPop());
                actionDeleteBtn.setOnAction(e -> handleStackPeek());
                actionSearchBtn.setOnAction(e -> handleStackSearch());
                feedbackLabel.setText("Stack container active. Last-In First-Out: items push and pop from the TOP.");
            }
            case QUEUE -> {
                btnQueue.getStyleClass().add("locked");
                action1Btn.setText("Enqueue");
                action2Btn.setText("Dequeue");
                actionDeleteBtn.setText("Front Element");
                actionSearchBtn.setText("🔎 Search");
                actionExtraBtn.setVisible(false);

                action1Btn.setOnAction(e -> handleQueueEnqueue());
                action2Btn.setOnAction(e -> handleQueueDequeue());
                actionDeleteBtn.setOnAction(e -> handleQueueFront());
                actionSearchBtn.setOnAction(e -> handleQueueSearch());
                feedbackLabel.setText("Queue pipeline active. First-In First-Out: items enter at REAR and exit at FRONT.");
            }
        }

        updateCount();
        renderCanvas();
    }

    private int getInputValue() {
        try {
            return Integer.parseInt(valueInput.getText().trim());
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Notice: Invalid input. Defaulting to 42.");
            return 42;
        }
    }

    private void loadSampleData() {
        stopTimeline();
        highlightedValue = -1;
        activePointerIndex = -1;
        bstPathHighlight.clear();

        switch (currentType) {
            case SINGLY_LIST -> {
                singlyList.clear();
                singlyList.addAll(List.of(12, 28, 45, 63, 89));
                feedbackLabel.setText("Loaded sample Singly Linked List.");
            }
            case DOUBLY_LIST -> {
                doublyList.clear();
                doublyList.addAll(List.of(18, 34, 55, 72, 91));
                feedbackLabel.setText("Loaded sample Doubly Linked List.");
            }
            case BST -> loadSampleBST();
            case STACK -> {
                stack.clear();
                stack.addAll(List.of(10, 25, 40, 65, 80));
                feedbackLabel.setText("Loaded sample Stack with 5 items.");
            }
            case QUEUE -> {
                queue.clear();
                queue.addAll(List.of(15, 30, 48, 70, 95));
                feedbackLabel.setText("Loaded sample Queue with 5 items.");
            }
        }
        updateCount();
        renderCanvas();
    }

    // =========================================================================
    // Linked List Operations (Singly & Doubly)
    // =========================================================================
    private void handleListInsertHead(boolean isDoubly) {
        stopTimeline();
        int v = getInputValue();
        LinkedList<Integer> list = isDoubly ? doublyList : singlyList;
        list.addFirst(v);
        highlightedValue = v;
        activePointerIndex = 0;
        feedbackLabel.setText("Inserted " + v + " at HEAD position [0] in O(1) time.");
        updateCount();
        renderCanvas();
    }

    private void handleListInsertTail(boolean isDoubly) {
        stopTimeline();
        int v = getInputValue();
        LinkedList<Integer> list = isDoubly ? doublyList : singlyList;
        list.addLast(v);
        highlightedValue = v;
        activePointerIndex = list.size() - 1;
        feedbackLabel.setText("Inserted " + v + " at TAIL position [" + (list.size() - 1) + "] in O(1) time.");
        updateCount();
        renderCanvas();
    }

    private void handleListDelete(boolean isDoubly) {
        stopTimeline();
        int v = getInputValue();
        LinkedList<Integer> list = isDoubly ? doublyList : singlyList;
        boolean removed = list.remove((Integer) v);
        highlightedValue = -1;
        activePointerIndex = -1;
        feedbackLabel.setText(removed ? "Deleted value " + v + " from list." : "Value " + v + " not found to delete.");
        updateCount();
        renderCanvas();
    }

    private void handleListSearch(boolean isDoubly) {
        stopTimeline();
        int target = getInputValue();
        LinkedList<Integer> list = isDoubly ? doublyList : singlyList;
        if (list.isEmpty()) {
            feedbackLabel.setText("List is empty.");
            return;
        }

        // Animate pointer stepping node-by-node!
        activePointerIndex = 0;
        highlightedValue = -1;
        feedbackLabel.setText("🔎 Searching for " + target + ": Inspecting node [0]...");
        renderCanvas();

        traversalTimeline = new Timeline(new KeyFrame(Duration.millis(550), e -> {
            if (activePointerIndex < list.size()) {
                int curVal = list.get(activePointerIndex);
                if (curVal == target) {
                    highlightedValue = target;
                    feedbackLabel.setText("🎯 FOUND target " + target + " at node index [" + activePointerIndex + "]!");
                    renderCanvas();
                    stopTimeline();
                } else {
                    activePointerIndex++;
                    if (activePointerIndex < list.size()) {
                        feedbackLabel.setText("🔎 Node [" + (activePointerIndex - 1) + "] (" + curVal + ") != " + target + " -> Moving to node [" + activePointerIndex + "]...");
                        renderCanvas();
                    } else {
                        activePointerIndex = -1;
                        feedbackLabel.setText("❌ Traversal reached NULL: Target " + target + " was not found in the list.");
                        renderCanvas();
                        stopTimeline();
                    }
                }
            }
        }));
        traversalTimeline.setCycleCount(Timeline.INDEFINITE);
        traversalTimeline.play();
    }

    private void handleListReverse(boolean isDoubly) {
        stopTimeline();
        LinkedList<Integer> list = isDoubly ? doublyList : singlyList;
        LinkedList<Integer> rev = new LinkedList<>();
        while (!list.isEmpty()) rev.addFirst(list.removeFirst());
        list.addAll(rev);
        highlightedValue = -1;
        activePointerIndex = -1;
        feedbackLabel.setText("Reversed all pointer directions in O(n) time.");
        renderCanvas();
    }

    // =========================================================================
    // Binary Search Tree Operations
    // =========================================================================
    private void loadSampleBST() {
        bstRoot = null;
        bstSize = 0;
        int[] sample = {50, 25, 75, 15, 35, 65, 90};
        for (int v : sample) bstRoot = bstInsertRec(bstRoot, v);
        bstSize = sample.length;
        highlightedValue = -1;
        bstPathHighlight.clear();
        feedbackLabel.setText("Loaded balanced sample BST with 7 nodes.");
        updateCount();
        renderCanvas();
    }

    private void handleBSTInsert() {
        stopTimeline();
        int v = getInputValue();
        if (bstContains(bstRoot, v)) {
            feedbackLabel.setText("BST already contains value " + v + ". BST stores distinct keys.");
            return;
        }

        // Trace insertion path
        bstPathHighlight.clear();
        BSTNode curr = bstRoot;
        while (curr != null) {
            bstPathHighlight.add(curr.val);
            curr = v < curr.val ? curr.left : curr.right;
        }
        bstPathHighlight.add(v);

        bstRoot = bstInsertRec(bstRoot, v);
        bstSize++;
        highlightedValue = v;
        feedbackLabel.setText("Inserted " + v + " into BST along path " + bstPathHighlight + ".");
        updateCount();
        renderCanvas();
    }

    private BSTNode bstInsertRec(BSTNode root, int val) {
        if (root == null) return new BSTNode(val);
        if (val < root.val) root.left = bstInsertRec(root.left, val);
        else if (val > root.val) root.right = bstInsertRec(root.right, val);
        return root;
    }

    private void handleBSTDelete() {
        stopTimeline();
        int v = getInputValue();
        if (!bstContains(bstRoot, v)) {
            feedbackLabel.setText("Value " + v + " not found in BST.");
            return;
        }
        bstRoot = bstDeleteRec(bstRoot, v);
        bstSize--;
        highlightedValue = -1;
        bstPathHighlight.clear();
        feedbackLabel.setText("Deleted " + v + " from BST.");
        updateCount();
        renderCanvas();
    }

    private BSTNode bstDeleteRec(BSTNode root, int val) {
        if (root == null) return null;
        if (val < root.val) root.left = bstDeleteRec(root.left, val);
        else if (val > root.val) root.right = bstDeleteRec(root.right, val);
        else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            BSTNode min = root.right;
            while (min.left != null) min = min.left;
            root.val = min.val;
            root.right = bstDeleteRec(root.right, min.val);
        }
        return root;
    }

    private boolean bstContains(BSTNode node, int val) {
        if (node == null) return false;
        if (node.val == val) return true;
        return val < node.val ? bstContains(node.left, val) : bstContains(node.right, val);
    }

    private void handleBSTSearch() {
        stopTimeline();
        int target = getInputValue();
        bstPathHighlight.clear();

        List<Integer> path = new ArrayList<>();
        BSTNode curr = bstRoot;
        while (curr != null) {
            path.add(curr.val);
            if (curr.val == target) break;
            curr = target < curr.val ? curr.left : curr.right;
        }

        if (path.isEmpty()) {
            feedbackLabel.setText("Tree is empty.");
            return;
        }

        // Animate step-by-step path search
        final int[] step = {0};
        traversalTimeline = new Timeline(new KeyFrame(Duration.millis(600), e -> {
            if (step[0] < path.size()) {
                int nodeVal = path.get(step[0]);
                bstPathHighlight.add(nodeVal);
                highlightedValue = nodeVal;

                if (nodeVal == target) {
                    feedbackLabel.setText("🎯 FOUND " + target + " at depth " + step[0] + "! Search path: " + bstPathHighlight);
                    stopTimeline();
                } else {
                    String dir = target < nodeVal ? "LEFT (" + target + " < " + nodeVal + ")" : "RIGHT (" + target + " > " + nodeVal + ")";
                    feedbackLabel.setText("Inspected " + nodeVal + " -> Traversing " + dir + "...");
                }
                renderCanvas();
                step[0]++;
            } else {
                feedbackLabel.setText("❌ Reached leaf NULL. Value " + target + " is not in the BST.");
                highlightedValue = -1;
                renderCanvas();
                stopTimeline();
            }
        }));
        traversalTimeline.setCycleCount(Timeline.INDEFINITE);
        traversalTimeline.play();
    }

    private void handleBSTInOrder() {
        stopTimeline();
        List<Integer> list = new ArrayList<>();
        inOrderRec(bstRoot, list);
        feedbackLabel.setText("In-Order Traversal (L -> Root -> R): " + list + " (Always produces sorted order!)");
    }

    private void inOrderRec(BSTNode root, List<Integer> list) {
        if (root != null) {
            inOrderRec(root.left, list);
            list.add(root.val);
            inOrderRec(root.right, list);
        }
    }

    // =========================================================================
    // Stack & Queue Operations
    // =========================================================================
    private void handleStackPush() {
        stopTimeline();
        int v = getInputValue();
        stack.push(v);
        highlightedValue = v;
        activePointerIndex = stack.size() - 1;
        feedbackLabel.setText("Pushed " + v + " onto Stack TOP in O(1) time.");
        updateCount();
        renderCanvas();
    }

    private void handleStackPop() {
        stopTimeline();
        if (stack.isEmpty()) {
            feedbackLabel.setText("Stack Underflow: Stack is empty.");
            return;
        }
        int popped = stack.pop();
        highlightedValue = -1;
        activePointerIndex = -1;
        feedbackLabel.setText("Popped " + popped + " from Stack TOP in O(1) time.");
        updateCount();
        renderCanvas();
    }

    private void handleStackPeek() {
        stopTimeline();
        if (stack.isEmpty()) {
            feedbackLabel.setText("Stack is empty.");
            return;
        }
        highlightedValue = stack.peek();
        activePointerIndex = stack.size() - 1;
        feedbackLabel.setText("Stack TOP element is " + highlightedValue + ".");
        renderCanvas();
    }

    private void handleStackSearch() {
        stopTimeline();
        int v = getInputValue();
        int pos = stack.search(v);
        if (pos != -1) {
            highlightedValue = v;
            activePointerIndex = stack.size() - pos;
            feedbackLabel.setText("Found " + v + " at 1-based distance " + pos + " from Stack TOP.");
        } else {
            highlightedValue = -1;
            activePointerIndex = -1;
            feedbackLabel.setText("Value " + v + " not found in Stack.");
        }
        renderCanvas();
    }

    private void handleQueueEnqueue() {
        stopTimeline();
        int v = getInputValue();
        queue.addLast(v);
        highlightedValue = v;
        activePointerIndex = queue.size() - 1;
        feedbackLabel.setText("Enqueued " + v + " at Queue REAR in O(1) time.");
        updateCount();
        renderCanvas();
    }

    private void handleQueueDequeue() {
        stopTimeline();
        if (queue.isEmpty()) {
            feedbackLabel.setText("Queue Underflow: Queue is empty.");
            return;
        }
        int val = queue.removeFirst();
        highlightedValue = -1;
        activePointerIndex = -1;
        feedbackLabel.setText("Dequeued " + val + " from Queue FRONT in O(1) time.");
        updateCount();
        renderCanvas();
    }

    private void handleQueueFront() {
        stopTimeline();
        if (queue.isEmpty()) {
            feedbackLabel.setText("Queue is empty.");
            return;
        }
        highlightedValue = queue.getFirst();
        activePointerIndex = 0;
        feedbackLabel.setText("Queue FRONT element is " + highlightedValue + ".");
        renderCanvas();
    }

    private void handleQueueSearch() {
        stopTimeline();
        int v = getInputValue();
        int idx = queue.indexOf(v);
        if (idx != -1) {
            highlightedValue = v;
            activePointerIndex = idx;
            feedbackLabel.setText("Found " + v + " at Queue position index [" + idx + "].");
        } else {
            highlightedValue = -1;
            activePointerIndex = -1;
            feedbackLabel.setText("Value " + v + " not in Queue.");
        }
        renderCanvas();
    }

    private void handleClear() {
        stopTimeline();
        switch (currentType) {
            case SINGLY_LIST -> singlyList.clear();
            case DOUBLY_LIST -> doublyList.clear();
            case BST -> { bstRoot = null; bstSize = 0; }
            case STACK -> stack.clear();
            case QUEUE -> queue.clear();
        }
        highlightedValue = -1;
        activePointerIndex = -1;
        bstPathHighlight.clear();
        feedbackLabel.setText("Cleared data structure.");
        updateCount();
        renderCanvas();
    }

    private void updateCount() {
        int c = switch (currentType) {
            case SINGLY_LIST -> singlyList.size();
            case DOUBLY_LIST -> doublyList.size();
            case BST -> bstSize;
            case STACK -> stack.size();
            case QUEUE -> queue.size();
        };
        countLabel.setText(String.valueOf(c));
    }

    // =========================================================================
    // Canvas Renderers
    // =========================================================================
    private void renderCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        switch (currentType) {
            case SINGLY_LIST -> renderSinglyList(gc, w, h);
            case DOUBLY_LIST -> renderDoublyList(gc, w, h);
            case BST -> renderBST(gc, w, h);
            case STACK -> renderStack(gc, w, h);
            case QUEUE -> renderQueue(gc, w, h);
        }
    }

    private void renderSinglyList(GraphicsContext gc, double w, double h) {
        if (singlyList.isEmpty()) {
            gc.setFill(Color.web("#64748B"));
            gc.fillText("[ Singly Linked List is Empty: HEAD -> NULL ]", w * 0.35, h * 0.5);
            return;
        }

        double startX = 55;
        double startY = h * 0.45;
        double nodeW = 80;
        double nodeH = 44;
        double arrowLen = 38;

        gc.setFill(Color.web("#00F5D4"));
        gc.fillText("HEAD", startX - 42, startY + 27);

        for (int i = 0; i < singlyList.size(); i++) {
            int val = singlyList.get(i);
            double x = startX + i * (nodeW + arrowLen);
            if (x + nodeW + arrowLen > w) break;

            boolean isCurrent = (i == activePointerIndex);
            boolean isFound = (val == highlightedValue);

            // Node Body
            LinearGradient grad = isFound
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")))
                    : (isCurrent
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                    : new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A"))));

            gc.setFill(grad);
            gc.fillRoundRect(x, startY, nodeW * 0.65, nodeH, 8, 8);

            // Pointer Box
            gc.setFill(Color.web("#334155"));
            gc.fillRoundRect(x + nodeW * 0.65, startY, nodeW * 0.35, nodeH, 8, 8);

            gc.setStroke(isCurrent || isFound ? Color.web("#FFFFFF") : Color.web("#475569"));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(x, startY, nodeW, nodeH, 8, 8);

            // Value Text
            gc.setFill(isCurrent || isFound ? Color.web("#070B12") : Color.web("#FFFFFF"));
            String valStr = String.valueOf(val);
            gc.fillText(valStr, x + 16, startY + 27);

            // Pointer Dot
            gc.setFill(Color.web("#00BBF9"));
            gc.fillOval(x + nodeW * 0.78, startY + 17, 9, 9);

            // Arrow to next
            double aStart = x + nodeW;
            double aEnd = aStart + arrowLen;
            gc.setStroke(Color.web("#00F5D4"));
            gc.setLineWidth(2.0);
            gc.strokeLine(aStart, startY + 22, aEnd, startY + 22);
            gc.fillPolygon(new double[]{aEnd, aEnd - 7, aEnd - 7}, new double[]{startY + 22, startY + 17, startY + 27}, 3);

            // Pointer label above active node
            if (isCurrent) {
                gc.setFill(isFound ? Color.web("#00F5D4") : Color.web("#FFB703"));
                gc.fillText("🔎 POINTER", x + 10, startY - 10);
            }
        }

        // NULL
        double lastX = startX + singlyList.size() * (nodeW + arrowLen);
        if (lastX + 45 < w) {
            gc.setFill(Color.web("#EF4444"));
            gc.fillText("NULL", lastX + 8, startY + 27);
        }
    }

    private void renderDoublyList(GraphicsContext gc, double w, double h) {
        if (doublyList.isEmpty()) {
            gc.setFill(Color.web("#64748B"));
            gc.fillText("[ Doubly Linked List is Empty: NULL <- HEAD <-> NULL ]", w * 0.32, h * 0.5);
            return;
        }

        double startX = 65;
        double startY = h * 0.45;
        double nodeW = 95;
        double nodeH = 44;
        double arrowLen = 38;

        // Leading NULL
        gc.setFill(Color.web("#EF4444"));
        gc.fillText("NULL", startX - 52, startY + 27);
        gc.setStroke(Color.web("#64748B"));
        gc.strokeLine(startX - 18, startY + 22, startX - 2, startY + 22);

        for (int i = 0; i < doublyList.size(); i++) {
            int val = doublyList.get(i);
            double x = startX + i * (nodeW + arrowLen);
            if (x + nodeW + arrowLen > w) break;

            boolean isCurrent = (i == activePointerIndex);
            boolean isFound = (val == highlightedValue);

            // Prev box
            gc.setFill(Color.web("#334155"));
            gc.fillRoundRect(x, startY, nodeW * 0.25, nodeH, 6, 6);

            // Data box
            LinearGradient grad = isFound
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")))
                    : (isCurrent
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                    : new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A"))));

            gc.setFill(grad);
            gc.fillRoundRect(x + nodeW * 0.25, startY, nodeW * 0.50, nodeH, 6, 6);

            // Next box
            gc.setFill(Color.web("#334155"));
            gc.fillRoundRect(x + nodeW * 0.75, startY, nodeW * 0.25, nodeH, 6, 6);

            gc.setStroke(isCurrent || isFound ? Color.web("#FFFFFF") : Color.web("#475569"));
            gc.strokeRoundRect(x, startY, nodeW, nodeH, 6, 6);

            // Data value
            gc.setFill(isCurrent || isFound ? Color.web("#070B12") : Color.web("#FFFFFF"));
            gc.fillText(String.valueOf(val), x + nodeW * 0.38, startY + 27);

            // Forward arrow (top line)
            double aStart = x + nodeW;
            double aEnd = aStart + arrowLen;
            gc.setStroke(Color.web("#00F5D4"));
            gc.setLineWidth(1.8);
            gc.strokeLine(aStart, startY + 16, aEnd, startY + 16);
            gc.fillPolygon(new double[]{aEnd, aEnd - 5, aEnd - 5}, new double[]{startY + 16, startY + 12, startY + 20}, 3);

            // Backward arrow (bottom line)
            gc.setStroke(Color.web("#00BBF9"));
            gc.strokeLine(aEnd, startY + 28, aStart, startY + 28);
            gc.fillPolygon(new double[]{aStart, aStart + 5, aStart + 5}, new double[]{startY + 28, startY + 24, startY + 32}, 3);

            if (isCurrent) {
                gc.setFill(isFound ? Color.web("#00F5D4") : Color.web("#FFB703"));
                gc.fillText("🔎 POINTER", x + 15, startY - 10);
            }
        }

        // Trailing NULL
        double lastX = startX + doublyList.size() * (nodeW + arrowLen);
        if (lastX + 45 < w) {
            gc.setFill(Color.web("#EF4444"));
            gc.fillText("NULL", lastX + 8, startY + 27);
        }
    }

    private void renderBST(GraphicsContext gc, double w, double h) {
        if (bstRoot == null) {
            gc.setFill(Color.web("#64748B"));
            gc.fillText("[ Binary Search Tree is Empty ]", w * 0.40, h * 0.5);
            return;
        }
        drawBSTNode(gc, bstRoot, w / 2, 45, w * 0.23, 1);
    }

    private void drawBSTNode(GraphicsContext gc, BSTNode node, double x, double y, double xOffset, int level) {
        if (node == null) return;

        double radius = 20;

        // Draw connecting branches to children
        if (node.left != null) {
            double cx = x - xOffset;
            double cy = y + 62;
            boolean inPath = bstPathHighlight.contains(node.val) && bstPathHighlight.contains(node.left.val);
            gc.setStroke(inPath ? Color.web("#FFB703") : Color.web("#334155"));
            gc.setLineWidth(inPath ? 3.0 : 2.0);
            gc.strokeLine(x, y + radius, cx, cy - radius);
            drawBSTNode(gc, node.left, cx, cy, xOffset * 0.52, level + 1);
        }

        if (node.right != null) {
            double cx = x + xOffset;
            double cy = y + 62;
            boolean inPath = bstPathHighlight.contains(node.val) && bstPathHighlight.contains(node.right.val);
            gc.setStroke(inPath ? Color.web("#FFB703") : Color.web("#334155"));
            gc.setLineWidth(inPath ? 3.0 : 2.0);
            gc.strokeLine(x, y + radius, cx, cy - radius);
            drawBSTNode(gc, node.right, cx, cy, xOffset * 0.52, level + 1);
        }

        boolean isHighlighted = (node.val == highlightedValue);
        boolean inPath = bstPathHighlight.contains(node.val);

        LinearGradient grad = isHighlighted
                ? new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")))
                : (inPath
                ? new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                : new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A"))));

        gc.setFill(grad);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setStroke(isHighlighted || inPath ? Color.web("#FFFFFF") : Color.web("#00BBF9"));
        gc.setLineWidth(isHighlighted ? 2.5 : 1.5);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setFill(isHighlighted || inPath ? Color.web("#070B12") : Color.web("#FFFFFF"));
        String valStr = String.valueOf(node.val);
        gc.fillText(valStr, x - (valStr.length() * 3.6), y + 5);
    }

    private void renderStack(GraphicsContext gc, double w, double h) {
        double bucketW = 180;
        double bucketX = (w - bucketW) / 2;
        double itemH = 32;
        double baseY = h - 45;

        // Draw Container Walls
        gc.setStroke(Color.web("#475569"));
        gc.setLineWidth(3.0);
        gc.strokeLine(bucketX, 40, bucketX, baseY);
        gc.strokeLine(bucketX, baseY, bucketX + bucketW, baseY);
        gc.strokeLine(bucketX + bucketW, baseY, bucketX + bucketW, 40);

        gc.setFill(Color.web("#94A3B8"));
        gc.fillText("STACK CONTAINER (LIFO - Top Access Only)", bucketX - 40, 30);

        if (stack.isEmpty()) {
            gc.setFill(Color.web("#64748B"));
            gc.fillText("[ Stack is Empty ]", bucketX + 35, baseY - 40);
            return;
        }

        for (int i = 0; i < stack.size(); i++) {
            int val = stack.get(i);
            double y = baseY - ((i + 1) * (itemH + 5));
            boolean isTop = (i == stack.size() - 1);
            boolean isFound = (val == highlightedValue);

            LinearGradient grad = isFound
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")))
                    : (isTop
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                    : new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A"))));

            gc.setFill(grad);
            gc.fillRoundRect(bucketX + 8, y, bucketW - 16, itemH, 6, 6);

            gc.setStroke(isTop || isFound ? Color.web("#FFFFFF") : Color.web("#334155"));
            gc.strokeRoundRect(bucketX + 8, y, bucketW - 16, itemH, 6, 6);

            gc.setFill(isTop || isFound ? Color.web("#070B12") : Color.web("#FFFFFF"));
            gc.fillText(String.valueOf(val), bucketX + (bucketW / 2) - 10, y + 21);

            if (isTop) {
                gc.setFill(Color.web("#00F5D4"));
                gc.fillText("← TOP", bucketX + bucketW + 12, y + 21);
            }
        }
    }

    private void renderQueue(GraphicsContext gc, double w, double h) {
        double trackY = h * 0.42;
        double trackH = 64;
        double nodeW = 68;
        double startX = w * 0.15;

        // Pipeline Rails
        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(2.0);
        gc.strokeLine(startX - 40, trackY, w * 0.85, trackY);
        gc.strokeLine(startX - 40, trackY + trackH, w * 0.85, trackY + trackH);

        gc.setFill(Color.web("#94A3B8"));
        gc.fillText("QUEUE PIPELINE (FIFO - Enqueue at REAR, Dequeue from FRONT)", startX, trackY - 25);

        if (queue.isEmpty()) {
            gc.setFill(Color.web("#64748B"));
            gc.fillText("[ Queue is Empty: FRONT <---> REAR ]", w * 0.36, trackY + 36);
            return;
        }

        for (int i = 0; i < queue.size(); i++) {
            int val = queue.get(i);
            double x = startX + i * (nodeW + 14);
            if (x + nodeW > w * 0.85) break;

            boolean isFront = (i == 0);
            boolean isRear = (i == queue.size() - 1);
            boolean isFound = (val == highlightedValue);

            LinearGradient grad = isFound
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")))
                    : (isFront
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#00BBF9")))
                    : (isRear
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                    : new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A")))));

            gc.setFill(grad);
            gc.fillRoundRect(x, trackY + 8, nodeW, trackH - 16, 8, 8);

            gc.setStroke(isFound || isFront || isRear ? Color.web("#FFFFFF") : Color.web("#334155"));
            gc.strokeRoundRect(x, trackY + 8, nodeW, trackH - 16, 8, 8);

            gc.setFill(isFound || isFront || isRear ? Color.web("#070B12") : Color.web("#FFFFFF"));
            gc.fillText(String.valueOf(val), x + 24, trackY + 36);

            if (isFront) {
                gc.setFill(Color.web("#00F5D4"));
                gc.fillText("▲ FRONT", x + 10, trackY - 8);
            }
            if (isRear) {
                gc.setFill(Color.web("#FFB703"));
                gc.fillText("▼ REAR", x + 12, trackY + trackH + 18);
            }
        }
    }

    private void goBack() {
        stopTimeline();
        Navigation.slideBack((Parent) backBtn.getScene().getRoot(), "/algexplorer/ui/HubPane.fxml");
    }
}
