
package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kr.co.sist.user.view.MainForm;

/**
 * 메인 화면 이벤트
 *
 * 매장 버튼 / 포장 버튼 처리
 */
public class MainEvt implements ActionListener {

    private final MainForm form;

    public MainEvt(MainForm form) {
        this.form = form;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource()
                == form.getDineInButton()) {

            selectOrderType("매장");

        } else if (e.getSource()
                == form.getTakeoutButton()) {

            selectOrderType("포장");
        }
    }

    /**
     * 주문 유형 선택
     */
    public void selectOrderType(String type) {

        form.openMenuForm(type);
    }
}
//