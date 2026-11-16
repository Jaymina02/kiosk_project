package kr.co.sist.user.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MENU 테이블의 상품 정보를 저장하는 DTO.
 *
 * 1번 사용자 메뉴 화면과 2번 옵션 화면,
 * 5번 관리자 메뉴 화면에서 공통으로 사용한다.
 */
public class MenuDTO {

    // 메뉴 판매 상태 상수
    public static final int ON_SALE = 0;  // 판매중
    public static final int SOLD_OUT = 1; // 품절
    public static final int DELETED = 2;  // 삭제

    // MENU_ID : 메뉴 고유번호
    private long menuId;

    // CATEGORY_ID : 메뉴가 속한 카테고리 번호
    private long categoryId;

    // MENU_NAME : 메뉴 이름
    private String menuName;

    // BASE_PRICE : 기본가격
    private int basePrice;

    // DESCRIPTION : 메뉴 설명
    private String description;

    // IMAGE_PATH : 상세 이미지 경로
    private String imagePath;

    // THUMBNAIL_PATH : 썸네일 이미지 경로
    private String thumbnailPath;

    // 상세 이미지 경로를 별도로 사용하는 UI 호환 필드
    private String detailImagePath;

    // CAFFEINE_YN : 카페인 포함 여부(Y/N)
    private String caffeineYn;

    // ORIGIN_INFO : 원산지
    private String originInfo;

    // MENU_STATUS : 0 판매중 / 1 품절 / 2 삭제
    private int menuStatus;

    // CREATED_DATE : 메뉴 등록일
    private LocalDateTime createdDate;

    /*
     * TODO - 2번 담당자의 MenuOptionDTO 통합 후
     * 아래 세 목록을 List<MenuOptionDTO>로 변경한다.
     *
     * 현재 List<?>를 사용하는 이유는
     * MenuOptionDTO.java가 아직 없어도
     * 1번 담당 코드가 컴파일되도록 하기 위해서다.
     */
    private List<?> temperatureOptions = new ArrayList<>();
    private List<?> sizeOptions = new ArrayList<>();
    private List<?> extraOptions = new ArrayList<>();

    // 기본 생성자
    public MenuDTO() {
    }

    // 정상 판매중인 메뉴인지 판단
    public boolean isOrderable() {
        return menuStatus == ON_SALE;
    }

    // 품절 메뉴인지 판단
    public boolean isSoldOut() {
        return menuStatus == SOLD_OUT;
    }

    // 삭제된 메뉴인지 판단
    public boolean isDeleted() {
        return menuStatus == DELETED;
    }

    // 메뉴 고유번호 Getter / Setter
    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    // 카테고리 번호 Getter / Setter
    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    // 메뉴 이름 Getter / Setter
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    // 기본가격 Getter / Setter
    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    // 메뉴 설명 Getter / Setter
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 상세 이미지 경로 Getter / Setter
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.detailImagePath = imagePath;
    }

    // 썸네일 이미지 경로 Getter / Setter
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    // 상세 이미지 경로 별칭 Getter / Setter
    public String getDetailImagePath() {
        return detailImagePath;
    }

    public void setDetailImagePath(String detailImagePath) {
        this.detailImagePath = detailImagePath;
        this.imagePath = detailImagePath;
    }

    // 카페인 여부 Getter / Setter
    public String getCaffeineYn() {
        return caffeineYn;
    }

    public void setCaffeineYn(String caffeineYn) {
        this.caffeineYn = caffeineYn;
    }

    // 원산지 Getter / Setter
    public String getOriginInfo() {
        return originInfo;
    }

    public void setOriginInfo(String originInfo) {
        this.originInfo = originInfo;
    }

    // 메뉴 상태 Getter / Setter
    public int getMenuStatus() {
        return menuStatus;
    }

    public void setMenuStatus(int menuStatus) {
        this.menuStatus = menuStatus;
    }

    // 메뉴 등록일 Getter / Setter
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    // 온도 옵션 목록 Getter / Setter
    public List<?> getTemperatureOptions() {
        return temperatureOptions;
    }

    public void setTemperatureOptions(List<?> options) {
        this.temperatureOptions = options;
    }

    // 사이즈 옵션 목록 Getter / Setter
    public List<?> getSizeOptions() {
        return sizeOptions;
    }

    public void setSizeOptions(List<?> options) {
        this.sizeOptions = options;
    }

    // 추가 옵션 목록 Getter / Setter
    public List<?> getExtraOptions() {
        return extraOptions;
    }

    public void setExtraOptions(List<?> options) {
        this.extraOptions = options;
    }
}