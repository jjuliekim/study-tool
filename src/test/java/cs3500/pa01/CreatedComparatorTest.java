package cs3500.pa01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.Test;

/**
 * Tests for the method in CreatedComparator class.
 */
public class CreatedComparatorTest {
  private final Path arrays = Path.of("src", "test", "resources", "PA01Examples",
      "SomeFolder", "arrays.md");
  private final Path vectors = Path.of("src", "test", "resources", "PA01Examples",
      "vectors.md");
  private final Path formattedArrays = Path.of("src", "test", "resources",
      "formattedArrays.md");

  /**
   * Tests the compare method
   */
  @Test
  public void testCompare() {
    final CreatedComparator comparator = new CreatedComparator();
    BasicFileAttributes attrs1 = null;
    BasicFileAttributes attrs2 = null;
    try {
      attrs1 = Files.readAttributes(vectors, BasicFileAttributes.class);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    try {
      attrs2 = Files.readAttributes(arrays, BasicFileAttributes.class);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assert attrs1 != null;
    assert attrs2 != null;
    int expected = attrs1.creationTime().compareTo(attrs2.creationTime());

    assertEquals(expected, comparator.compare(vectors, arrays));
    assertEquals(expected, comparator.compare(arrays, formattedArrays));
    assertEquals(expected, comparator.compare(vectors, formattedArrays));
    assertThrows(RuntimeException.class, () -> comparator.compare(Path.of(
        "src", "test", "resources", "unknown.md"), vectors));
  }
}