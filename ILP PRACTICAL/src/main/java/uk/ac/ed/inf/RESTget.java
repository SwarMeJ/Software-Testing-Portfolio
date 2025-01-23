package uk.ac.ed.inf;

import java.time.LocalDate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Type;


public class RESTget {

    private final String baseUrl;
    //pass in the baseUrl which is stored in the instance of RESTget when created
    public RESTget(String baseUrl) { //takes a url when a instance is created
        this.baseUrl = baseUrl;
    }
    //all gets apart from order are the same as this code
    public Restaurant[] getRestaurants() {
        String apiUrl = baseUrl + "/restaurants"; //adds on the url the path for the data
        // try to handle exceptions thrown from the get and just returns null in that case
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Parse the JSON response using Gson
                List<Restaurant> restaurants = parseJsonResponseForRestaurants(connection);

                return restaurants.toArray(new Restaurant[0]);
            }

        } catch (Exception e) {
            System.out.println("connection failed to URL");
            e.printStackTrace();
            return null;
        }

        return null;
    }
    public Order[] getOrders() {
        String apiUrl = baseUrl + "/orders";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new GsonBuilder()
                        //uses the Local deserializer
                        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                        .create();
                List<Order> orders = parseJsonResponseForOrders(connection, gson);
                return orders.toArray(new Order[0]);
            }
        } catch (Exception e) {
            System.out.println("connection failed to URL");
            e.printStackTrace();
            return null;
        }
        return null;
    }



    public NamedRegion getCentralArea() {
        String apiUrl = baseUrl + "/centralArea";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return parseJsonResponseForCentralArea(connection);
            }
        } catch (Exception e) {
            System.out.println("connection failed to URL");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public NamedRegion[] getNoFlyZones() {
        String apiUrl = baseUrl + "/noFlyZones";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                List<NamedRegion> noFlyZones = parseJsonResponseForNoFlyZones(connection);
                return noFlyZones.toArray(new NamedRegion[0]);
            }

        } catch (Exception e) {
            System.out.println("connection failed to URL");
            e.printStackTrace();
            return null;
        }

        return null;
    }
    //different parseJson for each as datatype returned is different code is the same apart from for central area
    // and order
    private static List<Restaurant> parseJsonResponseForRestaurants(HttpURLConnection connection) {
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            // Use Gson to parse the JSON array into a list of Restaurant objects
            return new Gson().fromJson(reader, new TypeToken<List<Restaurant>>() {}.getType());
        } catch (Exception e) {
            System.out.println("parsing failed");
            e.printStackTrace();
            return null;
        }
    }
    private static List<Order> parseJsonResponseForOrders(HttpURLConnection connection, Gson gson) {
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            // Use the changed Gson instance to parse the JSON array into a list of Order objects
            return gson.fromJson(reader, new TypeToken<List<Order>>() {}.getType());
        } catch (Exception e) {
            System.out.println("parsing failed");
            e.printStackTrace();
            return null;
        }
    }
    //changing the localdate deserializer to fit what i need it to do
    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        @Override //overrides the current method deserialize to change it to accomodate
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // different deserialization due to private LocalDate year field being an issue to access
            String dateString = json.getAsString();
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    //different as doesn't return a list as central area is only 1 object
    private static NamedRegion parseJsonResponseForCentralArea(HttpURLConnection connection) {
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            return new Gson().fromJson(reader, NamedRegion.class);
        } catch (Exception e) {
            System.out.println("parsing failed");
            e.printStackTrace();
            return null;
        }
    }

    private static List<NamedRegion> parseJsonResponseForNoFlyZones(HttpURLConnection connection) {
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            return new Gson().fromJson(reader, new TypeToken<List<NamedRegion>>() {}.getType());
        } catch (Exception e) {
            System.out.println("parsing failed");
            e.printStackTrace();
            return null;
        }
    }
}
