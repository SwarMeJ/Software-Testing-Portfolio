package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.*;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.sql.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.data.Restaurant;

import static org.junit.Assert.*;


import org.junit.Test;
import uk.ac.ed.inf.*;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class IntegrationTest {

    private static final double TOLERANCE = 0.0001;

    private boolean areLngLatEqual(LngLat actual, LngLat expected, double tolerance) {
        return Math.abs(actual.lng() - expected.lng()) <= tolerance &&
                Math.abs(actual.lat() - expected.lat()) <= tolerance;
    }

    @Test
    public void testOrderValidationAndDeliveryCreation() {
        Pizza pizza1 = new Pizza("Margherita", 1000);
        Pizza pizza2 = new Pizza("Pepperoni", 1200);
        Pizza[] menu = {pizza1, pizza2};
        Restaurant restaurant = new Restaurant(
                "Pizza Palace",
                new LngLat(-3.186874, 55.944494),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY},
                menu
        );

        Order order = new Order(
                "ORDER001",
                LocalDate.of(2025, 1, 1),
                OrderStatus.UNDEFINED,
                null,
                2200,
                new Pizza[]{pizza1, pizza2},
                null
        );

        Restaurant[] restaurants = {restaurant};
        OrderValidator validator = new OrderValidator();

        CreditCardInformation test = new CreditCardInformation("1349947269650412","06/28","952");
        order.setCreditCardInformation(test);

        Order validatedOrder = validator.validateOrder(order, restaurants);
        Deliveries delivery = new Deliveries(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
        assertEquals("ORDER001", delivery.OrderNo);
        assertEquals(2200, delivery.costInPence);
    }

    @Test
    public void testPathFindingIntegration() {
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);

        NamedRegion noFlyZone = new NamedRegion("NoFlyZone", new LngLat[]{
                new LngLat(-3.186700, 55.944600),
                new LngLat(-3.186800, 55.944700),
                new LngLat(-3.186600, 55.944800),
                new LngLat(-3.186500, 55.944600)
        });
        Set<NamedRegion> noFlyZones = new HashSet<>();
        noFlyZones.add(noFlyZone);

        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{
                new LngLat(-3.187500, 55.944000),
                new LngLat(-3.187500, 55.945500),
                new LngLat(-3.185500, 55.945500),
                new LngLat(-3.185500, 55.944000)
        });

        List<LngLat> path = FlightAStar.astar(start, goal, noFlyZones, centralArea, 100);

        assertNotNull(path);
        assertTrue(areLngLatEqual(start, path.get(0), TOLERANCE));
        assertTrue(areLngLatEqual(goal, path.get(path.size() - 1), TOLERANCE));

        LngLatHandler handler = new LngLatHandler();
        for (LngLat point : path) {
            assertFalse(handler.isInRegion(point, noFlyZone));
        }
    }

    @Test
    public void testMoveAndPathsMappedIntegration() {
        PathsMapped pathsMapped = new PathsMapped();
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);

        List<LngLat> path = new ArrayList<>();
        path.add(start);
        path.add(new LngLat(-3.186500, 55.944700));
        path.add(goal);

        pathsMapped.addPath(path, goal);

        List<LngLat> retrievedPath = pathsMapped.getPath(goal);

        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < retrievedPath.size() - 1; i++) {
            LngLat from = retrievedPath.get(i);
            LngLat to = retrievedPath.get(i + 1);
            moves.add(new Move("ORDER001", from.lng(), from.lat(), to.lng(), to.lat()));
        }

        assertNotNull(retrievedPath);
        assertEquals(path.size(), retrievedPath.size());
        assertEquals(path.size() - 1, moves.size());

        for (int i = 0; i < path.size(); i++) {
            assertTrue(areLngLatEqual(path.get(i), retrievedPath.get(i), TOLERANCE));
        }

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            assertTrue(areLngLatEqual(new LngLat(move.fromLongitude, move.fromLatitude), retrievedPath.get(i), TOLERANCE));
            assertTrue(areLngLatEqual(new LngLat(move.toLongitude, move.toLatitude), retrievedPath.get(i + 1), TOLERANCE));
        }
    }
}
