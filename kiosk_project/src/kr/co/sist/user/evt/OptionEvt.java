package kr.co.sist.user.evt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.service.CartListService;
import kr.co.sist.user.service.MenuService;
import kr.co.sist.user.view.OptionDialog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionEvt implements ActionListener {

	private OptionDialog optionDialog;
	private MenuService menuService;

	public OptionEvt(OptionDialog optionDialog) {
		this.optionDialog = optionDialog;
		this.menuService = new MenuService();
	}// OptionEvt

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		// 1. 수량 감소, 증가
		if (src == optionDialog.getBtnMinus()) {
			changeQuantity(-1);
		} else if (src == optionDialog.getBtnPlus()) {
			changeQuantity(1);
		}
		// 2. 취소 버튼
		else if (src == optionDialog.getBtnCancel()) {
			optionDialog.closeDialog();
		}
		// 3. 장바구니 담기 & 바로 주문 버튼
		else if (src == optionDialog.getBtnAddCart()) {
			addToCart();
		} else if (src == optionDialog.getBtnOrderNow()) {
			orderNow();
		}
		// 4. 추가 옵션(라디오, 체크박스) 클릭 시 합계 금액 갱신
		else {
			optionDialog.displayTotalAmount();
		}
	}// actionPerformed

	/**
	 * 수량을 +/- 버튼으로 변경하는 일
	 */
	public void changeQuantity(int quantity) {
		int currentQty = optionDialog.getQuantity();
		if (currentQty <= 0) {
			currentQty = 1;
		}

		int nextQty = currentQty + quantity;

		if (nextQty >= 1) {
			optionDialog.setQuantity(nextQty);
			optionDialog.displayQuantity();
			optionDialog.displayTotalAmount();
		}
	}// changeQuantity

	/**
	 * 메뉴 기본가격+추가옵션 가격의 총 합계 금액을 계산하는 일
	 */
	public int calculatePreviewTotal() {
		int basePrice = 0; // MenuDTO 연결 전 임시 기본 가격 및 초기화
		
		if (optionDialog.getSelectedMenu() != null) {
			basePrice = optionDialog.getSelectedMenu().getBasePrice();
		}
		
		int optionTotal = 0; // 추가 옵션 합계

		// 사이즈 추가금 (Large 선택 시 +500원)
		if (optionDialog.getRdoLarge().isSelected()) {
			optionTotal += 500;
		}

		// 추가 옵션 체크박스 금액 합산
		if (optionDialog.getChkVanilla().isSelected()) optionTotal += 500;
		if (optionDialog.getChkHazelnut().isSelected()) optionTotal += 500;
		if (optionDialog.getChkShot().isSelected()) optionTotal += 500;
		if (optionDialog.getChkSoyMilk().isSelected()) optionTotal += 500;
		if (optionDialog.getChkMilk().isSelected()) optionTotal += 500;
		if (optionDialog.getChkDecaf().isSelected()) optionTotal += 600;

		// (기본가격 + 옵션추가금) * 수량
		int qty = (optionDialog.getQuantity() <= 0) ? 1 : optionDialog.getQuantity();

		return (basePrice + optionTotal) * qty;
	}// calculatePreviewTotal

	/**
	 * 현재 화면에서 선택한 옵션과 수량을 CartDTO 객체 하나로 조립하는 일 
	 */
	public CartDTO buildCartItem() {
		CartDTO cart = new CartDTO();
		cart.setMenu(optionDialog.getSelectedMenu());

		// 1. 온도 옵션
		MenuOptionDTO tempDto = new MenuOptionDTO();
		tempDto.setOptionType("TEMPERATURE");
		tempDto.setOptionName(optionDialog.getRdoHot().isSelected() ? "HOT" : "ICE");
		cart.setTemperatureOption(tempDto);

		// 2. 사이즈 옵션
		MenuOptionDTO sizeDto = new MenuOptionDTO();
		sizeDto.setOptionType("SIZE");
		if (optionDialog.getRdoLarge().isSelected()) {
			sizeDto.setOptionName("Large");
			sizeDto.setOptionPrice(500);
		} else {
			sizeDto.setOptionName("Regular");
			sizeDto.setOptionPrice(0);
		}
		cart.setSizeOption(sizeDto);

		// 3. 추가 옵션 리스트
		List<MenuOptionDTO> extraList = new ArrayList<MenuOptionDTO>();
		if (optionDialog.getChkVanilla().isSelected()) extraList.add(createExtraOption("바닐라시럽", 500));
		if (optionDialog.getChkHazelnut().isSelected()) extraList.add(createExtraOption("헤이즐넛시럽", 500));
		if (optionDialog.getChkShot().isSelected()) extraList.add(createExtraOption("샷 추가", 500));
		if (optionDialog.getChkSoyMilk().isSelected()) extraList.add(createExtraOption("두유", 500));
		if (optionDialog.getChkMilk().isSelected()) extraList.add(createExtraOption("우유추가", 500));
		if (optionDialog.getChkDecaf().isSelected()) extraList.add(createExtraOption("디카페인 샷 추가", 600));
		cart.setExtraOptions(extraList);

		// 4. 수량 & 1잔 단가
		int qty = (optionDialog.getQuantity() <= 0) ? 1 : optionDialog.getQuantity();
		int unitPrice = calculatePreviewTotal() / qty;
		cart.setQuantity(qty);
		cart.setUnitPrice(unitPrice);

		return cart;
	}// buildCartItem

	private MenuOptionDTO createExtraOption(String name, int price) {
		MenuOptionDTO dto = new MenuOptionDTO();
		dto.setOptionType("EXTRA");
		dto.setOptionName(name);
		dto.setOptionPrice(price);
		return dto;
	}

	/**
	 * 장바구니 담기 버튼 클릭 시 실행 
	 */
	public void addToCart() {
		// 필수 옵션(온도, 사이즈) 선택 검사
		if (!optionDialog.getRdoHot().isSelected() && !optionDialog.getRdoIce().isSelected()) {
			JOptionPane.showMessageDialog(optionDialog, "온도(HOT/ICE)를 선택해주세요.", "필수 옵션 안내", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!optionDialog.getRdoRegular().isSelected() && !optionDialog.getRdoLarge().isSelected()) {
			JOptionPane.showMessageDialog(optionDialog, "사이즈(Regular/Large)를 선택해주세요.", "필수 옵션 안내", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 장바구니 저장소에 아이템 추가
		CartListService.clearDirectOrderItem();
		CartListService.addCartItem(buildCartItem());

		// 현재 옵션 창 닫고 장바구니 화면 열기
		optionDialog.closeDialog();
		optionDialog.openCartForm();
	}// addToCart

	/**
	 * 바로 주문 버튼 클릭 시 실행 
	 */
	public void orderNow() {
		// 필수 옵션(온도, 사이즈) 선택 검사
		if (!optionDialog.getRdoHot().isSelected() && !optionDialog.getRdoIce().isSelected()) {
			JOptionPane.showMessageDialog(optionDialog, "온도(HOT/ICE)를 선택해주세요.", "필수 옵션 안내", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!optionDialog.getRdoRegular().isSelected() && !optionDialog.getRdoLarge().isSelected()) {
			JOptionPane.showMessageDialog(optionDialog, "사이즈(Regular/Large)를 선택해주세요.", "필수 옵션 안내", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 바로 주문 전용 칸에 아이템 저장
		CartListService.setDirectOrderItem(buildCartItem());

		// 현재 옵션 창 닫고 결제 선택 화면 열기
		optionDialog.openPaymentSelectForm();
		
		
	}// orderNow

	// --- [기타 메서드] ---
	public void searchMenuOptions(long menuId) { }

	public String selectThumbnail(long menuId) {
		return "";
	}

}// class