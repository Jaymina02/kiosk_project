package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.view.PaymentSelectForm.WireButton;

public class OrderCompleteForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private OrderDTO order;
    private List<OrderDetailDTO> details;

    public OrderCompleteForm(OrderDTO order, List<OrderDetailDTO> details) {
        super("주문 완료");
        this.order = order;
        this.details = details;

        JPanel framePanel = new JPanel(new BorderLayout());
        framePanel.setBackground(new Color(0xA6A6A6));
        framePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel cardPanel = new JPanel(new BorderLayout(0, 20));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));

        JLabel title = new JLabel("주문이 완료되었습니다.", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        cardPanel.add(title, BorderLayout.NORTH);

        String orderNo = (order != null) ? order.getOrderNo() : "-";
        int totalAmount = (order != null) ? order.getTotalAmount() : 0;
        int cnt = (details != null) ? details.size() : 0;
        DecimalFormat df = new DecimalFormat("#,###");

        JPanel infoBox = new JPanel(new GridLayout(3, 1, 0, 8));
        infoBox.setBackground(new Color(0xF0F0F0));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x7F7F7F)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblNoCaption = new JLabel("주문번호", SwingConstants.CENTER);
        lblNoCaption.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

        JLabel lblOrderNo = new JLabel(orderNo, SwingConstants.CENTER);
        lblOrderNo.setFont(new Font("맑은 고딕", Font.BOLD, 28));

        JLabel lblSub = new JLabel("주문 품목 " + cnt + "건  |  결제 금액 " + df.format(totalAmount) + "원", SwingConstants.CENTER);
        lblSub.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        infoBox.add(lblNoCaption);
        infoBox.add(lblOrderNo);
        infoBox.add(lblSub);
        cardPanel.add(infoBox, BorderLayout.CENTER);

        JButton btnClose = new WireButton("확인");
        btnClose.setPreferredSize(new Dimension(140, 44));
        btnClose.addActionListener(e -> dispose());

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(btnClose);
        cardPanel.add(south, BorderLayout.SOUTH);

        framePanel.add(cardPanel, BorderLayout.CENTER);
        setContentPane(framePanel);

        setSize(680, 460);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public OrderDTO getOrder() {
        return order;
    }

    public List<OrderDetailDTO> getDetails() {
        return details;
    }
}