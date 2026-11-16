
package kr.co.sist.user.view;

import kr.co.sist.user.dto.CategoryDTO;
import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.evt.MenuEvt;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import java.util.function.IntSupplier;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * 사용자 메뉴 선택 화면
 *
 * 화면설계서 기준 UI
 *
 * 1. 회색 배경
 * 2. 상단 카테고리 버튼 5개
 * 3. 메뉴 카드 4열 배치
 * 4. 이미지 / 메뉴명 / 가격 표시
 * 5. 하단 장바구니 및 결제 버튼
 *
 * 기존 UI를 유지하면서 DB와 연결한다.
 */
public class MenuForm extends JFrame {

    // =========================================
    // 1. 화면 색상 및 크기
    // =========================================

    // 화면설계서의 전체 배경색
    private static final Color BG_COLOR =
            new Color(170, 170, 170);

    // 선택된 카테고리 및 결제 버튼 색상
    private static final Color DARK_COLOR =
            new Color(95, 95, 95);

    // 메뉴 이미지가 없을 때 표시할 배경색
    private static final Color IMAGE_BG =
            new Color(230, 230, 230);

    // 메뉴 카드 배경색
    private static final Color CARD_COLOR =
            Color.WHITE;

    // 화면설계서 기준 콘텐츠 크기
    private static final int FORM_WIDTH = 804;
    private static final int FORM_HEIGHT = 564;

    // 메뉴 카드 크기
    private static final int CARD_WIDTH = 155;
    private static final int CARD_HEIGHT = 181;

    // 메뉴 카드 간 가로 및 세로 간격
    private static final int CARD_GAP_X = 18;
    private static final int CARD_GAP_Y = 14;


    // =========================================
    // 2. 이벤트 및 데이터
    // =========================================

    // 메뉴 이벤트 담당 객체
    private MenuEvt menuEvt;

    // DB에서 가져온 카테고리 목록
    private List<CategoryDTO> categoryList =
            new ArrayList<>();

    // DB에서 가져온 메뉴 목록
    private List<MenuDTO> menuList =
            new ArrayList<>();

    // 현재 선택한 카테고리 번호
    private long selectedCategoryId = 0;


    // =========================================
    // 3. 화면 컴포넌트
    // =========================================

    // 전체 화면 패널
    private JPanel mainPanel;

    // 상단 카테고리 버튼 영역
    private JPanel categoryPanel;

    // 메뉴 카드들이 배치되는 패널
    private JPanel menuPanel;

    // 메뉴 목록 스크롤
    private JScrollPane menuScrollPane;

    // 하단 장바구니 표시
    private JLabel cartSummaryLabel;

    // 결제 버튼
    private JButton checkoutButton;


    // =========================================
    // 4. 팀원 기능 연동
    // =========================================

    /*
     * 2번 담당자의 CartListService에서
     * 장바구니 상품 개수를 가져오기 위한 함수
     */
    private static IntSupplier cartCountSupplier =
            () -> 0;

    /*
     * 장바구니 총금액 조회 함수
     */
    private static IntSupplier cartTotalSupplier =
            () -> 0;

    /*
     * 3번 담당자의 PaymentSelectForm을
     * 실행하기 위한 함수
     */
    private static Runnable paymentHandler;


    // =========================================
    // 5. 생성자
    // =========================================

    /**
     * MenuForm 생성자
     */
    public MenuForm() {

        // 프로그램 제목 설정
        setTitle("카페 키오스크 - 메뉴 선택");

        // 전체 화면 생성
        initializeComponents();

        // 콘텐츠 크기에 맞춰 JFrame 크기 설정
        pack();

        // 화면설계서 크기 유지를 위해 크기 변경 제한
        setResizable(false);

        // 모니터 중앙에 배치
        setLocationRelativeTo(null);

        // 메뉴 화면을 닫을 때 현재 창만 닫는다.
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        // 메뉴 이벤트 객체 생성
        menuEvt = new MenuEvt(this);

        // Oracle DB에서 카테고리 조회
        menuEvt.loadCategories();

        // 처음에는 전체 메뉴 조회
        menuEvt.selectCategory(0);

        // 현재 장바구니 상태 표시
        refreshCartSummary();
    }


