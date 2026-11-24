package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.co.sist.user.service.MenuService;

class MenuOption {

	@DisplayName("모든 옵션 검색")
	@Test
	void test() {
//		MenuDAO mDAO = MenuDAO.getInstance();
//		assertDoesNotThrow(()->mDAO.selectMenuOptions());
		
		MenuService ms=new MenuService();
//		System.out.println(ms.searchMenuOptions());
		assertEquals(ms.searchMenuOptions(1L).size(),8);
	}

}
