
package kr.co.sist.user.dto;

import java.time.LocalDateTime;

/**
 * 메뉴 정보를 저장하는 DTO
 */
public class MenuDTO {

    private long menuId;
    private long categoryId;
    private String menuName;
    private int basePrice;
    private String description;
    private String imagePath;
    private String detailImagePath;
    private String thumbnailPath;
    private String caffeineYn;
    private String originInfo;
    private int menuStatus;
    private LocalDateTime createdDate;

    public MenuDTO() {
    }

    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDetailImagePath() {
        return detailImagePath;
    }

    public void setDetailImagePath(String detailImagePath) {
        this.detailImagePath = detailImagePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getCaffeineYn() {
        return caffeineYn;
    }

    public void setCaffeineYn(String caffeineYn) {
        this.caffeineYn = caffeineYn;
    }

    public String getOriginInfo() {
        return originInfo;
    }

    public void setOriginInfo(String originInfo) {
        this.originInfo = originInfo;
    }

    public int getMenuStatus() {
        return menuStatus;
    }

    public void setMenuStatus(int menuStatus) {
        this.menuStatus = menuStatus;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 품절 여부
     * 0 = 판매중
     * 1 = 품절
     */
    public boolean isSoldOut() {
        return menuStatus == 1;
    }
}
