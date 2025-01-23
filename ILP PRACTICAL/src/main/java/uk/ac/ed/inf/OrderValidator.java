package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.time.DayOfWeek;
import java.util.*;

public class OrderValidator implements OrderValidation {
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        CreditCardInformation cardToCheck = orderToValidate.getCreditCardInformation();

        int thisMonth = orderToValidate.getOrderDate().getMonthValue();
        int thisYear = orderToValidate.getOrderDate().getYear() % 100;

        // check if card number is valid length and contains only numbers
        if (cardToCheck.getCreditCardNumber().length() != 16 || !(cardToCheck.getCreditCardNumber().matches("\\d+"))) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        // check if expiry date is valid if it contains a / with 4 numbers in 2 by 2 pattern
        String[] expirySplit = cardToCheck.getCreditCardExpiry().split("/");
        if (expirySplit.length == 2) { //checks it only has 1 / character
            //checks if both the parts of the expiry are positive integers
            if (expirySplit[0].length() != 2 || expirySplit[1].length() != 2
                    || !(expirySplit[0].matches("\\d+")) || !(expirySplit[1].matches("\\d+"))) {
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                return orderToValidate;
            } else {
                int expiryYear = Integer.parseInt(expirySplit[1]);
                if (!(expiryYear > thisYear)){ //checking if the expiry year is this year or not and therefore if the month needs to be checked
                    if(expiryYear == thisYear){
                        int expiryMonth = Integer.parseInt(expirySplit[0]);
                        if (expiryMonth < thisMonth || expiryMonth > 12) {
                            orderToValidate.setOrderStatus(OrderStatus.INVALID);
                            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                            return orderToValidate;
                        }
                    } else {
                        orderToValidate.setOrderStatus(OrderStatus.INVALID);
                        orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                        return orderToValidate;
                    }
                }
            }
        } else {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }


        // check if cvv is valid format
        if (cardToCheck.getCvv().length() != 3 || !(cardToCheck.getCvv().matches("\\d+"))) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        // check if order total is correct  NEED TO CHECK WITH RESTAURANT FOR PRICES
        int testTotal = 0;

        for (Pizza pizzas : orderToValidate.getPizzasInOrder()) {
            testTotal += pizzas.priceInPence();
        }
        testTotal += SystemConstants.ORDER_CHARGE_IN_PENCE;
        if (testTotal != orderToValidate.getPriceTotalInPence()) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        //check if ordered too many pizzas >4
        int size = orderToValidate.getPizzasInOrder().length; // to count the number of pizzas in the order
        if (size > 4) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if (size == 0) { //checking if the pizzas chosen is empty
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }





        Boolean[] isPizzaDefined = new Boolean[size]; // an array used to see if all the pizzas are found at...
        // least once among all the restaurants

        //check if pizzas chosen are all at the same restaurant
        int pizzaIsPresent; // counter to check how many of the pizzas chosen are at the particular restaurant
        int count;
        Restaurant chosenResturant = null;
        for (Restaurant restaurant : definedRestaurants) {
            pizzaIsPresent = 0;
            count = 0;
            List<Pizza> pizzaListRestaurant = new ArrayList<Pizza>(Arrays.asList(restaurant.menu()));
            for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
                if (pizzaListRestaurant.contains(pizza)) { //seeing if the restaurant has the particular pizza in the loop
                    pizzaIsPresent += 1;
                    isPizzaDefined[count] = true;
                }
                count += 1;

            }
            if (count == pizzaIsPresent) {
                chosenResturant = restaurant;
                break; //breaks out the outer for loop if a restaurant is found having all the desired pizzas
            }
        }
        List<Boolean> isPizzaDefinedList = new ArrayList<Boolean>(Arrays.asList(isPizzaDefined));
        if (isPizzaDefinedList.contains(null)){
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        } else if (chosenResturant == null) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            return orderToValidate;
        }
        //checking if the chosen restaurant is open on this current day
        DayOfWeek thisDay = orderToValidate.getOrderDate().getDayOfWeek();
        List<DayOfWeek> openDays = new ArrayList<DayOfWeek>(Arrays.asList(chosenResturant.openingDays()));
        if (!(openDays.contains(thisDay))){
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return orderToValidate;
        }

        // if all checks pass return the order is valid
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;



    }
}
