package kr.co.sist.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.user.dao.OrderDAO;
import kr.co.sist.user.dao.PaymentDAO;
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
            conn = DbConn.getInstance().getConn();
            conn.setAutoCommit(false);

            long orderId = orderDAO.selectNextOrderId(conn);
            String orderNo = String.format("A-%03d", orderId % 1000);

            order.setOrderId(orderId);
            order.setOrderNo(orderNo);
            order.setOrderType((orderType == null || orderType.isEmpty()) ? "매장" : orderType);
            order.setTotalAmount(totalAmount);
            order.setOrderStatus(0);
            order.setOrderDate(LocalDateTime.now());

            orderDAO.insertOrder(conn, order);

            for (CartDTO item : items) {
                OrderDetailDTO detail = new OrderDetailDTO();
                detail.setOrderId(orderId);
                detail.setMenuId(item.getMenu() != null ? item.getMenu().getMenuId() : 0L);
                detail.setMenuNameSnapshot(item.getMenuName());
                detail.setBasePriceSnapshot(item.getMenu() != null ? item.getMenu().getBasePrice() : item.getUnitPrice());
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
                    OrderOptionDTO orderOption = new OrderOptionDTO();
                    orderOption.setDetailId(detail.getDetailId());
                    orderOption.setOptionId(opt.getOptionId());
                    orderDAO.insertOrderOption(conn, orderOption);
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

    public List<OrderDetailDTO> searchOrderDetails(long orderId) throws SQLException {
        return orderDAO.selectOrderDetails(orderId);
    }
}
