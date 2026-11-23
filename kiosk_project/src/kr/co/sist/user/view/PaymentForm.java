package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.evt.PaymentEvt;
import kr.co.sist.user.service.CartListService;
import kr.co.sist.user.view.PaymentSelectForm.WireButton;

public class PaymentForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color FRAME_GRAY = new Color(0xA6A6A6);
    private static final Color LINE_GRAY = new Color(0x7F7F7F);
    private static final Color DIVIDER = new Color(0xD0D0D0);
    private static final Color PANEL_LIGHT = new Color(0xF0F0F0);
    private static final String FONT_NAME = "맑은 고딕";

    private PaymentEvt paymentEvt;
    private int paymentType;

    private JTextArea jtaCheckoutSummary;
    private JLabel jlblPaymentGuide;
    private JLabel jlblTotalAmount;
    private JButton jbtnPay;
    private JButton jbtnBack;

    public PaymentForm(int paymentType) {
        super("결제 진행");
        this.paymentType = paymentType;

        initComponents();
        displayCheckoutItems();
        displayPaymentGuide();

        this.paymentEvt = new PaymentEvt(this);
        jbtnPay.addActionListener(paymentEvt);
        jbtnBack.addActionListener(paymentEvt);

        setSize(940, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        JPanel framePanel = new JPanel(new BorderLayout());
        framePanel.setBackground(FRAME_GRAY);
        framePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel cardPanel = new JPanel(new BorderLayout(0, 18));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));

        // 1. 상단 헤더
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                BorderFactory.createEmptyBorder(0, 0, 14, 0)));

        String methodTitle = (paymentType == 0) ? "카드 결제" : "현금 결제";
        JLabel titleLabel = new JLabel(methodTitle);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        String rawOrderType = CartListService.getOrderType();
        String orderTypeText = ("TAKE_OUT".equalsIgnoreCase(rawOrderType)
                || "TAKEOUT".equalsIgnoreCase(rawOrderType)
                || "포장".equals(rawOrderType)) ? "포장" : "매장";

        JLabel orderTypeLabel = new JLabel("주문 구분 : " + orderTypeText, SwingConstants.CENTER);
        orderTypeLabel.setFont(new Font(FONT_NAME, Font.BOLD, 15));
        orderTypeLabel.setForeground(Color.BLACK);
        orderTypeLabel.setOpaque(true);
        orderTypeLabel.setBackground(PANEL_LIGHT);
        orderTypeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(orderTypeLabel, BorderLayout.EAST);
        cardPanel.add(topPanel, BorderLayout.NORTH);

        // 2. 중앙 영역 (좌측: 최종 주문 내역 / 우측: 결제 안내 + 결제 승인 버튼)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        centerPanel.setOpaque(false);

        // 2-1. 좌측 주문 내역 패널
        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel summaryTitle = new JLabel("최종 주문 내역");
        summaryTitle.setFont(new Font(FONT_NAME, Font.BOLD, 17));
        summaryTitle.setForeground(Color.BLACK);
        leftPanel.add(summaryTitle, BorderLayout.NORTH);

        jtaCheckoutSummary = new JTextArea();
        jtaCheckoutSummary.setEditable(false);
        jtaCheckoutSummary.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        jtaCheckoutSummary.setForeground(Color.BLACK);
        jtaCheckoutSummary.setBackground(Color.WHITE);
        jtaCheckoutSummary.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scrollPane = new JScrollPane(jtaCheckoutSummary);
        scrollPane.setBorder(BorderFactory.createLineBorder(LINE_GRAY));
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel totalBox = new JPanel(new BorderLayout());
        totalBox.setBackground(PANEL_LIGHT);
        totalBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel totalCaption = new JLabel("결제 금액");
        totalCaption.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        totalCaption.setForeground(Color.BLACK);

        jlblTotalAmount = new JLabel("0원", SwingConstants.RIGHT);
        jlblTotalAmount.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        jlblTotalAmount.setForeground(Color.BLACK);

        totalBox.add(totalCaption, BorderLayout.WEST);
        totalBox.add(jlblTotalAmount, BorderLayout.EAST);
        leftPanel.add(totalBox, BorderLayout.SOUTH);

        // 2-2. 우측 결제 안내 패널
        JPanel rightPanel = new JPanel(new BorderLayout(0, 16));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(16, 18, 18, 18)));

        JLabel guideHeader = new JLabel("결제 안내");
        guideHeader.setFont(new Font(FONT_NAME, Font.BOLD, 17));
        guideHeader.setForeground(Color.BLACK);
        rightPanel.add(guideHeader, BorderLayout.NORTH);

        JPanel guideBox = new JPanel(new BorderLayout());
        guideBox.setBackground(PANEL_LIGHT);
        guideBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_GRAY),
                BorderFactory.createEmptyBorder(20, 16, 20, 16)));

        jlblPaymentGuide = new JLabel("", SwingConstants.CENTER);
        jlblPaymentGuide.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        jlblPaymentGuide.setForeground(Color.BLACK);
        guideBox.add(jlblPaymentGuide, BorderLayout.CENTER);
        rightPanel.add(guideBox, BorderLayout.CENTER);

        jbtnPay = new WireButton("결제 승인");
        jbtnPay.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        jbtnPay.setPreferredSize(new Dimension(0, 56));
        rightPanel.add(jbtnPay, BorderLayout.SOUTH);

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
        StringBuilder sb = new StringBuilder();
        List<CartDTO> items = CartListService.getCheckoutItems();
        DecimalFormat df = new DecimalFormat("#,###");
        int total = 0;

        if (items != null) {
            for (CartDTO item : items) {
                sb.append("• ").append(item.getMenuName());
                String opts = buildOptionSummary(item);
                if (!opts.isEmpty()) {
                    sb.append(" (").append(opts).append(")");
                }
                sb.append("\n   수량: ").append(item.getQuantity()).append("개")
                  .append("  |  금액: ").append(df.format(item.getLineAmount())).append("원\n\n");
                total += item.getLineAmount();
            }
        }
        jtaCheckoutSummary.setText(sb.toString());
        jtaCheckoutSummary.setCaretPosition(0);
        jlblTotalAmount.setText(df.format(total) + "원");
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
        return String.join(", ", names);
    }

    public void displayPaymentGuide() {
        if (paymentType == 0) {
            jlblPaymentGuide.setText(
                    "<html><center><b>하단 카드 리더기에<br>IC 카드를 끝까지 삽입해 주세요.</b><br><br>"
                    + "<span style='font-size:13px; color:#333333;'>결제가 완료될 때까지 카드를 빼지 마세요.<br>"
                    + "투입 후 아래 [결제 승인] 버튼을 눌러주세요.</span></center></html>");
        } else {
            jlblPaymentGuide.setText(
                    "<html><center><b>우측 현금 투입구에<br>지폐 또는 동전을 투입해 주세요.</b><br><br>"
                    + "<span style='font-size:13px; color:#333333;'>금액 확인 후 아래 [결제 승인] 버튼을 눌러주세요.</span></center></html>");
        }
    }

    public void openOrderCompleteForm(OrderDTO order, List<OrderDetailDTO> details) {
        new OrderCompleteForm(order, details);
        dispose();
    }

    public void returnToPaymentSelect() {
        new PaymentSelectForm();
        dispose();
    }

    public int getPaymentType() {
        return paymentType;
    }

    public PaymentEvt getPaymentEvt() {
        return paymentEvt;
    }

    public JButton getJbtnPay() {
        return jbtnPay;
    }

    public JButton getJbtnBack() {
        return jbtnBack;
    }
}
=======
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.evt.PaymentEvt;
import kr.co.sist.user.service.CartListService;

