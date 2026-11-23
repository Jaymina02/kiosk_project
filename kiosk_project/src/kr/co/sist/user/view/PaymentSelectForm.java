package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
<<<<<<< HEAD
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.evt.PaymentSelectEvt;
import kr.co.sist.user.service.CartListService;

public class PaymentSelectForm extends JFrame {
    private static final long serialVersionUID = 1L;

    // 화면설계서(PPT 와이어프레임) 공통 무채색 팔레트
    static final Color FRAME_GRAY = new Color(0xA6A6A6);
    static final Color BTN_GRAY = new Color(0xD9D9D9);
    static final Color BTN_HOVER = new Color(0xE6E6E6);
    static final Color BTN_PRESS = new Color(0xBFBFBF);
    static final Color LINE_GRAY = new Color(0x7F7F7F);
    static final Color DIVIDER = new Color(0xD0D0D0);
    static final Color PANEL_LIGHT = new Color(0xF0F0F0);
    static final Color ROW_ALT = new Color(0xE8E8E8);
    static final String FONT_NAME = "맑은 고딕";

    private PaymentSelectEvt paymentSelectEvt;
    private int selectedPaymentType = -1;

    private JTable jtCheckoutItems;
    private DefaultTableModel dtmCheckoutItems;
    private JLabel jlblOrderType;
    private JLabel jlblTotalAmount;
    private JButton jbtnCard;
    private JButton jbtnCash;
    private JButton jbtnBack;

    public PaymentSelectForm() {
        super("결제 방식 선택");
        initComponents();
        displayCheckoutItems();
        displayTotalAmount();

        this.paymentSelectEvt = new PaymentSelectEvt(this);
        jbtnCard.addActionListener(paymentSelectEvt);
        jbtnCash.addActionListener(paymentSelectEvt);
        jbtnBack.addActionListener(paymentSelectEvt);

        setSize(940, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        // 바깥 회색 프레임 + 내부 흰색 카드
        JPanel framePanel = new JPanel(new BorderLayout());
        framePanel.setBackground(FRAME_GRAY);
        framePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel cardPanel = new JPanel(new BorderLayout(0, 18));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));

        // 1. 상단 헤더 (제목 + 주문 구분 + 구분선)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                BorderFactory.createEmptyBorder(0, 0, 14, 0)));

        JLabel titleLabel = new JLabel("결제 방식 선택");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        String rawOrderType = CartListService.getOrderType();
        String orderTypeText = ("TAKE_OUT".equalsIgnoreCase(rawOrderType)
                || "TAKEOUT".equalsIgnoreCase(rawOrderType)
                || "포장".equals(rawOrderType)) ? "포장" : "매장";

        jlblOrderType = new JLabel("주문 구분 : " + orderTypeText, SwingConstants.CENTER);
        jlblOrderType.setFont(new Font(FONT_NAME, Font.BOLD, 15));
        jlblOrderType.setForeground(Color.BLACK);
        jlblOrderType.setOpaque(true);
        jlblOrderType.setBackground(PANEL_LIGHT);
        jlblOrderType.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(jlblOrderType, BorderLayout.EAST);
        cardPanel.add(topPanel, BorderLayout.NORTH);

        // 2. 중앙 영역 (좌측: 주문 내역 확인 / 우측: 결제 수단 선택)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        centerPanel.setOpaque(false);

        // 2-1. 좌측 주문 내역 패널
        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel listTitle = new JLabel("주문 내역 확인");
        listTitle.setFont(new Font(FONT_NAME, Font.BOLD, 17));
        listTitle.setForeground(Color.BLACK);
        leftPanel.add(listTitle, BorderLayout.NORTH);

        String[] cols = {"메뉴명", "선택 옵션", "수량", "금액"};
        dtmCheckoutItems = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jtCheckoutItems = new JTable(dtmCheckoutItems);
        jtCheckoutItems.setRowHeight(34);
        jtCheckoutItems.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
        jtCheckoutItems.setForeground(Color.BLACK);
        jtCheckoutItems.setGridColor(LINE_GRAY);
        jtCheckoutItems.setShowGrid(true);
        jtCheckoutItems.setIntercellSpacing(new Dimension(1, 1));
        jtCheckoutItems.setRowSelectionAllowed(false);