    // =========================================
    // 6. 전체 화면 생성
    // =========================================

    /**
     * 화면설계서와 같은 위치에
     * UI 컴포넌트를 배치한다.
     */
    private void initializeComponents() {

        // 전체 화면 패널 생성
        mainPanel = new JPanel();

        // 화면설계서의 위치를 그대로 사용
        mainPanel.setLayout(null);

        // 회색 배경 설정
        mainPanel.setBackground(BG_COLOR);

        // 전체 콘텐츠 크기
        mainPanel.setPreferredSize(
                new Dimension(
                        FORM_WIDTH,
                        FORM_HEIGHT
                )
        );

        // JFrame에 패널 등록
        setContentPane(mainPanel);

        // 상단 카테고리 생성
        createCategoryArea();

        // 중앙 메뉴 목록 생성
        createMenuArea();

        // 하단 장바구니 및 결제 생성
        createBottomArea();
    }


    // =========================================
    // 7. 카테고리 버튼 영역
    // =========================================

    /**
     * 전체, 커피, 티, 스무디, 디저트
     * 카테고리 버튼 영역 생성
     */
    private void createCategoryArea() {

        // 상단 카테고리 패널 생성
        categoryPanel = new JPanel(null);

        // 전체 배경과 동일한 색상
        categoryPanel.setBackground(BG_COLOR);

        // 화면설계서의 카테고리 위치
        categoryPanel.setBounds(
                65, 26, 675, 27
        );

        // 전체 화면에 추가
        mainPanel.add(categoryPanel);
    }

    /**
     * DB에서 조회한 카테고리 저장
     */
    public void setCategoryList(
            List<CategoryDTO> categoryList) {

        // null이면 빈 리스트 사용
        this.categoryList =
                categoryList == null
                        ? new ArrayList<>()
                        : new ArrayList<>(categoryList);
    }

