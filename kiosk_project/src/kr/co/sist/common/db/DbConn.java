package kr.co.sist.common.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import kr.co.sist.common.db.DbConn;
//DB연결객체를 반환하는 일을 하는 클래스.
public class DbConn {
private static DbConn db=null;
	
	private DbConn() {	
	}//DbConn
	
	public static DbConn getInstance() {
		if(db==null) {
			db=new DbConn();
		}//end if
		return db;
	}//getInstance
	
	/**
	 * 입력된 경로에 존재하는 properties 파일을 Properties 객체로 생성하여
	 * 반환하는 일<br>
	 * 파일의 경로가 null이나 ""라면 기본 파일의 경로로
	 * c:/dev/config/database.properties가 설정된다.
	 * @param  path properties 파일의 경로
	 * @return 객체
	 */
	public Properties getProp(String path){
		Properties prop=null;
		if(path==null || "".equals(path)) {
			path="config/database.properties";
		}
		
		File readProp=new File(path);
		if(readProp.exists()) {
			BufferedReader br= null; 
			try {
				br= new BufferedReader(new FileReader(readProp));
				prop=new Properties();
				prop.load(br);
				if(br!=null) {br.close();}// end if
			} catch (IOException e) {
				e.printStackTrace();
			}//end catch
		}//end if
		
		return prop;
	}//getProp
	
	/**
	 * DB연동 작업 중 1.드라이버 로딩, 2.로딩된 드라이버를 사용하여 Connection을
	 * 얻는 일
	 * @return 연결된 connection
	 * @throws SQLException 
	 */
	public Connection getConn(Properties prop) throws SQLException {
		Connection con = null;
		if(prop!=null) {
			String url=prop.getProperty("url");
			String id=prop.getProperty("id");
			String pass=prop.getProperty("pass");
			
			con=DriverManager.getConnection(url, id, pass);
		}//end if
		
		return con;
	}//getConn
	
	/**
	 * DB작업에 사용된 객체들을 종료하는 일
	 * @param rs
	 * @param stmt
	 * @param con
	 * @throws SQLException
	 */
	public void dbClose(ResultSet rs,Statement stmt, Connection con) 
			throws SQLException {
		try {
			
		if(rs!=null) {rs.close();} // end if
		if(stmt!=null) {stmt.close();}// end if
		}finally {
		if(con!=null) {con.close();}// end if
		}//finally
	}//dbClose
	
}