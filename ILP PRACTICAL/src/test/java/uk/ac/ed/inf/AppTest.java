package uk.ac.ed.inf;


import org.junit.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.constant.SystemConstants;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;
import uk.ac.ed.inf.*;
import uk.ac.ed.inf.ilp.data.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class AppTest {
    CreditCardInformation defaultCardInfo = new CreditCardInformation("1349947269650412","06/28","952");

    @Test
    public void testGetDeliveriesFormat() {

        Pizza pizza1 = new Pizza("Margherita", 1000);
        Pizza pizza2 = new Pizza("Calzone", 1400);

        Order order1 = new Order("ORDER001", LocalDate.of(2025, 1, 1), OrderStatus.UNDEFINED, null, 2400, new Pizza[]{pizza1, pizza2}, null);
        Order order2 = new Order("ORDER002", LocalDate.of(2025, 1, 1), OrderStatus.UNDEFINED, null, 1000, new Pizza[]{pizza1}, null);

        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);


        List<Deliveries> deliveries = App.getDeliveriesFormat(orders);


        assertEquals(2, deliveries.size());
        assertEquals("ORDER001", deliveries.get(0).OrderNo);
        assertEquals("ORDER002", deliveries.get(1).OrderNo);
        assertEquals(OrderStatus.DELIVERED, orders.get(0).getOrderStatus());
    }

    @Test
    public void testGetPathForDay() {

        Pizza pizza1 = new Pizza("Margherita", 1000);

        Restaurant mockRestaurant = new Restaurant(
                "Mock Pizza Place",
                new LngLat(-3.186874, 55.944494),
                new DayOfWeek[]{DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{pizza1}
        );

        List<Order> todaysOrders = new ArrayList<>();
        todaysOrders.add(new Order("ORDER001", LocalDate.of(2025, 1, 1), OrderStatus.UNDEFINED, null, 1000, new Pizza[]{pizza1}, null));

        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{appletonTower});
        NamedRegion[] noFlyZones = {};


        List<LngLat> pathsForDay = App.getPathForDay(todaysOrders, new Restaurant[]{mockRestaurant}, appletonTower, noFlyZones, centralArea);


        assertNotNull(pathsForDay);
        assertTrue(pathsForDay.size() > 0);
        assertEquals(appletonTower, pathsForDay.get(0));
    }

    @Test
    public void testGetMovesList() {

        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        List<LngLat> flightPath = new ArrayList<>();
        flightPath.add(appletonTower);
        flightPath.add(new LngLat(-3.186500, 55.944700));
        flightPath.add(new LngLat(-3.186000, 55.945000));

        List<Order> todaysOrders = new ArrayList<>();
        todaysOrders.add(new Order("ORDER001", LocalDate.of(2025, 1, 1), OrderStatus.DELIVERED, null, 2400, null, null));


        List<Move> moves = App.getMovesList(todaysOrders, flightPath, appletonTower);


        assertNotNull(moves);
        assertEquals(2, moves.size());
        assertEquals("ORDER001", moves.get(0).orderNo);
        assertEquals(30.963, moves.get(1).angle, 0.001);
    }

}
