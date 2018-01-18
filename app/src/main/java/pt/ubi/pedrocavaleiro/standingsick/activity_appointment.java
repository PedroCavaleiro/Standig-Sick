package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Criado por pedrocavaleiro em 21/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class activity_appointment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        Intent i = getIntent();

        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvData = findViewById(R.id.tvData);

        tvDate.setText(i.getStringExtra("date"));
        tvTime.setText(i.getStringExtra("time"));

        // O texto está gravado com formatação HTML precisamos de manter essa mesma formatação
        // utilizando o HTML.fromHtml
        tvData.setText(Html.fromHtml(readFile(i.getStringExtra("filename"))));

    }

    /**
     * Ler o ficheiro que contem o relatório
     * @param filename nome do ficheiro (não inclui o caminho)
     * @return retorna o texto
     */
    private String readFile(String filename) {

        String report = "";

        // Ober a pasta privada da aplicação
        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("StandingSick", "Erro pacote não encontrado ", e);
        }

        byte[] getBytes;

        try {
            // Lêmos todos os bytes dos ficheiros para mais tarde construir a string
            File file = new File(s + "/files/" + filename);
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();

            // Construimos a String a partir dos bytes do ficheiro com a formatação UTF-8
            report = new String(getBytes, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return report;

    }

}
