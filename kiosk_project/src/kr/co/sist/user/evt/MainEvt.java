
package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kr.co.sist.user.view.MainForm;
import kr.co.sist.user.service.CartListService;

/**
 * 메인 화면 이벤트
 *
 * 1번 담당
 * - 매장 버튼 처리
 * - 포장 버튼 처리
 * - 주문 유형 저장
 * - 메뉴 화면 이동
 */
public class MainEvt implements ActionListener {

    private final MainForm form;

    public MainEvt(MainForm form) {
        this.form = form;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // 매장 주문 버튼
        if (e.getSource() == form.getDineInButton()) {

            selectOrderType("매장");

        // 포장 주문 버튼
        } else if (e.getSource() == form.getTakeoutButton()) {

            selectOrderType("포장");
        }
    }

    /**
     * 주문 유형 선택
     *
     * 매장 -> EAT_IN
     * 포장 -> TAKE_OUT
     *
     * 선택한 주문 유형을 CartListService에 저장한 후
     * 메뉴 화면으로 이동한다.
     */
    public void selectOrderType(String type) {

        // 1. 주문 유형 저장
        if ("포장".equals(type)) {

            CartListService.setOrderType("TAKE_OUT");

        } else {

            CartListService.setOrderType("EAT_IN");
        }

        // 2. 메뉴 화면 이동
        form.openMenuForm(type);
    }
}