    /**
     * 카테고리 버튼 표시
     */
    public void displayCategories() {

        // 기존 카테고리 버튼 제거
        categoryPanel.removeAll();

        // 첫 번째 전체 버튼 생성
        JButton allButton =
                createCategoryButton(
                        "전체",
                        0,
                        0,
                        70
                );

        // 전체 메뉴 버튼 등록
        categoryPanel.add(allButton);

        // 다음 버튼 시작 위치
        int buttonX = 74;

        // DB에 등록된 카테고리 반복
        for (CategoryDTO category : categoryList) {

            // 카테고리 번호 가져오기
            long categoryId =
                    category.getCategoryId();

            // 카테고리 이름 가져오기
            String categoryName =
                    category.getCategoryName();

            // 카테고리 버튼 생성
            JButton button =
                    createCategoryButton(
                            categoryName,
                            categoryId,
                            buttonX,
                            70
                    );

            // 패널에 버튼 추가
            categoryPanel.add(button);

            // 다음 버튼을 오른쪽에 배치
            buttonX += 74;
        }

        // 버튼 선택 상태 갱신
        updateCategoryButtons();

        // 화면 다시 그리기
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    /**
     * 카테고리 버튼 한 개 생성
     *
     * @param name 버튼에 표시할 이름
     * @param categoryId 카테고리 번호
     * @param x 가로 위치
     * @param width 버튼 너비
     */
    private JButton createCategoryButton(
            String name,
            long categoryId,
            int x,
            int width) {

        // 이름을 가진 버튼 생성
        JButton button = new JButton(name);

        // 카테고리 번호 저장
        button.putClientProperty(
                "categoryId",
                categoryId
        );

        // 버튼 위치와 크기
        button.setBounds(
                x, 0, width, 26
        );

        // 글씨 크기
        button.setFont(
                new Font(
                        "맑은 고딕",
                        Font.PLAIN,
                        13
                )
        );

        // 버튼 배경
        button.setBackground(Color.WHITE);

        // 글씨 색상
        button.setForeground(Color.BLACK);

        // 얇은 테두리 적용
        button.setBorder(
                BorderFactory.createLineBorder(
                        new Color(130, 130, 130)
                )
        );

        // 포커스 테두리 제거
        button.setFocusPainted(false);

        // 마우스 커서 설정
        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        // 버튼 클릭 시 카테고리 변경
        button.addActionListener(e -> {

            // 선택 카테고리 저장
            selectedCategoryId = categoryId;

            // 선택 상태 반영
            updateCategoryButtons();

            // DB에서 해당 카테고리 메뉴 조회
            menuEvt.selectCategory(categoryId);
        });

        // 생성된 버튼 반환
        return button;
    }

    /**
     * 선택한 카테고리는 진회색,
     * 나머지는 흰색으로 표시한다.
     */
    private void updateCategoryButtons() {

        // 카테고리 패널의 모든 버튼 확인
        for (java.awt.Component component :
                categoryPanel.getComponents()) {

            // JButton인지 확인
            if (!(component instanceof JButton)) {
                continue;
            }

            JButton button =
                    (JButton) component;

            // 버튼에 저장된 카테고리 번호
            Object idObject =
                    button.getClientProperty(
                            "categoryId"
                    );

            // 번호가 없으면 건너뛴다.
            if (!(idObject instanceof Number)) {
                continue;
            }

            long categoryId =
                    ((Number) idObject).longValue();

            // 현재 선택한 카테고리인지 확인
            boolean selected =
                    categoryId == selectedCategoryId;

            // 선택 버튼은 진회색
            button.setBackground(
                    selected
                            ? DARK_COLOR
                            : Color.WHITE
            );

            // 선택 버튼은 흰 글씨
            button.setForeground(
                    selected
                            ? Color.WHITE
                            : Color.BLACK
            );
        }
    }


    // =========================================
    // 8. 메뉴 카드 영역
    // =========================================

    /**
     * 메뉴 목록을 표시할 영역 생성
     */
    private void createMenuArea() {

        // 메뉴 카드 배치용 패널
        menuPanel = new JPanel(null);

        // 메뉴 영역 배경 회색
        menuPanel.setBackground(BG_COLOR);

        // 기본 크기
        menuPanel.setPreferredSize(
                new Dimension(674, 376)
        );

        // 메뉴 목록을 스크롤로 표시
        menuScrollPane =
                new JScrollPane(menuPanel);

        // 화면설계서의 메뉴 목록 위치
        menuScrollPane.setBounds(
                65, 70, 675, 378
        );

        // 스크롤 영역의 테두리 제거
        menuScrollPane.setBorder(null);

        // 스크롤 내부 배경 설정
        menuScrollPane.getViewport()
                .setBackground(BG_COLOR);

        // 가로 스크롤 사용 안 함
        menuScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants
                .HORIZONTAL_SCROLLBAR_NEVER
        );

        // 세로 스크롤은 필요할 때만 표시
        menuScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants
                .VERTICAL_SCROLLBAR_AS_NEEDED
        );

        // 세로 스크롤 속도
        menuScrollPane.getVerticalScrollBar()
                .setUnitIncrement(18);

