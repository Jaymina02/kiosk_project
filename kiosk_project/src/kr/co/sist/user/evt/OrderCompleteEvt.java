package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Timer;

import kr.co.sist.user.service.CartListService;
import kr.co.sist.user.view.MainForm;
import kr.co.sist.user.view.OrderCompleteForm;

public class OrderCompleteEvt extends WindowAdapter implements ActionListener {
    private OrderCompleteForm form;
    private Timer autoReturnTimer;
    private int remainSeconds = 5;
    private boolean moved = false;

    public OrderCompleteEvt(OrderCompleteForm form) {
        this.form = form;
        this.form.addWindowListener(this);
        startAutoReturnTimer();
    }

    private void startAutoReturnTimer() {
        form.getJbtnMain().setText("처음으로 (" + remainSeconds + "초)");

        autoReturnTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainSeconds--;
                if (remainSeconds > 0) {
                    form.getJbtnMain().setText("처음으로 (" + remainSeconds + "초)");
                } else {
                    returnToMain();
                }
            }
        });
        autoReturnTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == form.getJbtnMain()) {
            returnToMain();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        returnToMain();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (autoReturnTimer != null) {
            autoReturnTimer.stop();
        }
    }

    public void returnToMain() {
        if (moved) {
            return;
        }
        moved = true;

        if (autoReturnTimer != null) {
            autoReturnTimer.stop();
        }

        CartListService.resetOrder();
        form.dispose();

        MainForm.setOrderTypeHandler(CartListService::setOrderType);
        MainForm mainForm = new MainForm();
        mainForm.setVisible(true);
    }
}