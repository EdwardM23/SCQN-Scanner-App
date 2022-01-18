package edward.com.scannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edward.com.scannerapp.model.History;

public class ScanTextActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int REQUEST_CODE_FILE = 300;
    public static final String SCAN_RESULT = "SCAN_RESULT";
    public static final String SCAN_RESULT_ID = "SCAN_RESULT_ID";

    private Button btnPickImage;
    private DatabaseHandler db;
    private ImageView imgScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_text);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnPickImage = findViewById(R.id.btnPickImage);
        imgScan = findViewById(R.id.img_textScan);
        db = new DatabaseHandler(this);
        setButtonListener();
    }

    private void setButtonListener() {
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextRecognition();
            }
        });
    }

    private void setTextRecognition() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ScanTextActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || requestCode != REQUEST_CODE_FILE) {
            return;
        }

        try {
            scanText(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
        } catch (Exception e) {
        }
    }

    private void scanText(Bitmap bitmap) {
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setLanguage("en")
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .create();
        MLTextAnalyzer analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer();
        MLFrame frame = MLFrame.fromBitmap(bitmap);

        imgScan.setImageBitmap(bitmap);

        Task<MLText> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
            @Override
            public void onSuccess(MLText text) {
                if (analyzer != null) {
                    try {
                        analyzer.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String res = text.getStringValue();

                long scanId = addScanHistory(res);
                Intent scanResultAct = new Intent(getApplicationContext(), ScanResultActivity.class);
                scanResultAct.putExtra(SCAN_RESULT, res);
                scanResultAct.putExtra(SCAN_RESULT_ID, scanId);
                startActivity(scanResultAct);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Processing logic for recognition failure.
            }
        });
    }


    private long addScanHistory(String scanResult) {
        return db.addScanHistory(new History(scanResult));
    }
}