package kr.co.sist.admin.view;
import java.awt.*;
import javax.swing.*;
/** 기본 Swing 컴포넌트와 setBounds로 배치한 로그인 입력 부분. */
public class AdminLoginPanel extends JPanel {
    private final JTextField idField=new JTextField();
    private final JPasswordField passwordField=new JPasswordField();
    private final JButton loginButton=UiStyle.button("로그인");
    private final JLabel message=new JLabel(" ");
    public AdminLoginPanel() {
        setLayout(null);setBackground(UiStyle.PANEL);
        JLabel title=new JLabel("관리자 로그인");title.setFont(new Font("SansSerif",Font.BOLD,24));title.setBounds(45,20,330,40);add(title);
        JLabel idLabel=new JLabel("아이디");idLabel.setBounds(45,83,90,38);add(idLabel);
        JLabel passwordLabel=new JLabel("비밀번호");passwordLabel.setBounds(45,140,90,38);add(passwordLabel);
        idField.setBounds(140,83,330,38);passwordField.setBounds(140,140,330,38);
        idField.setBackground(Color.WHITE);passwordField.setBackground(Color.WHITE);
        idField.setForeground(Color.BLACK);passwordField.setForeground(Color.BLACK);
        idField.setCaretColor(Color.BLACK);passwordField.setCaretColor(Color.BLACK);
        idField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY),BorderFactory.createEmptyBorder(4,8,4,8)));
        passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY),BorderFactory.createEmptyBorder(4,8,4,8)));
        idField.setToolTipText("관리자 아이디를 입력하세요");passwordField.setToolTipText("관리자 비밀번호를 입력하세요");
        add(idField);add(passwordField);
        loginButton.setBounds(360,211,110,40);add(loginButton);
        message.setBounds(45,267,455,24);message.setForeground(new Color(170,35,35));add(message);
    }
    public JTextField getIdField(){return idField;}
    public JPasswordField getPasswordField(){return passwordField;}
    public JButton getLoginButton(){return loginButton;}
    public void setMessage(String text){message.setText(text);}
    public void setBusy(boolean busy){loginButton.setEnabled(!busy);idField.setEnabled(!busy);passwordField.setEnabled(!busy);}
}
