package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.Move;

import static org.junit.Assert.*;

public class MoveTest {

    private static final double TOLERANCE = 0.001;

    @Test
    public void testMoveInitialization() {
        String orderNo = "ORDER001";
        double fromLongitude = -3.186874;
        double fromLatitude = 55.944494;
        double toLongitude = -3.186000;
        double toLatitude = 55.945000;

        Move move = new Move(orderNo, fromLongitude, fromLatitude, toLongitude, toLatitude);

        assertEquals(orderNo, move.orderNo);
        assertEquals(fromLongitude, move.fromLongitude, TOLERANCE);
        assertEquals(fromLatitude, move.fromLatitude, TOLERANCE);
        assertEquals(toLongitude, move.toLongitude, TOLERANCE);
        assertEquals(toLatitude, move.toLatitude, TOLERANCE);
    }



    @Test
    public void testAngleCalculationSouthWest() {
        Move move = new Move("ORDER003", -3.186874, 55.944494, -3.187174, 55.944194);

        assertEquals(225.0, move.angle, TOLERANCE); // Moving southwest
    }



    @Test
    public void testAngleNormalization() {
        Move move = new Move("ORDER005", -3.186874, 55.944494, -3.186000, 55.945000);

        double rawAngle = Math.toDegrees(Math.atan2(move.toLongitude - move.fromLongitude, move.toLatitude - move.fromLatitude));
        double normalizedAngle = ((360 - rawAngle) + 90) % 360;

        assertEquals(normalizedAngle, move.angle, TOLERANCE);
    }

    @Test
    public void testSetAngle() {
        Move move = new Move("ORDER006", -3.186874, 55.944494, -3.186000, 55.945000);

        move.setAngle(180.0);

        assertEquals(180.0, move.angle, TOLERANCE);
    }
}
