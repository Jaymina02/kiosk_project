package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.evt.OrderCompleteEvt;

public class OrderCompleteForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private OrderCompleteEvt orderCompleteEvt;
    private OrderDTO order;
    private List<OrderDetailDTO> details;

    private JButton jbtnMain;

    public OrderCompleteForm(OrderDTO order, List<OrderDetailDTO> details) {
        super("카페 키오스크 - 주문 완료");
        this.order = order;
        this.details = details;

        initComponents();

        this.orderCompleteEvt = new OrderCompleteEvt(this);
        jbtnMain.addActionListener(orderCompleteEvt);

        setSize(804, 564);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        getContentPane().setBackground(new Color(166, 166, 166));
        setLayout(new GridBagLayout());

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(620, 420));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120), 1),
                BorderFactory.createEmptyBorder(35, 40, 35, 40)));

        JLabel lblTitle = new JLabel("주문이 완료되었습니다", SwingConstants.CENTER);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblOrderNoTitle = new JLabel("주문번호", SwingConstants.CENTER);
        lblOrderNoTitle.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        lblOrderNoTitle.setForeground(new Color(80, 80, 80));
        lblOrderNoTitle.setAlignmentX(CENTER_ALIGNMENT);

        String orderNoText = (order != null && order.getOrderNo() != null) ? order.getOrderNo() : "0001";
        JLabel lblOrderNo = new JLabel(orderNoText, SwingConstants.CENTER);
        lblOrderNo.setFont(new Font("맑은 고딕", Font.BOLD, 42));
        lblOrderNo.setForeground(Color.BLACK);
        lblOrderNo.setAlignmentX(CENTER_ALIGNMENT);

        int itemCount = (details != null) ? details.size() : 0;
        JLabel lblSubInfo = new JLabel("주문 내역 " + itemCount + "건 결제가 정상 처리되었습니다.", SwingConstants.CENTER);
        lblSubInfo.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        lblSubInfo.setForeground(new Color(60, 60, 60));
        lblSubInfo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblGuide = new JLabel("잠시 후 매장/포장 선택 화면으로 자동 이동합니다.", SwingConstants.CENTER);
        lblGuide.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        lblGuide.setForeground(new Color(100, 100, 100));
        lblGuide.setAlignmentX(CENTER_ALIGNMENT);

        //처음으로
        jbtnMain = new JButton("처음으로 (5초)");
        jbtnMain.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        jbtnMain.setBackground(new Color(217, 217, 217));
        jbtnMain.setForeground(Color.BLACK);
        jbtnMain.setFocusPainted(false);
        jbtnMain.setBorder(BorderFactory.createLineBorder(new Color(140, 140, 140), 1));
        jbtnMain.setMaximumSize(new Dimension(280, 60));
        jbtnMain.setPreferredSize(new Dimension(280, 60));
        jbtnMain.setAlignmentX(CENTER_ALIGNMENT);

        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(25));
        cardPanel.add(lblOrderNoTitle);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(lblOrderNo);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(lblSubInfo);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(lblGuide);
        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(jbtnMain);

        add(cardPanel);
    }

    public JButton getJbtnMain() {
        return jbtnMain;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public List<OrderDetailDTO> getDetails() {
        return details;
    }
}
