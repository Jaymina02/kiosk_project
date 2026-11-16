package kr.co.sist.user.dto;

/**
 * CATEGORY 테이블의 카테고리 정보를 저장하는 클래스.
 * 메뉴 화면의 카테고리 버튼 생성에 사용된다.
 */
public class CategoryDTO {

    // CATEGORY_ID : 카테고리 고유번호
    private long categoryId;

    // CATEGORY_NAME : 카테고리 이름
    private String categoryName;

    // 기본 생성자
    public CategoryDTO() {
    }

    // 카테고리 번호와 이름을 전달받는 생성자
    public CategoryDTO(long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // 카테고리 고유번호 반환
    public long getCategoryId() {
        return categoryId;
    }

    // 카테고리 고유번호 변경
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    // 카테고리 이름 반환
    public String getCategoryName() {
        return categoryName;
    }

    // 카테고리 이름 변경
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}