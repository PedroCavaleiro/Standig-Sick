package pt.ubi.pedrocavaleiro.standingsick;

/**
 * Criado por pedrocavaleiro em 21/11/17.
 */

public class Answer {

    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getAnswerID() {
        return answerID;
    }

    public void setAnswerID(int answerID) {
        this.answerID = answerID;
    }

    private int answerID;

    public Answer(int id, String answer) {
        this.answerID = id;
        this.answer = answer;
    }

}
