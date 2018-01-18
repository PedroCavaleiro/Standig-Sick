package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class OptionAdapter extends ArrayAdapter<Answer> {

    private final Context context;
    private final ArrayList<Answer> elements;
    private final int questionID;

    public OptionAdapter(Context context, ArrayList<Answer> elements, int questionID) {
        super(context, R.layout.questionmanager_answerlist_line, elements);
        this.context = context;
        this.elements = elements;
        this.questionID = questionID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.questionmanager_answerlist_line, parent, false);

        TextView tvAnswer = rowView.findViewById(R.id.tvOption);
        ImageButton delButton = rowView.findViewById(R.id.imageButton);
        tvAnswer.setText(elements.get(position).getAnswer());

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });
        return rowView;

    }

    private void removeItem(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DatabaseInterface db = new DatabaseInterface(context);
                        db.deleteOption(questionID, elements.get(position).getAnswerID());
                        AddEditQuestions.options.remove(position);
                        AddEditQuestions.adapter.notifyDataSetChanged();
                        Toast t = Toast.makeText(context, R.string.questionSaved, Toast.LENGTH_LONG);
                        t.show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.deleteOptionConfirm).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

}
