
package kr.co.sist.user.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.evt.MenuDetailEvt;
import kr.co.sist.user.service.MenuService;

/**
 * 사용자 메뉴 상세 팝업
 * 
 * 최종 화면설계서에 맞춘 UI
 * 기존 DB 조회 및 옵션 선택 기능 유지
 */
public class MenuDetailDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // 2번 담당자 옵션 선택 화면 연결
    private static Consumer<MenuDTO> optionHandler;

    // 메뉴 상세 조회 서비스
    private final MenuService service = new MenuService();

    // 현재 선택한 메뉴
    private MenuDTO selectedMenu;

    // 기존 이벤트 클래스와 연결할 버튼
    private final JButton optionButton =
            new JButton("옵션 선택");

    private final JButton closeButton =
            new JButton("닫기");

    // 창 내부 화면 크기
    private static final int CONTENT_WIDTH = 650;
    private static final int CONTENT_HEIGHT = 490;

    // 색상
    private static final Color WHITE = Color.WHITE;
    private static final Color HEADER_COLOR =
            new Color(220, 220, 220);
    private static final Color BORDER_COLOR =
            new Color(190, 190, 190);
    private static final Color LABEL_COLOR =
            new Color(242, 242, 242);
    private static final Color CLOSE_COLOR =
            new Color(220, 220, 220);
    private static final Color OPTION_COLOR =
            new Color(95, 95, 95);

    // 글꼴
    private static final Font TITLE_FONT =
            new Font("맑은 고딕", Font.BOLD, 15);

    private static final Font NAME_FONT =
            new Font("맑은 고딕", Font.BOLD, 21);

    private static final Font PRICE_FONT =
            new Font("맑은 고딕", Font.BOLD, 19);

    private static final Font TEXT_FONT =
            new Font("맑은 고딕", Font.PLAIN, 13);

    private static final Font SMALL_FONT =
            new Font("맑은 고딕", Font.PLAIN, 11);

    /**
     * 메뉴 ID만 전달하는 생성자
     */
    public MenuDetailDialog(long menuId) {
        this((Frame) null, menuId);
    }

    /**
     * 부모 화면 및 메뉴 ID를 전달하는 생성자
     */
    public MenuDetailDialog(Frame owner, long menuId) {

        super(owner, "상품 상세", true);

        // =====================================
        // 1. 메뉴 상세 정보 조회
        // =====================================

        selectedMenu = service.searchMenuDetail(menuId);

        if (selectedMenu == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 메뉴 ID: " + menuId
            );
        }

        // =====================================
        // 2. 다이얼로그 기본 설정
        // =====================================

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel content = new JPanel(null);
        content.setBackground(WHITE);

        content.setPreferredSize(
                new Dimension(
                        CONTENT_WIDTH,
                        CONTENT_HEIGHT
                )
        );

        setContentPane(content);

        // =====================================
        // 3. 상단 제목 표시줄
        // =====================================

        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 650, 38);
        header.setBackground(HEADER_COLOR);

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, Color.GRAY
                )
        );

        content.add(header);

        JLabel titleLabel = new JLabel(
                "상품 상세",
                SwingConstants.CENTER
        );

        titleLabel.setBounds(0, 0, 650, 37);
        titleLabel.setFont(TITLE_FONT);

        header.add(titleLabel);

        // =====================================
        // 4. 상품 이미지
        // =====================================

        JLabel imageLabel = new JLabel(
                "상품 이미지",
                SwingConstants.CENTER
        );

        imageLabel.setBounds(16, 61, 253, 274);
        imageLabel.setOpaque(true);

        imageLabel.setBackground(
                new Color(232, 232, 232)
        );

        imageLabel.setForeground(Color.GRAY);
        imageLabel.setFont(TEXT_FONT);

        imageLabel.setBorder(
                new LineBorder(BORDER_COLOR, 1)
        );

        // 기존 MenuForm 이미지 로딩 방식 유지
        ImageIcon icon = MenuForm.loadScaledIcon(
                selectedMenu.getDetailImagePath(),
                253,
                274
        );

        if (icon != null) {
            imageLabel.setIcon(icon);
            imageLabel.setText("");
        }

        content.add(imageLabel);

        // =====================================
        // 5. 상품명
        // =====================================

        JLabel nameLabel = new JLabel(
                safe(selectedMenu.getMenuName())
        );

        nameLabel.setBounds(299, 59, 320, 35);
        nameLabel.setFont(NAME_FONT);

        content.add(nameLabel);

        // =====================================
        // 6. 상품 가격
        // =====================================

        String priceText = NumberFormat
                .getIntegerInstance(Locale.KOREA)
                .format(selectedMenu.getBasePrice())
                + "원";

        JLabel priceLabel = new JLabel(priceText);

        priceLabel.setBounds(299, 119, 320, 30);
        priceLabel.setFont(PRICE_FONT);

        content.add(priceLabel);

        // =====================================
        // 7. 상품 설명
        // =====================================

        JTextArea descriptionArea = new JTextArea(
                safe(selectedMenu.getDescription())
        );

        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(TEXT_FONT);
        descriptionArea.setBackground(WHITE);

        descriptionArea.setMargin(
                new Insets(5, 5, 5, 5)
        );

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        descriptionScroll.setBounds(
                295, 161, 337, 90
        );

        descriptionScroll.setBorder(
                new LineBorder(BORDER_COLOR, 1)
        );

        descriptionScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        descriptionScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        content.add(descriptionScroll);

        // =====================================
        // 8. 카페인 / 원산지 정보 표
        // =====================================

        JPanel infoPanel = new JPanel(null);

        infoPanel.setBounds(
                295, 273, 337, 56
        );

        infoPanel.setBackground(WHITE);

        infoPanel.setBorder(
                new LineBorder(BORDER_COLOR, 1)
        );

        content.add(infoPanel);

        // 카페인 정보 변환
        String caffeineYn =
                selectedMenu.getCaffeineYn();

        String caffeineText;

        if ("Y".equalsIgnoreCase(caffeineYn)) {
            caffeineText = "O";
        } else if ("N".equalsIgnoreCase(caffeineYn)) {
            caffeineText = "X";
        } else {
            caffeineText = "-";
        }

        // 첫 번째 행: 카페인 유무
        JLabel caffeineTitle = createTableCell(
                "카페인 유무",
                true,
                false
        );

        caffeineTitle.setBounds(0, 0, 94, 27);
        infoPanel.add(caffeineTitle);

        JLabel caffeineValue = createTableCell(
                caffeineText,
                false,
                false
        );

        caffeineValue.setBounds(94, 0, 241, 27);
        infoPanel.add(caffeineValue);

        // 두 번째 행: 원산지
        JLabel originTitle = createTableCell(
                "원산지",
                true,
                true
        );

        originTitle.setBounds(0, 27, 94, 27);
        infoPanel.add(originTitle);

        // 원산지가 긴 경우에는 툴팁으로 전체 확인
        String originText =
                safe(selectedMenu.getOriginInfo());

        JLabel originValue = createTableCell(
                originText,
                false,
                true
        );

        originValue.setBounds(94, 27, 241, 27);
        originValue.setToolTipText(originText);

        infoPanel.add(originValue);

        // =====================================
        // 9. 닫기 버튼
        // =====================================

        closeButton.setBounds(
                149, 427, 148, 45
        );

        closeButton.setFont(TITLE_FONT);
        closeButton.setBackground(CLOSE_COLOR);
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);

        closeButton.setBorder(
                new LineBorder(Color.GRAY, 1)
        );

        content.add(closeButton);

        // =====================================
        // 10. 옵션 선택 버튼
        // =====================================

        optionButton.setBounds(
                320, 427, 200, 45
        );

        optionButton.setFont(TITLE_FONT);
        optionButton.setBackground(OPTION_COLOR);
        optionButton.setForeground(WHITE);
        optionButton.setFocusPainted(false);

        optionButton.setBorder(
                new LineBorder(Color.GRAY, 1)
        );

        content.add(optionButton);

        // =====================================
        // 11. 이벤트 연결
        // =====================================

        MenuDetailEvt evt = new MenuDetailEvt(this);

        optionButton.addActionListener(
                e -> evt.selectOption()
        );

        closeButton.addActionListener(
                e -> evt.closeDetail()
        );

        // 품절 메뉴 옵션 선택 차단
        if (selectedMenu.getMenuStatus() != 0) {
            optionButton.setEnabled(false);
        }

        // 내부 화면 크기에 맞게 창 크기 자동 조정
        pack();

        setLocationRelativeTo(owner);
    }

    /**
     * 카페인/원산지 표 셀 생성
     *
     * @param value 셀에 표시할 값
     * @param isHeader 항목명 셀 여부
     * @param isLastRow 마지막 행 여부
     */
    private JLabel createTableCell(
            String value,
            boolean isHeader,
            boolean isLastRow) {

        JLabel label = new JLabel(
                value,
                isHeader
                        ? SwingConstants.CENTER
                        : SwingConstants.LEFT
        );

        label.setFont(SMALL_FONT);
        label.setOpaque(true);

        label.setBackground(
                isHeader ? LABEL_COLOR : WHITE
        );

        if (!isHeader) {

            label.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(
                                    0,
                                    1,
                                    isLastRow ? 0 : 1,
                                    0,
                                    BORDER_COLOR
                            ),
                            BorderFactory.createEmptyBorder(
                                    0, 5, 0, 2
                            )
                    )
            );

        } else {

            label.setBorder(
                    BorderFactory.createMatteBorder(
                            0,
                            0,
                            isLastRow ? 0 : 1,
                            0,
                            BORDER_COLOR
                    )
            );
        }

        return label;
    }

    /**
     * 2번 담당자 옵션 선택 화면 연결
     */
    public static void setOptionHandler(
            Consumer<MenuDTO> handler) {

        optionHandler = handler;
    }

    /**
     * 현재 선택한 메뉴 반환
     */
    public MenuDTO getSelectedMenu() {
        return selectedMenu;
    }

    public JButton getOptionButton() {
        return optionButton;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    /**
     * 옵션 선택 버튼 처리
     */
    public void selectOption() {

        if (selectedMenu.getMenuStatus() != 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "품절된 메뉴입니다."
            );

            return;
        }

        if (optionHandler == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "옵션 선택 기능은 "
                    + "2번 담당자 연동 후 사용할 수 있습니다."
            );

            return;
        }

        // 기존 연동 방식 유지
        optionHandler.accept(selectedMenu);
    }

    /**
     * 상품 상세 팝업 닫기
     */
    public void closeDetail() {
        dispose();
    }

    /**
     * null 문자열 처리
     */
    private static String safe(String value) {
        return value == null ? "" : value;
    }
}

