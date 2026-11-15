package algexplorer.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** The Recursion Lab: one inner tab per screen. */
public class RecursionPanel extends JPanel {
    public RecursionPanel() {
        setLayout(new BorderLayout());
        JTabbedPane inner = new JTabbedPane();
        inner.addTab("Fibonacci", new FibonacciPanel());
        inner.addTab("Factorial", new FactorialPanel());
        inner.addTab("Tower of Hanoi", new HanoiPanel());
        add(inner, BorderLayout.CENTER);
    }
}