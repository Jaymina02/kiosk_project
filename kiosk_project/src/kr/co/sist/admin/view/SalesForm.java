package kr.co.sist.admin.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import kr.co.sist.admin.dto.SalesHistoryDTO;
import kr.co.sist.admin.dto.SalesSummaryDTO;
import kr.co.sist.admin.evt.SalesEvt;

/**
 * View = 화면만 담당. (계산·DB 코드는 여기에 없다)
 * 화면설계서 21쪽(adminSales) 디자인: 회색 프레임 + 흰 카드 / 둥근 회색 버튼 / 달력 아이콘 날짜칸 / 회색 표
 */
public class SalesForm extends JFrame {

	// ===== 화면설계서 색상 =====
	static final Color FRAME_GRAY = new Color(0xA6A6A6); // 바깥 회색 프레임
	static final Color BTN_GRAY = new Color(0xD9D9D9); // 버튼/표 머리글
	static final Color LINE_GRAY = new Color(0x7F7F7F); // 버튼·날짜칸 테두리
	static final Color DIVIDER = new Color(0xD0D0D0); // 옅은 구분선
	static final Color ROW_DARK = new Color(0xE0E0E0); // 표 홀수 행
	static final Color ROW_LIGHT = new Color(0xF0F0F0); // 표 짝수 행
	static final String FONT_NAME = "맑은 고딕";

	private JTable salesTable;
	private DefaultTableModel tableModel;
	private JTextField startDateField, endDateField; // 날짜 표시칸(클릭하면 달력)
	private JLabel startIconLabel, endIconLabel; // 날짜칸 옆 달력 아이콘
	private JLabel finalSalesLabel; // 최종 매출액 숫자
	private JButton searchButton, todayButton, monthButton, adminMainButton;
	private JPanel resultPanel; // 최종 매출액 + 표 (조회 전에는 숨김)
	private SalesEvt salesEvt;

	private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

