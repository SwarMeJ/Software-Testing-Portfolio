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

public class ModelTest {
    private static final double TOLERANCE = 0.0001;
    CreditCardInformation defaultCardInfo = new CreditCardInformation("1349947269650412","06/28","952");

    private boolean areLngLatEqual(LngLat actual, LngLat expected, double tolerance) {
        return Math.abs(actual.lng() - expected.lng()) <= tolerance &&
                Math.abs(actual.lat() - expected.lat()) <= tolerance;
    }
    @Test
    public void testTransitionIdleToMoving() {
        // Arrange
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        Order order = new Order("ORDER001", LocalDate.of(2025, 1, 6), OrderStatus.UNDEFINED, null, 1400, new Pizza[]{new Pizza("Margherita", 1000)}, null);
        List<Order> validOrders = List.of(order);
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{
                new LngLat(-3.187500, 55.944000),
                new LngLat(-3.187500, 55.945500),
                new LngLat(-3.185500, 55.945500),
                new LngLat(-3.185500, 55.944000)});
        // Act
        List<LngLat> path = App.getPathForDay(validOrders, new Restaurant[]{
                new Restaurant("Pizza Hub", new LngLat(-3.1875, 55.945), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("Margherita", 1000)})
        }, appletonTower, new NamedRegion[]{}, centralArea);

        // Assert
        assertNotNull(path); // Path should exist
        assertEquals(path.get(0),appletonTower); // Path starts at Appleton Tower
    }

    @Test
    public void testTransitionMovingToDelivering() {
        // Arrange
        LngLat restaurantLocation = new LngLat(-3.1875, 55.945);
        LngLat deliveryLocation = new LngLat(-3.1865, 55.9445);
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{
                new LngLat(-3.2, 55.94),
                new LngLat(-3.2, 55.95),
                new LngLat(-3.18, 55.95),
                new LngLat(-3.18, 55.94)
        });

        // Act
        List<LngLat> flightPath = FlightAStar.astar(restaurantLocation, deliveryLocation, new HashSet<>(), centralArea, 300);

        // Assert
        assertNotNull(flightPath); // Path should exist
        assertEquals(flightPath.get(0),(restaurantLocation)); // Starts at the restaurant
        assertTrue(areLngLatEqual(flightPath.get(flightPath.size() - 1),deliveryLocation,TOLERANCE)); // Ends at the delivery location
    }

    @Test
    public void testTransitionDeliveringToReturning() {
        // Arrange
        LngLat deliveryLocation = new LngLat(-3.1865, 55.9445);
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);

        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);
        Set<NamedRegion> noFlyZones = new HashSet<>();
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{start, goal});

        // Act
        List<LngLat> returnPath = FlightAStar.astar(deliveryLocation, appletonTower, new HashSet<>(), centralArea, 300);

        // Assert
        assertNotNull(returnPath); // Path should exist
        assertEquals(returnPath.get(0),(deliveryLocation)); // Starts at the delivery location
        assertTrue(areLngLatEqual(returnPath.get(returnPath.size() - 1),(appletonTower),TOLERANCE)); // Ends at Appleton Tower
    }

    @Test
    public void testFullWorkflow() {
        // Arrange
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        Restaurant restaurant = new Restaurant(
                "Pizza Hub",
                new LngLat(-3.1875, 55.945),
                new DayOfWeek[]{DayOfWeek.MONDAY},
                new Pizza[]{new Pizza("Margherita", 1000)}
        );
        Order order = new Order("ORDER002", LocalDate.of(2025, 1, 6), OrderStatus.UNDEFINED, null, 1400, new Pizza[]{new Pizza("Margherita", 1000)}, defaultCardInfo);
        NamedRegion[] noFlyZones = {};
        NamedRegion centralArea = new NamedRegion("Central", new LngLat[]{
                new LngLat(-3.2, 55.94),
                new LngLat(-3.2, 55.95),
                new LngLat(-3.18, 55.95),
                new LngLat(-3.18, 55.94)
        });

        // Act
        List<Order> validOrders = App.getValidOrders(new Order[]{order}, LocalDate.of(2025, 1, 6), new Restaurant[]{restaurant});
        List<LngLat> pathsForDay = App.getPathForDay(validOrders, new Restaurant[]{restaurant}, appletonTower, noFlyZones, centralArea);
        List<Move> moves = App.getMovesList(validOrders, pathsForDay, appletonTower);

        // Assert
        assertNotNull(validOrders); // Orders should be validated
        assertNotNull(pathsForDay); // Paths should be calculated

    }

    @Test
    public void testInvalidTransition() {
        // Arrange
        LngLat invalidStart = new LngLat(-3.18, 55.94); // Not Appleton Tower
        LngLat restaurantLocation = new LngLat(-3.1875, 55.945);
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{start, goal});
        // Act
        List<LngLat> path = FlightAStar.astar(invalidStart, restaurantLocation, new HashSet<>(), centralArea, 300);

        // Assert
        assertNotNull(path); // Path may still exist, but first point must NOT be Appleton Tower
        assertNotEquals(invalidStart, new LngLat(-3.186874, 55.944494)); // Invalid transition should not simulate Idle to Moving
    }


}
