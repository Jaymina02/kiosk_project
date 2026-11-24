package kr.co.sist.admin.run;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import kr.co.sist.admin.common.AdminApplication;
import kr.co.sist.admin.common.ConnectionProvider;
import kr.co.sist.admin.common.TeamConnectionProvider;
import kr.co.sist.admin.dao.AdminDAO;
import kr.co.sist.admin.dao.OrderDAO;
import kr.co.sist.admin.dao.PaymentDAO;
import kr.co.sist.admin.service.AdminService;
import kr.co.sist.admin.service.CancelService;
import kr.co.sist.admin.service.OrderService;
import kr.co.sist.admin.view.AdminMenuForm;
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

        // 메뉴관리 연결
        app.setMenuAction(() -> new AdminMenuForm(app));

        // 매출조회 연결
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