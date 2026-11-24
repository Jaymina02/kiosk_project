package kr.co.sist.admin.evt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import kr.co.sist.admin.dto.MenuDTO;
import kr.co.sist.admin.dto.MenuOptionDTO;
import kr.co.sist.admin.service.MenuService;
import kr.co.sist.admin.view.AdminMenuForm;
import kr.co.sist.admin.view.MenuEditDialog;
import kr.co.sist.admin.view.MenuImageUtil;

public class AdminMenuEvt implements ActionListener {
	private AdminMenuForm menuForm;
	private MenuService menuService;

	public AdminMenuEvt(AdminMenuForm menuForm) {
		this.menuForm = menuForm;
		this.menuService = new MenuService();
	}

	/** 처음 화면이 뜰 때 카테고리와 메뉴 목록을 DB에서 읽어온다 */
	public void initData() {
		try {
			menuForm.setCategories(menuService.searchCategories());
		} catch (SQLException e) {
			e.printStackTrace();
			menuForm.showMessage("카테고리를 불러오지 못했습니다.\n" + e.getMessage());
		}
		searchAdminMenus();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src instanceof JButton && ((JButton) src).getClientProperty("categoryId") != null) {
			menuForm.selectCategory((JButton) src); // 카테고리 필터 버튼
			searchAdminMenus();
		} else if (src == menuForm.getBackButton()) {
			menuForm.dispose();
		} else if (src == menuForm.getAddButton()) {
			openAddDialog();
		} else if (src == menuForm.getEditButton()) {
			openEditDialog();
		} else if (src == menuForm.getSoldOutButton()) {
			soldOutMenu();
		} else if (src == menuForm.getRestoreButton()) {
			restoreMenu();
		} else if (src == menuForm.getDeleteButton()) {
			removeMenu();
		}
	}

	/** 선택된 카테고리(전체=0)로 메뉴 목록 조회 */
	public void searchAdminMenus() {
		long categoryId = menuForm.getSelectedCategoryId();
		try {
			menuForm.setMenuList(menuService.searchAdminMenus(categoryId));
		} catch (SQLException e) {
			e.printStackTrace();
			menuForm.showMessage("메뉴 목록을 불러오지 못했습니다.\n" + e.getMessage());
		}
	}

	public void openAddDialog() {
		new MenuEditDialog(menuForm, null).setVisible(true);
	}

	public void openEditDialog() {
		long menuId = requireSelection();
		if (menuId < 0) {
			return;
		}
		try {
			MenuDTO dto = menuService.searchMenuDetail(menuId);
			if (dto == null) {
				menuForm.showMessage("메뉴 정보를 찾을 수 없습니다.");
				menuForm.refreshMenuList();
				return;
			}
			new MenuEditDialog(menuForm, dto).setVisible(true);
		} catch (SQLException e) {
			e.printStackTrace();
			menuForm.showMessage("메뉴 정보를 불러오지 못했습니다.\n" + e.getMessage());
		}
	}

	public void soldOutMenu() {
		changeStatus(1, "품절 처리");
	}

	public void restoreMenu() {
		changeStatus(0, "품절 해제");
	}

	private void changeStatus(int status, String label) {
		long menuId = requireSelection();
		if (menuId < 0) {
			return;
		}
		try {
			MenuDTO dto = menuService.searchMenuDetail(menuId);
			if (dto != null && dto.getMenuStatus() == status) {
				menuForm.showMessage("이미 " + AdminMenuForm.statusName(status) + " 상태입니다.");
				return;
			}
			if (menuService.changeMenuStatus(menuId, status)) {
				menuForm.refreshMenuList();
			} else {
				menuForm.showMessage(label + "에 실패했습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			menuForm.showMessage(label + " 중 DB 오류가 발생했습니다.\n" + e.getMessage());
		}
	}

	public void removeMenu() {
		long menuId = requireSelection();
		if (menuId < 0) {
			return;
		}
		int answer = JOptionPane.showConfirmDialog(menuForm, "선택한 메뉴를 삭제할까요?\n목록에서 삭제 처리됩니다.", "메뉴 삭제",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (answer != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			if (menuService.removeMenu(menuId)) {
				menuForm.refreshMenuList();
			} else {
				menuForm.showMessage("삭제할 메뉴를 찾을 수 없습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			menuForm.showMessage("메뉴를 삭제하지 못했습니다.\n" + e.getMessage());
		}
	}

	/** 선택된 메뉴의 MENU_ID, 선택이 없으면 안내 후 -1 */
	private long requireSelection() {
		long menuId = menuForm.getSelectedMenuId();
		if (menuId < 0) {
			menuForm.showMessage("메뉴를 먼저 선택해주세요.");
		}
		return menuId;
	}

	// ------------------------------------------------------------ 상품 상세 (adminMenuDetail)

	private static final Color DETAIL_OUTER = new Color(140, 140, 140);
	private static final Color DETAIL_LINE = new Color(120, 120, 120);
	private static final Color DETAIL_LABEL_BG = new Color(221, 221, 221);

	/** 메뉴명을 클릭하면 열리는 상품 상세 팝업 (기획서 20p) */
	public void openDetailDialog() {
		long menuId = menuForm.getSelectedMenuId();
		if (menuId < 0) {
			return;
		}
		MenuDTO menu;
		List<MenuOptionDTO> options;
		try {
			menu = menuService.searchMenuDetail(menuId);
			options = menuService.searchMenuOptions(menuId);
		} catch (SQLException e) {
			e.printStackTrace();
			menuForm.showMessage("상세 정보를 불러오지 못했습니다.\n" + e.getMessage());
			return;
		}
		if (menu == null) {
			menuForm.showMessage("메뉴 정보를 찾을 수 없습니다.");
			return;
		}

		// 옵션을 온도 / 사이즈 / 추가 옵션으로 묶기
		StringBuilder[] groups = { new StringBuilder(), new StringBuilder(), new StringBuilder() };
		for (MenuOptionDTO o : options) {
			StringBuilder sb = groups[MenuEditDialog.optionGroup(o)];
			if (sb.length() > 0) {
				sb.append(" / ");
			}
			sb.append(o.getOptionName());
			if (o.getExtraPrice() > 0) {
				sb.append(" (+").append(o.getExtraPrice()).append("원)");
			}
		}

		JDialog d = new JDialog(menuForm, "상품 상세", true);

		// ---- 제목 ----
		JLabel title = new JLabel("상품 상세", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
		title.setOpaque(true);
		title.setBackground(DETAIL_LABEL_BG);
		title.setBorder(BorderFactory.createLineBorder(DETAIL_LINE));
		title.setPreferredSize(new Dimension(0, 40));

		// ---- 왼쪽: 상품 이미지 ----
		JLabel image = new JLabel("상품 이미지", SwingConstants.CENTER);
		image.setOpaque(true);
		image.setBackground(new Color(230, 230, 230));
		image.setForeground(Color.GRAY);
		image.setBorder(BorderFactory.createLineBorder(DETAIL_LINE));
		image.setPreferredSize(new Dimension(200, 245));
		ImageIcon icon = loadImage(menu.getImagePath(), 198, 243);
		if (icon != null) {
			image.setIcon(icon);
			image.setText(null);
		}

		// ---- 오른쪽: 이름 / 가격 / 설명 / 카페인 / 원산지 ----
		JLabel name = new JLabel(menu.getMenuName());
		name.setFont(name.getFont().deriveFont(Font.BOLD, 24f));
		JLabel price = new JLabel(String.format("%,d원", menu.getBasePrice()));
		price.setFont(price.getFont().deriveFont(Font.BOLD, 18f));

		JTextArea desc = new JTextArea(menu.getDescription() == null ? "" : menu.getDescription());
		desc.setEditable(false);
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		JScrollPane descScroll = new JScrollPane(desc);
		descScroll.setBorder(BorderFactory.createLineBorder(DETAIL_LINE));
		descScroll.setPreferredSize(new Dimension(0, 88));
		descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

		JPanel right = new JPanel();
		right.setOpaque(false);
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		name.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		price.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		descScroll.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		right.add(name);
		right.add(Box.createVerticalStrut(14));
		right.add(price);
		right.add(Box.createVerticalStrut(14));
		right.add(descScroll);
		right.add(Box.createVerticalStrut(14));
		right.add(infoRow("카페인", "Y".equalsIgnoreCase(menu.getCaffeineYn()) ? "함유" : "없음"));
		right.add(Box.createVerticalStrut(3));
		right.add(infoRow("원산지", menu.getOriginInfo()));

		JPanel top = new JPanel(new BorderLayout(28, 0));
		top.setOpaque(false);
		top.add(image, BorderLayout.WEST);
		top.add(right, BorderLayout.CENTER);

		// ---- 등록 옵션 ----
		JLabel optionTitle = new JLabel("등록 옵션");
		optionTitle.setFont(optionTitle.getFont().deriveFont(Font.BOLD, 14f));
		optionTitle.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		JPanel lower = new JPanel();
		lower.setOpaque(false);
		lower.setLayout(new BoxLayout(lower, BoxLayout.Y_AXIS));
		lower.add(optionTitle);
		lower.add(Box.createVerticalStrut(8));
		lower.add(infoRow("온도", groups[MenuEditDialog.GROUP_TEMPERATURE].toString()));
		lower.add(Box.createVerticalStrut(3));
		lower.add(infoRow("사이즈", groups[MenuEditDialog.GROUP_SIZE].toString()));
		lower.add(Box.createVerticalStrut(3));
		lower.add(infoRow("추가 옵션", groups[MenuEditDialog.GROUP_EXTRA].toString()));

		JPanel center = new JPanel(new BorderLayout(0, 24));
		center.setOpaque(false);
		center.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
		center.add(top, BorderLayout.NORTH);
		center.add(lower, BorderLayout.CENTER);

		// ---- 하단 버튼 ----
		JButton close = detailButton("닫기", false);
		JButton edit = detailButton("수정", true);
		JButton status = detailButton(menu.getMenuStatus() == 1 ? "해제" : "품절", false);
		JButton delete = detailButton("삭제", false);
		close.addActionListener(ev -> d.dispose());
		edit.addActionListener(ev -> {
			d.dispose();
			openEditDialog();
		});
		status.addActionListener(ev -> {
			d.dispose();
			if (menu.getMenuStatus() == 1) {
				restoreMenu();
			} else {
				soldOutMenu();
			}
		});
		delete.addActionListener(ev -> {
			d.dispose();
			removeMenu();
		});

		JPanel leftBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		leftBtn.setOpaque(false);
		leftBtn.setBorder(BorderFactory.createEmptyBorder(0, 82, 0, 0));
		leftBtn.add(close);
		JPanel rightBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
		rightBtn.setOpaque(false);
		rightBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
		rightBtn.add(edit);
		rightBtn.add(status);
		rightBtn.add(delete);
		JPanel buttons = new JPanel(new BorderLayout());
		buttons.setOpaque(false);
		buttons.add(leftBtn, BorderLayout.WEST);
		buttons.add(rightBtn, BorderLayout.EAST);

		// ---- 조립: 회색 바깥 + 흰색 카드 ----
		JPanel card = new JPanel(new BorderLayout(0, 22));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DETAIL_LINE),
				BorderFactory.createEmptyBorder(2, 2, 14, 2)));
		card.add(title, BorderLayout.NORTH);
		card.add(center, BorderLayout.CENTER);
		card.add(buttons, BorderLayout.SOUTH);

		JPanel outer = new JPanel(new BorderLayout());
		outer.setBackground(DETAIL_OUTER);
		outer.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
		outer.add(card, BorderLayout.CENTER);

		d.setContentPane(outer);
		d.setSize(700, 580);
		d.setResizable(false);
		d.setLocationRelativeTo(menuForm);
		d.setVisible(true);
	}

	/** [회색 라벨 | 값] 한 줄 */
	private JPanel infoRow(String label, String value) {
		JLabel l = new JLabel(label, SwingConstants.CENTER);
		l.setOpaque(true);
		l.setBackground(DETAIL_LABEL_BG);
		l.setFont(l.getFont().deriveFont(Font.BOLD));
		l.setBorder(BorderFactory.createLineBorder(DETAIL_LINE));
		l.setPreferredSize(new Dimension(98, 30));

		JLabel v = new JLabel(value == null || value.isEmpty() ? "-" : value);
		v.setOpaque(true);
		v.setBackground(Color.WHITE);
		v.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DETAIL_LINE),
				BorderFactory.createEmptyBorder(0, 8, 0, 8)));

		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.add(l, BorderLayout.WEST);
		row.add(v, BorderLayout.CENTER);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		row.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		return row;
	}

	private JButton detailButton(String text, boolean dark) {
		JButton b = new JButton(text);
		b.setFocusPainted(false);
		b.setContentAreaFilled(false);
		b.setOpaque(true);
		b.setBackground(dark ? new Color(85, 85, 85) : new Color(244, 244, 244));
		b.setForeground(dark ? Color.WHITE : Color.BLACK);
		b.setBorder(BorderFactory.createLineBorder(dark ? new Color(60, 60, 60) : DETAIL_LINE));
		b.setFont(b.getFont().deriveFont(Font.BOLD, 12f));
		b.setPreferredSize(new Dimension(92, 36));
		return b;
	}

	/** 경로로 이미지를 찾아 크기를 맞춰 반환, 없으면 null */
	private ImageIcon loadImage(String path, int w, int h) {
		BufferedImage img = MenuImageUtil.read(path);
		if (img == null) {
			return null;
		}
		return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
}
