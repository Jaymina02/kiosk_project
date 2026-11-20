package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
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
