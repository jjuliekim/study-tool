package cs3500.pa01;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Represents a study session.
 */
public class StudySession {
  private final Path srFile;
  private final int numOfQuestions;
  private final ArrayList<Question> questionBank;
  private int easyToHard;
  private int hardToEasy;

  /**
   * Constructor for StudySession
   *
   * @param srFile path to .sr file to be studied
   * @param num    number of questions to study
   */
  public StudySession(Path srFile, String num) {
    this.srFile = srFile;
    this.numOfQuestions = Integer.parseInt(num);
    questionBank = new ArrayList<>();
    easyToHard = 0;
    hardToEasy = 0;
  }

  /**
   * gets the .sr file
   *
   * @return Path to .sr file
   */
  public Path getSrFile() {
    return this.srFile;
  }

  /**
   * gets the number of questions
   *
   * @return number of questions
   */
  public int getNumOfQuestions() {
    return this.numOfQuestions;
  }

  /**
   * gets the question bank
   *
   * @return question bank
   */
  public ArrayList<Question> getQuestionBank() {
    return this.questionBank;
  }

  /**
   * gets the number of questions that changed from easy to hard
   *
   * @return easy -> hard questions
   */
  public int getEasyToHard() {
    return this.easyToHard;
  }

  /**
   * gets the number of questions that changed from hard to easy
   *
   * @return hard -> easy questions
   */
  public int getHardToEasy() {
    return hardToEasy;
  }

  /**
   * Reads the .sr file and adds each question to a list of questions
   *
   * @throws IOException throws IOException if it can not scan file
   */
  public void readFile(Path srFile) throws IOException {
    Scanner scanner = new Scanner(srFile);
    while (scanner.hasNextLine()) {
      String questionInput = scanner.nextLine();
      String question = questionInput.split(":::")[0];
      String answer = questionInput.split(":::")[1].split("--")[0];
      String difficulty = questionInput.split("--")[1];
      questionBank.add(new Question(question, answer, difficulty));
    }
  }

  /**
   * displays the questions to the user
   *
   * @throws IOException throws IOException if it can not update .sr file at the end
   */
  public void displayQuestions(Readable readable, Appendable appendable, ArrayList<Question> list)
      throws IOException {
    Scanner scanner = new Scanner(readable);
    for (int i = 0; i < list.size(); i++) {
      String input = scanner.nextLine();
      appendable.append(list.get(i).getQuestion()).append("\n");
      switch (input) {
        case "1" -> {
          if (list.get(i).getDifficulty().equals("1")) {
            hardToEasy++;
          }
          list.get(i).setDifficulty("0");
        }
        case "2" -> {
          if (list.get(i).getDifficulty().equals("0")) {
            easyToHard++;
          }
          list.get(i).setDifficulty("1");
        }
        case "3" -> {
          if (list.get(i).getDifficulty().equals("0")) {
            easyToHard++;
          }
          list.get(i).setDifficulty("1");
          appendable.append("Answer: ").append(list.get(i).getAnswer());
        }
        default -> {
          updateFile();
          endStats(new PrintStream(System.out), i);
          return;
        }
      }
    }
    updateFile();
    appendable.append("\n==== Study Session Complete! ====");
    endStats(new PrintStream(System.out), Math.min(numOfQuestions, questionBank.size()));
  }

  /**
   * Sorts the list of questions from hard -> easy.
   * Randomizes the order of hard questions.
   * Randomizes the order of easy questions.
   * Returns only the number of questions user chose to study.
   *
   * @return Array list of questions to be studied
   */
  public ArrayList<Question> sortAndRandomize() {
    ArrayList<Question> hard = new ArrayList<>();
    ArrayList<Question> easy = new ArrayList<>();
    for (Question q : questionBank) {
      if (q.getDifficulty().equals("0")) {
        easy.add(q);
      } else {
        hard.add(q);
      }
    }
    ArrayList<Question> questions = new ArrayList<>();
    Random random = new Random();
    while (!hard.isEmpty()) {
      int randomIndex = random.nextInt(hard.size());
      questions.add(hard.get(randomIndex));
      hard.remove(randomIndex);
    }
    while (!easy.isEmpty()) {
      int randomIndex = random.nextInt(easy.size());
      questions.add(easy.get(randomIndex));
      easy.remove(randomIndex);
    }
    if (numOfQuestions < questions.size()) {
      ArrayList<Question> toStudy = new ArrayList<>();
      for (int i = 0; i < numOfQuestions; i++) {
        toStudy.add(questions.get(i));
      }
      return toStudy;
    } else {
      return questions;
    }
  }

  /**
   * Rewrites the .sr file with the updated question difficulties
   *
   * @throws IOException throws IOException if it can not write file
   */
  public void updateFile() throws IOException {
    StringBuilder updates = new StringBuilder();
    for (Question question : questionBank) {
      updates.append(question.getQuestion()).append(":::");
      updates.append(question.getAnswer()).append("--");
      updates.append(question.getDifficulty()).append("\n");
    }
    Files.write(srFile, updates.toString().trim().getBytes());
  }

  /**
   * Displays the end stats of the study session
   *
   * @param numAnswered number of questions answered
   */
  public void endStats(Appendable appendable, int numAnswered) throws IOException {
    int hard = 0;
    int easy = 0;
    for (Question question : questionBank) {
      if (question.getDifficulty().equals("0")) {
        easy++;
      } else {
        hard++;
      }
    }
    appendable.append("Number of questions answered: ")
        .append(String.valueOf(numAnswered)).append("\n");
    appendable.append("Number of questions that went from easy to hard: ")
        .append(String.valueOf(easyToHard)).append("\n");
    appendable.append("Number of questions that went from hard to easy: ")
        .append(String.valueOf(hardToEasy)).append("\n");
    appendable.append("Number of hard questions: ").append(String.valueOf(hard)).append("\n");
    appendable.append("Number of easy questions: ").append(String.valueOf(easy)).append("\n");
  }
}