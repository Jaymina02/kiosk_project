
package kr.co.sist.user.evt;

import kr.co.sist.user.view.MenuDetailDialog;

/**
 * 메뉴 상세 팝업 이벤트
 *
 * 1. 옵션 선택
 * 2. 상세화면 닫기
 */
public class MenuDetailEvt {

    private final MenuDetailDialog dialog;

    public MenuDetailEvt(MenuDetailDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * 옵션 선택 버튼 클릭
     */
    public void selectOption() {
        dialog.selectOption();
    }

    /**
     * 닫기 버튼 클릭
     */
    public void closeDetail() {
        dialog.closeDetail();
    }
}
