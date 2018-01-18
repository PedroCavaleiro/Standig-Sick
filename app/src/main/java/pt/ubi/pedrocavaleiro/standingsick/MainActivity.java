package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.GravityCompat;
import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;

/**
 * Criado por pedrocavaleiro em 17/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:40 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseInterface db;         // Comunicação com a base de dados limitada às funções necessárias
    int currentQuestion = 1;      // Questão atual visivel ao utilizador
    int questionCount = 1;        // Total de questões
    TextView tvCurrQuestion;      // Elemento Gráfico que mostra em texto a questão atual e o total
    ProgressBar pgCurrQuestion;   // Elemento Gráfico que mostra uma barra de progressão
    TextView tvQuestionText;      // Elemento Gráfico que mostra a questão
    List<Question> questions;     // A lista com todas as questões a ser efetuas ao utilizador
    Button btnNext;               // Elemento Gráfico botão seguinte
    Button btnPrevious;           // Elemento Gráfico botão anterior
    EditText descricaoSintomas;   // Elemento Gráfico onde o utilizador vai escrever as observações
    LinearLayout questionArea;    // Area onde se encontram todos os elementos relacionados com as perguntas
    String observacoes;           // RESPOSTA TEMPORÁRIA - Guarda o texto inserido nas observações
    Object[] respostas;           // RESPOSTA TEMPORÁRIA - Guarda as opções selecionadas ao longo do formulário
    List<CheckBox> options;       // Lista com as opções para a pergunta atual

    /*
     * Overrides
     * A seguinte secção do ficheiro apenas contem os overrides da classe
     * Classes de onde surgem os overrides:
     *     AppCompatActivity
     *     NavigationView.OnNavigationItemSelectedListener
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseInterface(this);

        // Barra de titulo/navegação da aplicação
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Ligação para a "gaveta" de navegação
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        btnNext = findViewById(R.id.btnnext);
        btnPrevious = findViewById(R.id.btnprevious);
        btnPrevious.setVisibility(View.INVISIBLE);

        questionArea = findViewById(R.id.questionsArea);
        tvCurrQuestion = findViewById(R.id.tvCurrQuestion);
        pgCurrQuestion = findViewById(R.id.pgCurrQuestion);

        tvQuestionText = findViewById(R.id.tvQuestion);
        loadSettings();

    }

    // Ver o estado da gaveta se estiver aberta fechamos a gaveta, caso contrario navegamos para a
    // view anterior
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Carregamos/Recarregamos o formulário
    @Override
    public void onResume() {
        super.onResume();
        if (currentQuestion == questionCount) {
            questionArea.removeView(descricaoSintomas);
            currentQuestion = 1;
            loadForm();
        } else {
            removeCheckBoxes();
            currentQuestion = 1;
            loadForm();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_new_report) {
            if (currentQuestion == questionCount) {
                questionArea.removeView(descricaoSintomas);
                currentQuestion = 1;
                loadForm();
            } else {
                removeCheckBoxes();
                currentQuestion = 1;
                loadForm();
            }
            btnPrevious.setVisibility(View.INVISIBLE);
            btnNext.setText(R.string.button_next);
        } else if (id == R.id.nav_latest_appointments) {
            Intent toHistory = new Intent(this, AppointmentsHistory.class);
            startActivity(toHistory);
        } else if (id == R.id.nav_settings) {
            Intent toSettings = new Intent(this, Settings.class);
            startActivityForResult(toSettings, Constants.INTENT_SHOW_SETTINGS);
        } else if (id == R.id.nav_manage_questions) {
            Intent toQuestionManager = new Intent(this, ManageQuestions.class);
            askForPasswordForNavigation(toQuestionManager);
        } else if (id == R.id.nav_manage_answers) {
            Intent toAnswerManager = new Intent(this, ManageAnswers.class);
            toAnswerManager.putExtra("calledFrom", "home");
            askForPasswordForNavigation(toAnswerManager);
        } else if (id == R.id.nav_reset_database) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            askForPassword(Constants.ACTION_RESET_DATABASE);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm_reset_database).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show();
        } else if (id == R.id.nav_send) {
            Intent shareReport = new Intent(this, AppointmentsHistory.class);
            shareReport.putExtra("share", true);
            startActivity(shareReport);
        }

        // Quando escolhemos um menu, fechamos a "gaveta"
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int reqCode, int rCode, Intent iData) {
        if (reqCode == Constants.INTENT_SHOW_REPORT) {
            if (currentQuestion == questionCount) {
                questionArea.removeView(descricaoSintomas);
                currentQuestion = 1;
                loadForm();
            } else {
                removeCheckBoxes();
                currentQuestion = 1;
                loadForm();
            }
            btnPrevious.setVisibility(View.INVISIBLE);
            btnNext.setText(R.string.button_next);
        }
    }

    /*
     *
     * Overrides END
     *
     */

    /*
     *
     * Event Handlers
     * A seguinte secção do ficheiro contem os handlers para todos os elementos relacionados com
     * esta atividade
     *
     */

    /**
     * Controla o evento do botão "Seguinte"/"Gerar Relatório"
     * @param view a view de onde foi chamado o evento
     */
    public void nextQuestion(View view) {
        if (currentQuestion < questionCount) {
            // Progredimos, então vamos guardar as respostas da questão anterior
            saveQuestionAnswers();

            currentQuestion++;

            // Atualizamos a pergunta atual (para o utilizador)
            tvCurrQuestion.setText("Pergunta " + Integer.toString(currentQuestion) + " de " +
                    Integer.toString(questionCount));
            pgCurrQuestion.setProgress(currentQuestion);

            // Verificar se estamos nas questões de escolha multipla ou se nos encontramos na
            // ultima questão.
            if (currentQuestion < questionCount) {

                // Mudamos o texto da pergunta para a pergunta que se encontra armazenada na
                // base de dados
                tvQuestionText.setText((CharSequence) questions.get(currentQuestion - 1).get_question());
                removeCheckBoxes();

                // Adicionamos as escolhas à area de questões
                loadOptionsCheckBoxes(questions.get(currentQuestion - 1).get_id());

                // Podemos já ter as respostas guardadas, então vamos carrega-las
                readSavedQuestionsAnswers();

            } else if (currentQuestion == questionCount) {

                // Apagamos as checkboxes
                removeCheckBoxes();

                // Mudamos o texto para a ultima pergunta
                tvQuestionText.setText(R.string.last_question);

                // Criamos o elemento de edição de texto
                descricaoSintomas = new EditText(this);
                descricaoSintomas.setInputType(TYPE_CLASS_TEXT);

                // Adicionamos um Listener para quando o texto for alterado guardar nas respostas
                // como texto temporário
                descricaoSintomas.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        observacoes = s.toString();
                    }
                });

                // Pegamos na resposta e colocamos a resposta temporaria, caso exista.
                descricaoSintomas.setText(observacoes);
                // Adicionamos o elemento ao LinearView
                questionArea.addView(descricaoSintomas);
            }
        } else if (currentQuestion == questionCount) {
            genReport();
        }

        // Verificamos em que pergunta é que estamos e dependendo da pergunta atual mostramos ou
        // escondemos os botões de seguinte e anterior
        if (currentQuestion > 1) {
            btnPrevious.setVisibility(View.VISIBLE);
        } else if (currentQuestion == 1) {
            btnPrevious.setVisibility(View.INVISIBLE);
        }
        if (currentQuestion < questionCount) {
            btnNext.setText(R.string.button_next);
        } else if (currentQuestion == questionCount) {
            btnNext.setText(R.string.gen_report);
        }
    }

    /**
     * Controla o evento do botão "Anterior"
     * @param view a view de onde foi chamado o evento
     */
    public void previousQuestion(View view) {

        if (currentQuestion == questionCount) {
            questionArea.removeView(descricaoSintomas);
            btnNext.setText(R.string.button_next);
        } else {
            saveQuestionAnswers();
        }

        if (currentQuestion >= 1) {
            currentQuestion--;
            removeCheckBoxes();
            loadOptionsCheckBoxes(questions.get(currentQuestion - 1).get_id());
            readSavedQuestionsAnswers();
            tvCurrQuestion.setText("Pergunta " + Integer.toString(currentQuestion) + " de " +
                    Integer.toString(questionCount));
            pgCurrQuestion.setProgress(currentQuestion);
            tvQuestionText.setText((CharSequence) questions.get(currentQuestion - 1).get_question());
        }

        // Verificamos em que pergunta é que estamos e dependendo da pergunta atual mostramos ou
        // escondemos os botões de seguinte e anterior
        if (currentQuestion > 1) {
            btnPrevious.setVisibility(View.VISIBLE);
        } else if (currentQuestion == 1) {
            btnPrevious.setVisibility(View.INVISIBLE);
        }
        if (currentQuestion < questionCount) {
            btnNext.setVisibility(View.VISIBLE);
        } else if (currentQuestion == questionCount) {
            btnNext.setVisibility(View.INVISIBLE);
        }
    }

    /*
     *
     * Event Handlers END
     *
     */

    /*
     *
     * Funções auxiliares
     * Esta parte do ficheiro contem apenas algumas funções auxiliares
     *
     */

    /**
     * Carregar as definições do programa
     */
    private void loadSettings() {

        SharedPreferences oSettings = this.getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);

        // Verificar se é o primeiro arranque
        if (oSettings.getBoolean(Constants.FIRST_START, true)) {
            Intent iRegister = new Intent(this, Register.class);
            //Pedimos para devolver um resultado mesmo não sendo necessário nenhum valor
            // apenas para recarregar as definições para a UI
            startActivityForResult(iRegister, Constants.INTENT_REGISTER);
        } else {

            // Obtemos a View relacionada com a barra de navegação para ser possível obter o ID
            // correto dos campos que vão ser modificados
            // Sem este passo é apresentado um erro de NullPointerException
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView usrView = headerView.findViewById(R.id.drawer_username);
            TextView emailView = headerView.findViewById(R.id.drawer_email);
            usrView.setText(oSettings.getString(Constants.USER, "Utilizador desconhecido"));
            emailView.setText(oSettings.getString(Constants.EMAIL, "Email desconhecido"));
        }
    }

    /**
     * Carregar o formulário
     */
    private void loadForm() {
        // Contar o n de perguntas existentes
        // Adicionamos 1 para contar com o campo de texto
        questionCount = db.countQuestions() + 1;

        // Iniciar o formulario (nº de questão a atual e nº de questões totais)
        tvCurrQuestion.setText("Pergunta " + Integer.toString(currentQuestion) + " de " +
                Integer.toString(questionCount));
        pgCurrQuestion.setMax(questionCount);
        pgCurrQuestion.setProgress(currentQuestion);

        // Obter as perguntas
        questions = new ArrayList<Question>();
        questions = db.getQuestions();
        tvQuestionText.setText((CharSequence) questions.get(currentQuestion - 1).get_question());

        // Caso estejamos a limpar o formulario, vamos limpar o texto que ficou guardado nas
        // observações e vamos colocar o botão "Gerar Relatório" como "Seguinte" caso seja
        // necessário
        observacoes = "";
        btnNext.setText(R.string.button_next);

        // Lista onde vão ser guardadas as respoastas temporarias
        // Cada pergunta pode ter várias respostas válidas
        respostas = new Object[questionCount - 1];

        // Onde vão ser guardadas as opções
        loadOptionsCheckBoxes(questions.get(0).get_id());
    }

    /**
     * Carregar as opções para uma dada questão
     * @param questionID O id da questão atual
     */
    private void loadOptionsCheckBoxes(int questionID) {
        List<String> tmpOptions = db.getOptions(questionID);
        options = new ArrayList<CheckBox>();
        for (String option : tmpOptions) {
            CheckBox cb = new CheckBox(this);
            cb.setText(option);
            cb.setTextSize(28);
            cb.setTag(tmpOptions.indexOf(cb));
            options.add(cb);
            questionArea.addView(cb);
        }
    }

    /**
     * Remove as checkboxes do linear layout
     */
    private void removeCheckBoxes() {
        for (CheckBox option : options) {
            questionArea.removeView(option);
        }
    }

    /**
     * Guarda as respostas dadas a uma questão, visto que uma questão pode ter multiplas respostas
     * válidas
     */
    private void saveQuestionAnswers() {
        List<CheckBox> answers = new ArrayList<CheckBox>();
        for (CheckBox cb : options) {
            if (cb.isChecked())
                answers.add(cb);
        }
        respostas[currentQuestion - 1] = answers;
    }

    /**
     * Lê as respostas dadas a uma questão
     */
    private void readSavedQuestionsAnswers() {
        List<CheckBox> answers = (List<CheckBox>) respostas[currentQuestion - 1];
        if (answers != null) {
            for (CheckBox answer : answers) {
                for (CheckBox option : options) {
                    if (answer.getText().equals(option.getText()))
                        option.setChecked(true);
                }
            }
        }
    }

    /**
     * Gera o relatório e mostra ao utilizador
     */
    private void genReport() {

        SharedPreferences oSettings = this.getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);

        String report = getString(R.string.title) + "<br />";
        report += getString(R.string.usr) + " " + oSettings.getString(Constants.USER, "Utilizador desconhecido") + "<br />";
        report += getString(R.string.email) + " " + oSettings.getString(Constants.EMAIL, "Email desconhecido") + "<br /><br />";

        Intent oReportIntent = new Intent(this, Report.class);

        int i = 0;
        for (Question q : questions) {
            String question = q.get_question();
            report += "<b>" + question + "</b><br />";

            List<CheckBox> r = (List<CheckBox>) respostas[i];

            for (CheckBox cb : r) {
                report += cb.getText().toString() + "<br />";
            }

            report += "<br />";

            i++;
        }

        if (observacoes != "") {
            report += "<b>Observações:</b><br />";
            report += observacoes;
        }


        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String filename = ts + ".txt";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(report.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        oReportIntent.putExtra("relatorio", report);

        startActivityForResult(oReportIntent, Constants.INTENT_SHOW_REPORT);
    }

    /**
     * Pede a password ao utilizador para dar acesso à gestão de perguntas/respostas
     * @param intent Intento a iniciar caso a password esteja correta
     */
    private void askForPasswordForNavigation(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_password));

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPassword = input.getText().toString();
                SharedPreferences oSettings = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
                String hashedPassword = PasswordHandler.hashPassword(enteredPassword);
                if (hashedPassword.equals(oSettings.getString(Constants.PASS, "nopassword"))) {
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this,
                            getString(R.string.incorrect_password),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Pede a password ao utilizador para efetuar uma ação sem sair da atividade
     * @param action id da ação a ser efetuada
     */
    private void askForPassword(final int action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_password));

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPassword = input.getText().toString();
                SharedPreferences oSettings = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
                String hashedPassword = PasswordHandler.hashPassword(enteredPassword);
                if (hashedPassword.equals(oSettings.getString(Constants.PASS, "nopassword"))) {
                    switch (action) {
                        case Constants.ACTION_RESET_DATABASE:
                            db.resetDatabase();
                            onResume();
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast toast = Toast.makeText(MainActivity.this,
                            getString(R.string.incorrect_password),
                            Toast.LENGTH_LONG);
                    toast.show();

                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /*
     *
     * Funções auxiliares END
     *
     */

}
