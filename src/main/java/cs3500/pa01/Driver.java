package cs3500.pa01;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * This is the main driver of this project.
 */
public class Driver {
  /**
   * Project entry point
   *
   * @param args path to files, ordering flag, and output path but no args required
   * @throws IOException throws IOException if errors reading file
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 3) {
      Path notesRoot = Path.of(args[0]);
      Order order = Order.valueOf(args[1].toUpperCase());
      System.out.println("Walking the file system.");
      MdVisitor visitor = new MdVisitor(new ArrayList<>());
      Files.walkFileTree(notesRoot, visitor);
      ArrayList<Path> listOfMdFiles = visitor.getList();
      orderingFiles(order, listOfMdFiles);
      Path output = Path.of(args[2]);
      String notes = formatNotes(listOfMdFiles, output);
      byte[] data = notes.getBytes();
      Files.write(output, data);
    } else {
      startStudying(new InputStreamReader(System.in), new PrintStream(System.out));
    }
  }

  /**
   * starts the study session (main has no args)
   *
   * @param readable   input
   * @param appendable output
   * @throws IOException throws IOException if it can not read the file
   */
  public static void startStudying(Readable readable, Appendable appendable) throws IOException {
    Scanner scanner = new Scanner(readable);
    appendable.append("\n==== Starting Study Session! ====\n\n");
    appendable.append("Which file would you like to study from?\n");
    appendable.append("[Please input a full path to a .sr file]\n");
    Path file = Path.of(scanner.nextLine());
    appendable.append("\nHow many questions would you like to study?\n");
    String num = scanner.nextLine();
    StudySession session = new StudySession(file, num);
    session.readFile(file);
    session.displayQuestions(new InputStreamReader(System.in), new PrintStream(System.out),
        session.sortAndRandomize());
  }

  /**
   * Sorts the given list of .md files based on the given ordering flag.
   *
   * @param order         ordering flag
   * @param listOfMdFiles list of .md files to sort
   */
  public static void orderingFiles(Order order, ArrayList<Path> listOfMdFiles) {
    switch (order) {
      case FILENAME -> Collections.sort(listOfMdFiles);
      case CREATED -> listOfMdFiles.sort(new CreatedComparator());
      default -> listOfMdFiles.sort(new ModifiedComparator());
    }
  }

  /**
   * Formats the text in the .md files.
   * Only keep headers and separate bracketed text with bullets.
   *
   * @param listOfMdFiles list of sorted .md files to format
   * @return study guide text
   * @throws IOException if scanner can not scan file
   */
  public static String formatNotes(ArrayList<Path> listOfMdFiles, Path output) throws IOException {
    Scanner scanner;
    StringBuilder keepContents = new StringBuilder();
    StringBuilder questions = new StringBuilder();
    boolean isQuestion = false;
    for (Path file : listOfMdFiles) {
      StringBuilder contents = new StringBuilder();
      scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        contents.append(line).append('\n');
      }
      char[] text = contents.toString().trim().toCharArray();
      boolean inHeader = false;
      boolean inBrackets = false;
      StringBuilder allBracketedText = new StringBuilder();

      for (int i = 0; i < text.length; i++) {
        char character = text[i];
        if (inHeader) {
          if (character == '\n') {
            inHeader = false;
          } else {
            keepContents.append(character);
          }
          continue;
        }
        if (inBrackets) {
          if (character == ':' && text[i + 1] == ':' && text[i + 2] == ':') {
            isQuestion = true;
          }
          if (character == ']' && text[i + 1] == ']') {
            inBrackets = false;
            if (isQuestion) {
              questions.append(allBracketedText.append("--1").append('\n').toString()
                  .replaceAll(" *::: *", ":::"));
            } else {
              keepContents.append("\n- ").append(allBracketedText);
            }
          }
          if (character != '\n') {
            allBracketedText.append(character);
          }
        }
        if (character == '#') {
          inHeader = true;
          if (i > 0) {
            keepContents.append("\n\n");
          }
          keepContents.append('#');
          continue;
        }
        if (character == '[' && text[i + 1] == '[') {
          inBrackets = true;
          isQuestion = false;
          i++;
          allBracketedText = new StringBuilder();
        }
      }
      keepContents.append("\n\n");
    }
    makeSrFile(questions.toString().trim(), output);
    return keepContents.toString().trim();
  }

  /**
   * Makes a .sr file with the given questions.
   *
   * @param questions questions/answers to put in the .sr file
   * @param output    output path
   * @throws IOException throws IOException if it can not write file
   */
  public static void makeSrFile(String questions, Path output) throws IOException {
    byte[] data = questions.getBytes();
    String outputName = output.toString();
    Path outputSr = Path.of(outputName.substring(0, outputName.length() - 2) + "sr");
    Files.write(outputSr, data);
  }
}