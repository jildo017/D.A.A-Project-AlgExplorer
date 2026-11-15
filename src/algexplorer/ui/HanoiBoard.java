package algexplorer.ui;

import algexplorer.recursion.HanoiState;
import java.awt.BasicStroke; // NEW
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Draws three pegs and the disks of a HanoiState. It draws only; it never
 * thinks.
 */
public class HanoiBoard extends JPanel {
    private static final Color[] COLOURS = {
            new Color(231, 76, 60), new Color(230, 126, 34), new Color(241, 196, 15),
            new Color(46, 204, 113), new Color(26, 188, 156), new Color(52, 152, 219),
            new Color(155, 89, 182), new Color(127, 140, 141)
    };

    private HanoiState state; // null means "nothing to draw yet"
    private int highlightDisk = 0; // NEW: the disk that just moved (0 = none)

    public HanoiBoard() {
        setPreferredSize(new Dimension(700, 260));
        setBackground(Color.WHITE);
    }

    /** Shows a state with no highlight. */
    public void setState(HanoiState newState) {
        setState(newState, 0); // CHANGED
    }

    /**
     * NEW: shows a state and outlines one disk, usually the one that just moved.
     */
    public void setState(HanoiState newState, int highlight) {
        state = newState;
        highlightDisk = highlight;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int baseY = height - 30;
        int pegHeight = height - 60;
        int slot = width / 3;

        g2.setColor(new Color(110, 80, 50));
        g2.fillRect(10, baseY, width - 20, 12);

        for (int p = 0; p < 3; p++) {
            char name = (char) ('A' + p);
            int centreX = slot * p + slot / 2;

            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(centreX - 4, baseY - pegHeight, 8, pegHeight);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            g2.drawString(String.valueOf(name), centreX - 5, baseY + 28);

            if (state != null) {
                drawDisks(g2, name, centreX, baseY, pegHeight, slot);
            }
        }
    }

    private void drawDisks(Graphics2D g2, char name, int centreX, int baseY,
            int pegHeight, int slot) {
        int n = state.getTotalDisks();
        int diskHeight = Math.min(24, (pegHeight - 10) / n);
        int maxWidth = slot - 30;
        int minWidth = 30;

        for (int level = 0; level < state.count(name); level++) {
            int disk = state.diskAt(name, level);
            int diskWidth = minWidth + (maxWidth - minWidth) * disk / n;
            int x = centreX - diskWidth / 2;
            int y = baseY - (level + 1) * diskHeight;

            g2.setColor(COLOURS[(disk - 1) % COLOURS.length]);
            g2.fillRoundRect(x, y, diskWidth, diskHeight - 2, 10, 10);

            if (disk == highlightDisk) { // NEW: black outline
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(x, y, diskWidth, diskHeight - 2, 10, 10);
                g2.setStroke(new BasicStroke(1));
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String label = String.valueOf(disk);
            g2.drawString(label, centreX - fm.stringWidth(label) / 2,
                    y + (diskHeight - 2 + fm.getAscent()) / 2 - 2);
        }
    }
}