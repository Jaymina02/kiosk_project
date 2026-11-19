package kr.co.sist.admin.service;

import java.sql.SQLException;
import java.util.List;

import kr.co.sist.admin.dao.SalesDAO;
import kr.co.sist.admin.dto.SalesHistoryDTO;
import kr.co.sist.admin.dto.SalesSummaryDTO;

/**
 * Service = 업무 규칙(계산)을 담당하는 클래스
 * - DB에서 데이터 가져오기는 DAO에게 시키고
 * - 그 데이터로 "최종 매출액 = 결제금액의 합" 계산을 한다.
 */
public class SalesService {

	private SalesDAO salesDAO;

	public SalesService() {
		salesDAO = SalesDAO.getInstance();
	}

	/** 기간별 결제 내역 조회 (DAO에게 그대로 시킨다) */
	public List<SalesHistoryDTO> searchSalesHistory(String startDate, String endDate) throws SQLException {
		return salesDAO.selectSalesHistory(startDate, endDate);
	}

	/** 결제 내역들의 금액을 모두 더해 최종 매출액을 만든다 */
	public SalesSummaryDTO calculateSalesSummary(List<SalesHistoryDTO> history) {
		long total = 0;
		for (SalesHistoryDTO dto : history) {
			total += dto.getPaymentAmount();
		}

		SalesSummaryDTO summary = new SalesSummaryDTO();
		summary.setFinalSales(total);
		return summary;
	}

}
