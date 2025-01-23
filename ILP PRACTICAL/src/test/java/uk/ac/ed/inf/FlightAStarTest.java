package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.FlightAStar;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class FlightAStarTest {

    private static final double TOLERANCE = 0.0001;

    private boolean areLngLatEqual(LngLat actual, LngLat expected, double tolerance) {
        return Math.abs(actual.lng() - expected.lng()) <= tolerance &&
                Math.abs(actual.lat() - expected.lat()) <= tolerance;
    }

    @Test
    public void testSimplePath() {
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);
        Set<NamedRegion> noFlyZones = new HashSet<>();
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{start, goal});

        List<LngLat> path = FlightAStar.astar(start, goal, noFlyZones, centralArea, 100);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(areLngLatEqual(path.get(0), start, TOLERANCE));
        assertTrue(areLngLatEqual(path.get(path.size() - 1), goal, TOLERANCE));
    }

    @Test
    public void testPathWithObstacle() {
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);
        NamedRegion noFlyZone = new NamedRegion("NoFly", new LngLat[]{
                new LngLat(-3.186800, 55.944600),
                new LngLat(-3.186700, 55.944700),
                new LngLat(-3.186900, 55.944800),
                new LngLat(-3.187000, 55.944600)
        });
        Set<NamedRegion> noFlyZones = new HashSet<>();
        noFlyZones.add(noFlyZone);
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{start, goal});

        List<LngLat> path = FlightAStar.astar(start, goal, noFlyZones, centralArea, 100);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(areLngLatEqual(path.get(0), start, TOLERANCE));
        assertTrue(areLngLatEqual(path.get(path.size() - 1), goal, TOLERANCE));

        for (LngLat point : path) {
            assertFalse(new LngLatHandler().isInRegion(point, noFlyZone));
        }
    }


    @Test
    public void testLimitedDepth() {
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);
        Set<NamedRegion> noFlyZones = new HashSet<>();
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{start, goal});

        List<LngLat> path = FlightAStar.astar(start, goal, noFlyZones, centralArea, 1);

        assertNull(path);
    }

    @Test
    public void testExactPathMatching() {
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);
        Set<NamedRegion> noFlyZones = new HashSet<>();
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{start, goal});

        List<LngLat> path = FlightAStar.astar(start, goal, noFlyZones, centralArea, 100);

        assertNotNull(path);
        assertTrue(path.size() >= 2); // Should at least contain start and goal
        assertTrue(areLngLatEqual(path.get(0), start, TOLERANCE));
        assertTrue(areLngLatEqual(path.get(path.size() - 1), goal, TOLERANCE));
    }

    @Test
    public void testAvoidCentralAreaBoundary() {
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat goal = new LngLat(-3.186000, 55.945000);
        NamedRegion centralArea = new NamedRegion("central", new LngLat[]{
                new LngLat(-3.187500, 55.944000),
                new LngLat(-3.187500, 55.945500),
                new LngLat(-3.185500, 55.945500),
                new LngLat(-3.185500, 55.944000)
        });
        Set<NamedRegion> noFlyZones = new HashSet<>();

        List<LngLat> path = FlightAStar.astar(start, goal, noFlyZones, centralArea, 100);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(areLngLatEqual(path.get(0), start, TOLERANCE));
        assertTrue(areLngLatEqual(path.get(path.size() - 1), goal, TOLERANCE));

        LngLatHandler handler = new LngLatHandler();
        for (LngLat point : path) {
            assertTrue(handler.isInRegion(point, centralArea));
        }
    }
}


