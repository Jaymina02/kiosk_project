
package kr.co.sist.user.dto;

/**
 * 카테고리 정보를 저장하는 DTO
 */
public class CategoryDTO {

    private long categoryId;
    private String categoryName;

    public CategoryDTO() {
    }

    public CategoryDTO(long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
