package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.evt.CartEvt;
import kr.co.sist.user.service.CartListService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartForm extends JDialog {

	private CartEvt cartEvt;
	private List<CartDTO> cartItems;

	private JPanel pnlListContainer;
	private JLabel lblTotalCountTitle, lblTotalAmount;
	private JButton btnMoreMenu, btnOrder;

	public CartForm() {
		setTitle("장바구니");
		setModal(true);
		setSize(600, 520);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout(10, 10));

		// 1. 중앙 영역 (장바구니 목록 + 총 결제 금액)
		JPanel pnlCenter = new JPanel(new BorderLayout(10, 15));
		pnlCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

		pnlListContainer = new JPanel();
		pnlListContainer.setLayout(new BoxLayout(pnlListContainer, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane(pnlListContainer);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		pnlCenter.add(scrollPane, BorderLayout.CENTER);

		JPanel pnlSummary = new JPanel(new BorderLayout());
		pnlSummary.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		lblTotalCountTitle = new JLabel("총 결제 금액 (0개)");
		lblTotalAmount = new JLabel("0 원", SwingConstants.RIGHT);

		pnlSummary.add(lblTotalCountTitle, BorderLayout.WEST);
		pnlSummary.add(lblTotalAmount, BorderLayout.EAST);
		pnlCenter.add(pnlSummary, BorderLayout.SOUTH);

		add(pnlCenter, BorderLayout.CENTER);

		// 2. 최하단 버튼 영역
		JPanel pnlBottom = new JPanel(new GridLayout(1, 2, 20, 0));
		pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

		btnMoreMenu = new JButton("메뉴 더 담기");
		btnOrder = new JButton("주문하기");
		btnMoreMenu.setPreferredSize(new Dimension(0, 40));
		btnOrder.setPreferredSize(new Dimension(0, 40));

		pnlBottom.add(btnMoreMenu);
		pnlBottom.add(btnOrder);
		add(pnlBottom, BorderLayout.SOUTH);

		// 3. 이벤트 등록 및 초기 데이터 화면 출력
		this.cartEvt = new CartEvt(this);
		this.cartItems = CartListService.getCartItems();

		btnMoreMenu.addActionListener(cartEvt);
		btnOrder.addActionListener(cartEvt);

		displayCartItems();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public void displayCartItems() {
		this.cartItems = CartListService.getCartItems();
		pnlListContainer.removeAll();

		for (int i = 0; i < cartItems.size(); i++) {
			CartDTO item = cartItems.get(i);
			JPanel card = createItemCard(item, i);
			pnlListContainer.add(card);
			pnlListContainer.add(Box.createVerticalStrut(10));
		}

		displayTotalAmount();
		refreshCartView();
	}

	private JPanel createItemCard(CartDTO item, int index) {
		JPanel card = new JPanel(new BorderLayout(10, 5));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(10, 15, 10, 15)));

		// 상단: 메뉴명(온도, 사이즈) + 삭제 버튼
		JPanel pnlTop = new JPanel(new BorderLayout());
		String titleText = item.getMenuName();
		if (item.getTemperatureOption() != null && item.getSizeOption() != null) {
			titleText += "(" + item.getTemperatureOption().getOptionName() + ", " + item.getSizeOption().getOptionName()
					+ ")";
		}
		JLabel lblTitle = new JLabel(titleText);

		JButton btnDelete = new JButton("삭제");
		btnDelete.setMargin(new Insets(2, 8, 2, 8));
		btnDelete.setActionCommand("DELETE_" + index);
		btnDelete.addActionListener(cartEvt);

		pnlTop.add(lblTitle, BorderLayout.WEST);
		pnlTop.add(btnDelete, BorderLayout.EAST);
		card.add(pnlTop, BorderLayout.NORTH);

		// 중단: 선택된 추가 옵션 표시
		JLabel lblOptions = new JLabel(getExtraOptionText(item));
		lblOptions.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		card.add(lblOptions, BorderLayout.CENTER);

		// 하단: [-] 수량 [+] + 우측 금액
		JPanel pnlBottom = new JPanel(new BorderLayout());

		JPanel pnlQty = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		JButton btnMinus = new JButton("-");
		JLabel lblQty = new JLabel(String.valueOf(item.getQuantity()));
		JButton btnPlus = new JButton("+");

		btnMinus.setMargin(new Insets(1, 6, 1, 6));
		btnPlus.setMargin(new Insets(1, 6, 1, 6));

		btnMinus.setActionCommand("MINUS_" + index);
		btnPlus.setActionCommand("PLUS_" + index);
		btnMinus.addActionListener(cartEvt);
		btnPlus.addActionListener(cartEvt);

		pnlQty.add(btnMinus);
		pnlQty.add(lblQty);
		pnlQty.add(btnPlus);

		JLabel lblPrice = new JLabel(String.format("%,d 원", item.getLineAmount()), SwingConstants.RIGHT);

		pnlBottom.add(pnlQty, BorderLayout.WEST);
		pnlBottom.add(lblPrice, BorderLayout.EAST);
		card.add(pnlBottom, BorderLayout.SOUTH);

		return card;
	}

	public void displaySelectedOptions() {
	}// displaySelectedOptions

	private String getExtraOptionText(CartDTO item) {
		List<MenuOptionDTO> extras = item.getExtraOptions();
		if (extras == null || extras.isEmpty()) {
			return " ";
		}
		List<String> names = new ArrayList<>();
		for (MenuOptionDTO opt : extras) {
			names.add(opt.getOptionName());
		}
		return "- 옵션 : " + String.join(", ", names);
	}// getExtraOptionText

	public void displayTotalAmount() {
		int totalCount = CartListService.getCartItemCount();
		int totalPrice = CartListService.getCartTotalAmount();

		lblTotalCountTitle.setText("총 결제 금액 (" + totalCount + "개)");
		lblTotalAmount.setText(String.format("%,d 원", totalPrice));
	}// displayTotalAmount

	public void refreshCartView() {
		pnlListContainer.revalidate();
		pnlListContainer.repaint();
	}// refreshCartView

	public void openMenuForm() {
	    Window[] windows = Window.getWindows();
	    
	    for (int i = windows.length - 1; i >= 0; i--) {
	        Window w = windows[i];
	        String className = w.getClass().getSimpleName();
	        
	        // 1. MenuForm과 자바 내부 시스템 창(SharedOwnerFrame)은 절대 건드리지 않음!
	        if (!className.equals("MenuForm") && !className.contains("SharedOwnerFrame")) {
	            w.setVisible(false);
	            w.dispose();
	        }
	    }

	    SwingUtilities.invokeLater(() -> {
	        for (Window w : Window.getWindows()) {
	            if (w.getClass().getSimpleName().equals("MenuForm") && w.isDisplayable()) {
	                ((MenuForm) w).refreshCartSummary();
	                w.setVisible(true);
	                w.toFront();
	                return;
	            }
	        }
	        new MenuForm().setVisible(true);
	    });
	}// openMenuForm

public void openPaymentSelectForm() {
	 for (Window w : Window.getWindows()) {
	        String className = w.getClass().getSimpleName();
	        
	        if (className.equals("MenuForm")) {
	            w.setVisible(false);
	        } else if (!className.contains("SharedOwnerFrame")) {
	            w.setVisible(false);
	            w.dispose();
	        }
	    }

	    new PaymentSelectForm();
	}// openPaymentSelectForm
}