	public SalesForm() {
		super("정산 · 매출조회");

		// ===== 위쪽: 제목 + 관리자 메인 버튼 + 구분선 =====
		JLabel titleLabel = new JLabel("정산 · 매출조회");
		titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 26));
		adminMainButton = new RoundButton("관리자 메인");

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
				BorderFactory.createEmptyBorder(0, 0, 14, 0)));
		topPanel.add(titleLabel, BorderLayout.WEST);
		topPanel.add(adminMainButton, BorderLayout.EAST);

		// ===== 조회 기간 줄 =====
		String today = LocalDate.now().toString();
		startDateField = makeDateField(today);
		endDateField = makeDateField(today);
		startIconLabel = new JLabel(new CalendarIcon());
		endIconLabel = new JLabel(new CalendarIcon());
		startIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		endIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		searchButton = new RoundButton("조회");
		todayButton = new RoundButton("오늘");
		monthButton = new RoundButton("이번달");

		JLabel periodLabel = new JLabel("조회 기간");
		periodLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
		JLabel waveLabel = new JLabel("~");

		JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		periodPanel.setBackground(Color.WHITE);
		periodPanel.setBorder(BorderFactory.createLineBorder(DIVIDER));
		periodPanel.add(periodLabel);
		periodPanel.add(makeDateBox(startDateField, startIconLabel));
		periodPanel.add(waveLabel);
		periodPanel.add(makeDateBox(endDateField, endIconLabel));
		periodPanel.add(searchButton);
		periodPanel.add(todayButton);
		periodPanel.add(monthButton);

		JPanel northPanel = new JPanel(new BorderLayout(0, 14));
		northPanel.setOpaque(false);
		northPanel.add(topPanel, BorderLayout.NORTH);
		northPanel.add(periodPanel, BorderLayout.CENTER);

		// ===== 최종 매출액 =====
		JLabel finalTitleLabel = new JLabel("최종 매출액", SwingConstants.CENTER);
		finalTitleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
		finalSalesLabel = new JLabel("0원", SwingConstants.CENTER);
		finalSalesLabel.setFont(new Font(FONT_NAME, Font.BOLD, 32));

		JPanel summaryPanel = new JPanel(new GridLayout(2, 1));
		summaryPanel.setOpaque(false);
		summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		summaryPanel.add(finalTitleLabel);
		summaryPanel.add(finalSalesLabel);

		// ===== 결제 내역 표 (회색 머리글 + 줄무늬 행 + 검은 테두리) =====
		String[] columns = { "주문번호", "결제일시", "결제방식", "결제금액", "상태" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // 표 칸을 직접 수정하지 못하게
			}
		};
		salesTable = new JTable(tableModel);
		salesTable.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
		salesTable.setRowHeight(36);
		salesTable.setGridColor(Color.BLACK);
		salesTable.setShowGrid(true);
		salesTable.setIntercellSpacing(new Dimension(1, 1));
		salesTable.getTableHeader().setReorderingAllowed(false);
		salesTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

		// 본문 칸: 줄무늬 배경, 결제금액(3번 칸)만 오른쪽 정렬
		DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, false, false, row, column);
				setBackground(row % 2 == 0 ? ROW_DARK : ROW_LIGHT);
				setForeground(Color.BLACK);
				setHorizontalAlignment(column == 3 ? SwingConstants.RIGHT : SwingConstants.CENTER);
				setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
				return this;
			}
		};
		// 머리글 칸: 회색 배경 + 굵은 글씨
		DefaultTableCellRenderer headRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, false, false, row, column);
				setBackground(BTN_GRAY);
				setForeground(Color.BLACK);
				setFont(new Font(FONT_NAME, Font.BOLD, 13));
				setHorizontalAlignment(SwingConstants.CENTER);
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
				return this;
			}
		};
		for (int i = 0; i < columns.length; i++) {
			salesTable.getColumnModel().getColumn(i).setCellRenderer(bodyRenderer);
			salesTable.getColumnModel().getColumn(i).setHeaderRenderer(headRenderer);
		}
		// 열 너비 (긴 주문번호가 ...으로 잘리지 않도록 주문번호 칸을 넓게)
		salesTable.getColumnModel().getColumn(0).setPreferredWidth(240); // 주문번호
		salesTable.getColumnModel().getColumn(1).setPreferredWidth(170); // 결제일시
		salesTable.getColumnModel().getColumn(2).setPreferredWidth(110); // 결제방식
		salesTable.getColumnModel().getColumn(3).setPreferredWidth(130); // 결제금액
		salesTable.getColumnModel().getColumn(4).setPreferredWidth(110); // 상태

		JScrollPane scrollPane = new JScrollPane(salesTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		scrollPane.getViewport().setBackground(Color.WHITE);

		// ===== 결과 영역(처음엔 숨김) =====
		resultPanel = new JPanel(new BorderLayout(0, 18));
		resultPanel.setOpaque(false);
		resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		resultPanel.add(summaryPanel, BorderLayout.NORTH);
		resultPanel.add(scrollPane, BorderLayout.CENTER);
		resultPanel.setVisible(false); // "조회 전에는 테이블이 나타나지 않는다"

		// ===== 전체 배치: 회색 프레임 > 흰 카드 =====
		JPanel card = new JPanel(new BorderLayout(0, 10));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(28, 36, 28, 36)));
		card.add(northPanel, BorderLayout.NORTH);
		card.add(resultPanel, BorderLayout.CENTER);

		JPanel frame = new JPanel(new BorderLayout());
		frame.setBackground(FRAME_GRAY);
		frame.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));
		frame.add(card, BorderLayout.CENTER);
		setContentPane(frame);

		// ===== 이벤트 연결 (화면 부품들이 다 만들어진 뒤에 한다) =====
		salesEvt = new SalesEvt(this);
		searchButton.addActionListener(salesEvt);
		todayButton.addActionListener(salesEvt);
		monthButton.addActionListener(salesEvt);
		adminMainButton.addActionListener(salesEvt);
		startDateField.addMouseListener(salesEvt); // 날짜칸/아이콘 클릭 → 달력
		endDateField.addMouseListener(salesEvt);
		startIconLabel.addMouseListener(salesEvt);
		endIconLabel.addMouseListener(salesEvt);

		setSize(940, 700);
		setLocationRelativeTo(null); // 화면 가운데
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	// ================= 화면 만들기 도우미 =================

	private JTextField makeDateField(String text) {
		JTextField field = new JTextField(text, 8);
		field.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
		field.setEditable(false); // 직접 입력 대신 달력으로만 선택 (형식 오류 방지)
		field.setBackground(Color.WHITE);
		field.setBorder(null);
		field.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return field;
	}

	/** 날짜칸 + 달력 아이콘을 하나의 테두리 상자로 묶는다 */
	private JPanel makeDateBox(JTextField field, JLabel iconLabel) {
		JPanel box = new JPanel(new BorderLayout(6, 0));
		box.setBackground(Color.WHITE);
		box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE_GRAY),
				BorderFactory.createEmptyBorder(4, 8, 4, 6)));
		box.add(field, BorderLayout.CENTER);
		box.add(iconLabel, BorderLayout.EAST);
		return box;
	}

	// ================= Evt가 호출하는 기능 =================

	/** 결제 내역 리스트를 받아 표에 채운다 */
	public void setSalesHistory(List<SalesHistoryDTO> list) {
		tableModel.setRowCount(0); // 기존 행 모두 삭제
		for (SalesHistoryDTO dto : list) {
			tableModel.addRow(new Object[] { dto.getOrderNo(), dto.getPaymentDate(),
					toKoreanMethod(dto.getPaymentMethod()),
					moneyFormat.format(dto.getPaymentAmount()) + "원",
					toKoreanStatus(dto.getPaymentStatus()) });
		}
	}

	/** 최종 매출액을 채우고, 결과 영역을 화면에 보이게 한다 */
	public void setSalesSummary(SalesSummaryDTO summary) {
		finalSalesLabel.setText(moneyFormat.format(summary.getFinalSales()) + "원");
		resultPanel.setVisible(true);
	}

	/** 조회 기간 입력칸에 날짜를 채운다 ([오늘], [이번달] 버튼용) */
	public void setPeriod(String startDate, String endDate) {
		startDateField.setText(startDate);
		endDateField.setText(endDate);
	}

	/** 달력 팝업을 띄워서 고른 날짜를 입력칸에 넣는다 (true: 시작일, false: 종료일) */
	public void pickDate(boolean isStart) {
		JTextField field = isStart ? startDateField : endDateField;

		LocalDate init;
		try {
			init = LocalDate.parse(field.getText().trim()); // 칸의 날짜에서 달력 시작
		} catch (DateTimeParseException e) {
			init = LocalDate.now();
		}

		LocalDate picked = DatePickerDialog.showDialog(this, init);
		if (picked != null) {
			field.setText(picked.toString()); // 2026-11-18 형식
		}
	}

	public String getStartDate() {
		return startDateField.getText().trim();
	}

	public String getEndDate() {
		return endDateField.getText().trim();
	}

	/** 현재 입력된 기간으로 다시 조회 */
	public void refreshSales() {
		salesEvt.searchSales();
	}

	// DB에는 영어로 저장되어 있으므로 화면에는 한글로 바꿔서 보여준다
	private String toKoreanMethod(String method) {
		if ("CARD".equals(method)) {
			return "카드";
		}
		if ("CASH".equals(method)) { // 현금이 CASH로 저장되는지는 3번 조원께 확인
			return "현금";
		}
		return method;
	}

	private String toKoreanStatus(String status) {
		if ("COMPLETED".equals(status)) {
			return "결제완료";
		}
		return status;
	}

	// Evt가 "어느 버튼/칸이 눌렸나" 비교할 수 있도록 내어주는 메서드들
	public JButton getSearchButton() {
		return searchButton;
	}

	public JButton getTodayButton() {
		return todayButton;
	}

	public JButton getMonthButton() {
		return monthButton;
	}

	public JButton getAdminMainButton() {
		return adminMainButton;
	}

	/** 시작일 날짜칸 또는 그 옆 아이콘을 눌렀는가? */
	public boolean isStartTarget(Object src) {
		return src == startDateField || src == startIconLabel;
	}

	/** 종료일 날짜칸 또는 그 옆 아이콘을 눌렀는가? */
	public boolean isEndTarget(Object src) {
		return src == endDateField || src == endIconLabel;
	}

	// ================= 디자인용 작은 클래스 (이 파일 안에 포함) =================

	/** 화면설계서처럼 둥근 회색 버튼 (DatePickerDialog에서도 같이 사용) */
	static class RoundButton extends JButton {

		RoundButton(String text) {
			super(text);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setBorderPainted(false);
			setOpaque(false);
			setRolloverEnabled(true);
			setFont(new Font(FONT_NAME, Font.PLAIN, 13));
			setForeground(Color.BLACK);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setMargin(new Insets(7, 18, 7, 18));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color fill = BTN_GRAY;
			if (getModel().isPressed()) {
				fill = new Color(0xBFBFBF); // 누르는 중
			} else if (getModel().isRollover()) {
				fill = new Color(0xE6E6E6); // 마우스 올림
			}
			g2.setColor(fill);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
			g2.setColor(LINE_GRAY);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
			g2.dispose();
			super.paintComponent(g); // 글자 그리기
		}
	}

	/** 직접 그리는 달력 아이콘 (이미지 파일이 필요 없다) */
	static class CalendarIcon implements Icon {

		@Override
		public int getIconWidth() {
			return 20;
		}

		@Override
		public int getIconHeight() {
			return 20;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(0x595959));
			g2.drawRoundRect(x + 1, y + 3, 17, 15, 3, 3); // 몸통
			g2.fillRect(x + 1, y + 3, 18, 4); // 윗줄
			g2.drawLine(x + 5, y + 1, x + 5, y + 4); // 고리
			g2.drawLine(x + 14, y + 1, x + 14, y + 4);
			for (int r = 0; r < 3; r++) { // 날짜 점들
				for (int col = 0; col < 4; col++) {
					g2.fillRect(x + 4 + col * 4, y + 9 + r * 3, 2, 2);
				}
			}
			g2.dispose();
		}
	}

}
