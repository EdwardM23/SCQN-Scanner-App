package edward.com.scannerapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Bookmark {
    private int id;
    private String dateTime;
    private int history_id;
    private String result;
    private int id_counter = 1;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    public Bookmark(String result, int history_id){
        this.id = id_counter++;
        this.history_id = history_id;
        this.result = result;
        this.dateTime = sdf.format(new Date());
    }

    public Bookmark() {
    }

    public String getResult() {
        return result;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHistory_id() {
        return history_id;
    }

    public void setHistory_id(int history_id) {
        this.history_id = history_id;
    }
}
