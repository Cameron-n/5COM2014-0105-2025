package fairview.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FairviewTheme {

    public static final Color PRIMARY = new Color(52, 152, 219);
    public static final Color PRIMARY_DARK = new Color(41, 128, 185);
    public static final Color BACKGROUND = new Color(245, 245, 245);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(33, 33, 33);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    public static void styleCard(JPanel panel) {
        panel.setBackground(CARD);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    public static void styleButton(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_DARK);
        return label;
    }
}