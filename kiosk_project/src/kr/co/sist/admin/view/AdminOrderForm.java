package kr.co.sist.admin.view;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import kr.co.sist.admin.dto.OrderListDTO;
public class AdminOrderForm extends JFrame {
    private final OrderTableModel makingModel=new OrderTableModel(), completedModel=new OrderTableModel();
    private final JTable makingTable=createOrderTable(makingModel), completedTable=createOrderTable(completedModel);
    private final JTabbedPane tabs=new JTabbedPane();
    private final JButton refreshButton=UiStyle.button("새로고침"), backButton=UiStyle.button("뒤로 가기");
    private final JButton completeButton=UiStyle.button("제작완료"), cancelButton=UiStyle.button("주문 취소");
    private final JButton detailButton=UiStyle.button("주문 상세");
    private final JLabel counts=new JLabel("제작중 0건  |  제작완료 0건"), status=new JLabel("주문을 불러오는 중입니다.");
    private boolean busy;
    public AdminOrderForm() {
        super("관리자 주문 관리");setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        add(UiStyle.header("주문 관리","5초마다 새 주문을 조회합니다. 제작중 주문만 취소할 수 있습니다."),BorderLayout.NORTH);
       
        configureCompletedTable(completedTable);
        tabs.addTab("주문 내역",new JScrollPane(makingTable));tabs.addTab("완료",new JScrollPane(completedTable));
        JPanel body=new JPanel(new BorderLayout(0,14));body.setBackground(Color.WHITE);body.setBorder(BorderFactory.createEmptyBorder(20,25,10,25));body.add(tabs,BorderLayout.CENTER);
        JPanel summary=new JPanel(new BorderLayout());summary.setBackground(Color.WHITE);counts.setFont(counts.getFont().deriveFont(Font.BOLD,16f));summary.add(counts,BorderLayout.WEST);summary.add(status,BorderLayout.EAST);body.add(summary,BorderLayout.SOUTH);add(body,BorderLayout.CENTER);
        JPanel bottom=new JPanel(new BorderLayout());bottom.setBackground(Color.WHITE);bottom.setBorder(BorderFactory.createEmptyBorder(5,18,15,18));
        JPanel left=new JPanel(new FlowLayout(FlowLayout.LEFT));left.setBackground(Color.WHITE);left.add(refreshButton);left.add(detailButton);
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT));right.setBackground(Color.WHITE);right.add(backButton);
        cancelButton.setForeground(new Color(158,45,45));bottom.add(left,BorderLayout.WEST);bottom.add(right,BorderLayout.EAST);add(bottom,BorderLayout.SOUTH);
        makingTable.getSelectionModel().addListSelectionListener(e->updateActions());completedTable.getSelectionModel().addListSelectionListener(e->updateActions());tabs.addChangeListener(e->updateActions());
        UiStyle.frame(this,1220,730);updateActions();
    }
    public static JTable createOrderTable(OrderTableModel model) {
        JTable table=new JTable(model);table.setRowHeight(42);table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);table.setFillsViewportHeight(true);table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);table.getTableHeader().setPreferredSize(new Dimension(0,36));
        int[] widths={85,320,75,110,80,85,95,95};for(int i=0;i<widths.length;i++)table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        DefaultTableCellRenderer center=new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
        for(int column:new int[]{0,2,4,5})table.getColumnModel().getColumn(column).setCellRenderer(center);
        if(table.getTableHeader().getDefaultRenderer() instanceof DefaultTableCellRenderer){
            ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        }
        table.getColumnModel().getColumn(6).setCellRenderer(new OrderActionRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new OrderActionRenderer());
        table.setDefaultRenderer(Integer.class,new DefaultTableCellRenderer(){
            @Override protected void setValue(Object value){setHorizontalAlignment(CENTER);setText(value==null?"":String.format("%,d원",value));}
        });return table;
    }
    public static void configureCompletedTable(JTable table) {
        table.removeColumn(table.getColumnModel().getColumn(7));
        table.removeColumn(table.getColumnModel().getColumn(6));
    }
    public void displayOrders(List<OrderListDTO> all) {
        OrderListDTO selected=getSelectedOrder();Long id=selected==null?null:selected.getOrderId();
        List<OrderListDTO> making=new ArrayList<>(),completed=new ArrayList<>();
        for(OrderListDTO item:all) {if(item.getOrderStatus()==1)making.add(item);else if(item.getOrderStatus()==2)completed.add(item);}
        makingModel.setOrders(making);completedModel.setOrders(completed);
        if(id!=null) {
            JTable table=getActiveTable();OrderTableModel model=(OrderTableModel)table.getModel();int row=model.findRow(id);
            if(row>=0) {int viewRow=table.convertRowIndexToView(row);table.setRowSelectionInterval(viewRow,viewRow);}
        }
        counts.setText("제작중 "+making.size()+"건  |  제작완료 "+completed.size()+"건");
        status.setText("최근 갱신 "+LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));updateActions();
    }
    public JTable getActiveTable() {return tabs.getSelectedIndex()==0?makingTable:completedTable;}
    public OrderListDTO getSelectedOrder() {
        JTable table=getActiveTable();int selected=table.getSelectedRow();
        return selected<0?null:((OrderTableModel)table.getModel()).getOrder(table.convertRowIndexToModel(selected));
    }
    public void setBusy(boolean busy) {this.busy=busy;makingTable.setEnabled(!busy);completedTable.setEnabled(!busy);makingTable.repaint();completedTable.repaint();refreshButton.setEnabled(!busy);backButton.setEnabled(!busy);updateActions();}
    private void updateActions() {OrderListDTO selected=getSelectedOrder();boolean making=!busy&&selected!=null&&selected.getOrderStatus()==1;
        completeButton.setEnabled(making);cancelButton.setEnabled(making);detailButton.setEnabled(!busy&&selected!=null);}
    public void setStatus(String message) {status.setText(message);}
    public JButton getRefreshButton(){return refreshButton;}
    public JButton getBackButton(){return backButton;}
    public JButton getCompleteButton(){return completeButton;}
    public JButton getCancelButton(){return cancelButton;}
    public JButton getDetailButton(){return detailButton;}
    public JTable getMakingTable(){return makingTable;}
    public JTable getCompletedTable(){return completedTable;}
}
