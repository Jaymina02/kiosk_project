package kr.co.sist.admin.common;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import kr.co.sist.common.db.DbConn;

public class TeamConnectionProvider implements ConnectionProvider {
    private final DbConn db=DbConn.getInstance();
    private final String configPath;
    public TeamConnectionProvider(){this(System.getProperty("cafe.db.config"));}
    public TeamConnectionProvider(String configPath){this.configPath=configPath;}
    @Override public Connection getConnection() throws SQLException {
        Properties properties=db.getProp(configPath);
        if(properties==null)throw new SQLException("DB 설정파일을 찾을 수 없습니다. 프로젝트 루트의 config/database.properties를 확인하세요.");
        for(String key:new String[]{"url","id","pass"}) {
            String value=properties.getProperty(key);
            if(value==null||value.isBlank())throw new SQLException("DB 설정의 "+key+" 항목이 비어 있습니다.");
        }
        Connection con=db.getConn(properties);
        if(con==null)throw new SQLException("팀 DbConn이 Connection을 반환하지 않았습니다.");
        return con;
    }
}
