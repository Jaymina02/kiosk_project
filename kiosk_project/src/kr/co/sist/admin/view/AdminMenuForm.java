package kr.co.sist.admin.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import kr.co.sist.admin.common.AdminApplication;
import kr.co.sist.admin.dto.CategoryDTO;
import kr.co.sist.admin.dto.MenuDTO;
import kr.co.sist.admin.evt.AdminMenuEvt;

public class AdminMenuForm extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final Color BG = new Color(166, 166, 166);
	private static final Color DARK = new Color(85, 85, 85);
	private static final Color LIGHT = new Color(244, 244, 244);
	private static final Color LINE = new Color(140, 140, 140);
	private static final Color HEADER = new Color(221, 221, 221);
	private static final Color SELECTED = new Color(232, 240, 254);
	private static final Color SOLD_OUT = new Color(176, 58, 80);
	private static final int BTN_W = 54;
	private static final int BTN_H = 34;
	private static final int COL_NAME = 0, COL_EDIT = 4, COL_STATE = 5, COL_DELETE = 6;

	private JTable menuTable;
	private DefaultTableModel tableModel;
	private JPanel categoryPanel;
	private long selectedCategoryId = 0;
	private JButton addButton;
	private JButton editButton;
	private JButton soldOutButton;
	private JButton restoreButton;
	private JButton deleteButton;
	private JButton backButton;
	private JLabel summaryLabel;
	private AdminMenuEvt adminMenuEvt;
	private AdminApplication app; // 뒤로 가기 때 메인으로 돌아가는 데 사용

	private List<MenuDTO> menuList = new ArrayList<>(); // 테이블 행 순서와 동일
	private Map<Long, String> categoryNames = new HashMap<>();
	private Map<String, ImageIcon> thumbCache = new HashMap<>();

	public AdminMenuForm() {
		this(null);
	}

	public AdminMenuForm(AdminApplication app) {
		super("메뉴 관리");
		this.app = app;

		addButton = new JButton("+ 메뉴 추가");
		editButton = new JButton("수정");
		soldOutButton = new JButton("품절");
		restoreButton = new JButton("해제");
		deleteButton = new JButton("삭제");
		backButton = new JButton("뒤로 가기");
		styleButton(addButton, true);
		addButton.setPreferredSize(new Dimension(100, 40));
		for (JButton b : new JButton[] { editButton, soldOutButton, restoreButton, deleteButton }) {
			styleButton(b, false);
			b.setPreferredSize(new Dimension(BTN_W, BTN_H));
		}
		styleButton(backButton, true);
		backButton.setPreferredSize(new Dimension(100, 40));

		// ---- 상단: 제목 ----
		JLabel title = new JLabel("메뉴 관리", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
		title.setOpaque(true);
		title.setBackground(Color.WHITE);
		title.setBorder(BorderFactory.createLineBorder(LINE));
		title.setPreferredSize(new Dimension(0, 52));

		// ---- 카테고리 필터 버튼 + 메뉴 추가 ----
		categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		categoryPanel.setOpaque(false);
		JPanel filterRow = new JPanel(new BorderLayout());
		filterRow.setOpaque(false);
		filterRow.add(categoryPanel, BorderLayout.WEST);
		filterRow.add(addButton, BorderLayout.EAST);

		JPanel north = new JPanel();
		north.setOpaque(false);
		north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		north.add(title);
		north.add(Box.createVerticalStrut(16));
		north.add(filterRow);
		north.add(Box.createVerticalStrut(14));
		title.setAlignmentX(LEFT_ALIGNMENT);
		filterRow.setAlignmentX(LEFT_ALIGNMENT);
		filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

		// ---- 가운데: 메뉴 테이블 ----
		String[] columns = { "이미지 / 메뉴명", "카테고리", "가격", "상태", "수정", "품절/해제", "삭제" };
		tableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		menuTable = new JTable(tableModel);
		menuTable.setRowHeight(68);
		menuTable.setIntercellSpacing(new Dimension(0, 8));
		menuTable.setShowGrid(false);
		menuTable.setBackground(BG);
		menuTable.setFillsViewportHeight(true);
		menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JTableHeader header = menuTable.getTableHeader();
		header.setReorderingAllowed(false);
		header.setResizingAllowed(false);
		header.setDefaultRenderer((t, v, sel, foc, r, c) -> {
			JLabel l = new JLabel(String.valueOf(v), SwingConstants.CENTER);
			l.setOpaque(true);
			l.setBackground(HEADER);
			l.setFont(l.getFont().deriveFont(Font.BOLD));
			l.setBorder(BorderFactory.createLineBorder(LINE));
			return l;
		});
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

		int[] widths = { 260, 100, 90, 80, 70, 70, 70 };
		for (int i = 0; i < widths.length; i++) {
			TableColumn col = menuTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(widths[i]);
			col.setResizable(false);
		}
		menuTable.getColumnModel().getColumn(0).setCellRenderer(new NameRenderer());
		menuTable.getColumnModel().getColumn(1).setCellRenderer(new TextRenderer(false));
		menuTable.getColumnModel().getColumn(2).setCellRenderer(new TextRenderer(false));
		menuTable.getColumnModel().getColumn(3).setCellRenderer(new TextRenderer(true));
		for (int i = COL_EDIT; i <= COL_DELETE; i++) {
			menuTable.getColumnModel().getColumn(i).setCellRenderer(new ButtonCellRenderer(i));
		}

		JScrollPane scroll = new JScrollPane(menuTable);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(BG);

		// ---- 하단: 안내 문구, 요약, 뒤로 가기 ----
		JLabel note = new JLabel("※ 메뉴명을 클릭하면 상품 상세 조회");
		summaryLabel = new JLabel(" ");
		summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.BOLD));
		JPanel infoRow = new JPanel(new BorderLayout());
		infoRow.setOpaque(false);
		infoRow.add(note, BorderLayout.WEST);
		infoRow.add(summaryLabel, BorderLayout.EAST);

		JPanel backRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		backRow.setOpaque(false);
		backRow.add(backButton);

		JPanel south = new JPanel(new BorderLayout(0, 12));
		south.setOpaque(false);
		south.setBorder(BorderFactory.createEmptyBorder(14, 8, 0, 0));
		south.add(infoRow, BorderLayout.NORTH);
		south.add(backRow, BorderLayout.SOUTH);

		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(BG);
		content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		content.add(north, BorderLayout.NORTH);
		content.add(scroll, BorderLayout.CENTER);
		content.add(south, BorderLayout.SOUTH);
		setContentPane(content);

		// ---- 이벤트 연결 (초기 데이터 로딩 후 리스너 연결) ----
		adminMenuEvt = new AdminMenuEvt(this);
		adminMenuEvt.initData();

		addButton.addActionListener(adminMenuEvt);
		editButton.addActionListener(adminMenuEvt);
		soldOutButton.addActionListener(adminMenuEvt);
		restoreButton.addActionListener(adminMenuEvt);
		deleteButton.addActionListener(adminMenuEvt);
		backButton.addActionListener(adminMenuEvt);

		menuTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleTableClick(e);
			}
		});

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if (app != null) {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) { // 뒤로 가기
					AdminMenuForm.this.app.openMain(); // 메인 화면으로 돌아간다
				}
			});
		}
		setSize(790, 700);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void handleTableClick(MouseEvent e) {
		int row = menuTable.rowAtPoint(e.getPoint());
		int col = menuTable.columnAtPoint(e.getPoint());
		if (row < 0 || col < 0 || row >= menuList.size()) {
			return;
		}
		menuTable.setRowSelectionInterval(row, row);

		if (col == COL_NAME) { // 메뉴명 클릭 -> 상품 상세
			adminMenuEvt.openDetailDialog();
			return;
		}
		if (col < COL_EDIT) {
			return;
		}

		Rectangle cell = menuTable.getCellRect(row, col, false);
		Rectangle btn = new Rectangle(cell.x + (cell.width - BTN_W) / 2, cell.y + (cell.height - BTN_H) / 2, BTN_W,
				BTN_H);
		if (!btn.contains(e.getPoint())) {
			return;
		}
		if (col == COL_EDIT) {
			editButton.doClick(0);
		} else if (col == COL_STATE) {
			(menuList.get(row).getMenuStatus() == 1 ? restoreButton : soldOutButton).doClick(0);
		} else {
			deleteButton.doClick(0);
		}
	}

	// 데이터 표시

	/** 카테고리 필터 버튼 만들기 (맨 앞에 '전체') */
	public void setCategories(List<CategoryDTO> list) {
		categoryPanel.removeAll();
		categoryNames.clear();
		selectedCategoryId = 0;
		addCategoryButton(0, "전체");
		for (CategoryDTO c : list) {
			categoryNames.put(c.getCategoryId(), c.getCategoryName());
			addCategoryButton(c.getCategoryId(), c.getCategoryName());
		}
		refreshCategoryStyles();
		categoryPanel.revalidate();
		categoryPanel.repaint();
	}

	private void addCategoryButton(long id, String name) {
		JButton b = new JButton(name);
		b.putClientProperty("categoryId", Long.valueOf(id));
		b.setPreferredSize(new Dimension(82, 40));
		b.addActionListener(adminMenuEvt);
		categoryPanel.add(b);
	}

	public void selectCategory(JButton button) {
		selectedCategoryId = (Long) button.getClientProperty("categoryId");
		refreshCategoryStyles();
	}

	private void refreshCategoryStyles() {
		for (Component c : categoryPanel.getComponents()) {
			JButton b = (JButton) c;
			styleButton(b, ((Long) b.getClientProperty("categoryId")) == selectedCategoryId);
		}
	}

	public long getSelectedCategoryId() {
		return selectedCategoryId;
	}

	public void setMenuList(List<MenuDTO> list) {
		menuList = new ArrayList<>(list);
		tableModel.setRowCount(0);
		int soldOut = 0;
		for (MenuDTO m : menuList) {
			String category = categoryNames.get(m.getCategoryId());
			tableModel.addRow(new Object[] { m, category == null ? "" : category,
					String.format("%,d원", m.getBasePrice()), statusName(m.getMenuStatus()), m, m, m });
			if (m.getMenuStatus() == 1) {
				soldOut++;
			}
		}
		summaryLabel.setText("등록 메뉴 " + menuList.size() + "개  |  품절 " + soldOut + "개");
	}

	/* 선택된 행의 MENU_ID, 선택이 없으면 -1 */
	public long getSelectedMenuId() {
		int row = menuTable.getSelectedRow();
		if (row < 0 || row >= menuList.size()) {
			return -1;
		}
		return menuList.get(row).getMenuId();
	}

	public void refreshMenuList() {
		adminMenuEvt.searchAdminMenus();
	}

	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	public static String statusName(int status) {
		return status == 2 ? "삭제" : status == 1 ? "품절" : "판매중";
	}

	public JTable getMenuTable() {
		return menuTable;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public JButton getEditButton() {
		return editButton;
	}

	public JButton getSoldOutButton() {
		return soldOutButton;
	}

	public JButton getRestoreButton() {
		return restoreButton;
	}

	public JButton getDeleteButton() {
		return deleteButton;
	}

	public JButton getBackButton() {
		return backButton;
	}

	// 모양 관련

	private static void styleButton(JButton b, boolean dark) {
		b.setFocusPainted(false);
		b.setContentAreaFilled(false);
		b.setOpaque(true);
		b.setBackground(dark ? DARK : LIGHT);
		b.setForeground(dark ? Color.WHITE : Color.BLACK);
		b.setBorder(BorderFactory.createLineBorder(dark ? new Color(60, 60, 60) : LINE));
		b.setFont(b.getFont().deriveFont(Font.BOLD, 12f));
	}

	private static Border rowBorder(int col) {
		return BorderFactory.createMatteBorder(1, col == 0 ? 1 : 0, 1, col == COL_DELETE ? 1 : 0, LINE);
	}

	/* 썸네일(없으면 null) */
	private ImageIcon loadThumb(MenuDTO m) {
		String path = m.getThumbnailPath();
		if (path == null || path.isEmpty()) {
			path = m.getImagePath();
		}
		if (path == null || path.isEmpty()) {
			return null;
		}
		if (thumbCache.containsKey(path)) {
			return thumbCache.get(path);
		}
		ImageIcon icon = null;
		BufferedImage img = MenuImageUtil.read(path);
		if (img != null) {
			icon = new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
		}
		thumbCache.put(path, icon);
		return icon;
	}

	/* 이미지 / 메뉴명 칸 */
	private class NameRenderer implements TableCellRenderer {
		private final JPanel panel = new JPanel(new BorderLayout(12, 0));
		private final JLabel thumb = new JLabel("이미지", SwingConstants.CENTER);
		private final JLabel name = new JLabel();

		NameRenderer() {
			thumb.setPreferredSize(new Dimension(50, 50));
			thumb.setOpaque(true);
			thumb.setBackground(new Color(236, 236, 236));
			thumb.setForeground(Color.GRAY);
			thumb.setFont(thumb.getFont().deriveFont(11f));
			thumb.setBorder(BorderFactory.createLineBorder(LINE));
			name.setFont(name.getFont().deriveFont(Font.BOLD));
			JPanel thumbWrap = new JPanel(new GridBagLayout());
			thumbWrap.setOpaque(false);
			thumbWrap.add(thumb);
			panel.add(thumbWrap, BorderLayout.WEST);
			panel.add(name, BorderLayout.CENTER);
			panel.setBorder(
					BorderFactory.createCompoundBorder(rowBorder(0), BorderFactory.createEmptyBorder(0, 8, 0, 0)));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			MenuDTO m = (MenuDTO) value;
			ImageIcon icon = loadThumb(m);
			thumb.setIcon(icon);
			thumb.setText(icon == null ? "이미지" : null);
			name.setText(m.getMenuName());
			panel.setBackground(isSelected ? SELECTED : Color.WHITE);
			return panel;
		}
	}

	/* 카테고리 / 가격 / 상태 칸 */
	private static class TextRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final boolean statusColumn;

		TextRenderer(boolean statusColumn) {
			this.statusColumn = statusColumn;
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, false, false, row, column);
			setOpaque(true);
			setBackground(isSelected ? SELECTED : Color.WHITE);
			setBorder(rowBorder(column));
			setForeground(statusColumn && "품절".equals(value) ? SOLD_OUT : Color.BLACK);
			setFont(getFont().deriveFont(statusColumn ? Font.BOLD : Font.PLAIN));
			return this;
		}
	}

	/* 수정 / 품절·해제 / 삭제 버튼 칸 */
	private class ButtonCellRenderer implements TableCellRenderer {
		private final JPanel panel = new JPanel(new GridBagLayout());
		private final int col;

		ButtonCellRenderer(int col) {
			this.col = col;
			if (col == COL_EDIT) {
				panel.add(editButton);
			} else if (col == COL_STATE) {
				panel.add(soldOutButton);
				panel.add(restoreButton);
			} else {
				panel.add(deleteButton);
			}
			panel.setBorder(rowBorder(col));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (col == COL_STATE) {
				boolean soldOut = ((MenuDTO) value).getMenuStatus() == 1;
				soldOutButton.setVisible(!soldOut); // 판매중이면 '품절', 품절이면 '해제'
				restoreButton.setVisible(soldOut);
			}
			panel.setBackground(isSelected ? SELECTED : Color.WHITE);
			return panel;
		}
	}
}
