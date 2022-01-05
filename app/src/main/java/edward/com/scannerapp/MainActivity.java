package edward.com.scannerapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int PERMISSIONS_LENGTH = 2;
    private Button btnOpenInBrowser, btnCopyLink, btnScanHistory;
    private ImageButton flash_button;
    private final int CAMERA_REQUEST_CODE = 2;
    private boolean flashOn = false, scanPaused = false;
    private FrameLayout frameLayout;
    private RemoteView remoteView;
    private TextView TVScanResult, txtScanAgain;
    private ImageView imgReset;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        HwAds.init(this);

        // AD BANNER
        // Obtain BannerView.
//        BannerView bannerView = findViewById(R.id.hw_banner_view);
//        bannerView.setBannerRefresh(20);
//        AdParam adParam = new AdParam.Builder().build();
//        bannerView.loadAd(adParam);

        txtScanAgain = findViewById(R.id.txtScanAgain);
        imgReset = findViewById(R.id.imgReset);

        flash_button = findViewById(R.id.btn_flash);
        btnOpenInBrowser = findViewById(R.id.btnOpenInBrowser);
        btnCopyLink = findViewById(R.id.btnCopy);
        TVScanResult = findViewById(R.id.txtScanResult);
        btnScanHistory = findViewById(R.id.btnScanHistory);
        db = new DatabaseHandler(this);
        String[] scanResult = {null};

        db.clearScanHistoryTable();
        db.addScanHistory(new History("Scan Test 1"));
        db.addScanHistory(new History("Scan Test 2"));
        db.addScanHistory(new History("Scan Test 3"));

        // SCANNER
        int mScreenWidth, mScreenHeight;
        // Bind the camera preview layout.
        frameLayout = findViewById(R.id.frame_layout);
        // Set the scanning area. Set the parameters as required.
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
//        Log.d(TAG, "Density = " + density);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
//        Log.d(TAG, "Width = " + mScreenHeight + " Height = " + mScreenWidth);

        // Set the width and height of the barcode scanning box to 300 dp.
        final int SCAN_FRAME_SIZE = 1000;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;
        Log.d(TAG, "rect: " + rect.left + "_" + rect.right + "_" + rect.top + "_" + rect.bottom);

        // Initialize the remote view. Use setContext() to pass the context (mandatory), use setBoundingBox() to set the scanning area, and use setFormat() to set the barcode format. Then call the build() method to create the remote view. Set the non-consecutive scanning mode using the setContinuouslyScan method (optional).
        remoteView = new RemoteView.Builder().setContext(this).build();
//        remoteView.setBackgroundResource(R.drawable.rounded_frame);
        // Load the customized view to the frameLayout of the activity.
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);

        // Subscribe to the recognition result callback event.
        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                // Obtain the scanning result object HmsScan.
                pauseScan();
                disableFlash();
                scanResult[0] = result[0].getOriginalValue();
                String strScanResult = scanResult[0];
                TVScanResult.setText(strScanResult);
                parseResult(result[0]);
                remoteView.pauseContinuouslyScan();
                remoteView.setAlpha((float) 0.3);
                db.addScanHistory(new History(strScanResult));
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

        // HISTORY
//        btnHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GoToHistoryPage();
//            }
//        });

        // OPEN IN BROWSER
        btnOpenInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = scanResult[0];
                if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
//                Log.d(TAG, "URL = " + url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        // COPY LINK
        btnCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = scanResult[0];
                if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
//                Log.d(TAG, "URL = " + url);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
                ClipData clip = ClipData.newPlainText("copy", url);
                clipboard.setPrimaryClip(clip);
            }
        });

        btnScanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(MainActivity.this, HistoryPage.class);
                startActivity(history);
            }
        });
    }

    private void disableFlash(){
        flash_button.setImageResource(R.drawable.flash_disable);
    }

    private void pauseScan(){
        remoteView.onStop();
        txtScanAgain = findViewById(R.id.txtScanAgain);
        imgReset = findViewById(R.id.imgReset);

        txtScanAgain.setVisibility(View.VISIBLE);
        imgReset.setVisibility(View.VISIBLE);
        scanPaused = true;

        if(scanPaused) {
            imgReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetScan();
                }
            });
        }
    }

    private void resetScan(){
        remoteView.setAlpha(1);
        TVScanResult.setText("");
        resetButton();
        remoteView.onStart();
        txtScanAgain.setVisibility(View.INVISIBLE);
        imgReset.setVisibility(View.INVISIBLE);
        scanPaused = false;
        enableFlash();
        remoteView.resumeContinuouslyScan();
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
        resetButton();

        if (result.getScanTypeForm() == HmsScan.URL_FORM){
            btnOpenInBrowser.setVisibility(View.VISIBLE);
            btnCopyLink.setVisibility(View.VISIBLE);
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
            btnCopyLink.setVisibility(View.VISIBLE);
        }
    }

    private void resetButton(){
        btnOpenInBrowser.setVisibility(View.INVISIBLE);
        btnCopyLink.setVisibility(View.INVISIBLE);
    }

    private void saveHistory(HmsScan text){
//        History history = new History(text);
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
        // Listen to the onStart method of the activity.
        remoteView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Listen to the onResume method of the activity.
        remoteView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Listen to the onPause method of the activity.
        remoteView.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Listen to the onStop method of the activity.
        remoteView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Listen to the onDestroy method of the activity.
        remoteView.onDestroy();
    }
}