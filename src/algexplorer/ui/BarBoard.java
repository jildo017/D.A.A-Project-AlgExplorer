package algexplorer.ui;

import algexplorer.sorting.SortStep;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Draws the array as bars. Orange = compared/swapped/placed, blue = part being worked on, green = finished. */
public class BarBoard extends JPanel {
    private static final Color IN_RANGE = new Color(93, 173, 226);
    private static final Color OUT_RANGE = new Color(189, 195, 199);
    private static final Color MARKED = new Color(245, 176, 65);
    private static final Color DONE = new Color(88, 214, 141);

    private SortStep step;
    private boolean finished;

    public BarBoard() {
        setPreferredSize(new Dimension(700, 240));
        setBackground(Color.WHITE);
    }

    /** Shows one step; finished = true paints every bar green. */
    public void setStep(SortStep newStep, boolean isFinished) {
        step = newStep;
        finished = isFinished;
        repaint();
    }

    public void clear() {
        step = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (step == null) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
            g2.drawString("Enter numbers (or generate random ones) and press Sort.", 20, getHeight() / 2);
            return;
        }
        int[] v = step.values;
        int n = v.length;
        if (n == 0) {
            return;
        }
        int min = 0;
        int max = 0;
        for (int i = 0; i < n; i++) {
            if (v[i] < min) {
                min = v[i];
            }
            if (v[i] > max) {
                max = v[i];
            }
        }
        double span = max - min == 0 ? 1 : max - min;

        int margin = 20;
        int top = 20;
        int bottom = 24;
        int plotH = getHeight() - top - bottom;
        int slot = (getWidth() - 2 * margin) / n;
        if (slot < 1) {
            slot = 1;
        }
        int barW = Math.max(1, slot > 4 ? slot - 3 : slot);
        int startX = (getWidth() - slot * n) / 2;
        int zeroY = top + (int) Math.round(plotH * (max / span));

        boolean labels = slot >= 26;
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, slot >= 36 ? 13 : 11));
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < n; i++) {
            int x = startX + i * slot;
            int h = (int) Math.round(plotH * (Math.abs(v[i]) / span));
            if (h < 2) {
                h = 2;
            }
            int y = v[i] >= 0 ? zeroY - h : zeroY;

            Color c = OUT_RANGE;
            if (step.rangeLo >= 0 && i >= step.rangeLo && i <= step.rangeHi) {
                c = IN_RANGE;
            } else if (step.rangeLo < 0) {
                c = IN_RANGE;
            }
            if (i == step.markA || i == step.markB) {
                c = MARKED;
            }
            if (finished) {
                c = DONE;
            }
            g2.setColor(c);
            g2.fillRect(x, y, barW, h);
            if (labels) {
                String t = String.valueOf(v[i]);
                g2.setColor(Color.BLACK);
                int ty = v[i] >= 0 ? y - 4 : y + h + fm.getAscent();
                g2.drawString(t, x + (barW - fm.stringWidth(t)) / 2, ty);
            }
        }
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(startX, zeroY, startX + slot * n, zeroY);
    }
}
