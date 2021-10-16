package edward.com.scannerapp;

import com.huawei.hms.ml.scan.HmsScan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class History {
    private String dateTime;
    private HmsScan text;
    ArrayList<History> historyList = new ArrayList<>();

    public History(HmsScan text){
        this.text = text;
        this.dateTime = getDateTime();
    }

    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public ArrayList<History> getHistoryList(){
        return historyList;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public HmsScan getText() {
        return text;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setText(HmsScan text) {
        this.text = text;
    }
}
