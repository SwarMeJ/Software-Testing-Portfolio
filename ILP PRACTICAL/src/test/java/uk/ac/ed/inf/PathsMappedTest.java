package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.PathsMapped;
import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PathsMappedTest {

    private static final double TOLERANCE = 0.0001;

    private boolean areLngLatEqual(LngLat actual, LngLat expected, double tolerance) {
        return Math.abs(actual.lng() - expected.lng()) <= tolerance &&
                Math.abs(actual.lat() - expected.lat()) <= tolerance;
    }

    @Test
    public void testAddPathAndRetrieve() {
        PathsMapped pathsMapped = new PathsMapped();
        LngLat endpoint = new LngLat(-3.186874, 55.944494);
        List<LngLat> path = new ArrayList<>();
        path.add(new LngLat(-3.186874, 55.944494));
        path.add(new LngLat(-3.187000, 55.944600));

        pathsMapped.addPath(path, endpoint);
        List<LngLat> retrievedPath = pathsMapped.getPath(endpoint);

        assertNotNull(retrievedPath);
        assertEquals(path.size(), retrievedPath.size());
        for (int i = 0; i < path.size(); i++) {
            assertTrue(areLngLatEqual(path.get(i), retrievedPath.get(i), TOLERANCE));
        }
    }

    @Test
    public void testIsPathedTrue() {
        PathsMapped pathsMapped = new PathsMapped();
        LngLat endpoint = new LngLat(-3.186874, 55.944494);
        List<LngLat> path = new ArrayList<>();
        path.add(new LngLat(-3.186874, 55.944494));
        path.add(new LngLat(-3.187000, 55.944600));

        pathsMapped.addPath(path, endpoint);

        assertTrue(pathsMapped.isPathed(endpoint));
    }

    @Test
    public void testIsPathedFalse() {
        PathsMapped pathsMapped = new PathsMapped();
        LngLat endpoint = new LngLat(-3.186874, 55.944494);

        assertFalse(pathsMapped.isPathed(endpoint));
    }

    @Test
    public void testRetrieveNonExistentPath() {
        PathsMapped pathsMapped = new PathsMapped();
        LngLat endpoint = new LngLat(-3.186874, 55.944494);

        List<LngLat> retrievedPath = pathsMapped.getPath(endpoint);

        assertNull(retrievedPath);
    }

    @Test
    public void testMultiplePaths() {
        PathsMapped pathsMapped = new PathsMapped();
        LngLat endpoint1 = new LngLat(-3.186874, 55.944494);
        LngLat endpoint2 = new LngLat(-3.187000, 55.944600);

        List<LngLat> path1 = new ArrayList<>();
        path1.add(new LngLat(-3.186874, 55.944494));
        path1.add(new LngLat(-3.187000, 55.944600));

        List<LngLat> path2 = new ArrayList<>();
        path2.add(new LngLat(-3.187000, 55.944600));
        path2.add(new LngLat(-3.187500, 55.945000));

        pathsMapped.addPath(path1, endpoint1);
        pathsMapped.addPath(path2, endpoint2);

        List<LngLat> retrievedPath1 = pathsMapped.getPath(endpoint1);
        List<LngLat> retrievedPath2 = pathsMapped.getPath(endpoint2);

        assertNotNull(retrievedPath1);
        assertNotNull(retrievedPath2);
        assertEquals(path1.size(), retrievedPath1.size());
        assertEquals(path2.size(), retrievedPath2.size());

        for (int i = 0; i < path1.size(); i++) {
            assertTrue(areLngLatEqual(path1.get(i), retrievedPath1.get(i), TOLERANCE));
        }
        for (int i = 0; i < path2.size(); i++) {
            assertTrue(areLngLatEqual(path2.get(i), retrievedPath2.get(i), TOLERANCE));
        }
    }
}

