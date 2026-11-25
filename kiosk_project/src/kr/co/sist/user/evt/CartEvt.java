package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.service.CartListService;
import kr.co.sist.user.view.CartForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartEvt implements ActionListener {

	private CartForm cartForm;

	public CartEvt(CartForm cartForm) {
		this.cartForm = cartForm;
		
	}// CartEvt

	@Override
	public void actionPerformed(ActionEvent ae) {

		// 1. 하단 고정 버튼 ("메뉴 더 담기", "주문하기")
		if (ae.getSource() == cartForm.getBtnMoreMenu()) {
			addMoreMenu();
			return;
		}
		if (ae.getSource() == cartForm.getBtnOrder()) {
			proceedToPayment();
			return;
		}

		// 2. 동적으로 생성된 카드 내부 버튼 ("MINUS_0", "PLUS_0", "DELETE_0" 등)
		if (ae.getActionCommand() != null && ae.getActionCommand().contains("_")) {
			String[] parts = ae.getActionCommand().split("_");
			String action = parts[0];
			int index = Integer.parseInt(parts[1]);

			selectCartItem(index);

			if ("MINUS".equals(action)) {
				decreaseQuantity(index);
			} else if ("PLUS".equals(action)) {
				increaseQuantity(index);
			} else if ("DELETE".equals(action)) {
				removeCartItem(index);
			}
		}
	}// actionPerformed

	public void selectCartItem(int index) {
		// 필요 시 선택된 인덱스 로깅 또는 상태 처리
	}

	/**
	 * 해당 인덱스 메뉴의 수량 1 증가
	 */
	public void increaseQuantity(int index) {
		CartDTO item = CartListService.getCartItem(index);
		if (item != null) {
			CartListService.modifyQuantity(index, item.getQuantity() + 1);
			refreshCartView();
		}
	}// increaseQuantity

	/**
	 * 해당 인덱스 메뉴의 수량 1 감소 (최소 1개 유지)
	 */
	public void decreaseQuantity(int index) {
		CartDTO item = CartListService.getCartItem(index);
		if (item != null && item.getQuantity() > 1) {
			CartListService.modifyQuantity(index, item.getQuantity() - 1);
			refreshCartView();
		}
	}// decreaseQuantity

	/**
	 * 해당 인덱스 메뉴 장바구니에서 삭제
	 */
	public void removeCartItem(int index) {
		CartListService.removeCartItem(index);
		refreshCartView();
	}// removeCartItem

	/**
	 * 장바구니 화면 전체 다시 그리기
	 */
	public void refreshCartView() {
		cartForm.displayCartItems();
	}// refreshCartView

	/**
	 * 메뉴 더 담기 클릭 시 창 닫고 메뉴판으로 복귀
	 */
	public void addMoreMenu() {
		cartForm.openMenuForm();  
	}// addMoreMenu

	/**
	 * 주문하기 클릭 시 결제 선택 화면으로 이동
	 */
	public void proceedToPayment() {
		List<CartDTO> items = CartListService.getCartItems();
		if (items == null || items.isEmpty()) {
			JOptionPane.showMessageDialog(cartForm, "장바구니가 비어 있습니다. 메뉴 더 담기를 눌러 메뉴를 담아주세요.",
					"안내", JOptionPane.WARNING_MESSAGE);
			return;
		}
		cartForm.dispose();
		cartForm.openPaymentSelectForm();
	}// proceedToPayment

}// class