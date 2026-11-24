package kr.co.sist.admin.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JOptionPane;

import kr.co.sist.admin.dto.SalesHistoryDTO;
import kr.co.sist.admin.dto.SalesSummaryDTO;
import kr.co.sist.admin.service.SalesService;
import kr.co.sist.admin.view.SalesForm;


public class SalesEvt extends MouseAdapter implements ActionListener {

	private SalesForm salesForm;
	private SalesService salesService;

	public SalesEvt(SalesForm salesForm) {
		this.salesForm = salesForm;
		this.salesService = new SalesService();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    Object src = e.getSource();

	    if (src == salesForm.getSearchButton()) {
	        searchSales();
	    } else if (src == salesForm.getTodayButton()) {
	        searchToday();
	    } else if (src == salesForm.getMonthButton()) {
	        searchThisMonth();
	    } else if (src == salesForm.getAdminMainButton()) {
	    	if (salesForm.getApp() != null) {
	            salesForm.dispose();             
	            salesForm.getApp().openMain();     
	        } else {
	            JOptionPane.showMessageDialog(salesForm, "관리자 메인 화면과 연결되어 있지 않습니다.");
	        }
	    }
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		Object src = e.getSource();
		if (salesForm.isStartTarget(src)) {
			salesForm.pickDate(true);
		} else if (salesForm.isEndTarget(src)) {
			salesForm.pickDate(false); 
			}
	}


	public void searchSales() {
		String startDate = salesForm.getStartDate();
		String endDate = salesForm.getEndDate();

		
		try {
			LocalDate start = LocalDate.parse(startDate); 
			LocalDate end = LocalDate.parse(endDate);
			if (start.isAfter(end)) {
				JOptionPane.showMessageDialog(salesForm, "시작일이 종료일보다 늦을 수 없습니다.");
				return;
			}
		} catch (DateTimeParseException dpe) {
			JOptionPane.showMessageDialog(salesForm, "날짜는 2026-11-02 형식으로 입력해 주세요.");
			return;
		}

		
		try {
			List<SalesHistoryDTO> list = salesService.searchSalesHistory(startDate, endDate);
			SalesSummaryDTO summary = salesService.calculateSalesSummary(list);

			salesForm.setSalesHistory(list);
			salesForm.setSalesSummary(summary); 
		} catch (Exception ex) {
			ex.printStackTrace(); 
		
			JOptionPane.showMessageDialog(salesForm,
					"매출 조회 중 오류가 발생했습니다.\n\n" + ex.getClass().getName() + "\n" + ex.getMessage());
		}
	}

	
	public void searchToday() {
		String today = LocalDate.now().toString();
		salesForm.setPeriod(today, today);
		searchSales();
	}

	
	public void searchThisMonth() {
		LocalDate now = LocalDate.now();
		LocalDate first = now.withDayOfMonth(1);
		LocalDate last = now.withDayOfMonth(now.lengthOfMonth());
		salesForm.setPeriod(first.toString(), last.toString());
		searchSales();
	}

}
