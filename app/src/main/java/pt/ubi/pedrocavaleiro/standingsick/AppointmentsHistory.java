package pt.ubi.pedrocavaleiro.standingsick;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Criado por pedrocavaleiro em 21/11/17.
 *
 * Bugs:
 *  Última verificação 22/11/2017 17:49 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class AppointmentsHistory extends AppCompatActivity {

    // Variavel que necessita de estar acessivel em toda a classe
    ListView lvHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_history);

        lvHistory = findViewById(R.id.history);

        final ArrayList<History> elements = loadData();

        Collections.reverse(elements);

        final Intent oIntent = getIntent();

        ArrayAdapter adapter = new HistoryAdapter(this, elements);

        lvHistory.setAdapter(adapter);

        // Ao definir o listener precisamos de saber se esta atividade foi chamada a partir de
        // Consultas recentes ou a partir do menu de partilha. Dependendo de onnde foi chamada
        // ou lê-mos o relatório dentro da aplicação ou criamos um email
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (oIntent.hasExtra("share")) {
                    if (oIntent.getBooleanExtra("share", true))
                    {
                        final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Relatório Pré-Triagem");
                        // O texto tem formato HTML portanto vamos carregar o texto como HTML
                        shareIntent.putExtra(
                                Intent.EXTRA_TEXT,
                                Html.fromHtml(readFile(elements.get(position).getFilename()))
                        );
                        startActivity(shareIntent);
                    } else {
                        Intent loadItem = new Intent(AppointmentsHistory.this, activity_appointment.class);
                        loadItem.putExtra("date", elements.get(position).getDate());
                        loadItem.putExtra("time", elements.get(position).getTime());
                        loadItem.putExtra("filename", elements.get(position).getFilename());
                        startActivity(loadItem);
                    }
                } else {
                    Intent loadItem = new Intent(AppointmentsHistory.this, activity_appointment.class);
                    loadItem.putExtra("date", elements.get(position).getDate());
                    loadItem.putExtra("time", elements.get(position).getTime());
                    loadItem.putExtra("filename", elements.get(position).getFilename());
                    startActivity(loadItem);
                }
            }
        });

        // Para apagar uma consulta recente basta apenas segurar no item
        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // Dialogo a confirmar a eliminação do relatório
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Ober a pasta privada da aplicação
                                PackageManager m = getPackageManager();
                                String s = getPackageName();
                                try {
                                    PackageInfo p = m.getPackageInfo(s, 0);
                                    s = p.applicationInfo.dataDir;
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.w("StandingSick", "Erro pacote não encontrado ", e);
                                }

                                File delFile = new File(s + "/files/" + elements.get(position).getFilename());
                                delFile.delete();

                                // Atualizamos a lista de consultas recentes visto que foi alterada
                                final ArrayList<History> elements = loadData();

                                Collections.reverse(elements);

                                ArrayAdapter adapter = new HistoryAdapter(AppointmentsHistory.this, elements);
                                lvHistory.invalidate();
                                lvHistory.setAdapter(adapter);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentsHistory.this);
                builder.setMessage(R.string.confirm_delete_report).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();

                return true;
            }
        });
    }

    /**
     * Carregar todos os ficheiros que existem (que fazem parte do historico) para crirar a lista
     * de elementos
     * @return lista de elementos do tipo History
     */
    public ArrayList<History> loadData() {

        ArrayList<History> elements = new ArrayList<History>();

        // Ober a pasta privada da aplicação
        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("StandingSick", "Erro pacote não encontrado ", e);
        }

        // Obtem todos os ficheiros no directório
        File fileDirectory = new File(s + "/files");

        // Passa todos os ficheiros para um array
        File[] dirFiles = fileDirectory.listFiles();

        // Precorrer todos os ficheiros e obter apenas o nome dos mesmos
        if (dirFiles != null) {
            if (dirFiles.length != 0) {
                for (int i = 0; i < dirFiles.length; i++) {
                    if (dirFiles[i].getName().toString().equals("instant-run"))
                        continue;
                    History h = new History(dirFiles[i].getName().toString());
                    elements.add(h);
                }
            }
        }
        return elements;
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
            // Lê-mos todos os bytes do ficheiro
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