        // 메인 화면에 메뉴 영역 추가
        mainPanel.add(menuScrollPane);
    }

    /**
     * DB에서 조회한 메뉴 목록 저장
     */
    public void setMenuList(
            List<MenuDTO> menuList) {

        // null인 경우 빈 목록으로 처리
        this.menuList =
                menuList == null
                        ? new ArrayList<>()
                        : new ArrayList<>(menuList);
    }

    /**
     * 저장된 메뉴 목록을 카드로 표시한다.
     */
    public void displayMenus() {

        // 이전 메뉴 카드 제거
        menuPanel.removeAll();

        // 조회한 메뉴 개수
        int menuCount = menuList.size();

        // 메뉴가 없다면 안내 표시
        if (menuCount == 0) {

            JLabel emptyLabel = new JLabel(
                    "등록된 메뉴가 없습니다.",
                    SwingConstants.CENTER
            );

            emptyLabel.setBounds(
                    0, 130, 674, 35
            );

            menuPanel.add(emptyLabel);

        } else {

            // DB 메뉴들을 하나씩 카드로 생성
            for (int i = 0; i < menuCount; i++) {

                // 현재 메뉴 정보
                MenuDTO menu = menuList.get(i);

                // 한 줄에 4개씩 배치
                int column = i % 4;

                // 네 개가 지나면 다음 행
                int row = i / 4;

                // 카드 가로 위치
                int x = column *
                        (CARD_WIDTH + CARD_GAP_X);

                // 카드 세로 위치
                int y = row *
                        (CARD_HEIGHT + CARD_GAP_Y);

                // 메뉴 카드 생성
                JPanel card = createMenuCard(menu);

                // 카드 위치 설정
                card.setBounds(
                        x,
                        y,
                        CARD_WIDTH,
                        CARD_HEIGHT
                );

                // 메뉴 패널에 카드 추가
                menuPanel.add(card);
            }
        }

        // 전체 행 개수 계산
        int rowCount =
                (menuCount + 3) / 4;

        // 스크롤 가능하도록 높이 계산
        int contentHeight = Math.max(
                378,
                rowCount *
                        (CARD_HEIGHT + CARD_GAP_Y)
        );

        // 메뉴 패널 크기 변경
        menuPanel.setPreferredSize(
                new Dimension(
                        674,
                        contentHeight
                )
        );

        // UI 갱신
        menuPanel.revalidate();
        menuPanel.repaint();
    }


    // =========================================
    // 9. 메뉴 카드 생성
    // =========================================

    /**
     * 화면설계서 기준 메뉴 카드 생성
     *
     * 흰색 카드 안에
     * 이미지 / 메뉴 이름 / 가격을 표시한다.
     */
    private JPanel createMenuCard(
            MenuDTO menu) {

        // 메뉴 카드 패널
        JPanel card = new JPanel(null);

        // 카드 흰색 배경
        card.setBackground(CARD_COLOR);


        // =====================================
        // 이미지 영역
        // =====================================

        // 이미지 표시 라벨
        JLabel imageLabel = new JLabel(
                "이미지",
                SwingConstants.CENTER
        );

        // 이미지 위치와 크기
        imageLabel.setBounds(
                5, 5, 145, 100
        );

        // 이미지 기본 배경
        imageLabel.setOpaque(true);
        imageLabel.setBackground(IMAGE_BG);

        // 기본 글씨 색상
        imageLabel.setForeground(
                new Color(125, 125, 125)
        );

        // 기본 글씨 크기
        imageLabel.setFont(
                new Font(
                        "맑은 고딕",
                        Font.PLAIN,
                        12
                )
        );

        // 이미지 영역 테두리
        imageLabel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(190, 190, 190)
                )
        );

        /*
         * DB의 THUMBNAIL_PATH를 가져온다.
         *
         * 예:
         * /images/americano_thumb.jpg
         */
        String imagePath =
                menu.getThumbnailPath();

        // 실제 이미지 파일 불러오기
        ImageIcon imageIcon =
                loadScaledIcon(
                        imagePath,
                        145,
                        100
                );

        // 실제 이미지 파일이 있다면 표시
        if (imageIcon != null) {

            // 기본 '이미지' 글씨 제거
            imageLabel.setText("");

            // 메뉴 사진 표시
            imageLabel.setIcon(imageIcon);
        }

        // 카드에 이미지 추가
        card.add(imageLabel);


        // =====================================
        // 메뉴 이름
        // =====================================

        // 메뉴명 표시
        JLabel nameLabel = new JLabel(
                menu.getMenuName()
        );

        // 메뉴명 위치
        nameLabel.setBounds(
                10, 108, 135, 22
        );

        // 메뉴명 폰트
        nameLabel.setFont(
                new Font(
                        "맑은 고딕",
                        Font.BOLD,
                        13
                )
        );

        // 메뉴명 색상
        nameLabel.setForeground(Color.BLACK);

        // 메뉴 이름 추가
        card.add(nameLabel);


        // =====================================
        // 메뉴 가격
        // =====================================

        // 가격 표시
        JLabel priceLabel = new JLabel(
                String.format(
                        "%,d원",
                        menu.getBasePrice()
                )
        );

        // 가격 위치
        priceLabel.setBounds(
                10, 147, 135, 24
        );

        // 가격 폰트
        priceLabel.setFont(
                new Font(
                        "맑은 고딕",
                        Font.BOLD,
                        14
                )
        );

        // 가격 색상
        priceLabel.setForeground(Color.BLACK);

        // 카드에 가격 추가
        card.add(priceLabel);


        // =====================================
        // 품절 여부 및 클릭 이벤트
        // =====================================

        // 품절된 메뉴인 경우
        if (menu.isSoldOut()) {

            // 가격 대신 품절 표시
            priceLabel.setText("품절");

            // 빨간색으로 표시
            priceLabel.setForeground(Color.RED);

            // 품절 상품은 클릭 불가능
            card.setCursor(
                    Cursor.getDefaultCursor()
            );

        } else if (menu.isOrderable()) {

            // 판매 중인 상품은 클릭 가능
            card.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            // 메뉴 클릭 이벤트 생성
            MouseAdapter clickListener =
                    new MouseAdapter() {

                @Override
                public void mouseClicked(
                        MouseEvent e) {

                    // 메뉴 고유번호 전달
                    menuEvt.selectMenu(
                            menu.getMenuId()
                    );
                }
            };

            // 카드 자체 클릭
            card.addMouseListener(
                    clickListener
            );

            // 이미지 클릭
            imageLabel.addMouseListener(
                    clickListener
            );

            // 메뉴 이름 클릭
            nameLabel.addMouseListener(
                    clickListener
            );

            // 가격 클릭
            priceLabel.addMouseListener(
                    clickListener
            );
        }

        // 완성된 메뉴 카드 반환
        return card;
    }


    // =========================================
    // 10. 이미지 로딩 공통 메서드
    // =========================================

    /**
     * Oracle DB에 저장된 이미지 경로를 이용해
     * 실제 JPG / PNG 파일을 불러온다.
     *
     * MenuDetailDialog에서도 사용할 수 있도록
     * public static으로 선언한다.
     *
     * 검색 순서:
     * 1. 클래스패스
     * 2. 프로젝트 루트
     * 3. src 폴더
     *
     * @param imagePath DB 이미지 경로
     * @param width 이미지 너비
     * @param height 이미지 높이
     * @return ImageIcon 또는 null
     */
    public static ImageIcon loadScaledIcon(
            String imagePath,
            int width,
            int height) {

        // 이미지 경로가 비어 있는 경우
        if (imagePath == null
                || imagePath.isBlank()) {

            return null;
        }

        // 파일 경로 정리
        String path = imagePath.trim()
                .replace('\\', '/');

        // 실제 절대경로 파일 확인
        File absoluteFile = new File(path);

        if (absoluteFile.isAbsolute()
                && absoluteFile.isFile()) {

            return scaleImage(
                    new ImageIcon(
                            absoluteFile.getAbsolutePath()
                    ),
                    width,
                    height
            );
        }

        // 상대경로로 변경
        String resourcePath = path;

        // 경로 앞쪽의 / 제거
        while (resourcePath.startsWith("/")) {

            resourcePath =
                    resourcePath.substring(1);
        }


        // =====================================
        // 1. 클래스패스에서 검색
        // =====================================

        URL resourceUrl = MenuForm.class
                .getClassLoader()
                .getResource(resourcePath);

        // 클래스패스에 이미지가 있는 경우
        if (resourceUrl != null) {

            ImageIcon icon =
                    scaleImage(
                            new ImageIcon(resourceUrl),
                            width,
                            height
                    );

            if (icon != null) {
                return icon;
            }
        }


        // =====================================
        // 2. 프로젝트 루트에서 검색
        // =====================================

        File projectFile =
                new File(resourcePath);

        // 프로젝트 루트에 파일이 있는 경우
        if (projectFile.isFile()) {

            ImageIcon icon =
                    scaleImage(
                            new ImageIcon(
                                    projectFile
                                    .getAbsolutePath()
                            ),
                            width,
                            height
                    );

            if (icon != null) {
                return icon;
            }
        }


        // =====================================
        // 3. src 폴더에서 검색
        // =====================================

        File srcFile = new File(
                "src",
                resourcePath
        );

        // src/images 폴더에 파일이 있는 경우
        if (srcFile.isFile()) {

            ImageIcon icon =
                    scaleImage(
                            new ImageIcon(
                                    srcFile.getAbsolutePath()
                            ),
                            width,
                            height
                    );

            if (icon != null) {
                return icon;
            }
        }

        // 이미지를 못 찾은 경우 콘솔 출력
        System.out.println(
                "[이미지 파일 없음] " + imagePath
        );

        // null을 반환하면 '이미지' 표시 유지
        return null;
    }


    // =========================================
    // 11. 이미지 크기 조정
    // =========================================

    /**
     * 불러온 이미지를 화면 크기에 맞춘다.
     */
    private static ImageIcon scaleImage(
            ImageIcon icon,
            int width,
            int height) {

        // 이미지 파일이 잘못된 경우
        if (icon == null
                || icon.getIconWidth() <= 0
                || icon.getIconHeight() <= 0) {

            return null;
        }

        // 이미지 크기 조절
        Image scaled = icon.getImage()
                .getScaledInstance(
                        width,
                        height,
                        Image.SCALE_SMOOTH
                );

        // 조절된 이미지 반환
        return new ImageIcon(scaled);
    }


    // =========================================
    // 12. 하단 장바구니 / 결제 영역
    // =========================================

    /**
     * 화면 하단에 장바구니 정보와
     * 결제 버튼을 생성한다.
     */
    private void createBottomArea() {

        // 하단 흰색 패널 생성
        JPanel bottomPanel = new JPanel(null);

        // 화면설계서 위치
        bottomPanel.setBounds(
                65, 495, 674, 40
        );

        // 흰색 배경
        bottomPanel.setBackground(Color.WHITE);


        // =====================================
        // 장바구니 요약
        // =====================================

        // 상품 개수 및 총금액
        cartSummaryLabel = new JLabel(
                "담은 상품 0개  |  합계 0원"
        );

        // 요약 문구 위치
        cartSummaryLabel.setBounds(
                16, 5, 460, 30
        );

        // 글씨 크기
        cartSummaryLabel.setFont(
                new Font(
                        "맑은 고딕",
                        Font.BOLD,
                        13
                )
        );

        // 하단 패널에 추가
        bottomPanel.add(cartSummaryLabel);


        // =====================================
        // 결제 버튼
        // =====================================

        // 결제 버튼 생성
        checkoutButton = new JButton("결제");

        // 화면설계서 위치
        checkoutButton.setBounds(
                518, 5, 142, 30
        );

        // 진회색 배경
        checkoutButton.setBackground(DARK_COLOR);

        // 흰색 글씨
        checkoutButton.setForeground(Color.WHITE);

        // 글씨 크기
        checkoutButton.setFont(
                new Font(
                        "맑은 고딕",
                        Font.BOLD,
                        13
                )
        );

        // 포커스 표시 제거
        checkoutButton.setFocusPainted(false);

        // 버튼 테두리 제거
        checkoutButton.setBorderPainted(false);

        // 마우스 커서
        checkoutButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        // 결제 버튼 클릭 이벤트
        checkoutButton.addActionListener(e -> {

            // MenuEvt의 결제 처리 메서드 호출
            menuEvt.clickCheckout();
        });

        // 결제 버튼 추가
        bottomPanel.add(checkoutButton);

        // 전체 화면에 하단 패널 추가
        mainPanel.add(bottomPanel);
    }


    // =========================================
    // 13. 메뉴 상세 화면 이동
    // =========================================

    /**
     * 메뉴를 클릭하면 상세정보 창을 연다.
     */
    public void openMenuDetail(long menuId) {

        // 메뉴 상세 화면 객체 생성
        MenuDetailDialog dialog =
                new MenuDetailDialog(
                        this,
                        menuId
                );

        // 상세 화면 표시
        dialog.setVisible(true);

        // 상세 창에서 돌아온 후 장바구니 갱신
        refreshCartSummary();
    }


    // =========================================
    // 14. 장바구니 데이터 연결
    // =========================================

    /**
     * 2번 담당자의 CartListService에서
     * 상품 개수와 총금액을 가져오도록 연결한다.
     */
    public static void setCartSummarySuppliers(
            IntSupplier countSupplier,
            IntSupplier totalSupplier) {

        // 상품 개수 조회 함수 저장
        cartCountSupplier =
                countSupplier == null
                        ? () -> 0
                        : countSupplier;

        // 총금액 조회 함수 저장
        cartTotalSupplier =
                totalSupplier == null
                        ? () -> 0
                        : totalSupplier;
    }

    /**
     * 장바구니 현재 상태 갱신
     */
    public void refreshCartSummary() {

        // 장바구니 상품 개수 조회
        int count =
                cartCountSupplier.getAsInt();

        // 장바구니 전체 금액 조회
        int total =
                cartTotalSupplier.getAsInt();

        // 화면 표시 갱신
        refreshCartSummary(count, total);
    }

    /**
     * 장바구니 개수와 합계 표시
     */
    public void refreshCartSummary(
            int count,
            int total) {

        // 화면설계서의 문구 형식 유지
        cartSummaryLabel.setText(
                String.format(
                        "담은 상품 %,d개  |  합계 %,d원",
                        count,
                        total
                )
        );
    }

    /**
     * 기존 코드와의 호환 메서드
     */
    public void displayCartSummary(
            int count,
            int total) {

        // 장바구니 정보를 화면에 반영
        refreshCartSummary(count, total);
    }


    // =========================================
    // 15. 결제 화면 연결
    // =========================================

    /**
     * 결제 화면을 여는 함수를 등록한다.
     */
    public static void setPaymentHandler(
            Runnable handler) {

        // 실제 결제 화면 연결 함수 저장
        paymentHandler = handler;
    }

    /**
     * 결제 화면을 연다.
     */
    public void openPaymentSelectForm() {

        // 결제 화면 연결이 된 경우
        if (paymentHandler != null) {

            // 3번 담당자의 화면 실행
            paymentHandler.run();

        } else {

            // 결제 화면이 아직 없는 경우
            JOptionPane.showMessageDialog(
                    this,
                    "결제 화면이 연결되지 않았습니다."
            );
        }
    }

    /**
     * 결제 버튼 반환
     */
    public JButton getCheckoutButton() {

        return checkoutButton;
    }


    // =========================================
    // 16. 기존 MenuEvt 호환 메서드
    // =========================================

    /**
     * 카테고리 제목 설정
     *
     * 이번 화면설계서는 별도의 제목 영역이
     * 없으므로 카테고리 버튼 상태만 갱신한다.
     */
    public void setCategoryTitle(String title) {

        // 전체 메뉴 선택
        if ("전체".equals(title)
                || "전체 메뉴".equals(title)) {

            selectedCategoryId = 0;

        } else {

            // 일치하는 카테고리 이름 검색
            for (CategoryDTO category : categoryList) {

                if (category.getCategoryName()
                        .equals(title)) {

                    selectedCategoryId =
                            category.getCategoryId();

                    break;
                }
            }
        }

        // 선택된 카테고리 버튼 색상 갱신
        updateCategoryButtons();
    }

    /**
     * 현재 선택 카테고리 번호 설정
     */
    public void setSelectedCategoryId(
            long categoryId) {

        selectedCategoryId = categoryId;

        // 선택 상태 반영
        updateCategoryButtons();
    }

    /**
     * 현재 선택 카테고리 번호 반환
     */
    public long getSelectedCategoryId() {

        return selectedCategoryId;
    }

    /**
     * 기존 썸네일 갱신 메서드
     */
    public void displayMenuThumbnails() {

        // 실제 카드 출력은 displayMenus에서 처리
        displayMenus();
    }
}
