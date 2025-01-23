package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;

class Deliveries {
    String OrderNo;
    OrderStatus orderStatus;
    OrderValidationCode orderValidationCode;
    int costInPence;

    public Deliveries(Order order){
        this.OrderNo = order.getOrderNo();
        this.orderStatus = order.getOrderStatus();
        this.orderValidationCode = order.getOrderValidationCode();
        this.costInPence = order.getPriceTotalInPence();
    }

    public Deliveries(String orderNo, OrderStatus orderstatus, OrderValidationCode noError, int i) {
    }
}
