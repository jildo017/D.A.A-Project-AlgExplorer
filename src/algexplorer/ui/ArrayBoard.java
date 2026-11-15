package algexplorer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Draws a row of numbered boxes with optional markers above them. It only
 * draws; it never thinks.
 */
public class ArrayBoard extends JPanel {
    private static final Color FILLED = new Color(174, 214, 241);
    private static final Color JUST_CHANGED = new Color(245, 176, 65);
    private static final Color MARKER = new Color(192, 57, 43);

    private String[] labels = new String[0]; // text inside each box; null means an empty box
    private String[] above = new String[0]; // marker text above each box; null means none
    private int highlight = -1; // the box that just changed (-1 = none)

    public ArrayBoard() {
        setPreferredSize(new Dimension(700, 150));
        setBackground(Color.WHITE);
    }

    /**
     * Shows new contents and asks for a redraw. The two arrays must have the same
     * length.
     */
    public void setContents(String[] newLabels, String[] newAbove, int newHighlight) {
        labels = newLabels;
        above = newAbove;
        highlight = newHighlight;
        repaint();
    }

    /** Shows nothing. */
    public void clear() {
        setContents(new String[0], new String[0], -1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = labels.length;
        if (n == 0) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
            g2.drawString("Create a structure to see it here.", 20, getHeight() / 2);
            return;
        }

        int margin = 20;
        int boxW = Math.min(64, (getWidth() - 2 * margin) / n);
        int boxH = 46;
        int startX = (getWidth() - boxW * n) / 2;
        int y = getHeight() / 2 - boxH / 2 + 10;

        for (int i = 0; i < n; i++) {
            int x = startX + i * boxW;
            boolean filled = labels[i] != null;

            g2.setColor(!filled ? Color.WHITE : (i == highlight ? JUST_CHANGED : FILLED));
            g2.fillRect(x, y, boxW, boxH);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, boxW, boxH);

            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();
            if (filled) {
                g2.setColor(Color.BLACK);
                g2.drawString(labels[i], x + (boxW - fm.stringWidth(labels[i])) / 2,
                        y + (boxH + fm.getAscent()) / 2 - 3);
            }

            g2.setColor(Color.GRAY); // the box number below
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            fm = g2.getFontMetrics();
            String index = String.valueOf(i);
            g2.drawString(index, x + (boxW - fm.stringWidth(index)) / 2, y + boxH + 18);

            if (above[i] != null) { // a marker: a small arrow and a word
                int cx = x + boxW / 2;
                g2.setColor(MARKER);
                g2.fillPolygon(new int[] { cx - 6, cx + 6, cx }, new int[] { y - 14, y - 14, y - 3 }, 3);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                fm = g2.getFontMetrics();
                g2.drawString(above[i], cx - fm.stringWidth(above[i]) / 2, y - 18);
            }
        }
    }
}