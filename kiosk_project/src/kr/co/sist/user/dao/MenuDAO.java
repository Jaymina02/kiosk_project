
package kr.co.sist.user.dao;

// DB 연결을 담당하는 공통 클래스
import kr.co.sist.db.DbConn;

// 카테고리 정보를 저장하는 DTO
import kr.co.sist.user.dto.CategoryDTO;

// 메뉴 정보를 저장하는 DTO
import kr.co.sist.user.dto.MenuDTO;

// JDBC 관련 클래스
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

// 목록을 저장하는 클래스
import java.util.ArrayList;
import java.util.List;

/**
 * MenuDAO
 *
 * 카페 키오스크의 메뉴 관련 DB 작업을 담당한다.
 *
 * 주요 기능
 * 1. 카테고리 전체 조회
 * 2. 카테고리별 메뉴 조회
 * 3. 메뉴 상세정보 조회
 * 4. 메뉴 옵션 조회 (추후 연동)
 *
 * Oracle DB 연결은 DbConn.getConnection()을 사용한다.
 */
public final class MenuDAO {

    // ========================================
    // 1. 싱글턴 패턴
    // ========================================

    // 프로그램 전체에서 하나의 MenuDAO 객체를 사용한다.
    private static final MenuDAO instance = new MenuDAO();

    // 외부에서 직접 객체를 생성하지 못하도록 한다.
    private MenuDAO() {
    }

    /**
     * 공통 MenuDAO 객체를 반환한다.
     *
     * @return MenuDAO 객체
     */
    public static MenuDAO getInstance() {

        return instance;
    }


    // ========================================
    // 2. Oracle DB 연결
    // ========================================

    /**
     * DbConn을 이용해 Oracle DB에 연결한다.
     *
     * DbConn.java에 설정된
     * DB 주소, 아이디, 비밀번호를 사용한다.
     *
     * @return Connection 객체
     * @throws SQLException 연결 실패
     */
    private Connection openConnection() throws SQLException {

        // DbConn의 DB 연결 메서드 호출
        return DbConn.getConnection();
    }


    // ========================================
    // 3. 카테고리 전체 조회
    // ========================================

    /**
     * CATEGORY 테이블의 모든 카테고리를 조회한다.
     *
     * @return 카테고리 목록
     */
    public List<CategoryDTO> selectCategories() {

        // 조회한 카테고리를 저장할 리스트
        List<CategoryDTO> categoryList = new ArrayList<>();

        // CATEGORY 테이블 조회 SQL
        String sql =
                "SELECT CATEGORY_ID, CATEGORY_NAME " +
                "FROM CATEGORY " +
                "ORDER BY CATEGORY_ID";

        try (
            // Oracle DB 연결
            Connection conn = openConnection();

            // SQL 실행 준비
            PreparedStatement pstmt =
                    conn.prepareStatement(sql);

            // SQL 실행 결과 가져오기
            ResultSet rs = pstmt.executeQuery()
        ) {

            // 조회된 카테고리가 있는 동안 반복
            while (rs.next()) {

                // 카테고리 DTO 객체 생성
                CategoryDTO category = new CategoryDTO();

                // 카테고리 번호 저장
                category.setCategoryId(
                        rs.getLong("CATEGORY_ID")
                );

                // 카테고리 이름 저장
                category.setCategoryName(
                        rs.getString("CATEGORY_NAME")
                );

                // 조회한 카테고리를 리스트에 추가
                categoryList.add(category);
            }

        } catch (SQLException e) {

            // SQL 또는 DB 연결 오류 전달
            throw new IllegalStateException(
                    "CATEGORY 조회 실패", e
            );
        }

        // 카테고리 목록 반환
        return categoryList;
    }


    // ========================================
    // 4. 메뉴 목록 조회
    // ========================================

    /**
     * 전체 메뉴 또는 카테고리별 메뉴를 조회한다.
     *
     * categoryId = 0 : 전체 메뉴
     * categoryId = 1 : 커피
     * categoryId = 2 : 티
     * categoryId = 3 : 스무디
     * categoryId = 4 : 디저트
     *
     * MENU_STATUS = 2인 삭제 메뉴는 제외한다.
     *
     * @param categoryId 카테고리 번호
     * @return 메뉴 목록
     */
    public List<MenuDTO> selectMenus(long categoryId) {

        // 조회 결과를 저장할 리스트
        List<MenuDTO> menuList = new ArrayList<>();

        /*
         * MENU 테이블에서 메뉴 정보 조회
         *
         * 실제 DB 컬럼명을 사용한다.
         *
         * MENU_STATUS
         * 0 = 판매중
         * 1 = 품절
         * 2 = 삭제
         */
        String sql =
                "SELECT MENU_ID, CATEGORY_ID, MENU_NAME, " +
                "BASE_PRICE, DESCRIPTION, IMAGE_PATH, " +
                "THUMBNAIL_PATH, CAFFEINE_YN, ORIGIN_INFO, " +
                "MENU_STATUS, CREATED_DATE " +
                "FROM MENU " +
                "WHERE MENU_STATUS <> 2 " +
                "AND (? = 0 OR CATEGORY_ID = ?) " +
                "ORDER BY MENU_ID";

        try (
            // Oracle DB 연결
            Connection conn = openConnection();

            // SQL 실행 준비
            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            // 첫 번째 ?에 카테고리 번호 전달
            pstmt.setLong(1, categoryId);

            // 두 번째 ?에도 카테고리 번호 전달
            pstmt.setLong(2, categoryId);

            // SQL 조회 실행
            try (ResultSet rs = pstmt.executeQuery()) {

                // 메뉴 목록을 순서대로 읽는다.
                while (rs.next()) {

                    // DB 데이터를 MenuDTO로 변환
                    MenuDTO menu = mapMenu(rs);

                    // 조회한 메뉴를 리스트에 추가
                    menuList.add(menu);
                }
            }

        } catch (SQLException e) {

            // 조회 실패 시 예외 전달
            throw new IllegalStateException(
                    "MENU 목록 조회 실패", e
            );
        }

        // 전체 또는 카테고리별 메뉴 목록 반환
        return menuList;
    }


