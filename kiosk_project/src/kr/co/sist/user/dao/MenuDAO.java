package kr.co.sist.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.common.db.DbConn;
import kr.co.sist.user.dto.MenuOptionDTO;



public class MenuDAO {
	private static MenuDAO mDAO;
	private MenuDAO() {
	}//MenuDAO
	
	public static MenuDAO getInstance() {
		if(mDAO==null) {
			mDAO=new MenuDAO();
		}//end if
		return mDAO;
	}//getInstance
	
	/**
	 * 메뉴 옵션 검색
	 * @return
	 * @throws SQLException
	 */
	public List<MenuOptionDTO> selectMenuOptions() throws SQLException{
		List<MenuOptionDTO> listMenuOption = new ArrayList<MenuOptionDTO>();
		
		Connection con=null; //DB연결관리, transaction 처리, 쿼리문 생성객체 얻는 일
		PreparedStatement pstmt = null; // 쿼리문을 전처리하여 생성, 쿼리 실행
		ResultSet rs = null; //cursor의 제어권을 얻어, 레코드를 다음으로 이동, 데이터형 변경
		
		DbConn dbCon=DbConn.getInstance();
		try {
//		1. 드라이버 로딩
//		2. 커넥션 얻기
		con=dbCon.getConn(dbCon.getProp(null));
//		3. 쿼리문 생성객체 얻기
		StringBuilder selectMenuOption = new StringBuilder();
		selectMenuOption
		.append("	select option_id, option_name, extra_price, option_type	")
		.append("	from menu_option	")
		.append("	order by option_id	");
		
		pstmt=con.prepareStatement(selectMenuOption.toString());
//		4. 바인드 변수 설정
//		5. 실행 후 결과 얻기
		rs=pstmt.executeQuery();
		
		MenuOptionDTO mDTO = null;//여러 개의 컬럼값을 하나로 묶어서 저장.
		while(rs.next()) { // 다음 레코드가 존재?
		mDTO=new MenuOptionDTO();
		mDTO.setOptionId(rs.getInt("option_id"));
		mDTO.setOptionName(rs.getString("option_name"));
		mDTO.setOptionPrice(rs.getInt("extra_price"));
		mDTO.setOptionType(rs.getNString("option_type"));
		
		listMenuOption.add(mDTO);//검색된 여러 개의 레코드를 list에 넣어서
		//하나로 관리한다.
		}
		}finally {
//		6. 연결 끊기
		dbCon.dbClose(rs, pstmt, con);
		}//end finally 
		return listMenuOption;
	}
	
}
