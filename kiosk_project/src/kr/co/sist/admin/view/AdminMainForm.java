package kr.co.sist.admin.view;
import java.awt.*;
import javax.swing.*;
/** 화면설계서의 세 개 관리 버튼 배치. */
public class AdminMainForm extends JFrame {
    private final JButton orderButton=UiStyle.button("<html><center>주문관리<br><br>주문 확인 · 제작완료<br>주문 취소</center></html>");
    private final JButton menuButton=UiStyle.button("<html><center>메뉴관리<br><br>메뉴 추가 · 삭제 · 품절</center></html>");
    private final JButton salesButton=UiStyle.button("<html><center>매출조회<br><br>기간별 결제 내역<br>매출합계</center></html>");
    private final JButton logoutButton=UiStyle.button("로그아웃");
    public AdminMainForm(String loginId) {
        super("카페 관리자 메인");setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UiStyle.frame(this,920,710);setResizable(false);
        JPanel background=new JPanel(new BorderLayout());background.setBackground(UiStyle.BACKGROUND);
        background.setBorder(BorderFactory.createEmptyBorder(30,55,30,55));
        JPanel content=new JPanel(null);content.setBackground(Color.WHITE);content.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));
        background.add(content);setContentPane(background);
        JLabel title=new JLabel("관리자 메인");title.setFont(new Font("SansSerif",Font.BOLD,24));title.setBounds(35,25,250,40);content.add(title);
        logoutButton.setBounds(650,30,100,40);content.add(logoutButton);
        JSeparator line=new JSeparator();line.setBounds(35,105,715,1);content.add(line);
        JLabel guide=new JLabel("관리할 항목을 선택해주세요.");guide.setBounds(35,140,500,30);guide.setFont(new Font("SansSerif",Font.PLAIN,20));content.add(guide);
        orderButton.setBounds(35,235,205,210);menuButton.setBounds(290,235,205,210);salesButton.setBounds(545,235,205,210);
        content.add(orderButton);content.add(menuButton);content.add(salesButton);
    }
    public JButton getOrderButton(){return orderButton;}
    public JButton getMenuButton(){return menuButton;}
    public JButton getSalesButton(){return salesButton;}
    public JButton getLogoutButton(){return logoutButton;}
}
