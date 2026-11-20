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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.evt.OrderCompleteEvt;

public class OrderCompleteForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private OrderCompleteEvt orderCompleteEvt;
    private OrderDTO order;
    private List<OrderDetailDTO> details;

    private JLabel jlblOrderNo;
    private JLabel jlblOrderType;
    private JLabel jlblTotalAmount;
    private JTable jtOrderDetails;
    private DefaultTableModel dtmOrderDetails;
    private JButton jbtnMain;

    public OrderCompleteForm(OrderDTO order, List<OrderDetailDTO> details) {
        super("카페 키오스크 - 주문 완료");
        this.order = order;
        this.details = details;

        initComponents();
        displayOrderNumber();
        displayOrderDetails();

        this.orderCompleteEvt = new OrderCompleteEvt(this);
        jbtnMain.addActionListener(orderCompleteEvt);

        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(248, 250, 252));

        // 상단 헤더
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 41, 59));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("결제가 완료되었습니다");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        jlblOrderType = new JLabel("주문 유형: 매장", SwingConstants.RIGHT);
        jlblOrderType.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        jlblOrderType.setForeground(new Color(253, 224, 71));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(jlblOrderType, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 중앙 메인 영역 (좌: 주문번호 안내 카드 / 우: 주문 상세 내역)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // 좌측 주문번호 패널
        JPanel leftCard = new JPanel(new BorderLayout(0, 20));
        leftCard.setBackground(Color.WHITE);
        leftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(40, 30, 40, 30)));

        JLabel guideTopLabel = new JLabel("주문번호를 확인해 주세요", SwingConstants.CENTER);
        guideTopLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        guideTopLabel.setForeground(new Color(51, 65, 85));

        jlblOrderNo = new JLabel("A-0001", SwingConstants.CENTER);
        jlblOrderNo.setFont(new Font("맑은 고딕", Font.BOLD, 72));
        jlblOrderNo.setForeground(new Color(37, 99, 235));

        JLabel guideBottomLabel = new JLabel(
                "<html><div style='text-align:center;'>음료가 준비되면 호출 모니터에 주문번호가 표시됩니다.<br>영수증 및 주문번호를 꼭 확인해 주세요.</div></html>",
                SwingConstants.CENTER);
        guideBottomLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
        guideBottomLabel.setForeground(new Color(100, 116, 139));

        leftCard.add(guideTopLabel, BorderLayout.NORTH);
        leftCard.add(jlblOrderNo, BorderLayout.CENTER);
        leftCard.add(guideBottomLabel, BorderLayout.SOUTH);

        // 우측 주문 내역 패널
        JPanel rightCard = new JPanel(new BorderLayout(0, 15));
        rightCard.setBackground(Color.WHITE);
        rightCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        JLabel detailTitle = new JLabel("주문 상세 내역");
        detailTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        detailTitle.setForeground(new Color(30, 41, 59));

        String[] cols = { "메뉴명", "단가", "수량", "금액" };
        dtmOrderDetails = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jtOrderDetails = new JTable(dtmOrderDetails);
        jtOrderDetails.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        jtOrderDetails.setRowHeight(36);
        jtOrderDetails.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 16));
        jtOrderDetails.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane scrollPane = new JScrollPane(jtOrderDetails);

        jlblTotalAmount = new JLabel("총 결제금액: 0원", SwingConstants.RIGHT);
        jlblTotalAmount.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        jlblTotalAmount.setForeground(new Color(220, 38, 38));

        rightCard.add(detailTitle, BorderLayout.NORTH);
        rightCard.add(scrollPane, BorderLayout.CENTER);
        rightCard.add(jlblTotalAmount, BorderLayout.SOUTH);

        centerPanel.add(leftCard);
        centerPanel.add(rightCard);
        add(centerPanel, BorderLayout.CENTER);

        // 하단 버튼 영역
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        jbtnMain = new JButton("처음으로");
        jbtnMain.setPreferredSize(new Dimension(320, 68));
        jbtnMain.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        jbtnMain.setBackground(new Color(37, 99, 235));
        jbtnMain.setForeground(Color.WHITE);
        jbtnMain.setFocusPainted(false);

        bottomPanel.add(jbtnMain);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void displayOrderNumber() {
        DecimalFormat df = new DecimalFormat("#,###");
        if (order != null) {
            jlblOrderNo.setText(order.getOrderNo() != null ? order.getOrderNo() : "N/A");
            String typeText = (order.getOrderType() != null && !order.getOrderType().isBlank())
                    ? order.getOrderType()
                    : "매장";
            jlblOrderType.setText("주문 유형: " + typeText);
            jlblTotalAmount.setText("총 결제금액: " + df.format(order.getTotalAmount()) + "원");
        } else {
            jlblOrderNo.setText("N/A");
        }
    }

    private void displayOrderDetails() {
        dtmOrderDetails.setRowCount(0);
        if (details == null) {
            return;
        }
        DecimalFormat df = new DecimalFormat("#,###");
        for (OrderDetailDTO item : details) {
            dtmOrderDetails.addRow(new Object[] {
                    item.getMenuNameSnapshot(),
                    df.format(item.getUnitPrice()) + "원",
                    item.getQuantity() + "개",
                    df.format(item.getLineAmount()) + "원"
            });
        }
    }

    public OrderCompleteEvt getOrderCompleteEvt() {
        return orderCompleteEvt;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public List<OrderDetailDTO> getDetails() {
        return details;
    }

    public JButton getJbtnMain() {
        return jbtnMain;
    }
}