package kr.co.sist.user.dto;

import java.time.LocalDateTime;

public class PaymentDTO {
    private long paymentId;
    private long orderId;
    private String paymentMethod;
    private String paymentStatus;
    private int paymentAmount;
    private LocalDateTime paymentDate;

    public PaymentDTO() {
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "PaymentDTO [paymentId=" + paymentId + ", orderId=" + orderId + ", paymentMethod=" + paymentMethod
                + ", paymentStatus=" + paymentStatus + ", paymentAmount=" + paymentAmount
                + ", paymentDate=" + paymentDate + "]";
    }
}
