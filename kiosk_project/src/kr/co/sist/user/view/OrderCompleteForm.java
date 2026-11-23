package kr.co.sist.user.view;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;

/**
 * [임시 연결용 화면]
 * 4번 파트 담당자의 실제 OrderCompleteForm이 main에 병합되면 이 파일은 삭제하세요.
 */
public class OrderCompleteForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private OrderDTO order;
    private List<OrderDetailDTO> details;

    public OrderCompleteForm(OrderDTO order, List<OrderDetailDTO> details) {
        super("주문 완료 (4번 파트 화면 연결 확인)");
        this.order = order;
        this.details = details;

        String orderNo = (order != null) ? order.getOrderNo() : "N/A";
        int cnt = (details != null) ? details.size() : 0;
        add(new JLabel("주문 완료! 주문번호: " + orderNo + " (상세항목 " + cnt + "건)", SwingConstants.CENTER));

        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public OrderDTO getOrder() {
        return order;
    }

    public List<OrderDetailDTO> getDetails() {
        return details;
    }

	public Object getJbtnMain() {
		return null;
	}
}