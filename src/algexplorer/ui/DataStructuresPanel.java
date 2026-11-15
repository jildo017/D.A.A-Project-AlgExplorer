package algexplorer.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** The Data Structures Lab: one inner tab per structure. */
public class DataStructuresPanel extends JPanel {
    public DataStructuresPanel() {
        setLayout(new BorderLayout());
        JTabbedPane inner = new JTabbedPane();
        inner.addTab("Stack", new StackPanel());
        inner.addTab("Linear Queue", new QueuePanel(false));
        inner.addTab("Circular Queue", new QueuePanel(true));
        inner.addTab("Bracket Checker", new BracketPanel());
        inner.addTab("Singly Linked List", new ListPanel(false));
        inner.addTab("Doubly Linked List", new ListPanel(true));
        add(inner, BorderLayout.CENTER);
    }
}