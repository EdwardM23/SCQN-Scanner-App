package edward.com.scannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
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

        BannerView bannerView = findViewById(R.id.huawei_banner);
        RecyclerView rvHistory = findViewById(R.id.rvHistory);

        db = new DatabaseHandler(this);

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
    }
}