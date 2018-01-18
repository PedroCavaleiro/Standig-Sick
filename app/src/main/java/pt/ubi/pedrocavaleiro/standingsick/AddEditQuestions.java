package pt.ubi.pedrocavaleiro.standingsick;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Criado por pedrocavaleiro em 20/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class AddEditQuestions extends AppCompatActivity {

    // As variaveis abaixo necessitam de estar acessiveis na classe inteira
    Question question;
    EditText questionField;
    ListView answersListView;

    // As variaveis abaixo necessitam de estar acessiveis a partir de outras classes
    public static ArrayAdapter adapter;
    public static ArrayList<Answer> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_questions);

        questionField = findViewById(R.id.questionField);
        answersListView = findViewById(R.id.answersListview);

        Intent questionIntent = getIntent();

        options = new ArrayList<Answer>();

        DatabaseInterface db = new DatabaseInterface(this);

        // Para certificar que existe um id efetuamos este if que vai carregar todas as informações
        // sobre a questão selecionada
        if (questionIntent.hasExtra("questionID")) {
            question = db.getQuestion(questionIntent.getIntExtra("questionID", -1));
            options = db.getOptionsAsAnswer(question.get_id());
            questionField.setText(question.get_question());
            db.close();
        }

        adapter = new OptionAdapter(this, options, question.get_id());
        answersListView.setAdapter(adapter);

    }

    /**
     * Handler do botão que adiciona a questão
     * @param view
     */
    public void addQuestion(View view) {
        Intent answerSelector = new Intent(this, ManageAnswers.class);
        answerSelector.putExtra("calledFrom", "questionManager");
        startActivityForResult(answerSelector, Constants.INTENT_SELECT_ANSWERS);
    }

    /**
     * Handler do botão que elemina a questão
     * @param view
     */
    public void deleteQuestion(View view) {
        // Interface de Dialogo para perguntar ao utilizador se de facto quer eliminar a questão

        // O listner que vai indicar qual dos butões foram pressionados
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DatabaseInterface db = new DatabaseInterface(AddEditQuestions.this);
                        db.deleteQuestion(question.get_id());
                        db.closeDB();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        // Constriumos o alerta e mostramos ao utilizador
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteQuestionQuestion).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    /**
     * Handler do botão que guarda a questão
     * @param view
     */
    public void saveQuestion(View view) {
        // As opções da questão são automaticamente guardadas quando adicionadas ou removidas
        // aqui vamos apenas guardar a mudança de texto
        DatabaseInterface db = new DatabaseInterface(this);
        String newQuestion = questionField.getText().toString();
        if (TextUtils.isEmpty(newQuestion)) {
            Toast t = Toast.makeText(this, getString(R.string.invalidQuestionName), Toast.LENGTH_LONG);
            t.show();
        } else {
                db.editQuestion(question.get_id(), newQuestion);
                Toast t = Toast.makeText(this, getString(R.string.questionSaved), Toast.LENGTH_LONG);
                t.show();
        }
        db.close();
    }

    @Override
    protected void onActivityResult(int reqCode, int rCode, Intent iData) {
        if (reqCode == Constants.INTENT_SELECT_ANSWERS) {
            // Confirmamos que foi selecionada uma resposta e linkamos a resposta à pergunta
            // Verificamos também se a resposta já se encontra linkada ou não
            if (iData.hasExtra("answerID")) {
                int selectedAnswer = iData.getIntExtra("answerID", -1);
                if (selectedAnswer != -1) {
                    DatabaseInterface db = new DatabaseInterface(this);
                    if(!db.isLinked(question.get_id(), selectedAnswer)) {
                        db.linkAnswer(question.get_id(), selectedAnswer);
                        db.closeDB();
                        Toast t = Toast.makeText(this, getString(R.string.questionSaved), Toast.LENGTH_LONG);
                        t.show();
                    } else {
                        Toast t = Toast.makeText(this, getString(R.string.option_aready_exists), Toast.LENGTH_LONG);
                        t.show();
                    }
                }
            }

            // Como o utilizador pode editar e remover respostas sem adicionar às opções,
            // recarregamos todas as opções linkadas
            DatabaseInterface db = new DatabaseInterface(this);
            options = db.getOptionsAsAnswer(question.get_id());
            adapter = new OptionAdapter(this, options, question.get_id());
            answersListView.invalidate();
            answersListView.setAdapter(adapter);
            db.closeDB();
        }
    }
}
