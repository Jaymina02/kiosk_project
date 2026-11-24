package kr.co.sist.admin.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import kr.co.sist.admin.dto.CategoryDTO;
import kr.co.sist.admin.dto.MenuDTO;
import kr.co.sist.admin.dto.MenuOptionDTO;
import kr.co.sist.admin.evt.MenuEditEvt;

/** 메뉴 추가 / 수정 팝업. 생성자에 MenuDTO 를 넘기면 수정, null 이면 추가 */
public class MenuEditDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public static final int GROUP_TEMPERATURE = 0;
	public static final int GROUP_SIZE = 1;
	public static final int GROUP_EXTRA = 2;

	/** 카페인과 옵션(온도/사이즈/추가 옵션)을 아예 쓰지 않는 카테고리 ID (4 = 디저트) */
	private static final long[] NO_OPTION_CATEGORY_IDS = { 4 };
	/** 온도에서 ICE 만 쓰는 카테고리 ID (3 = 스무디) */
	private static final long[] ICE_ONLY_CATEGORY_IDS = { 3 };

	private static final Color OUTER = new Color(140, 140, 140);
	private static final Color LINE = new Color(120, 120, 120);
	private static final Color TITLE_BG = new Color(221, 221, 221);
	private static final Color DARK = new Color(85, 85, 85);
	private static final Color LIGHT = new Color(244, 244, 244);

	private JTextField menuNameField;
	private JTextField priceField;
	private JComboBox<CategoryDTO> categoryCombo;
	private JTextArea descriptionArea;
	private JTextField originField;
	private JCheckBox caffeineCheck;
	private JTextField imagePathField; // 화면에는 안 보이고 DB에 저장할 경로를 들고 있다
	private JTextField thumbnailPathField;
	private JButton saveButton;
	private JButton cancelButton;
	private JButton imageButton;
	private JLabel imagePreview;
	private MenuDTO menuDTO; // null 이면 추가 모드
	private MenuEditEvt menuEditEvt;
	private JPanel temperatureChoices;
	private JPanel sizeChoices;
	private JPanel extraChoices;
	// 옵션 줄과 그 위의 여백 (디저트면 모두 숨김)
	private Component caffeineGap, optionGap, sizeGap, extraGap;
	private JPanel caffeineRow, temperatureRow, sizeRow, extraRow;
	private List<Long> hotOptionIds = new ArrayList<>(); // HOT 옵션 (스무디면 숨김)
	private List<Long> selectedOptionIds = new ArrayList<>();

	private AdminMenuForm ownerForm;
	private Map<Long, JCheckBox> optionChecks = new LinkedHashMap<>();
	private List<Long> sizeOptionIds = new ArrayList<>();
	private File pickedImage; // 이미지 등록 버튼으로 고른 파일 (저장할 때 images 폴더로 복사)

	public MenuEditDialog(AdminMenuForm owner, MenuDTO dto) {
		super(owner, dto == null ? "메뉴 추가" : "메뉴 수정", true);
		this.ownerForm = owner;
		this.menuDTO = dto;

		menuNameField = textField();
		priceField = textField();
		categoryCombo = new JComboBox<>();
		categoryCombo.setPreferredSize(new Dimension(0, 32));
		descriptionArea = new JTextArea(2, 20);
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		originField = textField();
		caffeineCheck = new JCheckBox("카페인 함유");
		caffeineCheck.setOpaque(false);
		imagePathField = new JTextField();
		thumbnailPathField = new JTextField();

		imageButton = new JButton("이미지 등록");
		styleButton(imageButton, false);
		imageButton.setPreferredSize(new Dimension(100, 34));
		cancelButton = new JButton("취소");
		styleButton(cancelButton, false);
		cancelButton.setPreferredSize(new Dimension(105, 40));
		saveButton = new JButton("저장");
		styleButton(saveButton, true);
		saveButton.setPreferredSize(new Dimension(105, 40));

		imagePreview = new JLabel("상품 이미지", SwingConstants.CENTER);
		imagePreview.setOpaque(true);
		imagePreview.setBackground(new Color(230, 230, 230));
		imagePreview.setForeground(Color.GRAY);
		imagePreview.setBorder(BorderFactory.createLineBorder(LINE));
		imagePreview.setPreferredSize(new Dimension(125, 105));

		temperatureChoices = choicesBox();
		sizeChoices = choicesBox();
		extraChoices = new JPanel(new GridLayout(0, 3, 8, 6));
		extraChoices.setOpaque(false);

		// ---- 제목 ----
		JLabel title = new JLabel(dto == null ? "메뉴 추가" : "메뉴 수정", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
		title.setOpaque(true);
		title.setBackground(TITLE_BG);
		title.setBorder(BorderFactory.createLineBorder(LINE));
		title.setPreferredSize(new Dimension(0, 40));

		// ---- 맨 위: 이미지 / 이미지 등록 / 메뉴명 / 가격 ----
		JPanel top = new JPanel(new GridBagLayout());
		top.setOpaque(false);
		top.add(imagePreview, gbc(0, 0, 2, 0, GridBagConstraints.NONE, new Insets(0, 0, 0, 16)));
		top.add(imageButton, gbc(1, 0, 2, 0, GridBagConstraints.NONE, new Insets(0, 0, 0, 28)));
		top.add(boldLabel("메뉴명 *"), gbc(2, 0, 1, 0, GridBagConstraints.NONE, new Insets(0, 0, 10, 14)));
		top.add(menuNameField, gbc(3, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0)));
		top.add(boldLabel("가격 *"), gbc(2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0, 0, 0, 14)));
		top.add(priceField, gbc(3, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0)));

		JScrollPane descScroll = new JScrollPane(descriptionArea);
		descScroll.setBorder(BorderFactory.createLineBorder(LINE));
		descScroll.setPreferredSize(new Dimension(0, 58));

		JPanel caffeineBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
		caffeineBox.setBackground(Color.WHITE);
		caffeineBox.setBorder(BorderFactory.createLineBorder(LINE));
		caffeineBox.add(caffeineCheck);

		// ---- 본문 ----
		JPanel body = new JPanel();
		body.setOpaque(false);
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder(20, 24, 6, 24));
		top.setAlignmentX(LEFT_ALIGNMENT);
		body.add(top);
		body.add(Box.createVerticalStrut(18));
		body.add(formRow("카테고리 *", categoryCombo));
		body.add(Box.createVerticalStrut(6));
		body.add(formRow("상세 설명", descScroll));
		caffeineGap = Box.createVerticalStrut(6);
		caffeineRow = formRow("카페인", caffeineBox);
		body.add(caffeineGap);
		body.add(caffeineRow);
		body.add(Box.createVerticalStrut(6));
		body.add(formRow("원산지", originField));
		optionGap = Box.createVerticalStrut(18);
		temperatureRow = formRow("온도", temperatureChoices);
		sizeGap = Box.createVerticalStrut(6);
		sizeRow = formRow("사이즈 *", sizeChoices);
		extraGap = Box.createVerticalStrut(6);
		extraRow = formRow("추가 옵션", extraChoices);
		body.add(optionGap);
		body.add(temperatureRow);
		body.add(sizeGap);
		body.add(sizeRow);
		body.add(extraGap);
		body.add(extraRow);

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		btnPanel.setOpaque(false);
		btnPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 14));
		btnPanel.add(cancelButton);
		btnPanel.add(saveButton);

		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),
				BorderFactory.createEmptyBorder(2, 2, 14, 2)));
		card.add(title, BorderLayout.NORTH);
		JPanel bodyWrap = new JPanel(new BorderLayout()); // 줄이 숨겨져도 남은 줄의 간격이 늘어나지 않게 위쪽에 붙인다
		bodyWrap.setOpaque(false);
		bodyWrap.add(body, BorderLayout.NORTH);
		card.add(bodyWrap, BorderLayout.CENTER);
		card.add(btnPanel, BorderLayout.SOUTH);

		JPanel outer = new JPanel(new BorderLayout());
		outer.setBackground(OUTER);
		outer.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
		outer.add(card, BorderLayout.CENTER);
		setContentPane(outer);

		// 이벤트 처리 객체 생성 후 DB 데이터 로딩
		menuEditEvt = new MenuEditEvt(this);
		menuEditEvt.initData();
		saveButton.addActionListener(menuEditEvt);
		cancelButton.addActionListener(menuEditEvt);
		imageButton.addActionListener(menuEditEvt);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack(); // 모든 줄이 보이는 상태 기준으로 창 크기를 고정한다 (카테고리가 바뀌어도 크기는 그대로)
		setSize(Math.max(getWidth(), 640), getHeight());
		setResizable(false);
		setLocationRelativeTo(owner);

		updateOptionVisibility(); // 처음 선택된 카테고리 기준
		categoryCombo.addActionListener(ev -> updateOptionVisibility()); // 카테고리를 바꾸면 줄을 보이고 숨김
	}

	// ---------------------------------------------------------------- 모양 도우미

	private JTextField textField() {
		JTextField f = new JTextField();
		f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),
				BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		f.setPreferredSize(new Dimension(0, 32));
		return f;
	}

	private JPanel choicesBox() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
		p.setBackground(Color.WHITE);
		p.setBorder(BorderFactory.createLineBorder(LINE));
		return p;
	}

	private JLabel boldLabel(String text) {
		JLabel l = new JLabel(text);
		l.setFont(l.getFont().deriveFont(Font.BOLD));
		return l;
	}

	private JPanel formRow(String label, JComponent comp) {
		JLabel l = boldLabel(label);
		l.setPreferredSize(new Dimension(86, 30));
		l.setVerticalAlignment(SwingConstants.TOP);
		l.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		JPanel row = new JPanel(new BorderLayout(8, 0));
		row.setOpaque(false);
		row.add(l, BorderLayout.WEST);
		row.add(comp, BorderLayout.CENTER);
		row.setAlignmentX(LEFT_ALIGNMENT);
		return row;
	}

	private GridBagConstraints gbc(int x, int y, int height, double weightx, int fill, Insets insets) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = y;
		g.gridheight = height;
		g.weightx = weightx;
		g.fill = fill;
		g.insets = insets;
		return g;
	}

	private static void styleButton(JButton b, boolean dark) {
		b.setFocusPainted(false);
		b.setContentAreaFilled(false);
		b.setOpaque(true);
		b.setBackground(dark ? DARK : LIGHT);
		b.setForeground(dark ? Color.WHITE : Color.BLACK);
		b.setBorder(BorderFactory.createLineBorder(dark ? new Color(60, 60, 60) : LINE));
		b.setFont(b.getFont().deriveFont(Font.BOLD, 12f));
	}

	// ---------------------------------------------------------------- 데이터 세팅

	public void setCategories(List<CategoryDTO> list) {
		categoryCombo.removeAllItems();
		for (CategoryDTO c : list) {
			categoryCombo.addItem(c);
		}
	}

	/** DB의 옵션 목록으로 체크박스를 만든다 (이름으로 온도/사이즈/추가 옵션 분류) */
	public void setOptionChoices(List<MenuOptionDTO> options) {
		temperatureChoices.removeAll();
		sizeChoices.removeAll();
		extraChoices.removeAll();
		optionChecks.clear();
		sizeOptionIds.clear();
		hotOptionIds.clear();

		for (MenuOptionDTO o : options) {
			String text = o.getOptionName();
			if (o.getExtraPrice() > 0) {
				text += " (+" + o.getExtraPrice() + "원)";
			}
			JCheckBox cb = new JCheckBox(text);
			optionChecks.put(o.getOptionId(), cb);
			switch (optionGroup(o)) {
			case GROUP_TEMPERATURE:
				cb.setOpaque(false);
				temperatureChoices.add(cb);
				if ("HOT".equalsIgnoreCase(o.getOptionName() == null ? "" : o.getOptionName().trim())) {
					hotOptionIds.add(o.getOptionId());
				}
				break;
			case GROUP_SIZE:
				cb.setOpaque(false);
				sizeChoices.add(cb);
				sizeOptionIds.add(o.getOptionId());
				break;
			default:
				cb.setBackground(Color.WHITE);
				cb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),
						BorderFactory.createEmptyBorder(5, 6, 5, 6)));
				extraChoices.add(cb);
			}
		}
		for (JPanel p : new JPanel[] { temperatureChoices, sizeChoices, extraChoices }) {
			if (p.getComponentCount() == 0) {
				p.add(new JLabel("(등록된 옵션 없음)"));
			}
		}
	}

	/** 옵션 이름으로 그룹 분류 */
	public static int optionGroup(MenuOptionDTO o) {
		String n = o.getOptionName() == null ? "" : o.getOptionName().trim().toUpperCase();
		if (n.equals("HOT") || n.equals("ICE") || n.contains("온도")) {
			return GROUP_TEMPERATURE;
		}
		if (n.equals("REGULAR") || n.equals("LARGE") || n.equals("SMALL") || n.equals("MEDIUM")
				|| n.contains("사이즈")) {
			return GROUP_SIZE;
		}
		return GROUP_EXTRA;
	}

	public void setSelectedOptionIds(List<Long> ids) {
		for (Map.Entry<Long, JCheckBox> e : optionChecks.entrySet()) {
			e.getValue().setSelected(ids.contains(e.getKey()));
		}
	}

	public void setMenuData(MenuDTO dto) {
		this.menuDTO = dto;
		menuNameField.setText(dto.getMenuName());
		priceField.setText(String.valueOf(dto.getBasePrice()));
		descriptionArea.setText(dto.getDescription());
		originField.setText(dto.getOriginInfo());
		caffeineCheck.setSelected("Y".equalsIgnoreCase(dto.getCaffeineYn()));
		imagePathField.setText(dto.getImagePath() == null ? "" : dto.getImagePath());
		thumbnailPathField.setText(dto.getThumbnailPath() == null ? "" : dto.getThumbnailPath());
		for (int i = 0; i < categoryCombo.getItemCount(); i++) {
			if (categoryCombo.getItemAt(i).getCategoryId() == dto.getCategoryId()) {
				categoryCombo.setSelectedIndex(i);
				break;
			}
		}
		showExistingImage(dto.getImagePath());
	}

	/** 이미 등록된 이미지가 있으면 미리보기에 보여준다 */
	private void showExistingImage(String path) {
		BufferedImage img = MenuImageUtil.read(path);
		if (img != null) {
			showPreview(img);
		}
	}

	/** 이미지 등록 버튼으로 고른 파일을 미리보기에 보여준다. 읽을 수 없는 파일이면 false */
	public boolean setPickedImage(File file) {
		if (!showPreview(file)) {
			return false;
		}
		pickedImage = file;
		return true;
	}

	private boolean showPreview(File file) {
		try {
			return showPreview(ImageIO.read(file));
		} catch (IOException e) {
			return false;
		}
	}

	private boolean showPreview(BufferedImage img) {
		if (img == null) {
			return false;
		}
		int boxW = 123, boxH = 103;
		double scale = Math.min((double) boxW / img.getWidth(), (double) boxH / img.getHeight());
		int w = Math.max(1, (int) (img.getWidth() * scale));
		int h = Math.max(1, (int) (img.getHeight() * scale));
		imagePreview.setIcon(new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
		imagePreview.setText(null);
		return true;
	}

	/** 입력값으로 MenuDTO 생성 (가격 검증은 MenuEditEvt.validateMenu 에서 먼저 한다) */
	public MenuDTO getMenuData() {
		MenuDTO dto = new MenuDTO();
		if (menuDTO != null) {
			dto.setMenuId(menuDTO.getMenuId());
			dto.setMenuStatus(menuDTO.getMenuStatus());
			dto.setCreatedDate(menuDTO.getCreatedDate());
		}
		CategoryDTO c = (CategoryDTO) categoryCombo.getSelectedItem();
		dto.setCategoryId(c == null ? 0 : c.getCategoryId());
		dto.setMenuName(menuNameField.getText().trim());
		dto.setBasePrice(Integer.parseInt(priceField.getText().trim()));
		dto.setDescription(descriptionArea.getText().trim());
		dto.setOriginInfo(originField.getText().trim());
		dto.setCaffeineYn(!isOptionHidden() && caffeineCheck.isSelected() ? "Y" : "N"); // 디저트는 카페인 없음
		dto.setImagePath(imagePathField.getText().trim());
		dto.setThumbnailPath(thumbnailPathField.getText().trim());
		return dto;
	}

	private long selectedCategoryId() {
		CategoryDTO c = (CategoryDTO) categoryCombo.getSelectedItem();
		return c == null ? -1 : c.getCategoryId();
	}

	private static boolean contains(long[] ids, long id) {
		for (long v : ids) {
			if (v == id) {
				return true;
			}
		}
		return false;
	}

	/** 선택된 카테고리가 옵션을 쓰지 않는 카테고리(디저트)인지 */
	private boolean isOptionHidden() {
		return contains(NO_OPTION_CATEGORY_IDS, selectedCategoryId());
	}

	/** 선택된 카테고리가 온도에서 HOT 을 쓰지 않는 카테고리(스무디)인지 */
	private boolean isHotHidden() {
		return contains(ICE_ONLY_CATEGORY_IDS, selectedCategoryId());
	}

	/** 카테고리에 맞게 옵션 줄(온도/사이즈/추가 옵션)과 HOT 체크박스를 보이고 숨긴다 */
	private void updateOptionVisibility() {
		boolean showOptions = !isOptionHidden();
		for (Component c : new Component[] { caffeineGap, caffeineRow, optionGap, temperatureRow, sizeGap, sizeRow,
				extraGap, extraRow }) {
			c.setVisible(showOptions);
		}
		boolean showHot = !isHotHidden();
		for (Long id : hotOptionIds) {
			optionChecks.get(id).setVisible(showHot);
		}
	}

	public List<Long> getSelectedOptionIds() {
		selectedOptionIds = new ArrayList<>();
		if (isOptionHidden()) { // 디저트는 옵션을 저장하지 않는다
			return selectedOptionIds;
		}
		boolean skipHot = isHotHidden(); // 스무디는 HOT 을 저장하지 않는다
		for (Map.Entry<Long, JCheckBox> e : optionChecks.entrySet()) {
			if (skipHot && hotOptionIds.contains(e.getKey())) {
				continue;
			}
			if (e.getValue().isSelected()) {
				selectedOptionIds.add(e.getKey());
			}
		}
		return selectedOptionIds;
	}

	/** 사이즈 옵션이 등록되어 있다면 하나 이상 선택되었는지 (사이즈는 필수, 디저트는 제외) */
	public boolean isSizeSelected() {
		if (isOptionHidden() || sizeOptionIds.isEmpty()) {
			return true;
		}
		for (Long id : sizeOptionIds) {
			if (optionChecks.get(id).isSelected()) {
				return true;
			}
		}
		return false;
	}

	public void clearFields() {
		menuNameField.setText("");
		priceField.setText("");
		descriptionArea.setText("");
		originField.setText("");
		caffeineCheck.setSelected(false);
		imagePathField.setText("");
		thumbnailPathField.setText("");
		pickedImage = null;
		imagePreview.setIcon(null);
		imagePreview.setText("상품 이미지");
		if (categoryCombo.getItemCount() > 0) {
			categoryCombo.setSelectedIndex(0);
		}
		for (JCheckBox cb : optionChecks.values()) {
			cb.setSelected(false);
		}
	}

	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	public boolean isEditMode() { return menuDTO != null; }
	public MenuDTO getMenuDTO() { return menuDTO; }
	public AdminMenuForm getOwnerForm() { return ownerForm; }
	public File getPickedImage() { return pickedImage; }
	public JTextField getMenuNameField() { return menuNameField; }
	public JTextField getPriceField() { return priceField; }
	public JTextField getImagePathField() { return imagePathField; }
	public JTextField getThumbnailPathField() { return thumbnailPathField; }
	public JButton getSaveButton() { return saveButton; }
	public JButton getCancelButton() { return cancelButton; }
	public JButton getImageButton() { return imageButton; }
}
