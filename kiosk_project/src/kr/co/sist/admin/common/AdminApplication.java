package kr.co.sist.admin.common;
import kr.co.sist.admin.dto.AdminDTO;
import kr.co.sist.admin.evt.*;
import kr.co.sist.admin.service.*;
import kr.co.sist.admin.view.*;

public class AdminApplication {
    private final AdminService adminService;
    private final OrderService orderService;
    private final CancelService cancelService;
    private AdminDTO admin;
    private Runnable menuAction,salesAction;
    public AdminApplication(AdminService a,OrderService o,CancelService c) {adminService=a;orderService=o;cancelService=c;}
    public AdminService getAdminService(){return adminService;}
    public OrderService getOrderService(){return orderService;}
    public CancelService getCancelService(){return cancelService;}
    public void setAdmin(AdminDTO admin){this.admin=admin;}
    public void openLogin(){admin=null;AdminLoginForm form=new AdminLoginForm();new AdminLoginEvt(form,this);form.setVisible(true);}
    public void openMain(){AdminMainForm form=new AdminMainForm(admin.getLoginId());new AdminMainEvt(form,this);form.setVisible(true);}
    public void openOrders(){AdminOrderForm form=new AdminOrderForm();AdminOrderEvt evt=new AdminOrderEvt(form,this);form.setVisible(true);evt.start();}
    
    public void setMenuAction(Runnable action){menuAction=action;}
    public void setSalesAction(Runnable action){salesAction=action;}
    public boolean hasMenuAction(){return menuAction!=null;}
    public boolean hasSalesAction(){return salesAction!=null;}
    public void openMenu(){if(menuAction!=null)menuAction.run();}
    public void openSales(){if(salesAction!=null)salesAction.run();}
}
