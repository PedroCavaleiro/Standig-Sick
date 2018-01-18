package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

/**
 * Criado por pedrocavaleiro em 17/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class Report extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent oIntent = getIntent();
        TextView tvReport = findViewById(R.id.reportView);
        tvReport.setText(Html.fromHtml(oIntent.getStringExtra("relatorio")));
    }

    /**
     * Obrigar a atividade a devolver um resultado
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
