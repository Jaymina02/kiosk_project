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

/**
 * Evt = 버튼을 눌렀을 때 "무슨 일을 할지" 정하는 클래스
 * ActionListener를 implements 하면 버튼 클릭 소식을 받을 수 있다.
 */
public class SalesEvt extends MouseAdapter implements ActionListener {

	private SalesForm salesForm;
	private SalesService salesService;

	public SalesEvt(SalesForm salesForm) {
		this.salesForm = salesForm;
		this.salesService = new SalesService();
	}

	/** 버튼이 눌리면 자바가 자동으로 이 메서드를 호출해 준다 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource(); // 어떤 버튼이 눌렸는지

		if (src == salesForm.getSearchButton()) {
			searchSales();
		} else if (src == salesForm.getTodayButton()) {
			searchToday();
		} else if (src == salesForm.getMonthButton()) {
			searchThisMonth();
		} else if (src == salesForm.getAdminMainButton()) {
			salesForm.dispose(); // 매출조회 창 닫기
			// TODO: 팀 AdminMainForm 생성자를 확인한 뒤 아래 줄 주석을 풀어 주세요.
			// new kr.co.sist.admin.view.AdminMainForm();
		}
	}

	/** 날짜칸을 마우스로 클릭하면 자바가 자동으로 호출한다 */
	@Override
	public void mouseClicked(MouseEvent e) {
		Object src = e.getSource();
		if (salesForm.isStartTarget(src)) {
			salesForm.pickDate(true); // 시작일 달력
		} else if (salesForm.isEndTarget(src)) {
			salesForm.pickDate(false); // 종료일 달력
		}
	}

	/** [조회] 버튼: 입력한 기간으로 조회 */
	public void searchSales() {
		String startDate = salesForm.getStartDate();
		String endDate = salesForm.getEndDate();

		// 1) 날짜 입력이 올바른지 검사
		try {
			LocalDate start = LocalDate.parse(startDate); // "2026-11-02" 형식이 아니면 예외 발생
			LocalDate end = LocalDate.parse(endDate);
			if (start.isAfter(end)) {
				JOptionPane.showMessageDialog(salesForm, "시작일이 종료일보다 늦을 수 없습니다.");
				return;
			}
		} catch (DateTimeParseException dpe) {
			JOptionPane.showMessageDialog(salesForm, "날짜는 2026-11-02 형식으로 입력해 주세요.");
			return;
		}

		// 2) Service에게 조회와 합계 계산을 시키고, 결과를 화면에 전달
		try {
			List<SalesHistoryDTO> list = salesService.searchSalesHistory(startDate, endDate);
			SalesSummaryDTO summary = salesService.calculateSalesSummary(list);

			salesForm.setSalesHistory(list); // 표 채우기
			salesForm.setSalesSummary(summary); // 최종 매출액 채우기 + 결과 영역 보이기
		} catch (Exception ex) {
			ex.printStackTrace(); // 콘솔에 자세한 오류 출력
			// 원인을 알 수 있도록 오류 종류와 내용을 팝업에도 보여준다
			JOptionPane.showMessageDialog(salesForm,
					"매출 조회 중 오류가 발생했습니다.\n\n" + ex.getClass().getName() + "\n" + ex.getMessage());
		}
	}

	/** [오늘] 버튼: 기간을 오늘~오늘로 맞추고 조회 */
	public void searchToday() {
		String today = LocalDate.now().toString();
		salesForm.setPeriod(today, today);
		searchSales();
	}

	/** [이번달] 버튼: 기간을 이번 달 1일~말일로 맞추고 조회 */
	public void searchThisMonth() {
		LocalDate now = LocalDate.now();
		LocalDate first = now.withDayOfMonth(1);
		LocalDate last = now.withDayOfMonth(now.lengthOfMonth());
		salesForm.setPeriod(first.toString(), last.toString());
		searchSales();
	}

}
