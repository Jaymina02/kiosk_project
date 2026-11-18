package kr.co.sist.admin.evt;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import kr.co.sist.admin.common.AdminApplication;
import kr.co.sist.admin.dto.OrderListDTO;
import kr.co.sist.admin.service.OrderActionResult;
import kr.co.sist.admin.view.AdminOrderForm;
public class AdminOrderEvt extends WindowAdapter implements ActionListener {
    private final AdminOrderForm form;private final AdminApplication app;
    private final Timer refreshTimer;private boolean busy,confirming;
    public AdminOrderEvt(AdminOrderForm form,AdminApplication app){
        this.form=form;this.app=app;refreshTimer=new Timer(5000,e->refresh(false));
        for(JButton button:new JButton[]{form.getRefreshButton(),form.getBackButton(),form.getCompleteButton(),form.getCancelButton(),form.getDetailButton()})button.addActionListener(this);
        form.addWindowListener(this);
        MouseAdapter detail=new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                if(busy)return;
                JTable table=(JTable)e.getSource();int row=table.rowAtPoint(e.getPoint());int column=table.columnAtPoint(e.getPoint());
                if(row<0||column<0)return;table.setRowSelectionInterval(row,row);
                int modelColumn=table.convertColumnIndexToModel(column);
                if(e.getClickCount()==1&&modelColumn==6)form.getCompleteButton().doClick();
                else if(e.getClickCount()==1&&modelColumn==7)form.getCancelButton().doClick();
                else if(e.getClickCount()==2&&modelColumn<6)showDetail();
            }
        };
        form.getMakingTable().addMouseListener(detail);form.getCompletedTable().addMouseListener(detail);
    }
    public void start(){refresh(true);refreshTimer.start();}
    private void stop(){refreshTimer.stop();}
    @Override public void windowClosed(WindowEvent e){stop();}
    @Override public void windowClosing(WindowEvent e){back();}
    @Override public void actionPerformed(ActionEvent event){
        Object source=event.getSource();
        if(source==form.getBackButton())back();
        else if(source==form.getRefreshButton())refresh(true);
        else if(source==form.getCompleteButton())change(false);
        else if(source==form.getCancelButton())change(true);
        else if(source==form.getDetailButton())showDetail();
    }
    private void back(){if(busy||confirming)return;stop();form.dispose();app.openMain();}
    private void refresh(boolean manual){
        if(busy||confirming||!form.isDisplayable())return;busy=true;form.setBusy(true);
        new SwingWorker<List<OrderListDTO>,Void>() {
            @Override protected List<OrderListDTO> doInBackground() throws Exception{return app.getOrderService().searchOrderList();}
            @Override protected void done(){
                busy=false;if(!form.isDisplayable())return;form.setBusy(false);
                try{form.displayOrders(get());}
                catch(InterruptedException ex){Thread.currentThread().interrupt();form.setStatus("조회가 중단되었습니다.");}
                catch(ExecutionException ex){ex.getCause().printStackTrace();form.setStatus("조회 실패 · 기존 목록 표시 중");if(manual)error("주문 조회에 실패했습니다. DB 연결과 콘솔을 확인해주세요.");}
            }
        }.execute();
    }
    private void change(boolean cancel){
        if(busy)return;OrderListDTO item=form.getSelectedOrder();if(item==null)return;
        if(item.getOrderStatus()!=1){error("제작중 주문만 처리할 수 있습니다.");return;}
        confirming=true;int answer;
        try{answer=JOptionPane.showConfirmDialog(form,"주문번호 "+item.getOrderNo()+"\n"+(cancel?"이 주문을 취소하시겠습니까?\n주문·상세·옵션·결제 데이터가 삭제됩니다.":"제작완료로 변경하시겠습니까?"),cancel?"주문 취소 확인":"제작완료 확인",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);}
        finally{confirming=false;}
        if(answer!=JOptionPane.YES_OPTION)return;
        busy=true;form.setBusy(true);
        new SwingWorker<OrderActionResult,Void>() {
            @Override protected OrderActionResult doInBackground() throws Exception{return cancel?app.getCancelService().cancelOrder(item.getOrderId()):app.getOrderService().completeOrder(item.getOrderId());}
            @Override protected void done(){
                busy=false;if(!form.isDisplayable())return;form.setBusy(false);
                try{OrderActionResult result=get();
                    if(result==OrderActionResult.SUCCESS)form.setStatus(cancel?"주문을 취소했습니다.":"제작완료로 변경했습니다.");
                    else error(result==OrderActionResult.NOT_FOUND?"이미 삭제된 주문입니다. 목록을 새로 불러옵니다.":"이미 제작완료되었거나 처리 가능한 상태가 아닙니다.");
                }catch(InterruptedException ex){Thread.currentThread().interrupt();error("작업 결과를 확인하지 못했습니다. 목록을 새로 조회해주세요.");}
                catch(ExecutionException ex){ex.getCause().printStackTrace();error("처리에 실패했습니다. 목록을 새로 조회해 결과를 확인해주세요.");}
                refresh(false);
            }
        }.execute();
    }
    private void showDetail(){
        OrderListDTO item=form.getSelectedOrder();if(item==null||busy)return;
        JTextArea text=new JTextArea("주문번호: "+item.getOrderNo()+"\n상태: "+item.getOrderStatusName()+"\n결제 상태: "+item.getPaymentStatusName()+"\n\n"+item.getOrderContent()+"\n\n총 결제금액: "+String.format("%,d원",item.getTotalAmount()));
        text.setEditable(false);text.setLineWrap(true);text.setWrapStyleWord(true);text.setMargin(new Insets(12,12,12,12));
        JScrollPane scroll=new JScrollPane(text);scroll.setPreferredSize(new Dimension(520,300));
        confirming=true;try{JOptionPane.showMessageDialog(form,scroll,"주문 상세",JOptionPane.PLAIN_MESSAGE);}finally{confirming=false;}
    }
    private void error(String message){JOptionPane.showMessageDialog(form,message,"안내",JOptionPane.INFORMATION_MESSAGE);}
}
