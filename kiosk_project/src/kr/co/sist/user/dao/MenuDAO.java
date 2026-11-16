
package kr.co.sist.user.dao;

// DB 담당자가 만든 공통 연결 클래스
import kr.co.sist.common.db.DbConn;

// 카테고리 및 메뉴 DTO
import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;

// JDBC 관련 클래스
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

// 리스트 및 DB 설정 관련 클래스
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * MenuDAO
 *
 * 카페 키오스크 사용자 메뉴 조회 DAO
 *
 * [주요 기능]
 * 1. 카테고리 전체 조회
 * 2. 전체 및 카테고리별 메뉴 조회
 * 3. 메뉴 상세정보 조회
 * 4. 메뉴 옵션 조회 (2번 담당자 연동 예정)
 *
 * [DB 연결]
 * 팀 공통 DbConn 클래스를 사용한다.
 *
 * [MENU_STATUS]
 * 0 : 판매중
 * 1 : 품절
 * 2 : 삭제
 */
public final class MenuDAO {

    // ========================================
    // 1. 싱글턴 객체
    // ========================================

    // 프로그램 전체에서 사용할 MenuDAO 객체
    private static final MenuDAO instance =
            new MenuDAO();

    /**
     * 외부에서 new MenuDAO() 호출을 방지한다.
     */
    private MenuDAO() {
    }

    /**
     * 공통 MenuDAO 객체 반환
     *
     * @return MenuDAO 객체
     */
    public static MenuDAO getInstance() {

        return instance;
    }


    // ========================================
    // 2. DB 연결
    // ========================================

    /**
     * DB 담당자가 작성한 DbConn을 이용해
     * Oracle DB 연결 객체를 반환한다.
     *
     * 연결 과정
     *
     * 1. DbConn 싱글턴 객체 가져오기
     * 2. database.properties 설정 파일 읽기
     * 3. 설정값을 이용해 Oracle DB 연결
     *
     * @return Connection 객체
     * @throws SQLException 연결 실패
     */
    private Connection openConnection()
            throws SQLException {

        // 공통 DB 연결 객체 가져오기
        DbConn dbConn = DbConn.getInstance();

        /*
         * DB 접속정보가 저장된 파일 읽기
         *
         * 프로젝트 기준 경로:
         * config/database.properties
         */
        Properties prop = dbConn.getProp(
                "config/database.properties"
        );

        // DB 설정 파일이 없는 경우
        if (prop == null) {

            throw new SQLException(
                    "DB 설정 파일을 찾을 수 없습니다. "
                    + "config/database.properties를 확인하세요."
            );
        }

        // DB 설정값이 모두 있는지 확인
        String url = prop.getProperty("url");
        String id = prop.getProperty("id");
        String pass = prop.getProperty("pass");

        if (url == null || url.isBlank()
                || id == null || id.isBlank()
                || pass == null || pass.isBlank()) {

            throw new SQLException(
                    "database.properties의 "
                    + "url, id, pass 설정을 확인하세요."
            );
        }

        // 공통 DbConn을 이용해 Oracle 연결
        Connection conn = dbConn.getConn(prop);

        // 연결 결과 확인
        if (conn == null) {

            throw new SQLException(
                    "Oracle Connection 객체를 "
                    + "생성하지 못했습니다."
            );
        }

        // 연결된 Connection 반환
        return conn;
    }


    // ========================================
    // 3. 카테고리 전체 조회
    // ========================================

    /**
     * CATEGORY 테이블의 전체 카테고리 조회
     *
     * @return 카테고리 목록
     */
    public List<CategoryDTO> selectCategories() {

        // 조회 결과를 저장할 리스트
        List<CategoryDTO> categoryList =
                new ArrayList<>();

        // CATEGORY 테이블 조회 SQL
        String sql =
                "SELECT CATEGORY_ID, CATEGORY_NAME " +
                "FROM CATEGORY " +
                "ORDER BY CATEGORY_ID";

        /*
         * try-with-resources 사용
         *
         * SQL 실행이 끝나면
         * ResultSet, PreparedStatement,
         * Connection이 자동으로 닫힌다.
         */
        try (
            // DB 연결
            Connection conn = openConnection();

            // SQL 실행 준비
            PreparedStatement pstmt =
                    conn.prepareStatement(sql);

            // SQL 실행
            ResultSet rs = pstmt.executeQuery()
        ) {

            // 조회된 데이터 반복
            while (rs.next()) {

                // 카테고리 DTO 생성
                CategoryDTO category =
                        new CategoryDTO();

                // 카테고리 번호 저장
                category.setCategoryId(
                        rs.getLong("CATEGORY_ID")
                );

                // 카테고리 이름 저장
                category.setCategoryName(
                        rs.getString("CATEGORY_NAME")
                );

                // 조회 목록에 추가
                categoryList.add(category);
            }

        } catch (SQLException e) {

            // Console에서 상세 오류 확인 가능
            throw new IllegalStateException(
                    "CATEGORY 조회 실패", e
            );
        }

        // 조회한 카테고리 목록 반환
        return categoryList;
    }


    // ========================================
    // 4. 메뉴 목록 조회
    // ========================================

