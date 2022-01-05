package edward.com.scannerapp;

import com.huawei.hms.ml.scan.HmsScan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class History {
    private int id;
    private String dateTime;
    private String result;
    private int id_counter = 1;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    public History(String result){
        this.id = id_counter++;
        this.result = result;
        this.dateTime = sdf.format(new Date());
    }

    public History(){

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
}
