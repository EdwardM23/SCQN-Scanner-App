package edward.com.scannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.banner.BannerView;

import edward.com.scannerapp.model.Bookmark;
import edward.com.scannerapp.model.History;

public class ScanResultActivity extends AppCompatActivity implements HuaweiBannerAds{

    private final int REQUEST_CODE_FILE = 300;

    private ImageButton btnCopy, btnOpenInBrowser, btnMenu,btnBookmark;
    private TextView txtResult;
    private Button btnHistory;
    private ImageView imgScan;
    private BannerView bannerView;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_scan_result);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = new DatabaseHandler(this);

        bannerView = findViewById(R.id.hw_banner_view);
        btnCopy = findViewById(R.id.btnCopy);
        btnOpenInBrowser = findViewById(R.id.btnOpenInBrowser);
        btnBookmark = findViewById(R.id.btnBookmarkResult);
        txtResult = findViewById(R.id.txtResult);
        btnHistory = findViewById(R.id.btnHistory);
        btnMenu = findViewById(R.id.btnMenu);

        imgScan = findViewById(R.id.imgScan);
        imgScan.setImageBitmap(MainActivity.getBitmap_transfer());

        String scanResult = getIntent().getStringExtra(MainActivity.SCAN_RESULT);
        long scanResultId = getIntent().getLongExtra(MainActivity.SCAN_RESULT_ID, -1);
        txtResult.setText(scanResult);

        showButtons(scanResult);
        setHuaweiBannerAds(bannerView);

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

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBookmark.setImageResource(R.drawable.btn_bookmark);
                db.addScanBookmark(new Bookmark(scanResult, (int) scanResultId));
                Toast toast = Toast.makeText(getApplicationContext(), "Added to bookmark", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(getApplicationContext(), HistoryPage.class);
                startActivity(history);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_scan_result);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_scanHistory:
                        startActivity(new Intent(getApplicationContext(), HistoryPage.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        break;
                    case R.id.btn_generateQRCode:
                        startActivity(new Intent(getApplicationContext(), GenerateBarcodeActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        break;
                    case R.id.btn_scanFromFile:
                        scanFromFile();
                        break;
                    case R.id.btn_bookmark:
                        startActivity(new Intent(getApplicationContext(), BookmarkActivity.class). addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        break;
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_scan_result);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void scanFromFile() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ScanResultActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_FILE);
    }

    private void showButtons(String scanResult) {
        btnCopy.setVisibility(View.VISIBLE);
        btnBookmark.setVisibility(View.VISIBLE);
        if(scanResult.startsWith("http://") || scanResult.startsWith("https://")) {
            btnOpenInBrowser.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setHuaweiBannerAds(BannerView banner) {
        banner.setAdId("testw6vs28auh3");
        banner.setBannerRefresh(60);
        AdParam adParam = new AdParam.Builder().build();
        banner.loadAd(adParam);
        banner.setAdListener(adListener);
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