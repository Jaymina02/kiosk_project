package kr.co.sist.admin.run;

import kr.co.sist.admin.view.SalesForm;

/**
 * 매출조회 화면만 따로 실행해서 테스트하는 시작점.
 * (나중에 팀이 합칠 때는 관리자 메인에서 new SalesForm() 으로 열면 된다)
 */
public class SalesMain {

	public static void main(String[] args) {
		new SalesForm();
	}

}
