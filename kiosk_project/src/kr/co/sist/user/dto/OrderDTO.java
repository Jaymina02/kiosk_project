package kr.co.sist.user.dto;

import java.time.LocalDateTime;

public class OrderDTO {
    private long orderId;
    private String orderNo;
    private String orderType;
    private int totalAmount;
    private int orderStatus;
    private LocalDateTime orderDate;

    public OrderDTO() {
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "OrderDTO [orderId=" + orderId + ", orderNo=" + orderNo + ", orderType=" + orderType
                + ", totalAmount=" + totalAmount + ", orderStatus=" + orderStatus + ", orderDate=" + orderDate + "]";
    }
}
