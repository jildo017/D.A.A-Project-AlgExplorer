package ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/** Application window and entry point. Switches between 4 screens with a sliding transition. */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final Color BG = new Color(30, 30, 46);
    public static final Color CARD = new Color(42, 42, 60);
    public static final Color BLUE = new Color(59, 130, 246);
    public static final Color GREEN = new Color(34, 197, 94);
    public static final Color RED = new Color(239, 68, 68);
    public static final Color ORANGE = new Color(249, 115, 22);
    public static final Color PURPLE = new Color(99, 102, 241);
    public static final Color PURPLE_LIGHT = new Color(129, 140, 248);
    public static final Color SIDEBAR = new Color(24, 24, 38);
    public static final Color BORDER = new Color(62, 62, 88);
    public static final Color TEXT = Color.WHITE;
    public static final Color MUTED = new Color(170, 175, 200);

    /** 11 sorting + 4 searching + 3 data structures + 4 recursion. */
    public static final int TOTAL_ALGORITHMS = 22;
    public static final int TOTAL_CATEGORIES = 4;

    private static final String[] ORDER = {"HOME", "CATEGORIES", "WORKSPACE", "BENCHMARK"};
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final HomePanel home;
    private final CategoryPanel categories;
    private final WorkspacePanel workspace;
    private final BenchmarkPanel benchmark;
    private final SlideTransition slide = new SlideTransition();
    private String current = "HOME";
    private boolean sliding = false;

    public MainFrame() {
        super("Algorithm Explorer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Use screen bounds that EXCLUDE the Windows taskbar
        java.awt.Rectangle usable = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int w = Math.min(1250, usable.width);
        int h = Math.min(820,  usable.height);
        setSize(w, h);
        setLocation(usable.x + (usable.width - w) / 2, usable.y + (usable.height - h) / 2);

        home = new HomePanel(this);
        categories = new CategoryPanel(this);
        workspace = new WorkspacePanel(this);
        benchmark = new BenchmarkPanel(this);

        root.setBackground(BG);
        root.add(home, "HOME");
        root.add(categories, "CATEGORIES");
        root.add(workspace, "WORKSPACE");
        root.add(benchmark, "BENCHMARK");
        root.add(slide, "SLIDE");
        setContentPane(root);
        cards.show(root, "HOME");
    }

    public String getCurrentScreen() {
        return current;
    }

    /** Starts the splash animation on the home screen (called once the window is visible). */
    public void startIntro() {
        home.startIntro();
    }

    /** Switches screen with a sliding transition: HOME and CATEGORIES scroll vertically, the rest slide sideways. */
    public void showScreen(String target) {
        if (sliding || target.equals(current)) {
            return;
        }
        int w = root.getWidth();
        int h = root.getHeight();
        if (w <= 0 || h <= 0) {
            cards.show(root, target);
            current = target;
            return;
        }
        int fromIndex = indexOf(current);
        int toIndex = indexOf(target);
        int direction;
        if (current.equals("HOME") || target.equals("HOME")) {
            direction = toIndex > fromIndex ? UP : DOWN;
        } else {
            direction = toIndex > fromIndex ? LEFT : RIGHT;
        }
        BufferedImage a = snapshot(componentFor(current), w, h);
        BufferedImage b = snapshot(componentFor(target), w, h);
        sliding = true;
        final String finalTarget = target;
        slide.start(a, b, direction, new Runnable() {
            @Override
            public void run() {
                cards.show(root, finalTarget);
                current = finalTarget;
                sliding = false;
            }
        });
        cards.show(root, "SLIDE");
    }

    /** Configures the workspace for one algorithm / data structure and shows it. */
    public void openWorkspace(String itemName) {
        workspace.configure(itemName);
        showScreen("WORKSPACE");
    }

    private static int indexOf(String name) {
        for (int i = 0; i < ORDER.length; i++) {
            if (ORDER[i].equals(name)) {
                return i;
            }
        }
        return 0;
    }

    private Component componentFor(String name) {
        switch (name) {
            case "CATEGORIES": return categories;
            case "WORKSPACE": return workspace;
            case "BENCHMARK": return benchmark;
            default: return home;
        }
    }

    /** Paints a screen into an image (also works for screens that are not currently visible). */
    static BufferedImage snapshot(Component c, int w, int h) {
        c.setSize(w, h);
        layoutAll(c);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        c.paint(g);
        g.dispose();
        return img;
    }

    private static void layoutAll(Component c) {
        if (c instanceof Container) {
            Container container = (Container) c;
            container.doLayout();
            for (Component child : container.getComponents()) {
                layoutAll(child);
            }
        }
    }

    // ================================================================== shared widgets

    /** Creates a rounded, hover-aware button used across all screens. */
    public static JButton makeButton(String text, Color background) {
        return new RoundButton(text, background);
    }

    /** Rounded coloured button. */
    public static class RoundButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color base;
        private boolean hover = false;

        public RoundButton(String text, Color base) {
            super(text);
            this.base = base;
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(new EmptyBorder(9, 18, 9, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = base;
            if (!isEnabled()) {
                c = new Color(70, 70, 92);
            } else if (getModel().isPressed()) {
                c = base.darker();
            } else if (hover) {
                c = new Color(Math.min(255, base.getRed() + 25), Math.min(255, base.getGreen() + 25), Math.min(255, base.getBlue() + 25));
            }
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Rounded card panel with a thin border. */
    public static class RoundPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color fill;
        private Color borderColor;
        private final int arc;

        public RoundPanel(LayoutManager layout, Color fill, Color borderColor, int arc) {
            super(layout);
            this.fill = fill;
            this.borderColor = borderColor;
            this.arc = arc;
            setOpaque(false);
        }

        public void setBorderColor(Color c) {
            borderColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Draws two screen images sliding past each other. */
    public static class SlideTransition extends JPanel {
        private static final long serialVersionUID = 1L;
        private BufferedImage from;
        private BufferedImage to;
        private int direction;
        private double progress = 0;
        private javax.swing.Timer timer;

        public SlideTransition() {
            setBackground(BG);
        }

        public void start(BufferedImage a, BufferedImage b, int dir, final Runnable done) {
            from = a;
            to = b;
            direction = dir;
            progress = 0;
            if (timer != null) {
                timer.stop();
            }
            final long begin = System.nanoTime();
            timer = new javax.swing.Timer(15, null);
            timer.addActionListener(e -> {
                double p = (System.nanoTime() - begin) / 650e6;
                if (p >= 1) {
                    progress = 1;
                    timer.stop();
                    repaint();
                    if (done != null) {
                        done.run();
                    }
                } else {
                    progress = p;
                    repaint();
                }
            });
            timer.start();
        }

        /** Used for testing: jump to a given point of the animation (0 to 1). */
        public void setProgress(double p, BufferedImage a, BufferedImage b, int dir) {
            from = a;
            to = b;
            direction = dir;
            progress = p;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (from == null || to == null) {
                return;
            }
            int w = getWidth();
            int h = getHeight();
            double ease = 1 - Math.pow(1 - progress, 3);
            int fx = 0;
            int fy = 0;
            int tx = 0;
            int ty = 0;
            switch (direction) {
                case UP:
                    fy = (int) (-ease * h);
                    ty = h + fy;
                    break;
                case DOWN:
                    fy = (int) (ease * h);
                    ty = fy - h;
                    break;
                case LEFT:
                    fx = (int) (-ease * w);
                    tx = w + fx;
                    break;
                default:
                    fx = (int) (ease * w);
                    tx = fx - w;
                    break;
            }
            g.drawImage(from, fx, fy, null);
            g.drawImage(to, tx, ty, null);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                frame.startIntro();
            }
        });
    }
}
