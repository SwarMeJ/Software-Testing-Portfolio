package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import static org.junit.Assert.*;

public class LngLatHandlerTest {

    private final LngLatHandler lngLatHandler = new LngLatHandler();
    private static final double TOLERANCE = 0.0001;

    private boolean areLngLatEqual(LngLat actual, LngLat expected, double tolerance) {
        return Math.abs(actual.lng() - expected.lng()) <= tolerance &&
                Math.abs(actual.lat() - expected.lat()) <= tolerance;
    }

    @Test
    public void testDistanceTo() {
        LngLat point1 = new LngLat(-3.186874, 55.944494);
        LngLat point2 = new LngLat(-3.187000, 55.945000);

        double distance = lngLatHandler.distanceTo(point1, point2);

        double expectedDistance = Math.sqrt(Math.pow(-3.187000 - (-3.186874), 2) +
                Math.pow(55.945000 - 55.944494, 2));
        assertEquals(expectedDistance, distance, TOLERANCE);
    }

    @Test
    public void testIsCloseToTrue() {
        LngLat point1 = new LngLat(-3.186874, 55.944494);
        LngLat point2 = new LngLat(-3.186880, 55.944500);

        boolean isClose = lngLatHandler.isCloseTo(point1, point2);

        assertTrue(isClose);
    }

    @Test
    public void testIsCloseToFalse() {
        LngLat point1 = new LngLat(-3.186874, 55.944494);
        LngLat point2 = new LngLat(-3.200000, 55.950000);

        boolean isClose = lngLatHandler.isCloseTo(point1, point2);

        assertFalse(isClose);
    }

    @Test
    public void testIsInRegionTrue() {
        LngLat point = new LngLat(-3.186874, 55.944494);
        NamedRegion region = new NamedRegion("TestRegion", new LngLat[]{
                new LngLat(-3.187500, 55.944000),
                new LngLat(-3.187500, 55.945000),
                new LngLat(-3.186000, 55.945000),
                new LngLat(-3.186000, 55.944000)
        });

        boolean isInRegion = lngLatHandler.isInRegion(point, region);

        assertTrue(isInRegion);
    }

    @Test
    public void testIsInRegionFalse() {
        LngLat point = new LngLat(-3.190000, 55.950000); // Outside the region
        NamedRegion region = new NamedRegion("TestRegion", new LngLat[]{
                new LngLat(-3.187500, 55.944000),
                new LngLat(-3.187500, 55.945000),
                new LngLat(-3.186000, 55.945000),
                new LngLat(-3.186000, 55.944000)
        });

        boolean isInRegion = lngLatHandler.isInRegion(point, region);

        assertFalse(isInRegion);
    }




}