        JTableHeader header = jtCheckoutItems.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setBackground(BTN_GRAY);
                setForeground(Color.BLACK);
                setFont(new Font(FONT_NAME, Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createLineBorder(LINE_GRAY));
                return this;
            }
        });

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                setForeground(Color.BLACK);
                setHorizontalAlignment(column == 3 ? SwingConstants.RIGHT : SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        for (int i = 0; i < cols.length; i++) {
            jtCheckoutItems.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        jtCheckoutItems.getColumnModel().getColumn(0).setPreferredWidth(115);
        jtCheckoutItems.getColumnModel().getColumn(1).setPreferredWidth(145);
        jtCheckoutItems.getColumnModel().getColumn(2).setPreferredWidth(50);
        jtCheckoutItems.getColumnModel().getColumn(3).setPreferredWidth(85);

        JScrollPane scrollPane = new JScrollPane(jtCheckoutItems);
        scrollPane.setBorder(BorderFactory.createLineBorder(LINE_GRAY));
        scrollPane.getViewport().setBackground(Color.WHITE);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // 좌측 하단 총 결제 금액 박스
        JPanel totalBox = new JPanel(new BorderLayout());
        totalBox.setBackground(PANEL_LIGHT);
        totalBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel totalCaption = new JLabel("총 결제 금액");
        totalCaption.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        totalCaption.setForeground(Color.BLACK);

        jlblTotalAmount = new JLabel("0원", SwingConstants.RIGHT);
        jlblTotalAmount.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        jlblTotalAmount.setForeground(Color.BLACK);

        totalBox.add(totalCaption, BorderLayout.WEST);
        totalBox.add(jlblTotalAmount, BorderLayout.EAST);
        leftPanel.add(totalBox, BorderLayout.SOUTH);

        // 2-2. 우측 결제 수단 선택 패널
        JPanel rightPanel = new JPanel(new BorderLayout(0, 14));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(16, 18, 18, 18)));

        JLabel selectGuide = new JLabel("결제 수단을 선택해 주세요.");
        selectGuide.setFont(new Font(FONT_NAME, Font.BOLD, 17));
        selectGuide.setForeground(Color.BLACK);
        rightPanel.add(selectGuide, BorderLayout.NORTH);

        JPanel btnGroupPanel = new JPanel(new GridLayout(2, 1, 0, 18));
        btnGroupPanel.setOpaque(false);

        jbtnCard = new WireButton(
                "<html><center><b style='font-size:17px;'>카드 결제</b><br><br>"
                + "<span style='font-size:12px; color:#333333;'>신용 · 체크카드 / 삼성페이 / 애플페이</span></center></html>");
        jbtnCash = new WireButton(
                "<html><center><b style='font-size:17px;'>현금 결제</b><br><br>"
                + "<span style='font-size:12px; color:#333333;'>지폐 · 동전 투입 결제</span></center></html>");

        btnGroupPanel.add(jbtnCard);
        btnGroupPanel.add(jbtnCash);
        rightPanel.add(btnGroupPanel, BorderLayout.CENTER);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        cardPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. 하단 이전 화면 버튼 영역
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);

        jbtnBack = new WireButton("이전 화면");
        jbtnBack.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        jbtnBack.setPreferredSize(new Dimension(120, 40));

        bottomPanel.add(jbtnBack);
        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        framePanel.add(cardPanel, BorderLayout.CENTER);
        setContentPane(framePanel);
    }

    public void displayCheckoutItems() {
        dtmCheckoutItems.setRowCount(0);
        List<CartDTO> items = CartListService.getCheckoutItems();
        if (items == null) {
            return;
        }
        DecimalFormat df = new DecimalFormat("#,###");
        for (CartDTO item : items) {
            dtmCheckoutItems.addRow(new Object[]{
                    item.getMenuName(),
                    buildOptionSummary(item),
                    item.getQuantity() + "개",
                    df.format(item.getLineAmount()) + "원"
            });
        }
    }

    private String buildOptionSummary(CartDTO item) {
        List<String> names = new ArrayList<>();
        if (item.getTemperatureOption() != null) {
            names.add(item.getTemperatureOption().getOptionName());
        }
        if (item.getSizeOption() != null) {
            names.add(item.getSizeOption().getOptionName());
        }
        if (item.getExtraOptions() != null) {
            for (MenuOptionDTO opt : item.getExtraOptions()) {
                names.add(opt.getOptionName());
            }
        }
        return names.isEmpty() ? "기본" : String.join(", ", names);
    }

    public void displayTotalAmount() {
        int total = 0;
        List<CartDTO> items = CartListService.getCheckoutItems();
        if (items != null) {
            for (CartDTO item : items) {
                total += item.getLineAmount();
            }
        }
        DecimalFormat df = new DecimalFormat("#,###");
        jlblTotalAmount.setText(df.format(total) + "원");
    }

    public void openPaymentForm(int paymentType) {
        new PaymentForm(paymentType);
        dispose();
    }

    public void returnToPreviousForm() {
        dispose();
        if (CartListService.getCartItems() != null && !CartListService.getCartItems().isEmpty()) {
            new CartForm();
        }
    }

    public int getSelectedPaymentType() {
        return selectedPaymentType;
    }

    public void setSelectedPaymentType(int selectedPaymentType) {
        this.selectedPaymentType = selectedPaymentType;
    }

    public PaymentSelectEvt getPaymentSelectEvt() {
        return paymentSelectEvt;
    }

    public JButton getJbtnCard() {
        return jbtnCard;
    }

    public JButton getJbtnCash() {
        return jbtnCash;
    }

    public JButton getJbtnBack() {
        return jbtnBack;
    }

    /** 화면설계서 스타일 회색 둥근 버튼 */
    static class WireButton extends JButton {
        private static final long serialVersionUID = 1L;

        WireButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setFont(new Font(FONT_NAME, Font.BOLD, 15));
            setForeground(Color.BLACK);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(8, 16, 8, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = BTN_GRAY;
            if (getModel().isPressed()) {
                fill = BTN_PRESS;
            } else if (getModel().isRollover()) {
                fill = BTN_HOVER;
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(LINE_GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
=======
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.evt.PaymentSelectEvt;
import kr.co.sist.user.service.CartListService;

public class PaymentSelectForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private PaymentSelectEvt paymentSelectEvt;
    private int selectedPaymentType = -1;

    private JTable jtCheckoutItems;
    private DefaultTableModel dtmCheckoutItems;
    private JLabel jlblOrderType;
    private JLabel jlblTotalAmount;
    private JButton jbtnCard;
    private JButton jbtnCash;
    private JButton jbtnBack;

    public PaymentSelectForm() {
        super("카페 키오스크 - 결제 수단 선택");
        initComponents();
        displayCheckoutItems();
        displayTotalAmount();

        this.paymentSelectEvt = new PaymentSelectEvt(this);
        jbtnCard.addActionListener(paymentSelectEvt);
        jbtnCash.addActionListener(paymentSelectEvt);
        jbtnBack.addActionListener(paymentSelectEvt);

        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(248, 250, 252));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 41, 59));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("주문 내역 확인 및 결제 수단 선택");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        String rawOrderType = CartListService.getOrderType();
        String orderTypeText = ("TAKE_OUT".equalsIgnoreCase(rawOrderType)
                || "TAKEOUT".equalsIgnoreCase(rawOrderType)
                || "포장".equals(rawOrderType)) ? "포장" : "매장";
        jlblOrderType = new JLabel("주문 구분: " + orderTypeText);
        jlblOrderType.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        jlblOrderType.setForeground(new Color(250, 204, 21));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(jlblOrderType, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel listTitle = new JLabel("결제 상품 목록");
        listTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        leftPanel.add(listTitle, BorderLayout.NORTH);

        String[] cols = {"메뉴명", "선택 옵션", "수량", "금액"};
        dtmCheckoutItems = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jtCheckoutItems = new JTable(dtmCheckoutItems);
        jtCheckoutItems.setRowHeight(36);
        jtCheckoutItems.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        jtCheckoutItems.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 16));

        leftPanel.add(new JScrollPane(jtCheckoutItems), BorderLayout.CENTER);

        jlblTotalAmount = new JLabel("총 결제 금액: 0원", SwingConstants.RIGHT);
        jlblTotalAmount.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        jlblTotalAmount.setForeground(new Color(220, 38, 38));
        leftPanel.add(jlblTotalAmount, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        rightPanel.setOpaque(false);

        jbtnCard = new JButton("<html><center>신용 / 체크카드 결제<br><span style='font-size:16px;'>(IC칩 / 삼성페이 / 애플페이)</span></center></html>");
        jbtnCard.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        jbtnCard.setBackground(new Color(37, 99, 235));
        jbtnCard.setForeground(Color.WHITE);
        jbtnCard.setFocusPainted(false);

        jbtnCash = new JButton("<html><center>현금 결제<br><span style='font-size:16px;'>(지폐 / 동전 투입)</span></center></html>");
        jbtnCash.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        jbtnCash.setBackground(new Color(5, 150, 105));
        jbtnCash.setForeground(Color.WHITE);
        jbtnCash.setFocusPainted(false);

        rightPanel.add(jbtnCard);
        rightPanel.add(jbtnCash);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 15));
        bottomPanel.setOpaque(false);

        jbtnBack = new JButton("◀ 이전 화면으로");
        jbtnBack.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        jbtnBack.setPreferredSize(new Dimension(220, 55));
        jbtnBack.setBackground(new Color(100, 116, 139));
        jbtnBack.setForeground(Color.WHITE);
        jbtnBack.setFocusPainted(false);

        bottomPanel.add(jbtnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void displayCheckoutItems() {
        dtmCheckoutItems.setRowCount(0);
        List<CartDTO> items = CartListService.getCheckoutItems();
        if (items == null) {
            return;
        }
        DecimalFormat df = new DecimalFormat("#,###");
        for (CartDTO item : items) {
            String optionSummary = buildOptionSummary(item);
            dtmCheckoutItems.addRow(new Object[]{
                    item.getMenuName(),
                    optionSummary,
                    item.getQuantity() + "개",
                    df.format(item.getLineAmount()) + "원"
            });
        }
    }

    private String buildOptionSummary(CartDTO item) {
        List<String> names = new ArrayList<>();
        if (item.getTemperatureOption() != null) {
            names.add(item.getTemperatureOption().getOptionName());
        }
        if (item.getSizeOption() != null) {
            names.add(item.getSizeOption().getOptionName());
        }
        if (item.getExtraOptions() != null) {
            for (MenuOptionDTO opt : item.getExtraOptions()) {
                names.add(opt.getOptionName());
            }
        }
        return names.isEmpty() ? "기본" : String.join(", ", names);
    }

    public void displayTotalAmount() {
        int total = 0;
        List<CartDTO> items = CartListService.getCheckoutItems();
        if (items != null) {
            for (CartDTO item : items) {
                total += item.getLineAmount();
            }
        }
        DecimalFormat df = new DecimalFormat("#,###");
        jlblTotalAmount.setText("총 결제 금액: " + df.format(total) + "원");
    }

    public void openPaymentForm(int paymentType) {
        new PaymentForm(paymentType);
        dispose();
    }

    public void returnToPreviousForm() {
        dispose();
        if (CartListService.getCartItems() != null && !CartListService.getCartItems().isEmpty()) {
            new CartForm();
        }
    }

    public int getSelectedPaymentType() {
        return selectedPaymentType;
    }

    public void setSelectedPaymentType(int selectedPaymentType) {
        this.selectedPaymentType = selectedPaymentType;
    }

    public PaymentSelectEvt getPaymentSelectEvt() {
        return paymentSelectEvt;
    }

    public JButton getJbtnCard() {
        return jbtnCard;
    }

    public JButton getJbtnCash() {
        return jbtnCash;
    }

    public JButton getJbtnBack() {
        return jbtnBack;
    }
}
>>>>>>> refs/remotes/origin/lsj
