package kr.co.sist.admin.dto;

public class MenuOptionDTO {
	private long optionId;
	private String optionName;
	private int extraPrice;

	public MenuOptionDTO() {
	}

	public long getOptionId() { return optionId; }
	public void setOptionId(long optionId) { this.optionId = optionId; }
	public String getOptionName() { return optionName; }
	public void setOptionName(String optionName) { this.optionName = optionName; }
	public int getExtraPrice() { return extraPrice; }
	public void setExtraPrice(int extraPrice) { this.extraPrice = extraPrice; }
}
