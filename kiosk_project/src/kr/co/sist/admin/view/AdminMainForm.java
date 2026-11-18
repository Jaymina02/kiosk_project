package kr.co.sist.admin.view;
import java.awt.*;
import javax.swing.*;

public class AdminMainForm extends JFrame {
    private final JButton orderButton=UiStyle.button("<html><center>주문관리<br><br>주문 확인 · 제작완료<br>주문 취소</center></html>");
    private final JButton menuButton=UiStyle.button("<html><center>메뉴관리<br><br>메뉴 추가 · 삭제 · 품절</center></html>");
    private final JButton salesButton=UiStyle.button("<html><center>매출조회<br><br>기간별 결제 내역<br>매출합계</center></html>");
    private final JButton logoutButton=UiStyle.button("로그아웃");
    public AdminMainForm(String loginId) {
        super("카페 관리자 메인");setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);UiStyle.frame(this,920,470);setResizable(false);
        JLabel title=new JLabel("관리자 메인");title.setFont(new Font("SansSerif",Font.BOLD,24));title.setBounds(35,25,250,40);add(title);
        logoutButton.setBounds(740,30,130,36);add(logoutButton);
        JLabel greeting=new JLabel("관리자님");greeting.setBounds(35,95,250,25);add(greeting);
        JLabel guide=new JLabel("관리할 항목을 선택해주세요.");guide.setBounds(35,128,500,25);add(guide);
        orderButton.setBounds(35,185,265,160);menuButton.setBounds(320,185,265,160);salesButton.setBounds(605,185,265,160);
        add(orderButton);add(menuButton);add(salesButton);
    }
    public JButton getOrderButton(){return orderButton;}
    public JButton getMenuButton(){return menuButton;}
    public JButton getSalesButton(){return salesButton;}
    public JButton getLogoutButton(){return logoutButton;}
}
