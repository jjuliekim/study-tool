package cs3500.pa01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the methods in the StudySession class.
 */
class StudySessionTest {

  StudySession session;
  Question q1;
  Question q2;
  Question q3;

  /**
   * sets up questions in the .sr file before each test
   */
  @BeforeEach
  void beforeEachTest() {
    session = new StudySession(Path.of("src/test/resources/outputTest.sr"), "3");
    String question = """
        How many states make up the US?:::50--1
        Which ocean is off the west coast of the US?:::Pacific Ocean.--1
        What day in November is Veterans Day?:::the 11th--0""";
    try {
      Files.write(session.getSrFile(), question.getBytes());
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    q1 = new Question("How many states make up the US?", "50", "1");
    q2 = new Question("Which ocean is off the west coast of the US?",
        "Pacific Ocean.", "1");
    q3 = new Question("What day in November is Veterans Day?", "the 11th", "0");
    session.getQuestionBank().add(q1);
    session.getQuestionBank().add(q2);
    session.getQuestionBank().add(q3);
  }

  /**
   * tests the readFile method in the StudySession class
   */
  @Test
  void testReadFile() {
    try {
      session.readFile(session.getSrFile());
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals(session.getQuestionBank().get(0).getQuestion(),
        "How many states make up the US?");
    assertEquals(session.getQuestionBank().get(1).getQuestion(),
        "Which ocean is off the west coast of the US?");
    assertEquals(session.getQuestionBank().get(2).getQuestion(),
        "What day in November is Veterans Day?");
  }

  /**
   * tests the displayQuestion method in StudySession
   */
  @Test
  void testDisplayQuestions() {
    Readable input = new StringReader("1\n2\n3");
    Appendable output = new StringBuilder();

    session.getQuestionBank().get(1).setDifficulty("0");
    try {
      session.displayQuestions(input, output, session.getQuestionBank());
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals("0", session.getQuestionBank().get(0).getDifficulty());
    assertEquals(1, session.getHardToEasy());
    assertEquals("1", session.getQuestionBank().get(1).getDifficulty());
    assertEquals("1", session.getQuestionBank().get(2).getDifficulty());
    assertEquals(2, session.getEasyToHard());
    assertEquals(session.getQuestionBank().get(0).getQuestion()
                 + "\n"
                 + session.getQuestionBank().get(1).getQuestion()
                 + "\n"
                 + session.getQuestionBank().get(2).getQuestion()
                 + "\nAnswer: "
                 + session.getQuestionBank().get(2).getAnswer()
                 + "\n==== Study Session Complete! ====", output.toString());
  }

  /**
   * Tests the displayQuestion method in StudySession class
   */
  @Test
  void testDisplayQuestionsElse() {
    Readable input = new StringReader("1\n2\n3");
    Appendable output = new StringBuilder();

    session.getQuestionBank().get(0).setDifficulty("0");
    session.getQuestionBank().get(2).setDifficulty("1");
    try {
      session.displayQuestions(input, output, session.getQuestionBank());
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals("0", session.getQuestionBank().get(0).getDifficulty());
    assertEquals(0, session.getHardToEasy());
    assertEquals("1", session.getQuestionBank().get(1).getDifficulty());
    assertEquals(0, session.getEasyToHard());
    assertEquals("1", session.getQuestionBank().get(2).getDifficulty());
    assertEquals(0, session.getEasyToHard());
    assertEquals(session.getQuestionBank().get(0).getQuestion()
                 + "\n"
                 + session.getQuestionBank().get(1).getQuestion()
                 + "\n"
                 + session.getQuestionBank().get(2).getQuestion()
                 + "\nAnswer: "
                 + session.getQuestionBank().get(2).getAnswer()
                 + "\n==== Study Session Complete! ====", output.toString());
  }

  /**
   * Tests the displayQuestion method in StudySession class when input is 4 (exit)
   */
  @Test
  void testDisplayQuestionExit() {
    Readable input = new StringReader("4");
    Appendable output = new StringBuilder();
    try {
      session.displayQuestions(input, output, session.getQuestionBank());
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertEquals("How many states make up the US?\n", output.toString());
  }

  /**
   * Tests the sortAndRandomize method in StudySession.
   */
  @Test
  void testSortAndRandomize() {
    assertEquals(3, session.getNumOfQuestions());
    assertEquals(3, session.sortAndRandomize().size());
    assertEquals(q3, session.sortAndRandomize().get(2));
    assertEquals("0", session.getQuestionBank().get(2).getDifficulty());
    assertEquals("1", session.getQuestionBank().get(0).getDifficulty());
  }

  /**
   * Tests the sortAndRandomize method.
   * When the number of questions to study is less than the
   * number of questions in the question bank.
   */
  @Test
  void testSortRandomizeLessNumQuestions() {
    session = new StudySession(Path.of("src/test/resources/outputTest.sr"), "1");
    q1 = new Question("How many states make up the US?", "50", "1");
    q2 = new Question("Which ocean is off the west coast of the US?",
        "Pacific Ocean.", "1");
    q3 = new Question("What day in November is Veterans Day?", "the 11th", "0");
    session.getQuestionBank().add(q1);
    session.getQuestionBank().add(q2);
    session.getQuestionBank().add(q3);
    assertEquals(1, session.getNumOfQuestions());
    assertEquals(1, session.sortAndRandomize().size());
  }

  /**
   * Tests the updateFile method in the StudySession class.
   */
  @Test
  void testUpdateFile() {
    try {
      session.updateFile();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    assertTrue(Files.exists(session.getSrFile()));
    try {
      assertEquals(Files.readAllLines(session.getSrFile()),
          Files.readAllLines(Path.of("src/test/resources/outputTest.sr")));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * tests the endStats method in the StudySession class.
   */
  @Test
  void testEndStats() {
    Appendable output = new StringBuilder();
    try {
      session.endStats(output, 3);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertEquals("Number of questions answered: "
                 + session.getNumOfQuestions()
                 + "\nNumber of questions that went from easy to hard: "
                 + session.getEasyToHard()
                 + "\nNumber of questions that went from hard to easy: "
                 + session.getHardToEasy()
                 + "\nNumber of hard questions: 2\n"
                 + "Number of easy questions: 1\n", output.toString());
  }
}