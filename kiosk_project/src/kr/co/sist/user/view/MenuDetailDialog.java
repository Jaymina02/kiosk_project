package kr.co.sist.user.view;

import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.evt.MenuDetailEvt;

import java.awt.*;
import java.util.function.Consumer;

import javax.swing.*;

/**
 * 선택한 상품의 상세정보를 보여주는 팝업.
 *
 * 상품 이미지, 메뉴명, 가격, 설명,
 * 카페인 유무, 원산지 등을 표시한다.
 */
public class MenuDetailDialog extends JDialog {

    // 현재 상세 조회 중인 메뉴 고유번호
    private final long menuId;

    // Oracle DB에서 가져온 메뉴 정보
    private MenuDTO selectedMenu;

    // 상세창의 이벤트 처리 객체
    private final MenuDetailEvt menuDetailEvt;

    // 상세 이미지 표시
    private final JLabel imageLabel =
            new JLabel(
                    "상품 이미지",
                    SwingConstants.CENTER
            );

    // 메뉴 이름 표시
    private final JLabel nameLabel =
            new JLabel("메뉴명");

    // 메뉴 가격 표시
    private final JLabel priceLabel =
            new JLabel("가격");

    // 상품 설명 표시
    private final JTextArea descriptionArea =
            new JTextArea();

    // 카페인 유무 표시
    private final JLabel caffeineLabel =
            new JLabel("-");

    // 원산지 표시
    private final JLabel originLabel =
            new JLabel("-");

    // 닫기 버튼
    private final JButton closeButton =
            new JButton("닫기");

    // 옵션 선택 버튼
    private final JButton optionButton =
            new JButton("옵션 선택");

    /*
     * TODO [2번 담당자 연결]
     *
     * 선택한 MenuDTO를 OptionDialog에 전달하는 함수.
     * 다른 팀원 코드가 아직 없으면 안내창을 표시한다.
     */
    private static Consumer<MenuDTO> optionHandler = null;

    /**
     * 옵션 화면 연결 함수를 등록하는 메서드.
     */
    public static void setOptionHandler(
            Consumer<MenuDTO> handler) {

        optionHandler = handler;
    }

    /**
     * 클래스 다이어그램의 생성자.
     */
    public MenuDetailDialog(long menuId) {
        this((Frame) null, menuId);
    }

