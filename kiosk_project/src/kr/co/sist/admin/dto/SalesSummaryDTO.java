package kr.co.sist.admin.dto;

/**
 * 매출 "합계"를 담는 상자(DTO)
 * 화면 위쪽의 '최종 매출액 20000원' 숫자 하나를 담는다.
 */
public class SalesSummaryDTO {

	private long finalSales; // 최종 매출액

	public SalesSummaryDTO() {
	}

	public long getFinalSales() {
		return finalSales;
	}

	public void setFinalSales(long finalSales) {
		this.finalSales = finalSales;
	}

}
