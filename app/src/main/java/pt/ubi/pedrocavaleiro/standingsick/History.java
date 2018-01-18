package pt.ubi.pedrocavaleiro.standingsick;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Criado por pedrocavaleiro em 20/11/17.
 */

public class History {

    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String date;
    private String time;

    public History(String filename) {
        this.filename = filename;
        Date DateTime = new Date((long)Long.parseLong(filename.replace(".txt", ""))*1000);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        this.date = formatter.format(DateTime);
        formatter = new SimpleDateFormat("HH:mm");
        this.time = formatter.format(DateTime);
    }

}
