package edward.com.scannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.huawei.hms.ml.scan.HmsScan;

import android.os.Bundle;

import java.util.ArrayList;

public class HistoryPage extends AppCompatActivity {

    private ArrayList<History> historyList;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView rvHistory = findViewById(R.id.rvHistory);

        db = new DatabaseHandler(this);

        historyList = db.getAllScanHistory();
        HistoryAdapter adapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(adapter);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
    }
}