
package kr.co.sist.user.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.user.dao.MenuDAO;
import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.dto.MenuOptionDTO;

/**
 * 카페 키오스크 메뉴 서비스
 *
 * [1번 담당]
 * 1. 카테고리 조회
 * 2. 메뉴 목록 조회
 * 3. 메뉴 상세 조회
 *
 * [2번 담당]
 * 4. 전체 메뉴 옵션 조회 및 CSV 변환
 * 5. 특정 메뉴 옵션 조회
 *
 * 1번, 2번 담당 기능 통합
 */
public class MenuService {

    private final MenuDAO menuDAO =
            MenuDAO.getInstance();

    // =========================================
    // 1. 카테고리 조회 - 1번 담당
    // =========================================

    public List<CategoryDTO> searchCategories() {

        return menuDAO.selectCategories();
    }

    // =========================================
    // 2. 메뉴 목록 조회 - 1번 담당
    // =========================================

    public List<MenuDTO> searchMenus(
            long categoryId) {

        return menuDAO.selectMenus(categoryId);
    }

    // =========================================
    // 3. 메뉴 상세 조회 - 1번 담당
    // =========================================

    public MenuDTO searchMenuDetail(
            long menuId) {

        return menuDAO.selectMenuById(menuId);
    }

    // =========================================
    // 4. 전체 메뉴 옵션 조회 - 2번 담당
    // =========================================

    /**
     * 2번 담당자 기존 메서드 유지
     *
     * MENU_OPTION의 전체 옵션을 조회한 뒤
     * CSV 문자열 형태의 리스트로 변환
     *
     * 반환 예시:
     *
     * 1,SIZE,null,null,Regular,null,0,0,0,0,null,null,
     */
    public List<String> searchMenuOptions() {

        List<String> listMenuOption =
                new ArrayList<>();

        try {

            List<MenuOptionDTO> tempList =
                    menuDAO.selectMenuOptions();

            StringBuilder csvData =
                    new StringBuilder();

            for (MenuOptionDTO dto : tempList) {

                csvData.setLength(0);

                csvData
                        .append(dto.getOptionId())
                        .append(",")

                        .append(dto.getOptionType())
                        .append(",")

                        .append(dto.getTemperature())
                        .append(",")

                        .append(dto.getSize())
                        .append(",")

                        .append(dto.getOptionName())
                        .append(",")

                        .append(dto.getMenuName())
                        .append(",")

                        .append(dto.getOptionPrice())
                        .append(",")

                        .append(dto.getBasePrice())
                        .append(",")

                        .append(dto.getQuantity())
                        .append(",")

                        .append(dto.getTotalAmount())
                        .append(",")

                        .append(dto.getThumbnailPath())
                        .append(",")

                        .append(dto.getExtraOptionNames())
                        .append(",");

                listMenuOption.add(
                        csvData.toString()
                );
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "전체 MENU_OPTION 조회 실패",
                    e
            );
        }

        return listMenuOption;
    }

    // =========================================
    // 5. 특정 메뉴 옵션 조회 - 1번 + 2번
    // =========================================

    /**
     * 메뉴 ID에 따라 선택 가능한 옵션 조회
     *
     * 예:
     * 아메리카노 선택 ->
     * Regular, Large, 샷 추가 등
     *
     * @param menuId 메뉴 ID
     * @return 해당 메뉴에 연결된 옵션 DTO 목록
     */
    public List<MenuOptionDTO> searchMenuOptions(
            long menuId) {

        return menuDAO.selectMenuOptions(menuId);
    }
}
