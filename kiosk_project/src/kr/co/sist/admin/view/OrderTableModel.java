package kr.co.sist.admin.view;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.table.AbstractTableModel;
import kr.co.sist.admin.dto.OrderListDTO;
public class OrderTableModel extends AbstractTableModel {
    private final String[] columns={"주문번호","주문 내용","유형","금액","결제","상태","제작완료","취소"};
    private java.util.List<OrderListDTO> orders=new ArrayList<>();
    public void setOrders(java.util.List<OrderListDTO> orders) {this.orders=new ArrayList<>(orders);fireTableDataChanged();}
    public OrderListDTO getOrder(int modelRow) { return orders.get(modelRow); }
    public int findRow(long orderId) {for(int i=0;i<orders.size();i++) if(orders.get(i).getOrderId()==orderId)return i;return -1;}
    @Override public int getRowCount() {return orders.size();}
    @Override public int getColumnCount() {return columns.length;}
    @Override public String getColumnName(int column) {return columns[column];}
    @Override public Class<?> getColumnClass(int column) {return column==3?Integer.class:String.class;}
    @Override public boolean isCellEditable(int row,int column) {return false;}
    @Override public Object getValueAt(int row,int column) {
        OrderListDTO item=orders.get(row);
        switch(column) {
        case 0:return item.getOrderNo();
        case 1:return item.getOrderContent()==null?"":item.getOrderContent().replace("\n"," / ");
        case 2:return displayType(item.getOrderType());
        case 3:return item.getTotalAmount();
        case 4:return displayPayment(item.getPaymentMethod());
        case 5:return item.getOrderStatusName();
        case 6:return item.getOrderStatus()==OrderListDTO.MAKING?"제작완료":"완료";
        default:return item.getOrderStatus()==OrderListDTO.MAKING?"취소":"불가";
        }
    }
    public static String displayType(String value) {
        if(value==null)return "";
        switch(value.toUpperCase(Locale.ROOT)) {
        case "STORE":case "DINE_IN":case "매장":return "매장";
        case "TAKEOUT":case "TAKE_OUT":case "포장":return "포장";
        default:return value;
        }
    }
    public static String displayPayment(String value) {
        if(value==null)return "미확인";
        switch(value.toUpperCase(Locale.ROOT)) {
        case "CARD":case "카드":return "카드";
        case "CASH":case "현금":return "현금";
        default:return value;
        }
    }
}