    /**
     * 전체 또는 카테고리별 메뉴 조회
     *
     * categoryId = 0 : 전체 메뉴
     * categoryId > 0 : 해당 카테고리 메뉴
     *
     * MENU_STATUS = 2인 삭제 메뉴는 제외
     *
     * @param categoryId 카테고리 번호
     * @return 메뉴 목록
     */
    public List<MenuDTO> selectMenus(long categoryId) {

        // 메뉴 조회 결과 저장
        List<MenuDTO> menuList =
                new ArrayList<>();

        /*
         * 실제 Oracle MENU 테이블 컬럼 사용
         *
         * MENU_STATUS
         * 0 = 판매중
         * 1 = 품절
         * 2 = 삭제
         *
         * 삭제 메뉴는 사용자 화면에 표시하지 않는다.
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

            // 첫 번째 ? : 전체 메뉴 여부
            pstmt.setLong(1, categoryId);

            // 두 번째 ? : 카테고리 번호
            pstmt.setLong(2, categoryId);

            // 메뉴 조회
            try (ResultSet rs = pstmt.executeQuery()) {

                // 조회한 메뉴 반복
                while (rs.next()) {

                    // SQL 결과를 MenuDTO로 변환
                    MenuDTO menu = mapMenu(rs);

                    // 목록에 추가
                    menuList.add(menu);
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "MENU 목록 조회 실패", e
            );
        }

        // 메뉴 목록 반환
        return menuList;
    }


    // ========================================
    // 5. 메뉴 상세정보 조회
    // ========================================

    /**
     * 메뉴 고유번호를 이용한 상세 조회
     *
     * MenuDetailDialog에서 사용한다.
     *
     * @param menuId 메뉴 고유번호
     * @return 메뉴 DTO 또는 null
     */
    public MenuDTO selectMenuById(long menuId) {

        /*
         * 선택한 메뉴 한 개 조회
         *
         * 삭제된 메뉴는 제외한다.
         */
        String sql =
                "SELECT MENU_ID, CATEGORY_ID, MENU_NAME, " +
                "BASE_PRICE, DESCRIPTION, IMAGE_PATH, " +
                "THUMBNAIL_PATH, CAFFEINE_YN, ORIGIN_INFO, " +
                "MENU_STATUS, CREATED_DATE " +
                "FROM MENU " +
                "WHERE MENU_ID = ? " +
                "AND MENU_STATUS <> 2";

        try (
            // DB 연결
            Connection conn = openConnection();

            // SQL 실행 준비
            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            // 선택한 메뉴 ID 설정
            pstmt.setLong(1, menuId);

            // 메뉴 상세 조회
            try (ResultSet rs = pstmt.executeQuery()) {

                // 해당 메뉴가 존재하면
                if (rs.next()) {

                    // 메뉴 DTO로 변환 후 반환
                    return mapMenu(rs);
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "MENU 상세정보 조회 실패", e
            );
        }

        // 메뉴가 없거나 삭제 상태일 때
        return null;
    }


    // ========================================
    // 6. ResultSet을 MenuDTO로 변환
    // ========================================

    /**
     * DB 조회 결과 한 행을
     * MenuDTO 객체로 변환한다.
     *
     * 메뉴 목록 조회 및 상세조회에서
     * 공통으로 사용한다.
     *
     * @param rs 조회 결과
     * @return MenuDTO 객체
     */
    private MenuDTO mapMenu(ResultSet rs)
            throws SQLException {

        // 메뉴 DTO 생성
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

        // 기본가격
        menu.setBasePrice(
                rs.getInt("BASE_PRICE")
        );

        // 메뉴 설명
        menu.setDescription(
                rs.getString("DESCRIPTION")
        );

        /*
         * 상세 이미지 경로
         *
         * 예:
         * /images/americano.jpg
         */
        String imagePath =
                rs.getString("IMAGE_PATH");

        // 메뉴 기본 이미지 경로 저장
        menu.setImagePath(imagePath);

        // 메뉴 상세 이미지 경로 저장
        menu.setDetailImagePath(imagePath);

        /*
         * 메뉴 썸네일 이미지 경로
         *
         * 예:
         * /images/americano_thumb.jpg
         */
        menu.setThumbnailPath(
                rs.getString("THUMBNAIL_PATH")
        );

        // 카페인 여부
        menu.setCaffeineYn(
                rs.getString("CAFFEINE_YN")
        );

        // 원산지 정보
        menu.setOriginInfo(
                rs.getString("ORIGIN_INFO")
        );

        /*
         * 메뉴 판매 상태
         * 0 = 판매중
         * 1 = 품절
         * 2 = 삭제
         */
        menu.setMenuStatus(
                rs.getInt("MENU_STATUS")
        );

        // 메뉴 등록일 조회
        Timestamp createdDate =
                rs.getTimestamp("CREATED_DATE");

        // 등록일이 있는 경우 변환
        if (createdDate != null) {

            menu.setCreatedDate(
                    createdDate.toLocalDateTime()
            );
        }

        // 변환된 메뉴 DTO 반환
        return menu;
    }


    // ========================================
    // 7. 메뉴 옵션 조회 (추후 연동)
    // ========================================

    /**
     * 메뉴 옵션 조회
     *
     * 2번 담당자의 MenuOptionDTO와
     * 옵션 DAO가 완성되면 연결한다.
     *
     * 현재는 빈 리스트 반환
     *
     * @param menuId 메뉴 고유번호
     * @return 옵션 목록
     */
    public List<?> selectMenuOptions(long menuId) {

        // 2번 담당자 연동 전 임시 반환값
        return new ArrayList<>();
    }
}
