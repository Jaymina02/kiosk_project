package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.co.sist.common.db.DbConn;

class DbConnTest {

	@DisplayName("Properties 객체 생성 테스트")
	@Test
	void test() {
		DbConn dc = DbConn.getInstance();
		Properties prop = dc.getProp(null);

		assertNotNull(prop);
		assertFalse(prop.isEmpty());
	}

	@DisplayName("Connection Test")
	@Test
	void connectionTest() {

		DbConn dc = DbConn.getInstance();
		Properties prop = dc.getProp(null);

		Connection con = assertDoesNotThrow(() -> {
			return dc.getConn(prop);
		});

		assertAll(
				() -> assertNotNull(con),

				() -> assertFalse(con.isClosed()));

		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();;
		}
	}
}
