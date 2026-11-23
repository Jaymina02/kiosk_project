package kr.co.sist.admin.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kr.co.sist.admin.common.AdminApplication;
import kr.co.sist.admin.view.AdminMainForm;

public class AdminMainEvt implements ActionListener {

    private final AdminMainForm form;
    private final AdminApplication app;

    public AdminMainEvt(AdminMainForm form, AdminApplication app) {
        this.form = form;
        this.app = app;

        form.getOrderButton().addActionListener(this);
        form.getLogoutButton().addActionListener(this);
        form.getMenuButton().addActionListener(this);
        form.getSalesButton().addActionListener(this);

        form.getMenuButton().setEnabled(app.hasMenuAction());
        form.getSalesButton().setEnabled(app.hasSalesAction());

        form.getMenuButton().setToolTipText(
            app.hasMenuAction() ? null : "아직 연결되지 않은 화면입니다."
        );

        form.getSalesButton().setToolTipText(
            app.hasSalesAction() ? null : "아직 연결되지 않은 화면입니다."
        );
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == form.getOrderButton()) {
            form.dispose();
            app.openOrders();
        } else if (source == form.getLogoutButton()) {
            form.dispose();
            app.openLogin();
        } else if (source == form.getMenuButton()) {
            form.dispose();
            app.openMenu();
        } else if (source == form.getSalesButton()) {
            form.dispose();
            app.openSales();
        }
    }
}