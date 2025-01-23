package uk.ac.ed.inf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.data.LngLat;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.time.format.DateTimeFormatter;


public class FileHandler {

    //takes a list of Deliveries objects and a date and writes these to a file for that date
    public void recordDelivery(List<Deliveries> deliveries, LocalDate date) {
        Gson gson = new Gson();
        String filename = "deliveries-" + dateToString(date) + ".json";
        try {
            String dirname = "resultfiles";
            File dir = new File(dirname);
            File actualfile = new File(dir,filename);
            FileWriter fileWriter = new FileWriter(actualfile);
            gson.toJson(deliveries, fileWriter);
            fileWriter.close();
        } catch(Exception ignored){
            System.out.println("deliveries file writing failed");
            System.exit(2);
        }

    }
    //takes a list of object type move and a date and writes these to the file in json format
    public void recordMove(List<Move> moves, LocalDate date) {
        Gson gson = new GsonBuilder().create();
        String filename = "flightpath-" + dateToString(date) + ".json";
        try {
            String dirname = "resultfiles";
            File dir = new File(dirname);
            File actualfile = new File(dir,filename);
            FileWriter fileWriter = new FileWriter(actualfile);
            gson.toJson(moves, fileWriter);
            fileWriter.close();
        } catch (Exception ignored) {
            System.out.println("flightpath file writing failed");
            System.exit(2);
        }
    }
    //takes a list of LngLat type and a date and records this in a file for that date in geojson format
    public void recordGeoJson(List<LngLat> flightpath, LocalDate date) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filename = "drone-" + dateToString(date) + ".geojson";
        JsonObject featureCollection = new JsonObject();
        featureCollection.addProperty("type", "FeatureCollection"); //making the featureCollection
        JsonArray feature = new JsonArray();
        JsonObject properties = new JsonObject();
        properties.addProperty("name","properties"); //adding the property

        JsonObject lineStringFeature = new JsonObject();
        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString"); //making it a lineString
        JsonArray coordinates = new JsonArray();
        for (LngLat point : flightpath) { //looping thought the list and making the LngLat as coordinates
            JsonArray coordinate = new JsonArray();
            coordinate.add(point.lng());
            coordinate.add(point.lat());
            coordinates.add(coordinate);
        }
        geometry.add("coordinates", coordinates); //adding the coordinates to the geometry

        lineStringFeature.addProperty("type","Feature"); //combining it all together
        lineStringFeature.add("geometry",geometry);
        lineStringFeature.add("properties",properties);
        feature.add(lineStringFeature);

        featureCollection.add("features", feature);
        try {
            String dirname = "resultfiles";
            File dir = new File(dirname);
            File actualfile = new File(dir,filename);
            FileWriter fileWriter = new FileWriter(actualfile);
            gson.toJson(featureCollection,fileWriter);
            fileWriter.close();

        } catch (Exception ignored) {
            System.out.println("geojson file writing failed");
            System.exit(2);
        }

    }
    //turns a LocalDate date into the string of that date
    public String dateToString(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

}
