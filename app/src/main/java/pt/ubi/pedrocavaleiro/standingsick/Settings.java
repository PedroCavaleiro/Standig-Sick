package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.w3c.dom.Text;

/**
 * Criado por pedrocavaleiro em 17/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class Settings extends AppCompatActivity {

    EditText etName;
    EditText etEmail;
    EditText etPassword;
    EditText etConfirmPassword;
    Button btnSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences oSettings = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);

        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.passwordConfirm);
        btnSaveSettings = findViewById(R.id.btnSave);
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });

        etName.setText(oSettings.getString(Constants.USER, "NoUser"));
        etEmail.setText(oSettings.getString(Constants.EMAIL, "NoEmail"));

    }

    /**
     * Função para guardar as definições
     */
    private void saveSettings() {
        // Limpamos todos os erros
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        boolean changePassword = false;
        String passwordHash = "";

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.required_field_err));
            focusView = etName;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.required_field_err));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.invalid_email_err));
            focusView = etEmail;
            cancel = true;
        }

        // Verificamos se o utilizador tentou alterar a password
        if (!TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmPassword)) {
            if (TextUtils.isEmpty(password)) {
                etPassword.setError(getString(R.string.required_field_err));
                focusView = etPassword;
                cancel = true;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                etConfirmPassword.setError(getString(R.string.required_field_err));
                focusView = etConfirmPassword;
                cancel = true;
            }

            // Se ambos os campos tiverem preenchidos verificamos se a password condiz
            // com a confirmação de password
            if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) &&
                    !password.equals(confirmPassword)) {
                etPassword.setError(getString(R.string.password_dont_match_err));
                etConfirmPassword.setError(getString(R.string.password_dont_match_err));
                focusView = etPassword;
                cancel = true;
            } else {
                if (!isPasswordValid(password)) {
                    etPassword.setError(getString(R.string.invalid_password_err));
                    focusView = etPassword;
                    cancel = true;
                } else {
                    changePassword = true;
                    passwordHash = PasswordHandler.hashPassword(password);
                }
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            SharedPreferences oSettings = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
            SharedPreferences.Editor oSettingsEditor = oSettings.edit();

            oSettingsEditor.putString(Constants.USER, name);
            if (changePassword)
                oSettingsEditor.putString(Constants.PASS, passwordHash);
            oSettingsEditor.putString(Constants.EMAIL, email);
            oSettingsEditor.commit();
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

    /**
     * Termina a atividade devolvendo um resultado para a atividade anterior
     */
    public void finish () {
        Intent iResponse = new Intent();
        setResult(RESULT_OK, iResponse) ;
        super.finish();
    }

}
