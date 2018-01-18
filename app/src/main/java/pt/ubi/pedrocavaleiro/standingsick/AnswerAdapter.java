package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class AnswerAdapter extends ArrayAdapter {

    // Variaveis que são necessárias pela classe toda e também em métodos anonimos
    private final Context context;
    private final ArrayList<Answer> elements;

    public AnswerAdapter(Context context, ArrayList<Answer> elements) {
        super(context, R.layout.answers_lv_line, elements);
        this.context = context;
        this.elements = elements;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.answers_lv_line, parent, false);

        TextView ansName = rowView.findViewById(R.id.ansName);
        ImageButton delButton = rowView.findViewById(R.id.delAnsButton);
        ImageButton editButton = rowView.findViewById(R.id.ansEditButton);

        ansName.setText(elements.get(position).getAnswer());

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editItem(position);
            }
        });
        return rowView;

    }

    /**
     * Remover uma resposta da lista / Handler do botão de remover
     * @param position posição do item
     */
    private void removeItem(final int position) {
        // Perguntamos ao utilizador se ele quer realmente remover a resposta
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DatabaseInterface db = new DatabaseInterface(context);
                        db.deleteAnswer(elements.get(position).getAnswerID());
                        // Atualiza a informação na atividade que contem a listview para atualizar
                        // os elementos
                        ManageAnswers.answers.remove(position);
                        ManageAnswers.adapter.notifyDataSetChanged();
                        db.closeDB();
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

    /**
     * Editar uma resposta da lista / Handler do botão de editar
     * @param position posição do item
     */
    private void editItem(final int position) {
        // Criamos um "alerta" que vai conter uma caixa de texto para introduzir o nome da questão
        // não sendo necessário criar uma nova atividade
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.answer_name_field);

        final EditText input = new EditText(context);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(elements.get(position).getAnswer());
        builder.setView(input);

        builder.setPositiveButton(R.string.questionSaveQuestion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = input.getText().toString();
                // Verificamos se o texto introduzido está vaizo ou não
                if (TextUtils.isEmpty(answer)) {
                    Toast t = Toast.makeText(context, R.string.invalidAnswerName, Toast.LENGTH_LONG);
                    t.show();
                } else {
                    // Verificamos se a resposta já existe na base de dados
                    DatabaseInterface db = new DatabaseInterface(context);
                    if (db.answerExsists(answer)) {
                        Toast t = Toast.makeText(context,
                                R.string.answer_already_exists, Toast.LENGTH_LONG);
                        t.show();
                    } else {
                        db.editAnswer(elements.get(position).getAnswerID(), answer);
                        ManageAnswers.answers = db.getAnswers();
                        ManageAnswers.adapter = new AnswerAdapter(context, ManageAnswers.answers);
                        ManageAnswers.answerList.invalidate();
                        ManageAnswers.answerList.setAdapter(ManageAnswers.adapter);
                    }
                    db.closeDB();
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
