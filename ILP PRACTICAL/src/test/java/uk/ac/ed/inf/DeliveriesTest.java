package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class DeliveriesTest {

    private Order createOrder(String orderNo, OrderStatus status, OrderValidationCode validationCode, int totalCost, Pizza[] pizzas) {
        CreditCardInformation cardInfo = new CreditCardInformation("1234567812345678", "12/25", "123");
        return new Order(orderNo, LocalDate.of(2024, 1, 1), status, validationCode, totalCost, pizzas, cardInfo);
    }

    @Test
    public void testValidDeliveryConversion() {
        Pizza pizza1 = new Pizza("Margherita", 1000);
        Pizza pizza2 = new Pizza("Pepperoni", 1200);
        Order validOrder = createOrder(
                "ORDER001",
                OrderStatus.VALID_BUT_NOT_DELIVERED,
                OrderValidationCode.NO_ERROR,
                2500,
                new Pizza[]{pizza1, pizza2}
        );

        Deliveries delivery = new Deliveries(validOrder);

        assertEquals("ORDER001", delivery.OrderNo);
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, delivery.orderStatus);
        assertEquals(OrderValidationCode.NO_ERROR, delivery.orderValidationCode);
        assertEquals(2500, delivery.costInPence);
    }

    @Test
    public void testInvalidOrderConversion() {
        Order invalidOrder = createOrder(
                "ORDER002",
                OrderStatus.INVALID,
                OrderValidationCode.CARD_NUMBER_INVALID,
                0,
                new Pizza[]{}
        );

        Deliveries delivery = new Deliveries(invalidOrder);

        assertEquals("ORDER002", delivery.OrderNo);
        assertEquals(OrderStatus.INVALID, delivery.orderStatus);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, delivery.orderValidationCode);
        assertEquals(0, delivery.costInPence);
    }

    @Test
    public void testDeliveryWithZeroCost() {
        Pizza pizza = new Pizza("Free Pizza", 0);
        Order zeroCostOrder = createOrder(
                "ORDER003",
                OrderStatus.VALID_BUT_NOT_DELIVERED,
                OrderValidationCode.NO_ERROR,
                0,
                new Pizza[]{pizza}
        );

        Deliveries delivery = new Deliveries(zeroCostOrder);

        assertEquals("ORDER003", delivery.OrderNo);
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, delivery.orderStatus);
        assertEquals(OrderValidationCode.NO_ERROR, delivery.orderValidationCode);
        assertEquals(0, delivery.costInPence);
    }

    @Test
    public void testDeliveryStatusChangeEffect() {
        Pizza pizza = new Pizza("Hawaiian", 1200);
        Order order = createOrder(
                "ORDER004",
                OrderStatus.VALID_BUT_NOT_DELIVERED,
                OrderValidationCode.NO_ERROR,
                1200,
                new Pizza[]{pizza}
        );

        Deliveries delivery = new Deliveries(order);
        order.setOrderStatus(OrderStatus.DELIVERED);

        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, delivery.orderStatus);
        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
    }

    @Test
    public void testEmptyPizzaArray() {
        Order emptyPizzaOrder = createOrder(
                "ORDER005",
                OrderStatus.INVALID,
                OrderValidationCode.PIZZA_NOT_DEFINED,
                0,
                new Pizza[]{}
        );

        Deliveries delivery = new Deliveries(emptyPizzaOrder);

        assertEquals("ORDER005", delivery.OrderNo);
        assertEquals(OrderStatus.INVALID, delivery.orderStatus);
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, delivery.orderValidationCode);
        assertEquals(0, delivery.costInPence);
    }

    @Test
    public void testInitialisation(){
        Deliveries test = new Deliveries("test",OrderStatus.UNDEFINED,OrderValidationCode.UNDEFINED,0);
        assertTrue(test != null);
    }
}

