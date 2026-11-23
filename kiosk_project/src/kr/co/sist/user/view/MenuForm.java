
package kr.co.sist.user.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.net.URL;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.function.IntSupplier;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.evt.MenuEvt;

/**
 * 사용자 메뉴 선택 화면
 *
 * 주요 기능
 * 1. 카테고리 표시
 * 2. 메뉴 조회
 * 3. 품절 상품 표시
 * 4. 상품 상세 화면 열기
 * 5. 장바구니 개수 및 금액 표시
 * 6. 장바구니가 비어 있으면 결제 차단
 * 7. 결제 선택 화면 연결
 */
public class MenuForm extends JFrame {

    private static final long serialVersionUID = 1L;

    // ========================================
    // 1. 기본 색상 및 숫자 형식
    // ========================================

    private static final Color BG =
            new Color(170, 170, 170);

    private static final Color SELECTED =
            new Color(100, 100, 100);

    private static final Color SOLD_OUT_BG =
            new Color(70, 70, 70);

    private static final NumberFormat MONEY =
            NumberFormat.getIntegerInstance(
                    Locale.KOREA
            );

    // ========================================
    // 2. 팀원 기능 연결용 변수
    // ========================================

    // 3번 담당자의 결제 화면 실행 기능
    private static Runnable paymentHandler;

    // 2번 담당자 장바구니 상품 개수
    private static IntSupplier cartCountSupplier =
            () -> 0;

    // 2번 담당자 장바구니 총금액
    private static IntSupplier cartTotalSupplier =
            () -> 0;

    // ========================================
    // 3. 화면 구성 요소
    // ========================================

    private final JPanel categoryPanel =
            new JPanel(null);

    private final JPanel cardPanel =
            new JPanel(null);

    private final JLabel cartSummary =
            new JLabel(
                    "담은 상품 0개  |  합계 0원"
            );

    private final JButton checkoutButton =
            new JButton("결제");

    private final MenuEvt menuEvt;

    private List<CategoryDTO> categories =
            new ArrayList<>();

    private List<MenuDTO> menus =
            new ArrayList<>();

    private long selectedCategoryId = 0;

    // ========================================
    // 4. 생성자
    // ========================================

    public MenuForm() {

        super("카페 키오스크 - 메뉴 선택");

        // 화면 설정
        setSize(804, 564);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        getContentPane().setLayout(null);
        getContentPane().setBackground(BG);

        // ====================================
        // 5. 카테고리 영역
        // ====================================

        categoryPanel.setOpaque(false);

        categoryPanel.setBounds(
                40, 20, 715, 48
        );

        add(categoryPanel);

        // ====================================
        // 6. 메뉴 카드 영역
        // ====================================

        cardPanel.setBackground(BG);

        cardPanel.setPreferredSize(
                new Dimension(690, 400)
        );

        JScrollPane scroll =
                new JScrollPane(cardPanel);

        scroll.setBounds(
                49, 87, 707, 362
        );

        scroll.setBorder(null);

        scroll.getViewport().setBackground(BG);

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(scroll);

        // ====================================
        // 7. 하단 장바구니 및 결제 영역
        // ====================================

        JPanel bottom = new JPanel(null);

        bottom.setBounds(
                0, 469, 788, 56
        );

        bottom.setBackground(Color.WHITE);

        add(bottom);

        // 장바구니 개수 및 금액 표시
        cartSummary.setBounds(
                40, 10, 545, 36
        );

        cartSummary.setFont(
                new Font(
                        "맑은 고딕",
                        Font.BOLD,
                        16
                )
        );

        bottom.add(cartSummary);

        // 결제 버튼
        checkoutButton.setBounds(
                626, 6, 137, 44
        );

        checkoutButton.setBackground(
                new Color(75, 75, 75)
        );

        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);

        checkoutButton.setFont(
                new Font(
                        "맑은 고딕",
                        Font.BOLD,
                        17
                )
        );

        bottom.add(checkoutButton);

        // ====================================
        // 8. 이벤트 연결
        // ====================================

        menuEvt = new MenuEvt(this);

        checkoutButton.addActionListener(
                e -> menuEvt.clickCheckout()
        );

        // ====================================
        // 9. DB 메뉴 데이터 불러오기
        // ====================================

        menuEvt.loadCategories();

        menuEvt.selectCategory(0);

