package kr.co.sist.user.dto;

public class OrderOptionDTO {
    private long detailOptionId;
    private long detailId;
    private long optionId;

    public OrderOptionDTO() {
    }

    public long getDetailOptionId() {
        return detailOptionId;
    }

    public void setDetailOptionId(long detailOptionId) {
        this.detailOptionId = detailOptionId;
    }

    public long getDetailId() {
        return detailId;
    }

    public void setDetailId(long detailId) {
        this.detailId = detailId;
    }

    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    @Override
    public String toString() {
        return "OrderOptionDTO [detailOptionId=" + detailOptionId + ", detailId=" + detailId + ", optionId=" + optionId + "]";
    }
}
