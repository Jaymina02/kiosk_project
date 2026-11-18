package kr.co.sist.admin.dto;

public class CategoryDTO {
	private long categoryId;
	private String categoryName;

	public CategoryDTO() {
	}

	public CategoryDTO(long categoryId, String categoryName) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

	public long getCategoryId() { return categoryId; }
	public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
	public String getCategoryName() { return categoryName; }
	public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

	@Override
	public String toString() {
		return categoryName;
	}
}
