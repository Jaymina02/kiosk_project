
package kr.co.sist.user.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.user.dao.MenuDAO;
import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.dto.MenuOptionDTO;

/**
 * 사용자 메뉴 서비스
 *
 * 1번 담당:
 * - 카테고리 조회
 * - 메뉴 목록 조회
 * - 메뉴 상세 조회
 *
 * 2번 담당:
 * - 전체 옵션 조회
 * - 메뉴별 옵션 조회
 */
public class MenuService {

    private final MenuDAO menuDAO;

    public MenuService() {
        this.menuDAO = MenuDAO.getInstance();
    }

    // ========================================
    // 1번 담당 - 카테고리 조회
    // ========================================

    public List<CategoryDTO> searchCategories() {
        return menuDAO.selectCategories();
    }

    // ========================================
    // 1번 담당 - 메뉴 목록 조회
    // ========================================

    public List<MenuDTO> searchMenus(long categoryId) {
        return menuDAO.selectMenus(categoryId);
    }

    // ========================================
    // 1번 담당 - 메뉴 상세 조회
    // ========================================

    public MenuDTO searchMenuDetail(long menuId) {
        return menuDAO.selectMenuById(menuId);
    }

    // ========================================
    // 2번 담당 - 전체 옵션 조회
    // ========================================

    /**
     * 2번 담당자 기존 반환 형식 유지:
     * MenuOptionDTO 목록을 CSV 문자열 목록으로 변환
     */
    public List<String> searchMenuOptions() {

        List<String> result = new ArrayList<>();

        try {

            List<MenuOptionDTO> options =
                menuDAO.selectMenuOptions();

            for (MenuOptionDTO dto : options) {

                StringBuilder csv = new StringBuilder();

                csv.append(dto.getOptionId()).append(",");
                csv.append(dto.getOptionType()).append(",");
                csv.append(dto.getTemperature()).append(",");
                csv.append(dto.getSize()).append(",");
                csv.append(dto.getOptionName()).append(",");
                csv.append(dto.getMenuName()).append(",");
                csv.append(dto.getOptionPrice()).append(",");
                csv.append(dto.getBasePrice()).append(",");
                csv.append(dto.getQuantity()).append(",");
                csv.append(dto.getTotalAmount()).append(",");
                csv.append(dto.getThumbnailPath()).append(",");
                csv.append(dto.getExtraOptionNames()).append(",");

                result.add(csv.toString());
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                "메뉴 옵션 조회 실패", e
            );
        }

        return result;
    }

    // ========================================
    // 1번·2번 연동 - 메뉴별 옵션 조회
    // ========================================

    public List<MenuOptionDTO> searchMenuOptions(long menuId) {
        return menuDAO.selectMenuOptions(menuId);
    }
}
//