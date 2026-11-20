package kr.co.sist.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import kr.co.sist.common.db.DbConn;
import kr.co.sist.user.dto.PaymentDTO;

public class PaymentDAO {
    private static PaymentDAO pDAO;

    private PaymentDAO() {
    }

    public static PaymentDAO getInstance() {
        if (pDAO == null) {
            pDAO = new PaymentDAO();
        }
        return pDAO;
    }

    public int insertPayment(Connection conn, PaymentDTO payment) throws SQLException {
        int rowCnt = 0;
        PreparedStatement pstmt = null;

        String sql = "INSERT INTO PAYMENT (PAYMENT_ID, ORDER_ID, PAYMENT_METHOD, PAYMENT_STATUS, PAYMENT_AMOUNT, PAYMENT_DATE) "
                   + "VALUES ((SELECT NVL(MAX(PAYMENT_ID), 0) + 1 FROM PAYMENT), ?, ?, ?, ?, SYSDATE)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, payment.getOrderId());
            pstmt.setString(2, payment.getPaymentMethod());
            pstmt.setString(3, payment.getPaymentStatus());
            pstmt.setInt(4, payment.getPaymentAmount());

            rowCnt = pstmt.executeUpdate();
        } finally {
            DbConn.getInstance().dbClose(null, pstmt, null);
        }
        return rowCnt;
    }
}
