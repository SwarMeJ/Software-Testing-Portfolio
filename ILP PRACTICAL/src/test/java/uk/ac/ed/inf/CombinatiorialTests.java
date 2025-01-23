package uk.ac.ed.inf;




import org.junit.Test;
import uk.ac.ed.inf.*;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

    public class CombinatiorialTests {
        CreditCardInformation defaultCardInfo = new CreditCardInformation("1349947269650412","06/28","952");
        // Mock restaurant data
        Restaurant mockRestaurant = new Restaurant(
                "Mock Pizza Place",
                new LngLat(-3.186874, 55.944494),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY},
                new Pizza[]{
                        new Pizza("Margherita", 1000),
                        new Pizza("Calzone", 1400)
                }
        );

        // Mock data for testing
        Pizza pizza1 = new Pizza("Margherita", 1000);
        Pizza pizza2 = new Pizza("Calzone", 1400);
        Pizza invalidPizza = new Pizza("Invalid Pizza", 500);

        Restaurant[] restaurants = {mockRestaurant};

        @Test
        public void testOrderDateAndRestaurantOpening() {
            LocalDate validDate = LocalDate.of(2025, 1, 6); // Monday
            LocalDate invalidDate = LocalDate.of(2025, 1, 8); // Wednesday
            Order validOrder = new Order("ORDER001", validDate, OrderStatus.UNDEFINED, null, 2400, new Pizza[]{pizza1, pizza2}, defaultCardInfo);
            Order invalidOrder = new Order("ORDER002", invalidDate, OrderStatus.UNDEFINED, null, 2400, new Pizza[]{pizza1, pizza2}, defaultCardInfo);

            OrderValidator validator = new OrderValidator();
            Order validatedValidOrder = validator.validateOrder(validOrder, restaurants);
            Order validatedInvalidOrder = validator.validateOrder(invalidOrder, restaurants);

            assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedValidOrder.getOrderValidationCode());
            assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedInvalidOrder.getOrderValidationCode());
        }

        @Test
        public void testPaymentDetailsCombinations() {
            // Arrange
            CreditCardInformation validCard = new CreditCardInformation("1234567812345678", "06/28", "123");
            CreditCardInformation invalidCardNumber = new CreditCardInformation("12345", "06/28", "123");
            CreditCardInformation invalidExpiry = new CreditCardInformation("1234567812345678", "06/21", "123");
            CreditCardInformation invalidCVV = new CreditCardInformation("1234567812345678", "06/28", "12");

            Order order = new Order("ORDER003", LocalDate.of(2025, 1, 6), OrderStatus.UNDEFINED, null, 2400, new Pizza[]{pizza1, pizza2}, null);

            // Act & Assert
            OrderValidator validator = new OrderValidator();

            order.setCreditCardInformation(validCard);
            assertEquals(OrderValidationCode.TOTAL_INCORRECT, validator.validateOrder(order, restaurants).getOrderValidationCode());

            order.setCreditCardInformation(invalidCardNumber);
            assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validator.validateOrder(order, restaurants).getOrderValidationCode());

            order.setCreditCardInformation(invalidExpiry);
            assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validator.validateOrder(order, restaurants).getOrderValidationCode());

            order.setCreditCardInformation(invalidCVV);
            assertEquals(OrderValidationCode.CVV_INVALID, validator.validateOrder(order, restaurants).getOrderValidationCode());
        }

        @Test
        public void testOrderSizeCombinations() {
            // Arrange
            Pizza[] validOrderSize = {pizza1, pizza2};
            Pizza[] invalidOrderSize = {pizza1, pizza1, pizza1, pizza1, pizza1}; // Exceeds maximum limit

            Order validOrder = new Order("ORDER004", LocalDate.of(2025, 1, 6), OrderStatus.UNDEFINED, null, 2400, validOrderSize, defaultCardInfo);
            Order invalidOrder = new Order("ORDER005", LocalDate.of(2025, 1, 6), OrderStatus.UNDEFINED, null, 5000, invalidOrderSize, defaultCardInfo);

            // Act
            OrderValidator validator = new OrderValidator();

            Order validatedValidOrder = validator.validateOrder(validOrder, restaurants);
            Order validatedInvalidOrder = validator.validateOrder(invalidOrder, restaurants);

            // Assert
            assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedValidOrder.getOrderValidationCode());
        }

        @Test
        public void testFlightPathWithNoFlyZones() {
            // Arrange
            LngLat appletonTower = new LngLat(-3.186874, 55.944494);
            LngLat restaurantLocation = new LngLat(-3.1875, 55.9449);
            NamedRegion noFlyZone = new NamedRegion("central", new LngLat[]{
                    new LngLat(-3.1869, 55.9446),
                    new LngLat(-3.1871, 55.9446),
                    new LngLat(-3.1871, 55.9448),
                    new LngLat(-3.1869, 55.9448)
            });

            Set<NamedRegion> noFlyZones = new HashSet<>();
            noFlyZones.add(noFlyZone);

            // Act
            List<LngLat> flightPath = FlightAStar.astar(appletonTower, restaurantLocation, noFlyZones, noFlyZone, 300);

            // Assert
            assertNotNull(flightPath);
            for (LngLat point : flightPath) {
                assertFalse(new LngLatHandler().isInRegion(point, noFlyZone));
            }
        }
    }

