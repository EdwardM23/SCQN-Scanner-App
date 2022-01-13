package edward.com.scannerapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int PERMISSIONS_LENGTH = 2;
    private ImageButton flash_button;
    private final int CAMERA_REQUEST_CODE = 2;
    private boolean flashOn = false, scanPaused = false;
    private FrameLayout frameLayout;
    private RemoteView remoteView;
    private TextView TVScanResult, txtScanAgain;
    private ImageView imgReset;
    private DatabaseHandler db;
    private static Bitmap bitmap_transfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        HwAds.init(this);

        flash_button = findViewById(R.id.btn_flash);
        db = new DatabaseHandler(this);
        String[] scanResult = {null};

        // SCANNER
        int mScreenWidth, mScreenHeight;
        frameLayout = findViewById(R.id.frame_layout);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        final int SCAN_FRAME_SIZE = 300;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;

        // Initialize the remote view. Use setContext() to pass the context (mandatory), use setBoundingBox() to set the scanning area, and use setFormat() to set the barcode format. Then call the build() method to create the remote view. Set the non-consecutive scanning mode using the setContinuouslyScan method (optional).
        remoteView = new RemoteView.Builder().setContext(this).enableReturnBitmap().build();
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);

        // Subscribe to the recognition result callback event.
        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                // Obtain the scanning result object HmsScan.
//                disableFlash();
//                ImageView image = findViewById(R.id.img_preview);
                Bitmap img = result[0].getOriginalBitmap();
                setBitmap_transfer(img);
//                image.setImageBitmap(img);

                scanResult[0] = result[0].getOriginalValue();
                String strScanResult = scanResult[0];
                db.addScanHistory(new History(strScanResult));

                Intent scanResultAct = new Intent(MainActivity.this, ScanResultActivity.class);
                scanResultAct.putExtra("result", strScanResult);
                startActivity(scanResultAct);
            }
        });

        // FLASHLIGHT
        flash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remoteView.switchLight();
                changeFlashIcon();
            }
        });

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_scanHistory:
                        startActivity(new Intent(getApplicationContext(), HistoryPage.class));
                        break;
                    case R.id.btn_generateQRCode:
                        startActivity(new Intent(getApplicationContext(), GenerateBarcodeActivity.class));
                        break;
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void disableFlash(){
        flash_button.setImageResource(R.drawable.flash_disable);
    }

    private void changeFlashIcon() {
        if(!scanPaused){
            if(flashOn){
                flash_button.setImageResource(R.drawable.flash_off);
                flashOn = false;
            }
            else {
                flash_button.setImageResource(R.drawable.flash_on);
                flashOn = true;
            }
        }
    }

    private void enableFlash(){
        flash_button.setImageResource(R.drawable.flash_off);
        flashOn = false;
    }

    public void parseResult(HmsScan result) {
//        resetButton();
//
        if (result.getScanTypeForm() == HmsScan.URL_FORM){
//            btnOpenInBrowser.setVisibility(View.VISIBLE);
//            btnCopyLink.setVisibility(View.VISIBLE);
        } else if (result.getScanTypeForm() ==  HmsScan.SMS_FORM) {
            // Parse the data into structured SMS data.
            HmsScan.SmsContent smsContent = result.getSmsContent();
            String content = smsContent.getMsgContent();
            String phoneNumber = smsContent.getDestPhoneNumber();
        } else if (result.getScanTypeForm() == HmsScan.WIFI_CONNECT_INFO_FORM){
            // Parse the data into structured Wi-Fi data.
            HmsScan.WiFiConnectionInfo wifiConnectionInfo = result.getWiFiConnectionInfo();
            String password = wifiConnectionInfo.getPassword();
            String ssidNumber = wifiConnectionInfo.getSsidNumber();
            int cipherMode = wifiConnectionInfo.getCipherMode();
        }
        else{
//            btnCopyLink.setVisibility(View.VISIBLE);
        }
    }

    // Use the onRequestPermissionsResult function to receive the permission verification result.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Check whether requestCode is set to the value of CAMERA_REQ_CODE during permission application, and then check whether the permission is enabled.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length == PERMISSIONS_LENGTH && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // Call the barcode scanning API to build the scanning capability.

        }
    }

    public void GoToHistoryPage(){
        Intent intent = new Intent(this, HistoryPage.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        remoteView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    public static Bitmap getBitmap_transfer() {
        return bitmap_transfer;
    }

    public static void setBitmap_transfer(Bitmap bitmap_transfer_param) {
        bitmap_transfer = bitmap_transfer_param;
    }
}