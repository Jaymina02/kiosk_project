package kr.co.sist.admin.dto;

/**
 * 결제 내역 "한 줄"을 담는 상자(DTO)
 * 화면 표의 한 행(주문번호/결제일시/결제방식/결제금액/상태)과 1:1로 대응된다.
 */
public class SalesHistoryDTO {

	private long orderId; // 주문 고유번호 (DB 내부용 PK)
	private String orderNo; // 고객에게 보이는 주문번호 (화면 '주문번호' 칸)
	private String paymentDate; // 결제일시 (예: 2026-11-02 09:12)
	private String paymentMethod; // 결제방식 (카드 / 현금)
	private String paymentStatus; // 상태 (결제완료)
	private int paymentAmount; // 결제금액

	public SalesHistoryDTO() {
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public int getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(int paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

}
