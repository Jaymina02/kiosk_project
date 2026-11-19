package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import kr.co.sist.user.view.PaymentSelectForm;

public class PaymentSelectEvt implements ActionListener {
    private PaymentSelectForm form;

    public PaymentSelectEvt(PaymentSelectForm form) {
        this.form = form;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == form.getJbtnCard()) {
            selectPaymentType(0);
            proceedToPayment();
        } else if (src == form.getJbtnCash()) {
            selectPaymentType(1);
            proceedToPayment();
        } else if (src == form.getJbtnBack()) {
            CartListService.clearDirectOrderItem();
            form.returnToPreviousForm();
        }
    }

    public void selectPaymentType(int paymentType) {
        form.setSelectedPaymentType(paymentType);
    }

    public void proceedToPayment() {
        int paymentType = form.getSelectedPaymentType();
        if (paymentType != 0 && paymentType != 1) {
            JOptionPane.showMessageDialog(form, "결제 수단을 선택해 주세요.", "안내", JOptionPane.WARNING_MESSAGE);
            return;
        }
        form.openPaymentForm(paymentType);
    }
}
