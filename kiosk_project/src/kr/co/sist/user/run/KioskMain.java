
package kr.co.sist.user.run;

import javax.swing.SwingUtilities;

import kr.co.sist.user.view.MainForm;
import kr.co.sist.user.view.MenuForm;
import kr.co.sist.user.view.MenuDetailDialog;

/**
 * 카페 키오스크 사용자 프로그램 실행 클래스
 *
 * [주요 기능]
 * 1. Java Swing 화면 실행
 * 2. 매장·포장 선택 화면(MainForm) 실행
 * 3. 2번 담당자 옵션·장바구니 연결 지점 제공
 * 4. 3번 담당자 결제 화면 연결 지점 제공
 *
 * DB 연결은 MenuDAO에서 처리하며,
 * DbConn은 DB 담당자의 공통 클래스를 사용한다.
 */
public class KioskMain {

    /**
     * 프로그램 시작 메서드
     *
     * Eclipse에서 이 클래스를 실행하면
     * MainForm이 가장 먼저 표시된다.
     */
    public static void main(String[] args) {

        /*
         * Swing 화면을 이벤트 처리 스레드(EDT)에서 실행한다.
         *
         * Swing은 화면 생성과 이벤트 처리를
         * EDT에서 수행하는 것이 기본 원칙이다.
         */
        SwingUtilities.invokeLater(() -> {

            // 2번·3번 담당자가 완성한 코드를 연결할 위치
            initializeTeamIntegration();

            // 메인 화면 객체 생성
            MainForm mainForm = new MainForm();

            // 매장·포장 선택 화면 표시
            mainForm.setVisible(true);
        });
    }

    /**
     * 다른 팀원 기능을 연결하기 위한 메서드
     *
     * 현재는 1번 담당자 단독 실행이 가능하도록
     * 연결 코드를 주석 처리해 둔다.
     */
    private static void initializeTeamIntegration() {

        /*
         * =========================================
         * ① 2번 담당 - 매장·포장 주문 유형 전달
         * =========================================
         *
         * CartListService가 완성되면 연결한다.
         *
         * MainForm.setOrderTypeHandler(
         *     CartListService::setOrderType
         * );
         */

        /*
         * =========================================
         * ② 2번 담당 - 장바구니 수량과 금액
         * =========================================
         *
         * CartListService가 완성되면 연결한다.
         *
         * MenuForm.setCartSummarySuppliers(
         *     CartListService::getCartItemCount,
         *     CartListService::getCartTotalAmount
         * );
         */

        /*
         * =========================================
         * ③ 2번 담당 - 상품 옵션 선택 화면
         * =========================================
         *
         * OptionDialog가 완성되면 연결한다.
         *
         * MenuDetailDialog.setOptionHandler(menu -> {
         *     OptionDialog dialog = new OptionDialog(menu);
         *     dialog.setVisible(true);
         * });
         */

        /*
         * =========================================
         * ④ 3번 담당 - 결제 방식 선택 화면
         * =========================================
         *
         * PaymentSelectForm이 완성되면 연결한다.
         *
         * MenuForm.setPaymentHandler(() -> {
         *     PaymentSelectForm paymentForm =
         *         new PaymentSelectForm();
         *
         *     paymentForm.setVisible(true);
         * });
         */

    }
}
