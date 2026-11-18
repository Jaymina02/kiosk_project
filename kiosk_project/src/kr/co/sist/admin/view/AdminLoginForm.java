package kr.co.sist.admin.view;
import java.awt.BorderLayout;
import javax.swing.*;
public class AdminLoginForm extends JFrame {
    private final AdminLoginPanel loginPanel;
    public AdminLoginForm() {
        super("카페 관리자 로그인");setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(UiStyle.header("관리자 로그인","카페 관리자 전용 프로그램"),BorderLayout.NORTH);
        loginPanel=new AdminLoginPanel();add(loginPanel,BorderLayout.CENTER);
        getRootPane().setDefaultButton(loginPanel.getLoginButton());UiStyle.frame(this,560,440);setResizable(false);
    }
    public JTextField getIdField(){return loginPanel.getIdField();}
    public JPasswordField getPasswordField(){return loginPanel.getPasswordField();}
    public JButton getLoginButton(){return loginPanel.getLoginButton();}
    public void setMessage(String text){loginPanel.setMessage(text);}
    public void setBusy(boolean busy){loginPanel.setBusy(busy);}
}
