package kr.co.sist.admin.view;
import java.awt.*;
import javax.swing.*;

public class AdminLoginPanel extends JPanel {
    private final JTextField idField=new JTextField();
    private final JPasswordField passwordField=new JPasswordField();
    private final JButton loginButton=UiStyle.button("로그인");
    private final JLabel message=new JLabel(" ");
    public AdminLoginPanel() {
        setLayout(null);setBackground(Color.WHITE);
        JLabel idLabel=new JLabel("아이디");idLabel.setBounds(45,28,90,38);add(idLabel);
        JLabel passwordLabel=new JLabel("비밀번호");passwordLabel.setBounds(45,85,90,38);add(passwordLabel);
        idField.setBounds(140,28,330,38);passwordField.setBounds(140,85,330,38);
        idField.setBackground(Color.WHITE);passwordField.setBackground(Color.WHITE);
        idField.setForeground(Color.BLACK);passwordField.setForeground(Color.BLACK);
        idField.setCaretColor(Color.BLACK);passwordField.setCaretColor(Color.BLACK);
        idField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY),BorderFactory.createEmptyBorder(4,8,4,8)));
        passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY),BorderFactory.createEmptyBorder(4,8,4,8)));
        idField.setToolTipText("관리자 아이디를 입력하세요");passwordField.setToolTipText("관리자 비밀번호를 입력하세요");
        add(idField);add(passwordField);
        loginButton.setBounds(140,146,330,40);add(loginButton);
        message.setBounds(45,198,455,24);message.setForeground(new Color(170,35,35));add(message);
    }
    public JTextField getIdField(){return idField;}
    public JPasswordField getPasswordField(){return passwordField;}
    public JButton getLoginButton(){return loginButton;}
    public void setMessage(String text){message.setText(text);}
    public void setBusy(boolean busy){loginButton.setEnabled(!busy);idField.setEnabled(!busy);passwordField.setEnabled(!busy);}
}
