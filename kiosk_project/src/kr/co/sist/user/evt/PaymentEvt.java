package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.service.OrderService;
import kr.co.sist.user.view.PaymentForm;

public class PaymentEvt implements ActionListener {
    private PaymentForm form;
    private OrderService orderService;

    public PaymentEvt(PaymentForm form) {
        this.form = form;
        this.orderService = new OrderService();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == form.getJbtnPay()) {
            processPayment();
        } else if (src == form.getJbtnBack()) {
            form.returnToPaymentSelect();
        }
    }

    public void processPayment() {
        List<CartDTO> items = CartListService.getCheckoutItems();
        if (items == null || items.isEmpty()) {
            JOptionPane.showMessageDialog(form, "결제할 주문 내역이 없습니다.", "결제 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderType = CartListService.getOrderType();
        int paymentType = form.getPaymentType();

        try {
            OrderDTO order = orderService.placeOrder(items, orderType, paymentType);
            List<OrderDetailDTO> details = orderService.searchOrderDetails(order.getOrderId());
            CartListService.resetOrder();

            JOptionPane.showMessageDialog(form,
                    "결제가 완료되었습니다.\n주문번호: " + order.getOrderNo(),
                    "결제 완료", JOptionPane.INFORMATION_MESSAGE);

            form.openOrderCompleteForm(order, details);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(form,
                    "결제 처리 중 DB 오류가 발생했습니다.\n" + ex.getMessage(),
                    "결제 실패", JOptionPane.ERROR_MESSAGE);
        }
    }
}
