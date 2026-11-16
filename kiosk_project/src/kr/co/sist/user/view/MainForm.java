package kr.co.sist.user.view;

import kr.co.sist.user.evt.MainEvt;

import java.awt.*;
import java.util.function.Consumer;

import javax.swing.*;

/**
 * 카페 키오스크의 메인 화면.
 *
 * 매장 주문과 포장 주문을 선택한다.
 * 관리자 로그인 버튼은 만들지 않는다.
 */
public class MainForm extends JFrame {

    // 매장·포장 버튼 클릭 이벤트 객체
    private final MainEvt mainEvt;

    // 매장 주문 버튼
    private final JButton storeButton = new JButton("매장");

    // 포장 주문 버튼
    private final JButton takeoutButton = new JButton("포장");

    // 현재 선택한 주문 유형을 임시 저장
    private static String orderType = "";

    /*
     * TODO [2번 담당자 연결]
     *
     * 주문 유형을 2번 담당자의
     * CartListService에 전달하기 위한 연결 함수.
     *
     * 현재는 아무 작업도 하지 않는다.
     */
    private static Consumer<String> orderTypeHandler =
            type -> {};

    /**
     * 다른 담당자가 주문 유형 저장 기능을 연결할 때 사용.
     */
    public static void setOrderTypeHandler(
            Consumer<String> handler) {

        // null이 아닌 연결 함수만 등록
        if (handler != null) {
            orderTypeHandler = handler;
        }
    }

    /**
     * 매장 또는 포장 주문 유형을 저장한다.
     */
    public static void saveOrderType(String type) {

        // 현재 주문 유형 저장
        orderType = type;

        // 등록된 팀원 연결 함수 실행
        orderTypeHandler.accept(type);
    }

    // 현재 주문 유형 반환
    public static String getOrderType() {
        return orderType;
    }

    /**
     * 메인 화면 생성자.
     */
    public MainForm() {

        super("카페 키오스크 - 시작");

        // 버튼 이벤트 처리 객체 생성
        mainEvt = new MainEvt(this);

        // 창을 닫으면 프로그램 종료
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 화면 크기 설정
        setSize(900, 640);

        // 모니터 가운데 배치
        setLocationRelativeTo(null);

        // 창 크기를 변경하지 못하도록 설정
        setResizable(false);

        // 전체 화면 레이아웃 지정
        setLayout(new BorderLayout());

        // 배경색 설정
        getContentPane().setBackground(
                new Color(169, 169, 169)
        );

        // 매장·포장 버튼을 가로로 배치
        JPanel centerPanel = new JPanel(
                new GridLayout(1, 2, 85, 0)
        );

        // 패널 배경을 투명하게 설정
        centerPanel.setOpaque(false);

        // 버튼 주변 여백 설정
        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        200, 120, 170, 120
                )
        );

        // 두 버튼에 같은 디자인 적용
        for (JButton button :
                new JButton[]{storeButton, takeoutButton}) {

            // 버튼 글꼴 설정
            button.setFont(
                    new Font("맑은 고딕", Font.BOLD, 30)
            );

            // 버튼 배경색 설정
            button.setBackground(Color.WHITE);

            // 버튼을 눌렀을 때 기본 테두리 효과 제거
            button.setFocusPainted(false);

            // 버튼 클릭 시 MainEvt 실행
            button.addActionListener(mainEvt);

            // 중앙 패널에 버튼 추가
            centerPanel.add(button);
        }

        // 중앙 패널을 메인 화면에 추가
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * 주문 유형을 선택하면 MenuForm으로 이동한다.
     */
    public void openMenuForm() {

        // 메뉴 화면 생성
        MenuForm menuForm = new MenuForm();

        // 메뉴 화면 표시
        menuForm.setVisible(true);

        // 기존 메인 화면 종료
        dispose();
    }

    // 매장 버튼 Getter
    public JButton getStoreButton() {
        return storeButton;
    }

    // 포장 버튼 Getter
    public JButton getTakeoutButton() {
        return takeoutButton;
    }

    /**
     * 1번 담당 화면 단독 테스트용 main 메서드.
     *
     * 팀 공통 실행 클래스가 완성되면
     * 팀 실행 클래스에서 new MainForm()을 호출하면 된다.
     */
    public static void main(String[] args) {

        // Swing 이벤트 처리 스레드에서 화면 실행
        SwingUtilities.invokeLater(() -> {

            // 메인 화면 생성 및 표시
            new MainForm().setVisible(true);
        });
    }
}