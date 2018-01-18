package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Context;

/**
 * Criado por pedrocavaleiro em 17/11/17.
 */

public class Question {

    private String _question;
    private Integer _id;
    private Integer _assignedAnswersCount;

    public Question(int id, String question) {
        _id = id;
        _question = question;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String get_question() {
        return _question;
    }

    public Integer get_assignedAnswersCount() {
        return _assignedAnswersCount;
    }

    public void calculateAssignedAnswers(Context context) {
        DatabaseInterface db = new DatabaseInterface(context);
        _assignedAnswersCount = db.getOptions(_id).size();
    }

    public void set_question(String _question) {
        this._question = _question;
    }
}
