package kr.co.sist.admin.view;
import java.awt.*;
import javax.swing.*;
/** PPT처럼 흰 배경, 검은 글씨, 회색 버튼만 사용합니다. */
public final class UiStyle {
    public static final Color BACKGROUND=new Color(166,166,166);
    public static final Color PANEL=new Color(231,230,230);
    public static final Color BUTTON=new Color(217,217,217);
    private UiStyle() { }
    public static JPanel header(String title,String subtitle) {
        JPanel panel=new JPanel(new BorderLayout(0,8));panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24,28,16,28));
        JLabel heading=new JLabel(title);heading.setFont(new Font("SansSerif",Font.BOLD,24));
        JLabel sub=new JLabel(subtitle);sub.setForeground(Color.DARK_GRAY);
        panel.add(heading,BorderLayout.NORTH);panel.add(sub,BorderLayout.SOUTH);return panel;
    }
    public static JButton button(String text) {
        JButton button=new JButton(text);button.setFocusPainted(false);
        button.setBackground(BUTTON);button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140,38));return button;
    }
    public static void frame(JFrame frame,int width,int height) {
        frame.setSize(width,height);frame.setMinimumSize(new Dimension(width,height));
        frame.setLocationRelativeTo(null);frame.getContentPane().setBackground(Color.WHITE);
    }
}
