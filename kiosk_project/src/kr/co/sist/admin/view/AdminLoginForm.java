package kr.co.sist.admin.view;
import java.awt.BorderLayout;
import javax.swing.*;
public class AdminLoginForm extends JFrame {
    private final AdminLoginPanel loginPanel;
    public AdminLoginForm() {
        super("카페 관리자 로그인");setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel background=new JPanel(new java.awt.GridBagLayout());background.setBackground(UiStyle.BACKGROUND);
        loginPanel=new AdminLoginPanel();loginPanel.setPreferredSize(new java.awt.Dimension(560,360));
        loginPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY,2));
        background.add(loginPanel);add(background,BorderLayout.CENTER);
        getRootPane().setDefaultButton(loginPanel.getLoginButton());UiStyle.frame(this,760,560);setResizable(false);
    }
    public JTextField getIdField(){return loginPanel.getIdField();}
    public JPasswordField getPasswordField(){return loginPanel.getPasswordField();}
    public JButton getLoginButton(){return loginPanel.getLoginButton();}
    public void setMessage(String text){loginPanel.setMessage(text);}
    public void setBusy(boolean busy){loginPanel.setBusy(busy);}
}
