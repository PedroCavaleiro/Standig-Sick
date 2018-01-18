package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Criado por pedrocavaleiro em 21/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class QuestionAdapter extends ArrayAdapter<Question> {

    private final Context context;
    private final ArrayList<Question> elements;

    public QuestionAdapter(Context context, ArrayList<Question> elements) {
        super(context, R.layout.questions_lv_line, elements);
        this.context = context;
        this.elements = elements;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.questions_lv_line, parent, false);

        TextView tvQuestion = rowView.findViewById(R.id.questName);
        TextView tvAnswerCount = rowView.findViewById(R.id.ansCount);

        tvQuestion.setText(elements.get(position).get_question());
        tvAnswerCount.setText(elements.get(position).get_assignedAnswersCount().toString());

        return rowView;

    }

}
