package kr.co.sist.user.dto;

public class OrderDetailDTO {
    private long detailId;
    private long orderId;
    private long menuId;
    private String menuNameSnapshot;
    private int basePriceSnapshot;
    private int unitPrice;
    private int quantity;
    private int lineAmount;

    public OrderDetailDTO() {
    }

    public long getDetailId() {
        return detailId;
    }

    public void setDetailId(long detailId) {
        this.detailId = detailId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public String getMenuNameSnapshot() {
        return menuNameSnapshot;
    }

    public void setMenuNameSnapshot(String menuNameSnapshot) {
        this.menuNameSnapshot = menuNameSnapshot;
    }

    public int getBasePriceSnapshot() {
        return basePriceSnapshot;
    }

    public void setBasePriceSnapshot(int basePriceSnapshot) {
        this.basePriceSnapshot = basePriceSnapshot;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLineAmount() {
        return lineAmount;
    }

    public void setLineAmount(int lineAmount) {
        this.lineAmount = lineAmount;
    }

    @Override
    public String toString() {
        return "OrderDetailDTO [detailId=" + detailId + ", orderId=" + orderId + ", menuId=" + menuId
                + ", menuNameSnapshot=" + menuNameSnapshot + ", unitPrice=" + unitPrice
                + ", quantity=" + quantity + ", lineAmount=" + lineAmount + "]";
    }
}
