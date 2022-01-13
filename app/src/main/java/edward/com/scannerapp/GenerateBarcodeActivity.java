package edward.com.scannerapp;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class GenerateBarcodeActivity extends AppCompatActivity {

    private static final String TAG = "GenerateBarcodeActivity" ;
    private static final int[] BARCODE_TYPES = {
            HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE,
            HmsScan.PDF417_SCAN_TYPE, HmsScan.AZTEC_SCAN_TYPE, HmsScan.CODE128_SCAN_TYPE};

    private static  final int[] COLOR = {Color.BLACK, Color.BLUE, Color.GRAY, Color.GREEN, Color.RED, Color.YELLOW};
    private static  final int[] BACKGROUND = {Color.WHITE, Color.YELLOW, Color.RED, Color.GREEN, Color.GRAY, Color.BLUE, Color.BLACK};

    private static final int barcodeWidth = 600;
    private static final int barcodeHeight = 600;

    private final int STORAGE_WRITE_REQUEST_CODE = 1;

    // Controls
    private EditText edtContent;
    private Spinner ddlType;
    private Spinner ddlForeColor;
    private Spinner ddlBgColor;
    private Button btnGenerateBarcode;
    private ImageButton btnSaveBarcode, btnBack;
    private ImageView imgBarcodeResult;
    private BannerView bannerAds;

    // Variables
    private String content;
    private int width, height;
    private Bitmap resultImage;

    // Default Variables
    private int type = 0;
    private int margin = 1;
    private int color = Color.BLACK;
    private int background = Color.WHITE;
    private int saveQuality = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUIElements();
    }

    protected void setupUIElements() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            if (window != null) {
//                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            }
//        }

        setContentView(R.layout.activity_generate);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edtContent = findViewById(R.id.edtContent);
        ddlType = findViewById(R.id.ddlType);
        ddlForeColor = findViewById(R.id.ddlForeColor);
        ddlBgColor = findViewById(R.id.ddlBgColor);
        imgBarcodeResult = findViewById(R.id.imgBarcodeResult);
        btnGenerateBarcode = findViewById(R.id.btnGenerateBarcode);
        btnSaveBarcode = findViewById(R.id.btnSaveBarcode);
        btnBack = findViewById(R.id.btnBack);
        bannerAds = findViewById(R.id.huawei_banner);

        //Set Huawei Ads Banner
        HwAds.init(this);
        bannerAds.setAdId(("testw6vs28auh3"));
        bannerAds.setBannerRefresh(60);
        AdParam adParam = new AdParam.Builder().build();
        bannerAds.loadAd(adParam);

        setupListeners();
    }

    protected void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(GenerateBarcodeActivity.this, MainActivity.class);
                startActivity(home);
            }
        });

        btnGenerateBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateBarcode(view);
            }
        });

        btnSaveBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGeneratedBarcode(view);
            }
        });

        // Tipe utk barcodenya.
        ddlType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = BARCODE_TYPES[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = BARCODE_TYPES[0];
            }
        });

        ddlForeColor.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = COLOR[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                color = COLOR[0];
            }
        });

        ddlBgColor.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                background = BACKGROUND[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                background = BACKGROUND[0];
            }
        });
    }

    public void generateBarcode(View v) {
        content = edtContent.getText().toString();
        width = barcodeWidth;
        height = barcodeHeight;

        //Set the barcode content.
        if (content.length() <= 0) {
            Toast.makeText(this, "Please fill the content first!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator()
                    .setBitmapMargin(margin)
                    .setBitmapColor(color)
                    .setBitmapBackgroundColor(background).create();
            resultImage = ScanUtil.buildBitmap(content, type, width, height, options);
            imgBarcodeResult.setImageBitmap(resultImage);
        } catch (WriterException e) {
            Toast.makeText(this, "There's an error while generating barcode!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveGeneratedBarcode(View v) {
        if (resultImage == null) {
            Toast.makeText(GenerateBarcodeActivity.this, "Please generate the barcode first!", Toast.LENGTH_LONG).show();
            return;
        }

        // Request storage write permission
        if (!(ContextCompat.checkSelfPermission(GenerateBarcodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            requestStoragePermission();
            return;
        }

        try {
            // Configure store path
            String fileName = System.currentTimeMillis() + ".jpg";
            String storePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File appDir = new File(storePath);
            if (!appDir.exists()) {
                appDir.mkdir();
            }

            // Save the file
            File file = new File(appDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            boolean isSuccess = resultImage.compress(Bitmap.CompressFormat.JPEG, saveQuality, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Uri uri = Uri.fromFile(file);
            GenerateBarcodeActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                Toast.makeText(GenerateBarcodeActivity.this, "Barcode has been saved locally", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GenerateBarcodeActivity.this, "Barcode save failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.w(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(GenerateBarcodeActivity.this, "There's an error while saving file!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is required to save the barcode.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(GenerateBarcodeActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_WRITE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted, please try saving again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}