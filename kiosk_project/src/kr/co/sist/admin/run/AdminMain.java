package kr.co.sist.admin.run;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import kr.co.sist.admin.common.*;
import kr.co.sist.admin.dao.*;
import kr.co.sist.admin.service.*;
import kr.co.sist.admin.view.SalesForm;

public class AdminMain {

    public static void main(String[] args) {
        start(new TeamConnectionProvider());
    }

    public static void start(ConnectionProvider connections) {
        launch(createDatabaseApplication(connections));
    }

    public static AdminApplication createDatabaseApplication(
            ConnectionProvider connections) {

        OrderDAO orderDAO = new OrderDAO(connections);

        AdminApplication app = new AdminApplication(
            new AdminService(new AdminDAO(connections)),
            new OrderService(connections, orderDAO),
            new CancelService(
                connections, orderDAO, new PaymentDAO())
        );

        // 관리자 메인의 매출조회 버튼 연결
        app.setSalesAction(() -> new SalesForm(app));

        return app;
    }

    public static void launch(AdminApplication app) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            Font font = new Font("SansSerif", Font.PLAIN, 14);
            Enumeration<Object> keys = UIManager.getDefaults().keys();

            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();

                if (UIManager.get(key) instanceof FontUIResource) {
                    UIManager.put(key, new FontUIResource(font));
                }
            }

            app.openLogin();
        });
    }
}