package kr.co.sist.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.common.db.DbConn;
import kr.co.sist.user.dao.OrderDAO;
import kr.co.sist.user.dao.PaymentDAO;
import kr.co.sist.user.dto.CartDTO;
import kr.co.sist.user.dto.MenuOptionDTO;
import kr.co.sist.user.dto.OrderDTO;
import kr.co.sist.user.dto.OrderDetailDTO;
import kr.co.sist.user.dto.OrderOptionDTO;
import kr.co.sist.user.dto.PaymentDTO;

public class OrderService {
    private OrderDAO orderDAO;
    private PaymentDAO paymentDAO;

    public OrderService() {
        this.orderDAO = OrderDAO.getInstance();
        this.paymentDAO = PaymentDAO.getInstance();
    }

    /**
     * 주문(ORDERS) -> 주문상세(ORDER_DETAIL) -> 주문옵션(ORDER_OPTION) -> 결제(PAYMENT)를
     * 하나의 트랜잭션으로 처리한다.
     *
     * @param items       결제 대상 장바구니/바로주문 목록
     * @param orderType   주문 유형 ("EAT_IN" / "TAKE_OUT" / "매장" / "포장")
     * @param paymentType 결제 수단 (0: 카드 "CARD", 1: 현금 "CASH")
     */
    public OrderDTO placeOrder(List<CartDTO> items, String orderType, int paymentType) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("결제할 주문 항목이 비어 있습니다.");
        }

        int totalAmount = 0;
        for (CartDTO item : items) {
            totalAmount += item.getLineAmount();
        }

        Connection conn = null;
        OrderDTO order = new OrderDTO();

        try {
            DbConn db = DbConn.getInstance();
            conn = db.getConn(db.getProp(null));
            if (conn == null) {
                throw new SQLException("DB 연결 실패: config/database.properties 경로 또는 설정을 확인하세요.");
            }
            conn.setAutoCommit(false);

            long orderId = orderDAO.selectNextOrderId(conn);
            String orderNo = String.format("A-%04d", orderId);

            order.setOrderId(orderId);
            order.setOrderNo(orderNo);
            order.setOrderType(normalizeOrderType(orderType));
            order.setTotalAmount(totalAmount);
            // 관리자 주문관리(AdminOrderDAO) 기준: 1 = 제작중, 2 = 제작완료
            order.setOrderStatus(1);
            order.setOrderDate(LocalDateTime.now());

            orderDAO.insertOrder(conn, order);

            for (CartDTO item : items) {
                // 1번 파트(lsj) MenuDTO 연동 완료 시 아래처럼 getMenu() 우선 사용 가능:
                // long menuId = (item.getMenu() != null && item.getMenu().getMenuId() > 0)
                //         ? item.getMenu().getMenuId()
                //         : orderDAO.selectMenuIdByName(conn, item.getMenuName());
                long menuId = orderDAO.selectMenuIdByName(conn, item.getMenuName());
                int basePriceSnapshot = orderDAO.selectBasePriceByMenuId(conn, menuId, item.getUnitPrice());

                OrderDetailDTO detail = new OrderDetailDTO();
                detail.setOrderId(orderId);
                detail.setMenuId(menuId);
                detail.setMenuNameSnapshot(item.getMenuName());
                detail.setBasePriceSnapshot(basePriceSnapshot);
                detail.setUnitPrice(item.getUnitPrice());
                detail.setQuantity(item.getQuantity());
                detail.setLineAmount(item.getLineAmount());

                orderDAO.insertOrderDetail(conn, detail);

                List<MenuOptionDTO> selectedOptions = new ArrayList<>();
                if (item.getTemperatureOption() != null) {
                    selectedOptions.add(item.getTemperatureOption());
                }
                if (item.getSizeOption() != null) {
                    selectedOptions.add(item.getSizeOption());
                }
                if (item.getExtraOptions() != null) {
                    selectedOptions.addAll(item.getExtraOptions());
                }

                for (MenuOptionDTO opt : selectedOptions) {
                    long optionId = opt.getOptionId();
                    if (optionId <= 0) {
                        optionId = orderDAO.selectOptionIdByName(conn, opt.getOptionName());
                    }
                    if (optionId > 0) {
                        OrderOptionDTO orderOption = new OrderOptionDTO();
                        orderOption.setDetailId(detail.getDetailId());
                        orderOption.setOptionId(optionId);
                        orderDAO.insertOrderOption(conn, orderOption);
                    }
                }
            }

            PaymentDTO payment = new PaymentDTO();
            payment.setOrderId(orderId);
            payment.setPaymentMethod(paymentType == 0 ? "CARD" : "CASH");
            payment.setPaymentStatus("COMPLETED");
            payment.setPaymentAmount(totalAmount);
            payment.setPaymentDate(LocalDateTime.now());

            paymentDAO.insertPayment(conn, payment);

            conn.commit();
            return order;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException("주문/결제 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignore) {
                }
                DbConn.getInstance().dbClose(null, null, conn);
            }
        }
    }

    private String normalizeOrderType(String orderType) {
        if (orderType != null) {
            String upper = orderType.trim().toUpperCase();
            if ("TAKE_OUT".equals(upper) || "TAKEOUT".equals(upper) || "포장".equals(upper)) {
                return "포장";
            }
        }
        return "매장";
    }

    public List<OrderDetailDTO> searchOrderDetails(long orderId) throws SQLException {
        return orderDAO.selectOrderDetails(orderId);
    }
<<<<<<< HEAD
    public OrderDTO searchOrder(long orderId) throws SQLException {
        return orderDAO.selectOneOrder(orderId);
    }

    public OrderDTO searchOrderByNo(String orderNo) throws SQLException {
        return orderDAO.selectOrderByNo(orderNo);
    }
=======
>>>>>>> refs/remotes/origin/lsj
}
