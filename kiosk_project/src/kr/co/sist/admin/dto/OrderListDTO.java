package kr.co.sist.admin.dto;
import java.time.LocalDateTime;

public class OrderListDTO {
    public static final int MAKING=1, COMPLETED=2;
    private long orderId;
    private String orderNo, orderContent, orderType, paymentMethod, paymentStatus;
    private LocalDateTime orderDate;
    private int totalAmount, orderStatus;
    public OrderListDTO() { }
    public long getOrderId() { return orderId; }
    public void setOrderId(long value) { orderId=value; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String value) { orderNo=value; }
    public String getOrderContent() { return orderContent; }
    public void setOrderContent(String value) { orderContent=value; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String value) { orderType=value; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String value) { paymentMethod=value; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String value) { paymentStatus=value; }
    public String getPaymentStatusName() {
        if(paymentStatus==null)return "미확인";
        if("PAID".equalsIgnoreCase(paymentStatus)||"COMPLETED".equalsIgnoreCase(paymentStatus))return "결제완료";
        if("CANCELED".equalsIgnoreCase(paymentStatus)||"CANCELLED".equalsIgnoreCase(paymentStatus))return "결제취소";
        return paymentStatus;
    }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime value) { orderDate=value; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int value) { totalAmount=value; }
    public int getOrderStatus() { return orderStatus; }
    public void setOrderStatus(int value) { orderStatus=value; }
    public String getOrderStatusName() { return orderStatus==MAKING?"제작중":"제작완료"; }
}