    /**
     * MenuForm에서 사용하는 생성자.
     *
     * 부모 창을 지정해 팝업을 메뉴 화면 위에 표시한다.
     */
    public MenuDetailDialog(Frame owner, long menuId) {

        super(owner, "상품 상세", true);

        // 선택한 메뉴의 고유번호 저장
        this.menuId = menuId;

        // 버튼 이벤트 처리 객체 생성
        menuDetailEvt = new MenuDetailEvt(this);

        // 팝업 크기 및 위치 설정
        setSize(760, 560);
        setLocationRelativeTo(owner);

        // 팝업만 닫고 프로그램은 유지
        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        // 전체 레이아웃 설정
        setLayout(new BorderLayout());

        // 상단 제목 생성
        JLabel titleLabel = new JLabel(
                "상품 상세",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("맑은 고딕", Font.BOLD, 20)
        );

        titleLabel.setOpaque(true);

        titleLabel.setBackground(
                new Color(220, 220, 220)
        );

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 0, 15, 0
                )
        );

        add(titleLabel, BorderLayout.NORTH);

        // 중앙 상세정보 영역 생성
        JPanel body = new JPanel(
                new GridLayout(1, 2, 20, 0)
        );

        body.setBackground(Color.WHITE);

        body.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 25, 25, 25
                )
        );

        // 이미지 영역 설정
        imageLabel.setOpaque(true);

        imageLabel.setBackground(
                new Color(235, 235, 235)
        );

        body.add(imageLabel);

        /**
         * 이미지 클릭 시 확대 이미지를 표시한다.
         */
        imageLabel.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        // 이미지 클릭 이벤트 전달
                        menuDetailEvt.clickDetailImage();
                    }
                }
        );

        // 오른쪽 상세정보 패널
        JPanel infoPanel = new JPanel();

        infoPanel.setBackground(Color.WHITE);

        infoPanel.setLayout(
                new BoxLayout(
                        infoPanel,
                        BoxLayout.Y_AXIS
                )
        );

        // 메뉴명 글꼴 설정
        nameLabel.setFont(
                new Font("맑은 고딕", Font.BOLD, 22)
        );

        infoPanel.add(nameLabel);

        infoPanel.add(
                Box.createVerticalStrut(20)
        );

        // 가격 글꼴 설정
        priceLabel.setFont(
                new Font("맑은 고딕", Font.BOLD, 19)
        );

        infoPanel.add(priceLabel);

        infoPanel.add(
                Box.createVerticalStrut(20)
        );

        // 상품 설명 제목
        infoPanel.add(new JLabel("상품 설명"));

        // 상품 설명을 여러 줄로 표시
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // 고객이 설명을 수정하지 못하도록 설정
        descriptionArea.setEditable(false);

        descriptionArea.setFont(
                new Font("맑은 고딕", Font.PLAIN, 13)
        );

        // 설명이 길면 스크롤 가능
        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        descriptionScroll.setPreferredSize(
                new Dimension(300, 100)
        );

        infoPanel.add(descriptionScroll);

        infoPanel.add(
                Box.createVerticalStrut(15)
        );

        // 카페인 유무 표시
        infoPanel.add(new JLabel("카페인 유무"));

        infoPanel.add(caffeineLabel);

        infoPanel.add(
                Box.createVerticalStrut(10)
        );

        // 원산지 표시
        infoPanel.add(new JLabel("원산지"));

        infoPanel.add(originLabel);

        body.add(infoPanel);

        add(body, BorderLayout.CENTER);

        // 하단 닫기·옵션 선택 버튼 영역
        JPanel bottomPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER, 25, 15
                )
        );

        bottomPanel.setBackground(Color.WHITE);

        // 버튼 디자인과 이벤트 적용
        for (JButton button :
                new JButton[]{closeButton, optionButton}) {

            button.setPreferredSize(
                    new Dimension(170, 42)
            );

            button.setFont(
                    new Font("맑은 고딕", Font.BOLD, 14)
            );

            // 버튼 클릭 이벤트 등록
            button.addActionListener(menuDetailEvt);

            bottomPanel.add(button);
        }

        add(bottomPanel, BorderLayout.SOUTH);

        // 상품의 최신 상세정보를 Oracle DB에서 조회
        menuDetailEvt.searchMenuDetail(menuId);
    }

    /**
     * DB에서 조회한 메뉴 정보를 상세창에 표시한다.
     */
    public void displayMenuDetail() {

        // 조회한 메뉴가 없으면 화면 갱신 중단
        if (selectedMenu == null) {
            return;
        }

        // 메뉴 이름 표시
        nameLabel.setText(
                selectedMenu.getMenuName()
        );

        // 기본가격 표시
        priceLabel.setText(
                String.format(
                        "%,d원",
                        selectedMenu.getBasePrice()
                )
        );

        // 메뉴 설명 표시
        String description =
                selectedMenu.getDescription();

        descriptionArea.setText(
                description == null ? "" : description
        );

        // 설명 스크롤을 위쪽으로 이동
        descriptionArea.setCaretPosition(0);

        // 카페인 유무를 Y/N에서 한글로 변환
        caffeineLabel.setText(
                "Y".equalsIgnoreCase(
                        selectedMenu.getCaffeineYn()
                )
                        ? "있음"
                        : "없음"
        );

        // 원산지 표시
        String origin = selectedMenu.getOriginInfo();

        originLabel.setText(
                origin == null || origin.isBlank()
                        ? "정보 없음"
                        : origin
        );

        // DB 상세 이미지 경로로 JPG/PNG 로딩
        ImageIcon detailImage =
                MenuForm.loadScaledIcon(
                        selectedMenu.getDetailImagePath(),
                        320,
                        340
                );

        // 이미지가 존재하면 실제 사진 표시
        if (detailImage != null) {

            imageLabel.setText("");
            imageLabel.setIcon(detailImage);

        } else {

            // 이미지 파일을 찾을 수 없을 때
            imageLabel.setText("상품 이미지 없음");
            imageLabel.setIcon(null);
        }

        // 판매중인 상품만 옵션 선택 가능
        optionButton.setEnabled(
                selectedMenu.isOrderable()
        );
    }

    /**
     * 상세 이미지를 클릭하면 확대해서 표시한다.
     */
    public void showDetailImage() {

        // 상품이 없다면 처리하지 않음
        if (selectedMenu == null) {
            return;
        }

        // 상세 이미지의 큰 버전을 읽는다
        ImageIcon largeImage =
                MenuForm.loadScaledIcon(
                        selectedMenu.getDetailImagePath(),
                        500,
                        500
                );

        // 이미지가 있으면 팝업 표시
        if (largeImage != null) {

            JOptionPane.showMessageDialog(
                    this,
                    new JLabel(largeImage),
                    "상품 상세 이미지",
                    JOptionPane.PLAIN_MESSAGE
            );

        } else {

            // 이미지가 없다면 안내
            JOptionPane.showMessageDialog(
                    this,
                    "이미지 파일을 찾을 수 없습니다."
            );
        }
    }

    /**
     * 2번 담당자의 옵션 선택 화면 연결.
     */
    public void openOptionDialog() {

        // 상품이 없거나 주문 불가능하면 종료
        if (selectedMenu == null
                || !selectedMenu.isOrderable()) {
            return;
        }

        // 2번 담당자의 연결 함수가 등록된 경우
        if (optionHandler != null) {

            // 현재 선택한 MenuDTO를 옵션 화면에 전달
            optionHandler.accept(selectedMenu);

        } else {

            // 2번 담당자 코드 통합 전 안내
            JOptionPane.showMessageDialog(
                    this,
                    "2번 담당 OptionDialog 연결 예정: "
                            + selectedMenu.getMenuName()
            );
        }
    }

    /**
     * 상품 상세 팝업 닫기.
     */
    public void closeDialog() {
        dispose();
    }

    // 메뉴 고유번호 Getter
    public long getMenuId() {
        return menuId;
    }

    // 현재 조회한 MenuDTO Getter
    public MenuDTO getSelectedMenu() {
        return selectedMenu;
    }

    /**
     * DB에서 조회한 상품 저장 후 화면 갱신.
     */
    public void setSelectedMenu(MenuDTO selectedMenu) {

        this.selectedMenu = selectedMenu;

        // UI에 새 상품 정보 표시
        displayMenuDetail();
    }

    // 닫기 버튼 Getter
    public JButton getCloseButton() {
        return closeButton;
    }

    // 옵션 선택 버튼 Getter
    public JButton getOptionButton() {
        return optionButton;
    }
}