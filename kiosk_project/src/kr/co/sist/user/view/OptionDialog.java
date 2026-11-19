package kr.co.sist.user.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.evt.OptionEvt;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OptionDialog extends JDialog {

    // [다이어그램 명시 필드]
    private OptionEvt optionEvt;
//    private MenuDTO selectedMenu;
    private MenuOptionDTO temperatureOption;
    private MenuOptionDTO sizeOption;
    private List<MenuOptionDTO> extraOptions;
    private int quantity;

    // [UI 컴포넌트 필드]
    private JLabel lblImage, lblMenuName, lblBasePrice, lblTotalPrice;
    private JPanel pnlTempRdo, pnlSizeRdo, pnlExtraChk;
    private JRadioButton rdoHot, rdoIce, rdoRegular, rdoLarge;
    private JCheckBox chkVanilla, chkHazelnut, chkShot, chkSoyMilk, chkMilk, chkDecaf;
    private JButton btnMinus, btnPlus, btnCancel, btnAddCart, btnOrderNow;
    private JTextField txtQuantity;

    public OptionDialog(/* MenuDTO menu */) {
//        this.selectedMenu = menu;

        setTitle("옵션 선택");
        setModal(true);
        setSize(800, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        // 2. 중앙 메인 영역
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        // 2-1. 상단 상품 정보 패널
        JPanel pnlProductInfo = new JPanel(new BorderLayout(15, 0));
        pnlProductInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        lblImage = new JLabel("이미지", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(100, 100));
        lblImage.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlProductInfo.add(lblImage, BorderLayout.WEST);

        JPanel pnlNamePrice = new JPanel(new GridLayout(2, 1, 5, 5));
        lblMenuName = new JLabel("메뉴명");
        lblBasePrice = new JLabel("기본가격");
        pnlNamePrice.add(lblMenuName);
        pnlNamePrice.add(lblBasePrice);
        pnlProductInfo.add(pnlNamePrice, BorderLayout.CENTER);

        pnlCenter.add(pnlProductInfo, BorderLayout.NORTH);

     // 2-2. 중단 옵션 영역 (4층으로 위아래 나누기)
        JPanel pnlOptions = new JPanel(new GridLayout(4, 1, 5, 10));

        // 온도 (JRadioButton + ButtonGroup 생성)
        JPanel pnlTemp = new JPanel(new BorderLayout());
        JLabel lblTemp = new JLabel("온도 *");
        lblTemp.setPreferredSize(new Dimension(100, 30));

        pnlTempRdo = new JPanel(new GridLayout(1, 3, 10, 0));
        rdoHot = new JRadioButton("HOT");
        rdoIce = new JRadioButton("ICE");

        ButtonGroup bgTemp = new ButtonGroup();
        bgTemp.add(rdoHot);
        bgTemp.add(rdoIce);

        pnlTempRdo.add(rdoHot);
        pnlTempRdo.add(rdoIce);
        pnlTempRdo.add(new JLabel("")); // 3칸 비율 맞추기용 빈 라벨

        pnlTemp.add(lblTemp, BorderLayout.WEST);
        pnlTemp.add(pnlTempRdo, BorderLayout.CENTER);
        pnlOptions.add(pnlTemp);

        // 사이즈 (JRadioButton + ButtonGroup 생성)
        JPanel pnlSize = new JPanel(new BorderLayout());
        JLabel lblSize = new JLabel("사이즈 *");
        lblSize.setPreferredSize(new Dimension(100, 30));

        // 추가 옵션과 동일한 1행 3열 GridLayout 적용
        pnlSizeRdo = new JPanel(new GridLayout(1, 3, 5, 0));
        rdoRegular = new JRadioButton("Regular");
        rdoLarge = new JRadioButton("Large (+500원)");
        ButtonGroup bgSize = new ButtonGroup();
        bgSize.add(rdoRegular);
        bgSize.add(rdoLarge);
        pnlSizeRdo.add(rdoRegular);
        pnlSizeRdo.add(rdoLarge);
        pnlSizeRdo.add(new JLabel("")); // 3칸 비율 맞추기용 빈 라벨

        pnlSize.add(lblSize, BorderLayout.WEST);
        pnlSize.add(pnlSizeRdo, BorderLayout.CENTER);
        pnlOptions.add(pnlSize);

        // 추가 옵션 (JCheckBox 6개 생성)
        JPanel pnlExtra = new JPanel(new BorderLayout());
        JLabel lblExtra = new JLabel("추가 옵션");
        lblExtra.setPreferredSize(new Dimension(100, 30));

        pnlExtraChk = new JPanel(new GridLayout(2, 3, 5, 5));
        chkVanilla = new JCheckBox("바닐라시럽 (+500원)");
        chkHazelnut = new JCheckBox("헤이즐넛시럽 (+500원)");
        chkShot = new JCheckBox("샷 추가 (+500원)");
        chkSoyMilk = new JCheckBox("두유 (+500원)");
        chkMilk = new JCheckBox("우유추가 (+500원)");
        chkDecaf = new JCheckBox("디카페인 샷 추가 (+600원)");

        pnlExtraChk.add(chkVanilla);
        pnlExtraChk.add(chkHazelnut);
        pnlExtraChk.add(chkShot);
        pnlExtraChk.add(chkSoyMilk);
        pnlExtraChk.add(chkMilk);
        pnlExtraChk.add(chkDecaf);

        pnlExtra.add(lblExtra, BorderLayout.WEST);
        pnlExtra.add(pnlExtraChk, BorderLayout.CENTER);
        pnlOptions.add(pnlExtra);

        // 수량 (수동 배치)
        JPanel pnlQty = new JPanel();
        pnlQty.setLayout(null); // 수동 배치 선언

        // 1. "수량" 라벨 
        JLabel lblQty = new JLabel("수량");
        lblQty.setBounds(0, 20, 100, 32);

        // 2. 마이너스(-) 버튼
        btnMinus = new JButton("-");
        btnMinus.setBounds(100, 20, 40, 32);
        btnMinus.setMargin(new Insets(0, 0, 0, 0));
        btnMinus.setFocusPainted(false);

        // 3. 수량 텍스트필드
        txtQuantity = new JTextField("1");
        txtQuantity.setBounds(140, 20, 50, 32);
        txtQuantity.setHorizontalAlignment(SwingConstants.CENTER);
        txtQuantity.setEditable(false);
        txtQuantity.setBackground(Color.WHITE);

        // 4. 플러스(+) 버튼
        btnPlus = new JButton("+");
        btnPlus.setBounds(190, 20, 40, 32);
        btnPlus.setMargin(new Insets(0, 0, 0, 0));
        btnPlus.setFocusPainted(false);

        // 패널에 모두 부착
        pnlQty.add(lblQty);
        pnlQty.add(btnMinus);
        pnlQty.add(txtQuantity);
        pnlQty.add(btnPlus);

        pnlOptions.add(pnlQty);


        pnlCenter.add(pnlOptions, BorderLayout.CENTER);

        // 2-3. 하단 합계 금액 영역
        JPanel pnlTotalWrapper = new JPanel(new BorderLayout(0, 10));
        pnlTotalWrapper.add(new JSeparator(), BorderLayout.NORTH);

        JPanel pnlTotal = new JPanel(new BorderLayout());
        JLabel lblTotalTitle = new JLabel("합계 금액");
        lblTotalPrice = new JLabel(" 원", SwingConstants.RIGHT);

        pnlTotal.add(lblTotalTitle, BorderLayout.WEST);
        pnlTotal.add(lblTotalPrice, BorderLayout.EAST);
        pnlTotalWrapper.add(pnlTotal, BorderLayout.CENTER);

        pnlCenter.add(pnlTotalWrapper, BorderLayout.SOUTH);
        add(pnlCenter, BorderLayout.CENTER);

        // 3. 최하단 버튼 영역
        JPanel pnlButtons = new JPanel(new GridLayout(1, 3, 15, 0));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        btnCancel = new JButton("취소");
        btnAddCart = new JButton("장바구니 담기");
        btnOrderNow = new JButton("바로 주문");
        btnCancel.setPreferredSize(new Dimension(0, 40));

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnAddCart);
        pnlButtons.add(btnOrderNow);
        add(pnlButtons, BorderLayout.SOUTH);
        
        // 4. 이벤트 등록
        this.optionEvt = new OptionEvt(this);

        // 수량 버튼
        btnPlus.addActionListener(optionEvt);
        btnMinus.addActionListener(optionEvt);

        // 온도 & 사이즈 라디오 버튼
        rdoHot.addActionListener(optionEvt);
        rdoIce.addActionListener(optionEvt);
        rdoRegular.addActionListener(optionEvt);
        rdoLarge.addActionListener(optionEvt);

        // 추가 옵션 체크박스 6개
        chkVanilla.addActionListener(optionEvt);
        chkHazelnut.addActionListener(optionEvt);
        chkShot.addActionListener(optionEvt);
        chkSoyMilk.addActionListener(optionEvt);
        chkMilk.addActionListener(optionEvt);
        chkDecaf.addActionListener(optionEvt);

        // 취소, 장바구니, 바로주문 버튼
        btnCancel.addActionListener(optionEvt);
        btnAddCart.addActionListener(optionEvt);
        btnOrderNow.addActionListener(optionEvt);

        // 창기 띄우기 전 기본금액과 합계 금액 초기화면(테스트용)
        displayMenuInfo();
        displayTotalAmount();

        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void displayMenuInfo() { 
    	  // MenuDTO가 연결되면 아래 주석 해제 연동 확인
        /*
        if (selectedMenu != null) {
            lblMenuName.setText(selectedMenu.getMenuName());
            lblBasePrice.setText(String.format("%,d 원", selectedMenu.getPrice()));
            return;
        }
        */

        // MenuDTO 연결 전 임시 테스트용 표시
        lblMenuName.setText("아메리카노");
        lblBasePrice.setText("1,800 원");
    	
    }//displayMenuInfo

    public void displayTemperatureOptions() { }

    public void displaySizeOptions() { }

    public void displayExtraOptions() { }

    public void displayQuantity() { 
    	 txtQuantity.setText(String.valueOf(quantity));
    }//displayQuantity

    // 창을 처음 띄울 때 기본 합계 금액(테스트용: 4,500 원) 표시
    public void displayTotalAmount() { 
    	  if (optionEvt != null) {
              int total = optionEvt.calculatePreviewTotal();
              lblTotalPrice.setText(String.format("%,d 원", total));
          }//end if
    }//end if

    public void closeDialog() { 
    	dispose();// 옵션 창만 닫아서 뒤에 있는 메뉴판으로 복귀
    }//closeDialog

    public void openCartForm() { 
    	new CartForm(); // CartForm 생성자에서 자동으로 CartListService 목록을 읽고 띄워주기
    }//openCartForm

    public void openPaymentSelectForm() {
//    	new PaymentForm(); // 결제 폼 연결
    }//openPaymentSelectForm
}// class
