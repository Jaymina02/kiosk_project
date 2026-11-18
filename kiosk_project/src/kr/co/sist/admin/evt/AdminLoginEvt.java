package kr.co.sist.admin.evt;
import java.awt.event.*;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import kr.co.sist.admin.common.AdminApplication;
import kr.co.sist.admin.dto.AdminDTO;
import kr.co.sist.admin.view.AdminLoginForm;
public class AdminLoginEvt implements ActionListener {
    private final AdminLoginForm form;private final AdminApplication app;
    public AdminLoginEvt(AdminLoginForm form,AdminApplication app){this.form=form;this.app=app;form.getLoginButton().addActionListener(this);}
    @Override public void actionPerformed(ActionEvent event) {
        String id=form.getIdField().getText();char[] password=form.getPasswordField().getPassword();
        if(id.isBlank()||password.length==0){Arrays.fill(password,'\0');form.setMessage("아이디와 비밀번호를 입력해주세요.");return;}
        form.setBusy(true);form.setMessage("로그인 중입니다.");
        new SwingWorker<AdminDTO,Void>() {
            @Override protected AdminDTO doInBackground() throws Exception {
                try{return app.getAdminService().login(id,new String(password));}finally{Arrays.fill(password,'\0');}
            }
            @Override protected void done() {
                if(!form.isDisplayable())return;form.setBusy(false);form.getPasswordField().setText("");
                try {AdminDTO admin=get();if(admin==null){form.setMessage("아이디 또는 비밀번호를 확인해주세요.");return;}
                    app.setAdmin(admin);form.dispose();app.openMain();
                }catch(InterruptedException ex){Thread.currentThread().interrupt();form.setMessage("로그인이 중단되었습니다.");}
                catch(ExecutionException ex){ex.getCause().printStackTrace();form.setMessage("DB 연결 또는 로그인 조회에 실패했습니다. 콘솔을 확인해주세요.");}
            }
        }.execute();
    }
}
