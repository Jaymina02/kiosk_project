package kr.co.sist.admin.service;
import java.sql.*;
import kr.co.sist.admin.common.ConnectionProvider;
import kr.co.sist.admin.dao.*;

public class CancelService {
    private final ConnectionProvider connections;
    private final OrderDAO orderDAO;
    private final PaymentDAO paymentDAO;
    public CancelService(ConnectionProvider connections,OrderDAO orderDAO,PaymentDAO paymentDAO) {
        this.connections=connections;this.orderDAO=orderDAO;this.paymentDAO=paymentDAO;
    }
    public OrderActionResult cancelOrder(long orderId) throws SQLException {
        try(Connection con=connections.getConnection()) {
            con.setAutoCommit(false);
            try {
                Integer status=orderDAO.selectOrderStatusForUpdate(con,orderId);
                if(status==null) { con.rollback();return OrderActionResult.NOT_FOUND; }
                if(status!=1) { con.rollback();return OrderActionResult.NOT_MAKING; }
                // 옵션은 선택하지 않았을 수도 있으므로 삭제 0건도 정상입니다.
                orderDAO.deleteOrderOptions(con,orderId);
                orderDAO.deleteOrderDetails(con,orderId);
                orderDAO.deleteCancelRequests(con,orderId); // 이전 DB에 남아 있는 FK도 정리
                paymentDAO.deletePayment(con,orderId);
                if(orderDAO.deleteMakingOrder(con,orderId)!=1) throw new SQLException("주문 삭제 건수가 1이 아닙니다.");
                con.commit();return OrderActionResult.SUCCESS;
            } catch(SQLException|RuntimeException ex) {
                try { con.rollback(); } catch(SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        }
    }
}
