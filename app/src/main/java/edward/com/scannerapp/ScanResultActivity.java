package edward.com.scannerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.banner.BannerView;

public class ScanResultActivity extends AppCompatActivity {

    private ImageButton btnCopy, btnOpenInBrowser, btnBack;
    private TextView txtResult;
    private Button btnHistory;
    private ImageView imgScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // AD BANNER
        BannerView bannerView = findViewById(R.id.hw_banner_view);
        bannerView.setAdId("testw6vs28auh3");
        bannerView.setBannerRefresh(60);
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);
        bannerView.setAdListener(adListener);

        btnCopy = findViewById(R.id.btnCopy);
        btnOpenInBrowser = findViewById(R.id.btnOpenInBrowser);
        txtResult = findViewById(R.id.txtResult);
        btnHistory = findViewById(R.id.btnHistory);
        btnBack = findViewById(R.id.btnBack);

        imgScan = findViewById(R.id.imgScan);
        imgScan.setImageBitmap(MainActivity.getBitmap_transfer());

        String scanResult = getIntent().getStringExtra("result");
        txtResult.setText(scanResult);

        showButtons(scanResult);

        btnOpenInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = scanResult;
                if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        // COPY LINK
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = scanResult;
                if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
                ClipData clip = ClipData.newPlainText("copy", url);
                clipboard.setPrimaryClip(clip);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(getApplicationContext(), HistoryPage.class);
                startActivity(history);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(home);
            }
        });
    }

    private void showButtons(String scanResult) {
        if(scanResult.startsWith("http://") || scanResult.startsWith("https://")){
            btnOpenInBrowser.setVisibility(View.VISIBLE);
            btnCopy.setVisibility(View.VISIBLE);
        } else{
            btnCopy.setVisibility(View.VISIBLE);
        }
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            // Called when an ad is loaded successfully.
        }
        @Override
        public void onAdFailed(int errorCode) {
            // Called when an ad fails to be loaded.
        }
        @Override
        public void onAdOpened() {
            // Called when an ad is opened.
        }
        @Override
        public void onAdClicked() {
            // Called when an ad is clicked.
        }
        @Override
        public void onAdLeave() {
            // Called when an ad leaves an app.
        }
        @Override
        public void onAdClosed() {
            // Called when an ad is closed.
        }
    };
}