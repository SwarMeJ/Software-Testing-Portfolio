package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import java.lang.Math;

public class LngLatHandler implements LngLatHandling {
    public double distanceTo(LngLat startPosition, LngLat endPosition){
        double Xdist = startPosition.lng() - endPosition.lng();
        Xdist = Xdist * Xdist;   //getting the (x1 - x2)^2
        double Ydist = startPosition.lat() - endPosition.lat();
        Ydist = Ydist * Ydist;  //getting the (y1 - y2)^2
        double distance = Xdist + Ydist;
        distance = Math.sqrt(distance);
        return distance;
    }

    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition){
        double distance = distanceTo(startPosition, otherPosition);
        if (distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE){ //was <=
            return true;
        }
        return false;
    }

    public boolean isInRegion(LngLat position, NamedRegion region){ //using ray casting method casting to the right
        int crosses = 0; //variable to store how many times the point will cross the edges made by the vertices given
        for (int i = 0; i < region.vertices().length; i++ ){ //loops though the region array to get the vertices

            LngLat vertex1;
            LngLat vertex2;

            if ( i == (region.vertices().length -1) ){ //if it approaches the end and needs to check for the edge from the last vertex to the first
                vertex1 = region.vertices()[i];
                vertex2 = region.vertices()[0];

            } else { //else use the vertex currently on and the next one to make the edge
                vertex1 = region.vertices()[i];
                vertex2 = region.vertices()[i+1];
            }
            double x1 = vertex1.lng();
            double y1 = vertex1.lat();
            double x2 = vertex2.lng();
            double y2 = vertex2.lat();
            double x = position.lng();
            double y = position.lat();
            if ((y < y1) != (y < y2)){ //check if the position has lat values within the range that it could possibly cross

                if ( x < x1 + ((y - y1)/(y2-y1)) * (x2-x1)){
                    crosses += 1;
                }
            }
        }
        return crosses % 2 == 1; //checking if the the number of crosses is even or odd which shows if it is inside the polygon or not
    }

    public LngLat nextPosition(LngLat startPosition, double angle){
        double angleInRadians = (angle * Math.PI) / 180;
        double lngNew = startPosition.lng() + SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleInRadians);
        double latNew = startPosition.lat() + SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleInRadians);
        return new LngLat(lngNew,latNew);
    }

}
