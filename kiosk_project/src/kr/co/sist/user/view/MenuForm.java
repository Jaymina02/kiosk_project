
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
 * 카페 키오스크 사용자 메뉴 화면
 *
 * 담당 기능
 * 1. 카테고리 표시
 * 2. 메뉴 목록 표시
 * 3. 메뉴 상세화면 이동
 * 4. 품절 메뉴 표시 및 선택 제한
 * 5. 장바구니 개수와 총금액 표시
 * 6. 결제 화면 연결
 *
 * 기존 화면 디자인
 * - 크기 804 x 564
 * - 회색 배경
 * - 메뉴 카드 4열
 * - 하단 장바구니 및 결제 버튼
 */
public class MenuForm extends JFrame {

    private static final long serialVersionUID = 1L;

    // ========================================
    // 1. 화면 기본 설정
    // ========================================

    // 메인 배경색
    private static final Color BG =
            new Color(170, 170, 170);

    // 선택된 카테고리 버튼 색상
    private static final Color SELECTED =
            new Color(100, 100, 100);

    // 품절 표시 배경색
    private static final Color SOLD_OUT_BG =
            new Color(70, 70, 70);

    // 원화 표시
    private static final NumberFormat MONEY =
            NumberFormat.getIntegerInstance(
                    Locale.KOREA
            );

    // ========================================
    // 2. 팀원 기능 연결
    // ========================================

    // 3번 담당자 결제 화면 연결
    private static Runnable paymentHandler;

    // 2번 담당자 장바구니 개수
    private static IntSupplier cartCountSupplier =
            () -> 0;

    // 2번 담당자 장바구니 총금액
    private static IntSupplier cartTotalSupplier =
            () -> 0;

    // ========================================
    // 3. 화면 컴포넌트
    // ========================================

    // 카테고리 버튼 영역
    private final JPanel categoryPanel =
            new JPanel(null);

    // 메뉴 카드 영역
    private final JPanel cardPanel =
            new JPanel(null);

    // 하단 장바구니 정보
    private final JLabel cartSummary =
            new JLabel(
                    "담은 상품 0개  |  합계 0원"
            );

    // 결제 버튼
    private final JButton checkoutButton =
            new JButton("결제");

    // 메뉴 이벤트
    private final MenuEvt menuEvt;

    // 카테고리 목록
    private List<CategoryDTO> categories =
            new ArrayList<>();

    // 메뉴 목록
    private List<MenuDTO> menus =
            new ArrayList<>();

    // 현재 선택한 카테고리
    private long selectedCategoryId = 0;

    // ========================================
    // 4. 생성자
    // ========================================

    public MenuForm() {

        super("카페 키오스크 - 메뉴 선택");

        // 화면 크기 설정
        setSize(804, 564);

        // 화면 중앙 배치
        setLocationRelativeTo(null);

        // 화면 크기 변경 방지
        setResizable(false);

        // 화면 닫기
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        // 자유 배치
        getContentPane().setLayout(null);

        // 배경색 설정
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

        // 장바구니 요약
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

        checkoutButton.setForeground(
                Color.WHITE
        );

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
        // 8. 이벤트 객체 생성
        // ====================================

        menuEvt = new MenuEvt(this);

        // 결제 버튼 이벤트
        checkoutButton.addActionListener(
                e -> menuEvt.clickCheckout()
        );

        // ====================================
        // 9. DB 데이터 조회
        // ====================================

        // 카테고리 조회
        menuEvt.loadCategories();

        // 전체 메뉴 조회
        menuEvt.selectCategory(0);

        // 장바구니 금액 표시
        refreshCartSummary();
    }

    // ========================================
    // 10. 결제 화면 연결
    // ========================================

    /**
     * 3번 담당자의 결제 선택 화면 연결
     */
    public static void setPaymentHandler(
            Runnable handler) {

        paymentHandler = handler;
    }

