
package kr.co.sist.user.evt;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Timer;
import javax.swing.SwingUtilities;

import kr.co.sist.user.service.CartListService;
import kr.co.sist.user.view.MainForm;
import kr.co.sist.user.view.OrderCompleteForm;

public class OrderCompleteEvt extends WindowAdapter
        implements ActionListener {

    private final OrderCompleteForm form;
    private Timer autoReturnTimer;
    private int remainSeconds = 5;
    private boolean moved = false;

    public OrderCompleteEvt(OrderCompleteForm form) {

        this.form = form;
        this.form.addWindowListener(this);

        startAutoReturnTimer();
    }

    /**
     * 5초 자동 복귀 타이머
     */
    private void startAutoReturnTimer() {

        form.getJbtnMain().setText(
            "처음으로 (" + remainSeconds + "초)"
        );

        autoReturnTimer = new Timer(1000, e -> {

            remainSeconds--;

            if (remainSeconds > 0) {

                form.getJbtnMain().setText(
                    "처음으로 (" + remainSeconds + "초)"
                );

            } else {

                returnToMain();
            }
        });

        autoReturnTimer.start();
    }

    /**
     * 처음으로 버튼 클릭
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == form.getJbtnMain()) {
            returnToMain();
        }
    }

    /**
     * 주문 완료 창의 X 버튼 클릭
     */
    @Override
    public void windowClosing(WindowEvent e) {

        returnToMain();
    }

    /**
     * 주문 완료 창 종료 시 타이머 정지
     */
    @Override
    public void windowClosed(WindowEvent e) {

        if (autoReturnTimer != null) {
            autoReturnTimer.stop();
        }
    }

    /**
     * 주문 완료 후 처음 화면으로 이동
     *
     * 1. 중복 실행 방지
     * 2. 타이머 종료
     * 3. 주문 정보 초기화
     * 4. 이전 주문 관련 창 모두 닫기
     * 5. 새로운 메인 화면 실행
     */
    public void returnToMain() {

        // 중복 실행 방지
        if (moved) {
            return;
        }

        moved = true;

        // 자동 복귀 타이머 정지
        if (autoReturnTimer != null) {
            autoReturnTimer.stop();
        }

        // 장바구니 및 바로 주문 정보 초기화
        CartListService.resetOrder();

        // 기존 사용자 키오스크 창 종료
        for (Window window : Window.getWindows()) {

            if (window == null || !window.isDisplayable()) {
                continue;
            }

            // 같은 키오스크 사용자 화면에 속한 창만 종료
            Package windowPackage =
                window.getClass().getPackage();

            if (windowPackage != null
                    && "kr.co.sist.user.view".equals(
                        windowPackage.getName())) {

                window.dispose();
            }
        }

        // 새 주문을 위한 메인 화면 실행
        SwingUtilities.invokeLater(() -> {

            MainForm.setOrderTypeHandler(
                CartListService::setOrderType
            );

            MainForm mainForm = new MainForm();
            mainForm.setVisible(true);
            mainForm.toFront();
        });
    }
}
