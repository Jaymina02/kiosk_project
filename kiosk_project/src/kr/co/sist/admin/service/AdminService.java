package kr.co.sist.admin.service;
import java.sql.SQLException;
import kr.co.sist.admin.dao.AdminDAO;
import kr.co.sist.admin.dto.AdminDTO;
public class AdminService {
    private final AdminDAO adminDAO;
    public AdminService(AdminDAO dao) { adminDAO=dao; }
    public AdminDTO login(String id,String password) throws SQLException {
        if(id==null || id.isBlank() || password==null || password.isEmpty()) return null;
        AdminDTO input=new AdminDTO(id.trim(),password);
        try { return adminDAO.selectAdmin(input); }
        finally { input.setPassword(null); }
    }
}
