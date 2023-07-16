package cs3500.pa01;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the methods in the Driver class.
 */
public class DriverTest {
  private Path root;
  private Path output;
  private Path arrays;
  private Path vectors;
  private Path northAmerica;
  private MdVisitor visitor;
  private ArrayList<String> expected;
  private ArrayList<String> actual;
  private ArrayList<Path> expectedPath;
  private ArrayList<Path> actualPath;

  /**
   * Initialize data and set up before each test
   */
  @BeforeEach
  void setUpTest() {
    root = Path.of("src", "test", "resources", "PA01Examples");
    output = Path.of("src", "test", "resources", "output.md");
    expected = new ArrayList<>();
    actual = new ArrayList<>();
    try {
      Files.deleteIfExists(output);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }

    arrays = Path.of("src", "test", "resources", "PA01Examples",
        "SomeFolder", "arrays.md");
    vectors = Path.of("src", "test", "resources", "PA01Examples", "vectors.md");
    northAmerica = Path.of("src", "test", "resources", "PA01Examples",
        "SomeFolder", "northAmerica.md");
    visitor = new MdVisitor(new ArrayList<>());
    expectedPath = new ArrayList<>();
    actualPath = new ArrayList<>();
  }

  /**
   * Tests the main method in the Driver class.
   * command line argument 2 = "filename"
   */
  @Test
  void testMainFileName() {
    String[] args = new String[] {root.toString(), "filename", output.toString()};
    assertEquals(3, args.length);
    try {
      Driver.main(args);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    try {
      expected = new ArrayList<>(Files.readAllLines(Path.of("src", "test", "resources",
          "outputFilename.md")));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    try {
      actual = new ArrayList<>(Files.readAllLines(Path.of(args[2])));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals(expected, actual);
  }

  /**
   * Tests the main method in the Driver class.
   * command line argument 2 = "created"
   */
  @Test
  void testMainCreated() {
    String[] args = new String[] {root.toString(), "created", output.toString()};
    try {
      Driver.main(args);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }

    try {
      expected = new ArrayList<>(Files.readAllLines(Path.of("src", "test", "resources",
          "outputCreated.md")));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    try {
      actual = new ArrayList<>(Files.readAllLines(Path.of(args[2])));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals(expected, actual);
  }

  /**
   * Tests the main method in the Driver class.
   * command line argument 2 = "modified"
   */
  @Test
  void testMainModified() {
    String[] args = new String[] {root.toString(), "modified", output.toString()};
    try {
      Driver.main(args);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }

    try {
      expected = new ArrayList<>(Files.readAllLines(Path.of("src", "test", "resources",
          "outputModified.md")));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    try {
      actual = new ArrayList<>(Files.readAllLines(Path.of(args[2])));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals(expected, actual);
  }

  /**
   * Tests the orderingFiles method in the Driver class.
   * Sorting by filename
   */
  @Test
  void testOrderingFilesName() {
    try {
      Files.walkFileTree(root, visitor);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    actualPath = visitor.getList();
    Driver.orderingFiles(Order.FILENAME, actualPath);
    expectedPath = new ArrayList<>();
    expectedPath.add(arrays);
    expectedPath.add(northAmerica);
    expectedPath.add(vectors);
    assertEquals(expectedPath, actualPath);
  }

  /**
   * Tests the orderingFiles method in the Driver class.
   * Sorting by created time
   */
  @Test
  void testOrderingFilesCreated() {
    try {
      Files.walkFileTree(root, visitor);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    actualPath = visitor.getList();
    Driver.orderingFiles(Order.CREATED, actualPath);
    expectedPath.add(vectors);
    expectedPath.add(northAmerica);
    expectedPath.add(arrays);
    assertEquals(expectedPath, actualPath);
  }

  /**
   * Tests the orderingFiles method in the Driver class.
   * Sorting by last modified time
   */
  @Test
  void testOrderingFilesModified() {
    try {
      Files.walkFileTree(root, visitor);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    actualPath = visitor.getList();
    Driver.orderingFiles(Order.MODIFIED, actualPath);
    expectedPath.add(vectors);
    expectedPath.add(northAmerica);
    expectedPath.add(arrays);
    assertEquals(expectedPath, actualPath);
  }

  /**
   * Tests the formatNotes method in the Driver class.
   */
  @Test
  void testFormatNotes() {
    actualPath.add(Path.of("src", "test", "resources", "PA01Examples",
        "SomeFolder", "arrays.md"));

    try {
      expected = new ArrayList<>(Files.readAllLines(
          Path.of("src", "test", "resources", "formattedArrays.md")));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    try {
      actual = new ArrayList<>(Files.readAllLines(
          Files.write(output, Driver.formatNotes(actualPath, output).getBytes())));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals(expected, actual);
  }

  /**
   * Tests the makeSrFile method in the Driver class.
   */
  @Test
  void testMakeSrFile() {
    String[] args = new String[] {root.toString(), "filename", output.toString()};
    try {
      Driver.main(args);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    Path output = Path.of("src", "test", "resources", "output.sr");
    assertTrue(Files.exists(output));
    try {
      assertEquals(Files.readAllLines(Path.of("src", "test", "resources",
              "updatedOutputTest.sr")),
          Files.readAllLines(output));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Tests the main method in the Driver class when there's 0 arguments.
   */
  @Test
  void testStudySession() {
    Readable input = new StringReader("src/test/resources/output.sr\n3");
    Appendable output = new StringBuilder();
    try {
      Driver.startStudying(input, output);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals("""
                
        ==== Starting Study Session! ====

        Which file would you like to study from?
        [Please input a full path to a .sr file]

        How many questions would you like to study?
        """, output.toString());
  }
}