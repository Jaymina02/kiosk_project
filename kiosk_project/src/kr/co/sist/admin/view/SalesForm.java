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

import kr.co.sist.admin.common.AdminApplication;

import java.awt.Window;

public class SalesForm extends JFrame {

	static final Color FRAME_GRAY = new Color(0xA6A6A6);
	static final Color BTN_GRAY = new Color(0xD9D9D9); 
	static final Color LINE_GRAY = new Color(0x7F7F7F); 
	static final Color DIVIDER = new Color(0xD0D0D0);
	static final Color ROW_DARK = new Color(0xE0E0E0); 
	static final Color ROW_LIGHT = new Color(0xF0F0F0); 
	static final String FONT_NAME = "맑은 고딕";

	private JTable salesTable;
	private DefaultTableModel tableModel;
	private JTextField startDateField, endDateField; 
	private JLabel startIconLabel, endIconLabel;
	private JLabel finalSalesLabel; 
	private JButton searchButton, todayButton, monthButton, adminMainButton;
	private JPanel resultPanel; 
	private SalesEvt salesEvt;
	private AdminApplication app;
	
	private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

	public SalesForm(AdminApplication app) {
	    super("정산 · 매출조회");
	    this.app = app;

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

		JLabel finalTitleLabel = new JLabel("최종 매출액", SwingConstants.CENTER);
		finalTitleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
		finalSalesLabel = new JLabel("0원", SwingConstants.CENTER);
		finalSalesLabel.setFont(new Font(FONT_NAME, Font.BOLD, 32));

		JPanel summaryPanel = new JPanel(new GridLayout(2, 1));
		summaryPanel.setOpaque(false);
		summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		summaryPanel.add(finalTitleLabel);
		summaryPanel.add(finalSalesLabel);

		
		String[] columns = { "주문번호", "결제일시", "결제방식", "결제금액", "상태" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
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
		
		salesTable.getColumnModel().getColumn(0).setPreferredWidth(240);
		salesTable.getColumnModel().getColumn(1).setPreferredWidth(170);
		salesTable.getColumnModel().getColumn(2).setPreferredWidth(110);
		salesTable.getColumnModel().getColumn(3).setPreferredWidth(130); 
		salesTable.getColumnModel().getColumn(4).setPreferredWidth(110); 

		JScrollPane scrollPane = new JScrollPane(salesTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		scrollPane.getViewport().setBackground(Color.WHITE);

		
		resultPanel = new JPanel(new BorderLayout(0, 18));
		resultPanel.setOpaque(false);
		resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		resultPanel.add(summaryPanel, BorderLayout.NORTH);
		resultPanel.add(scrollPane, BorderLayout.CENTER);
		resultPanel.setVisible(false); 

		
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

		
		salesEvt = new SalesEvt(this);
		searchButton.addActionListener(salesEvt);
		todayButton.addActionListener(salesEvt);
		monthButton.addActionListener(salesEvt);
		adminMainButton.addActionListener(salesEvt);
		startDateField.addMouseListener(salesEvt);
		endDateField.addMouseListener(salesEvt);
		startIconLabel.addMouseListener(salesEvt);
		endIconLabel.addMouseListener(salesEvt);

		setSize(940, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		if (app != null) {
			for (Window w : Window.getWindows()) {
				if (w instanceof AdminMainForm && w.isDisplayable()) {
					w.dispose();
				}
			}
		}
		setVisible(true);
	}
	
	
	public SalesForm() {
	    this(null);
	}

		private JTextField makeDateField(String text) {
		JTextField field = new JTextField(text, 8);
		field.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
		field.setEditable(false); 
		field.setBackground(Color.WHITE);
		field.setBorder(null);
		field.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return field;
	}

	
	private JPanel makeDateBox(JTextField field, JLabel iconLabel) {
		JPanel box = new JPanel(new BorderLayout(6, 0));
		box.setBackground(Color.WHITE);
		box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE_GRAY),
				BorderFactory.createEmptyBorder(4, 8, 4, 6)));
		box.add(field, BorderLayout.CENTER);
		box.add(iconLabel, BorderLayout.EAST);
		return box;
	}

	

	
	public void setSalesHistory(List<SalesHistoryDTO> list) {
		tableModel.setRowCount(0); // 기존 행 모두 삭제
		for (SalesHistoryDTO dto : list) {
			tableModel.addRow(new Object[] { dto.getOrderNo(), dto.getPaymentDate(),
					toKoreanMethod(dto.getPaymentMethod()),
					moneyFormat.format(dto.getPaymentAmount()) + "원",
					toKoreanStatus(dto.getPaymentStatus()) });
		}
	}

	
	public void setSalesSummary(SalesSummaryDTO summary) {
		finalSalesLabel.setText(moneyFormat.format(summary.getFinalSales()) + "원");
		resultPanel.setVisible(true);
	}

	
	public void setPeriod(String startDate, String endDate) {
		startDateField.setText(startDate);
		endDateField.setText(endDate);
	}

	
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

	
	public void refreshSales() {
		salesEvt.searchSales();
	}

	
	private String toKoreanMethod(String method) {
		if ("CARD".equals(method)) {
			return "카드";
		}
		if ("CASH".equals(method)) { 
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
	
	public AdminApplication getApp() {
	    return app;
	}

	public boolean isStartTarget(Object src) {
		return src == startDateField || src == startIconLabel;
	}

	
	public boolean isEndTarget(Object src) {
		return src == endDateField || src == endIconLabel;
	}

	
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
				fill = new Color(0xBFBFBF); 
			} else if (getModel().isRollover()) {
				fill = new Color(0xE6E6E6); 
			}
			g2.setColor(fill);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
			g2.setColor(LINE_GRAY);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
			g2.dispose();
			super.paintComponent(g);
		}
	}

	
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
			g2.drawRoundRect(x + 1, y + 3, 17, 15, 3, 3);
			g2.fillRect(x + 1, y + 3, 18, 4); 
			g2.drawLine(x + 5, y + 1, x + 5, y + 4);
			g2.drawLine(x + 14, y + 1, x + 14, y + 4);
			for (int r = 0; r < 3; r++) { 
				for (int col = 0; col < 4; col++) {
					g2.fillRect(x + 4 + col * 4, y + 9 + r * 3, 2, 2);
				}
			}
			g2.dispose();
		}
	}

}
