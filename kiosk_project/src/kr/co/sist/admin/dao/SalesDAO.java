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

/**
 * DAO = Data Access Object (DB에 접근하는 전담 클래스)
 * 이 클래스만 SQL을 안다. 화면/버튼 코드는 SQL을 몰라도 된다.
 */
public class SalesDAO {

	// 객체를 딱 1개만 만들어 돌려쓰는 방식(싱글톤)
	private static SalesDAO sDAO;

	private SalesDAO() { // private → 밖에서 new SalesDAO() 불가
	}

	public static SalesDAO getInstance() {
		if (sDAO == null) {
			sDAO = new SalesDAO();
		}
		return sDAO;
	}

	/**
	 * 기간(startDate ~ endDate) 안의 '결제완료(COMPLETED)' 내역을 조회한다.
	 * 
	 * @param startDate 시작일 "yyyy-MM-dd"
	 * @param endDate   종료일 "yyyy-MM-dd" (이 날짜 하루 전체 포함)
	 */
	public List<SalesHistoryDTO> selectSalesHistory(String startDate, String endDate) throws SQLException {

		List<SalesHistoryDTO> list = new ArrayList<SalesHistoryDTO>();

		DbConn dbConn = DbConn.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 1) DB 연결: 우리 팀 DbConn은 properties(접속정보) 파일을 먼저 읽어서 getConn()에 넘겨야 한다
			Properties prop = dbConn.getProp(null); // null이면 기본 경로 config/database.properties
			con = dbConn.getConn(prop);

			// properties 파일을 못 찾으면 DbConn이 null을 돌려준다 → 원인을 알 수 있게 메시지를 남긴다
			if (con == null) {
				throw new SQLException("DB 연결 실패: config/database.properties 파일을 찾을 수 없거나 접속 정보가 비어 있습니다.");
			}

			// 2) SQL 준비 (? 자리에 날짜가 들어간다)
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
			pstmt.setString(1, startDate); // 첫 번째 ? 자리
			pstmt.setString(2, endDate); // 두 번째 ? 자리

			// 3) 실행 후 결과를 한 줄씩 DTO 상자에 담는다
			rs = pstmt.executeQuery();
			while (rs.next()) {
				SalesHistoryDTO dto = new SalesHistoryDTO(); // 빈 상자 준비
				dto.setOrderId(rs.getLong("order_id"));
				dto.setOrderNo(rs.getString("order_no"));
				dto.setPaymentDate(rs.getString("pay_date"));
				dto.setPaymentMethod(rs.getString("payment_method"));
				dto.setPaymentAmount(rs.getInt("payment_amount"));
				dto.setPaymentStatus(rs.getString("payment_status"));
				list.add(dto); // 채운 상자를 리스트에 넣는다
			}
		} finally {
			// 성공하든 실패하든 마지막에 항상 닫는다 (팀 DbConn의 dbClose 사용)
			dbConn.dbClose(rs, pstmt, con);
		}

		return list;
	}

}
