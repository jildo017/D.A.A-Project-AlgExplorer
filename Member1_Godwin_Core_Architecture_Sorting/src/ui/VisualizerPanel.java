package ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Draws the current state with Graphics2D.
 * Modes: SORT, BSEARCH, STACK, QUEUE, LIST, FACT, FIB, HANOI.
 */
public class VisualizerPanel extends JPanel {
    private static final Color ORANGE = MainFrame.ORANGE;
    private static final Color GRAY = new Color(85, 88, 110);
    private static final Color HINT = new Color(150, 155, 180);

    private String mode = "SORT";
    private int[] data = null;
    private int h1 = -1;
    private int h2 = -1;
    private int boundary = -1;
    private String message = "Enter your input and press Run to start the visualization.";

    public VisualizerPanel() {
        setBackground(MainFrame.CARD);
        setPreferredSize(new Dimension(700, 500));
        setBorder(BorderFactory.createLineBorder(new Color(70, 70, 95), 1));
    }

    public void setMode(String newMode) {
        mode = newMode;
    }

    public void setFrame(int[] d, int a, int b, int bd, String msg) {
        data = d;
        h1 = a;
        h2 = b;
        boundary = bd;
        message = msg;
        repaint();
    }

    public void clear(String msg) {
        data = null;
        h1 = -1;
        h2 = -1;
        boundary = -1;
        message = msg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g.setFont(new Font("SansSerif", Font.BOLD, 14));

        if (data == null) {
            g.setColor(HINT);
            drawCentered(g, fit(g, message, w - 30), w / 2, h / 2);
            g.dispose();
            return;
        }
        switch (mode) {
            case "SORT":
                legend(g, new String[] {"Unsorted", "Active", "Compared", "Sorted"},
                        new Color[] {MainFrame.BLUE, ORANGE, MainFrame.RED, MainFrame.GREEN});
                drawBars(g, w, h, false);
                break;
            case "BSEARCH":
                legend(g, new String[] {"Search range", "Middle", "Eliminated", "Found"},
                        new Color[] {MainFrame.BLUE, ORANGE, GRAY, MainFrame.GREEN});
                drawBars(g, w, h, true);
                break;
            case "STACK":
                drawStack(g, w, h);
                break;
            case "QUEUE":
                drawQueue(g, w, h);
                break;
            case "LIST":
                drawList(g, w, h);
                break;
            case "FACT":
            case "FIB":
                drawCalls(g, w, h);
                break;
            case "HANOI":
                drawHanoi(g, w, h);
                break;
            default:
                break;
        }
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(MainFrame.TEXT);
        drawCentered(g, fit(g, message, w - 30), w / 2, h - 18);
        g.dispose();
    }

    // ------------------------------------------------------------------ helpers

    private static String fit(Graphics2D g, String s, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        while (s.length() > 4 && fm.stringWidth(s) > maxWidth) {
            s = s.substring(0, s.length() - 4) + "...";
        }
        return s;
    }

