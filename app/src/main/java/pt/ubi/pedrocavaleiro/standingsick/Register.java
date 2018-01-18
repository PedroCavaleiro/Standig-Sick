package pt.ubi.pedrocavaleiro.standingsick;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Criado por pedrocavaleiro em 21/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class Register extends AppCompatActivity {

    // UI references.
    private EditText iNameField;
    private EditText iEmailField;
    private EditText iPasswordField;
    private EditText iConfirmPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iNameField = findViewById(R.id.name);
        iEmailField = findViewById(R.id.email);
        iPasswordField = findViewById(R.id.password);
        iConfirmPasswordField = findViewById(R.id.confirmPassword);
        iConfirmPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button iRegisterButton = findViewById(R.id.register);
        iRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }


    /**
     * Tentamos efetuar o registo
     * Se existirem erros (email invalido, campos em falta, etc.),
     * os erros são apresentados e não é efetuado o registo
     */
    private void attemptRegister() {

        // Limpar os erros
        iNameField.setError(null);
        iEmailField.setError(null);
        iPasswordField.setError(null);
        iConfirmPasswordField.setError(null);

        // Passar os valores dos campos de texto para variaveis
        String name = iNameField.getText().toString();
        String email = iEmailField.getText().toString();
        String password = iPasswordField.getText().toString();
        String confirmPassword = iConfirmPasswordField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Verificar se o campo de nome contem texto
        if (TextUtils.isEmpty(name)) {
            iNameField.setError(getString(R.string.required_field_err));
        }

        // Verificar se o campo de email contem texto e se o mesmo é válido
        if (TextUtils.isEmpty(email)) {
            iEmailField.setError(getString(R.string.required_field_err));
            focusView = iEmailField;
            cancel = true;
        } else if (!isEmailValid(email)){
            iEmailField.setError(getString(R.string.invalid_email_err));
            focusView = iEmailField;
            cancel = true;
        }

        // Verificar se o campo de password contem texto e se o mesmo é válido
        if (TextUtils.isEmpty(password)) {
            iPasswordField.setError(getString(R.string.required_field_err));
            focusView = iPasswordField;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            iPasswordField.setError(getString(R.string.invalid_password_err));
            focusView = iPasswordField;
            cancel = true;
        }

        // Verificar se o campo de confirmação de password contem texto
        if (TextUtils.isEmpty(confirmPassword)) {
            iConfirmPasswordField.setError(getString(R.string.required_field_err));
            focusView = iConfirmPasswordField;
            cancel = true;
        }

        // Confirmar que as passwords combinam
        if ((!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) && (password == confirmPassword)) {
            iPasswordField.setError(getString(R.string.password_dont_match_err));
            iConfirmPasswordField.setError(getString(R.string.password_dont_match_err));
            focusView = iPasswordField;
            cancel = true;
        }

        // Caso tenha ocorrido um erro cancelamos o registo e pedimos o focus para o último campo
        // que foi verificado, caso contrario guardamos tudo nas definições a password é guardada
        // como hash com o algoritmo SHA256
        if (cancel) {
            focusView.requestFocus();
        } else {
            SharedPreferences oSettings = this.getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
            SharedPreferences.Editor oEditor = oSettings.edit();
            oEditor.putBoolean(Constants.FIRST_START, false);
            oEditor.putString(Constants.USER, name);
            oEditor.putString(Constants.PASS, PasswordHandler.hashPassword(password));
            oEditor.putString(Constants.EMAIL, email);
            oEditor.commit();
            finish();
        }
    }

    /**
     * Verificar se o email é válido, de forma simples
     * @param email string a conter o email
     * @return verdadeiro ou falso dependendo se o email é valido ou não
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Verificar se a password é maior ou igual a seis caracteres
     * @param password password em texto limpo
     * @return verdadeiro ou falso dependendo se a password é valida ou não
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    // Para anular o botão de recuo do Android
    @Override
    public void onBackPressed() {
    }

    /**
     * Termina a atividade devolvendo um resultado para a atividade anterior
     */
    public void finish () {
        Intent iResponse = new Intent();
        setResult(RESULT_OK, iResponse) ;
        super.finish();
    }
}

