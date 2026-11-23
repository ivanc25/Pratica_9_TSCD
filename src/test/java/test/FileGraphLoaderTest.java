package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.pathfinder.FileGraphLoader;
import software.ulpgc.pathfinder.GraphContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileGraphLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoadValidGraphAndIgnoreMalformedLines() throws IOException {
        Path csvFile = tempDir.resolve("graph.csv");
        Files.write(csvFile, List.of(
                "A,B,1.0",
                "B,C,2.0",
                "A,D,4.0",
                "C,D,1.0",
                "INVALID_LINE_WITHOUT_COMMAS",
                "A,B,not_a_number"
        ));

        FileGraphLoader loader = new FileGraphLoader(csvFile.toFile());
        GraphContainer container = loader.load();

        assertNotNull(container);
        assertEquals(List.of("A", "B", "C"), container.shortestPathBetween("A", "C"));
        assertEquals(3.0, container.pathWeightBetween("A", "C"), 0.0001);
    }

    @Test
    void testLoadNonExistentFileThrowsIOException() {
        File missingFile = tempDir.resolve("does_not_exist.csv").toFile();
        FileGraphLoader loader = new FileGraphLoader(missingFile);

        assertThrows(IOException.class, loader::load);
    }
}