package kr.co.sist.user.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.user.dao.MenuDAO;
import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.dto.MenuOptionDTO;

public class MenuService {

	private final MenuDAO menuDAO = MenuDAO.getInstance();

	// 카테고리 전체 조회
	public List<CategoryDTO> searchCategories() {
		return menuDAO.selectCategories();
	}

	// 메뉴 목록 조회
	public List<MenuDTO> searchMenus(long categoryId) {
		return menuDAO.selectMenus(categoryId);
	}

	// 메뉴 상세 조회
	public MenuDTO searchMenuDetail(long menuId) {
		return menuDAO.selectMenuById(menuId);
	}

	// 기존 화면에서 사용하는 임시 옵션 조회
	public List<?> searchMenuOptions(long menuId) {
		return menuDAO.selectMenuOptions(menuId);
	}

	// 옵션 담당자가 사용하는 전체 옵션 조회
	public List<String> searchMenuOptions() {
		List<String> listMenuOption = new ArrayList<>();

		try {
			List<MenuOptionDTO> tempList = menuDAO.selectMenuOptions();

			for (MenuOptionDTO mDTO : tempList) {
				StringBuilder csvData = new StringBuilder();

				csvData.append(mDTO.getOptionId()).append(",").append(mDTO.getOptionType()).append(",")
						.append(mDTO.getTemperature()).append(",").append(mDTO.getSize()).append(",")
						.append(mDTO.getOptionName()).append(",").append(mDTO.getMenuName()).append(",")
						.append(mDTO.getOptionPrice()).append(",").append(mDTO.getBasePrice()).append(",")
						.append(mDTO.getQuantity()).append(",").append(mDTO.getTotalAmount()).append(",")
						.append(mDTO.getThumbnailPath()).append(",").append(mDTO.getExtraOptionNames()).append(",");

				listMenuOption.add(csvData.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listMenuOption;
	}
}
