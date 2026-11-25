package kr.co.sist.user.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class MenuOptionDTO {

	private long optionId;
	private String optionType,temperature, size, 
	optionName, menuName;
	private int optionPrice, basePrice, quantity, totalAmount;
	private String thumbnailPath; // 썸네일 이미지
	private List<String> extraOptionNames;
	
	public MenuOptionDTO() {
	}
	
	
	
}