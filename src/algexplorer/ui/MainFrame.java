package algexplorer.ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/** The main window with one tab per module. */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Algorithm Explorer");
        setSize(1050, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centre on the screen

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Recursion Lab", new RecursionPanel());
        tabs.addTab("Data Structures Lab", new DataStructuresPanel());
        tabs.addTab("Sorting Lab", new SortingPanel());
        tabs.addTab("Complexity Analysis", new ComplexityPanel());

        add(tabs); // put the tabs inside the window
    }
}
