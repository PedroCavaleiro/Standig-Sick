package pt.ubi.pedrocavaleiro.standingsick;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Criado por pedrocavaleiro em 21/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class ManageQuestions extends AppCompatActivity {

    ListView lvQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_questions);

        lvQuestions = findViewById(R.id.lvQuestions);

        final ArrayList<Question> elements = loadQuestions();

        ArrayAdapter adapter = new QuestionAdapter(this, elements);

        lvQuestions.setAdapter(adapter);
        lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent loadItem = new Intent(ManageQuestions.this, AddEditQuestions.class);
                loadItem.putExtra("questionID", elements.get(position).get_id());
                startActivity(loadItem);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        final ArrayList<Question> elements = loadQuestions();

        ArrayAdapter adapter = new QuestionAdapter(this, elements);

        lvQuestions.setAdapter(adapter);
        lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent loadItem = new Intent(ManageQuestions.this, AddEditQuestions.class);
                loadItem.putExtra("questionID", elements.get(position).get_id());
                startActivity(loadItem);
            }
        });
    }

    public void addQuestion(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_new_question);

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.questionSaveQuestion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String question = input.getText().toString();
                if (TextUtils.isEmpty(question)) {
                    Toast t = Toast.makeText(ManageQuestions.this, getString(R.string.invalidQuestionName), Toast.LENGTH_LONG);
                    t.show();
                } else {
                    DatabaseInterface db = new DatabaseInterface(ManageQuestions.this);

                    if (db.questionExists(question)) {
                        Toast t = Toast.makeText(ManageQuestions.this,
                                getString(R.string.question_already_exists), Toast.LENGTH_LONG);
                        t.show();
                        db.closeDB();
                    } else {
                        int newID = db.addQuestion(question);
                        db.closeDB();
                        Intent loadItem = new Intent(ManageQuestions.this, AddEditQuestions.class);
                        loadItem.putExtra("questionID", newID);
                        startActivity(loadItem);

                    }
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

    private ArrayList<Question> loadQuestions() {
        List<Question> elements = new ArrayList<Question>();
        ArrayList<Question> returnElements = new ArrayList<Question>();
        DatabaseInterface db = new DatabaseInterface(this);
        elements = db.getQuestions();

        for (Question question : elements) {
            question.calculateAssignedAnswers(this);
            returnElements.add(question);
        }

        return returnElements;

    }

}
