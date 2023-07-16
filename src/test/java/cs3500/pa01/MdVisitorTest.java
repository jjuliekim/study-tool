package cs3500.pa01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the methods in MdVisitor class
 */
public class MdVisitorTest {

  private Path notesRoot;
  private MdVisitor visitor;
  ArrayList<Path> expected;

  /**
   * Initialize data and set up before each test
   */
  @BeforeEach
  void setUpTest() {
    notesRoot = Path.of("src", "test", "resources", "PA01Examples");
    visitor = new MdVisitor(new ArrayList<>());
    expected = new ArrayList<>();
  }

  /**
   * Tests the preVisitDirectory method in the MdVisitor class.
   */
  @Test
  void testPreVisitDirectory() {
    BasicFileAttributes attrs = null;
    try {
      attrs = Files.readAttributes(notesRoot, BasicFileAttributes.class);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals(FileVisitResult.CONTINUE, visitor.preVisitDirectory(notesRoot, attrs));
  }

  /**
   * Tests the visitFile method in the MdVisitor class
   */
  @Test
  void testVisitFile() {
    try {
      Files.walkFileTree(notesRoot, visitor);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    Path arrays = Path.of("src", "test", "resources", "PA01Examples",
        "SomeFolder", "arrays.md");
    Path vectors = Path.of("src", "test", "resources", "PA01Examples", "vectors.md");
    assertTrue(visitor.getList().contains(arrays));
    assertTrue(visitor.getList().contains(vectors));

    MdVisitor visitor2 = new MdVisitor(new ArrayList<>());
    BasicFileAttributes attrs = null;
    try {
      attrs = Files.readAttributes(arrays, BasicFileAttributes.class);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    visitor2.visitFile(arrays, attrs);
    assertEquals(1, visitor2.getList().size());
    BasicFileAttributes attrs2 = null;
    Path sampleInput = Path.of("src", "test", "resources", "sampleInput.txt");
    try {
      attrs2 = Files.readAttributes(
          sampleInput,
          BasicFileAttributes.class);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    visitor2.visitFile(sampleInput, attrs2);
    assertEquals(1, visitor2.getList().size());
  }

  /**
   * Tests the visitFileFailed method in the MdVisitor class.
   */
  @Test()
  void testVisitFileFailed() {
    assertThrows(IOException.class,
        () -> visitor.visitFileFailed(Path.of("src", "test", "resources",
                "PA01Examples", "unknown.md"),
            new IOException()));
  }

  /**
   * Tests the postVisitDirectory method in the MdVisitor class.
   */
  @Test
  void testPostVisitDirectory() {
    assertEquals(FileVisitResult.CONTINUE,
        visitor.postVisitDirectory(notesRoot, new IOException()));

  }

  /**
   * Tests the getList method in the MdVisitor class.
   */
  @Test
  void testGetList() {
    ArrayList<Path> files = visitor.getList();
    assertEquals(expected, files);

    Path vectors = Path.of("src", "test", "resources", "PA01Examples", "vectors.md");
    expected.add(vectors);
    try {
      Files.walkFileTree(vectors, visitor);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals(expected, files);
  }
}