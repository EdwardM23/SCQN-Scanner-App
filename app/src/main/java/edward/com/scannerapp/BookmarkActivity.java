package edward.com.scannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;

import java.util.ArrayList;

import edward.com.scannerapp.model.Bookmark;
import edward.com.scannerapp.model.History;

public class BookmarkActivity extends AppCompatActivity implements HuaweiBannerAds{

    private ArrayList<Bookmark> bookmarkList;
    private DatabaseHandler db;
    private ImageButton btnBack, btnClearBookmark;
    private BannerView bannerView;
    private RecyclerView rvBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bannerView = findViewById(R.id.huawei_banner);
        rvBookmark = findViewById(R.id.rvBookmark);
        db = new DatabaseHandler(this);
        btnBack = findViewById(R.id.btnBackBookmark);
        btnClearBookmark = findViewById(R.id.btnClearBookmark);
        bookmarkList = db.getAllScanBookmark();

        setBookmarkAdapter();
        setHuaweiBannerAds(bannerView);
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

    public void setBookmarkAdapter() {
        BookmarkAdapter adapter = new BookmarkAdapter(bookmarkList);
        adapter.setOnItemClickListener(new BookmarkAdapter.onItemClickListener() {
            @Override
            public void onItemCopy(int position) {
                String text = bookmarkList.get(position).getResult();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
                ClipData clip = ClipData.newPlainText("copy", text);
                clipboard.setPrimaryClip(clip);
            }

            @Override
            public void onItemDelete(int position) {
                int id = bookmarkList.get(position).getId();
                Toast toast = Toast.makeText(getApplicationContext(), "NOT IMPLEMENTED YET", Toast.LENGTH_SHORT);
                toast.show();
//                db.deleteHistory(id);
//                historyList.remove(position);
//                adapter.notifyItemRemoved(position);
            }
        });

        rvBookmark.setAdapter(adapter);
        rvBookmark.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(BookmarkActivity.this, MainActivity.class);
                startActivity(home);
            }
        });

        btnClearBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.clearScanHistoryTable();
                Toast toast = Toast.makeText(getApplicationContext(), "NOT IMPLEMENTED YET", Toast.LENGTH_SHORT);
                toast.show();
//                int size = historyList.size();
//                historyList.clear();
//                adapter.notifyItemRangeRemoved(0, size);
            }
        });
    }


}