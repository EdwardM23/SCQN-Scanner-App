package edward.com.scannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.ml.scan.HmsScan;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class HistoryPage extends AppCompatActivity {

    private ArrayList<History> historyList;
    private DatabaseHandler db;
    private ImageButton btnBack, btnClearHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BannerView bannerView = findViewById(R.id.huawei_banner);
        RecyclerView rvHistory = findViewById(R.id.rvHistory);
        db = new DatabaseHandler(this);
        btnBack = findViewById(R.id.btnBack);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        historyList = db.getAllScanHistory();

        HistoryAdapter adapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(adapter);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        //Set Huawei Ads Banner
        HwAds.init(this);
        bannerView.setAdId(("testw6vs28auh3"));
        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_360_57);
        bannerView.setBannerRefresh(60);
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(HistoryPage.this, MainActivity.class);
                startActivity(home);
            }
        });

        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.clearScanHistoryTable();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }
}