        refreshCartSummary();
    }

    // ========================================
    // 10. 결제 기능 연결
    // ========================================

    /**
     * 3번 담당자의 결제 선택 화면을
     * 실행할 핸들러를 등록한다.
     *
     * KioskMain.java에서 호출한다.
     */
    public static void setPaymentHandler(
            Runnable handler) {

        paymentHandler = handler;
    }

    /**
     * 결제 버튼 클릭 시 실행
     *
     * 1. 장바구니 상품 개수 확인
     * 2. 상품이 없으면 결제 차단
     * 3. 상품이 있으면 결제 화면 실행
     */
    public void openPaymentSelectForm() {

        // 현재 장바구니 상품 개수
        int cartCount;

        try {

            cartCount =
                    cartCountSupplier.getAsInt();

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "장바구니 정보를 확인할 수 없습니다.",
                    "결제 안내",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ====================================
        // 장바구니에 상품이 없는 경우
        // ====================================

        if (cartCount <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "장바구니에 상품이 없습니다.\n"
                            + "상품을 먼저 담아주세요.",
                    "결제 안내",
                    JOptionPane.WARNING_MESSAGE
            );

            // 결제 화면 이동 차단
            return;
        }

        // ====================================
        // 결제 화면 미연결 확인
        // ====================================

        if (paymentHandler == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "결제 화면이 연결되지 않았습니다.",
                    "결제 안내",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ====================================
        // 상품이 있는 경우 결제 화면 이동
        // ====================================

        paymentHandler.run();
    }

    // ========================================
    // 11. 장바구니 기능 연결
    // ========================================

    /**
     * 2번 담당자의 CartListService와
     * 연결하기 위한 메서드
     */
    public static void setCartSummarySuppliers(
            IntSupplier itemCount,
            IntSupplier total) {

        cartCountSupplier =
                itemCount == null
                        ? () -> 0
                        : itemCount;

        cartTotalSupplier =
                total == null
                        ? () -> 0
                        : total;
    }

    /**
     * 하단 장바구니 개수 및 금액 표시
     */
    public void refreshCartSummary() {

        try {

            int count = Math.max(
                    0,
                    cartCountSupplier.getAsInt()
            );

            int total = Math.max(
                    0,
                    cartTotalSupplier.getAsInt()
            );

            cartSummary.setText(
                    "담은 상품 "
                            + count
                            + "개  |  합계 "
                            + MONEY.format(total)
                            + "원"
            );

        } catch (RuntimeException e) {

            cartSummary.setText(
                    "장바구니 정보를 불러올 수 없습니다."
            );
        }
    }

    // ========================================
    // 12. 카테고리 관련 메서드
    // ========================================

    public long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(
            long categoryId) {

        selectedCategoryId = categoryId;
    }

    public void setCategoryTitle(String title) {

        setTitle(
                "카페 키오스크 - " + title
        );
    }

    /**
     * 카테고리 버튼 표시
     */
    public void displayCategories(
            List<CategoryDTO> result) {

        categories =
                result == null
                        ? new ArrayList<>()
                        : result;

        categoryPanel.removeAll();

        // 전체 카테고리 추가
        List<CategoryDTO> all =
                new ArrayList<>();

        all.add(
                new CategoryDTO(0, "전체")
        );

        all.addAll(categories);

        int count = Math.max(
                1, all.size()
        );

        int buttonWidth = Math.min(
                120,
                (715 - (count - 1) * 12) / count
        );

        int gap =
                (715 - buttonWidth * count)
                        / Math.max(1, count - 1);

        // 카테고리 버튼 생성
        for (int i = 0; i < all.size(); i++) {

            CategoryDTO category = all.get(i);

            JButton button = new JButton(
                    category.getCategoryName()
            );

            button.setFont(
                    new Font(
                            "맑은 고딕",
                            Font.BOLD,
                            15
                    )
            );

            button.setBounds(
                    i * (buttonWidth + gap),
                    0,
                    buttonWidth,
                    42
            );

            button.setFocusPainted(false);

            boolean selected =
                    category.getCategoryId()
                            == selectedCategoryId;

            button.setBackground(
                    selected
                            ? SELECTED
                            : Color.WHITE
            );

            button.setForeground(
                    selected
                            ? Color.WHITE
                            : Color.BLACK
            );

            button.addActionListener(
                    e -> menuEvt.selectCategory(
                            category.getCategoryId()
                    )
            );

            categoryPanel.add(button);
        }

        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    // ========================================
    // 13. 메뉴 카드 표시
    // ========================================

    /**
     * 메뉴 카드 표시
     *
     * MENU_STATUS
     * 0 = 판매중
     * 1 = 품절
     *
     * 품절 상품은 클릭할 수 없다.
     */
    public void displayMenuThumbnails(
            List<MenuDTO> result) {

        menus =
                result == null
                        ? new ArrayList<>()
                        : result;

        cardPanel.removeAll();

        final int cardWidth = 155;
        final int cardHeight = 181;

        final int gapX = 17;
        final int gapY = 16;

        for (int i = 0; i < menus.size(); i++) {

            MenuDTO item = menus.get(i);

            // 메뉴 상태 조회
            int status = item.getMenuStatus();

            // 상태 확인용 콘솔 출력
            System.out.println(
                    "[메뉴 조회] "
                            + item.getMenuName()
                            + " / MENU_STATUS = "
                            + status
            );

            // 메뉴 카드 위치 계산
            int col = i % 4;
            int row = i / 4;

            // 메뉴 카드 생성
            JPanel card = new JPanel(null);

            card.setBounds(
                    col * (cardWidth + gapX),
                    row * (cardHeight + gapY),
                    cardWidth,
                    cardHeight
            );

            card.setBackground(Color.WHITE);

            card.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(225, 225, 225)
                    )
            );

            // =================================
            // 메뉴 이미지
            // =================================

            JLabel picture = new JLabel(
                    "이미지",
                    SwingConstants.CENTER
            );

            picture.setBounds(
                    8, 8, 139, 115
            );

            picture.setOpaque(true);

            picture.setBackground(
                    new Color(220, 220, 220)
            );

            picture.setForeground(
                    new Color(100, 100, 100)
            );

            // 품절 라벨 배치를 위해
            // 이미지 영역에 자유 배치 적용
            picture.setLayout(null);

            ImageIcon icon = loadScaledIcon(
                    item.getThumbnailPath(),
                    139,
                    115
            );

            if (icon != null) {

                picture.setIcon(icon);
                picture.setText("");
            }

            // =================================
            // 품절 상품 표시
            // =================================

            if (status == 1) {

                JLabel soldOutLabel =
                        new JLabel(
                                "품절",
                                SwingConstants.CENTER
                        );

                // 이미지 중앙에 배치
                soldOutLabel.setBounds(
                        0, 37, 139, 40
                );

                soldOutLabel.setOpaque(true);

                soldOutLabel.setBackground(
                        SOLD_OUT_BG
                );

                soldOutLabel.setForeground(
                        Color.WHITE
                );

                soldOutLabel.setFont(
                        new Font(
                                "맑은 고딕",
                                Font.BOLD,
                                22
                        )
                );

                // 이미지 위에 표시
                picture.add(soldOutLabel);

                picture.setComponentZOrder(
                        soldOutLabel, 0
                );
            }

            card.add(picture);

            // =================================
            // 메뉴 이름
            // =================================

            JLabel name = new JLabel(
                    item.getMenuName()
            );

            name.setBounds(
                    9, 130, 139, 20
            );

            name.setFont(
                    new Font(
                            "맑은 고딕",
                            Font.BOLD,
                            13
                    )
            );

            card.add(name);

            // =================================
            // 메뉴 가격
            // =================================

            JLabel price = new JLabel(
                    MONEY.format(
                            item.getBasePrice()
                    ) + "원"
            );

            price.setBounds(
                    9, 152, 139, 21
            );

            price.setFont(
                    new Font(
                            "맑은 고딕",
                            Font.PLAIN,
                            13
                    )
            );

            card.add(price);

            // =================================
            // 판매중 메뉴만 선택 가능
            // =================================

            if (status == 0) {

                MouseAdapter click =
                        new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        menuEvt.selectMenu(
                                item.getMenuId()
                        );
                    }
                };

                card.addMouseListener(click);

                for (Component child :
                        card.getComponents()) {

                    child.addMouseListener(click);
                }
            }

            cardPanel.add(card);
        }

        // 스크롤 영역 크기 계산
        int rows =
                (menus.size() + 3) / 4;

        cardPanel.setPreferredSize(
                new Dimension(
                        690,
                        Math.max(
                                345,
                                rows * (cardHeight + gapY)
                        )
                )
        );

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // ========================================
    // 14. 메뉴 상세 화면
    // ========================================

    /**
     * 메뉴 상세 팝업 표시
     */
    public void openMenuDetail(long menuId) {

        MenuDetailDialog dialog =
                new MenuDetailDialog(
                        this,
                        menuId
                );

        dialog.setVisible(true);

        // 상세화면 종료 후 장바구니 갱신
        refreshCartSummary();
    }

    // ========================================
    // 15. 이미지 로딩
    // ========================================

    /**
     * DB에 저장된 이미지 경로에서
     * 이미지 파일을 읽어 크기를 조절한다.
     */
    public static ImageIcon loadScaledIcon(
            String path,
            int width,
            int height) {

        if (path == null
                || path.trim().isEmpty()) {

            return null;
        }

        try {

            String resourcePath =
                    path.startsWith("/")
                            ? path
                            : "/" + path;

            URL url =
                    MenuForm.class.getResource(
                            resourcePath
                    );

            ImageIcon original = null;

            // 클래스패스에서 이미지 로딩
            if (url != null) {

                original = new ImageIcon(url);
            }

            // 실제 파일 경로에서 이미지 로딩
            if (original == null
                    || original.getIconWidth() <= 0) {

                File file = new File(path);

                if (!file.isFile()
                        && path.startsWith("/")) {

                    file = new File(
                            "src" + path
                    );
                }

                if (file.isFile()) {

                    original = new ImageIcon(
                            file.getAbsolutePath()
                    );
                }
            }

            if (original == null
                    || original.getIconWidth() <= 0) {

                return null;
            }

            // 이미지 크기 조절
            Image scaled =
                    original.getImage()
                            .getScaledInstance(
                                    width,
                                    height,
                                    Image.SCALE_SMOOTH
                            );

            return new ImageIcon(scaled);

        } catch (RuntimeException e) {

            return null;
        }
    }

    // ========================================
    // 16. Getter
    // ========================================

    /**
     * 결제 버튼 반환
     */
    public JButton getCheckoutButton() {
        return checkoutButton;
    }
}
