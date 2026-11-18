package kr.co.sist.admin.view;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
/** 표의 마지막 두 칸을 버튼 모양으로 표시합니다. 클릭 처리는 AdminOrderEvt에 있습니다. */
public class OrderActionRenderer extends JButton implements TableCellRenderer {
    public OrderActionRenderer(){setFocusPainted(false);setBorder(BorderFactory.createLineBorder(Color.GRAY));}
    @Override public Component getTableCellRendererComponent(JTable table,Object value,boolean selected,boolean focus,int row,int column){
        setText(value==null?"":value.toString());setBackground(UiStyle.BUTTON);
        int modelRow=table.convertRowIndexToModel(row);
        OrderTableModel model=(OrderTableModel)table.getModel();
        boolean making=model.getOrder(modelRow).getOrderStatus()==1;
        setEnabled(table.isEnabled()&&making);
        setToolTipText(making?null:"제작완료된 주문은 취소할 수 없습니다.");
        return this;
    }
}
