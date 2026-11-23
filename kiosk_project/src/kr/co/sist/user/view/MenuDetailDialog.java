
package kr.co.sist.user.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.evt.MenuDetailEvt;
import kr.co.sist.user.service.MenuService;

/**
 * 카페 키오스크 메뉴 상세화면
 *
 * 표시 정보
 * 1. 메뉴 이미지
 * 2. 메뉴명
 * 3. 가격
 * 4. 메뉴 설명
 * 5. 원산지
 * 6. 카페인 유무
 *
 * 옵션 선택은 2번 담당자 코드와 연동 예정
 */
public class MenuDetailDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // 옵션 선택 화면 연결용
    private static Consumer<MenuDTO> optionHandler;

    // 메뉴 서비스
    private final MenuService service =
            new MenuService();

    // 현재 선택한 메뉴
    private MenuDTO selectedMenu;

    // 버튼
    private final JButton optionButton =
            new JButton("옵션 선택");

    private final JButton closeButton =
            new JButton("닫기");

    /**
     * 메뉴 ID만 전달받는 생성자
     */
    public MenuDetailDialog(long menuId) {
        this((Frame) null, menuId);
    }

    /**
     * 메뉴 상세화면 생성자
     */
    public MenuDetailDialog(Frame owner, long menuId) {

        super(owner, "메뉴 상세", true);
        

        setSize(670, 485);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setLayout(null);
        getContentPane().setBackground(
                new Color(245, 245, 245)
        );

        // ====================================
        // 1. 메뉴 상세정보 조회
        // ====================================

        selectedMenu =
                service.searchMenuDetail(menuId);

        if (selectedMenu == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 메뉴 ID: " + menuId
            );
        }

        // ====================================
        // 2. 메뉴 상세 이미지
        // ====================================

        JLabel image = new JLabel(
                "이미지",
                SwingConstants.CENTER
        );

        image.setOpaque(true);
        image.setBackground(
                new Color(220, 220, 220)
        );
        image.setBounds(25, 25, 315, 340);

        ImageIcon icon = MenuForm.loadScaledIcon(
                selectedMenu.getDetailImagePath(),
                315,
                340
        );

        if (icon != null) {
            image.setIcon(icon);
            image.setText("");
        }

        add(image);

        // ====================================
        // 3. 메뉴 이름
        // ====================================

        JLabel name = new JLabel(
                selectedMenu.getMenuName()
        );

        name.setBounds(365, 40, 267, 40);
        name.setFont(
                new Font("맑은 고딕", Font.BOLD, 22)
        );

        add(name);

        // ====================================
        // 4. 메뉴 가격
        // ====================================

        JLabel price = new JLabel(
                NumberFormat
                        .getIntegerInstance(Locale.KOREA)
                        .format(selectedMenu.getBasePrice())
                        + "원"
        );

        price.setBounds(365, 93, 267, 32);
        price.setFont(
                new Font("맑은 고딕", Font.BOLD, 20)
        );

        add(price);

        // ====================================
        // 5. 메뉴 설명
        // ====================================

        JLabel desc = new JLabel(
                "<html><div style='width:240px'>"
                        + escapeHtml(
                                selectedMenu.getDescription()
                        )
                        + "</div></html>"
        );

        desc.setBounds(365, 148, 267, 75);
        desc.setFont(
                new Font("맑은 고딕", Font.PLAIN, 14)
        );

        add(desc);

        // ====================================
        // 6. 원산지 정보
        // ====================================

        JLabel origin = new JLabel(
                "원산지: "
                        + safe(selectedMenu.getOriginInfo())
        );

        origin.setBounds(365, 248, 270, 25);
        origin.setFont(
                new Font("맑은 고딕", Font.PLAIN, 12)
        );

        add(origin);

        // ====================================
        // 7. 카페인 유무 표시 (추가)
        // ====================================

        // DB의 CAFFEINE_YN 컬럼값 가져오기
        String caffeineYn =
                selectedMenu.getCaffeineYn();

        String caffeineText;

        if ("Y".equalsIgnoreCase(caffeineYn)) {

            caffeineText = "카페인 함유";

        } else if ("N".equalsIgnoreCase(caffeineYn)) {

            caffeineText = "카페인 없음";

        } else {

            caffeineText = "카페인 정보 없음";
        }

        // 카페인 안내 라벨 생성
        JLabel caffeineLabel = new JLabel(
                "카페인: " + caffeineText
        );

        // 원산지 아래 배치
        caffeineLabel.setBounds(
                365, 278, 270, 25
        );

        caffeineLabel.setFont(
                new Font("맑은 고딕", Font.PLAIN, 13)
        );

        add(caffeineLabel);

        // ====================================
        // 8. 옵션 선택 버튼
        // ====================================

        optionButton.setBounds(365, 326, 126, 45);
        optionButton.setFont(
                new Font("맑은 고딕", Font.BOLD, 15)
        );

        add(optionButton);

        // ====================================
        // 9. 닫기 버튼
        // ====================================

        closeButton.setBounds(505, 326, 126, 45);
        closeButton.setFont(
                new Font("맑은 고딕", Font.BOLD, 15)
        );

        add(closeButton);

        // ====================================
        // 10. 이벤트 연결
        // ====================================

        MenuDetailEvt evt =
                new MenuDetailEvt(this);

        optionButton.addActionListener(
                e -> evt.selectOption()
        );

        closeButton.addActionListener(
                e -> evt.closeDetail()
        );

        // 품절 메뉴는 옵션 선택 제한
        if (selectedMenu.getMenuStatus() != 0) {
            optionButton.setEnabled(false);
        }
    }

    /**
     * 2번 담당자의 옵션 선택 화면 연결
     */
    public static void setOptionHandler(
            Consumer<MenuDTO> handler) {

        optionHandler = handler;
    }

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
        dispose();
        optionHandler.accept(selectedMenu);
    }

    /**
     * 상세화면 닫기
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

    /**
     * HTML 특수문자 처리
     */
    private static String escapeHtml(String value) {

        return safe(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}