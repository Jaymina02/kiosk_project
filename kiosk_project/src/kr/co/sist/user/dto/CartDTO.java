package kr.co.sist.user.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartDTO {

	private MenuDTO menu;
	private MenuOptionDTO temperatureOption;
	private MenuOptionDTO sizeOption;
	private List<MenuOptionDTO> extraOptions;
	private int quantity;
	private int unitPrice;
	
	public CartDTO() {
		
	}//CartDTO
	
	// 장바구니 한 줄의 총 금액 계산 (단가 * 수량)
	public int getLineAmount() {
		return this.unitPrice * this.quantity;
	}//getLineAmount
	

//	// MenuDTO에서 썸네일 경로를 바로 꺼내오는 편의 메서드
	public String getThumbnailPath() {
		return (this.menu != null) ? this.menu.getThumbnailPath() : null;
	}//getThumbnailPath

	// MenuDTO에서 메뉴 이름을 바로 꺼내오는 편의 메서드
	public String getMenuName() {
		return (this.menu != null) ? this.menu.getMenuName() : null;
	}//getMenuName
	

	}