    // ========================================
    // 5. 메뉴 상세정보 조회
    // ========================================

    /**
     * 메뉴 고유번호로 상품 상세정보를 조회한다.
     *
     * MenuDetailDialog에서 사용한다.
     *
     * @param menuId 메뉴 고유번호
     * @return 메뉴 DTO, 없으면 null
     */
    public MenuDTO selectMenuById(long menuId) {

        // 클릭한 메뉴의 상세정보 조회
        String sql =
                "SELECT MENU_ID, CATEGORY_ID, MENU_NAME, " +
                "BASE_PRICE, DESCRIPTION, IMAGE_PATH, " +
                "THUMBNAIL_PATH, CAFFEINE_YN, ORIGIN_INFO, " +
                "MENU_STATUS, CREATED_DATE " +
                "FROM MENU " +
                "WHERE MENU_ID = ? " +
                "AND MENU_STATUS <> 2";

        try (
            // Oracle DB 연결
            Connection conn = openConnection();

            // SQL 실행 준비
            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            // 메뉴 고유번호 설정
            pstmt.setLong(1, menuId);

            // SQL 실행
            try (ResultSet rs = pstmt.executeQuery()) {

                // 메뉴가 존재하면
                if (rs.next()) {

                    // 해당 메뉴 정보를 DTO로 변환해 반환
                    return mapMenu(rs);
                }
            }

        } catch (SQLException e) {

            // 상세정보 조회 실패 시 예외 전달
            throw new IllegalStateException(
                    "MENU 상세정보 조회 실패", e
            );
        }

        // 메뉴가 없거나 삭제된 경우 null 반환
        return null;
    }


    // ========================================
    // 6. ResultSet을 MenuDTO로 변환
    // ========================================

    /**
     * Oracle DB 조회 결과를 MenuDTO 객체로 변환한다.
     *
     * 메뉴 목록과 메뉴 상세정보 조회에서
     * 공통으로 사용하는 메서드다.
     *
     * @param rs ResultSet 객체
     * @return MenuDTO 객체
     */
    private MenuDTO mapMenu(ResultSet rs)
            throws SQLException {

        // 메뉴 정보를 담을 DTO 생성
        MenuDTO menu = new MenuDTO();

        // 메뉴 고유번호
        menu.setMenuId(
                rs.getLong("MENU_ID")
        );

        // 카테고리 고유번호
        menu.setCategoryId(
                rs.getLong("CATEGORY_ID")
        );

        // 메뉴 이름
        menu.setMenuName(
                rs.getString("MENU_NAME")
        );

        // 메뉴 기본가격
        menu.setBasePrice(
                rs.getInt("BASE_PRICE")
        );

        // 메뉴 설명
        menu.setDescription(
                rs.getString("DESCRIPTION")
        );

        /*
         * 상품 이미지 경로
         * 메뉴 상세화면에서 사용한다.
         */
        String imagePath = rs.getString("IMAGE_PATH");

        menu.setImagePath(imagePath);
        menu.setDetailImagePath(imagePath);

        /*
         * 상품 썸네일 경로
         * 메뉴 목록 카드에서 사용한다.
         */
        menu.setThumbnailPath(
                rs.getString("THUMBNAIL_PATH")
        );

        // 카페인 포함 여부
        menu.setCaffeineYn(
                rs.getString("CAFFEINE_YN")
        );

        // 원산지 정보
        menu.setOriginInfo(
                rs.getString("ORIGIN_INFO")
        );

        /*
         * 메뉴 판매상태
         * 0 = 판매중, 1 = 품절, 2 = 삭제
         */
        menu.setMenuStatus(
                rs.getInt("MENU_STATUS")
        );

        // DB에 저장된 상품 등록일
        Timestamp createdDate =
                rs.getTimestamp("CREATED_DATE");

        // 등록일이 존재할 때만 변환
        if (createdDate != null) {

            menu.setCreatedDate(
                    createdDate.toLocalDateTime()
            );
        }

        // 변환된 메뉴 DTO 반환
        return menu;
    }


    // ========================================
    // 7. 메뉴 옵션 조회 (임시)
    // ========================================

    /**
     * 메뉴 옵션 조회 메서드
     *
     * 2번 담당자의 MenuOptionDTO와
     * 옵션 조회 기능이 완성되면 연동한다.
     *
     * 현재는 빈 리스트를 반환한다.
     *
     * @param menuId 메뉴 고유번호
     * @return 임시 빈 옵션 목록
     */
    public List<?> selectMenuOptions(long menuId) {

        // 추후 옵션 담당자 코드와 연동
        return new ArrayList<>();
    }
}
