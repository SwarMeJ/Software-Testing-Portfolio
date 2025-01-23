package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import java.util.ArrayList;
import java.util.List;

//class to store the flight paths made, it has the endpoints and list of lists of paths with the same index so lookup
// just requires a search for the matching endpoint index
public class PathsMapped {
    List<List<LngLat>> paths = new ArrayList<>();
    List<LngLat> endpoints = new ArrayList<>();
    public void addPath(List<LngLat> path, LngLat endpoint){
        paths.add(path);
        endpoints.add(endpoint);
    }
    public List<LngLat> getPath(LngLat endpoint) {
        int i = 0;
        for(LngLat end : endpoints){
            if(end.lng() == endpoint.lng() && end.lat() == endpoint.lat()){
                return paths.get(i);
            }
            i += 1;
        }
        return null;
    }
    public boolean isPathed(LngLat endpoint){
        for(LngLat end : endpoints) {
            if (end.lng() == endpoint.lng() && end.lat() == endpoint.lat()) {
                return true;
            }
        }
        return false;
    }
}
