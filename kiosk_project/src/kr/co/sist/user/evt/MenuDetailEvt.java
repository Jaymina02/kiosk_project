package kr.co.sist.user.evt;

import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.service.MenuService;
import kr.co.sist.user.view.MenuDetailDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * 상품 상세 팝업에서 발생하는 이벤트 처리.
 *
 * 1. 상품 상세정보 조회
 * 2. 상품 이미지 확대
 * 3. 옵션 선택 버튼 처리
 * 4. 상세 팝업 닫기
 */
public class MenuDetailEvt implements ActionListener {

    // 이벤트가 발생한 상세 팝업
    private final MenuDetailDialog menuDetailDialog;

    // 상품 상세정보 조회 서비스
    private final MenuService menuService;

    /**
     * 상세 팝업 객체를 전달받는 생성자.
     */
    public MenuDetailEvt(MenuDetailDialog dialog) {

        this.menuDetailDialog = dialog;

        // MenuService 생성
        this.menuService = new MenuService();
    }

    /**
     * 상세 팝업 버튼 클릭 이벤트.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // 클릭한 버튼 확인
        Object source = e.getSource();

        // 닫기 버튼 클릭
        if (source ==
                menuDetailDialog.getCloseButton()) {

            closeDetail();

        // 옵션 선택 버튼 클릭
        } else if (source ==
                menuDetailDialog.getOptionButton()) {

            selectOption();
        }
    }

    /**
     * Oracle DB에서 선택한 상품 상세정보 조회.
     */
    public void searchMenuDetail(long menuId) {

        try {

            // 상품 고유번호로 상세정보 조회
            MenuDTO menu =
                    menuService.searchMenuDetail(menuId);

            // 상품이 없거나 삭제된 경우
            if (menu == null || menu.isDeleted()) {

                JOptionPane.showMessageDialog(
                        menuDetailDialog,
                        "존재하지 않거나 삭제된 상품입니다."
                );

                // 상세 팝업 종료
                closeDetail();
                return;
            }

            // 조회한 상품 정보를 화면에 전달
            menuDetailDialog.setSelectedMenu(menu);

        } catch (RuntimeException e) {

            // Eclipse 콘솔에 오류 출력
            e.printStackTrace();

            // 오류 안내창 표시
            JOptionPane.showMessageDialog(
                    menuDetailDialog,
                    "상품 상세정보 조회 실패\n"
                            + e.getMessage()
            );

            // 오류 발생 시 상세 팝업 종료
            closeDetail();
        }
    }

    /**
     * 상품 이미지 클릭 시 확대 표시.
     */
    public void clickDetailImage() {

        // 상세 이미지 표시 메서드 호출
        menuDetailDialog.showDetailImage();
    }

    /**
     * 옵션 선택 버튼 클릭 처리.
     *
     * 옵션창을 열기 전에 DB에서 최신 판매 상태를 확인한다.
     */
    public void selectOption() {

        try {

            // 현재 상세 팝업의 메뉴번호 확인
            long menuId = menuDetailDialog.getMenuId();

            // 상품의 최신 상태를 DB에서 다시 조회
            MenuDTO latestMenu =
                    menuService.searchMenuDetail(menuId);

            // 품절 또는 삭제 상태일 경우
            if (latestMenu == null
                    || !latestMenu.isOrderable()) {

                JOptionPane.showMessageDialog(
                        menuDetailDialog,
                        "현재 주문할 수 없는 상품입니다."
                );

                closeDetail();
                return;
            }

            // 최신 상품 정보를 상세 팝업에 저장
            menuDetailDialog.setSelectedMenu(latestMenu);

            // 2번 담당자의 옵션 선택 화면 호출
            menuDetailDialog.openOptionDialog();

        } catch (RuntimeException e) {

            // Eclipse 콘솔에 상세 오류 출력
            e.printStackTrace();

            // 옵션 선택 오류 안내창
            JOptionPane.showMessageDialog(
                    menuDetailDialog,
                    "옵션 선택 오류\n" + e.getMessage()
            );
        }
    }

    /**
     * 상세 팝업 닫기.
     */
    public void closeDetail() {

        menuDetailDialog.closeDialog();
    }
}