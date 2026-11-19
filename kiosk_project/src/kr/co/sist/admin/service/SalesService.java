package kr.co.sist.admin.service;

import java.sql.SQLException;
import java.util.List;

import kr.co.sist.admin.dao.SalesDAO;
import kr.co.sist.admin.dto.SalesHistoryDTO;
import kr.co.sist.admin.dto.SalesSummaryDTO;

public class SalesService {

	private SalesDAO salesDAO;

	public SalesService() {
		salesDAO = SalesDAO.getInstance();
	}

	
	public List<SalesHistoryDTO> searchSalesHistory(String startDate, String endDate) throws SQLException {
		return salesDAO.selectSalesHistory(startDate, endDate);
	}

	
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
