package kr.co.sist.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.admin.dto.CategoryDTO;
import kr.co.sist.admin.dto.MenuDTO;
import kr.co.sist.admin.dto.MenuOptionDTO;
import kr.co.sist.common.db.DbConn;

public class MenuDAO {
	private static final String T_MENU = "MENU";
	private static final String T_CATEGORY = "CATEGORY";
	private static final String T_OPTION = "MENU_OPTION";
	private static final String T_OPTION_MAP = "MENU_OPTION_MAP";

	/* MENU_STATUS: 0 판매중, 1 품절, 2 삭제 */
	public static final int STATUS_DELETED = 2;

	private static final String MENU_COLS = "MENU_ID, CATEGORY_ID, MENU_NAME, BASE_PRICE, DESCRIPTION, "
			+ "IMAGE_PATH, THUMBNAIL_PATH, CAFFEINE_YN, ORIGIN_INFO, CREATED_DATE, MENU_STATUS";

	private static MenuDAO mDAO;

	private MenuDAO() {
	}

	public static MenuDAO getInstance() {
		if (mDAO == null) {
			mDAO = new MenuDAO();
		}
		return mDAO;
	}

	public List<CategoryDTO> selectCategories() throws SQLException {
		List<CategoryDTO> list = new ArrayList<>();
		DbConn db = DbConn.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = db.getConn(db.getProp(null));
			pstmt = con.prepareStatement(
					"SELECT CATEGORY_ID, CATEGORY_NAME FROM " + T_CATEGORY + " ORDER BY CATEGORY_ID");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new CategoryDTO(rs.getLong("CATEGORY_ID"), rs.getString("CATEGORY_NAME")));
			}
		} finally {
			db.dbClose(rs, pstmt, con);
		}
		return list;
	}

	public MenuDTO selectMenuById(long menuId) throws SQLException {
		MenuDTO dto = null;
		DbConn db = DbConn.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = db.getConn(db.getProp(null));
			pstmt = con.prepareStatement("SELECT " + MENU_COLS + " FROM " + T_MENU + " WHERE MENU_ID = ?");
			pstmt.setLong(1, menuId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				dto = toMenu(rs);
			}
		} finally {
			db.dbClose(rs, pstmt, con);
		}
		return dto;
	}

	/* 특정 메뉴에 등록된 옵션 */
	public List<MenuOptionDTO> selectMenuOptions(long menuId) throws SQLException {
		String sql = "SELECT O.OPTION_ID, O.OPTION_NAME, O.EXTRA_PRICE FROM " + T_OPTION + " O JOIN "
				+ T_OPTION_MAP + " M ON O.OPTION_ID = M.OPTION_ID WHERE M.MENU_ID = ? ORDER BY O.OPTION_ID";
		return queryOptions(sql, menuId);
	}

	/* 선택 가능한 전체 옵션 (메뉴 추가/수정 화면의 체크박스용) */
	public List<MenuOptionDTO> selectAllOptions() throws SQLException {
		String sql = "SELECT OPTION_ID, OPTION_NAME, EXTRA_PRICE FROM " + T_OPTION + " ORDER BY OPTION_ID";
		return queryOptions(sql, null);
	}

	private List<MenuOptionDTO> queryOptions(String sql, Long menuId) throws SQLException {
		List<MenuOptionDTO> list = new ArrayList<>();
		DbConn db = DbConn.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = db.getConn(db.getProp(null));
			pstmt = con.prepareStatement(sql);
			if (menuId != null) {
				pstmt.setLong(1, menuId);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				MenuOptionDTO o = new MenuOptionDTO();
				o.setOptionId(rs.getLong("OPTION_ID"));
				o.setOptionName(rs.getString("OPTION_NAME"));
				o.setExtraPrice(rs.getInt("EXTRA_PRICE"));
				list.add(o);
			}
		} finally {
			db.dbClose(rs, pstmt, con);
		}
		return list;
	}

	/* categoryId 가 0 이하이면 전체 조회 */
	public List<MenuDTO> selectAdminMenus(long categoryId) throws SQLException {
		List<MenuDTO> list = new ArrayList<>();
		DbConn db = DbConn.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = db.getConn(db.getProp(null));
			String sql = "SELECT " + MENU_COLS + " FROM " + T_MENU + " WHERE MENU_STATUS <> " + STATUS_DELETED;
			if (categoryId > 0) {
				sql += " AND CATEGORY_ID = ?";
			}
			sql += " ORDER BY MENU_ID";
			pstmt = con.prepareStatement(sql);
			if (categoryId > 0) {
				pstmt.setLong(1, categoryId);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(toMenu(rs));
			}
		} finally {
			db.dbClose(rs, pstmt, con);
		}
		return list;
	}

	public void insertMenu(Connection con, MenuDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement("SELECT COALESCE(MAX(MENU_ID), 0) + 1 FROM " + T_MENU);
			rs = pstmt.executeQuery();
			rs.next();
			dto.setMenuId(rs.getLong(1));
		} finally {
			DbConn.getInstance().dbClose(rs, pstmt, null);
		}

		String sql = "INSERT INTO " + T_MENU + " (" + MENU_COLS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, dto.getMenuId());
			pstmt.setLong(2, dto.getCategoryId());
			pstmt.setString(3, dto.getMenuName());
			pstmt.setInt(4, dto.getBasePrice());
			pstmt.setString(5, dto.getDescription());
			pstmt.setString(6, dto.getImagePath());
			pstmt.setString(7, dto.getThumbnailPath());
			pstmt.setString(8, dto.getCaffeineYn());
			pstmt.setString(9, dto.getOriginInfo());
			pstmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(11, 0); 
			pstmt.executeUpdate();
		} finally {
			DbConn.getInstance().dbClose(null, pstmt, null);
		}
	}

	public int updateMenu(Connection con, MenuDTO dto) throws SQLException {
		String sql = "UPDATE " + T_MENU + " SET CATEGORY_ID = ?, MENU_NAME = ?, BASE_PRICE = ?, DESCRIPTION = ?, "
				+ "IMAGE_PATH = ?, THUMBNAIL_PATH = ?, CAFFEINE_YN = ?, ORIGIN_INFO = ? WHERE MENU_ID = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, dto.getCategoryId());
			pstmt.setString(2, dto.getMenuName());
			pstmt.setInt(3, dto.getBasePrice());
			pstmt.setString(4, dto.getDescription());
			pstmt.setString(5, dto.getImagePath());
			pstmt.setString(6, dto.getThumbnailPath());
			pstmt.setString(7, dto.getCaffeineYn());
			pstmt.setString(8, dto.getOriginInfo());
			pstmt.setLong(9, dto.getMenuId());
			return pstmt.executeUpdate();
		} finally {
			DbConn.getInstance().dbClose(null, pstmt, null);
		}
	}

	public int updateMenuStatus(Connection con, long menuId, int status) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("UPDATE " + T_MENU + " SET MENU_STATUS = ? WHERE MENU_ID = ?");
			pstmt.setInt(1, status);
			pstmt.setLong(2, menuId);
			return pstmt.executeUpdate();
		} finally {
			DbConn.getInstance().dbClose(null, pstmt, null);
		}
	}

	public int deleteMenuOptionMap(Connection con, long menuId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("DELETE FROM " + T_OPTION_MAP + " WHERE MENU_ID = ?");
			pstmt.setLong(1, menuId);
			return pstmt.executeUpdate();
		} finally {
			DbConn.getInstance().dbClose(null, pstmt, null);
		}
	}

	public void insertMenuOptionMap(Connection con, long menuId, long optionId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("INSERT INTO " + T_OPTION_MAP + " (OPTION_ID, MENU_ID) VALUES (?, ?)");
			pstmt.setLong(1, optionId);
			pstmt.setLong(2, menuId);
			pstmt.executeUpdate();
		} finally {
			DbConn.getInstance().dbClose(null, pstmt, null);
		}
	}

	private MenuDTO toMenu(ResultSet rs) throws SQLException {
		MenuDTO m = new MenuDTO();
		m.setMenuId(rs.getLong("MENU_ID"));
		m.setCategoryId(rs.getLong("CATEGORY_ID"));
		m.setMenuName(rs.getString("MENU_NAME"));
		m.setBasePrice(rs.getInt("BASE_PRICE"));
		m.setDescription(rs.getString("DESCRIPTION"));
		m.setImagePath(rs.getString("IMAGE_PATH"));
		m.setThumbnailPath(rs.getString("THUMBNAIL_PATH"));
		m.setCaffeineYn(rs.getString("CAFFEINE_YN"));
		m.setOriginInfo(rs.getString("ORIGIN_INFO"));
		Timestamp ts = rs.getTimestamp("CREATED_DATE");
		if (ts != null) {
			m.setCreatedDate(ts.toLocalDateTime());
		}
		m.setMenuStatus(rs.getInt("MENU_STATUS"));
		return m;
	}
}
