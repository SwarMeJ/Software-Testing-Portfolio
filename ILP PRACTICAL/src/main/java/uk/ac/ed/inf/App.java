package uk.ac.ed.inf;
import java.sql.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.data.Restaurant;

public class App
{
    public static void main( String[] args )
    {
        //a try catch for the passed in arguements of website name and date
        try {
            //for time to run
            long startTime = System.nanoTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            RESTget restdata = new RESTget(args[1]); //args[1] is the base url
            LocalDate date = LocalDate.parse(args[0],formatter);

            //setting the appletonTower LngLat
            LngLat appletonTower = new LngLat(-3.186874, 55.944494);

            //getting information from the REST server
            Order[] orders = restdata.getOrders();
            Restaurant[] restaurants = restdata.getRestaurants();
            NamedRegion centralArea = restdata.getCentralArea();
            NamedRegion[] NoFlyZones = restdata.getNoFlyZones();


            List<Order> todaysOrders = getValidOrders(orders, date, restaurants);


            List<LngLat> pathsForDay = getPathForDay(todaysOrders, restaurants, appletonTower, NoFlyZones, centralArea);

            List<Deliveries> deliveries = getDeliveriesFormat(todaysOrders);

            List<Move> moves = getMovesList(todaysOrders, pathsForDay, appletonTower);

            FileHandler fileHandler = new FileHandler();

            fileHandler.recordMove(moves, date);
            fileHandler.recordDelivery(deliveries, date);
            fileHandler.recordGeoJson(pathsForDay, date);


            //for the run time
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            System.out.println(totalTime / 1000000000 + " Seconds");

        } catch (Exception e){
            System.out.println("Invalid inputs");
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static List<Order> getValidOrders(Order[] orders, LocalDate date, Restaurant[] restaurants){
        //filtering the orders by date and checking if valid to produce a list to be used for flightpath
        List<Order> filteredOrders = new ArrayList<>();
        OrderValidator validator = new OrderValidator();
        for (Order order : orders){
            if (order.getOrderDate().equals(date)){
                validator.validateOrder(order,restaurants);
                if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                    filteredOrders.add(order);
                }
            }
        }
        return filteredOrders;

    }

    public static List<Deliveries> getDeliveriesFormat(List<Order> orders){
        //turning the orders into the delivery format
        List<Deliveries> deliveriesList = new ArrayList<>();
        for(Order order : orders){
            order.setOrderStatus(OrderStatus.DELIVERED); //as it is going to be delivered change the status
            Deliveries delivery = new Deliveries(order);
            deliveriesList.add(delivery);
        }
        return deliveriesList;
    }

    public static List<LngLat> getPathForDay(List<Order> todaysOrders, Restaurant[] restaurants, LngLat appletonTower
                                                ,NamedRegion[] NoFlyZones, NamedRegion centralArea){
        PathsMapped madePaths = new PathsMapped();
        List<LngLat> pathsForDay = new ArrayList<>();

        for (Order order : todaysOrders){
            LngLat goal = new LngLat(0,0);
            Pizza pizza = order.getPizzasInOrder()[0];
            for(Restaurant restaurant : restaurants){ //to find the restaurant for the pizza
                List<Pizza> pizzaListRestaurant = new ArrayList<>(Arrays.asList(restaurant.menu()));
                if (pizzaListRestaurant.contains(pizza)) {
                    goal = restaurant.location();
                    break;
                }
            }

            Set<NamedRegion> namedAreas = new HashSet<>(Arrays.asList(NoFlyZones));

            if(!madePaths.isPathed(goal)) {

                //for if the path hasnt been made
                List<LngLat> path = FlightAStar.astar(appletonTower, goal, namedAreas, centralArea, 300);

                List<LngLat> pathToKeep = new ArrayList<>(); //list to store the flightpath there and back
                pathToKeep.addAll(path);
                Collections.reverse(path); //for the path backwards which is just the reverse
                pathToKeep.addAll(path);
                pathsForDay.addAll(pathToKeep);

                madePaths.addPath(pathToKeep, goal); //adding the new path to the list of already pathed routes
            } else {
                //for if the path is already made get the already made path and add it
                List<LngLat> path = madePaths.getPath(goal);
                pathsForDay.addAll(path);
            }
        }
        return pathsForDay;
    }

    public static List<Move> getMovesList(List<Order> todaysOrders, List<LngLat> flightpath, LngLat appleton){
        int hoveringCount = 0;
        int orderIndex = 0;
        boolean flag;
        List<Move> moves = new ArrayList<>();
        for(int i = 0; i < flightpath.size() -1; i++){

            flag = false;
            //check if it is hovering
            if(flightpath.get(i).lng() == flightpath.get(i+1).lng() && flightpath.get(i).lat() ==
                    flightpath.get(i+1).lat()){
                    flag = true;
            }
            if(flag) {
                //if its hovering
                Move thisMove = new Move(todaysOrders.get(orderIndex).getOrderNo(),flightpath.get(i).lng(),
                        flightpath.get(i).lat(),flightpath.get(i+1).lng(),flightpath.get(i+1).lat());
                hoveringCount += 1;
                if (hoveringCount == 2){
                    orderIndex += 1;
                    hoveringCount = 0;
                }
                thisMove.setAngle(999);
                moves.add(thisMove);
            } else{
                //if it is not hovering
                Move thisMove = new Move(todaysOrders.get(orderIndex).getOrderNo(),flightpath.get(i).lng(),
                        flightpath.get(i).lat(),flightpath.get(i+1).lng(),flightpath.get(i+1).lat());
                moves.add(thisMove);
            }
        }
        return moves;
    }

}