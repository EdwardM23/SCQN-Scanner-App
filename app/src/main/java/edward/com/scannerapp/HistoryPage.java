package edward.com.scannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import edward.com.scannerapp.model.Bookmark;
import edward.com.scannerapp.model.History;

public class HistoryPage extends AppCompatActivity implements HuaweiBannerAds {

    private final int REQUEST_CODE_FILE = 300;

    private ArrayList<History> historyList;
    private DatabaseHandler db;
    private ImageButton btnMenu, btnClearHistory;
    private BannerView bannerView;
    private RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_history);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bannerView = findViewById(R.id.huawei_banner);
        rvHistory = findViewById(R.id.rvHistory);
        db = new DatabaseHandler(this);
        btnMenu = findViewById(R.id.btnMenu);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        historyList = db.getAllScanHistory();

        setHistoryAdapter();
        setHuaweiBannerAds(bannerView);

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
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_history);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void scanFromFile() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        HistoryPage.this.startActivityForResult(pickIntent, REQUEST_CODE_FILE);
    }

    public void setHuaweiBannerAds(BannerView banner) {
        //Set Huawei Ads Banner
        HwAds.init(this);
        banner.setAdId(("testw6vs28auh3"));
//        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_360_57);
        banner.setBannerRefresh(60);
        AdParam adParam = new AdParam.Builder().build();
        banner.loadAd(adParam);
    }

    public void setHistoryAdapter() {
        HistoryAdapter adapter = new HistoryAdapter(historyList);
        adapter.setOnItemClickListener(new HistoryAdapter.onItemClickListener() {
            @Override
            public void onItemBookmark(int position) {
                History historyObject = historyList.get(position);

                // Jika saat ini udah di bookmark, maka ketika diklik, berarti delete bookmarknya.
                if (historyObject.isBookmarked()) {
                    db.deleteBookmarkByHistoryID(historyObject.getId());
                    historyObject.setBookmarked(false);
                }

                // Sebaliknya, jika saat ini belum di-bookmark, ketika diklik, akan di-bookmark.
                else if (!historyObject.isBookmarked()) {
                    db.addScanBookmark(new Bookmark(historyObject.getResult(), historyObject.getId()));
                    historyObject.setBookmarked(true);
                }

                adapter.notifyItemChanged(position);
            }

            @Override
            public void onItemCopy(int position) {
                String text = historyList.get(position).getResult();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
                ClipData clip = ClipData.newPlainText("copy", text);
                clipboard.setPrimaryClip(clip);
            }

            @Override
            public void onItemDelete(int position) {
                int id = historyList.get(position).getId();
                db.deleteHistory(id);
                historyList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });


        rvHistory.setAdapter(adapter);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_history);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.clearScanHistoryTable();
                int size = historyList.size();
                historyList.clear();
                adapter.notifyItemRangeRemoved(0, size);
            }
        });
    }

}