public class PaymentForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private PaymentEvt paymentEvt;
    private int paymentType;

    private JTextArea jtaCheckoutSummary;
    private JLabel jlblPaymentGuide;
    private JLabel jlblTotalAmount;
    private JButton jbtnPay;
    private JButton jbtnBack;

    public PaymentForm(int paymentType) {
        super("카페 키오스크 - 결제 진행");
        this.paymentType = paymentType;

        initComponents();
        displayCheckoutItems();
        displayPaymentGuide();

        this.paymentEvt = new PaymentEvt(this);
        jbtnPay.addActionListener(paymentEvt);
        jbtnBack.addActionListener(paymentEvt);

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

        String methodTitle = (paymentType == 0) ? "신용 / 체크카드 결제" : "현금 결제";
        JLabel titleLabel = new JLabel(methodTitle + " 진행");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        topPanel.add(titleLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel summaryTitle = new JLabel("최종 결제 내역 요약");
        summaryTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        leftPanel.add(summaryTitle, BorderLayout.NORTH);

        jtaCheckoutSummary = new JTextArea();
        jtaCheckoutSummary.setEditable(false);
        jtaCheckoutSummary.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        leftPanel.add(new JScrollPane(jtaCheckoutSummary), BorderLayout.CENTER);

        jlblTotalAmount = new JLabel("결제 금액: 0원", SwingConstants.RIGHT);
        jlblTotalAmount.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        jlblTotalAmount.setForeground(new Color(220, 38, 38));
        leftPanel.add(jlblTotalAmount, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        jlblPaymentGuide = new JLabel("", SwingConstants.CENTER);
        jlblPaymentGuide.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        rightPanel.add(jlblPaymentGuide, BorderLayout.CENTER);

        jbtnPay = new JButton("결제 승인하기");
        jbtnPay.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        jbtnPay.setPreferredSize(new Dimension(0, 85));
        jbtnPay.setBackground(new Color(37, 99, 235));
        jbtnPay.setForeground(Color.WHITE);
        jbtnPay.setFocusPainted(false);
        rightPanel.add(jbtnPay, BorderLayout.SOUTH);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 15));
        bottomPanel.setOpaque(false);

        jbtnBack = new JButton("◀ 결제수단 다시 선택");
        jbtnBack.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        jbtnBack.setPreferredSize(new Dimension(250, 55));
        jbtnBack.setBackground(new Color(100, 116, 139));
        jbtnBack.setForeground(Color.WHITE);
        jbtnBack.setFocusPainted(false);

        bottomPanel.add(jbtnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void displayCheckoutItems() {
        StringBuilder sb = new StringBuilder();
        List<CartDTO> items = CartListService.getCheckoutItems();
        DecimalFormat df = new DecimalFormat("#,###");
        int total = 0;

        if (items != null) {
            for (CartDTO item : items) {
                sb.append("• ").append(item.getMenuName())
                  .append("  x ").append(item.getQuantity())
                  .append("  =  ").append(df.format(item.getLineAmount())).append("원\n");
                total += item.getLineAmount();
            }
        }
        jtaCheckoutSummary.setText(sb.toString());
        jlblTotalAmount.setText("결제 금액: " + df.format(total) + "원");
    }

    public void displayPaymentGuide() {
        if (paymentType == 0) {
            jlblPaymentGuide.setText("<html><center>하단 카드 리더기에<br><span style='color:#2563EB;'>IC 카드를 끝까지</span> 넣어주세요.<br><br><span style='font-size:16px; color:#64748B;'>결제가 완료될 때까지 카드를 빼지 마세요.</span></center></html>");
        } else {
            jlblPaymentGuide.setText("<html><center>우측 현금 투입구에<br><span style='color:#059669;'>지폐 또는 동전</span>을 투입해 주세요.<br><br><span style='font-size:16px; color:#64748B;'>투입 후 아래 [결제 승인하기] 버튼을 눌러주세요.</span></center></html>");
        }
    }

    public void openOrderCompleteForm(OrderDTO order, List<OrderDetailDTO> details) {
        new OrderCompleteForm(order, details);
        dispose();
    }

    public void returnToPaymentSelect() {
        new PaymentSelectForm();
        dispose();
    }

    public int getPaymentType() {
        return paymentType;
    }

    public PaymentEvt getPaymentEvt() {
        return paymentEvt;
    }

    public JButton getJbtnPay() {
        return jbtnPay;
    }

    public JButton getJbtnBack() {
        return jbtnBack;
    }
}
>>>>>>> refs/remotes/origin/lsj
