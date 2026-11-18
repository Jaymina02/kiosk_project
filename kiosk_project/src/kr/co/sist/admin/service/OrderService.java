package kr.co.sist.admin.service;
import java.sql.*;
import java.util.List;
import kr.co.sist.admin.common.ConnectionProvider;
import kr.co.sist.admin.dao.OrderDAO;
import kr.co.sist.admin.dto.OrderListDTO;
public class OrderService {
    private final ConnectionProvider connections;
    private final OrderDAO orderDAO;
    public OrderService(ConnectionProvider connections,OrderDAO dao) { this.connections=connections;orderDAO=dao; }
    public List<OrderListDTO> searchOrderList() throws SQLException { return orderDAO.selectOrderList(); }
    public OrderActionResult completeOrder(long orderId) throws SQLException {
        try(Connection con=connections.getConnection()) {
            con.setAutoCommit(false);
            try {
                Integer status=orderDAO.selectOrderStatusForUpdate(con,orderId);
                if(status==null) { con.rollback();return OrderActionResult.NOT_FOUND; }
                if(status!=1) { con.rollback();return OrderActionResult.NOT_MAKING; }
                if(orderDAO.updateMakingToCompleted(con,orderId)!=1) throw new SQLException("제작완료 변경 건수가 1이 아닙니다.");
                con.commit();return OrderActionResult.SUCCESS;
            } catch(SQLException|RuntimeException ex) {
                try { con.rollback(); } catch(SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        }
    }
}