    /**
     * 결제 선택 화면 실행
     */
    public void openPaymentSelectForm() {

        if (paymentHandler != null) {

            paymentHandler.run();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "결제 화면 연결 전입니다."
            );
        }
    }

    // ========================================
    // 11. 장바구니 연결
    // ========================================

    /**
     * 장바구니 개수 및 금액 조회 메서드 등록
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
     * 하단 장바구니 요약 갱신
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
    // 12. 카테고리 목록 표시
    // ========================================

    /**
     * DB에서 조회한 카테고리로 버튼 생성
     *
     * 카테고리
     * 0 = 전체
     * 1 = 커피
     * 2 = 티
     * 3 = 스무디
     * 4 = 디저트
     */
    public void displayCategories(
            List<CategoryDTO> result) {

        categories =
                result == null
                        ? new ArrayList<>()
                        : result;

        // 기존 버튼 삭제
        categoryPanel.removeAll();

        // 전체 버튼을 포함한 목록 생성
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

        // 화면 다시 그리기
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    // ========================================
    // 13. 메뉴 카드 표시
    // ========================================

    /**
     * DB에서 조회한 메뉴 목록 표시
     *
     * MENU_STATUS
     * 0 = 판매중
     * 1 = 품절
     *
     * 품절 메뉴는 화면에 표시하지만
     * 선택할 수 없도록 처리한다.
     */
    public void displayMenuThumbnails(
            List<MenuDTO> result) {

        menus =
                result == null
                        ? new ArrayList<>()
                        : result;

        // 이전 메뉴 카드 삭제
        cardPanel.removeAll();

        // 메뉴 카드 크기
        final int cardWidth = 155;
        final int cardHeight = 181;

        // 메뉴 카드 간격
        final int gapX = 17;
        final int gapY = 16;

        // 메뉴 목록 반복
        for (int i = 0; i < menus.size(); i++) {

            MenuDTO item = menus.get(i);

            // 메뉴 상태값
            int status = item.getMenuStatus();

            // 콘솔에서 DB 상태 확인
            System.out.println(
                    "[메뉴 조회] "
                            + item.getMenuName()
                            + " / MENU_STATUS = "
                            + status
            );

            // 카드 배치 위치
            int col = i % 4;
            int row = i / 4;

            // =================================
            // 메뉴 카드 생성
            // =================================

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
            // 메뉴 이미지 영역
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

            // 품절 라벨을 내부에 배치하기 위해
            // 이미지 영역의 레이아웃을 null로 설정
            picture.setLayout(null);

            // 이미지 읽기
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
            // 품절 표시 기능
            // =================================

            if (status == 1) {

                /*
                 * 품절 라벨을 이미지 영역 안에
                 * 직접 추가한다.
                 *
                 * 메뉴 카드의 다른 컴포넌트와
                 * 겹치는 문제를 방지한다.
                 */

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

                // 이미지 위에 품절 라벨 추가
                picture.add(soldOutLabel);

                // 품절 라벨을 최상단으로 이동
                picture.setComponentZOrder(
                        soldOutLabel, 0
                );
            }

            card.add(picture);

            // =================================
            // 메뉴명 표시
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
            // 메뉴 가격 표시
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
            // 판매중 메뉴 클릭 이벤트
            // =================================

            if (status == 0) {

                MouseAdapter click =
                        new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        // 메뉴 상세 팝업 실행
                        menuEvt.selectMenu(
                                item.getMenuId()
                        );
                    }
                };

                card.addMouseListener(click);

                // 이미지, 메뉴명, 가격 클릭도 허용
                for (Component child :
                        card.getComponents()) {

                    child.addMouseListener(click);
                }

            } else {

                // 품절 메뉴는 클릭 이벤트 미등록
                // 따라서 상세 팝업이 열리지 않음
            }

            // 메뉴 카드 추가
            cardPanel.add(card);
        }

        // ====================================
        // 스크롤 영역 크기 설정
        // ====================================

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

        // 화면 다시 그리기
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // ========================================
    // 14. 메뉴 상세화면
    // ========================================

    /**
     * 판매중인 메뉴를 선택하면
     * 상세 팝업을 실행한다.
     */
    public void openMenuDetail(long menuId) {

        MenuDetailDialog dialog =
                new MenuDetailDialog(
                        this,
                        menuId
                );

        // 상세 화면 표시
        dialog.setVisible(true);

        // 상세화면 종료 후 장바구니 갱신
        refreshCartSummary();
    }

    // ========================================
    // 15. 이미지 로딩
    // ========================================

    /**
     * DB IMAGE_PATH / THUMBNAIL_PATH로
     * 저장된 이미지 파일을 읽는다.
     *
     * 예시 경로:
     * /images/americano.jpg
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

            // 클래스패스 이미지 경로
            String resourcePath =
                    path.startsWith("/")
                            ? path
                            : "/" + path;

            URL url =
                    MenuForm.class.getResource(
                            resourcePath
                    );

            ImageIcon original = null;

            // 클래스패스에서 이미지 조회
            if (url != null) {

                original = new ImageIcon(url);
            }

            // 이미지가 클래스패스에 없으면
            // 실제 파일 경로에서 조회
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

            // 이미지가 없으면 null 반환
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
    // 16. Getter / Setter
    // ========================================

    /**
     * 현재 선택된 카테고리 번호
     */
    public long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    /**
     * 카테고리 번호 변경
     */
    public void setSelectedCategoryId(
            long categoryId) {

        selectedCategoryId = categoryId;
    }

    /**
     * 메뉴 화면 제목 변경
     */
    public void setCategoryTitle(String title) {

        setTitle(
                "카페 키오스크 - " + title
        );
    }

    /**
     * 결제 버튼 반환
     */
    public JButton getCheckoutButton() {
        return checkoutButton;
    }
}
