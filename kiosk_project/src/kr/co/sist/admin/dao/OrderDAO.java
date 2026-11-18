package kr.co.sist.admin.dao;
import java.sql.*;
import java.util.*;
import kr.co.sist.admin.common.ConnectionProvider;
import kr.co.sist.admin.dto.OrderListDTO;

public class OrderDAO {
    private final ConnectionProvider connections;
    public OrderDAO(ConnectionProvider connections) { this.connections=connections; }
    public List<OrderListDTO> selectOrderList() throws SQLException {
        String sql=
                "SELECT O.ORDER_ID,O.ORDER_NO,O.ORDER_DATE,O.ORDER_TYPE,O.TOTAL_AMOUNT,O.ORDER_STATUS, "
                + "P.PAYMENT_METHOD, P.PAYMENT_STATUS "
                + "FROM ORDERS O "
                + "LEFT JOIN (SELECT ORDER_ID,PAYMENT_METHOD,PAYMENT_STATUS, "
                + "ROW_NUMBER() OVER(PARTITION BY ORDER_ID "
                + "ORDER BY PAYMENT_DATE DESC,PAYMENT_ID DESC) RN FROM PAYMENT) P "
                + "ON P.ORDER_ID=O.ORDER_ID AND P.RN=1 "
                + "WHERE O.ORDER_STATUS IN (1,2) "
                + "ORDER BY O.ORDER_DATE DESC,O.ORDER_ID DESC ";
        Map<Long,OrderListDTO> orders=new LinkedHashMap<>();
        try(Connection con=connections.getConnection()) {
            try(PreparedStatement ps=con.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
                while(rs.next()) {
                    OrderListDTO item=new OrderListDTO(); item.setOrderId(rs.getLong("ORDER_ID"));
                    item.setOrderNo(rs.getString("ORDER_NO"));
                    Timestamp date=rs.getTimestamp("ORDER_DATE");
                    item.setOrderDate(date==null?null:date.toLocalDateTime());
                    item.setOrderType(rs.getString("ORDER_TYPE")); item.setTotalAmount(rs.getInt("TOTAL_AMOUNT"));
                    item.setOrderStatus(rs.getInt("ORDER_STATUS"));
                    item.setPaymentMethod(rs.getString("PAYMENT_METHOD")); item.setPaymentStatus(rs.getString("PAYMENT_STATUS")); orders.put(item.getOrderId(),item);
                }
            }
            
            String detailSql=
                "SELECT D.ORDER_ID,D.DETAIL_ID,D.MENU_NAME_SNAPSHOT,D.QUANTITY,D.LINE_AMOUNT, "
                + "M.OPTION_NAME "
                + "FROM ORDER_DETAIL D JOIN ORDERS O ON O.ORDER_ID=D.ORDER_ID "
                + "LEFT JOIN ORDER_OPTION OP ON OP.DETAIL_ID=D.DETAIL_ID "
                + "LEFT JOIN MENU_OPTION M ON M.OPTION_ID=OP.OPTION_ID "
                + "WHERE O.ORDER_STATUS IN (1,2) "
                + "ORDER BY D.ORDER_ID,D.DETAIL_ID,OP.DETAIL_OPTION_ID ";
            Map<Long,StringBuilder> content=new HashMap<>();
            try(PreparedStatement ps=con.prepareStatement(detailSql); ResultSet rs=ps.executeQuery()) {
                long previousDetail=-1;
                while(rs.next()) {
                    long orderId=rs.getLong("ORDER_ID"), detailId=rs.getLong("DETAIL_ID");
                    StringBuilder text=content.get(orderId);
                    if(text==null){text=new StringBuilder();content.put(orderId,text);}
                    if(detailId!=previousDetail) {
                        if(text.length()>0) text.append("\n");
                        text.append(rs.getString("MENU_NAME_SNAPSHOT")).append(" × ").append(rs.getInt("QUANTITY"));
                        text.append("  (").append(String.format("%,d",rs.getInt("LINE_AMOUNT"))).append("원)");
                        previousDetail=detailId;
                    }
                    String option=rs.getString("OPTION_NAME");
                    if(option!=null) text.append(" / ").append(option);
                }
            }
            for(OrderListDTO item:orders.values()) {
                StringBuilder text=content.get(item.getOrderId());
                item.setOrderContent(text==null?"상세 내역 없음":text.toString());
            }
        }
        return new ArrayList<>(orders.values());
    }
    
    public Integer selectOrderStatusForUpdate(Connection con,long orderId) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement("SELECT ORDER_STATUS FROM ORDERS WHERE ORDER_ID=? FOR UPDATE")) {
            ps.setLong(1,orderId);
            try(ResultSet rs=ps.executeQuery()) { return rs.next()?rs.getInt(1):null; }
        }
    }
    public int updateMakingToCompleted(Connection con,long orderId) throws SQLException {
        return execute(con,"UPDATE ORDERS SET ORDER_STATUS=2 WHERE ORDER_ID=? AND ORDER_STATUS=1",orderId);
    }
    public int deleteOrderOptions(Connection con,long orderId) throws SQLException {
        return execute(con,"DELETE FROM ORDER_OPTION WHERE DETAIL_ID IN (SELECT DETAIL_ID FROM ORDER_DETAIL WHERE ORDER_ID=?)",orderId);
    }
    public int deleteOrderDetails(Connection con,long orderId) throws SQLException {
        return execute(con,"DELETE FROM ORDER_DETAIL WHERE ORDER_ID=?",orderId);
    }
    public int deleteCancelRequests(Connection con,long orderId) throws SQLException {
        return execute(con,"DELETE FROM CANCEL_REQUEST WHERE ORDER_ID=?",orderId);
    }
    public int deleteMakingOrder(Connection con,long orderId) throws SQLException {
        return execute(con,"DELETE FROM ORDERS WHERE ORDER_ID=? AND ORDER_STATUS=1",orderId);
    }
    private int execute(Connection con,String sql,long orderId) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement(sql)) { ps.setLong(1,orderId); return ps.executeUpdate(); }
    }
}
