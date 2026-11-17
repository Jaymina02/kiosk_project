
package kr.co.sist.user.service;

import java.util.List;

import kr.co.sist.user.dao.MenuDAO;
import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;

/**
 * 메뉴 서비스 클래스
 *
 * View와 DAO 사이에서
 * 데이터 조회를 처리한다.
 */
public class MenuService {

    private final MenuDAO menuDAO =
            MenuDAO.getInstance();

    /**
     * 카테고리 전체 조회
     */
    public List<CategoryDTO> searchCategories() {
        return menuDAO.selectCategories();
    }

    /**
     * 메뉴 목록 조회
     */
    public List<MenuDTO> searchMenus(
            long categoryId) {

        return menuDAO.selectMenus(categoryId);
    }

    /**
     * 메뉴 상세조회
     */
    public MenuDTO searchMenuDetail(
            long menuId) {

        return menuDAO.selectMenuById(menuId);
    }

    /**
     * 옵션 조회
     * 2번 담당자 연동 전 임시 메서드
     */
    public List<?> searchMenuOptions(
            long menuId) {

        return menuDAO.selectMenuOptions(menuId);
    }
}