    private static void drawCentered(Graphics2D g, String s, int cx, int cy) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, cx - fm.stringWidth(s) / 2, cy + fm.getAscent() / 2 - 2);
    }

    private void legend(Graphics2D g, String[] labels, Color[] colors) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int x = 16;
        for (int i = 0; i < labels.length; i++) {
            g.setColor(colors[i]);
            g.fillRect(x, 14, 12, 12);
            g.setColor(MainFrame.TEXT);
            g.drawString(labels[i], x + 18, 25);
            x += 28 + g.getFontMetrics().stringWidth(labels[i]) + 10;
        }
    }

    private void box(Graphics2D g, int x, int y, int bw, int bh, Color fill, String text) {
        g.setColor(fill);
        g.fillRoundRect(x, y, bw, bh, 10, 10);
        g.setColor(MainFrame.TEXT);
        g.drawRoundRect(x, y, bw, bh, 10, 10);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        drawCentered(g, fit(g, text, bw - 6), x + bw / 2, y + bh / 2);
    }

    private void arrowRight(Graphics2D g, int x1, int x2, int y) {
        g.setColor(MainFrame.MUTED);
        g.drawLine(x1, y, x2, y);
        g.fillPolygon(new int[] {x2, x2 - 8, x2 - 8}, new int[] {y, y - 5, y + 5}, 3);
    }

    // ------------------------------------------------------------------ sorting / binary search bars

    private void drawBars(Graphics2D g, int w, int h, boolean search) {
        int n = data.length;
        int top = 44;
        int bottom = 50;
        int left = 20;
        int right = 20;
        int availW = w - left - right;
        int availH = h - top - bottom;
        if (n == 0 || availH < 10) {
            return;
        }
        int min = data[0];
        int max = data[0];
        for (int i = 1; i < n; i++) {
            if (data[i] < min) {
                min = data[i];
            }
            if (data[i] > max) {
                max = data[i];
            }
        }
        if (min > 0) {
            min = 0;
        }
        if (max == min) {
            max = min + 1;
        }
        double barW = (double) availW / n;
        int gap = barW > 6 ? 2 : 0;
        int prefixEnd = boundary >= 0 ? boundary : 0;
        int suffixStart = boundary <= -2 ? -boundary - 2 : Integer.MAX_VALUE;
        boolean found = message != null && message.startsWith("Found");

        for (int i = 0; i < n; i++) {
            int bh = (int) ((long) (data[i] - min) * availH / (max - min));
            if (bh < 4) {
                bh = 4;
            }
            int x = left + (int) (i * barW);
            int bw = (int) Math.max(2, barW - gap);
            int y = top + availH - bh;

            Color c = MainFrame.BLUE;
            if (search) {
                boolean inRange = i >= boundary && i <= h2;
                c = inRange ? MainFrame.BLUE : GRAY;
                if (i == h1) {
                    c = found ? MainFrame.GREEN : ORANGE;
                }
            } else {
                if (i < prefixEnd || i >= suffixStart) {
                    c = MainFrame.GREEN;
                }
                if (i == h2) {
                    c = MainFrame.RED;
                }
                if (i == h1) {
                    c = ORANGE;
                }
            }
            g.setColor(c);
            g.fillRoundRect(x, y, bw, bh, 6, 6);

            // Active bar highlight border
            if (i == h1 || i == h2) {
                g.setColor(Color.WHITE);
                g.drawRoundRect(x, y, bw, bh, 6, 6);
            }

            // Number value above bar
            if (barW >= 15) {
                g.setColor(MainFrame.TEXT);
                g.setFont(new Font("SansSerif", Font.BOLD, barW > 25 ? 12 : 10));
                String s = String.valueOf(data[i]);
                g.drawString(s, x + (bw - g.getFontMetrics().stringWidth(s)) / 2, y - 4);
            }

            // Index label below bar
            if (barW >= 20) {
                g.setColor(MainFrame.MUTED);
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                String idx = "[" + i + "]";
                g.drawString(idx, x + (bw - g.getFontMetrics().stringWidth(idx)) / 2, top + availH + 16);
            }
        }

        // Overhead comparison/flip bracket
        if (h1 >= 0 && h2 >= 0 && h1 < n && h2 < n && h1 != h2) {
            int x1 = left + (int) (h1 * barW) + (int) (barW / 2);
            int x2 = left + (int) (h2 * barW) + (int) (barW / 2);
            int arcY = top - 18;
            g.setColor(ORANGE);
            g.drawLine(x1, arcY + 8, x1, arcY);
            g.drawLine(x1, arcY, x2, arcY);
            g.drawLine(x2, arcY, x2, arcY + 8);
            String badge = message != null && message.contains("Flip") ? "🥞 FLIP" : (message != null && message.contains("Swap") ? "⇄ SWAP" : "COMPARE");
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            int badgeW = g.getFontMetrics().stringWidth(badge) + 10;
            int midX = (x1 + x2) / 2;
            g.setColor(new Color(40, 40, 60));
            g.fillRoundRect(midX - badgeW / 2, arcY - 14, badgeW, 16, 6, 6);
            g.setColor(Color.WHITE);
            g.drawString(badge, midX - g.getFontMetrics().stringWidth(badge) / 2, arcY - 2);
        }
    }

    // ------------------------------------------------------------------ stack

    private void drawStack(Graphics2D g, int w, int h) {
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(MainFrame.MUTED);
        drawCentered(g, "STACK (LIFO)", w / 2, 24);
        int n = data.length;
        if (n == 0) {
            g.setColor(HINT);
            drawCentered(g, "Stack is empty (top = null)", w / 2, h / 2);
            return;
        }
        int boxW = 170;
        int boxH = Math.min(46, (h - 130) / n);
        int x = (w - boxW) / 2;
        for (int i = 0; i < n; i++) {
            int y = 56 + i * boxH;
            box(g, x, y, boxW, boxH - 4, i == h1 ? ORANGE : MainFrame.BLUE, String.valueOf(data[i]));
            if (i == 0) {
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.setColor(MainFrame.GREEN);
                g.drawString("TOP", x + boxW + 14, y + boxH / 2 + 4);
                g.drawLine(x + boxW + 2, y + boxH / 2 - 2, x + boxW + 10, y + boxH / 2 - 2);
            }
            if (i == n - 1) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g.setColor(MainFrame.MUTED);
                g.drawString("BOTTOM", x + boxW + 14, y + boxH / 2 + 4);
            }
        }
    }

    // ------------------------------------------------------------------ queue

    private void drawQueue(Graphics2D g, int w, int h) {
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(MainFrame.MUTED);
        drawCentered(g, "QUEUE (FIFO)", w / 2, 24);
        int n = data.length;
        if (n == 0) {
            g.setColor(HINT);
            drawCentered(g, "Queue is empty (front = rear = null)", w / 2, h / 2);
            return;
        }
        int spacing = Math.min(100, (w - 60) / n);
        int boxW = Math.max(20, spacing - 22);
        int boxH = 60;
        int startX = (w - n * spacing + (spacing - boxW)) / 2;
        int y = h / 2 - boxH / 2;
        for (int i = 0; i < n; i++) {
            int x = startX + i * spacing;
            box(g, x, y, boxW, boxH, i == h1 ? ORANGE : MainFrame.BLUE, String.valueOf(data[i]));
            if (i < n - 1) {
                arrowRight(g, x + boxW + 2, x + spacing - 2, y + boxH / 2);
            }
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.setColor(MainFrame.GREEN);
            String label = "";
            if (i == 0 && i == n - 1) {
                label = "FRONT / REAR";
            } else if (i == 0) {
                label = "FRONT";
            } else if (i == n - 1) {
                label = "REAR";
            }
            if (label.length() > 0) {
                g.drawString(label, x + (boxW - g.getFontMetrics().stringWidth(label)) / 2, y + boxH + 22);
            }
        }
    }

    // ------------------------------------------------------------------ linked list

    private void drawList(Graphics2D g, int w, int h) {
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(MainFrame.MUTED);
        drawCentered(g, "SINGLY LINKED LIST", w / 2, 24);
        int n = data.length;
        if (n == 0) {
            g.setColor(HINT);
            drawCentered(g, "List is empty (head = null)", w / 2, h / 2);
            return;
        }
        int spacing = Math.min(110, (w - 40) / (n + 1));
        int boxW = Math.max(20, spacing - 30);
        int boxH = 56;
        int startX = (w - (n + 1) * spacing + (spacing - boxW)) / 2;
        int y = h / 2 - boxH / 2;
        for (int i = 0; i < n; i++) {
            int x = startX + i * spacing;
            box(g, x, y, boxW, boxH, i == h1 ? ORANGE : MainFrame.BLUE, String.valueOf(data[i]));
            arrowRight(g, x + boxW + 2, x + spacing - 2, y + boxH / 2);
            if (i == 0) {
                g.setFont(new Font("SansSerif", Font.BOLD, 13));
                g.setColor(MainFrame.GREEN);
                g.drawString("HEAD", x + (boxW - g.getFontMetrics().stringWidth("HEAD")) / 2, y - 12);
            }
        }
        int nx = startX + n * spacing;
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(MainFrame.MUTED);
        g.drawString("NULL", nx, y + boxH / 2 + 5);
    }

    // ------------------------------------------------------------------ factorial / fibonacci call stack

    private void drawCalls(Graphics2D g, int w, int h) {
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(MainFrame.MUTED);
        drawCentered(g, "CALL STACK (top = most recent call)", w / 2, 24);
        int n = data.length;
        String fn = "FACT".equals(mode) ? "factorial" : "fibonacci";
        int boxW = 280;
        int boxH = Math.min(40, (h - 130) / Math.max(1, n));
        int x = (w - boxW) / 2;
        for (int k = 0; k < n; k++) {
            int y = h - 56 - (k + 1) * boxH;
            Color c = MainFrame.BLUE;
            if (k == h1) {
                c = h2 == 1 ? MainFrame.GREEN : ORANGE;
            }
            box(g, x, y, boxW, boxH - 4, c, "Depth " + k + ":  " + fn + "(" + data[k] + ")");
        }
    }

    // ------------------------------------------------------------------ tower of hanoi

    private void drawHanoi(Graphics2D g, int w, int h) {
        int n = data.length;
        int baseY = h - 80;
        int diskH = Math.max(10, Math.min(28, (h - 200) / (n + 1)));
        int maxW = Math.max(60, w / 3 - 30);
        for (int p = 0; p < 3; p++) {
            int cx = w * (2 * p + 1) / 6;
            g.setColor(new Color(120, 120, 150));
            g.fillRect(cx - 4, baseY - (n + 1) * diskH, 8, (n + 1) * diskH);
            g.fillRect(cx - maxW / 2 - 6, baseY, maxW + 12, 8);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.setColor(MainFrame.TEXT);
            drawCentered(g, "Peg " + (char) ('A' + p), cx, baseY + 30);
            int level = 0;
            for (int size = n; size >= 1; size--) {
                if (data[size - 1] == p) {
                    int dw = 30 + (maxW - 30) * (size - 1) / Math.max(1, n - 1);
                    int y = baseY - (level + 1) * diskH;
                    Color c = Color.getHSBColor((size * 0.13f) % 1f, 0.65f, 0.95f);
                    if (size - 1 == h1) {
                        c = ORANGE;
                    }
                    g.setColor(c);
                    g.fillRoundRect(cx - dw / 2, y, dw, diskH - 2, 8, 8);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("SansSerif", Font.BOLD, 12));
                    drawCentered(g, String.valueOf(size), cx, y + (diskH - 2) / 2);
                    level++;
                }
            }
        }
    }
}
