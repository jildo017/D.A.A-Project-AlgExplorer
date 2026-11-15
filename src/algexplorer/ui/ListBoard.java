package algexplorer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Draws a linked list as boxes and arrows. It only draws; it never thinks. */
public class ListBoard extends JPanel {
    private static final Color NODE = new Color(174, 214, 241);
    private static final Color MARKER = new Color(192, 57, 43);
    private static final int NODE_H = 44;
    private static final int NULL_W = 46;

    private boolean ready = false; // false = nothing to show yet
    private int[] values = new int[0];
    private boolean doubly = false;
    private int highlight = -1; // the node to colour (-1 = none)
    private Color highlightColor = new Color(245, 176, 65);

    public ListBoard() {
        setPreferredSize(new Dimension(700, 170));
        setBackground(Color.WHITE);
    }

    /** Shows a list (its values from first to last) and asks for a redraw. */
    public void setContents(int[] newValues, boolean isDoubly, int newHighlight, Color newColor) {
        ready = true;
        values = newValues;
        doubly = isDoubly;
        highlight = newHighlight;
        highlightColor = newColor;
        repaint();
    }

    public void clear() {
        ready = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = values.length;
        int midY = getHeight() / 2 + 8;
        if (!ready) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
            g2.drawString("The list will be drawn here.", 20, midY);
            return;
        }
        if (n == 0) {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            g2.drawString(doubly ? "Empty list:  head = null,  tail = null" : "Empty list:  head = null", 20, midY);
            return;
        }

        int margin = 20;
        int leftExtra = doubly ? NULL_W : 0;
        double unit = (getWidth() - 2.0 * margin - leftExtra - NULL_W) / (n + 1.0);
        int nodeW = (int) Math.max(24, Math.min(60, unit * 0.62));
        int gap = (int) Math.max(16, unit - nodeW);
        int total = leftExtra + (doubly ? gap : 0) + n * nodeW + n * gap + NULL_W;
        int x = Math.max(margin, (getWidth() - total) / 2);
        int top = midY - NODE_H / 2;

        int leftNullX = x;
        if (doubly) {
            drawNull(g2, leftNullX, top);
            x += NULL_W + gap;
        }
        int firstX = x;
        int lastX = x;
        for (int i = 0; i < n; i++) {
            int nodeX = x + i * (nodeW + gap);
            lastX = nodeX;
            g2.setColor(i == highlight ? highlightColor : NODE);
            g2.fillRect(nodeX, top, nodeW, NODE_H);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(nodeX, top, nodeW, NODE_H);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();
            String text = String.valueOf(values[i]);
            g2.drawString(text, nodeX + (nodeW - fm.stringWidth(text)) / 2, midY + fm.getAscent() / 2 - 2);

            g2.setColor(Color.GRAY); // the position number below
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            fm = g2.getFontMetrics();
            String index = String.valueOf(i);
            g2.drawString(index, nodeX + (nodeW - fm.stringWidth(index)) / 2, top + NODE_H + 18);

            g2.setColor(Color.DARK_GRAY);
            if (i < n - 1) { // arrows between this node and the next
                int from = nodeX + nodeW;
                int to = from + gap;
                if (doubly) {
                    drawArrow(g2, from, to, midY - 7); // next: pointing right
                    drawArrow(g2, to, from, midY + 7); // prev: pointing left
                } else {
                    drawArrow(g2, from, to, midY);
                }
            }
        }

        int rightNullX = lastX + nodeW + gap; // the arrow from the last node to null
        g2.setColor(Color.DARK_GRAY);
        drawArrow(g2, lastX + nodeW, rightNullX, doubly ? midY - 7 : midY);
        drawNull(g2, rightNullX, top);
        if (doubly) { // the arrow from the first node back to null
            drawArrow(g2, firstX, leftNullX + NULL_W, midY + 7);
        }

        drawMarker(g2, firstX, nodeW, top, n == 1 && doubly ? "HEAD/TAIL" : "HEAD");
        if (doubly && n > 1) {
            drawMarker(g2, lastX, nodeW, top, "TAIL");
        }
    }

    private void drawNull(Graphics2D g2, int x, int top) {
        g2.setColor(new Color(235, 235, 235));
        g2.fillRoundRect(x, top, NULL_W, NODE_H, 10, 10);
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(x, top, NULL_W, NODE_H, 10, 10);
        g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 13));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("null", x + (NULL_W - fm.stringWidth("null")) / 2, top + (NODE_H + fm.getAscent()) / 2 - 3);
    }

    /** A line from x1 to x2 at height y, with an arrow head at x2. */
    private void drawArrow(Graphics2D g2, int x1, int x2, int y) {
        g2.drawLine(x1, y, x2, y);
        int dir = x2 >= x1 ? 1 : -1;
        g2.fillPolygon(new int[] { x2, x2 - dir * 7, x2 - dir * 7 }, new int[] { y, y - 4, y + 4 }, 3);
    }

    private void drawMarker(Graphics2D g2, int nodeX, int nodeW, int top, String word) {
        int cx = nodeX + nodeW / 2;
        g2.setColor(MARKER);
        g2.fillPolygon(new int[] { cx - 6, cx + 6, cx }, new int[] { top - 14, top - 14, top - 3 }, 3);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(word, cx - fm.stringWidth(word) / 2, top - 18);
    }
}