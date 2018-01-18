package pt.ubi.pedrocavaleiro.standingsick;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

public class ManageAnswers extends AppCompatActivity {

    Intent upIntent;
    boolean requiresResult = false;
    public static ListView answerList;

    public static ArrayAdapter adapter;
    public static ArrayList<Answer> answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_answers);

        Intent oIntent = getIntent();

        answerList = findViewById(R.id.lvAnswers);
        answers = loadAnswers();

        adapter = new AnswerAdapter(this, answers);


        answerList.setAdapter(adapter);
        answerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                upIntent.putExtra("answerID", answers.get(position).getAnswerID());
                finish();
            }
        });

        upIntent = new Intent();

        // Vêmos de onde é que a atividade foi chamada para saber para qual atividade é que vamos regressar
        if (oIntent.hasExtra("calledFrom")) {
            if (oIntent.getStringExtra("calledFrom").equals("questionManager")) {
                requiresResult = true;
            } else
                upIntent = new Intent(this, MainActivity.class);
        } else
            upIntent = new Intent(this, MainActivity.class);

        // Criar programaticamento o botão de up
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private ArrayList<Answer> loadAnswers() {
        ArrayList<Answer> elements = new ArrayList<Answer>();
        DatabaseInterface db = new DatabaseInterface(this);
        elements = db.getAnswers();

        return elements;

    }

    /**
     * Visto que o up button foi adicionado programaticamente e não no manifesto iremos
     * controlar a sua acção.
     * Esta função é um callback para todos os botões na toolbar
     * @param item item clicado
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Handler para o botão de recuo na toolbar
            case android.R.id.home:
                if (requiresResult)
                    finish();
                else
                    NavUtils.navigateUpTo(this, upIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Termina a atividade devolvendo um resultado para a atividade anterior
     */
    public void finish () {
        setResult(RESULT_OK, upIntent) ;
        super.finish();
    }

    /**
     * Adiciona uma nova resposta
     * @param view
     */
    public void addAnswer(View view) {
        // É criada um "alerta" com uma caixa de texto para introduzir a resposta
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.answer_name_field);

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.questionSaveQuestion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = input.getText().toString();
                // Verificamos se o texto introduzido está vaizo ou se já existe na base de dados
                if(TextUtils.isEmpty(answer)) {
                    Toast t = Toast.makeText(ManageAnswers.this,
                            getString(R.string.invalidAnswerName), Toast.LENGTH_LONG);
                    t.show();
                } else {
                    DatabaseInterface db = new DatabaseInterface(ManageAnswers.this);

                    if (db.answerExsists(answer)) {
                        Toast t = Toast.makeText(ManageAnswers.this,
                                getString(R.string.answer_already_exists), Toast.LENGTH_LONG);
                        t.show();
                    } else {
                        db.addAnswer(answer);
                        answers = loadAnswers();
                        adapter = new AnswerAdapter(ManageAnswers.this, answers);
                        answerList.invalidate();
                        answerList.setAdapter(adapter);
                        db.closeDB();
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
}
