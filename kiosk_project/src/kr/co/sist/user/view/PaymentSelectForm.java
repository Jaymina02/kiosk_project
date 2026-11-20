package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
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
