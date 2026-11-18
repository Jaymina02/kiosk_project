package kr.co.sist.admin.dto;
public class AdminDTO {
    private long adminId;
    private String loginId;
    private String password;
    public AdminDTO() { }
    public AdminDTO(String loginId, String password) { this.loginId=loginId; this.password=password; }
    public long getAdminId() { return adminId; }
    public void setAdminId(long value) { adminId=value; }
    public String getLoginId() { return loginId; }
    public void setLoginId(String value) { loginId=value; }
    public String getPassword() { return password; }
    public void setPassword(String value) { password=value; }
}
