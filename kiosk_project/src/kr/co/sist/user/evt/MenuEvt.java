package kr.co.sist.user.evt;

import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.service.MenuService;
import kr.co.sist.user.view.MenuForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * 메뉴 목록 화면에서 발생하는 버튼 이벤트 처리.
 *
 * 1. 카테고리 목록 조회
 * 2. 카테고리 선택
 * 3. 메뉴 선택
 * 4. 품절 상태 확인
 * 5. 장바구니 수량 및 금액 갱신
 * 6. 결제 버튼 클릭 처리
 */
public class MenuEvt implements ActionListener {

    // 현재 메뉴 화면
    private final MenuForm menuForm;

    // 메뉴 DB 조회 서비스를 담당하는 객체
    private final MenuService menuService;

    // 현재 선택한 카테고리 번호
    private long selectedCategoryId = 0;

    /**
     * 클래스 다이어그램의 MenuEvt(MenuForm) 생성자.
     */
    public MenuEvt(MenuForm menuForm) {

        // 메뉴 화면 참조 저장
        this.menuForm = menuForm;

        // 메뉴 조회 서비스 생성
        this.menuService = new MenuService();
    }

    /**
     * 메뉴 화면이 처음 생성될 때 실행.
     *
     * DB에서 카테고리와 전체 메뉴를 조회한다.
     */
    public void loadCategories() {

        try {

            // Oracle CATEGORY 테이블에서 목록 조회
            List<CategoryDTO> categories =
                    menuService.searchCategories();

            // 조회한 카테고리 목록 저장
            menuForm.setCategoryList(categories);

            // 카테고리 버튼 출력
            menuForm.displayCategories();

            // 처음에는 전체 메뉴 표시
            selectCategory(0);

            // 장바구니 요약 표시
            refreshCartSummary();

        } catch (RuntimeException e) {

            // DB 연결 실패 등의 오류 표시
            showError("카테고리 조회 실패", e);
        }
    }

    /**
     * MenuForm의 버튼 클릭 시 자동 실행.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // 클릭한 객체 확인
        Object source = e.getSource();

        // 결제 버튼 클릭
        if (source == menuForm.getCheckoutButton()) {

            clickCheckout();
            return;
        }

        // 클릭한 객체가 JButton인지 확인
        if (source instanceof JButton button) {

            // 카테고리 버튼에 저장한 값 확인
            Object categoryId =
                    button.getClientProperty("categoryId");

            // 상품 카드에 저장한 메뉴번호 확인
            Object menuId =
                    button.getClientProperty("menuId");

            // 카테고리 버튼을 누른 경우
            if (categoryId instanceof Number number) {

                selectCategory(number.longValue());

            // 상품 카드를 누른 경우
            } else if (menuId instanceof Number number) {

                selectMenu(number.longValue());
            }
        }
    }

    /**
     * 선택한 카테고리의 메뉴를 DB에서 조회한다.
     *
     * 0을 전달하면 전체 메뉴를 조회한다.
     */
    public void selectCategory(long categoryId) {

        try {

            // 조회할 카테고리 번호 저장
            selectedCategoryId = categoryId;

            // Oracle MENU 테이블에서 메뉴 조회
            List<MenuDTO> menus =
                    menuService.searchMenus(categoryId);

            // 조회한 메뉴 목록을 화면에 전달
            menuForm.setMenuList(menus);

            // 상품 이미지, 이름, 가격 표시
            menuForm.displayMenus();

            // 현재 카테고리 제목 변경
            if (categoryId == 0) {

                menuForm.setCategoryTitle("전체 메뉴");

            } else {

                menuForm.setCategoryTitle("카테고리별 메뉴");
            }

        } catch (RuntimeException e) {

            // 메뉴 조회 오류 메시지 표시
            showError("메뉴 조회 실패", e);
        }
    }

    /**
     * 고객이 상품 카드를 클릭했을 때 실행.
     */
    public void selectMenu(long menuId) {

        try {

            // DB에서 메뉴의 최신 정보 조회
            MenuDTO menu =
                    menuService.searchMenuDetail(menuId);

            // 상품이 삭제되었거나 존재하지 않는 경우
            if (menu == null) {

                JOptionPane.showMessageDialog(
                        menuForm,
                        "삭제되었거나 존재하지 않는 메뉴입니다."
                );

                return;
            }

            // 판매중인 상태가 아닌 경우
            if (!menu.isOrderable()) {

                JOptionPane.showMessageDialog(
                        menuForm,
                        "현재 품절된 메뉴입니다."
                );

                return;
            }

            // 판매중이면 상품 상세 팝업 표시
            menuForm.openMenuDetail(menuId);

        } catch (RuntimeException e) {

            // 메뉴 선택 오류 표시
            showError("메뉴 선택 실패", e);
        }
    }

    /**
     * 장바구니 상품 개수 및 총금액 표시.
     *
     * 2번 담당자의 CartListService와 연결되면
     * 실제 데이터를 가져오게 된다.
     */
    public void refreshCartSummary() {

        // MenuForm에 등록된 장바구니 조회 함수 실행
        menuForm.refreshCartSummary();
    }

    /**
     * 고객이 결제 버튼을 눌렀을 때 실행.
     */
    public void clickCheckout() {

        // 3번 담당자의 결제 화면으로 연결
        menuForm.openPaymentSelectForm();
    }

    // 현재 선택한 카테고리 번호 반환
    public long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    /**
     * 오류 발생 시 Eclipse 콘솔과 사용자 화면에 표시.
     */
    private void showError(
            String message,
            RuntimeException e) {

        // Eclipse Console에 상세 오류 출력
        e.printStackTrace();

        // 사용자에게 오류 안내창 표시
        JOptionPane.showMessageDialog(
                menuForm,
                message + "\n" + e.getMessage(),
                "오류",
                JOptionPane.ERROR_MESSAGE
        );
    }
}