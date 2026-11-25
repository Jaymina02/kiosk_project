
package kr.co.sist.user.evt;

import javax.swing.JOptionPane;

import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.service.MenuService;
import kr.co.sist.user.view.MenuForm;

/**
 * 메뉴 화면 이벤트 클래스
 *
 * 1. 카테고리 조회
 * 2. 카테고리 선택
 * 3. 메뉴 상세 선택
 * 4. 결제 버튼 처리
 */
public class MenuEvt {

    private final MenuForm form;

    private final MenuService service =
            new MenuService();

    public MenuEvt(MenuForm form) {
        this.form = form;
    }

    /**
     * 카테고리 전체 조회
     */
    public void loadCategories() {

        try {

            form.displayCategories(
                service.searchCategories()
            );

        } catch (IllegalStateException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                form,
                "카테고리 조회 실패: "
                    + rootMessage(ex),
                "DB 오류",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * 카테고리 선택
     */
    public void selectCategory(
            long categoryId) {

        try {

            form.setSelectedCategoryId(
                categoryId
            );

            form.displayCategories(
                service.searchCategories()
            );

            form.displayMenuThumbnails(
                service.searchMenus(categoryId)
            );

        } catch (IllegalStateException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                form,
                "메뉴 조회 실패: "
                    + rootMessage(ex),
                "DB 오류",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * 메뉴 클릭
     */
    public void selectMenu(long menuId) {

        try {

            MenuDTO selected =
                service.searchMenuDetail(menuId);

            if (selected == null) {

                JOptionPane.showMessageDialog(
                    form,
                    "선택한 메뉴를 찾을 수 없습니다."
                );

            } else if (
                    selected.getMenuStatus() != 0) {

                JOptionPane.showMessageDialog(
                    form,
                    "현재 품절된 메뉴입니다."
                );

            } else {

                form.openMenuDetail(menuId);
            }

        } catch (IllegalStateException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                form,
                "메뉴 상세 조회 실패: "
                    + rootMessage(ex),
                "DB 오류",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * 결제 버튼 클릭
     */
    public void clickCheckout() {

        form.openPaymentSelectForm();
    }

    /**
     * 가장 안쪽 오류 메시지 가져오기
     */
    private static String rootMessage(
            Throwable ex) {

        while (ex.getCause() != null) {

            ex = ex.getCause();
        }

        return ex.getMessage() == null
            ? ex.toString()
            : ex.getMessage();
    }
}
