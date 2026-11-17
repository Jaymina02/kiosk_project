
package kr.co.sist.user.view;

import java.awt.Color;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import kr.co.sist.user.evt.MainEvt;

/**
 * 사용자 메인 화면
 *
 * 매장 / 포장 선택
 */
public class MainForm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static Consumer<String> orderTypeHandler;

    private static String currentOrderType = "매장";

    private final JButton dineInButton =
            new JButton("매장");

    private final JButton takeoutButton =
            new JButton("포장");

    private final MainEvt mainEvt;

    public MainForm() {

        super("카페 키오스크 - 주문 방법 선택");

        setSize(804, 564);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(
            JFrame.EXIT_ON_CLOSE
        );

        getContentPane().setLayout(null);

        getContentPane().setBackground(
            new Color(170, 170, 170)
        );

        JLabel title = new JLabel(
            "주문하실 방법을 선택해주세요",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font(
                "맑은 고딕",
                Font.BOLD,
                28
            )
        );

        title.setBounds(80, 110, 630, 60);

        add(title);

        setupButton(dineInButton, 158, 235);
        setupButton(takeoutButton, 430, 235);

        mainEvt = new MainEvt(this);

        dineInButton.addActionListener(mainEvt);
        takeoutButton.addActionListener(mainEvt);
    }

    /**
     * 공통 버튼 디자인 설정
     */
    private void setupButton(
            JButton button,
            int x,
            int y) {

        button.setBounds(x, y, 210, 150);

        button.setBackground(Color.WHITE);

        button.setFocusPainted(false);

        button.setFont(
            new Font(
                "맑은 고딕",
                Font.BOLD,
                28
            )
        );

        add(button);
    }

    public JButton getDineInButton() {
        return dineInButton;
    }

    public JButton getTakeoutButton() {
        return takeoutButton;
    }

    public static String getCurrentOrderType() {
        return currentOrderType;
    }

    /**
     * 2번 담당자 장바구니 연결용
     */
    public static void setOrderTypeHandler(
            Consumer<String> handler) {

        orderTypeHandler = handler;
    }

    /**
     * 매장 또는 포장 선택 후 메뉴 화면 열기
     */
    public void openMenuForm(String orderType) {

        currentOrderType = orderType;

        if (orderTypeHandler != null) {
            orderTypeHandler.accept(orderType);
        }

        MenuForm menuForm = new MenuForm();

        menuForm.setVisible(true);

        dispose();
    }
}
