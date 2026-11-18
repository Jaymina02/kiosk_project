package kr.co.sist.admin.dao;
import java.sql.*;

public class PaymentDAO {
    public int deletePayment(Connection con,long orderId) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement("DELETE FROM PAYMENT WHERE ORDER_ID=?")) {
            ps.setLong(1,orderId); return ps.executeUpdate();
        }
    }
}
