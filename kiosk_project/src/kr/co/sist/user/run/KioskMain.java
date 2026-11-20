
package kr.co.sist.user.run;

import javax.swing.SwingUtilities;

import kr.co.sist.user.view.MainForm;
import kr.co.sist.user.view.MenuForm;
import kr.co.sist.user.view.MenuDetailDialog;
import kr.co.sist.user.view.OptionDialog;
import kr.co.sist.user.view.PaymentSelectForm;
import kr.co.sist.user.service.CartListService;

/**
 * 카페 키오스크 프로그램 실행 클래스
 *
 * 1번 담당자 실행 진입점
 *
 * 메인 화면 → 메뉴 조회 → 상품 상세
 * → 옵션 선택 → 장바구니 → 결제
 */
public class KioskMain {

    /**
     * 프로그램 시작
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // 1번, 2번, 3번 담당자 기능 연결
            initializeTeamIntegration();

            // 메인 화면 생성 및 표시
            new MainForm().setVisible(true);
        });
    }

    /**
     * 팀원 기능 연동
     */
    private static void initializeTeamIntegration() {

        // ====================================
        // 1번 → 2번 : 옵션 선택 화면 연결
        // ====================================

        /*
         * 메뉴 상세 화면에서 옵션 선택 버튼 클릭 시
         * 선택한 MenuDTO를 OptionDialog에 전달한다.
         */
        MenuDetailDialog.setOptionHandler(
            menu -> new OptionDialog(menu)
        );

        // ====================================
        // 2번 → 1번 : 장바구니 정보 연결
        // ====================================

        /*
         * 2번 담당자의 CartListService에서
         * 장바구니에 담긴 총 수량과 총금액을 가져온다.
         *
         * MenuForm 하단에 표시되는
         * 담은 상품 수량 / 합계 금액에 사용한다.
         */
        MenuForm.setCartSummarySuppliers(
            CartListService::getCartItemCount,
            CartListService::getCartTotalAmount
        );

        // ====================================
        // 1번 → 3번 : 결제 화면 연결
        // ====================================

        /*
         * 메뉴 화면 하단의 결제 버튼 클릭 시
         * 3번 담당자의 결제수단 선택 화면을 실행한다.
         *
         * 장바구니가 비어 있는 경우에는
         * MenuForm에서 결제를 제한한다.
         */
        MenuForm.setPaymentHandler(
            () -> new PaymentSelectForm()
        );

        // ====================================
        // 매장 / 포장 주문 유형 연결
        // ====================================

        /*
         * MainForm의 주문 유형 선택 기능은
         * MainForm.setOrderTypeHandler(...)의
         * 실제 매개변수 형식을 확인한 후 연결한다.
         *
         * CartListService.setOrderType(...) 메서드는
         * 2번 담당자 코드에 구현되어 있다.
         */
    }
}
//
