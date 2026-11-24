package kr.co.sist.user.run;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.service.CartListService;
import kr.co.sist.user.view.MainForm;
import kr.co.sist.user.view.PaymentSelectForm;

public class Part3TestRun {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainForm.setOrderTypeHandler(CartListService::setOrderType);

            setupDummyCart();

            new PaymentSelectForm();
        });
    }

    public static void setupDummyCart() {
        CartListService.resetOrder();
        CartListService.setOrderType("매장");

        MenuOptionDTO tempOpt = new MenuOptionDTO();
        tempOpt.setOptionType("TEMPERATURE");
        tempOpt.setOptionName("ICE");
        tempOpt.setOptionPrice(0);

        MenuOptionDTO sizeOpt = new MenuOptionDTO();
        sizeOpt.setOptionType("SIZE");
        sizeOpt.setOptionName("Large");
        sizeOpt.setOptionPrice(500);

        List<MenuOptionDTO> extras = new ArrayList<>();
        MenuOptionDTO shotOpt = new MenuOptionDTO();
        shotOpt.setOptionType("EXTRA");
        shotOpt.setOptionName("샷 추가");
        shotOpt.setOptionPrice(500);
        extras.add(shotOpt);

        CartDTO item1 = new CartDTO();
        MenuDTO menu1 = new MenuDTO();
        menu1.setMenuName("아메리카노");
        item1.setMenu(menu1);
        item1.setTemperatureOption(tempOpt);
        item1.setSizeOption(sizeOpt);
        item1.setExtraOptions(extras);
        item1.setQuantity(2);
        item1.setUnitPrice(2800);
        CartListService.addCartItem(item1);

        MenuOptionDTO hotOpt = new MenuOptionDTO();
        hotOpt.setOptionType("TEMPERATURE");
        hotOpt.setOptionName("HOT");
        hotOpt.setOptionPrice(0);

        CartDTO item2 = new CartDTO();
        MenuDTO menu2 = new MenuDTO();
        menu2.setMenuName("카페라떼");
        item2.setMenu(menu2);
        item2.setTemperatureOption(hotOpt);
        item2.setQuantity(1);
        item2.setUnitPrice(3500);
        CartListService.addCartItem(item2);
    }
}