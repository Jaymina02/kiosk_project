package kr.co.sist.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.common.db.DbConn;
import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.dto.OrderOptionDTO;

public class OrderDAO {
    private static OrderDAO oDAO;

    private OrderDAO() {
    }

    public static OrderDAO getInstance() {
        if (oDAO == null) {
            oDAO = new OrderDAO();
        }
        return oDAO;
    }

    public long selectNextOrderId(Connection conn) throws SQLException {
        long nextId = 1L;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT NVL(MAX(ORDER_ID), 0) + 1 FROM ORDERS";

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                nextId = rs.getLong(1);
            }
        } finally {
            DbConn.getInstance().dbClose(rs, pstmt, null);
        }
        return nextId;
    }

    public long selectMenuIdByName(Connection conn, String menuName) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (menuName != null && !menuName.isBlank()) {
                String sql = "SELECT MENU_ID FROM MENU WHERE MENU_NAME = ? AND ROWNUM = 1";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, menuName.trim());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getLong("MENU_ID");
                }
                DbConn.getInstance().dbClose(rs, pstmt, null);
                rs = null;
                pstmt = null;
            }

            String fallbackSql = "SELECT NVL(MIN(MENU_ID), 1) FROM MENU";
            pstmt = conn.prepareStatement(fallbackSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } finally {
            DbConn.getInstance().dbClose(rs, pstmt, null);
        }
        return 1L;
    }

    public int selectBasePriceByMenuId(Connection conn, long menuId, int fallbackPrice) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT BASE_PRICE FROM MENU WHERE MENU_ID = ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, menuId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("BASE_PRICE");
            }
        } finally {
            DbConn.getInstance().dbClose(rs, pstmt, null);
        }
        return fallbackPrice;
    }

    /**
     * 2번 파트 OptionEvt에서 optionId가 미설정(0)된 경우 옵션명으로 MENU_OPTION 테이블의 OPTION_ID를 조회한다.
     */
    public long selectOptionIdByName(Connection conn, String optionName) throws SQLException {
        if (optionName == null || optionName.isBlank()) {
            return 0L;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT OPTION_ID FROM MENU_OPTION WHERE UPPER(TRIM(OPTION_NAME)) = UPPER(TRIM(?)) AND ROWNUM = 1";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, optionName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("OPTION_ID");
            }
        } finally {
            DbConn.getInstance().dbClose(rs, pstmt, null);
        }
        return 0L;
    }

    public int insertOrder(Connection conn, OrderDTO order) throws SQLException {
        int rowCnt = 0;
        PreparedStatement pstmt = null;

        String sql = "INSERT INTO ORDERS (ORDER_ID, ORDER_NO, ORDER_TYPE, TOTAL_AMOUNT, ORDER_STATUS, ORDER_DATE) "
                   + "VALUES (?, ?, ?, ?, ?, SYSDATE)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, order.getOrderId());
            pstmt.setString(2, order.getOrderNo());
            pstmt.setString(3, order.getOrderType());
            pstmt.setInt(4, order.getTotalAmount());
            pstmt.setInt(5, order.getOrderStatus());

            rowCnt = pstmt.executeUpdate();
        } finally {
            DbConn.getInstance().dbClose(null, pstmt, null);
        }
        return rowCnt;
    }

    public int insertOrderDetail(Connection conn, OrderDetailDTO detail) throws SQLException {
        int rowCnt = 0;
        PreparedStatement seqPstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String seqSql = "SELECT NVL(MAX(DETAIL_ID), 0) + 1 FROM ORDER_DETAIL";
        String insertSql = "INSERT INTO ORDER_DETAIL "
                         + "(DETAIL_ID, ORDER_ID, MENU_ID, MENU_NAME_SNAPSHOT, BASE_PRICE_SNAPSHOT, UNIT_PRICE, QUANTITY, LINE_AMOUNT) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            seqPstmt = conn.prepareStatement(seqSql);
            rs = seqPstmt.executeQuery();
            long detailId = 1L;
            if (rs.next()) {
                detailId = rs.getLong(1);
            }
            detail.setDetailId(detailId);

            pstmt = conn.prepareStatement(insertSql);
            pstmt.setLong(1, detail.getDetailId());
            pstmt.setLong(2, detail.getOrderId());
            pstmt.setLong(3, detail.getMenuId());
            pstmt.setString(4, detail.getMenuNameSnapshot());
            pstmt.setInt(5, detail.getBasePriceSnapshot());
            pstmt.setInt(6, detail.getUnitPrice());
            pstmt.setInt(7, detail.getQuantity());
            pstmt.setInt(8, detail.getLineAmount());

            rowCnt = pstmt.executeUpdate();
        } finally {
            DbConn.getInstance().dbClose(rs, seqPstmt, null);
            DbConn.getInstance().dbClose(null, pstmt, null);
        }
        return rowCnt;
    }

    public int insertOrderOption(Connection conn, OrderOptionDTO option) throws SQLException {
        int rowCnt = 0;
        PreparedStatement pstmt = null;

        String sql = "INSERT INTO ORDER_OPTION (DETAIL_OPTION_ID, DETAIL_ID, OPTION_ID) "
                   + "VALUES ((SELECT NVL(MAX(DETAIL_OPTION_ID), 0) + 1 FROM ORDER_OPTION), ?, ?)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, option.getDetailId());
            pstmt.setLong(2, option.getOptionId());

            rowCnt = pstmt.executeUpdate();
        } finally {
            DbConn.getInstance().dbClose(null, pstmt, null);
        }
        return rowCnt;
    }

    public List<OrderDetailDTO> selectOrderDetails(long orderId) throws SQLException {
        List<OrderDetailDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT DETAIL_ID, ORDER_ID, MENU_ID, MENU_NAME_SNAPSHOT, "
                   + "BASE_PRICE_SNAPSHOT, UNIT_PRICE, QUANTITY, LINE_AMOUNT "
                   + "FROM ORDER_DETAIL WHERE ORDER_ID = ? ORDER BY DETAIL_ID ASC";

        try {
            DbConn db = DbConn.getInstance();
            conn = db.getConn(db.getProp(null));
            if (conn == null) {
                throw new SQLException("DB 연결 실패: config/database.properties 경로 또는 설정을 확인하세요.");
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderDetailDTO dto = new OrderDetailDTO();
                dto.setDetailId(rs.getLong("DETAIL_ID"));
                dto.setOrderId(rs.getLong("ORDER_ID"));
                dto.setMenuId(rs.getLong("MENU_ID"));
                dto.setMenuNameSnapshot(rs.getString("MENU_NAME_SNAPSHOT"));
                dto.setBasePriceSnapshot(rs.getInt("BASE_PRICE_SNAPSHOT"));
                dto.setUnitPrice(rs.getInt("UNIT_PRICE"));
                dto.setQuantity(rs.getInt("QUANTITY"));
                dto.setLineAmount(rs.getInt("LINE_AMOUNT"));
                list.add(dto);
            }
        } finally {
            DbConn.getInstance().dbClose(rs, pstmt, conn);
        }
        return list;
    }
    public OrderDTO selectOneOrder(long orderId) throws SQLException {
        OrderDTO dto = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT ORDER_ID, ORDER_NO, ORDER_TYPE, TOTAL_AMOUNT, ORDER_STATUS, ORDER_DATE "
                   + "FROM ORDERS WHERE ORDER_ID = ?";

        try {
            DbConn db = DbConn.getInstance();
            conn = db.getConn(db.getProp(null));
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new OrderDTO();
                dto.setOrderId(rs.getLong("ORDER_ID"));
                dto.setOrderNo(rs.getString("ORDER_NO"));
                dto.setOrderType(rs.getString("ORDER_TYPE"));
                dto.setTotalAmount(rs.getInt("TOTAL_AMOUNT"));
                dto.setOrderStatus(rs.getInt("ORDER_STATUS"));
                if (rs.getTimestamp("ORDER_DATE") != null) {
                    dto.setOrderDate(rs.getTimestamp("ORDER_DATE").toLocalDateTime());
                }
            }
        } finally {
            DbConn.getInstance().dbClose(rs, pstmt, conn);
        }
        return dto;
    }

    public OrderDTO selectOrderByNo(String orderNo) throws SQLException {
        OrderDTO dto = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT ORDER_ID, ORDER_NO, ORDER_TYPE, TOTAL_AMOUNT, ORDER_STATUS, ORDER_DATE "
                   + "FROM ORDERS WHERE ORDER_NO = ?";

        try {
            DbConn db = DbConn.getInstance();
            conn = db.getConn(db.getProp(null));
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new OrderDTO();
                dto.setOrderId(rs.getLong("ORDER_ID"));
                dto.setOrderNo(rs.getString("ORDER_NO"));
                dto.setOrderType(rs.getString("ORDER_TYPE"));
                dto.setTotalAmount(rs.getInt("TOTAL_AMOUNT"));
                dto.setOrderStatus(rs.getInt("ORDER_STATUS"));
                if (rs.getTimestamp("ORDER_DATE") != null) {
                    dto.setOrderDate(rs.getTimestamp("ORDER_DATE").toLocalDateTime());
                }
            }
        } finally {
            DbConn.getInstance().dbClose(rs, pstmt, conn);
        }
        return dto;
    }
}
