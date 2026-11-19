
package kr.co.sist.user.run;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import kr.co.sist.user.view.MainForm;
import kr.co.sist.user.view.MenuForm;

/**
 * 카페 키오스크 프로그램 실행 클래스
 *
 * 1번 담당자 실행 진입점
 *
 * 메인 화면 -> 메뉴 화면 -> 결제 선택
 */
public class KioskMain {

    /**
     * 프로그램 시작
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // 팀원 기능 연결
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
        // 3번 담당자 결제 화면 연결
        // ====================================

        /*
         * 3번 담당자의 PaymentSelectForm은
         * 매개변수 없는 생성자를 사용한다.
         *
         * 현재는 컴파일 단계의 의존성을
         * 줄이기 위해 리플렉션으로 호출한다.
         *
         * 3번 담당자의 결제 코드와
         * 장바구니 코드가 준비되어 있어야
         * 정상 실행된다.
         */
        MenuForm.setPaymentHandler(() -> {

            try {

                Class<?> clazz =
                    Class.forName(
                        "kr.co.sist.user.view."
                        + "PaymentSelectForm"
                    );

                clazz.getDeclaredConstructor()
                     .newInstance();

            } catch (
                ReflectiveOperationException
                    | LinkageError ex
            ) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                    null,
                    "3번 담당자의 결제 화면을 "
                    + "실행할 수 없습니다.\n"
                    + "PaymentSelectForm 및 "
                    + "장바구니 연동 상태를 "
                    + "확인하세요.\n"
                    + ex,
                    "결제 화면 연결 오류",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // ====================================
        // 2번 담당자 기능 연결 예정
        // ====================================

        /*
         * 2번 담당자의 실제 코드를 받은 후
         * 다음 기능을 연결한다.
         *
         * 1. OptionDialog 열기
         * 2. 장바구니 상품 개수 조회
         * 3. 장바구니 총금액 조회
         * 4. 매장 / 포장 주문유형 전달
         *
         * 연결 대상 메서드:
         *
         * MenuDetailDialog.setOptionHandler(...)
         *
         * MenuForm.setCartSummarySuppliers(...)
         *
         * MainForm.setOrderTypeHandler(...)
         */
    }
}
