package kr.co.sist.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;


public class CartListService {

	private static List<CartDTO> cartList = new ArrayList<>();
	private static String orderType = "EAT_IN"; // 기본값 (매장/포장)
	private static CartDTO directOrderItem = null;

	// 장바구니에 메뉴 추가 (동일 메뉴+동일 옵션이면 수량만 합산)
	public static void addCartItem(CartDTO item) {
		if (item == null) return;

		int sameIdx = findSameItemIndex(item);
		if (sameIdx != -1) {
			CartDTO existing = cartList.get(sameIdx);
			existing.setQuantity(existing.getQuantity() + item.getQuantity());
		} else {
			cartList.add(item);
		}
	}//addCartItem

	// 동일한 메뉴 + 동일 옵션 조합이 이미 장바구니에 있는지 인덱스 찾기
	public static int findSameItemIndex(CartDTO item) {
		for (int i = 0; i < cartList.size(); i++) {
			CartDTO cur = cartList.get(i);
			if (isSameMenuAndOption(cur, item)) {
				return i;
			}
		}
		return -1;
	}//findSameItemIndex

	private static boolean isSameMenuAndOption(CartDTO a, CartDTO b) {
		// 1. 메뉴명 비교
		if (!Objects.equals(a.getMenuName(), b.getMenuName())) return false;

		// 2. 온도 옵션 비교
		String tempA = (a.getTemperatureOption() != null) ? a.getTemperatureOption().getOptionName() : null;
		String tempB = (b.getTemperatureOption() != null) ? b.getTemperatureOption().getOptionName() : null;
		if (!Objects.equals(tempA, tempB)) return false;

		// 3. 사이즈 옵션 비교
		String sizeA = (a.getSizeOption() != null) ? a.getSizeOption().getOptionName() : null;
		String sizeB = (b.getSizeOption() != null) ? b.getSizeOption().getOptionName() : null;
		if (!Objects.equals(sizeA, sizeB)) return false;

		// 4. 추가 옵션 리스트 비교
		List<MenuOptionDTO> extA = a.getExtraOptions();
		List<MenuOptionDTO> extB = b.getExtraOptions();
		int countA = (extA != null) ? extA.size() : 0;
		int countB = (extB != null) ? extB.size() : 0;
		if (countA != countB) return false;

		if (countA > 0) {
			List<String> namesA = new ArrayList<>();
			List<String> namesB = new ArrayList<>();
			for (MenuOptionDTO opt : extA) namesA.add(opt.getOptionName());
			for (MenuOptionDTO opt : extB) namesB.add(opt.getOptionName());
			if (!namesA.containsAll(namesB)) return false;
		}
		return true;
	}//isSameMenuAndOption

	public static void removeCartItem(int index) {
		if (index >= 0 && index < cartList.size()) {
			cartList.remove(index);
		}
	}//removeCartItem

	public static void modifyQuantity(int index, int quantity) {
		if (index >= 0 && index < cartList.size() && quantity >= 1) {
			cartList.get(index).setQuantity(quantity);
		}
	}//modifyQuantity

	public static List<CartDTO> getCartItems() {
		return cartList;
	}//getCartItems

	public static CartDTO getCartItem(int index) {
		if (index >= 0 && index < cartList.size()) {
			return cartList.get(index);
		}
		return null;
	}//getCartItem(int index)

	// 장바구니에 담긴 총 잔(개) 수 합계
	public static int getCartItemCount() {
		int count = 0;
		for (CartDTO item : cartList) {
			count += item.getQuantity();
		}
		return count;
	}//getCartItemCount

	public static int calculateTotal() {
		int total = 0;
		for (CartDTO item : cartList) {
			total += item.getLineAmount();
		}
		return total;
	}//calculateTotal

	public static int getCartTotalAmount() {
		return calculateTotal();
	}//getCartTotalAmount

	public static String getOrderType() {
		return orderType;
	}//getOrderType

	public static void setOrderType(String orderType) {
		CartListService.orderType = orderType;
	}//setOrderType

	public static void setDirectOrderItem(CartDTO item) {
<<<<<<< HEAD
		if (item == null) {
			CartListService.directOrderItem = null;
			return;
		}

		// 기존 장바구니에 담아둔 상품이 있으면 장바구니 리스트에 합치고(동일 옵션 수량 합산 포함) 바로 주문 칸은 비움
		if (!cartList.isEmpty()) {
			addCartItem(item);
			CartListService.directOrderItem = null;
		} else {
			// 장바구니가 비어있으면 기존처럼 바로 주문 칸에 단독 저장
			CartListService.directOrderItem = item;
		}//end else
=======
		CartListService.directOrderItem = item;
>>>>>>> refs/remotes/origin/lsj
	}//setDirectOrderItem

	// 바로 주문 아이템이 있으면 그것만 반환, 없으면 장바구니 전체 반환
	public static List<CartDTO> getCheckoutItems() {
		if (directOrderItem != null) {
			List<CartDTO> single = new ArrayList<>();
			single.add(directOrderItem);
			return single;
		}
		return cartList;
	}//getCheckoutItems

	public static void clearDirectOrderItem() {
		directOrderItem = null;
	}//clearDirectOrderItem

	public static void resetCart() {
		cartList.clear();
	}//resetCart

	public static void resetOrder() {
		cartList.clear();
		directOrderItem = null;
		orderType = "EAT_IN";
	}//resetOrder
}
