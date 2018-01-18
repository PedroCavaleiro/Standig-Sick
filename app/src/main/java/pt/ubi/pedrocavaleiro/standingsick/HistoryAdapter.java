package pt.ubi.pedrocavaleiro.standingsick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

public class HistoryAdapter extends ArrayAdapter<History> {

    // Variaveis que são necessárias pela classe toda e também em métodos anonimos
    private final Context context;
    private final ArrayList<History> elements;

    public HistoryAdapter(Context context, ArrayList<History> elements) {
        super(context, R.layout.history_lv_line, elements);
        this.context = context;
        this.elements = elements;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.history_lv_line, parent, false);

        TextView tvDate = rowView.findViewById(R.id.histDate);
        TextView tvTime = rowView.findViewById(R.id.histTime);

        tvDate.setText(elements.get(position).getDate());
        tvTime.setText(elements.get(position).getTime());

        return rowView;

    }

}
