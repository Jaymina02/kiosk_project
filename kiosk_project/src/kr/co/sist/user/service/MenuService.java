package kr.co.sist.user.service;

import java.util.List;

import kr.co.sist.user.dao.MenuDAO;
import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;

/**
 * 메뉴 조회 업무를 처리하는 서비스 클래스.
 *
 * Event 클래스와 DAO 사이에서
 * 데이터 조회 요청을 전달한다.
 */
public class MenuService {

    // 공통 MenuDAO 객체 사용
    private final MenuDAO menuDAO = MenuDAO.getInstance();

    /**
     * 카테고리 목록 조회.
     */
    public List<CategoryDTO> searchCategories() {
        return menuDAO.selectCategories();
    }

    /**
     * 전체 또는 카테고리별 메뉴 조회.
     */
    public List<MenuDTO> searchMenus(long categoryId) {
        return menuDAO.selectMenus(categoryId);
    }

    /**
     * 메뉴번호로 상품 상세정보 조회.
     */
    public MenuDTO searchMenuDetail(long menuId) {
        return menuDAO.selectMenuById(menuId);
    }

    /**
     * TODO [2번 담당자 연동]
     *
     * MenuOptionDTO 통합 후
     * 반환형을 List<MenuOptionDTO>로 변경한다.
     */
    public List<?> searchMenuOptions(long menuId) {
        return menuDAO.selectMenuOptions(menuId);
    }
}