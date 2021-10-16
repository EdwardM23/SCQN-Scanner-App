package edward.com.scannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class HistoryPage extends AppCompatActivity {

    private ArrayList<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);

        // Lookup the recyclerview in activity layout
        RecyclerView rvHistory = (RecyclerView) findViewById(R.id.rvHistory);

//        historyList =
        // Create adapter passing in the sample user data
        HistoryAdapter adapter = new HistoryAdapter(historyList) {
        };
        // Attach the adapter to the recyclerview to populate items
        rvHistory.setAdapter(adapter);
        // Set layout manager to position the items
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
    }

    public void addHistory(History history){
        historyList.add(history);
    }
}