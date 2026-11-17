package ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SCREEN 2: dashboard. A sidebar for navigation, a scrolling page with overview cards
 * and the full list of algorithms / data structures grouped in bordered sections.
 */
public class CategoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final MainFrame frame;
    private final ScrollPanel content = new ScrollPanel();
    private final JScrollPane scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JComponent secSorting;
    private JComponent secSearching;
    private JComponent secStructures;
    private JComponent secRecursion;
    private final NavButton[] navs = new NavButton[8];
    private int navCount = 0;
    private int row = 0;
    private javax.swing.Timer scrollTimer;

    public CategoryPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG);

        buildContent();
        add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(MainFrame.BG);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(MainFrame.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        styleScrollBar(scroll.getVerticalScrollBar());
        main.add(scroll, BorderLayout.CENTER);
        main.add(buildBottomBar(), BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);
    }

    // ================================================================== sidebar

    private JPanel buildSidebar() {
        JPanel sb = new JPanel(new GridBagLayout());
        sb.setBackground(MainFrame.SIDEBAR);
        sb.setPreferredSize(new Dimension(250, 0));
        sb.setBorder(new MatteBorder(0, 0, 0, 1, MainFrame.BORDER));

        JPanel logo = new JPanel(new BorderLayout(12, 0));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(22, 18, 18, 12));
        logo.add(new IconBadge("logo", 44), BorderLayout.WEST);
        JPanel names = new JPanel(new GridLayout(2, 1));
        names.setOpaque(false);
        names.add(label("Algorithm Explorer", 16, Font.BOLD, Color.WHITE));
        names.add(label("Interactive Learning", 12, Font.PLAIN, MainFrame.MUTED));
        logo.add(names, BorderLayout.CENTER);
        addSidebarRow(sb, logo);

        addNav(sb, "Dashboard", true, () -> scrollToY(0)).setActive(true);
        addSidebarRow(sb, sectionLabel("ALGORITHMS"));
        addNav(sb, "Sorting", true, () -> scrollTo(secSorting));
        addNav(sb, "Searching", true, () -> scrollTo(secSearching));
        addNav(sb, "Recursion", true, () -> scrollTo(secRecursion));
        addNav(sb, "Benchmark", false, () -> frame.showScreen("BENCHMARK"));
        addSidebarRow(sb, sectionLabel("DATA STRUCTURES"));
        addNav(sb, "Stack", false, () -> frame.openWorkspace("Custom Stack"));
        addNav(sb, "Queue", false, () -> frame.openWorkspace("Custom Queue"));
        addNav(sb, "Linked List", false, () -> frame.openWorkspace("Custom Linked List"));

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = 100;
        filler.weighty = 1;
        JPanel space = new JPanel();
        space.setOpaque(false);
        sb.add(space, filler);
        return sb;
    }

    private int sideRow = 0;

    private void addSidebarRow(JPanel sb, JComponent c) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = sideRow++;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTH;
        gc.insets = new Insets(1, 10, 1, 10);
        if (!(c instanceof NavButton)) {
            gc.insets = new Insets(0, 0, 0, 0);
        }
        sb.add(c, gc);
    }

    private JLabel sectionLabel(String text) {
        JLabel l = label(text, 11, Font.BOLD, new Color(120, 125, 155));
        l.setBorder(new EmptyBorder(18, 22, 6, 0));
        return l;
    }

    private NavButton addNav(JPanel sb, String text, final boolean keepsActive, final Runnable action) {
        final NavButton b = new NavButton(text);
        b.addActionListener(e -> {
            if (keepsActive) {
                for (int i = 0; i < navCount; i++) {
                    navs[i].setActive(false);
                }
                b.setActive(true);
            }
            action.run();
        });
        navs[navCount++] = b;
        addSidebarRow(sb, b);
        return b;
    }

    private static class NavButton extends JButton {
        private static final long serialVersionUID = 1L;
        private boolean active = false;
        private boolean hover = false;

        NavButton(String text) {
            super(text);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("SansSerif", Font.PLAIN, 15));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(new EmptyBorder(11, 24, 11, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(new Color(200, 205, 225));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        void setActive(boolean a) {
            active = a;
            setForeground(a ? new Color(165, 168, 255) : new Color(200, 205, 225));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(new Color(40, 40, 82));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(MainFrame.PURPLE);
                g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
            } else if (hover) {
                g2.setColor(new Color(36, 36, 56));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ================================================================== bottom bar

    private JPanel buildBottomBar() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        nav.setBackground(MainFrame.BG);
        nav.setBorder(new MatteBorder(1, 0, 0, 0, MainFrame.BORDER));
        JButton home = MainFrame.makeButton("Return to Home Screen", MainFrame.BLUE);
        JButton exit = MainFrame.makeButton("Exit App", MainFrame.RED);
        home.addActionListener(e -> frame.showScreen("HOME"));
        exit.addActionListener(e -> System.exit(0));
        nav.add(home);
        nav.add(exit);
        return nav;
    }

    // ================================================================== main content

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color);
        return l;
    }

    private void addRow(JComponent c, int top, int bottom) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = row++;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTH;
        gc.insets = new Insets(top, 0, bottom, 0);
        content.add(c, gc);
    }

    private void buildContent() {
        content.setLayout(new GridBagLayout());
        content.setBackground(MainFrame.BG);
        content.setBorder(new EmptyBorder(26, 34, 26, 34));

        JLabel title = label("Select an Algorithm or Data Structure", 34, Font.BOLD, Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel sub = label("Interactive step-by-step visualizations. Choose a topic below to begin.", 16, Font.PLAIN, MainFrame.MUTED);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        addRow(title, 8, 6);
        addRow(sub, 0, 18);

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 0));
        stats.setOpaque(false);
        stats.add(statBlock(String.valueOf(MainFrame.TOTAL_ALGORITHMS), "Algorithms"));
        stats.add(statBlock(String.valueOf(MainFrame.TOTAL_CATEGORIES), "Categories"));
        stats.add(statBlock("0", "Built-in sorts used"));
        addRow(stats, 0, 22);

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        grid.add(new CategoryCard("sort", "Sorting", "11 algos",
                "Bubble, Selection, Insertion, Shell, Merge, Quick, Heap, Counting, Radix, Bucket, Pancake", () -> scrollTo(secSorting)));
        grid.add(new CategoryCard("search", "Searching", "4 algos",
                "Linear, Binary, Jump and Interpolation Search", () -> scrollTo(secSearching)));
        grid.add(new CategoryCard("ds", "Data Structures", "3 types",
                "Stack, Queue and Linked List with a log of every pointer move", () -> scrollTo(secStructures)));
        grid.add(new CategoryCard("rec", "Recursion", "4 algos",
                "Factorial, Fibonacci, Tower of Hanoi, Recursive Binary Search", () -> scrollTo(secRecursion)));
        grid.add(new CategoryCard("bench", "Benchmark", "11 sorts",
                "Race all sorting algorithms on the same random array", () -> frame.showScreen("BENCHMARK")));
        grid.add(new CategoryCard("bigo", "Complexity", "Big-O",
                "Every screen shows Best, Average, Worst and Space complexity", () -> scrollTo(secSorting)));
        addRow(grid, 0, 26);

        String[] sorts = algorithms.SortingAlgorithms.NAMES;
        JPanel sortBody = new JPanel(new BorderLayout(0, 12));
        sortBody.setOpaque(false);
        sortBody.add(buttonGrid(sorts, 0, 4), BorderLayout.CENTER);
        JButton bench = MainFrame.makeButton("Benchmark All Sorts", MainFrame.GREEN);
        bench.setFont(new Font("SansSerif", Font.BOLD, 18));
        bench.addActionListener(e -> frame.showScreen("BENCHMARK"));
        sortBody.add(bench, BorderLayout.SOUTH);
        secSorting = section("Sorting Algorithms", "Watch every comparison, swap and shift. Pick one to open its workspace.", sortBody);
        addRow(secSorting, 0, 18);

        secSearching = section("Searching Algorithms", "Find a target in a list and see exactly which positions are checked.",
                buttonGrid(new String[] {"Linear Search", "Binary Search", "Jump Search", "Interpolation Search"}, 1, 4));
        addRow(secSearching, 0, 18);

        secStructures = section("Data Structures", "Built from linked nodes only. Every pointer change is logged.",
                buttonGrid(new String[] {"Custom Stack", "Custom Queue", "Custom Linked List"}, 1, 3));
        addRow(secStructures, 0, 18);

        secRecursion = section("Recursion", "Follow every call, base case and return value.",
                buttonGrid(new String[] {"Factorial", "Fibonacci", "Tower of Hanoi", "Recursive Binary Search"}, 1, 4));
        addRow(secRecursion, 0, 10);

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = row++;
        filler.weighty = 1;
        JPanel space = new JPanel();
        space.setOpaque(false);
        content.add(space, filler);
    }

    private JPanel statBlock(String value, String text) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel v = label(value, 34, Font.BOLD, MainFrame.PURPLE_LIGHT);
        v.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel t = label(text, 14, Font.PLAIN, MainFrame.MUTED);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(v);
        p.add(t);
        return p;
    }

    private JPanel section(String title, String subtitle, JComponent body) {
        MainFrame.RoundPanel p = new MainFrame.RoundPanel(new BorderLayout(0, 14), MainFrame.CARD, MainFrame.BORDER, 22);
        p.setBorder(new EmptyBorder(18, 20, 20, 20));
        JPanel head = new JPanel(new GridLayout(2, 1, 0, 2));
        head.setOpaque(false);
        head.add(label(title, 22, Font.BOLD, Color.WHITE));
        head.add(label(subtitle, 13, Font.PLAIN, MainFrame.MUTED));
        p.add(head, BorderLayout.NORTH);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private JPanel buttonGrid(String[] names, int rows, int cols) {
        JPanel grid = new JPanel(new GridLayout(rows, cols, 12, 12));
        grid.setOpaque(false);
        for (final String n : names) {
            JButton b = MainFrame.makeButton(n, MainFrame.BLUE);
            b.addActionListener(e -> frame.openWorkspace(n));
            grid.add(b);
        }
        return grid;
    }

    // ================================================================== smooth scrolling

    private void scrollTo(JComponent target) {
        scrollToY(target.getY() - 6);
    }

    private void scrollToY(int wanted) {
        final JViewport vp = scroll.getViewport();
        int maxY = Math.max(0, content.getHeight() - vp.getHeight());
        final int endY = Math.min(maxY, Math.max(0, wanted));
        final int startY = vp.getViewPosition().y;
        if (scrollTimer != null) {
            scrollTimer.stop();
        }
        final long begin = System.nanoTime();
        scrollTimer = new javax.swing.Timer(15, null);
        scrollTimer.addActionListener(e -> {
            double p = Math.min(1, (System.nanoTime() - begin) / 450e6);
            double ease = 1 - Math.pow(1 - p, 3);
            vp.setViewPosition(new Point(0, (int) (startY + (endY - startY) * ease)));
            if (p >= 1) {
                scrollTimer.stop();
            }
        });
        scrollTimer.start();
    }

    /** Test helper: jumps the page to a vertical position. */
    void setScrollPosition(int y) {
        scroll.getViewport().setViewPosition(new Point(0, y));
    }

    private static void styleScrollBar(JScrollBar bar) {
        bar.setPreferredSize(new Dimension(12, 12));
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(85, 85, 120);
                trackColor = MainFrame.BG;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return zeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return zeroButton();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(trackColor);
                g.fillRect(r.x, r.y, r.width, r.height);
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
                g2.dispose();
            }
        });
    }

    private static JButton zeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    /** Scrolling page that always matches the viewport width. */
    private static class ScrollPanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return (int) (visibleRect.height * 0.9);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // ================================================================== overview cards

    private static class Pill extends JLabel {
        private static final long serialVersionUID = 1L;

        Pill(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setForeground(new Color(165, 168, 255));
            setBorder(new EmptyBorder(3, 9, 3, 9));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(48, 48, 100));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CategoryCard extends MainFrame.RoundPanel {
        private static final long serialVersionUID = 1L;

        CategoryCard(String kind, String title, String badge, String description, final Runnable action) {
            super(new BorderLayout(16, 0), new Color(36, 36, 56), MainFrame.BORDER, 22);
            setBorder(new EmptyBorder(18, 18, 18, 18));
            setPreferredSize(new Dimension(280, 130));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel iconHolder = new JPanel(new BorderLayout());
            iconHolder.setOpaque(false);
            iconHolder.add(new IconBadge(kind, 56), BorderLayout.NORTH);
            add(iconHolder, BorderLayout.WEST);

            JPanel text = new JPanel(new BorderLayout(0, 6));
            text.setOpaque(false);
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            top.setOpaque(false);
            JLabel titleLabel = label(title, 18, Font.BOLD, Color.WHITE);
            titleLabel.setBorder(new EmptyBorder(0, 0, 0, 8));
            top.add(titleLabel);
            top.add(new Pill(badge));
            text.add(top, BorderLayout.NORTH);
            JLabel desc = label("<html><div style='width:138px'>" + description + "</div></html>", 12, Font.PLAIN, MainFrame.MUTED);
            desc.setVerticalAlignment(SwingConstants.TOP);
            text.add(desc, BorderLayout.CENTER);
            add(text, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBorderColor(MainFrame.PURPLE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorderColor(MainFrame.BORDER);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    action.run();
                }
            });
        }
    }

    /** Gradient rounded square with a small drawn symbol. */
    private static class IconBadge extends JComponent {
        private static final long serialVersionUID = 1L;
        private final String kind;
        private final int size;

        IconBadge(String kind, int size) {
            this.kind = kind;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        private Color[] colors() {
            switch (kind) {
                case "search": return new Color[] {new Color(59, 130, 246), new Color(6, 182, 212)};
                case "ds": return new Color[] {new Color(20, 184, 166), new Color(34, 197, 94)};
                case "rec": return new Color[] {new Color(249, 115, 22), new Color(245, 158, 11)};
                case "bench": return new Color[] {new Color(34, 197, 94), new Color(132, 204, 22)};
                case "bigo": return new Color[] {new Color(236, 72, 153), new Color(168, 85, 247)};
                case "logo": return new Color[] {new Color(99, 102, 241), new Color(168, 85, 247)};
                default: return new Color[] {new Color(99, 102, 241), new Color(139, 92, 246)};
            }
        }

        private void centered(Graphics2D g, String text, double sizeFactor) {
            g.setFont(new Font("SansSerif", Font.BOLD, (int) (size * sizeFactor)));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (size - fm.stringWidth(text)) / 2, (size + fm.getAscent()) / 2 - 3);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Color[] cs = colors();
            g.setPaint(new GradientPaint(0, 0, cs[0], size, size, cs[1]));
            g.fillRoundRect(0, 0, size, size, size / 3, size / 3);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(size / 18f + 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double s = size;
            switch (kind) {
                case "sort":
                    for (int i = 0; i < 4; i++) {
                        int bh = (int) (s * (0.16 + i * 0.1));
                        g.fillRoundRect((int) (s * 0.24 + i * s * 0.14), (int) (s * 0.74) - bh, (int) (s * 0.09), bh, 4, 4);
                    }
                    break;
                case "search":
                    g.drawOval((int) (s * 0.26), (int) (s * 0.26), (int) (s * 0.34), (int) (s * 0.34));
                    g.drawLine((int) (s * 0.57), (int) (s * 0.57), (int) (s * 0.76), (int) (s * 0.76));
                    break;
                case "ds":
                    g.fillOval((int) (s * 0.18), (int) (s * 0.4), (int) (s * 0.2), (int) (s * 0.2));
                    g.fillOval((int) (s * 0.62), (int) (s * 0.4), (int) (s * 0.2), (int) (s * 0.2));
                    g.drawLine((int) (s * 0.4), (int) (s * 0.5), (int) (s * 0.6), (int) (s * 0.5));
                    break;
                case "rec":
                    g.drawRoundRect((int) (s * 0.2), (int) (s * 0.2), (int) (s * 0.6), (int) (s * 0.6), 8, 8);
                    g.drawRoundRect((int) (s * 0.33), (int) (s * 0.33), (int) (s * 0.34), (int) (s * 0.34), 6, 6);
                    g.fillRoundRect((int) (s * 0.45), (int) (s * 0.45), (int) (s * 0.1), (int) (s * 0.1), 3, 3);
                    break;
                case "bench":
                    g.drawLine((int) (s * 0.24), (int) (s * 0.24), (int) (s * 0.24), (int) (s * 0.74));
                    g.drawLine((int) (s * 0.24), (int) (s * 0.74), (int) (s * 0.78), (int) (s * 0.74));
                    g.drawLine((int) (s * 0.3), (int) (s * 0.62), (int) (s * 0.44), (int) (s * 0.46));
                    g.drawLine((int) (s * 0.44), (int) (s * 0.46), (int) (s * 0.58), (int) (s * 0.56));
                    g.drawLine((int) (s * 0.58), (int) (s * 0.56), (int) (s * 0.76), (int) (s * 0.3));
                    break;
                case "bigo":
                    centered(g, "O(n)", 0.3);
                    break;
                default:
                    centered(g, "AE", 0.42);
                    break;
            }
            g.dispose();
        }
    }
}
