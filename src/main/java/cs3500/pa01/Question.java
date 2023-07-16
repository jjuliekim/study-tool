package cs3500.pa01;

/**
 * Represents a question/answer
 */
public class Question {
  private final String question;
  private final String answer;
  private String difficulty;

  /**
   * Constructor for Question
   *
   * @param q    question
   * @param a    answer
   * @param diff question difficulty
   */
  public Question(String q, String a, String diff) {
    this.question = q;
    this.answer = a;
    this.difficulty = diff;
  }

  /**
   * gets the question
   *
   * @return question
   */
  public String getQuestion() {
    return this.question;
  }

  /**
   * gets the answer
   *
   * @return answer
   */
  public String getAnswer() {
    return this.answer;
  }

  /**
   * gets the difficulty
   *
   * @return 0 if easy, 1 if hard
   */
  public String getDifficulty() {
    return this.difficulty;
  }

  public void setDifficulty(String diff) {
    this.difficulty = diff;
  }
}
