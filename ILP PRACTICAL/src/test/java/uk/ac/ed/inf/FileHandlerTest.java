package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.FileHandler;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.Move;
import uk.ac.ed.inf.Deliveries;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FileHandlerTest {

    private final FileHandler fileHandler = new FileHandler();

    // Helper method to clean up test files
    private void deleteFile(String fileName) {
        File file = new File("resultfiles/" + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testRecordDelivery() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 1, 6);
        Deliveries delivery = new Deliveries(new Order("ORDER001", testDate, OrderStatus.DELIVERED, OrderValidationCode.NO_ERROR, 2400, null, null));
        List<Deliveries> deliveriesList = new ArrayList<>();
        deliveriesList.add(delivery);
        String expectedFileName = "deliveries-2025-01-06.json";

        // Act
        fileHandler.recordDelivery(deliveriesList, testDate);

        // Assert
        File file = new File("resultfiles/" + expectedFileName);
        assertTrue("Delivery file was not created", file.exists());

        // Cleanup
        deleteFile(expectedFileName);
    }

    @Test
    public void testRecordMove() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 1, 6);
        Move move = new Move("ORDER001", -3.186874, 55.944494, -3.187500, 55.945000);
        List<Move> moves = new ArrayList<>();
        moves.add(move);
        String expectedFileName = "flightpath-2025-01-06.json";

        // Act
        fileHandler.recordMove(moves, testDate);

        // Assert
        File file = new File("resultfiles/" + expectedFileName);
        assertTrue("Flightpath file was not created", file.exists());

        // Cleanup
        deleteFile(expectedFileName);
    }

    @Test
    public void testRecordGeoJson() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 1, 6);
        List<LngLat> flightPath = List.of(
                new LngLat(-3.186874, 55.944494),
                new LngLat(-3.187500, 55.945000),
                new LngLat(-3.186874, 55.944494)
        );
        String expectedFileName = "drone-2025-01-06.geojson";

        // Act
        fileHandler.recordGeoJson(flightPath, testDate);

        // Assert
        File file = new File("resultfiles/" + expectedFileName);
        assertTrue("GeoJSON file was not created", file.exists());

        // Cleanup
        deleteFile(expectedFileName);
    }



    @Test
    public void testInvalidFileCreation() {
        // Arrange
        LocalDate testDate = null; // Invalid date
        List<Move> moves = new ArrayList<>();
        moves.add(new Move("ORDER001", -3.186874, 55.944494, -3.187500, 55.945000));

        // Act
        try {
            fileHandler.recordMove(moves, testDate);
            fail("Expected exception was not thrown for invalid date");
        } catch (Exception e) {
            // Assert
            assertTrue(e instanceof NullPointerException || e.getMessage().contains("Invalid inputs"));
        }
    }

}

