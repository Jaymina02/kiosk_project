package kr.co.sist.admin.dao;
import java.sql.*;
import kr.co.sist.admin.common.ConnectionProvider;
import kr.co.sist.admin.dto.AdminDTO;
public class AdminDAO {
    private final ConnectionProvider connections;
    public AdminDAO(ConnectionProvider connections) { this.connections=connections; }
    
    public AdminDTO selectAdmin(AdminDTO credentials) throws SQLException {
        String sql="SELECT ADMIN_ID, LOGIN_ID FROM ADMIN WHERE LOGIN_ID=? AND PASSWORD=?";
        try(Connection con=connections.getConnection(); PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1,credentials.getLoginId()); ps.setString(2,credentials.getPassword());
            try(ResultSet rs=ps.executeQuery()) {
                if(!rs.next()) return null;
                AdminDTO admin=new AdminDTO(); admin.setAdminId(rs.getLong("ADMIN_ID"));
                admin.setLoginId(rs.getString("LOGIN_ID")); return admin;
            }
        }
    }
}
