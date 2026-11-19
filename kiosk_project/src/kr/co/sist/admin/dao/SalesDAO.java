package kr.co.sist.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kr.co.sist.admin.dto.SalesHistoryDTO;
import kr.co.sist.common.db.DbConn;

public class SalesDAO {

	
	private static SalesDAO sDAO;

	private SalesDAO() { 
	}

	public static SalesDAO getInstance() {
		if (sDAO == null) {
			sDAO = new SalesDAO();
		}
		return sDAO;
	}

	
	public List<SalesHistoryDTO> selectSalesHistory(String startDate, String endDate) throws SQLException {

		List<SalesHistoryDTO> list = new ArrayList<SalesHistoryDTO>();

		DbConn dbConn = DbConn.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			
			Properties prop = dbConn.getProp(null); 
			con = dbConn.getConn(prop);

			
			if (con == null) {
				throw new SQLException("DB 연결 실패: config/database.properties 파일을 찾을 수 없거나 접속 정보가 비어 있습니다.");
			}

		
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT o.order_id, o.order_no,                                      ");
			sql.append("        TO_CHAR(p.payment_date, 'YYYY-MM-DD HH24:MI') AS pay_date,   ");
			sql.append("        p.payment_method, p.payment_amount, p.payment_status         ");
			sql.append(" FROM   payment p JOIN orders o ON p.order_id = o.order_id           ");
			sql.append(" WHERE  p.payment_status = 'COMPLETED'                               ");
			sql.append("   AND  p.payment_date >= TO_DATE(?, 'YYYY-MM-DD')                   ");
			sql.append("   AND  p.payment_date <  TO_DATE(?, 'YYYY-MM-DD') + 1               ");
			sql.append(" ORDER BY p.payment_date                                             ");

			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, startDate); 
			pstmt.setString(2, endDate); 

	
			rs = pstmt.executeQuery();
			while (rs.next()) {
				SalesHistoryDTO dto = new SalesHistoryDTO(); 
				dto.setOrderId(rs.getLong("order_id"));
				dto.setOrderNo(rs.getString("order_no"));
				dto.setPaymentDate(rs.getString("pay_date"));
				dto.setPaymentMethod(rs.getString("payment_method"));
				dto.setPaymentAmount(rs.getInt("payment_amount"));
				dto.setPaymentStatus(rs.getString("payment_status"));
				list.add(dto);
			}
		} finally {
			
			dbConn.dbClose(rs, pstmt, con);
		}

		return list;
	}

}
