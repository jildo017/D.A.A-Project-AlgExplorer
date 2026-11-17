package ui;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

/**
 * SCREEN 1: animated splash. The app name fades in, the stats count up, then the
 * screen scrolls up to the dashboard automatically (or when Start / Next is pressed).
 */
public class HomePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final double BUTTONS_AT = 1.7;
    private static final double AUTO_SCROLL_AT = 4.2;

    private final MainFrame frame;
    private final JButton start = MainFrame.makeButton("Start / Next", MainFrame.GREEN);
    private final JButton exit = MainFrame.makeButton("Exit App", MainFrame.RED);
    private double t = 0;
    private boolean introStarted = false;
    private boolean autoScroll = true;
    private javax.swing.Timer timer;

    public HomePanel(final MainFrame frame) {
        this.frame = frame;
        setBackground(MainFrame.BG);
        setLayout(null);

        for (JButton b : new JButton[] {start, exit}) {
            b.setFont(new Font("SansSerif", Font.BOLD, 18));
            b.setVisible(false);
            add(b);
        }
        start.addActionListener(e -> {
            autoScroll = false;
            stopTimer();
            frame.showScreen("CATEGORIES");
        });
        exit.addActionListener(e -> System.exit(0));
    }

    /** Begins the intro animation (runs once). */
    public void startIntro() {
        if (introStarted) {
            return;
        }
        introStarted = true;
        final long begin = System.nanoTime();
        timer = new javax.swing.Timer(16, null);
        timer.addActionListener(e -> {
            setIntroTime((System.nanoTime() - begin) / 1e9);
            if (autoScroll && t >= AUTO_SCROLL_AT) {
                autoScroll = false;
                stopTimer();
                if ("HOME".equals(frame.getCurrentScreen())) {
                    frame.showScreen("CATEGORIES");
                }
            } else if (!autoScroll && t > 2.6) {
                stopTimer();
            }
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    /** Sets the animation clock (seconds). Also used to render a specific frame. */
    void setIntroTime(double seconds) {
        t = seconds;
        boolean show = t > BUTTONS_AT;
        start.setVisible(show);
        exit.setVisible(show);
        repaint();
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        int y = (int) (h * 0.64);
        start.setBounds(w / 2 - 130, y, 260, 54);
        exit.setBounds(w / 2 - 130, y + 68, 260, 54);
    }

    private static double ease(double v) {
        if (v < 0) {
            v = 0;
        }
        if (v > 1) {
            v = 1;
        }
        return 1 - Math.pow(1 - v, 3);
    }

    private static void alpha(Graphics2D g, double a) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.max(0, Math.min(1, a))));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        g.setPaint(new GradientPaint(0, 0, new Color(34, 34, 54), 0, h, new Color(21, 21, 33)));
        g.fillRect(0, 0, w, h);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(w / 2f, h * 0.28f), Math.max(10, w * 0.45f),
                new float[] {0f, 1f}, new Color[] {new Color(99, 102, 241, 80), new Color(99, 102, 241, 0)}));
        g.fillRect(0, 0, w, h);

        // title
        double a1 = ease(t / 0.9);
        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        String s1 = "ALGORITHM ";
        String s2 = "EXPLORER";
        int tw = fm.stringWidth(s1) + fm.stringWidth(s2);
        int x = (w - tw) / 2;
        int y = (int) (h * 0.30 + (1 - a1) * 28);
        alpha(g, a1);
        g.setColor(Color.WHITE);
        g.drawString(s1, x, y);
        g.setColor(MainFrame.PURPLE_LIGHT);
        g.drawString(s2, x + fm.stringWidth(s1), y);

        // accent line
        int lineW = (int) (tw * ease((t - 0.3) / 0.9));
        g.setColor(MainFrame.PURPLE);
        g.fillRoundRect((w - lineW) / 2, y + 14, lineW, 4, 4, 4);

        // subtitle
        alpha(g, ease((t - 0.45) / 0.8));
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(MainFrame.MUTED);
        String sub = "Interactive Visualization, Step-by-Step Execution & Time Complexity Analyzer";
        g.drawString(sub, (w - g.getFontMetrics().stringWidth(sub)) / 2, y + 56);

        // stats
        double fade = ease((t - 1.0) / 0.7);
        double count = ease((t - 1.0) / 1.4);
        int[] values = {MainFrame.TOTAL_ALGORITHMS, MainFrame.TOTAL_CATEGORIES, 0};
        String[] labels = {"Algorithms", "Categories", "Built-in sorts used"};
        alpha(g, fade);
        for (int i = 0; i < 3; i++) {
            int cx = w / 2 + (i - 1) * 230;
            g.setFont(new Font("SansSerif", Font.BOLD, 40));
            g.setColor(MainFrame.PURPLE_LIGHT);
            String v = String.valueOf((int) Math.round(values[i] * count));
            g.drawString(v, cx - g.getFontMetrics().stringWidth(v) / 2, (int) (h * 0.48));
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g.setColor(MainFrame.MUTED);
            g.drawString(labels[i], cx - g.getFontMetrics().stringWidth(labels[i]) / 2, (int) (h * 0.48) + 28);
        }

        // hint while waiting for the automatic scroll
        if (autoScroll && t > BUTTONS_AT) {
            alpha(g, 0.75);
            g.setFont(new Font("SansSerif", Font.ITALIC, 14));
            g.setColor(MainFrame.MUTED);
            String hint = t > 3.2 ? "Scrolling to the dashboard..." : "Press Start / Next, or wait a moment";
            g.drawString(hint, (w - g.getFontMetrics().stringWidth(hint)) / 2, (int) (h * 0.64) + 160);
        }
        g.dispose();
    }
}
