package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import kr.co.sist.user.view.OrderCompleteForm;

public class OrderCompleteEvt implements ActionListener {
    private OrderCompleteForm form;

    public OrderCompleteEvt(OrderCompleteForm form) {
        this.form = form;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == form.getJbtnMain()) {
            returnToMain();
        }
    }

    public void returnToMain() {
        form.dispose();
        try {
            // MainForm을 동적으로 열기
            Class<?> mainFormClass = Class.forName("kr.co.sist.user.view.MainForm");
            mainFormClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "주문이 완료되었습니다.\n(MainForm 병합 후 자동으로 첫 화면으로 이동합니다.)",
                    "안내", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}