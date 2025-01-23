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

/**
 * Unit test for simple App.
 */
public class OrderValidatorTest
{
    public Order makeOrder(int priceTotal, Pizza[] pizzas){
        return new Order("Example", LocalDate.of(2023,10,4), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, priceTotal,
                pizzas,null);
    }

    Pizza pizza1 = new Pizza("Super Cheese",1400);
    Pizza pizza2 = new Pizza("All Shrooms",900);
    Pizza[] defaultPizzas =  new Pizza[]{pizza1,pizza2};
    int priceTotalDefault = 2400;
    CreditCardInformation defaultCardInfo = new CreditCardInformation("1349947269650412","06/28","952");



    Restaurant rest1 = new Restaurant(
            "Domino's Pizza - Edinburgh - Southside",
            new LngLat(-3, 55),
            new DayOfWeek[]{DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
            new Pizza[]{new Pizza("Super Cheese", 1400), new Pizza("All Shrooms", 900)}
    );
    Restaurant rest2 = new Restaurant(
            "Sodeberg Pavilion",
            new LngLat(-3, 55),
            new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
            new Pizza[]{new Pizza("Proper Pizza", 1400), new Pizza("Pineapple & Ham & Cheese", 900)}
    );


    Restaurant[] definedRestaurants = {rest1, rest2};
    @Test
    public void checkInvalidCardLength(){
        Order thisOrder = makeOrder(priceTotalDefault, defaultPizzas);
        CreditCardInformation wrongCardLength = new CreditCardInformation("12222222","06/28","952");
        thisOrder.setCreditCardInformation(wrongCardLength);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.CARD_NUMBER_INVALID);
    }
    @Test
    public void checkDefaultWorks(){
        Order thisOrder = makeOrder(priceTotalDefault, defaultPizzas);
        thisOrder.setCreditCardInformation(defaultCardInfo);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.NO_ERROR);
    }
    @Test
    public void checkInvalidExpiryMonth(){
        Order thisOrder = makeOrder(priceTotalDefault, defaultPizzas);
        CreditCardInformation wrong = new CreditCardInformation("1111111111111111","06/23","952");
        thisOrder.setCreditCardInformation(wrong);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.EXPIRY_DATE_INVALID);
    }
    @Test
    public void checkInvalidExpiryYear(){
        Order thisOrder = makeOrder(priceTotalDefault, defaultPizzas);
        CreditCardInformation wrong = new CreditCardInformation("1111111111111111","12/22","952");
        thisOrder.setCreditCardInformation(wrong);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.EXPIRY_DATE_INVALID);
    }
    @Test
    public void checkInvalidCVV(){
        Order thisOrder = makeOrder(priceTotalDefault, defaultPizzas);
        CreditCardInformation wrong = new CreditCardInformation("1111111111111111","12/23","11");
        thisOrder.setCreditCardInformation(wrong);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.CVV_INVALID);
    }
    @Test
    public void checkSizeInvalid(){
        Pizza pizza1 = new Pizza("Super Cheese",1400);
        Pizza pizza2 = new Pizza("All Shrooms",900);
        Pizza[] tooManyPizzas =  new Pizza[]{pizza1,pizza2,pizza1,pizza1,pizza1};
        int priceForToMany = 6600;
        Order thisOrder = makeOrder(priceForToMany, tooManyPizzas);
        thisOrder.setCreditCardInformation(defaultCardInfo);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
    }
    @Test
    public void checkNoPizzas(){
        Pizza[] noPizza = new Pizza[]{};
        int priceForNone = 100;
        Order thisOrder = makeOrder(priceForNone, noPizza);
        thisOrder.setCreditCardInformation(defaultCardInfo);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.PIZZA_NOT_DEFINED);
    }
    @Test
    public void checkPizzaFromMultiple(){
        Pizza pizza1 = new Pizza("Proper Pizza",1400);
        Pizza pizza2 = new Pizza("All Shrooms",900);
        Pizza[] nonExistantPizzas = new Pizza[]{pizza1,pizza2};
        int priceForNonExistant = 2400;
        Order thisOrder = makeOrder(priceForNonExistant, nonExistantPizzas);
        thisOrder.setCreditCardInformation(defaultCardInfo);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
    }
    @Test
    public void checkPizzaNotDefined(){
        Pizza pizza1 = new Pizza("Doesnt Exist",1400);
        Pizza pizza2 = new Pizza("All Shrooms",900);
        Pizza[] nonExistantPizzas = new Pizza[]{pizza1,pizza2};
        int priceForNonExistant = 2400;
        Order thisOrder = makeOrder(priceForNonExistant, nonExistantPizzas);
        thisOrder.setCreditCardInformation(defaultCardInfo);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.PIZZA_NOT_DEFINED);
    }
    @Test
    public void checkRestaurantClosed(){
        Order thisOrder = new Order("Example", LocalDate.of(2023,10,3), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, priceTotalDefault,
                defaultPizzas,null);
        CreditCardInformation wrong = new CreditCardInformation("1111111111111111","12/23","110");
        thisOrder.setCreditCardInformation(wrong);
        Order validated = new OrderValidator().validateOrder(thisOrder,definedRestaurants);
        assertEquals(validated.getOrderValidationCode(),OrderValidationCode.RESTAURANT_CLOSED);
    }
    LngLat pos1 = new LngLat(-3.192473,55.946233);
    LngLat pos2 = new LngLat(-3.192473,55.942617);
    LngLat pos3 = new LngLat(-3.184319,55.942617);
    LngLat pos4 = new LngLat(-3.184319,55.946233);
    LngLat pos5 = new LngLat(-3.188396,55.944425);
    LngLat toCheckTrue = new LngLat(-3.1904345,55.944662);
    LngLat toCheckFalse = new LngLat(-3.2,55.944662);
    LngLat[] region = new LngLat[]{pos1,pos2,pos3,pos4,pos5};
    @Test
    public void checkIsInRegionTrue(){
        NamedRegion polygon = new NamedRegion("",region);
        boolean check = new LngLatHandler().isInRegion(toCheckTrue,polygon);
        assertTrue(check);
    }
    @Test
    public void checkIsInRegionFalse(){
        NamedRegion polygon = new NamedRegion("",region);
        boolean check = new LngLatHandler().isInRegion(toCheckFalse,polygon);
        assertFalse(check);
    }
    @Test
    public void checkNextPosition(){
        LngLat pos1 = new LngLat(-3.192473,55.946233);
        double angle = (180 * Math.PI) / 180 ;
        double angleSec = 180;
        double lngN = pos1.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angle));
        double latN = pos1.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angle));
        LngLat pos2 = new LngLat(lngN,latN);
        LngLat posfound = new LngLatHandler().nextPosition(pos1,angleSec);
        assertEquals(posfound,pos2);
    }
    @Test
    public void checkPathsMappedreturn(){
        PathsMapped paths = new PathsMapped();
        List<LngLat> path = new ArrayList<>();
        LngLat end = new LngLat(-3,55);
        path.add(new LngLat(-2,55));
        path.add(new LngLat(-3,55));
        paths.addPath(path,end);
        List<LngLat> returnedpath = paths.getPath(end);
        assertEquals(returnedpath,path);
    }
    @Test
    public void checkMoveAngleNEis45(){
        Move move = new Move("test",0,0,1,1);
        assertEquals(move.angle,45,0.001);
    }
    @Test
    public void checkMoveAngleWestIs180(){
        Move move = new Move("test",1,0,0,0);
        assertEquals(move.angle,180,0.001);
    }

    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}


