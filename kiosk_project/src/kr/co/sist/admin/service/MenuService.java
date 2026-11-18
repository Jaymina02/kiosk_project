package kr.co.sist.admin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import kr.co.sist.admin.dao.MenuDAO;
import kr.co.sist.admin.dto.CategoryDTO;
import kr.co.sist.admin.dto.MenuDTO;
import kr.co.sist.admin.dto.MenuOptionDTO;
import kr.co.sist.common.db.DbConn;

public class MenuService {
	private MenuDAO menuDAO = MenuDAO.getInstance();

	public List<CategoryDTO> searchCategories() throws SQLException {
		return menuDAO.selectCategories();
	}

	public MenuDTO searchMenuDetail(long menuId) throws SQLException {
		return menuDAO.selectMenuById(menuId);
	}

	public List<MenuOptionDTO> searchMenuOptions(long menuId) throws SQLException {
		return menuDAO.selectMenuOptions(menuId);
	}

	/* 선택 가능한 전체 옵션 목록 */
	public List<MenuOptionDTO> searchAllOptions() throws SQLException {
		return menuDAO.selectAllOptions();
	}

	/* categoryId 가 0 이면 전체 */
	public List<MenuDTO> searchAdminMenus(long categoryId) throws SQLException {
		return menuDAO.selectAdminMenus(categoryId);
	}

	public boolean addMenu(MenuDTO dto, List<Long> optionIds) throws SQLException {
		Connection con = null;
		try {
			con = DbConn.getInstance().getConn(DbConn.getInstance().getProp(null));
			con.setAutoCommit(false);
			menuDAO.insertMenu(con, dto);
			for (Long optionId : optionIds) {
				menuDAO.insertMenuOptionMap(con, dto.getMenuId(), optionId);
			}
			con.commit();
			return true;
		} catch (SQLException e) {
			rollback(con);
			throw e;
		} finally {
			DbConn.getInstance().dbClose(null, null, con);
		}
	}

	public boolean modifyMenu(MenuDTO dto, List<Long> optionIds) throws SQLException {
		Connection con = null;
		try {
			con = DbConn.getInstance().getConn(DbConn.getInstance().getProp(null));
			con.setAutoCommit(false);
			if (menuDAO.updateMenu(con, dto) == 0) {
				rollback(con);
				return false;
			}
			menuDAO.deleteMenuOptionMap(con, dto.getMenuId());
			for (Long optionId : optionIds) {
				menuDAO.insertMenuOptionMap(con, dto.getMenuId(), optionId);
			}
			con.commit();
			return true;
		} catch (SQLException e) {
			rollback(con);
			throw e;
		} finally {
			DbConn.getInstance().dbClose(null, null, con);
		}
	}

	/* status: 0 판매중, 1 품절 */
	public boolean changeMenuStatus(long menuId, int status) throws SQLException {
		Connection con = null;
		try {
			con = DbConn.getInstance().getConn(DbConn.getInstance().getProp(null));
			return menuDAO.updateMenuStatus(con, menuId, status) > 0;
		} finally {
			DbConn.getInstance().dbClose(null, null, con);
		}
	}

	/* 소프트 삭제 */
	public boolean removeMenu(long menuId) throws SQLException {
		return changeMenuStatus(menuId, MenuDAO.STATUS_DELETED);
	}

	private void rollback(Connection con) {
		try {
			if (con != null) {
				con.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
