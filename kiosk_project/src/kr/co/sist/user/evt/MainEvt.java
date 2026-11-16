package kr.co.sist.user.evt;

import kr.co.sist.user.view.MainForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 메인 화면의 버튼 이벤트 처리.
 *
 * 매장 버튼 클릭 -> DINE_IN
 * 포장 버튼 클릭 -> TAKEOUT
 */
public class MainEvt implements ActionListener {

    // 현재 메인 화면 객체
    private final MainForm mainForm;

    // MainForm을 전달받는 생성자
    public MainEvt(MainForm mainForm) {
        this.mainForm = mainForm;
    }

    /**
     * 매장 또는 포장 버튼을 클릭할 때 실행.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // 클릭한 버튼 확인
        Object source = e.getSource();

        // 매장 버튼을 클릭한 경우
        if (source == mainForm.getStoreButton()) {

            selectOrderType("DINE_IN");

        // 포장 버튼을 클릭한 경우
        } else if (source == mainForm.getTakeoutButton()) {

            selectOrderType("TAKEOUT");
        }
    }

    /**
     * 주문 유형을 저장하고 메뉴 화면으로 이동한다.
     */
    public void selectOrderType(String orderType) {

        /*
         * MainForm에 주문 유형 저장.
         *
         * 2번 담당자가 연결 함수를 등록하면
         * CartListService에도 자동으로 전달된다.
         */
        MainForm.saveOrderType(orderType);

        // 메뉴 화면 이동
        mainForm.openMenuForm();
    }
}