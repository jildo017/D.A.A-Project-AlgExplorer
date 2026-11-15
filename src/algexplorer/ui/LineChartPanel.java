package algexplorer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** A simple line chart drawn by hand: running time (y) against n (x). NaN values are skipped. */
public class LineChartPanel extends JPanel {
    private static final Color[] COLORS = { new Color(192, 57, 43), new Color(41, 128, 185), new Color(39, 174, 96),
            new Color(142, 68, 173) };

    private String title = "";
    private int[] xs = new int[0];
    private String[] names = new String[0];
    private double[][] ys = new double[0][0];
    private boolean logScale;

    public LineChartPanel() {
        setPreferredSize(new Dimension(430, 300));
        setBackground(Color.WHITE);
    }

    /** ys[line][point] in ms. logScale draws both axes in powers of 10. */
    public void setData(String newTitle, int[] newXs, String[] newNames, double[][] newYs, boolean log) {
        title = newTitle;
        xs = newXs;
        names = newNames;
        ys = newYs;
        logScale = log;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int left = 62, right = 14, top = 34, bottom = 72;
        int w = getWidth() - left - right;
        int h = getHeight() - top - bottom;

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        g2.setColor(Color.BLACK);
        g2.drawString(title, left, 20);

        double maxY = 0;
        double minY = Double.MAX_VALUE;
        boolean any = false;
        for (int l = 0; l < ys.length; l++) {
            for (int p = 0; p < ys[l].length; p++) {
                if (!Double.isNaN(ys[l][p]) && ys[l][p] > 0) {
                    any = true;
                    maxY = Math.max(maxY, ys[l][p]);
                    minY = Math.min(minY, ys[l][p]);
                }
            }
        }
        if (!any || xs.length == 0) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 13));
            g2.setColor(Color.GRAY);
            g2.drawString("Run the experiment to see the chart.", left, getHeight() / 2);
            return;
        }

        double xMin = xs[0], xMax = xs[xs.length - 1];
        double yLo, yHi;
        if (logScale) {
            yLo = Math.pow(10, Math.floor(Math.log10(minY)));
            yHi = Math.pow(10, Math.ceil(Math.log10(maxY)));
            if (yHi <= yLo) {
                yHi = yLo * 10;
            }
        } else {
            yLo = 0;
            yHi = niceCeil(maxY);
        }

        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        // y grid and labels
        if (logScale) {
            for (double v = yLo; v <= yHi * 1.0001; v *= 10) {
                int y = top + h - (int) Math.round(h * (Math.log10(v) - Math.log10(yLo)) / (Math.log10(yHi) - Math.log10(yLo)));
                grid(g2, fm, left, w, y, formatMs(v));
            }
        } else {
            for (int t = 0; t <= 5; t++) {
                double v = yHi * t / 5.0;
                int y = top + h - (int) Math.round(h * t / 5.0);
                grid(g2, fm, left, w, y, formatMs(v));
            }
        }
        // x labels
        for (int p = 0; p < xs.length; p++) {
            int x = xPos(xs[p], xMin, xMax, left, w);
            g2.setColor(Color.GRAY);
            g2.drawLine(x, top + h, x, top + h + 4);
            String t = String.valueOf(xs[p]);
            if (p % 2 == 0 || xs.length <= 6) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(t, x - fm.stringWidth(t) / 2, top + h + 17);
            }
        }
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(left, top, left, top + h);
        g2.drawLine(left, top + h, left + w, top + h);
        g2.drawString("n (number of values)", left + w / 2 - 50, top + h + 32);
        g2.drawString("ms", 8, top + 4);

        // lines
        g2.setStroke(new BasicStroke(2f));
        for (int l = 0; l < ys.length; l++) {
            g2.setColor(COLORS[l % COLORS.length]);
            int px = -1, py = -1;
            for (int p = 0; p < xs.length; p++) {
                double v = ys[l][p];
                if (Double.isNaN(v) || v <= 0) {
                    px = -1;
                    continue;
                }
                int x = xPos(xs[p], xMin, xMax, left, w);
                double frac = logScale ? (Math.log10(v) - Math.log10(yLo)) / (Math.log10(yHi) - Math.log10(yLo))
                        : v / yHi;
                int y = top + h - (int) Math.round(h * frac);
                if (px >= 0) {
                    g2.drawLine(px, py, x, y);
                }
                g2.fillOval(x - 3, y - 3, 6, 6);
                px = x;
                py = y;
            }
        }
        // legend
        g2.setStroke(new BasicStroke(1f));
        for (int l = 0; l < names.length; l++) {
            int lx = left + (l % 2) * (w / 2);
            int ly = top + h + 48 + (l / 2) * 15;
            g2.setColor(COLORS[l % COLORS.length]);
            g2.fillRect(lx, ly - 8, 14, 8);
            g2.setColor(Color.BLACK);
            g2.drawString(names[l], lx + 19, ly);
        }
    }

    private int xPos(double x, double xMin, double xMax, int left, int w) {
        double frac = logScale ? (Math.log10(x) - Math.log10(xMin)) / (Math.log10(xMax) - Math.log10(xMin))
                : (x - xMin) / (xMax - xMin);
        return left + (int) Math.round(w * frac);
    }

    private void grid(Graphics2D g2, FontMetrics fm, int left, int w, int y, String label) {
        g2.setColor(new Color(225, 225, 225));
        g2.drawLine(left, y, left + w, y);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(label, left - 6 - fm.stringWidth(label), y + 4);
    }

    private static String formatMs(double v) {
        if (v >= 100) {
            return String.format("%.0f", v);
        }
        if (v >= 1) {
            return String.format("%.1f", v);
        }
        return String.format("%.3g", v);
    }

    /** Rounds up to 1, 2 or 5 times a power of ten (a tidy top for the axis). */
    private static double niceCeil(double v) {
        double p = Math.pow(10, Math.floor(Math.log10(v)));
        double f = v / p;
        double nice = f <= 1 ? 1 : f <= 2 ? 2 : f <= 5 ? 5 : 10;
        return nice * p;
    }
}
