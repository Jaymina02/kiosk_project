
package kr.co.sist.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kr.co.sist.common.db.DbConn;
import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;

/**
 * 카페 키오스크 사용자 메뉴 DAO
 *
 * DB 담당자의 공통 DbConn 사용
 *
 * 주요 기능
 * 1. 카테고리 전체 조회
 * 2. 카테고리별 메뉴 조회
 * 3. 메뉴 상세정보 조회
 */
public final class MenuDAO {

    private static final MenuDAO INSTANCE =
            new MenuDAO();

    private MenuDAO() {
    }

    public static MenuDAO getInstance() {
        return INSTANCE;
    }

    /**
     * DB 연결
     */
    private Connection openConnection()
            throws SQLException {

        DbConn db = DbConn.getInstance();

        Properties prop = db.getProp(
                "config/database.properties"
        );

        if (prop == null) {
            throw new SQLException(
                "DB 설정 파일이 없습니다: "
                + "config/database.properties "
                + "(Working Directory 확인)"
            );
        }

        for (String key :
                new String[]{"url", "id", "pass"}) {

            String value = prop.getProperty(key);

            if (value == null
                    || value.trim().isEmpty()) {

                throw new SQLException(
                    "database.properties 설정 누락: "
                    + key
                );
            }
        }

        Connection conn = db.getConn(prop);

        if (conn == null) {
            throw new SQLException(
                "DB Connection을 생성할 수 없습니다."
            );
        }

        return conn;
    }

    /**
     * 카테고리 전체 조회
     */
    public List<CategoryDTO> selectCategories() {

        List<CategoryDTO> result =
                new ArrayList<>();

        String sql =
            "SELECT CATEGORY_ID, CATEGORY_NAME " +
            "FROM CATEGORY " +
            "ORDER BY CATEGORY_ID";

        try (
            Connection conn = openConnection();

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                result.add(
                    new CategoryDTO(
                        rs.getLong("CATEGORY_ID"),
                        rs.getString("CATEGORY_NAME")
                    )
                );
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                "CATEGORY 조회 실패", e
            );
        }

        return result;
    }

    /**
     * 메뉴 목록 조회
     *
     * categoryId = 0 : 전체
     * categoryId > 0 : 특정 카테고리
     *
     * 품절 메뉴는 표시
     * 삭제 메뉴만 제외
     */
    public List<MenuDTO> selectMenus(
            long categoryId) {

        List<MenuDTO> result =
                new ArrayList<>();

        String sql =
            "SELECT MENU_ID, CATEGORY_ID, " +
            "MENU_NAME, BASE_PRICE, DESCRIPTION, " +
            "IMAGE_PATH, THUMBNAIL_PATH, " +
            "CAFFEINE_YN, ORIGIN_INFO, " +
            "MENU_STATUS, CREATED_DATE " +
            "FROM MENU " +
            "WHERE MENU_STATUS <> 2 " +
            "AND (? = 0 OR CATEGORY_ID = ?) " +
            "ORDER BY MENU_ID";

        try (
            Connection conn = openConnection();

            PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setLong(1, categoryId);
            ps.setLong(2, categoryId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    result.add(mapMenu(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                "MENU 목록 조회 실패", e
            );
        }

        return result;
    }

    /**
     * MENU_ID로 메뉴 상세조회
     */
    public MenuDTO selectMenuById(
            long menuId) {

        String sql =
            "SELECT MENU_ID, CATEGORY_ID, " +
            "MENU_NAME, BASE_PRICE, DESCRIPTION, " +
            "IMAGE_PATH, THUMBNAIL_PATH, " +
            "CAFFEINE_YN, ORIGIN_INFO, " +
            "MENU_STATUS, CREATED_DATE " +
            "FROM MENU " +
            "WHERE MENU_ID = ? " +
            "AND MENU_STATUS <> 2";

        try (
            Connection conn = openConnection();

            PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setLong(1, menuId);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next()
                    ? mapMenu(rs)
                    : null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                "MENU 상세 조회 실패", e
            );
        }
    }

    /**
     * ResultSet을 MenuDTO로 변환
     */
    private MenuDTO mapMenu(ResultSet rs)
            throws SQLException {

        MenuDTO menu = new MenuDTO();

        menu.setMenuId(
            rs.getLong("MENU_ID")
        );

        menu.setCategoryId(
            rs.getLong("CATEGORY_ID")
        );

        menu.setMenuName(
            rs.getString("MENU_NAME")
        );

        menu.setBasePrice(
            rs.getInt("BASE_PRICE")
        );

        menu.setDescription(
            rs.getString("DESCRIPTION")
        );

        String imagePath =
            rs.getString("IMAGE_PATH");

        menu.setImagePath(imagePath);
        menu.setDetailImagePath(imagePath);

        menu.setThumbnailPath(
            rs.getString("THUMBNAIL_PATH")
        );

        menu.setCaffeineYn(
            rs.getString("CAFFEINE_YN")
        );

        menu.setOriginInfo(
            rs.getString("ORIGIN_INFO")
        );

        menu.setMenuStatus(
            rs.getInt("MENU_STATUS")
        );

        Timestamp ts =
            rs.getTimestamp("CREATED_DATE");

        if (ts != null) {
            menu.setCreatedDate(
                ts.toLocalDateTime()
            );
        }

        return menu;
    }

    /**
     * 2번 담당자의 옵션 DAO 연동 전
     * 임시 메서드
     */
    public List<?> selectMenuOptions(
            long menuId) {

        return new ArrayList<>();
    }
}
