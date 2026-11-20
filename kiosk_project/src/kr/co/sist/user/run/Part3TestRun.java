package kr.co.sist.user.run;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.service.CartListService;
import kr.co.sist.user.view.PaymentSelectForm;

public class Part3TestRun {
    public static void main(String[] args) {
        CartListService.resetOrder();
        CartListService.setOrderType("EAT_IN");

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

        CartDTO cartItem = new CartDTO();
        cartItem.setTempMenuName("아메리카노");
        cartItem.setTemperatureOption(tempOpt);
        cartItem.setSizeOption(sizeOpt);
        cartItem.setExtraOptions(extras);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(2800);

        CartListService.addCartItem(cartItem);

        SwingUtilities.invokeLater(() -> new PaymentSelectForm());
    }
}
